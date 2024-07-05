package com.example.sysimplementation.data

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sysimplementation.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

class ProjectViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProjectRepository
    val allProjects: List<ProjectEntity>

    init {
        val projectDao = ProjectDatabase.getDatabase(application).ProjectDao()
        repository = ProjectRepository(projectDao)
        allProjects = repository.allProjects
    }

    fun insert(project: ProjectEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(project)
    }

    fun uploadFile(uri: Uri, description: String) {
        viewModelScope.launch {
            try {
                val file = File(uri.path!!)
                val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)
                val descriptionPart =
                    description.toRequestBody("multipart/form-data".toMediaTypeOrNull())

                withContext(Dispatchers.IO) {
                    val response = RetrofitClient.instance.uploadFile(filePart, descriptionPart).execute()
                    if (response.isSuccessful) {
                        // Handle successful upload
                    } else {
                        // Handle upload failure
                    }
                }
            } catch (e: IOException) {
                // Handle exceptions
            }
        }
    }
}

