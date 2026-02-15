package com.brokechef.recipesharingapp.ui.components.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.brokechef.recipesharingapp.data.models.openapi.CollectionsFindByUserId200ResponseInner

@Composable
fun CollectionDropdown(
    collections: List<CollectionsFindByUserId200ResponseInner>,
    onExpand: () -> Unit,
    onSaveToCollection: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        TextButton(
            onClick = {
                expanded = true
                onExpand()
            },
        ) {
            Text("Add to Collection")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            Column(
                modifier =
                    Modifier
                        .width(200.dp)
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                collections.forEach { collection ->
                    CollectionItem(
                        collection = collection,
                        onClick = {
                            onSaveToCollection(collection.id)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun CollectionItem(
    collection: CollectionsFindByUserId200ResponseInner,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape),
            shape = CircleShape,
            colors =
                CardDefaults.cardColors(
                    containerColor = Color.LightGray,
                ),
        ) {
            if (collection.imageUrl != null) {
                AsyncImage(
                    model = collection.imageUrl,
                    contentDescription = collection.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = collection.title.first().uppercase(),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        Text(
            text = collection.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}
