package com.brokechef.recipesharingapp.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brokechef.recipesharingapp.ui.theme.PrimaryGreen
import com.brokechef.recipesharingapp.ui.theme.PrimaryGreenDark
import com.brokechef.recipesharingapp.ui.theme.SecondaryGreen
import com.brokechef.recipesharingapp.ui.theme.SecondaryGreenDark
import com.brokechef.recipesharingapp.ui.theme.SubmitText
import com.brokechef.recipesharingapp.ui.theme.SubmitTextDark
import com.brokechef.recipesharingapp.ui.theme.TertiaryGreen
import com.brokechef.recipesharingapp.ui.theme.TertiaryGreenDark

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fillWidth: Boolean = true,
) {
    val isDark = isSystemInDarkTheme()
    val gradientColors =
        if (isDark) {
            listOf(PrimaryGreenDark, SecondaryGreenDark, TertiaryGreenDark)
        } else {
            listOf(PrimaryGreen, SecondaryGreen, TertiaryGreen)
        }
    val textColor = if (isDark) SubmitTextDark else SubmitText

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.horizontalGradient(colors = gradientColors),
                ).clickable { onClick() }
                .padding(vertical = 16.dp, horizontal = 24.dp),
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}
