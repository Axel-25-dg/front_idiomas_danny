package com.ute.guamanidiomas.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.model.*
import com.ute.guamanidiomas.domain.model.teacher.Classroom
import com.ute.guamanidiomas.domain.model.teacher.TeacherResource
import com.ute.guamanidiomas.domain.repository.CertificateRepository
import com.ute.guamanidiomas.domain.repository.GamificationRepository
import com.ute.guamanidiomas.domain.repository.StudentClassroomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─── UI States ────────────────────────────────────────────────────────────────

data class MyClassesUiState(
    val isLoading: Boolean = false,
    val classrooms: List<Classroom> = emptyList(),
    val error: String? = null
)

data class ClassDetailUiState(
    val isLoading: Boolean = false,
    val classroom: Classroom? = null,
    val resources: List<TeacherResource> = emptyList(),
    val error: String? = null
)

data class MyCertificatesUiState(
    val isLoading: Boolean = false,
    val certificates: List<Certificate> = emptyList(),
    val error: String? = null,
    val verifyResult: Certificate? = null
)

data class AchievementsUiState(
    val isLoading: Boolean = false,
    val allAchievements: List<Achievement> = emptyList(),
    val myAchievements: List<UserAchievement> = emptyList(),
    val error: String? = null
)

data class LeaderboardUiState(
    val isLoading: Boolean = false,
    val students: List<StudentStats> = emptyList(),
    val error: String? = null
)

