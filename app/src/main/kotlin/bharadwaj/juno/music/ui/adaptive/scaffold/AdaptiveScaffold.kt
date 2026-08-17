package bharadwaj.juno.music.ui.adaptive.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import bharadwaj.juno.music.ui.adaptive.navigation.AdaptiveNavigationHost
import bharadwaj.juno.music.ui.adaptive.navigation.AdaptiveNavigationStyle
import bharadwaj.juno.music.ui.adaptive.theme.LocalAdaptiveWindowInfo

/**
 * Modern, adaptive scaffold primitive that serves as the root UI container for JUNO Music screens.
 * Seamlessly manages adaptive window state, navigation hosts (BottomBar, Rail, Drawer), player bar placement,
 * and adaptive spacing.
 */
@Composable
fun AdaptiveScaffold(
    modifier: Modifier = Modifier,
    scaffoldState: AdaptiveScaffoldState = rememberAdaptiveScaffoldState(LocalAdaptiveWindowInfo.current),
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    navigationRail: @Composable (() -> Unit)? = null,
    permanentDrawer: @Composable (() -> Unit)? = null,
    snackbarHost: @Composable () -> Unit = { SnackbarHost(scaffoldState.snackbarHostState) },
    playerBar: @Composable (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val windowInfo = scaffoldState.windowInfo
    val navStyle = windowInfo.preferredNavigationStyle

    AdaptiveNavigationHost(
        modifier = modifier,
        navigationStyle = navStyle,
        bottomBarSlot = if (navStyle == AdaptiveNavigationStyle.BottomBar) bottomBar else null,
        navigationRailSlot = navigationRail,
        permanentDrawerSlot = permanentDrawer
    ) {
        Scaffold(
            topBar = topBar,
            bottomBar = {
                if (navStyle == AdaptiveNavigationStyle.BottomBar) {
                    bottomBar()
                }
            },
            snackbarHost = snackbarHost,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                content(PaddingValues())
                playerBar?.invoke()
            }
        }
    }
}
