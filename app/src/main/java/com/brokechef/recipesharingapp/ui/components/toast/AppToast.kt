package com.brokechef.recipesharingapp.ui.components.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brokechef.recipesharingapp.ui.components.MyCustomCircularProgressIndicator
import kotlinx.coroutines.delay

@Composable
fun AppToast() {
    val toast = ToastState.current

    LaunchedEffect(toast) {
        if (toast != null && toast.type != ToastType.LOADING) {
            delay(3000)
            ToastState.dismiss()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = toast != null,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp),
        ) {
            toast?.let {
                val backgroundColor =
                    when (it.type) {
                        ToastType.SUCCESS -> Color(0xFF22C55E)
                        ToastType.ERROR -> Color(0xFFEF4444)
                        ToastType.INFO -> Color(0xFF3B82F6)
                        ToastType.LOADING -> Color(0xFF6B7280)
                    }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(backgroundColor)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    if (it.type == ToastType.LOADING) {
                        MyCustomCircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Text(
                        text = it.message,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}
