package com.example.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Dialpad
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DialerTab

@Composable
fun DialerBottomBar(
    currentTab: DialerTab,
    missedCallsCount: Int,
    onTabSelected: (DialerTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(64.dp)
            .liquidGlassBackground(
                shape = RoundedCornerShape(32.dp),
                isDark = isDark,
                elevation = 8.dp
            ),
        containerColor = Color.Transparent,
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            Triple(DialerTab.FAVORITES, "Favorites", Pair(Icons.Filled.Star, Icons.Outlined.Star)),
            Triple(DialerTab.RECENTS, "Recents", Pair(Icons.Filled.History, Icons.Outlined.History)),
            Triple(DialerTab.CONTACTS, "Contacts", Pair(Icons.Filled.People, Icons.Outlined.People)),
            Triple(DialerTab.KEYPAD, "Keypad", Pair(Icons.Filled.Dialpad, Icons.Outlined.Dialpad))
        )

        items.forEach { (tab, label, icons) ->
            val isSelected = currentTab == tab

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    if (tab == DialerTab.RECENTS && missedCallsCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                ) {
                                    Text(
                                        text = missedCallsCount.toString(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) icons.first else icons.second,
                                contentDescription = label
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (isSelected) icons.first else icons.second,
                            contentDescription = label
                        )
                    }
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = if (isDark) Color(0x3338BDF8) else Color(0x400070F3),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                ),
                modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
            )
        }
    }
}
