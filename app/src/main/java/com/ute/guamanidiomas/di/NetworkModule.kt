package com.ute.guamanidiomas.di

import com.ute.guamanidiomas.BuildConfig
import com.ute.guamanidiomas.data.remote.api.AdminUsersApi
import com.ute.guamanidiomas.data.remote.api.AdminConsoleApi
import com.ute.guamanidiomas.data.remote.api.AuthApi
import com.ute.guamanidiomas.data.remote.api.CourseApi
import com.ute.guamanidiomas.data.remote.api.ExerciseApi
import com.ute.guamanidiomas.data.remote.api.GamificationApi
import com.ute.guamanidiomas.data.remote.api.HomeApi
import com.ute.guamanidiomas.data.remote.api.LanguageApi
import com.ute.guamanidiomas.data.remote.api.LessonApi
import com.ute.guamanidiomas.data.remote.api.ModuleApi
import com.ute.guamanidiomas.data.remote.api.OrderApi
import com.ute.guamanidiomas.data.remote.api.SubscriptionApi
import com.ute.guamanidiomas.data.remote.api.TutorApi
import com.ute.guamanidiomas.data.remote.api.TeacherClassroomApi
import com.ute.guamanidiomas.data.remote.api.TeacherExamApi
import com.ute.guamanidiomas.data.remote.api.TeacherResourceApi
import com.ute.guamanidiomas.data.remote.api.ClassroomApi
import com.ute.guamanidiomas.data.remote.api.CertificateApi
import com.ute.guamanidiomas.data.remote.interceptors.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })

        if (BuildConfig.DEBUG) {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            val sslContext = SSLContext.getInstance("SSL").apply {
                init(null, trustAllCerts, SecureRandom())
            }

            builder.sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAdminUsersApi(retrofit: Retrofit): AdminUsersApi {
        return retrofit.create(AdminUsersApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAdminConsoleApi(retrofit: Retrofit): AdminConsoleApi {
        return retrofit.create(AdminConsoleApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCourseApi(retrofit: Retrofit): CourseApi {
        return retrofit.create(CourseApi::class.java)
    }

    @Provides
    @Singleton
    fun provideExerciseApi(retrofit: Retrofit): ExerciseApi {
        return retrofit.create(ExerciseApi::class.java)
    }

    @Provides
    @Singleton
    fun provideModuleApi(retrofit: Retrofit): ModuleApi {
        return retrofit.create(ModuleApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLanguageApi(retrofit: Retrofit): LanguageApi {
        return retrofit.create(LanguageApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLessonApi(retrofit: Retrofit): LessonApi {
        return retrofit.create(LessonApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHomeApi(retrofit: Retrofit): HomeApi {
        return retrofit.create(HomeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOrderApi(retrofit: Retrofit): OrderApi {
        return retrofit.create(OrderApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGamificationApi(retrofit: Retrofit): GamificationApi {
        return retrofit.create(GamificationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSubscriptionApi(retrofit: Retrofit): SubscriptionApi {
        return retrofit.create(SubscriptionApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTutorApi(retrofit: Retrofit): TutorApi {
        return retrofit.create(TutorApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTeacherClassroomApi(retrofit: Retrofit): TeacherClassroomApi {
        return retrofit.create(TeacherClassroomApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTeacherExamApi(retrofit: Retrofit): TeacherExamApi {
        return retrofit.create(TeacherExamApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTeacherResourceApi(retrofit: Retrofit): TeacherResourceApi {
        return retrofit.create(TeacherResourceApi::class.java)
    }

    @Provides
    @Singleton
    fun provideClassroomApi(retrofit: Retrofit): ClassroomApi {
        return retrofit.create(ClassroomApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCertificateApi(retrofit: Retrofit): CertificateApi {
        return retrofit.create(CertificateApi::class.java)
    }
}