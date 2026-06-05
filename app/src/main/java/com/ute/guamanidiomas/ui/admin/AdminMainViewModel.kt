package com.ute.guamanidiomas.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.data.local.TokenDataStore
import com.ute.guamanidiomas.data.remote.dto.AuditLogDto
import com.ute.guamanidiomas.data.remote.dto.OrderDto
import com.ute.guamanidiomas.data.remote.dto.RoleDto
import com.ute.guamanidiomas.domain.model.Course
import com.ute.guamanidiomas.domain.model.User
import com.ute.guamanidiomas.domain.repository.AdminConsoleRepository
import com.ute.guamanidiomas.domain.repository.AdminUsersRepository
import com.ute.guamanidiomas.domain.repository.CourseRepository
import com.ute.guamanidiomas.domain.repository.OrderRepository
import com.ute.guamanidiomas.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─── UI States ────────────────────────────────────────────────────────────────

data class AdminOverviewState(
    val isLoading: Boolean = false,
    val adminName: String = "",
    val adminEmail: String = "",
    val totalUsers: Int = 0,
    val totalTeachers: Int = 0,
    val totalStudents: Int = 0,
    val totalCourses: Int = 0,
    val totalOrders: Int = 0,
    val totalRevenue: Double = 0.0,
    val pendingOrders: Int = 0,
    val completedOrders: Int = 0,
    val totalSubscriptions: Int = 0,
    val recentOrders: List<OrderDto> = emptyList(),
    val recentAuditLogs: List<AuditLogDto> = emptyList(),
    val error: String? = null
)

data class AdminUsersState(
    val isLoading: Boolean = false,
    val allUsers: List<User> = emptyList(),
    val teachers: List<User> = emptyList(),
    val students: List<User> = emptyList(),
    val searchQuery: String = "",
    val filterRole: String = "",
    val error: String? = null,
    val successMessage: String? = null,
    val isSubmitting: Boolean = false
)

