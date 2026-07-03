package com.yuseob.chaenggimdol.navigation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.yuseob.chaenggimdol.AppContainer
import com.yuseob.chaenggimdol.domain.session.CheckStatus
import com.yuseob.chaenggimdol.ui.complete.CompleteScreen
import com.yuseob.chaenggimdol.ui.home.HomeEffect
import com.yuseob.chaenggimdol.ui.home.HomeScreen
import com.yuseob.chaenggimdol.ui.home.PermissionUiState
import com.yuseob.chaenggimdol.ui.items.ItemsScreen
import com.yuseob.chaenggimdol.ui.onboarding.OnboardingScreen
import com.yuseob.chaenggimdol.ui.session.SessionScreen
import com.yuseob.chaenggimdol.ui.settings.SettingsScreen
import com.yuseob.chaenggimdol.ui.theme.Charcoal
import com.yuseob.chaenggimdol.ui.theme.DeepLilac
import com.yuseob.chaenggimdol.ui.theme.LilacContainer
import com.yuseob.chaenggimdol.ui.theme.WarmIvory
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AppNavigation(
    container: AppContainer,
    onboardingComplete: Boolean,
    notificationSessionId: Long?,
) {
    val initial = if (onboardingComplete) {
        AppRoute.Home
    } else {
        AppRoute.Onboarding
    }
    val backStack = rememberNavBackStack(initial)
    var notificationMessage by remember {
        mutableStateOf<String?>(null)
    }

    fun navigateTopLevel(route: AppRoute) {
        backStack.clear()
        backStack.add(route)
    }

    LaunchedEffect(notificationSessionId, onboardingComplete) {
        val sessionId = notificationSessionId ?: return@LaunchedEffect
        if (!onboardingComplete) return@LaunchedEffect
        val session = container.sessionRepository.get(sessionId)
        if (session?.completedAtMillis == null && session != null) {
            backStack.clear()
            backStack.add(AppRoute.Home)
            backStack.add(AppRoute.Session(sessionId))
        } else {
            notificationMessage = "이미 끝난 챙김 세션이에요."
            navigateTopLevel(AppRoute.Home)
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<AppRoute.Onboarding> {
                OnboardingRoute(
                    container = container,
                    onCompleted = {
                        navigateTopLevel(AppRoute.Home)
                    },
                )
            }
            entry<AppRoute.Home> {
                TopLevelScaffold(
                    selected = AppRoute.Home,
                    onSelect = ::navigateTopLevel,
                ) {
                    HomeRoute(
                        container = container,
                        externalMessage = notificationMessage,
                        onItems = {
                            navigateTopLevel(AppRoute.Items)
                        },
                        onSession = { sessionId ->
                            backStack.add(AppRoute.Session(sessionId))
                        },
                    )
                }
            }
            entry<AppRoute.Items> {
                TopLevelScaffold(
                    selected = AppRoute.Items,
                    onSelect = ::navigateTopLevel,
                ) {
                    ItemsRoute(container)
                }
            }
            entry<AppRoute.Settings> {
                TopLevelScaffold(
                    selected = AppRoute.Settings,
                    onSelect = ::navigateTopLevel,
                ) {
                    SettingsRoute(
                        container = container,
                        onDataDeleted = {
                            navigateTopLevel(AppRoute.Onboarding)
                        },
                    )
                }
            }
            entry<AppRoute.Session> { key ->
                SessionRoute(
                    container = container,
                    sessionId = key.sessionId,
                    onCompleted = {
                        backStack.add(
                            AppRoute.Complete(key.sessionId),
                        )
                    },
                )
            }
            entry<AppRoute.Complete> { key ->
                CompleteRoute(
                    container = container,
                    sessionId = key.sessionId,
                    onHome = {
                        navigateTopLevel(AppRoute.Home)
                    },
                )
            }
        },
    )
}

@Composable
private fun TopLevelScaffold(
    selected: AppRoute,
    onSelect: (AppRoute) -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(
        bottomBar = {
            TopLevelNavigationBar(
                selected = selected,
                onSelect = onSelect,
            )
        },
    ) { padding ->
        Box(Modifier.padding(padding)) {
            content()
        }
    }
}

@Composable
internal fun TopLevelNavigationBar(
    selected: AppRoute,
    onSelect: (AppRoute) -> Unit,
) {
    val destinations = listOf(
        AppRoute.Home to "오늘",
        AppRoute.Items to "내 물건",
        AppRoute.Settings to "설정",
    )
    NavigationBar(
        containerColor = WarmIvory,
    ) {
        destinations.forEach { (route, label) ->
            val selectedItem = selected == route
            NavigationBarItem(
                selected = selectedItem,
                onClick = { onSelect(route) },
                icon = {
                    NavigationDot(selected = selectedItem)
                },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DeepLilac,
                    selectedTextColor = DeepLilac,
                    indicatorColor = LilacContainer,
                    unselectedIconColor = Charcoal.copy(alpha = 0.72f),
                    unselectedTextColor = Charcoal.copy(alpha = 0.72f),
                ),
            )
        }
    }
}

