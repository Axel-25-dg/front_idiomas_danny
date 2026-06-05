package com.ute.guamanidiomas.di

import com.ute.guamanidiomas.data.repository.AdminConsoleRepositoryImpl
import com.ute.guamanidiomas.data.repository.AdminUsersRepositoryImpl
import com.ute.guamanidiomas.data.repository.AuthRepositoryImpl
import com.ute.guamanidiomas.data.repository.CourseRepositoryImpl
import com.ute.guamanidiomas.data.repository.ExerciseRepositoryImpl
import com.ute.guamanidiomas.data.repository.GamificationRepositoryImpl
import com.ute.guamanidiomas.data.repository.LanguageRepositoryImpl
import com.ute.guamanidiomas.data.repository.LessonRepositoryImpl
import com.ute.guamanidiomas.data.repository.ModuleRepositoryImpl
import com.ute.guamanidiomas.data.repository.HomeRepositoryImpl
import com.ute.guamanidiomas.data.repository.OrderRepositoryImpl
import com.ute.guamanidiomas.data.repository.SubscriptionRepositoryImpl
import com.ute.guamanidiomas.data.repository.TeacherRepositoryImpl
import com.ute.guamanidiomas.data.repository.StudentClassroomRepositoryImpl
import com.ute.guamanidiomas.data.repository.CertificateRepositoryImpl
import com.ute.guamanidiomas.domain.repository.AdminConsoleRepository
import com.ute.guamanidiomas.domain.repository.AdminUsersRepository
import com.ute.guamanidiomas.domain.repository.AuthRepository
import com.ute.guamanidiomas.domain.repository.CourseRepository
import com.ute.guamanidiomas.domain.repository.ExerciseRepository
import com.ute.guamanidiomas.domain.repository.GamificationRepository
import com.ute.guamanidiomas.domain.repository.HomeRepository
import com.ute.guamanidiomas.domain.repository.LanguageRepository
import com.ute.guamanidiomas.domain.repository.LessonRepository
import com.ute.guamanidiomas.domain.repository.ModuleRepository
import com.ute.guamanidiomas.domain.repository.OrderRepository
import com.ute.guamanidiomas.domain.repository.SubscriptionRepository
import com.ute.guamanidiomas.domain.repository.TeacherRepository
import com.ute.guamanidiomas.domain.repository.StudentClassroomRepository
import com.ute.guamanidiomas.domain.repository.CertificateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAdminUsersRepository(
        adminUsersRepositoryImpl: AdminUsersRepositoryImpl
    ): AdminUsersRepository

    @Binds
    @Singleton
    abstract fun bindAdminConsoleRepository(
        adminConsoleRepositoryImpl: AdminConsoleRepositoryImpl
    ): AdminConsoleRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCourseRepository(
        courseRepositoryImpl: CourseRepositoryImpl
    ): CourseRepository

    @Binds
    @Singleton
    abstract fun bindExerciseRepository(
        exerciseRepositoryImpl: ExerciseRepositoryImpl
    ): ExerciseRepository

    @Binds
    @Singleton
    abstract fun bindLanguageRepository(
        languageRepositoryImpl: LanguageRepositoryImpl
    ): LanguageRepository

    @Binds
    @Singleton
    abstract fun bindLessonRepository(
        lessonRepositoryImpl: LessonRepositoryImpl
    ): LessonRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository

    @Binds
    @Singleton
    abstract fun bindModuleRepository(
        moduleRepositoryImpl: ModuleRepositoryImpl
    ): ModuleRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository

    @Binds
    @Singleton
    abstract fun bindGamificationRepository(
        gamificationRepositoryImpl: GamificationRepositoryImpl
    ): GamificationRepository

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(
        subscriptionRepositoryImpl: SubscriptionRepositoryImpl
    ): SubscriptionRepository

    @Binds
    @Singleton
    abstract fun bindTeacherRepository(
        teacherRepositoryImpl: TeacherRepositoryImpl
    ): TeacherRepository

    @Binds
    @Singleton
    abstract fun bindStudentClassroomRepository(
        studentClassroomRepositoryImpl: StudentClassroomRepositoryImpl
    ): StudentClassroomRepository

    @Binds
    @Singleton
    abstract fun bindCertificateRepository(
        certificateRepositoryImpl: CertificateRepositoryImpl
    ): CertificateRepository
}
