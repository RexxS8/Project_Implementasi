package com.example.sysimplementation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sysimplementation.data.ProjectEntity
import com.example.sysimplementation.data.ProjectViewModel
import com.example.sysimplementation.ui.theme.SysimplementationTheme
import java.util.Calendar

class DataEntry : ComponentActivity() {
    private val projectViewModel: ProjectViewModel by viewModels()
    private lateinit var filePickerLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
    private var selectedFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    selectedFileUri = it
                }
            }
        }

        setContent {
            SysimplementationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UpdateDataForm(
                        onUploadButtonClick = { openFilePicker() },
                        onSubmitButtonClick = { projectName, activity, startDate, endDate, status ->
                            val project = ProjectEntity(
                                projectName = projectName,
                                activity = activity,
                                startDate = startDate,
                                endDate = endDate,
                                status = status
                            )
                            projectViewModel.insert(project)
                            selectedFileUri?.let { uri ->
                                projectViewModel.uploadFile(uri, "File description")
                            }
                        },
                        onCameraButtonClick = { openCamera() }
                    )
                }
            }
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
        }
        filePickerLauncher.launch(intent)
    }

    private fun openCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }
}

@Composable
fun UpdateDataForm(
    onUploadButtonClick: () -> Unit,
    onSubmitButtonClick: (String, String, String, String, String) -> Unit,
    onCameraButtonClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    var projectName by remember { mutableStateOf(TextFieldValue()) }
    var activity by remember { mutableStateOf(TextFieldValue()) }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Active") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Update Data",
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = projectName,
            onValueChange = { projectName = it },
            label = { Text("Project Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = activity,
            onValueChange = { activity = it },
            label = { Text("Activity") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        DatePickerField(
            label = "Start Date",
            selectedDate = startDate,
            onDateSelected = { startDate = it },
            context = context
        )
        Spacer(modifier = Modifier.height(16.dp))
        DatePickerField(
            label = "End Date",
            selectedDate = endDate,
            onDateSelected = { endDate = it },
            context = context
        )
        Spacer(modifier = Modifier.height(16.dp))
        ScrollableDropdownMenuField(
            options = listOf("Berhasil", "Gagal", "Berlangsung"),
            label = "Status",
            selectedOption = status,
            onOptionSelected = { status = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onUploadButtonClick) {
            Text("Upload")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            onSubmitButtonClick(
                projectName.text,
                activity.text,
                startDate,
                endDate,
                status
            )
        }) {
            Text("Submit")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCameraButtonClick) {
            Text("Open Camera")
        }
    }
}

@Composable
fun DatePickerField(label: String, selectedDate: String, onDateSelected: (String) -> Unit, context: Context) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    var dateText by remember { mutableStateOf(selectedDate) }

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            dateText = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            onDateSelected(dateText)
        },
        year, month, day
    )

    OutlinedTextField(
        value = dateText,
        onValueChange = { },
        label = { Text(label) },
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.CalendarToday,
                contentDescription = null,
                modifier = Modifier.clickable {
                    datePickerDialog.show()
                }
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                datePickerDialog.show()
            }
    )
}

@Composable
fun ScrollableDropdownMenuField(
    options: List<String>,
    label: String,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedOption) }

    Column {
        OutlinedTextField(
            value = selectedText,
            onValueChange = { },
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = true }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    expanded = true
                }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedText = option
                                onOptionSelected(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateDataFormPreview() {
    SysimplementationTheme {
        UpdateDataForm(
            onUploadButtonClick = { /* No action for preview */ },
            onSubmitButtonClick = { _, _, _, _, _ -> /* No action for preview */ },
            onCameraButtonClick = { /* No action for preview */ }
        )
    }
}
