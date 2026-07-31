package com.example.data

import kotlinx.coroutines.flow.Flow

class PlannerRepository(private val dao: PlannerDao) {

    // Tasks
    fun getTasksForDate(date: String): Flow<List<TaskEntity>> = dao.getTasksByDate(date)
    fun getAllTasks(): Flow<List<TaskEntity>> = dao.getAllTasks()
    suspend fun insertTask(task: TaskEntity) = dao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = dao.updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = dao.deleteTask(task)
    suspend fun setTaskCompleted(taskId: Long, isCompleted: Boolean) = dao.setTaskCompleted(taskId, isCompleted)

    // Habits
    fun getAllHabits(): Flow<List<HabitEntity>> = dao.getAllHabits()
    suspend fun insertHabit(habit: HabitEntity) = dao.insertHabit(habit)
    suspend fun updateHabit(habit: HabitEntity) = dao.updateHabit(habit)
    suspend fun deleteHabit(habit: HabitEntity) = dao.deleteHabit(habit)

    // Focus Sessions
    fun getFocusSessionsForDate(date: String): Flow<List<FocusSessionEntity>> = dao.getFocusSessionsByDate(date)
    fun getAllFocusSessions(): Flow<List<FocusSessionEntity>> = dao.getAllFocusSessions()
    suspend fun insertFocusSession(session: FocusSessionEntity) = dao.insertFocusSession(session)

    // Daily Notes
    fun getDailyNoteForDate(date: String): Flow<DailyNoteEntity?> = dao.getDailyNoteByDate(date)
    suspend fun saveDailyNote(note: DailyNoteEntity) = dao.insertOrUpdateDailyNote(note)
}
