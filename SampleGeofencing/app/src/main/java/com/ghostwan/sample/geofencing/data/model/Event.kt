package com.ghostwan.sample.geofencing.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ghostwan.sample.geofencing.data.Source
import java.util.*

@Entity
data class Event (
    val isHome:Boolean,
    val source: Source,
    val date: Date = Date(),
    @PrimaryKey(autoGenerate = true) val id:Long?=null
)
