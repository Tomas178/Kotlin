package com.brokechef.recipesharingapp.data.mappers

import com.brokechef.recipesharingapp.data.models.openapi.RecipesSearch200ResponseInner
import com.brokechef.recipesharingapp.data.models.openapi.RecipesSearch200ResponseInnerAuthor

fun RecipesSearch200ResponseInner.toRecipeFindAll(): RecipesSearch200ResponseInner =
    RecipesSearch200ResponseInner(
        id = id,
        userId = userId,
        title = title,
        duration = duration,
        createdAt = createdAt,
        author =
            RecipesSearch200ResponseInnerAuthor(
                name = author.name,
                image = author.image,
            ),
        steps = steps,
        imageUrl = imageUrl,
        rating = rating,
    )
