package com.ghostwan.sample.geofencing.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ghostwan.sample.geofencing.data.Source
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@Entity
@IgnoreExtraProperties
data class Event(
    val isHome: Boolean,
    val source: Source,
    val date: Date = Date(),
    @PrimaryKey(autoGenerate = true) val id: Long? = null
) {
    @Exclude
    fun toMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            "id" to id,
            "source" to source,
            "date" to date.toString(),
            "type" to if (isHome) "home" else "left"
        )
    }
}
