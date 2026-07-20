package com.spacecar.imyself.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.spacecar.imyself.data.AppDatabase
import com.spacecar.imyself.data.DailyLog
import com.spacecar.imyself.data.LogState
import com.spacecar.imyself.data.PersonalLog
import com.spacecar.imyself.data.StreakMilestone
import com.spacecar.imyself.data.TrackingRepository
import com.spacecar.imyself.logic.FibonacciProgressor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TrackingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TrackingRepository
    private val sharedPrefs = application.getSharedPreferences("tracking_prefs", Context.MODE_PRIVATE)
    
    private val _logs = MutableStateFlow<List<DailyLog>>(emptyList())
    val logs: StateFlow<List<DailyLog>> = _logs.asStateFlow()

    private val _activeMilestone = MutableStateFlow<StreakMilestone?>(null)
    val activeMilestone: StateFlow<StreakMilestone?> = _activeMilestone.asStateFlow()

    private val _currentStreakDays = MutableStateFlow(0)
    val currentStreakDays: StateFlow<Int> = _currentStreakDays.asStateFlow()

    private val _timeLeftString = MutableStateFlow("00:00:00:00")
    val timeLeftString: StateFlow<String> = _timeLeftString.asStateFlow()

    private val _startTimeMillis = MutableStateFlow(sharedPrefs.getLong("start_time_millis", System.currentTimeMillis()))
    val startTimeMillis: StateFlow<Long> = _startTimeMillis.asStateFlow()

    private val _firstTrackedDateMillis = MutableStateFlow(
        sharedPrefs.getLong("first_tracked_date", sharedPrefs.getLong("start_time_millis", System.currentTimeMillis()))
    )
    val firstTrackedDateMillis: StateFlow<Long> = _firstTrackedDateMillis.asStateFlow()

    private val _personalLogs = MutableStateFlow<List<PersonalLog>>(emptyList())
    val personalLogs: StateFlow<List<PersonalLog>> = _personalLogs.asStateFlow()

    private val quotes = listOf(
        "Small daily improvements are the key to staggering long-term results.",
        "You don't have to be perfect to be amazing.",
        "Strive for progress, not perfection.",
        "Success is the sum of small efforts repeated day in and day out.",
        "Focus on the step in front of you, not the whole staircase.",
        "Your only limit is you.",
        "Don't count the days, make the days count.",
        "Until you make the unconscious conscious, it will direct your life and you will call it fate.",
        "Pride goes before a fall, but healthy self-respect is the foundation of growth.",
        "Shame thrives in secrecy, but pride grounded in real accomplishment thrives in light. — Brené Brown",
        "I am not what I achieve; I am what I consistently do.",
        "Greed is the desire to have more than you need; generosity is the satisfaction of having enough to share.",
        "It is not the man who has too little, but the man who craves more, that is poor. — Seneca",
        "Sloth is avoidance hiding as rest; wisdom is rest that recharges.",
        "Rest is not laziness. Rest is what allows you to show up. — Tricia Hersey",
        "Flow requires cycles of effort and recovery. Skipping recovery doesn't create more work; it destroys the capacity for deep work. — Mihaly Csikszentmihalyi",
        "Rest is not surrender; it is the refill of the well from which all action flows.",
        "Wrath is the desire for justice perverted; righteous anger is justice channeled.",
        "All anger is a signal that a need is not being met. Anger is not the problem—what we do with it is. — Marshall Rosenberg",
        "You're not supposed to be blind when you can see, deaf when you can hear, and silent when you have a voice. — Malcolm X",
        "Anger is energy; use it to build walls or burn bridges—choose wisely.",
        "Gluttony seeks quantity to fill the void; pleasure seeks quality to savor the moment.",
        "Eat food. Not too much. Mostly plants. — Michael Pollan",
        "Mindfulness amplifies pleasure while reducing quantity needed.",
        "One excellent meal, fully tasted, nourishes more than a feast consumed in haste.",
        "Envy is pain at another's success; aspiration is inspiration from it.",
        "Benign envy drives growth, while malicious envy drives harm.",
        "Be inspired by people ahead of you, not threatened by them. Their success proves the path is possible. — James Clear",
        "Their win doesn't dim my light; it shows me where the light is.",
        "Lust seeks the new; love seeks the deep. Both require passion—the difference is direction.",
        "Lust is the beginning of romantic love; the challenge is whether it can mature into something more sustaining. — Alain de Botton",
        "The opposite of addiction is connection. — Johann Hari",
        "Depth over novelty; presence over possession.",
        "The highest form of wealth is the ability to wake up whenever you want. — Morgan Housel"
    )
    private val _dailyQuote = MutableStateFlow(quotes.random())
    val dailyQuote: StateFlow<String> = _dailyQuote.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TrackingRepository(database.trackingDao())
        
        // Save initial start time if not present
        if (!sharedPrefs.contains("start_time_millis")) {
            sharedPrefs.edit().putLong("start_time_millis", _startTimeMillis.value).apply()
        }

        if (!sharedPrefs.contains("first_tracked_date")) {
            sharedPrefs.edit().putLong("first_tracked_date", _firstTrackedDateMillis.value).apply()
        }
        
        viewModelScope.launch {
            recalculateFirstTrackedDate()
        }
        
        viewModelScope.launch {
            repository.allLogs.collectLatest { logList ->
                _logs.value = logList
            }
        }
        
        viewModelScope.launch {
            repository.allPersonalLogs.collectLatest { pLogs ->
                _personalLogs.value = pLogs
            }
        }
        
        viewModelScope.launch {
            initializeMilestone()
            startTimerTicker()
        }
    }

    private suspend fun initializeMilestone() {
        var milestone = repository.getActiveMilestone()
        if (milestone == null) {
            milestone = StreakMilestone(targetDay = FibonacciProgressor.getInitialMilestone(), isLocked = false)
            repository.insertMilestone(milestone)
        }
        _activeMilestone.value = milestone
    }

    private fun startTimerTicker() {
        viewModelScope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                val start = _startTimeMillis.value
                val elapsedMillis = now - start
                
                // Update streak in days (just for display)
                val daysElapsed = (elapsedMillis / (1000 * 60 * 60 * 24)).toInt()
                _currentStreakDays.value = daysElapsed

                val active = _activeMilestone.value
                if (active != null) {
                    val targetMillis = active.targetDay * 24L * 60L * 60L * 1000L
                    val leftMillis = targetMillis - elapsedMillis
                    
                    if (leftMillis <= 0) {
                        // We hit the milestone automatically!
                        handleMilestoneAchieved(active)
                    } else {
                        val d = leftMillis / (24 * 3600 * 1000)
                        val h = (leftMillis / (3600 * 1000)) % 24
                        val m = (leftMillis / (60 * 1000)) % 60
                        val s = (leftMillis / 1000) % 60
                        _timeLeftString.value = String.format("%02d:%02d:%02d:%02d", d, h, m, s)
                    }
                }
                
                delay(1000) // tick every second
            }
        }
    }

    private suspend fun handleMilestoneAchieved(active: StreakMilestone) {
        val todayStr = LocalDate.now().format(dateFormatter)
        repository.insertMilestone(active.copy(isLocked = true, dateReached = todayStr))
        
        val nextTarget = FibonacciProgressor.getNextMilestone(active.targetDay)
        val newMilestone = StreakMilestone(targetDay = nextTarget, isLocked = false)
        repository.insertMilestone(newMilestone)
        _activeMilestone.value = newMilestone
        
        // The ticker will naturally recalculate time left for the next target on the next loop
    }

    fun updateStartTime(newTimeMillis: Long) {
        _startTimeMillis.value = newTimeMillis
        sharedPrefs.edit().putLong("start_time_millis", newTimeMillis).apply()
        viewModelScope.launch {
            recalculateFirstTrackedDate()
        }
    }

    private suspend fun determineFallColor(dateStr: String): LogState {
        val existingLog = repository.getLogByDate(dateStr)
        return if (existingLog?.state == LogState.ORANGE || existingLog?.state == LogState.RED) LogState.RED else LogState.ORANGE
    }

    private suspend fun syncTimerWithLatestFall() {
        val latestFall = repository.getLatestFellLog()
        if (latestFall != null) {
            val date = LocalDate.parse(latestFall.date, dateFormatter)
            val timeMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            updateStartTime(timeMillis)
        }
    }

    private suspend fun recalculateFirstTrackedDate() {
        val oldestLog = repository.getOldestLog()
        val timerStart = _startTimeMillis.value
        val actualStart = if (oldestLog != null) {
            val oldestMillis = LocalDate.parse(oldestLog.date, dateFormatter).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            minOf(oldestMillis, timerStart)
        } else {
            timerStart
        }
        _firstTrackedDateMillis.value = actualStart
        sharedPrefs.edit().putLong("first_tracked_date", actualStart).apply()
    }

    fun checkInRelapse(timeMillis: Long) {
        viewModelScope.launch {
            val dateStr = Instant.ofEpochMilli(timeMillis).atZone(ZoneId.systemDefault()).format(dateFormatter)
            val state = determineFallColor(dateStr)
            repository.insertLog(DailyLog(date = dateStr, state = state))
            syncTimerWithLatestFall()
            recalculateFirstTrackedDate()
        }
    }

    fun deleteDailyLog(dateStr: String) {
        viewModelScope.launch {
            repository.deleteLogByDate(dateStr)
            syncTimerWithLatestFall()
            recalculateFirstTrackedDate()
        }
    }

    fun addRetroactiveFall(dateStr: String) {
        viewModelScope.launch {
            val state = determineFallColor(dateStr)
            repository.insertLog(DailyLog(date = dateStr, state = state))
            syncTimerWithLatestFall()
            recalculateFirstTrackedDate()
        }
    }

    fun addPersonalLog(mood: String, note: String, isFell: Boolean, timestamp: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            repository.insertPersonalLog(
                PersonalLog(
                    timestamp = timestamp,
                    moodEmoji = mood,
                    note = note,
                    isFellLog = isFell
                )
            )
        }
    }

    fun updatePersonalLog(log: PersonalLog) {
        viewModelScope.launch {
            repository.updatePersonalLog(log)
        }
    }

    fun deletePersonalLog(log: PersonalLog) {
        viewModelScope.launch {
            repository.deletePersonalLog(log)
        }
    }
}
