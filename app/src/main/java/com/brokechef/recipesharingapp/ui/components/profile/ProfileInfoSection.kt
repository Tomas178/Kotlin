package com.brokechef.recipesharingapp.ui.components.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brokechef.recipesharingapp.data.enums.FollowModalType
import com.brokechef.recipesharingapp.data.models.openapi.UsersFindById200Response
import com.brokechef.recipesharingapp.ui.theme.PrimaryGreen

@Composable
fun ProfileInfoSection(
    user: UsersFindById200Response,
    isOwnProfile: Boolean,
    isFollowing: Boolean,
    totalFollowers: Int,
    totalFollowing: Int,
    onFollow: () -> Unit,
    onUnfollow: () -> Unit,
    onOpenFollowModal: (FollowModalType) -> Unit,
    onNavigateToEditProfile: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Following: $totalFollowing",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier =
                            Modifier.clickable {
                                onOpenFollowModal(FollowModalType.FOLLOWING)
                            },
                    )
                    Text(
                        text = "Followers: $totalFollowers",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier =
                            Modifier.clickable {
                                onOpenFollowModal(FollowModalType.FOLLOWERS)
                            },
                    )
                }
            }

            if (isOwnProfile) {
                ProfileActionButton(
                    text = "Change Credentials",
                    onClick = { onNavigateToEditProfile(user.id) },
                )
            } else {
                ProfileActionButton(
                    text = if (isFollowing) "Unfollow" else "Follow",
                    onClick = if (isFollowing) onUnfollow else onFollow,
                )
            }
        }
    }
}

@Composable
private fun ProfileActionButton(
    text: String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors =
            ButtonDefaults.outlinedButtonColors(
                contentColor = PrimaryGreen,
            ),
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )
    }
}
