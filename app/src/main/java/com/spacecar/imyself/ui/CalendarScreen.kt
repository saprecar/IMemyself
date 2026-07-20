package com.spacecar.imyself.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spacecar.imyself.data.DailyLog
import com.spacecar.imyself.data.LogState
import com.spacecar.imyself.ui.theme.StatusGreen
import com.spacecar.imyself.ui.theme.StatusOrange
import com.spacecar.imyself.ui.theme.StatusRed
import com.spacecar.imyself.ui.theme.StatusYellow
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: TrackingViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onOpenRuleBook: () -> Unit,
    onOpenLogs: () -> Unit,
    onOpenReport: () -> Unit,
    onOpenAbout: () -> Unit
) {
    val logs by viewModel.logs.collectAsState()
    val activeMilestone by viewModel.activeMilestone.collectAsState()
    val currentStreakDays by viewModel.currentStreakDays.collectAsState()
    val timeLeft by viewModel.timeLeftString.collectAsState()
    val startTimeMillis by viewModel.startTimeMillis.collectAsState()
    val firstTrackedDateMillis by viewModel.firstTrackedDateMillis.collectAsState()
    val quote by viewModel.dailyQuote.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val activity = context as? Activity
        if (activity != null) {
            AppReviewManager.tryShowReviewPrompt(activity, firstTrackedDateMillis)
        }
    }

    var editingDate by remember { mutableStateOf<String?>(null) }
    var editingState by remember { mutableStateOf<LogState?>(null) }
    var showDetailedLogForDate by remember { mutableStateOf<String?>(null) }
    
    var showMotivationDialog by remember { mutableStateOf<Long?>(null) } // holds timestamp or retroactive date millis

    val targetDate = Instant.ofEpochMilli(startTimeMillis).atZone(ZoneId.systemDefault()).toLocalDate().plusDays(activeMilestone?.targetDay?.toLong() ?: 0L)

    val showTimePickerForDate = { dateStr: String ->
        val parsedDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val newCal = Calendar.getInstance()
                newCal.set(parsedDate.year, parsedDate.monthValue - 1, parsedDate.dayOfMonth, hourOfDay, minute, 0)
                viewModel.updateStartTime(newCal.timeInMillis)
                editingDate = null
                editingState = null
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("IMyself Tracker", fontWeight = FontWeight.ExtraBold) 
                },
                actions = {
                    IconButton(onClick = onOpenReport) {
                        Icon(Icons.Default.BarChart, contentDescription = "Insights Report")
                    }
                    IconButton(onClick = onOpenLogs) {
                        Icon(Icons.Default.MenuBook, contentDescription = "Personal Logs")
                    }
                    IconButton(onClick = onOpenRuleBook) {
                        Icon(Icons.Default.Info, contentDescription = "Rule Book")
                    }
                    IconButton(onClick = onOpenAbout) {
                        Icon(Icons.Default.Settings, contentDescription = "About & Legal")
                    }
                    IconButton(onClick = onThemeToggle) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "\"$quote\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp, start = 8.dp, end = 8.dp)
            )

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Time to Milestone ${activeMilestone?.targetDay ?: "-"}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Target Date: ${targetDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = timeLeft,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Current Streak: $currentStreakDays Days",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            MonthCalendar(
                logs = logs, 
                appStartMillis = firstTrackedDateMillis,
                targetDate = targetDate,
                onDayClick = { date, state ->
                    editingDate = date
                    editingState = state
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Button(
                    onClick = { showMotivationDialog = System.currentTimeMillis() },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusOrange),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Text("Fell (Reset Timer)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }

    if (editingDate != null) {
        val isFell = editingState == LogState.ORANGE || editingState == LogState.RED
        
        if (showDetailedLogForDate == null) {
            AlertDialog(
                onDismissRequest = { editingDate = null; editingState = null },
                title = { Text("Options for $editingDate") },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { showTimePickerForDate(editingDate!!) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Start Timer From Here")
                        }
                        
                        if (isFell) {
                            Button(
                                onClick = { 
                                    viewModel.deleteDailyLog(editingDate!!)
                                    editingDate = null; editingState = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Remove Fall")
                            }
                        } else {
                            Button(
                                onClick = { 
                                    val parsedDate = LocalDate.parse(editingDate!!, DateTimeFormatter.ISO_LOCAL_DATE)
                                    val millis = parsedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                    showMotivationDialog = millis
                                    editingDate = null; editingState = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = StatusOrange)
                            ) {
                                Text("Quick Mark Fall")
                            }
                            
                            Button(
                                onClick = { showDetailedLogForDate = editingDate },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = StatusOrange)
                            ) {
                                Text("Report Detailed Fall (Mood/Reason)")
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { editingDate = null; editingState = null }) { Text("Cancel") }
                }
            )
        } else {
            AddDetailedLogForDate(
                dateStr = showDetailedLogForDate!!,
                onDismiss = { 
                    showDetailedLogForDate = null
                    editingDate = null
                    editingState = null
                },
                onSave = { mood, note ->
                    val parsedDate = LocalDate.parse(showDetailedLogForDate!!, DateTimeFormatter.ISO_LOCAL_DATE)
                    val millis = parsedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    viewModel.addPersonalLog(mood, note, isFell = true, timestamp = millis)
                    viewModel.checkInRelapse(millis)
                    showDetailedLogForDate = null
                    editingDate = null
                    editingState = null
                }
            )
        }
    }

    if (showMotivationDialog != null) {
        RelapseMotivationDialog(
            streakDays = currentStreakDays,
            onDismiss = { showMotivationDialog = null },
            onConfirm = { 
                viewModel.checkInRelapse(showMotivationDialog!!)
                showMotivationDialog = null
            }
        )
    }
}

@Composable
fun RelapseMotivationDialog(streakDays: Int, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    val quotes = listOf(
        "Falling is not failure; stopping is. Every relapse is a data point, not a destination. The mountain doesn't care how many times you stumbled—it only cares that you're still climbing. Success is the sum of all the attempts you didn't quit on.",
        "You didn't fall backward; you fell forward into wisdom. The days you maintained matter more than the day you didn't. Keep your eyes on the actual summit, not on yesterday's slip. The only real failure is the one you stop trying to overcome.",
        "Count not from your lowest point, but from your resolve to climb again. Every streak broken is a lesson earned. The mountain rewards persistence, not perfection. You are still worthy of the peak.",
        "Failure is easy—it requires nothing but surrender. Success is hard—it requires one more attempt after every fall. You fell? Good. Now you know the slope. The mountain is still waiting. Keep climbing.",
        "Yesterday you stumbled. Today you continue. This is the entire story of success. The days you held strong matter. The day you didn't is just data—not destiny. The actual target date is still ahead. You are still capable. The mountain is still possible.",
        "You didn't quit. That's what matters. Compare yourself to last time—you're learning the mountain. Mark the calendar, track the days, but remember: success is built from failures you refused to stop at. One more attempt. That's all it takes to change the outcome."
    )
    val selectedQuote = remember { quotes.random() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hi, you fell. Don't worry!", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Compared to last time, you were able to continue for $streakDays days. Let's aim for the actual target date.",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = "\"$selectedQuote\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = StatusOrange)) {
                Text("Log Fall & Continue Climbing", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun DayCell(date: String, state: LogState?, isTarget: Boolean, isFuture: Boolean, isStartOrAfter: Boolean, isToday: Boolean, isClickable: Boolean, onClick: () -> Unit) {
    val color = when {
        state == LogState.ORANGE -> StatusOrange
        state == LogState.RED -> StatusRed
        !isFuture && isStartOrAfter -> StatusGreen
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }
    
    val dayNum = date.takeLast(2)

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .clickable(enabled = isClickable, onClick = onClick)
            .then(
                if (isToday) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isTarget) {
            Text("🎯", fontSize = 24.sp, modifier = Modifier.offset(y = (-4).dp).alpha(0.3f))
        }
        Text(
            text = dayNum.toInt().toString(), // Remove leading zero
            color = if (state == null && !(!isFuture && isStartOrAfter)) MaterialTheme.colorScheme.onSurface else Color.White,
            fontWeight = if (isToday || state != null || (!isFuture && isStartOrAfter)) FontWeight.ExtraBold else FontWeight.Medium
        )
    }
}

@Composable
fun MonthCalendar(logs: List<DailyLog>, appStartMillis: Long, targetDate: LocalDate, onDayClick: (String, LogState?) -> Unit) {
    var displayedMonth by remember { mutableStateOf(YearMonth.now()) }
    val daysInMonth = displayedMonth.lengthOfMonth()
    val firstDayOfWeek = displayedMonth.atDay(1).dayOfWeek.value 
    val offset = if (firstDayOfWeek == 7) 0 else firstDayOfWeek
    val totalCells = offset + daysInMonth
    val today = LocalDate.now()
    val appStartDate = Instant.ofEpochMilli(appStartMillis).atZone(ZoneId.systemDefault()).toLocalDate()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { displayedMonth = displayedMonth.minusMonths(1) }) {
                Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Text(
                text = "${displayedMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${displayedMonth.year}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { displayedMonth = displayedMonth.plusMonths(1) }) {
                Text(">", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(text = day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(totalCells) { index ->
                if (index < offset) {
                    Box(modifier = Modifier.aspectRatio(1f))
                } else {
                    val dayNum = index - offset + 1
                    val date = displayedMonth.atDay(dayNum)
                    val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    val log = logs.find { it.date == dateStr }
                    val isFuture = date.isAfter(today)
                    val isStartOrAfter = !date.isBefore(appStartDate) && !isFuture
                    val isClickable = !isFuture // allow clicking any past date to retroactively track

                    DayCell(
                        date = dateStr,
                        state = log?.state,
                        isTarget = date == targetDate,
                        isFuture = isFuture,
                        isStartOrAfter = isStartOrAfter,
                        isToday = date == today,
                        isClickable = isClickable,
                        onClick = { onDayClick(dateStr, log?.state) }
                    )
                }
            }
        }
    }
}

@Composable
fun AddDetailedLogForDate(dateStr: String, onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var note by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("😔") }
    
    val moods = listOf("😁", "😊", "😐", "😔", "😠", "😭")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Fall for $dateStr") },
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
                                .clip(androidx.compose.foundation.shape.CircleShape)
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
                    label = { Text("Reason for falling?") },
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
                Text("Save Log & Update Calendar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