data class AdminCoursesState(
    val isLoading: Boolean = false,
    val courses: List<Course> = emptyList(),
    val total: Int = 0,
    val error: String? = null,
    val isDeleting: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

data class AdminOrdersState(
    val isLoading: Boolean = false,
    val orders: List<OrderDto> = emptyList(),
    val totalRevenue: Double = 0.0,
    val filterStatus: String = "",
    val error: String? = null,
    val successMessage: String? = null
)

data class AdminRolesState(
    val isLoading: Boolean = false,
    val roles: List<RoleDto> = emptyList(),
    val auditLogs: List<AuditLogDto> = emptyList(),
    val error: String? = null,
    val isSaving: Boolean = false
)

data class AdminSubscriptionsState(
    val isLoading: Boolean = false,
    val totalRevenue: Double = 0.0,
    val totalSubscriptions: Int = 0,
    val activeSubscriptions: Int = 0,
    val error: String? = null
)

// ─── ViewModel ────────────────────────────────────────────────────────────────

@HiltViewModel
class AdminMainViewModel @Inject constructor(
    private val usersRepository: AdminUsersRepository,
    private val courseRepository: CourseRepository,
    private val orderRepository: OrderRepository,
    private val adminConsoleRepository: AdminConsoleRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _overviewState = MutableStateFlow(AdminOverviewState())
    val overviewState: StateFlow<AdminOverviewState> = _overviewState.asStateFlow()

    private val _usersState = MutableStateFlow(AdminUsersState())
    val usersState: StateFlow<AdminUsersState> = _usersState.asStateFlow()

    private val _coursesState = MutableStateFlow(AdminCoursesState())
    val coursesState: StateFlow<AdminCoursesState> = _coursesState.asStateFlow()

    private val _ordersState = MutableStateFlow(AdminOrdersState())
    val ordersState: StateFlow<AdminOrdersState> = _ordersState.asStateFlow()

    private val _rolesState = MutableStateFlow(AdminRolesState())
    val rolesState: StateFlow<AdminRolesState> = _rolesState.asStateFlow()

    private val _subscriptionsState = MutableStateFlow(AdminSubscriptionsState())
    val subscriptionsState: StateFlow<AdminSubscriptionsState> = _subscriptionsState.asStateFlow()

    init {
        loadOverview()
    }

    // ─── OVERVIEW ─────────────────────────────────────────────────────────────

    fun loadOverview() {
        viewModelScope.launch {
            _overviewState.value = _overviewState.value.copy(isLoading = true, error = null)

            val snapshot = tokenDataStore.userSnapshot.first()

            val usersDeferred     = async { usersRepository.getUsers() }
            val coursesDeferred   = async { courseRepository.getCourses() }
            val statsDeferred     = async { orderRepository.getStats() }
            val subsDeferred      = async { subscriptionRepository.getMySubscriptions() }
            val auditDeferred     = async { adminConsoleRepository.getAuditLogs() }

            val users     = usersDeferred.await().getOrElse { emptyList() }
            val courses   = coursesDeferred.await().getOrElse { Pair(emptyList(), 0) }
            val stats     = statsDeferred.await().getOrElse { emptyMap() }
            val subs      = subsDeferred.await().getOrElse { emptyList() }
            val auditLogs = auditDeferred.await().getOrElse { emptyList() }

            val teachers  = users.filter { it.role?.lowercase() in setOf("teacher", "profesor", "docente") }
            val students  = users.filter { it.role?.lowercase() in setOf("student", "user", "estudiante") || !it.isStaff }

            val totalRevenue = (stats["total_revenue"] as? Number)?.toDouble() ?: 0.0
            val totalOrders  = (stats["total_orders"] as? Number)?.toInt() ?: 0
            val byStatus     = (stats["by_status"] as? Map<*, *>)?.mapNotNull { (k, v) ->
                if (k is String && v is Number) k to v.toInt() else null
            }?.toMap() ?: emptyMap()

            val recentOrders = async {
                adminConsoleRepository.getOrders()
            }.await().getOrElse { emptyList() }.take(5)

            _overviewState.value = AdminOverviewState(
                isLoading         = false,
                adminName         = snapshot?.username ?: "",
                adminEmail        = snapshot?.email ?: "",
                totalUsers        = users.size,
                totalTeachers     = teachers.size,
                totalStudents     = students.size,
                totalCourses      = courses.second,
                totalOrders       = totalOrders,
                totalRevenue      = totalRevenue,
                pendingOrders     = byStatus["pending"] ?: byStatus["PENDING"] ?: 0,
                completedOrders   = byStatus["completed"] ?: byStatus["COMPLETED"] ?: 0,
                totalSubscriptions = subs.size,
                recentOrders      = recentOrders,
                recentAuditLogs   = auditLogs.take(5)
            )
        }
    }

    // ─── USERS ────────────────────────────────────────────────────────────────

    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = _usersState.value.copy(isLoading = true, error = null)
            usersRepository.getUsers()
                .onSuccess { users ->
                    val teachers = users.filter { it.role?.lowercase() in setOf("teacher", "profesor", "docente", "instructor") }
                    val students = users.filter { it.role?.lowercase() in setOf("student", "user", "estudiante") || !it.isStaff }
                    _usersState.value = _usersState.value.copy(
                        isLoading  = false,
                        allUsers   = users,
                        teachers   = teachers,
                        students   = students
                    )
                }
                .onFailure { e ->
                    _usersState.value = _usersState.value.copy(
                        isLoading = false,
                        error     = e.message ?: "Error al cargar usuarios"
                    )
                }
        }
    }

    fun setUserSearchQuery(query: String) {
        _usersState.value = _usersState.value.copy(searchQuery = query)
    }

    fun setUserRoleFilter(role: String) {
        _usersState.value = _usersState.value.copy(filterRole = role)
    }

    val filteredUsers: List<User>
        get() {
            val state = _usersState.value
            return state.allUsers.filter { user ->
                val matchesSearch = state.searchQuery.isBlank() ||
                    user.username.contains(state.searchQuery, ignoreCase = true) ||
                    user.email.contains(state.searchQuery, ignoreCase = true) ||
                    (user.firstName + " " + user.lastName).contains(state.searchQuery, ignoreCase = true)
                val matchesRole = state.filterRole.isBlank() ||
                    user.role?.lowercase() == state.filterRole.lowercase()
                matchesSearch && matchesRole
            }
        }

    fun toggleUserActive(userId: Int, currentActive: Boolean) {
        viewModelScope.launch {
            usersRepository.updateUser(userId, isActive = !currentActive)
                .onSuccess { updatedUser ->
                    _usersState.value = _usersState.value.copy(
                        allUsers = _usersState.value.allUsers.map { if (it.id == userId) updatedUser else it }
                    )
                }
                .onFailure { e ->
                    _usersState.value = _usersState.value.copy(error = e.message)
                }
        }
    }

    fun changeUserRole(userId: Int, role: String) {
        viewModelScope.launch {
            usersRepository.updateUser(userId, role = role)
                .onSuccess { updatedUser ->
                    _usersState.value = _usersState.value.copy(
                        allUsers = _usersState.value.allUsers.map { if (it.id == userId) updatedUser else it },
                        successMessage = "Rol actualizado"
                    )
                }
                .onFailure { e ->
                    _usersState.value = _usersState.value.copy(error = e.message)
                }
        }
    }

    fun createUser(
        username: String, email: String, firstName: String,
        lastName: String, role: String, password: String
    ) {
        viewModelScope.launch {
            _usersState.value = _usersState.value.copy(isSubmitting = true, error = null)
            usersRepository.createUser(username, email, firstName, lastName, role, password)
                .onSuccess { newUser ->
                    _usersState.value = _usersState.value.copy(
                        isSubmitting   = false,
                        allUsers       = _usersState.value.allUsers + newUser,
                        successMessage = "Usuario creado correctamente"
                    )
                }
                .onFailure { e ->
                    _usersState.value = _usersState.value.copy(
                        isSubmitting = false,
                        error        = e.message ?: "Error al crear el usuario"
                    )
                }
        }
    }

    fun clearUsersMessages() {
        _usersState.value = _usersState.value.copy(error = null, successMessage = null)
    }

    // ─── COURSES ──────────────────────────────────────────────────────────────

    fun loadCourses() {
        viewModelScope.launch {
            _coursesState.value = _coursesState.value.copy(isLoading = true, error = null)
            courseRepository.getCourses(com.ute.guamanidiomas.domain.repository.CourseFilters(pageSize = 100))
                .onSuccess { (courses, total) ->
                    _coursesState.value = _coursesState.value.copy(
                        isLoading = false,
                        courses   = courses,
                        total     = total
                    )
                }
                .onFailure { e ->
                    _coursesState.value = _coursesState.value.copy(
                        isLoading = false,
                        error     = e.message
                    )
                }
        }
    }

    fun deleteCourse(id: Int) {
        viewModelScope.launch {
            _coursesState.value = _coursesState.value.copy(isDeleting = true)
            courseRepository.deleteCourse(id)
                .onSuccess {
                    _coursesState.value = _coursesState.value.copy(isDeleting = false)
                    loadCourses()
                }
                .onFailure { e ->
                    _coursesState.value = _coursesState.value.copy(
                        isDeleting = false,
                        error      = e.message
                    )
                }
        }
    }

    // ─── ORDERS ───────────────────────────────────────────────────────────────

    fun loadOrders(statusFilter: String = "") {
        viewModelScope.launch {
            _ordersState.value = _ordersState.value.copy(isLoading = true, error = null, filterStatus = statusFilter)
            adminConsoleRepository.getOrders()
                .onSuccess { orders ->
                    val filtered = if (statusFilter.isBlank()) orders
                    else orders.filter { it.status?.lowercase() == statusFilter.lowercase() }
                    val revenue = orders.sumOf { it.total ?: it.totalPrice ?: 0.0 }
                    _ordersState.value = _ordersState.value.copy(
                        isLoading    = false,
                        orders       = filtered,
                        totalRevenue = revenue
                    )
                }
                .onFailure { e ->
                    _ordersState.value = _ordersState.value.copy(
                        isLoading = false,
                        error     = e.message
                    )
                }
        }
    }

    fun approveOrder(orderId: Int) {
        viewModelScope.launch {
            adminConsoleRepository.approveOrder(orderId)
                .onSuccess { updated ->
                    _ordersState.value = _ordersState.value.copy(
                        orders         = _ordersState.value.orders.map { if (it.id == orderId) updated else it },
                        successMessage = "Orden aprobada"
                    )
                    loadOverview()
                }
                .onFailure { e ->
                    _ordersState.value = _ordersState.value.copy(error = e.message)
                }
        }
    }

    fun clearOrdersMessages() {
        _ordersState.value = _ordersState.value.copy(error = null, successMessage = null)
    }

    // ─── ROLES / AUDIT ────────────────────────────────────────────────────────

    fun loadRoles() {
        viewModelScope.launch {
            _rolesState.value = _rolesState.value.copy(isLoading = true, error = null)
            val rolesDeferred = async { adminConsoleRepository.getRoles() }
            val logsDeferred  = async { adminConsoleRepository.getAuditLogs() }

            val roles = rolesDeferred.await().getOrElse { emptyList() }
            val logs  = logsDeferred.await().getOrElse { emptyList() }

            _rolesState.value = _rolesState.value.copy(
                isLoading  = false,
                roles      = roles,
                auditLogs  = logs
            )
        }
    }

    fun createRole(name: String, permissions: List<String>) {
        viewModelScope.launch {
            _rolesState.value = _rolesState.value.copy(isSaving = true)
            adminConsoleRepository.createRole(name, permissions)
                .onSuccess { role ->
                    _rolesState.value = _rolesState.value.copy(
                        isSaving = false,
                        roles    = _rolesState.value.roles + role
                    )
                }
                .onFailure { e ->
                    _rolesState.value = _rolesState.value.copy(isSaving = false, error = e.message)
                }
        }
    }

    fun assignRoleToUser(userId: Int, role: String) {
        viewModelScope.launch {
            _rolesState.value = _rolesState.value.copy(isSaving = true)
            adminConsoleRepository.assignRoleToUser(userId, role)
                .onSuccess {
                    _rolesState.value = _rolesState.value.copy(isSaving = false)
                }
                .onFailure { e ->
                    _rolesState.value = _rolesState.value.copy(isSaving = false, error = e.message)
                }
        }
    }

    // ─── SUBSCRIPTIONS ────────────────────────────────────────────────────────

    fun loadSubscriptions() {
        viewModelScope.launch {
            _subscriptionsState.value = _subscriptionsState.value.copy(isLoading = true)
            subscriptionRepository.getPaymentHistory()
                .onSuccess { payments ->
                    val active = subscriptionRepository.getMySubscriptions()
                        .getOrElse { emptyList() }.count { it.isPremium }
                    _subscriptionsState.value = _subscriptionsState.value.copy(
                        isLoading          = false,
                        totalRevenue       = payments.sumOf { it.amount },
                        totalSubscriptions = payments.size,
                        activeSubscriptions = active
                    )
                }
                .onFailure { e ->
                    _subscriptionsState.value = _subscriptionsState.value.copy(
                        isLoading = false,
                        error     = e.message
                    )
                }
        }
    }
}
