package com.brokechef.recipesharingapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brokechef.recipesharingapp.ui.theme.PrimaryGreen

private val ActivePageBackground = PrimaryGreen
private val ActivePageText = Color.White
private val InactivePageText = Color(0xFF6B7280)
private val InactivePageBackground = Color.White
private val DisabledColor = Color(0xFFD1D5DB)
private val BorderColor = Color(0xFFE5E7EB)

@Composable
fun Pagination(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxVisiblePages: Int = 5,
) {
    if (totalPages <= 1) return

    val pageNumbers = computeVisiblePages(currentPage, totalPages, maxVisiblePages)

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PaginationNavButton(
            text = "«",
            onClick = { onPageChange(1) },
            enabled = enabled && currentPage > 1,
        )

        PaginationNavButton(
            text = "‹",
            onClick = { onPageChange(currentPage - 1) },
            enabled = enabled && currentPage > 1,
        )

        pageNumbers.forEach { page ->
            if (page == -1) {
                // Ellipsis
                Text(
                    text = "…",
                    modifier = Modifier.padding(horizontal = 4.dp),
                    color = InactivePageText,
                    fontSize = 16.sp,
                )
            } else {
                PaginationPageButton(
                    page = page,
                    isActive = page == currentPage,
                    onClick = { onPageChange(page) },
                    enabled = enabled,
                )
            }
        }

        PaginationNavButton(
            text = "›",
            onClick = { onPageChange(currentPage + 1) },
            enabled = enabled && currentPage < totalPages,
        )

        PaginationNavButton(
            text = "»",
            onClick = { onPageChange(totalPages) },
            enabled = enabled && currentPage < totalPages,
        )
    }
}

@Composable
private fun PaginationPageButton(
    page: Int,
    isActive: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier =
            Modifier
                .padding(horizontal = 2.dp)
                .size(44.dp),
        shape = RoundedCornerShape(8.dp),
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = if (isActive) ActivePageBackground else InactivePageBackground,
                contentColor = if (isActive) ActivePageText else InactivePageText,
                disabledContainerColor = if (isActive) ActivePageBackground.copy(alpha = 0.5f) else InactivePageBackground,
                disabledContentColor = DisabledColor,
            ),
        border =
            ButtonDefaults.outlinedButtonBorder(enabled).copy(
                width = 1.dp,
            ),
    ) {
        Text(
            text = page.toString(),
            fontSize = 14.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

@Composable
private fun PaginationNavButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier =
            Modifier
                .padding(horizontal = 2.dp)
                .size(44.dp),
        shape = RoundedCornerShape(8.dp),
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = InactivePageBackground,
                contentColor = InactivePageText,
                disabledContainerColor = InactivePageBackground,
                disabledContentColor = DisabledColor,
            ),
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun computeVisiblePages(
    currentPage: Int,
    totalPages: Int,
    maxVisible: Int,
): List<Int> {
    if (totalPages <= maxVisible) {
        return (1..totalPages).toList()
    }

    val pages = mutableListOf<Int>()
    val halfWindow = (maxVisible - 3) / 2

    pages.add(1)

    val rangeStart = (currentPage - halfWindow).coerceAtLeast(2)
    val rangeEnd = (currentPage + halfWindow).coerceAtMost(totalPages - 1)

    if (rangeStart > 2) {
        pages.add(-1)
    }

    for (i in rangeStart..rangeEnd) {
        pages.add(i)
    }

    if (rangeEnd < totalPages - 1) {
        pages.add(-1)
    }

    pages.add(totalPages)

    return pages
}
