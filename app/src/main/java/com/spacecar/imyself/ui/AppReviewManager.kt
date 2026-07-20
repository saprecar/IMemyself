package com.spacecar.imyself.ui

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object AppReviewManager {
    fun tryShowReviewPrompt(activity: Activity, firstTrackedDateMillis: Long) {
        val prefs = activity.getSharedPreferences("review_prefs", Context.MODE_PRIVATE)
        val today = LocalDate.now()
        val appStartDate = Instant.ofEpochMilli(firstTrackedDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()

        // 1. Must be > 7 days since app start
        val daysSinceStart = ChronoUnit.DAYS.between(appStartDate, today)
        if (daysSinceStart <= 7) return

        // 2. Check last prompt date
        val lastPromptMillis = prefs.getLong("last_prompt_millis", 0L)
        if (lastPromptMillis > 0L) {
            val lastPromptDate = Instant.ofEpochMilli(lastPromptMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            
            // 3. Must be at least 10 days since last prompt
            val daysSinceLastPrompt = ChronoUnit.DAYS.between(lastPromptDate, today)
            if (daysSinceLastPrompt < 10) return
            
            // 4. Must be a different month
            if (lastPromptDate.year == today.year && lastPromptDate.monthValue == today.monthValue) return
        }

        // 5. Random 10% chance per launch when conditions are met
        if (Math.random() > 0.10) return

        // Show prompt
        val manager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    // Save the timestamp regardless of whether they actually rated it or dismissed it
                    prefs.edit().putLong("last_prompt_millis", System.currentTimeMillis()).apply()
                }
            }
        }
    }
}
