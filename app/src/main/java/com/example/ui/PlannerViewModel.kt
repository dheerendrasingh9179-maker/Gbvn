package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DailyNoteEntity
import com.example.data.FocusSessionEntity
import com.example.data.HabitEntity
import com.example.data.PlannerDatabase
import com.example.data.PlannerRepository
import com.example.data.TaskEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PlannerViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PlannerDatabase.getDatabase(application)
    private val repository: PlannerRepository = PlannerRepository(db.plannerDao())
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val todayDate: String = dateFormatter.format(Date())

    private val _selectedDate = MutableStateFlow(todayDate)
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Tasks for selected date
    val tasksForSelectedDate: StateFlow<List<TaskEntity>> = _selectedDate
        .flatMapLatest { date -> repository.getTasksForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All tasks
    val allTasks: StateFlow<List<TaskEntity>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Habits
    val habits: StateFlow<List<HabitEntity>> = repository.getAllHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Focus sessions for selected date
    val focusSessionsForSelectedDate: StateFlow<List<FocusSessionEntity>> = _selectedDate
        .flatMapLatest { date -> repository.getFocusSessionsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Daily Note for selected date
    val dailyNoteForSelectedDate: StateFlow<DailyNoteEntity?> = _selectedDate
        .flatMapLatest { date -> repository.getDailyNoteForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Focus Timer State
    private val _timerDurationMinutes = MutableStateFlow(25)
    val timerDurationMinutes: StateFlow<Int> = _timerDurationMinutes.asStateFlow()

    private val _timerSecondsRemaining = MutableStateFlow(25 * 60)
    val timerSecondsRemaining: StateFlow<Int> = _timerSecondsRemaining.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Populate sample data if empty
        viewModelScope.launch {
            val existingTasks = repository.getAllTasks().first()
            if (existingTasks.isEmpty()) {
                seedInitialData()
            }
        }
    }

    private suspend fun seedInitialData() {
        val today = todayDate

        val sampleTasks = listOf(
            TaskEntity(
                title = "Review Q3 Strategy & Goals",
                description = "Outline key milestones and coordinate with team members.",
                category = "Work",
                priority = "High",
                dueDate = today,
                dueTime = "10:00",
                isCompleted = false
            ),
            TaskEntity(
                title = "Morning Cardio Walk",
                description = "Target 30 minutes light exercise in the park.",
                category = "Health",
                priority = "Medium",
                dueDate = today,
                dueTime = "07:30",
                isCompleted = true
            ),
            TaskEntity(
                title = "Read Chapter 4 of Mobile Architecture",
                description = "Take structured notes on Jetpack Compose state management.",
                category = "Study",
                priority = "Medium",
                dueDate = today,
                dueTime = "16:00",
                isCompleted = false
            ),
            TaskEntity(
                title = "Grocery Shopping for the Week",
                description = "Fresh vegetables, fruits, and snacks.",
                category = "Home",
                priority = "Low",
                dueDate = today,
                dueTime = "18:30",
                isCompleted = false
            )
        )

        sampleTasks.forEach { repository.insertTask(it) }

        val sampleHabits = listOf(
            HabitEntity(
                title = "Drink 2.5L Water",
                category = "Health",
                targetFrequency = "Daily",
                streakCount = 5,
                lastCompletedDate = today
            ),
            HabitEntity(
                title = "20 Mins Meditation",
                category = "Health",
                targetFrequency = "Daily",
                streakCount = 3,
                lastCompletedDate = ""
            ),
            HabitEntity(
                title = "Read 15 Pages",
                category = "Study",
                targetFrequency = "Daily",
                streakCount = 7,
                lastCompletedDate = today
            )
        )

        sampleHabits.forEach { repository.insertHabit(it) }

        repository.insertFocusSession(
            FocusSessionEntity(
                title = "Deep Focus: App Design",
                durationMinutes = 25,
                date = today
            )
        )

        repository.saveDailyNote(
            DailyNoteEntity(
                date = today,
                mood = "Great",
                note = "Starting today with focus and clear priorities! Let's get things done."
            )
        )
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    // Task Actions
    fun addTask(title: String, description: String, category: String, priority: String, dueDate: String, dueTime: String) {
        viewModelScope.launch {
            repository.insertTask(
                TaskEntity(
                    title = title.trim(),
                    description = description.trim(),
                    category = category,
                    priority = priority,
                    dueDate = dueDate,
                    dueTime = dueTime
                )
            )
        }
    }

    fun toggleTaskCompleted(task: TaskEntity) {
        viewModelScope.launch {
            repository.setTaskCompleted(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Habit Actions
    fun addHabit(title: String, category: String, frequency: String) {
        viewModelScope.launch {
            repository.insertHabit(
                HabitEntity(
                    title = title.trim(),
                    category = category,
                    targetFrequency = frequency,
                    streakCount = 0
                )
            )
        }
    }

    fun toggleHabitToday(habit: HabitEntity) {
        viewModelScope.launch {
            val isCompletedToday = habit.lastCompletedDate == todayDate
            val newStreak = if (isCompletedToday) {
                (habit.streakCount - 1).coerceAtLeast(0)
            } else {
                habit.streakCount + 1
            }
            val newCompletedDate = if (isCompletedToday) "" else todayDate

            repository.updateHabit(
                habit.copy(
                    streakCount = newStreak,
                    lastCompletedDate = newCompletedDate
                )
            )
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    // Focus Timer
    fun setTimerPresetMinutes(minutes: Int) {
        if (_isTimerRunning.value) pauseTimer()
        _timerDurationMinutes.value = minutes
        _timerSecondsRemaining.value = minutes * 60
    }

    fun startTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_timerSecondsRemaining.value > 0 && _isTimerRunning.value) {
                delay(1000L)
                _timerSecondsRemaining.value -= 1
            }
            if (_timerSecondsRemaining.value <= 0) {
                // Completed!
                _isTimerRunning.value = false
                val minutes = _timerDurationMinutes.value
                logFocusSession("Focus Session", minutes)
                _timerSecondsRemaining.value = _timerDurationMinutes.value * 60
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        timerJob = null
    }

    fun resetTimer() {
        pauseTimer()
        _timerSecondsRemaining.value = _timerDurationMinutes.value * 60
    }

    fun logFocusSession(title: String, minutes: Int) {
        viewModelScope.launch {
            repository.insertFocusSession(
                FocusSessionEntity(
                    title = title.ifBlank { "Deep Focus" },
                    durationMinutes = minutes,
                    date = todayDate
                )
            )
        }
    }

    // Daily Note Actions
    fun saveDailyNote(mood: String, noteText: String) {
        viewModelScope.launch {
            repository.saveDailyNote(
                DailyNoteEntity(
                    date = _selectedDate.value,
                    mood = mood,
                    note = noteText,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    // Helper: Week dates for horizontal bar
    fun getWeekDays(): List<Pair<String, String>> {
        val cal = Calendar.getInstance()
        val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dayNumFormat = SimpleDateFormat("dd", Locale.getDefault())

        val days = mutableListOf<Pair<String, String>>()
        // Get 7 days centered around today
        cal.add(Calendar.DAY_OF_YEAR, -3)
        for (i in 0..6) {
            val dateStr = dateFormatter.format(cal.time)
            val dayLabel = dayOfWeekFormat.format(cal.time)
            val dayNum = dayNumFormat.format(cal.time)
            days.add(Pair(dateStr, "$dayLabel\n$dayNum"))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return days
    }
}
