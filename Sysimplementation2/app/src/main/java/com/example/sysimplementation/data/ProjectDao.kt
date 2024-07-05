package com.example.sysimplementation.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProjectDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(project: ProjectEntity)

    @Query("SELECT * FROM project_table ORDER BY id ASC")
    fun getAllProjects(): List<ProjectEntity>
}

