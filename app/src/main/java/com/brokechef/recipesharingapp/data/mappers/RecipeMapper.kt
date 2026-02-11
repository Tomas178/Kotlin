package com.brokechef.recipesharingapp.data.mappers

import com.brokechef.recipesharingapp.data.models.openapi.RecipesFindAll200ResponseInner
import com.brokechef.recipesharingapp.data.models.openapi.RecipesFindAll200ResponseInnerAuthor
import com.brokechef.recipesharingapp.data.models.openapi.RecipesSearch200ResponseInner

fun RecipesSearch200ResponseInner.toRecipeFindAll(): RecipesFindAll200ResponseInner =
    RecipesFindAll200ResponseInner(
        id = id,
        userId = userId,
        title = title,
        duration = duration,
        createdAt = createdAt,
        author =
            RecipesFindAll200ResponseInnerAuthor(
                name = author.name,
                image = author.image,
            ),
        steps = steps,
        imageUrl = imageUrl,
        rating = rating,
    )
