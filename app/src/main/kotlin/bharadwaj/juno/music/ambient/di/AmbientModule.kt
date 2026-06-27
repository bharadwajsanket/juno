package bharadwaj.juno.music.ambient.di

import bharadwaj.juno.music.ambient.AmbientRepository
import bharadwaj.juno.music.ambient.AmbientRepositoryImpl
import bharadwaj.juno.music.ambient.location.AmbientLocationProvider
import bharadwaj.juno.music.ambient.location.FusedAmbientLocationProvider
import bharadwaj.juno.music.ambient.weather.AmbientWeatherProvider
import bharadwaj.juno.music.ambient.weather.OpenMeteoWeatherProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Hilt module for all Ambient system dependencies.
 *
 * Bindings:
 *   [AmbientLocationProvider] → [FusedAmbientLocationProvider]
 *   [AmbientWeatherProvider]  → [OpenMeteoWeatherProvider]
 *   [AmbientRepository]       → [AmbientRepositoryImpl]
 *
 * The [AmbientHttpClient] qualifier isolates the Ambient Ktor client from any
 * future shared client, preventing accidental config bleed.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AmbientModule {

    @Binds
    @Singleton
    abstract fun bindLocationProvider(
        impl: FusedAmbientLocationProvider,
    ): AmbientLocationProvider

    @Binds
    @Singleton
    abstract fun bindAmbientRepository(
        impl: AmbientRepositoryImpl,
    ): AmbientRepository

    companion object {

        @Provides
        @Singleton
        @AmbientHttpClient
        fun provideAmbientHttpClient(): HttpClient = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        coerceInputValues = true
                    },
                )
            }
        }

        @Provides
        @Singleton
        fun provideWeatherProvider(
            @AmbientHttpClient httpClient: HttpClient,
        ): AmbientWeatherProvider = OpenMeteoWeatherProvider(httpClient)
    }
}

// ─── Qualifier ────────────────────────────────────────────────────────────────

/** Qualifier to distinguish the Ambient Ktor client from other HTTP clients. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AmbientHttpClient
