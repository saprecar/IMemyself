package com.spacecar.imyself.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spacecar.imyself.data.PersonalLog
import com.spacecar.imyself.ui.theme.StatusGreen
import com.spacecar.imyself.ui.theme.StatusOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalLogScreen(
    viewModel: TrackingViewModel,
    onBack: () -> Unit
) {
    val logs by viewModel.personalLogs.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingLog by remember { mutableStateOf<PersonalLog?>(null) }

    val successLogs = logs.filter { !it.isFellLog }
    val fellLogs = logs.filter { it.isFellLog }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personal Journal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Log")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Success Window", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Fell Window", fontWeight = FontWeight.Bold) }
                )
            }

            val displayedLogs = if (selectedTabIndex == 0) successLogs else fellLogs
            val themeColor = if (selectedTabIndex == 0) StatusGreen else StatusOrange

            if (displayedLogs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No logs in this window yet.",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayedLogs) { log ->
                        LogCard(log, themeColor, onClick = { editingLog = log })
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddLogDialog(
            isFell = selectedTabIndex == 1,
            onDismiss = { showAddDialog = false },
            onSave = { mood, note ->
                viewModel.addPersonalLog(mood, note, isFell = selectedTabIndex == 1)
                showAddDialog = false
            }
        )
    }

    editingLog?.let { log ->
        EditLogDialog(
            log = log,
            onDismiss = { editingLog = null },
            onSave = { mood, note ->
                viewModel.updatePersonalLog(log.copy(moodEmoji = mood, note = note))
                editingLog = null
            },
            onDelete = {
                viewModel.deletePersonalLog(log)
                editingLog = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogCard(log: PersonalLog, stripeColor: Color, onClick: () -> Unit) {
    val formatter = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    val dateString = formatter.format(Date(log.timestamp))

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(stripeColor)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Text(
                        text = log.moodEmoji,
                        fontSize = 24.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = log.note,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AddLogDialog(isFell: Boolean, onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var note by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("😊") }
    
    val moods = listOf("😁", "😊", "😐", "😔", "😠", "😭")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isFell) "Add to Fell Window" else "Add to Success Window")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("How are you feeling?", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    moods.forEach { emoji ->
                        val isSelected = selectedMood == emoji
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable { selectedMood = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 24.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Journal Note") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(selectedMood, note) },
                enabled = note.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditLogDialog(
    log: PersonalLog,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
    onDelete: () -> Unit
) {
    var note by remember { mutableStateOf(log.note) }
    var selectedMood by remember { mutableStateOf(log.moodEmoji) }
    
    val moods = listOf("😁", "😊", "😐", "😔", "😠", "😭")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Journal Log") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("How are you feeling?", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    moods.forEach { emoji ->
                        val isSelected = selectedMood == emoji
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable { selectedMood = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 24.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Reason or Note") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(selectedMood, note) }, enabled = note.isNotBlank()) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDelete, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Text("Delete")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
