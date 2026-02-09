package com.brokechef.recipesharingapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.brokechef.recipesharingapp.R
import com.brokechef.recipesharingapp.ui.navigation.Screen
import com.brokechef.recipesharingapp.ui.navigation.drawerScreens

@Composable
fun AppDrawer(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalDrawerSheet(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(24.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "BrokeChef logo",
                modifier = Modifier.size(48.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "BrokeChef",
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))
        drawerScreens.forEach { screen ->
            NavigationDrawerItem(
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = { onNavigate(screen) },
                modifier = Modifier.padding(horizontal = 12.dp),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider()
        NavigationDrawerItem(
            label = { Text("Sign Out") },
            selected = false,
            onClick = onSignOut,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
