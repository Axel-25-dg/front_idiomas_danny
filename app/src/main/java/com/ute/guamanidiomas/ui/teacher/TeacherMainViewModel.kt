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

// ─── UI States ────────────────────────────────────────────────────────────────

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
    val error: String? = null,
    val successMessage: String? = null
)

data class TeacherResourcesUiState(
    val isLoading: Boolean = false,
    val resources: List<TeacherResource> = emptyList(),
    val showCreateDialog: Boolean = false,
    val classrooms: List<Classroom> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

// ─── ViewModel ────────────────────────────────────────────────────────────────

@HiltViewModel
class TeacherMainViewModel @Inject constructor(
    private val teacherRepository: TeacherRepository,
    private val courseRepository: CourseRepository,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    // ── Home ──────────────────────────────────────────────────────────────────
    private val _homeState = MutableStateFlow(TeacherHomeUiState())
    val homeState: StateFlow<TeacherHomeUiState> = _homeState.asStateFlow()

    // ── Classrooms ────────────────────────────────────────────────────────────
    private val _classroomsState = MutableStateFlow(TeacherClassroomsUiState())
    val classroomsState: StateFlow<TeacherClassroomsUiState> = _classroomsState.asStateFlow()

    // ── Students ──────────────────────────────────────────────────────────────
    private val _studentsState = MutableStateFlow(TeacherStudentsUiState())
    val studentsState: StateFlow<TeacherStudentsUiState> = _studentsState.asStateFlow()

    // ── Exams ─────────────────────────────────────────────────────────────────
    private val _examsState = MutableStateFlow(TeacherExamsUiState())
    val examsState: StateFlow<TeacherExamsUiState> = _examsState.asStateFlow()

    // ── Resources ─────────────────────────────────────────────────────────────
    private val _resourcesState = MutableStateFlow(TeacherResourcesUiState())
    val resourcesState: StateFlow<TeacherResourcesUiState> = _resourcesState.asStateFlow()

    init {
        loadHome()
    }

    // ─── HOME ─────────────────────────────────────────────────────────────────

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

    // ─── CLASSROOMS ──────────────────────────────────────────────────────────

    fun loadClassrooms() {
        viewModelScope.launch {
            _classroomsState.value = _classroomsState.value.copy(isLoading = true, error = null)

            val classroomsDeferred = async { teacherRepository.getClassrooms() }
            // Cargar TODOS los cursos disponibles (sin límite de página)
            val coursesDeferred    = async {
                courseRepository.getCourses(
                    com.ute.guamanidiomas.domain.repository.CourseFilters(pageSize = 100)
                )
            }

            val classrooms = classroomsDeferred.await().getOrElse { emptyList() }
            val courses    = coursesDeferred.await().getOrElse { Pair(emptyList(), 0) }.first

            _classroomsState.value = _classroomsState.value.copy(
                isLoading  = false,
                classrooms = classrooms,
                courses    = courses
            )
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
                // Agregar inmediatamente a la lista local para feedback instantáneo
                val currentList = _classroomsState.value.classrooms
                _classroomsState.value = _classroomsState.value.copy(
                    isLoading        = false,
                    showCreateDialog = false,
                    classrooms       = currentList + createdClassroom,
                    successMessage   = "Clase «${createdClassroom.name}» creada — Código: ${createdClassroom.accessCode}"
                )
                // Recargar desde el backend en background para sincronizar
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

    // ─── STUDENTS ────────────────────────────────────────────────────────────

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
                    // Recargar lista de estudiantes
                    loadStudents(classroomId)
                }
                .onFailure { e ->
                    _studentsState.value = _studentsState.value.copy(
                        error = e.message ?: "Error al eliminar estudiante"
                    )
                }
        }
    }

    // ─── EXAMS ───────────────────────────────────────────────────────────────

    fun loadExams(classroomId: Int? = null) {
        viewModelScope.launch {
            _examsState.value = _examsState.value.copy(isLoading = true, error = null)

            val examsDeferred      = async { teacherRepository.getExams(classroomId) }
            val classroomsDeferred = async { teacherRepository.getClassrooms() }

            val exams      = examsDeferred.await().getOrElse { emptyList() }
            val classrooms = classroomsDeferred.await().getOrElse { emptyList() }

            _examsState.value = _examsState.value.copy(
                isLoading  = false,
                exams      = exams,
                classrooms = classrooms
            )
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
            teacherRepository.createExam(
                ExamPayload(
                    classroomId      = classroomId,
                    title            = title,
                    description      = description,
                    timeLimitMinutes = timeLimitMinutes,
                    passingScore     = passingScore,
                    autoGrade        = autoGrade,
                    startDate        = startDate,
                    endDate          = endDate,
                    isActive         = true
                )
            ).onSuccess {
                loadExams()
                loadHome()
                _examsState.value = _examsState.value.copy(
                    showCreateDialog = false,
                    successMessage   = "Examen «$title» creado correctamente"
                )
            }.onFailure { e ->
                _examsState.value = _examsState.value.copy(
                    isLoading = false,
                    error     = e.message ?: "Error al crear el examen"
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

    fun clearExamsMessages() {
        _examsState.value = _examsState.value.copy(error = null, successMessage = null)
    }

    // ─── RESOURCES ───────────────────────────────────────────────────────────

    fun loadResources(classroomId: Int? = null) {
        viewModelScope.launch {
            _resourcesState.value = _resourcesState.value.copy(isLoading = true, error = null)

            val resourcesDeferred  = async { teacherRepository.getResources(classroomId) }
            val classroomsDeferred = async { teacherRepository.getClassrooms() }

            val resources  = resourcesDeferred.await().getOrElse { emptyList() }
            val classrooms = classroomsDeferred.await().getOrElse { emptyList() }

            _resourcesState.value = _resourcesState.value.copy(
                isLoading  = false,
                resources  = resources,
                classrooms = classrooms
            )
        }
    }

    fun showCreateResourceDialog() {
        _resourcesState.value = _resourcesState.value.copy(showCreateDialog = true)
    }

    fun dismissResourceDialog() {
        _resourcesState.value = _resourcesState.value.copy(showCreateDialog = false)
    }

    fun createResource(
        classroomId: Int, title: String, description: String,
        resourceType: String, url: String
    ) {
        viewModelScope.launch {
            _resourcesState.value = _resourcesState.value.copy(isLoading = true)
            teacherRepository.createResource(
                TeacherResourcePayload(
                    classroomId  = classroomId,
                    title        = title,
                    description  = description,
                    resourceType = resourceType,
                    url          = url,
                    isActive     = true
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
