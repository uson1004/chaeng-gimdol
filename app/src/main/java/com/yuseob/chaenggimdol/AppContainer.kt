package com.yuseob.chaenggimdol

import android.content.Context
import androidx.room.Room
import com.yuseob.chaenggimdol.data.local.ChaenggimDatabase
import com.yuseob.chaenggimdol.data.repository.RoomItemRepository
import com.yuseob.chaenggimdol.data.repository.RoomSessionRepository
import com.yuseob.chaenggimdol.data.settings.AppPreferencesRepository
import com.yuseob.chaenggimdol.domain.item.ItemRepository
import com.yuseob.chaenggimdol.domain.item.SeedDefaultItemsUseCase
import com.yuseob.chaenggimdol.domain.session.CompleteCheckSessionUseCase
import com.yuseob.chaenggimdol.domain.session.SessionRepository
import com.yuseob.chaenggimdol.domain.session.StartCheckSessionUseCase
import com.yuseob.chaenggimdol.location.AndroidLocationSessionController
import com.yuseob.chaenggimdol.location.LocationSessionController
import com.yuseob.chaenggimdol.notification.AndroidReminderNotifier
import com.yuseob.chaenggimdol.notification.ReminderNotifier
import com.yuseob.chaenggimdol.ui.home.HomeViewModel
import com.yuseob.chaenggimdol.ui.items.ItemsViewModel
import com.yuseob.chaenggimdol.ui.onboarding.OnboardingViewModel
import com.yuseob.chaenggimdol.ui.session.SessionViewModel
import com.yuseob.chaenggimdol.ui.settings.SettingsViewModel

class AppContainer private constructor(
    private val database: ChaenggimDatabase,
    val itemRepository: ItemRepository,
    val sessionRepository: SessionRepository,
    val preferences: AppPreferencesRepository,
    val locationController: LocationSessionController,
    val reminderNotifier: ReminderNotifier,
) {
    suspend fun clearAllData() {
        locationController.stop()
        database.clearAllTables()
        preferences.clearAll()
    }

    fun onboardingViewModel() = OnboardingViewModel(
        seedDefaultItems = SeedDefaultItemsUseCase(itemRepository),
        preferences = preferences,
    )

    fun itemsViewModel() = ItemsViewModel(itemRepository)

    fun homeViewModel(
        canStartTracking: () -> Boolean,
    ) = HomeViewModel(
        itemRepository = itemRepository,
        sessionRepository = sessionRepository,
        startCheckSession = StartCheckSessionUseCase(sessionRepository),
        locationController = locationController,
        canStartTracking = canStartTracking,
    )

    fun sessionViewModel(sessionId: Long) = SessionViewModel(
        sessionId = sessionId,
        repository = sessionRepository,
        completeSession = CompleteCheckSessionUseCase(sessionRepository),
        locationController = locationController,
    )

    fun settingsViewModel() = SettingsViewModel(
        preferences = preferences,
        locationController = locationController,
        clearAllData = ::clearAllData,
    )

    companion object {
        fun create(context: Context): AppContainer {
            val appContext = context.applicationContext
            val database = Room.databaseBuilder(
                appContext,
                ChaenggimDatabase::class.java,
                "chaenggimdol.db",
            ).addMigrations(
                ChaenggimDatabase.MIGRATION_1_2,
                ChaenggimDatabase.MIGRATION_2_3,
            ).build()
            return AppContainer(
                database = database,
                itemRepository = RoomItemRepository(
                    database.userItemDao(),
                ),
                sessionRepository = RoomSessionRepository(database),
                preferences = AppPreferencesRepository(appContext),
                locationController = AndroidLocationSessionController(
                    appContext,
                ),
                reminderNotifier = AndroidReminderNotifier(appContext),
            )
        }
    }
}
