package com.ute.guamanidiomas.navigation

sealed class Screen(val route: String) {
    // Auth
    data object Login    : Screen("login")
    data object Register : Screen("register")

    // Student / Client
    data object Home     : Screen("home")
    data object Catalog  : Screen("catalog")
    data object Orders   : Screen("orders")
    data object Profile  : Screen("profile")
    data object Premium  : Screen("premium")
    data object Settings : Screen("settings")
    data object MyClasses : Screen("my_classes")
    data object MyClassDetail : Screen("my_classes/{classroomId}") {
        fun createRoute(classroomId: Int) = "my_classes/$classroomId"
    }
    data object MyCertificates : Screen("my_certificates")
    data object Achievements : Screen("achievements")
    data object Leaderboard : Screen("leaderboard")

    data object LearningPath : Screen("learning_path/{courseId}") {
        fun createRoute(courseId: Int) = "learning_path/$courseId"
    }
    data object Exercise : Screen("exercise/{exerciseId}") {
        fun createRoute(exerciseId: Int) = "exercise/$exerciseId"
    }
    data object GameCenter : Screen("games")
    data object WordMatch : Screen("games/word_match")
    data object Flashcards : Screen("games/flashcards")
    data object SentenceBuilder : Screen("games/sentence_builder")
    data object VocabQuiz : Screen("games/vocab_quiz")
    data object Hangman : Screen("games/hangman")
    data object MemoryCards : Screen("games/memory_cards")
    data object ListeningChallenge : Screen("games/listening_challenge")
    data object VerbConjugation : Screen("games/verb_conjugation")
    data object JoinClass : Screen("join_class")
    data object IATutor   : Screen("ia_tutor")
    data object Certificate : Screen("certificate/{courseId}") {
        fun createRoute(courseId: Int) = "certificate/$courseId"
    }

    // Teacher
    data object TeacherDashboard : Screen("teacher")
    data object TeacherClasses   : Screen("teacher/classes")
    data object TeacherStudents  : Screen("teacher/students")
    data object TeacherExams     : Screen("teacher/exams")
    data object TeacherResources : Screen("teacher/resources")

    // Admin
    data object AdminDashboard : Screen("admin")
    data object UserManagement : Screen("admin/users")
    data object RolesManagement : Screen("admin/roles")
    data object AdminOrders : Screen("admin/orders")
    data object AuditLogs : Screen("admin/audit-logs")
    data object CourseManagement : Screen("admin/courses")
    data object CourseCreate   : Screen("admin/course/new")
    data object CourseEdit     : Screen("admin/course/edit/{id}") {
        fun createRoute(id: Int) = "admin/course/edit/$id"
    }
    data object ModuleManagement : Screen("admin/course/{courseId}/modules") {
        fun createRoute(courseId: Int) = "admin/course/$courseId/modules"
    }
    data object ModuleCreate : Screen("admin/course/{courseId}/module/new") {
        fun createRoute(courseId: Int) = "admin/course/$courseId/module/new"
    }
    data object ModuleEdit : Screen("admin/course/{courseId}/module/edit/{moduleId}") {
        fun createRoute(courseId: Int, moduleId: Int) = "admin/course/$courseId/module/edit/$moduleId"
    }
}
