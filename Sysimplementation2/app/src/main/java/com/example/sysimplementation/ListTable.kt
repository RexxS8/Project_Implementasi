package com.example.sysimplementation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sysimplementation.ui.theme.SysimplementationTheme

data class Project(
    val name: String,
    val activity: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val file: String
)

@Composable
fun ProjectTable(projects: List<Project>) {
    val searchQuery = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Data Implementation System",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search Project") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
                .background(Color.LightGray)
                .border(1.dp, Color.Gray),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Project Name", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f).padding(2.dp), fontSize = 15.sp)
            Text("Activity", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f).padding(2.dp), fontSize = 15.sp)
            Text("Start Date", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f).padding(2.dp), fontSize = 15.sp)
            Text("End Date", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f).padding(2.dp), fontSize = 15.sp)
            Text("Status", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f).padding(2.dp), fontSize = 15.sp)
            Text("File", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f).padding(2.dp), fontSize = 15.sp)
        }

        LazyColumn {
            items(projects.filter { it.name.contains(searchQuery.value, true) }) { project ->
                ProjectRow(project)
                Spacer(modifier = Modifier.height(1.dp).background(Color.Gray))
            }
        }
    }
}

@Composable
fun ProjectRow(project: Project) {
    val expanded = remember { mutableStateOf(false) }
    val options = listOf("Details", "Update") // Add more options as needed
    val selectedText = remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Color.LightGray)
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(project.name, modifier = Modifier.weight(1f).padding(8.dp), maxLines = 1)
        Text(project.activity, modifier = Modifier.weight(1f).padding(8.dp), maxLines = 1)
        Text(project.startDate, modifier = Modifier.weight(1f).padding(8.dp), maxLines = 1)
        Text(project.endDate, modifier = Modifier.weight(1f).padding(8.dp), maxLines = 1)
        Text(project.status, modifier = Modifier.weight(1f).padding(8.dp), maxLines = 1)
        Text(project.file, modifier = Modifier.weight(1f).padding(8.dp), maxLines = 1)

        IconButton(onClick = { expanded.value = true }) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More Options")
        }

        MyDropdownMenu(
            expandedState = expanded,
            options = options,
            selectedText = selectedText,
            onOptionSelected = { selectedOption ->
                // Handle option selection
                println("Selected option: $selectedOption")
            }
        )
    }
}


@Composable
fun MyDropdownMenu(
    expandedState: MutableState<Boolean>,
    options: List<String>,
    selectedText: MutableState<String>,
    onOptionSelected: (String) -> Unit
) {
    DropdownMenu(
        expanded = expandedState.value,
        onDismissRequest = { expandedState.value = false }
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
                            selectedText.value = option
                            onOptionSelected(option)
                            expandedState.value = false
                        }
                    )
                }
            }
        }
    }
}

class ListTable : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SysimplementationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val projects = listOf(
                        Project("Duplicate Cleaning", "Menfilter data image agr..", "24/09/2023", "24/09/2023", "Selesai", "duplicate-clean.png"),
                        Project("House Price", "Memprediksi harga dar..", "02/10/2023", "02/10/2023", "Gagal", "house-price.png"),
                        // Add more projects here
                    )
                    ProjectTable(projects)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SysimplementationTheme {
        val projects = listOf(
            Project("Dupl..", "Men..", "24/..", "24/..", "Done", "dupl.."),
            Project("Hou..", "Me..", "02/..", "02/..", "Fail..", "hou.."),
            // Add more projects here
        )
        ProjectTable(projects)
    }
}
