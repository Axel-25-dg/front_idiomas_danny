package com.ute.guamanidiomas.ui.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.data.local.TokenDataStore
import com.ute.guamanidiomas.domain.model.Course
import com.ute.guamanidiomas.domain.model.teacher.*
import com.ute.guamanidiomas.domain.repository.CourseRepository
import com.ute.guamanidiomas.domain.repository.TeacherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TeacherHomeUiState(
    val isLoading: Boolean = false,
    val stats: TeacherStats = TeacherStats(0, 0, 0, 0, 0.0),
    val recentClassrooms: List<Classroom> = emptyList(),
    val teacherName: String = "",
    val teacherEmail: String = "",
    val error: String? = null
)

data class TeacherClassroomsUiState(
    val isLoading: Boolean = false,
    val classrooms: List<Classroom> = emptyList(),
    val courses: List<Course> = emptyList(),
    val showCreateDialog: Boolean = false,
    val editingClassroom: Classroom? = null,
    val error: String? = null,
    val successMessage: String? = null
)

data class TeacherStudentsUiState(
    val isLoading: Boolean = false,
    val enrollments: List<Enrollment> = emptyList(),
    val selectedClassroomId: Int? = null,
    val classrooms: List<Classroom> = emptyList(),
    val error: String? = null
)

