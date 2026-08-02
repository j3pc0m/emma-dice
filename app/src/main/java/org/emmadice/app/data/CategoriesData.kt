package org.emmadice.app.data

import org.emmadice.app.design.CategoryEmotions
import org.emmadice.app.design.CategoryFood
import org.emmadice.app.design.CategoryNeeds
import org.emmadice.app.design.CategoryPeople
import org.emmadice.app.model.Category

object CategoriesData {

    val categories = listOf(

        Category(
            id = 1,
            name = "Personas",
            color = CategoryPeople
        ),

        Category(
            id = 2,
            name = "Necesidades",
            color = CategoryNeeds
        ),

        Category(
            id = 3,
            name = "Comida",
            color = CategoryFood
        ),

        Category(
            id = 4,
            name = "Emociones",
            color = CategoryEmotions
        )
    )
}