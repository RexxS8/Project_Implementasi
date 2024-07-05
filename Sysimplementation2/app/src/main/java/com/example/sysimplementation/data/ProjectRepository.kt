package com.example.sysimplementation.data

class ProjectRepository(private val projectDao: ProjectDao) {

    val allProjects: List<ProjectEntity> = projectDao.getAllProjects()

    suspend fun insert(project: ProjectEntity) {
        projectDao.insert(project)
    }
}

