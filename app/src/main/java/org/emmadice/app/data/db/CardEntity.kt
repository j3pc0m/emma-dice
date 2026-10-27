package org.emmadice.app.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    indices = [Index(value = ["stableKey"], unique = true)]
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val stableKey: String,
    val title: String,
    val position: Int,
    val enabled: Boolean = true,
    val imagePath: String? = null,
    val audioPath: String? = null
)