@Composable
private fun NavigationDot(selected: Boolean) {
    Box(
        modifier = Modifier
            .clearAndSetSemantics {}
            .size(if (selected) 12.dp else 8.dp)
            .clip(CircleShape)
            .background(
                if (selected) {
                    DeepLilac
                } else {
                    Charcoal.copy(alpha = 0.72f)
                },
            ),
    )
}

@Composable
private fun OnboardingRoute(
    container: AppContainer,
    onCompleted: () -> Unit,
) {
    val viewModel = viewModel {
        container.onboardingViewModel()
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    OnboardingScreen(
        state = state,
        onToggle = viewModel::toggle,
        onComplete = {
            viewModel.complete(onCompleted)
        },
    )
}

@Composable
private fun HomeRoute(
    container: AppContainer,
    externalMessage: String?,
    onItems: () -> Unit,
    onSession: (Long) -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val trackingEnabled by container.preferences.isLocationTrackingEnabled
        .collectAsStateWithLifecycle(initialValue = false)
    var permissionRevision by remember {
        mutableIntStateOf(0)
    }
    val requestPermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        permissionRevision += 1
    }
    val locationGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED
    val notificationGranted =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
    val permissionState = PermissionUiState(
        locationGranted = locationGranted,
        notificationGranted = notificationGranted,
        locationTrackingEnabled = trackingEnabled,
        showLocationRationale = activity?.let {
            ActivityCompat.shouldShowRequestPermissionRationale(
                it,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        } == true,
        showNotificationRationale = activity?.let {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.shouldShowRequestPermissionRationale(
                    it,
                    Manifest.permission.POST_NOTIFICATIONS,
                )
        } == true,
    )
    val viewModel = viewModel {
        container.homeViewModel {
            trackingEnabled && locationGranted
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is HomeEffect.NavigateToSession -> {
                    onSession(effect.sessionId)
                }
            }
        }
    }
    LaunchedEffect(externalMessage) {
        externalMessage?.let(viewModel::showMessage)
    }

    HomeScreen(
        state = state,
        permissionState = permissionState,
        onStart = {
            if (state.activeSessionId == null) {
                viewModel.startSession()
            } else {
                viewModel.resumeSession()
            }
        },
        onRegisterItems = onItems,
        onRequestPermissions = {
            requestPermissions.launch(
                buildList {
                    add(Manifest.permission.ACCESS_COARSE_LOCATION)
                    add(Manifest.permission.ACCESS_FINE_LOCATION)
                    if (
                        Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.TIRAMISU
                    ) {
                        add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }.toTypedArray(),
            )
        },
        onOpenSettings = {
            context.startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:${context.packageName}"),
                ),
            )
        },
    )
}

@Composable
private fun ItemsRoute(container: AppContainer) {
    val viewModel = viewModel {
        container.itemsViewModel()
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    ItemsScreen(
        state = state,
        onNameChange = viewModel::setEditorName,
        onAdd = viewModel::saveEditor,
        onToggleActive = viewModel::toggleActive,
        onDelete = viewModel::delete,
        onRetry = viewModel::retrySave,
    )
}

@Composable
private fun SettingsRoute(
    container: AppContainer,
    onDataDeleted: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel = viewModel {
        container.settingsViewModel()
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsScreen(
        state = state,
        onLocationTrackingChanged =
            viewModel::setLocationTrackingEnabled,
        onOpenNotificationSettings = {
            context.startActivity(
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(
                        Settings.EXTRA_APP_PACKAGE,
                        context.packageName,
                    ),
            )
        },
        onRequestDelete = viewModel::requestDelete,
        onDismissDelete = viewModel::dismissDelete,
        onConfirmDelete = {
            viewModel.confirmDelete(onDataDeleted)
        },
    )
}

@Composable
private fun SessionRoute(
    container: AppContainer,
    sessionId: Long,
    onCompleted: () -> Unit,
) {
    val viewModel = viewModel(
        key = "session-$sessionId",
    ) {
        container.sessionViewModel(sessionId)
    }
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.completed) {
        if (state.completed) {
            onCompleted()
        }
    }

    SessionScreen(
        state = state,
        onTogglePacked = viewModel::togglePacked,
        onNotApplicable = viewModel::markNotApplicable,
        onRequestComplete = viewModel::requestComplete,
        onDismissIncomplete = viewModel::dismissIncomplete,
        onConfirmComplete = viewModel::completeConfirmed,
    )
}

@Composable
private fun CompleteRoute(
    container: AppContainer,
    sessionId: Long,
    onHome: () -> Unit,
) {
    var checkedCount by remember(sessionId) {
        mutableIntStateOf(0)
    }
    LaunchedEffect(sessionId) {
        checkedCount = container.sessionRepository
            .get(sessionId)
            ?.items
            ?.count { it.status != CheckStatus.Unchecked }
            ?: 0
    }
    CompleteScreen(
        checkedCount = checkedCount,
        onHome = onHome,
    )
}
