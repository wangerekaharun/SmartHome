package ke.co.appslab.smarthome.utils

import android.content.Context
import ke.co.appslab.smarthome.R
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId


fun getDurationFormatted(timestamp: Long, context: Context): String {
    val duration = getDifferenceBetweenTimeAndNow(timestamp)

    val text: String
    when {
        duration.toMinutes() < 1 -> text = context.resources.getString(R.string.few_seconds)
        duration.toMinutes() < 60 -> text = context.resources.getQuantityString(
            R.plurals.mins_formatted, duration.toMinutes().toInt(),
            duration.toMinutes().toInt()
        )
        duration.toHours() < 24 -> {
            val hoursLong = duration.toHours()
            val minutes = duration.minusHours(hoursLong)
            val minutesElapsed = minutes.toMinutes()
            text = context.resources
                .getQuantityString(
                    R.plurals.hours_formatted,
                    hoursLong.toInt(),
                    hoursLong.toInt()
                ) + ", " + context.resources
                .getQuantityString(R.plurals.mins_formatted, minutesElapsed.toInt(), minutesElapsed.toInt())
        }
        else -> {
            val days = duration.toDays()
            val hours = duration.minusDays(days)
            val hoursLong = hours.toHours()
            val minutes = hours.minusHours(hoursLong)
            val minutesElapsed = minutes.toMinutes()
            text =
                context.resources.getQuantityString(R.plurals.days_formatted, days.toInt(), days.toInt()) + ", " + context.resources
                    .getQuantityString(
                        R.plurals.hours_formatted,
                        hoursLong.toInt(),
                        hoursLong.toInt()
                    ) + ", " + context.resources
                    .getQuantityString(R.plurals.mins_formatted, minutesElapsed.toInt(), minutesElapsed.toInt())

        }
    }

    return text
}

private fun getDifferenceBetweenTimeAndNow(timestamp: Long): Duration {
    val today = LocalDateTime.now()
    val otherTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    return Duration.between(otherTime, today)
}