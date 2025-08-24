package com.example.u_study.ui.screens.exams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.u_study.R
import com.example.u_study.data.database.entities.Exam
import com.example.u_study.ui.composables.AppBar
import com.example.u_study.ui.composables.NavigationBar
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun ExamsScreen(
    state: ExamsState,
    actions: ExamsActions,
    navController: NavHostController
) {
    var selectedExam by remember { mutableStateOf<Exam?>(null) }
    var showOptionsDialog by remember { mutableStateOf(false) } //modifica o eliminazione
    var showAddorEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AppBar(stringResource(R.string.examsScreen_name), navController) },
        bottomBar = { NavigationBar(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                selectedExam = null
                showAddorEditDialog = true }, ) {
                Icon(Icons.Filled.Add, contentDescription = "Aggiungi Esame")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ****** Esami Futuri ********
            item {
                Text(
                    stringResource(R.string.nextExams),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            if (state.upcomingExams.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.noExamsScheduled),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(state.upcomingExams, key = { it.id }) { exam ->
                    ExamItem(exam = exam, onClick = {
                        selectedExam = exam
                        showOptionsDialog = true
                    })
                }
            }

            // ******* Esami Passati *******
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Text(
                    stringResource(R.string.completedExams),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            if (state.completedExams.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.noExamsTakenYet),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(state.completedExams, key = { it.id }) { exam ->
                    ExamItem(exam = exam, onClick = {
                        selectedExam = exam
                        showOptionsDialog = true
                    })
                }
            }

            item {
                Spacer(modifier = Modifier.height(54.dp))
            }
        }
    }
    if (showAddorEditDialog) {
        AddExamDialog(
            initialExam = selectedExam, // Passa l'esame (o null)
            onDismiss = { showAddorEditDialog = false },
            onConfirm = { id, name, cfu, date, grade ->
                if (id == null) {
                    actions.addExam(name, cfu, date, grade)
                } else {
                    actions.updateExam(id, name, cfu, date, grade)
                }
                showAddorEditDialog = false
            }
        )
    }
    if (showOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showOptionsDialog = false },
            title = { Text(selectedExam?.name ?: "") },
            text = {
                Column {
                    Text(stringResource(R.string.chooseOption))
                    Spacer(Modifier.height(16.dp))

                    //modifica
                    TextButton(
                        onClick = {
                            showOptionsDialog = false
                            showAddorEditDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Edit, contentDescription = "Modifica")
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.edit))
                        }
                    }

                    //elimina
                    TextButton(
                        onClick = {
                            actions.deleteExam(selectedExam!!.id)
                            showOptionsDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Delete, contentDescription = "Elimina", tint = Color.Red)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            },


            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showOptionsDialog = false }) {
                    Text(stringResource(R.string.back))
                }
            }
        )
    }
}

@Composable
fun ExamItem(exam: Exam, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
                            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(exam.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("CFU: ${exam.cfu}", style = MaterialTheme.typography.bodyMedium)
            }

            if (exam.grade == null) {
                //voto null -> l'esame è Upcoming, mostro la data
                Text(
                    text = exam.date,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                //voto non null -> l'esame è Completed (mostro il voto)
                Text(
                    text = "${exam.grade}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExamDialog(
    onDismiss: () -> Unit,
    onConfirm: (id: Int?, name: String, cfu: Int, date: LocalDate, grade: Int?) -> Unit,
    initialExam: Exam? = null, //se è null = Aggiungi, altrimenti = Modifica
) {
    //i campi sono precompilati se siamo in modalità modifica
    var name by rememberSaveable { mutableStateOf(initialExam?.name ?: "") }
    var cfu by rememberSaveable { mutableStateOf(initialExam?.cfu?.toString() ?: "") }
    var date by rememberSaveable { mutableStateOf(initialExam?.date?.let { LocalDate.parse(it) }) }
    var grade by rememberSaveable { mutableStateOf(initialExam?.grade?.toString() ?: "") }

    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.back)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialExam == null) stringResource(R.string.addNewExam) else stringResource(R.string.modifyExam)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.nameExam)) }
                )
                OutlinedTextField(
                    value = cfu,
                    onValueChange = { newValue ->
                        val filteredValue = newValue.filter { it.isDigit() }

                        val numericValue = filteredValue.toIntOrNull()
                        if (numericValue == null || numericValue in 0..180) {
                            cfu = filteredValue
                        }
                    },
                    label = { Text(stringResource(R.string.cfu)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Box {
                    OutlinedTextField(
                        value = date?.toString() ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.dateExam)) },
                        trailingIcon = { Icon(Icons.Default.DateRange, "Select Date") }
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { showDatePicker = true })
                }

                if (date != null && !date!!.isAfter(LocalDate.now())) {
                    OutlinedTextField(
                        value = grade,
                        onValueChange = { newValue ->
                            val filteredValue = newValue.filter { it.isDigit() }

                            val numericValue = filteredValue.toIntOrNull()
                            if (numericValue == null || numericValue in 0..30) {
                                grade = filteredValue
                            }
                        },
                        label = { Text(stringResource(R.string.vote)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val cfuAsInt = cfu.toIntOrNull()
                    val gradeAsInt = grade.toIntOrNull()

                    if (cfuAsInt != null && date != null) {
                        onConfirm(initialExam?.id, name, cfuAsInt, date!!, gradeAsInt)
                    }
                },
                enabled = name.isNotBlank() && cfu.isNotBlank() && date != null
            ) {
                Text(stringResource(R.string.saveString))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.back)) }
        }
    )
}