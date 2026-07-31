package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "Work", // Work, Personal, Health, Study, Home
    val priority: String = "Medium", // High, Medium, Low
    val dueDate: String, // YYYY-MM-DD
    val dueTime: String = "09:00", // HH:mm
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String = "Health",
    val targetFrequency: String = "Daily",
    val streakCount: Int = 0,
    val lastCompletedDate: String = "", // YYYY-MM-DD
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val durationMinutes: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val date: String // YYYY-MM-DD
)

@Entity(tableName = "daily_notes")
data class DailyNoteEntity(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val mood: String = "Good", // Great, Good, Okay, Tired, Stressed
    val note: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