// ─── ViewModel ────────────────────────────────────────────────────────────────

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val classroomRepository: StudentClassroomRepository,
    private val certificateRepository: CertificateRepository,
    private val gamificationRepository: GamificationRepository
) : ViewModel() {

    // ── My Classes ────────────────────────────────────────────────────────────
    private val _classesState = MutableStateFlow(MyClassesUiState())
    val classesState: StateFlow<MyClassesUiState> = _classesState.asStateFlow()

    // ── Class Detail ──────────────────────────────────────────────────────────
    private val _classDetailState = MutableStateFlow(ClassDetailUiState())
    val classDetailState: StateFlow<ClassDetailUiState> = _classDetailState.asStateFlow()

    // ── Certificates ──────────────────────────────────────────────────────────
    private val _certificatesState = MutableStateFlow(MyCertificatesUiState())
    val certificatesState: StateFlow<MyCertificatesUiState> = _certificatesState.asStateFlow()

    // ── Achievements ──────────────────────────────────────────────────────────
    private val _achievementsState = MutableStateFlow(AchievementsUiState())
    val achievementsState: StateFlow<AchievementsUiState> = _achievementsState.asStateFlow()

    // ── Leaderboard ───────────────────────────────────────────────────────────
    private val _leaderboardState = MutableStateFlow(LeaderboardUiState())
    val leaderboardState: StateFlow<LeaderboardUiState> = _leaderboardState.asStateFlow()

    // ─── MY CLASSES ──────────────────────────────────────────────────────────

    fun loadMyClasses() {
        viewModelScope.launch {
            _classesState.value = _classesState.value.copy(isLoading = true, error = null)
            classroomRepository.getMyClassrooms()
                .onSuccess { classrooms ->
                    _classesState.value = _classesState.value.copy(
                        isLoading  = false,
                        classrooms = classrooms
                    )
                }
                .onFailure { e ->
                    _classesState.value = _classesState.value.copy(
                        isLoading = false,
                        error     = e.message ?: "Error al cargar tus clases"
                    )
                }
        }
    }

    // ─── CLASS DETAIL ────────────────────────────────────────────────────────

    fun loadClassDetail(classroomId: Int) {
        viewModelScope.launch {
            _classDetailState.value = _classDetailState.value.copy(isLoading = true, error = null)

            // El backend tiene un bug: GET /api/classrooms/{id}/ devuelve 404 para estudiantes
            // Workaround: buscar la clase en /classrooms/mine/ y cargar recursos aparte
            val classroomsResult = classroomRepository.getMyClassrooms()
            val classroom = classroomsResult.getOrNull()?.find { it.id == classroomId }

            if (classroom != null) {
                // Cargar recursos asociados
                val resources = classroomRepository.getClassroomResources(classroomId)
                    .getOrElse { emptyList() }

                _classDetailState.value = _classDetailState.value.copy(
                    isLoading = false,
                    classroom = classroom,
                    resources = resources
                )
            } else {
                // Fallback: intentar el endpoint directo (por si se corrige el backend)
                val directResult = classroomRepository.getClassroomById(classroomId)
                directResult
                    .onSuccess { cls ->
                        val resources = classroomRepository.getClassroomResources(classroomId)
                            .getOrElse { emptyList() }
                        _classDetailState.value = _classDetailState.value.copy(
                            isLoading = false,
                            classroom = cls,
                            resources = resources
                        )
                    }
                    .onFailure { e ->
                        _classDetailState.value = _classDetailState.value.copy(
                            isLoading = false,
                            error     = when {
                                e.message?.contains("401") == true -> "La sesion ha expirado. Inicia sesion nuevamente."
                                e.message?.contains("404") == true -> "No se encontro la clase. Es posible que el acceso este restringido."
                                e.message?.contains("500") == true -> "Error interno del servidor."
                                else -> e.message ?: "Error al cargar la clase"
                            }
                        )
                    }
            }
        }
    }
    fun leaveClassroom(classroomId: Int) {
        viewModelScope.launch {
            classroomRepository.leaveClassroom(classroomId)
                .onSuccess { loadMyClasses() }
                .onFailure { e ->
                    _classesState.value = _classesState.value.copy(
                        error = e.message ?: "Error al salir de la clase"
                    )
                }
        }
    }

    // ─── CERTIFICATES ────────────────────────────────────────────────────────

    fun loadMyCertificates() {
        viewModelScope.launch {
            _certificatesState.value = _certificatesState.value.copy(isLoading = true, error = null)
            certificateRepository.getMyCertificates()
                .onSuccess { certs ->
                    _certificatesState.value = _certificatesState.value.copy(
                        isLoading    = false,
                        certificates = certs
                    )
                }
                .onFailure { e ->
                    _certificatesState.value = _certificatesState.value.copy(
                        isLoading = false,
                        error     = e.message ?: "Error al cargar certificados"
                    )
                }
        }
    }

    fun verifyCertificate(code: String) {
        viewModelScope.launch {
            certificateRepository.verifyCertificate(code)
                .onSuccess { cert ->
                    _certificatesState.value = _certificatesState.value.copy(verifyResult = cert)
                }
                .onFailure { e ->
                    _certificatesState.value = _certificatesState.value.copy(
                        error = e.message ?: "Código no válido"
                    )
                }
        }
    }

    fun clearVerifyResult() {
        _certificatesState.value = _certificatesState.value.copy(verifyResult = null, error = null)
    }

    // ─── ACHIEVEMENTS ────────────────────────────────────────────────────────

    fun loadAchievements() {
        viewModelScope.launch {
            _achievementsState.value = _achievementsState.value.copy(isLoading = true, error = null)

            val allDeferred = async { gamificationRepository.getAllAchievements() }
            val myDeferred  = async { gamificationRepository.getMyAchievements() }

            val all = allDeferred.await().getOrElse { emptyList() }
            val my  = myDeferred.await().getOrElse { emptyList() }

            _achievementsState.value = _achievementsState.value.copy(
                isLoading       = false,
                allAchievements = all,
                myAchievements  = my
            )
        }
    }

    // ─── LEADERBOARD ─────────────────────────────────────────────────────────

    fun loadLeaderboard() {
        viewModelScope.launch {
            _leaderboardState.value = _leaderboardState.value.copy(isLoading = true, error = null)
            gamificationRepository.getMyStats()
                .onSuccess { stats ->
                    if (stats.isEmpty()) {
                        _leaderboardState.value = _leaderboardState.value.copy(
                            isLoading = false,
                            students  = emptyList()
                        )
                    } else {
                        val sorted = stats.sortedByDescending { it.totalXp }
                        _leaderboardState.value = _leaderboardState.value.copy(
                            isLoading = false,
                            students  = sorted
                        )
                    }
                }
                .onFailure { e ->
                    // Si el endpoint no existe o falla, mostrar vacio sin error critico
                    _leaderboardState.value = _leaderboardState.value.copy(
                        isLoading = false,
                        students  = emptyList()
                    )
                }
        }
    }
}
