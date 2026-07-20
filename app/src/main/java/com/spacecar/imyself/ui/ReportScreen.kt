package com.spacecar.imyself.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spacecar.imyself.data.PersonalLog
import com.spacecar.imyself.ui.theme.StatusGreen
import com.spacecar.imyself.ui.theme.StatusOrange
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(viewModel: TrackingViewModel, onBack: () -> Unit) {
    val logs by viewModel.personalLogs.collectAsState()
    val dailyLogs by viewModel.logs.collectAsState()
    val firstTrackedDateMillis by viewModel.firstTrackedDateMillis.collectAsState()
    
    val fellLogs = logs.filter { it.isFellLog }
    
    val fellCount = dailyLogs.size
    val startDate = Instant.ofEpochMilli(firstTrackedDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    val today = LocalDate.now()
    val totalDays = ChronoUnit.DAYS.between(startDate, today).toInt() + 1
    val successCount = maxOf(0, totalDays - fellCount)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insights & Analytics", fontWeight = FontWeight.Bold) },
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
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Medical & Data Disclaimers
            DisclaimersSection()

            // Overview Cards
            OverviewSection(successCount = successCount, fellCount = fellCount)

            if (fellLogs.isNotEmpty()) {
                // Recent Fall Details
                RecentFallSection(fellLogs)

                // Pattern Analysis Text
                PatternAnalysisSection(fellLogs)

                // Custom Bar Chart
                DayOfWeekChart(fellLogs)
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Not enough detailed journal data to find patterns.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "You have ${fellCount} falls on your calendar, but to unlock Deep Pattern Analysis (Time of Day, Mood, and Triggers), you need to log details!\n\nNext time you slip, tap the day on the calendar and select 'Report Detailed Fall'.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DisclaimersSection() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = "Warning", tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Not a Medical Report",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = "This report is for experimental habit tracking only and is not a professional diagnosis.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    lineHeight = 18.sp
                )
            }
        }
    }
    
    Text(
        text = "💡 For accurate pattern detection, this tool works best with at least 30 days of consistent log data.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun OverviewSection(successCount: Int, fellCount: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        StatCard(
            title = "Continue",
            value = successCount.toString(),
            color = StatusGreen,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Fell",
            value = fellCount.toString(),
            color = StatusOrange,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            Text(text = value, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}

@Composable
fun RecentFallSection(fellLogs: List<PersonalLog>) {
    val mostRecentFall = fellLogs.maxByOrNull { it.timestamp } ?: return
    val calendar = Calendar.getInstance()
    
    calendar.timeInMillis = mostRecentFall.timestamp
    val recentYear = calendar.get(Calendar.YEAR)
    val recentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

    val fallsThatDay = fellLogs.count { log ->
        calendar.timeInMillis = log.timestamp
        calendar.get(Calendar.YEAR) == recentYear && calendar.get(Calendar.DAY_OF_YEAR) == recentDayOfYear
    }

    val formatter = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    val dateString = formatter.format(Date(mostRecentFall.timestamp))

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Last Relapse Summary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(12.dp))
            Text("• **Time:** $dateString", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text("• **Slips on this day:** $fallsThatDay", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Your Reason:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
            Text(
                text = mostRecentFall.note.ifBlank { "No reason logged." },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            )
        }
    }
}

@Composable
fun PatternAnalysisSection(fellLogs: List<PersonalLog>) {
    val calendar = Calendar.getInstance()
    var morning = 0; var afternoon = 0; var evening = 0; var night = 0
    val moodCounts = mutableMapOf<String, Int>()

    fellLogs.forEach { log ->
        calendar.timeInMillis = log.timestamp
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 6..11 -> morning++
            in 12..17 -> afternoon++
            in 18..23 -> evening++
            else -> night++
        }
        
        moodCounts[log.moodEmoji] = moodCounts.getOrDefault(log.moodEmoji, 0) + 1
    }

    val mostCommonTime = listOf(
        "Mornings" to morning, "Afternoons" to afternoon, 
        "Evenings" to evening, "Nights" to night
    ).maxByOrNull { it.second }?.first ?: "Unknown"

    val topMood = moodCounts.maxByOrNull { it.value }?.key ?: "Unknown"

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Your Vulnerabilities", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(12.dp))
            Text("• You are most likely to relapse during the **$mostCommonTime**.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text("• The most frequent mood logged during a slip is **$topMood**.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun DayOfWeekChart(fellLogs: List<PersonalLog>) {
    val dayCounts = IntArray(7) { 0 }
    val calendar = Calendar.getInstance()
    fellLogs.forEach { log ->
        calendar.timeInMillis = log.timestamp
        val dayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1
        dayCounts[dayIndex]++
    }
    
    val maxCount = dayCounts.maxOrNull()?.coerceAtLeast(1) ?: 1
    val dayLabels = listOf("S", "M", "T", "W", "T", "F", "S")

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Falls by Day of Week", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                for (i in 0..6) {
                    val count = dayCounts[i]
                    val fraction = count.toFloat() / maxCount.toFloat()
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = if(count > 0) count.toString() else "", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .fillMaxHeight(fraction.coerceAtLeast(0.05f))
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(StatusOrange)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = dayLabels[i], fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