data class TeacherExamsUiState(
    val isLoading: Boolean = false,
    val exams: List<Exam> = emptyList(),
    val examResults: List<ExamResult> = emptyList(),
    val selectedExamId: Int? = null,
    val showCreateDialog: Boolean = false,
    val editingExam: Exam? = null,
    val classrooms: List<Classroom> = emptyList(),
    val courses: List<com.ute.guamanidiomas.domain.model.Course> = emptyList(),
    val modules: List<com.ute.guamanidiomas.domain.model.Module> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

data class TeacherResourcesUiState(
    val isLoading: Boolean = false,
    val resources: List<TeacherResource> = emptyList(),
    val showCreateDialog: Boolean = false,
    val classrooms: List<Classroom> = emptyList(),
    val courses: List<Course> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class TeacherMainViewModel @Inject constructor(
    private val teacherRepository: TeacherRepository,
    private val courseRepository: CourseRepository,
    private val lessonRepository: com.ute.guamanidiomas.domain.repository.LessonRepository,
    private val exerciseRepository: com.ute.guamanidiomas.domain.repository.ExerciseRepository,
    private val moduleRepository: com.ute.guamanidiomas.domain.repository.ModuleRepository,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _homeState = MutableStateFlow(TeacherHomeUiState())
    val homeState: StateFlow<TeacherHomeUiState> = _homeState.asStateFlow()

    private val _classroomsState = MutableStateFlow(TeacherClassroomsUiState())
    val classroomsState: StateFlow<TeacherClassroomsUiState> = _classroomsState.asStateFlow()

    private val _studentsState = MutableStateFlow(TeacherStudentsUiState())
    val studentsState: StateFlow<TeacherStudentsUiState> = _studentsState.asStateFlow()

    private val _examsState = MutableStateFlow(TeacherExamsUiState())
    val examsState: StateFlow<TeacherExamsUiState> = _examsState.asStateFlow()

    private val _resourcesState = MutableStateFlow(TeacherResourcesUiState())
    val resourcesState: StateFlow<TeacherResourcesUiState> = _resourcesState.asStateFlow()

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            _homeState.value = _homeState.value.copy(isLoading = true, error = null)
            val snapshot = tokenDataStore.userSnapshot.first()

            val statsDeferred      = async { teacherRepository.getTeacherStats() }
            val classroomsDeferred = async { teacherRepository.getClassrooms() }

            val statsResult      = statsDeferred.await()
            val classroomsResult = classroomsDeferred.await()

            val stats      = statsResult.getOrElse { TeacherStats(0, 0, 0, 0, 0.0) }
            val classrooms = classroomsResult.getOrElse { emptyList() }

            _homeState.value = _homeState.value.copy(
                isLoading        = false,
                stats            = stats,
                recentClassrooms = classrooms.take(3),
                teacherName      = snapshot?.username ?: "",
                teacherEmail     = snapshot?.email ?: "",
                error            = statsResult.exceptionOrNull()?.message
            )
        }
    }

    fun loadClassrooms() {
        viewModelScope.launch {
            _classroomsState.value = _classroomsState.value.copy(isLoading = true, error = null)

            val classroomsDeferred = async { teacherRepository.getClassrooms() }
            val coursesDeferred    = async {
                courseRepository.getCourses(
                    com.ute.guamanidiomas.domain.repository.CourseFilters(pageSize = 100)
                )
            }

            val classroomsResult = classroomsDeferred.await()
            val courses = coursesDeferred.await().getOrElse { Pair(emptyList(), 0) }.first

            classroomsResult
                .onSuccess { classrooms ->
                    _classroomsState.value = _classroomsState.value.copy(
                        isLoading  = false,
                        classrooms = classrooms,
                        courses    = courses
                    )
                }
                .onFailure { e ->
                    _classroomsState.value = _classroomsState.value.copy(
                        isLoading  = false,
                        classrooms = emptyList(),
                        courses    = courses,
                        error      = e.message ?: "Error al cargar clases"
                    )
                }
        }
    }

    fun showCreateClassroomDialog() {
        _classroomsState.value = _classroomsState.value.copy(
            showCreateDialog = true,
            editingClassroom = null
        )
    }

    fun showEditClassroomDialog(classroom: Classroom) {
        _classroomsState.value = _classroomsState.value.copy(
            showCreateDialog = true,
            editingClassroom = classroom
        )
    }

    fun dismissClassroomDialog() {
        _classroomsState.value = _classroomsState.value.copy(
            showCreateDialog = false,
            editingClassroom = null
        )
    }

    fun createClassroom(courseId: Int, name: String, description: String) {
        viewModelScope.launch {
            _classroomsState.value = _classroomsState.value.copy(isLoading = true, error = null)
            teacherRepository.createClassroom(
                ClassroomPayload(courseId = courseId, name = name, description = description)
            ).onSuccess { createdClassroom ->
                val currentList = _classroomsState.value.classrooms
                _classroomsState.value = _classroomsState.value.copy(
                    isLoading        = false,
                    showCreateDialog = false,
                    classrooms       = currentList + createdClassroom,
                    successMessage   = "Clase «${createdClassroom.name}» creada — Código: ${createdClassroom.accessCode}"
                )
                loadClassrooms()
                loadHome()
            }.onFailure { e ->
                _classroomsState.value = _classroomsState.value.copy(
                    isLoading = false,
                    error     = e.message ?: "Error al crear la clase"
                )
            }
        }
    }

    fun updateClassroom(id: Int, courseId: Int, name: String, description: String) {
        viewModelScope.launch {
            _classroomsState.value = _classroomsState.value.copy(isLoading = true, error = null)
            teacherRepository.updateClassroom(
                id,
                ClassroomPayload(courseId = courseId, name = name, description = description)
            ).onSuccess { updatedClassroom ->
                val currentList = _classroomsState.value.classrooms.map {
                    if (it.id == id) updatedClassroom else it
                }
                _classroomsState.value = _classroomsState.value.copy(
                    isLoading        = false,
                    showCreateDialog = false,
                    editingClassroom = null,
                    classrooms       = currentList,
                    successMessage   = "Clase actualizada correctamente"
                )
                loadClassrooms()
            }.onFailure { e ->
                _classroomsState.value = _classroomsState.value.copy(
                    isLoading = false,
                    error     = e.message ?: "Error al actualizar la clase"
                )
            }
        }
    }

    fun deleteClassroom(id: Int) {
        viewModelScope.launch {
            teacherRepository.deleteClassroom(id).onSuccess {
                loadClassrooms()
                loadHome()
                _classroomsState.value = _classroomsState.value.copy(
                    successMessage = "Clase eliminada"
                )
            }.onFailure { e ->
                _classroomsState.value = _classroomsState.value.copy(
                    error = e.message ?: "Error al eliminar la clase"
                )
            }
        }
    }

    fun clearClassroomsMessages() {
        _classroomsState.value = _classroomsState.value.copy(
            error          = null,
            successMessage = null
        )
    }

    fun loadStudents(classroomId: Int? = null) {
        viewModelScope.launch {
            _studentsState.value = _studentsState.value.copy(isLoading = true, error = null)

            val classroomsResult = teacherRepository.getClassrooms()
            val classrooms       = classroomsResult.getOrElse { emptyList() }

            val targetId = classroomId ?: _studentsState.value.selectedClassroomId
                ?: classrooms.firstOrNull()?.id

            val enrollments = if (targetId != null) {
                teacherRepository.getEnrollments(targetId).getOrElse { emptyList() }
            } else emptyList()

            _studentsState.value = _studentsState.value.copy(
                isLoading           = false,
                enrollments         = enrollments,
                classrooms          = classrooms,
                selectedClassroomId = targetId
            )
        }
    }

    fun selectClassroomForStudents(classroomId: Int) {
        _studentsState.value = _studentsState.value.copy(selectedClassroomId = classroomId)
        loadStudents(classroomId)
    }

    fun removeStudent(classroomId: Int, studentId: Int) {
        viewModelScope.launch {
            teacherRepository.removeStudent(classroomId, studentId)
                .onSuccess {
                    loadStudents(classroomId)
                }
                .onFailure { e ->
                    _studentsState.value = _studentsState.value.copy(
                        error = e.message ?: "Error al eliminar estudiante"
                    )
                }
        }
    }

    fun loadExams(classroomId: Int? = null) {
        viewModelScope.launch {
            _examsState.value = _examsState.value.copy(isLoading = true, error = null)

            val lessonsResult = lessonRepository.getAllLessons()
            val coursesResult = courseRepository.getCourses(
                com.ute.guamanidiomas.domain.repository.CourseFilters(pageSize = 100)
            )

            val courses = coursesResult.getOrElse { Pair(emptyList(), 0) }.first

            lessonsResult
                .onSuccess { allLessons ->
                    val examLessons = allLessons.filter { it.isExam }
                    val exams = examLessons.map { lesson ->
                        Exam(
                            id               = lesson.id,
                            classroomId      = 0,
                            classroomName    = lesson.moduleTitle,
                            title            = lesson.title,
                            description      = lesson.content,
                            timeLimitMinutes = 60,
                            passingScore     = lesson.xpReward,
                            autoGrade        = true,
                            startDate        = null,
                            endDate          = null,
                            isActive         = lesson.isActive,
                            createdAt        = "",
                            submissionCount  = 0
                        )
                    }
                    _examsState.value = _examsState.value.copy(
                        isLoading = false,
                        exams     = exams,
                        courses   = courses
                    )
                }
                .onFailure { e ->
                    _examsState.value = _examsState.value.copy(
                        isLoading = false,
                        exams     = emptyList(),
                        courses   = courses,
                        error     = e.message ?: "Error al cargar lecciones"
                    )
                }
        }
    }

    private var lastLoadedCourseId: Int? = null

    fun loadModulesForCourse(courseId: Int) {
        if (lastLoadedCourseId == courseId) return
        viewModelScope.launch {
            lastLoadedCourseId = courseId
            _examsState.value = _examsState.value.copy(modules = emptyList())
            moduleRepository.getModulesByCourse(courseId)
                .onSuccess { modules ->
                    _examsState.value = _examsState.value.copy(modules = modules)
                }
                .onFailure {
                    lastLoadedCourseId = null
                    _examsState.value = _examsState.value.copy(modules = emptyList())
                }
        }
    }

    fun loadExamResults(examId: Int) {
        viewModelScope.launch {
            _examsState.value = _examsState.value.copy(selectedExamId = examId)
            teacherRepository.getExamResults(examId).onSuccess { results ->
                _examsState.value = _examsState.value.copy(examResults = results)
            }
        }
    }

    fun showCreateExamDialog() {
        _examsState.value = _examsState.value.copy(showCreateDialog = true, editingExam = null)
    }

    fun showEditExamDialog(exam: Exam) {
        _examsState.value = _examsState.value.copy(showCreateDialog = true, editingExam = exam)
    }

    fun dismissExamDialog() {
        _examsState.value = _examsState.value.copy(showCreateDialog = false, editingExam = null)
    }

    fun createExam(
        classroomId: Int, title: String, description: String,
        timeLimitMinutes: Int, passingScore: Int, autoGrade: Boolean,
        startDate: String?, endDate: String?
    ) {
        viewModelScope.launch {
            _examsState.value = _examsState.value.copy(isLoading = true)
            val payload = com.ute.guamanidiomas.domain.model.LessonPayload(
                moduleId    = classroomId,
                title       = title,
                content     = description,
                contentType = "interactive",
                order       = 99,
                xpReward    = passingScore,
                isActive    = true
            )
            lessonRepository.createLesson(payload)
                .onSuccess {
                    _examsState.value = _examsState.value.copy(
                        isLoading      = false,
                        showCreateDialog = false,
                        successMessage = "Leccion «$title» creada"
                    )
                    loadExams()
                }
                .onFailure { e ->
                    _examsState.value = _examsState.value.copy(
                        isLoading = false,
                        error     = e.message ?: "Error al crear la leccion"
                    )
                }
        }
    }

    fun deleteExam(id: Int) {
        viewModelScope.launch {
            teacherRepository.deleteExam(id).onSuccess {
                loadExams()
                loadHome()
                _examsState.value = _examsState.value.copy(successMessage = "Examen eliminado")
            }.onFailure { e ->
                _examsState.value = _examsState.value.copy(error = e.message ?: "Error al eliminar")
            }
        }
    }

    fun loadExamQuestions(lessonId: Int) {
        viewModelScope.launch {
            exerciseRepository.getExercisesByLesson(lessonId)
                .onSuccess { exercises ->
                    val results = exercises.map { ex ->
                        ExamResult(
                            id           = ex.id,
                            examId       = lessonId,
                            examTitle    = ex.question ?: "Pregunta sin título",
                            studentId    = 0,
                            studentEmail = ex.type.value,
                            studentName  = ex.correctAnswer ?: "",
                            score        = ex.xpReward,
                            passed       = ex.isActive,
                            submittedAt  = ""
                        )
                    }
                    _examsState.value = _examsState.value.copy(
                        examResults    = results,
                        selectedExamId = lessonId
                    )
                }
        }
    }

    fun addQuestionToExam(lessonId: Int, questionText: String, exerciseType: String, correctAnswer: String) {
        viewModelScope.launch {
            _examsState.value = _examsState.value.copy(isLoading = true)
            exerciseRepository.createExercise(
                com.ute.guamanidiomas.domain.model.ExercisePayload(
                    lessonId      = lessonId,
                    questionText  = questionText,
                    exerciseType  = exerciseType,
                    correctAnswer = correctAnswer
                )
            ).onSuccess {
                _examsState.value = _examsState.value.copy(
                    isLoading      = false,
                    successMessage = "Pregunta agregada"
                )
                loadExamQuestions(lessonId)
            }.onFailure { e ->
                _examsState.value = _examsState.value.copy(
                    isLoading = false,
                    error     = e.message ?: "Error al agregar pregunta"
                )
            }
        }
    }

    fun clearExamsMessages() {
        _examsState.value = _examsState.value.copy(error = null, successMessage = null)
    }

    fun loadResources(classroomId: Int? = null) {
        viewModelScope.launch {
            _resourcesState.value = _resourcesState.value.copy(isLoading = true, error = null)

            val resourcesDeferred  = async { teacherRepository.getResources(classroomId) }
            val classroomsDeferred = async { teacherRepository.getClassrooms() }
            val coursesDeferred    = async {
                courseRepository.getCourses(
                    com.ute.guamanidiomas.domain.repository.CourseFilters(pageSize = 100)
                )
            }

            val resourcesResult = resourcesDeferred.await()
            val classrooms      = classroomsDeferred.await().getOrElse { emptyList() }
            val courses         = coursesDeferred.await().getOrElse { Pair(emptyList(), 0) }.first

            resourcesResult
                .onSuccess { resources ->
                    _resourcesState.value = _resourcesState.value.copy(
                        isLoading  = false,
                        resources  = resources,
                        classrooms = classrooms,
                        courses    = courses
                    )
                }
                .onFailure { e ->
                    _resourcesState.value = _resourcesState.value.copy(
                        isLoading  = false,
                        resources  = emptyList(),
                        classrooms = classrooms,
                        courses    = courses,
                        error      = e.message ?: "Error al cargar recursos"
                    )
                }
        }
    }

    fun showCreateResourceDialog() {
        _resourcesState.value = _resourcesState.value.copy(showCreateDialog = true)
    }

    fun dismissResourceDialog() {
        _resourcesState.value = _resourcesState.value.copy(showCreateDialog = false)
    }

    fun createResource(
        courseId: Int?, title: String, description: String,
        resourceType: String, url: String
    ) {
        viewModelScope.launch {
            _resourcesState.value = _resourcesState.value.copy(isLoading = true)
            teacherRepository.createResource(
                TeacherResourcePayload(
                    title        = title,
                    description  = description,
                    resourceType = resourceType,
                    fileUrl      = url,
                    courseId     = courseId,
                    lessonId    = null,
                    isPublic     = true
                )
            ).onSuccess {
                loadResources()
                loadHome()
                _resourcesState.value = _resourcesState.value.copy(
                    showCreateDialog = false,
                    successMessage   = "Recurso «$title» agregado correctamente"
                )
            }.onFailure { e ->
                _resourcesState.value = _resourcesState.value.copy(
                    isLoading = false,
                    error     = e.message ?: "Error al agregar el recurso"
                )
            }
        }
    }

    fun deleteResource(id: Int) {
        viewModelScope.launch {
            teacherRepository.deleteResource(id).onSuccess {
                loadResources()
                loadHome()
                _resourcesState.value = _resourcesState.value.copy(successMessage = "Recurso eliminado")
            }.onFailure { e ->
                _resourcesState.value = _resourcesState.value.copy(error = e.message ?: "Error al eliminar")
            }
        }
    }

    fun clearResourcesMessages() {
        _resourcesState.value = _resourcesState.value.copy(error = null, successMessage = null)
    }
}