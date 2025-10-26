package ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.TaskStatus
import model.TaskType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    initialStatus: List<TaskStatus>,
    initialTypes: List<TaskType>,
    onApply: (List<TaskStatus>, List<TaskType>) -> Unit,
    onReset: () -> Unit,
    onClose: () -> Unit
) {
    val selStatus = remember { mutableStateListOf<TaskStatus>().apply { addAll(initialStatus) } }
    val selTypes  = remember { mutableStateListOf<TaskType>().apply { addAll(initialTypes) } }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Filters") },
                navigationIcon = { TextButton(onClick = onClose) { Text("Close") } },
                actions = {
                    TextButton(
                        onClick = {
                            selStatus.clear()
                            selTypes.clear()
                            onReset()
                        }
                    ) { Text("Reset") }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Status", style = MaterialTheme.typography.titleMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val statuses = listOf(TaskStatus.A_FAIRE, TaskStatus.EN_COURS, TaskStatus.TERMINEE)
                        items(statuses) { s ->
                            FilterChip(
                                selected = s in selStatus,
                                onClick = {
                                    if (s in selStatus) selStatus.remove(s) else selStatus.add(s)
                                },
                                label = {
                                    Text(
                                        when (s) {
                                            TaskStatus.A_FAIRE -> "To do"
                                            TaskStatus.EN_COURS -> "In progress"
                                            TaskStatus.TERMINEE -> "Done"
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }

            OutlinedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Type", style = MaterialTheme.typography.titleMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val types = listOf(TaskType.PERSONNEL, TaskType.TRAVAIL, TaskType.ETUDE, TaskType.AUTRE)
                        items(types) { t ->
                            FilterChip(
                                selected = t in selTypes,
                                onClick = {
                                    if (t in selTypes) selTypes.remove(t) else selTypes.add(t)
                                },
                                label = {
                                    Text(
                                        when (t) {
                                            TaskType.PERSONNEL -> "Personal"
                                            TaskType.TRAVAIL -> "Work"
                                            TaskType.ETUDE -> "Study"
                                            TaskType.AUTRE -> "Other"
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onApply(selStatus.toList(), selTypes.toList()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) { Text("Apply filters") }
        }
    }
}