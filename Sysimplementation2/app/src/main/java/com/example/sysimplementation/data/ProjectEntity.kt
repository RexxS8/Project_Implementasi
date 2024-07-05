package com.example.sysimplementation.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project_table")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectName: String,
    val activity: String,
    val startDate: String,
    val endDate: String,
    val status: String
)

