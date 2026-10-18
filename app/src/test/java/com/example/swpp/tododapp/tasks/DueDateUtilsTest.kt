package com.example.swpp.tododapp.tasks

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class DueDateUtilsTest {

    private val today = LocalDate.of(2026, 10, 18)

    @Test
    fun dueDateLabel_completedWithAnyDueDate_returnsClear() {
        // GIVEN - A completed task with absent, overdue, current, and upcoming due dates
        val dueDates = listOf(
            null,
            LocalDate.of(2026, 10, 17),
            LocalDate.of(2026, 10, 18),
            LocalDate.of(2026, 10, 19),
        )

        // WHEN - A label is produced for each due date
        val labels = dueDates.map { dueDate ->
            dueDateLabel(dueDate, today, isCompleted = true)
        }

        // THEN - Completion takes precedence for every due date
        assertEquals(listOf("CLEAR", "CLEAR", "CLEAR", "CLEAR"), labels)
    }

    @Test
    fun dueDateLabel_activeWithoutDueDate_returnsEmptyString() {
        // GIVEN - An active task without a due date on 2026-10-18
        val dueDate: LocalDate? = null

        // WHEN - Its label is produced
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is exactly empty
        assertEquals("", label)
    }

    @Test
    fun dueDateLabel_dueToday_returnsDDay() {
        // GIVEN - An active task due on 2026-10-18, which is today
        val dueDate = LocalDate.of(2026, 10, 18)

        // WHEN - Its label is produced
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label uses the exact due-today format
        assertEquals("D-Day", label)
    }

    @Test
    fun dueDateLabel_dueInSevenDays_returnsDMinusSeven() {
        // GIVEN - An active task due on 2026-10-25 from 2026-10-18
        val dueDate = LocalDate.of(2026, 10, 25)

        // WHEN - Its label is produced
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label contains seven without a leading zero
        assertEquals("D-7", label)
    }

    @Test
    fun dueDateLabel_overdueBySevenDays_returnsDPlusSeven() {
        // GIVEN - An active task due on 2026-10-11 from 2026-10-18
        val dueDate = LocalDate.of(2026, 10, 11)

        // WHEN - Its label is produced
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The overdue label contains seven without a leading zero
        assertEquals("D+7", label)
    }

    @Test
    fun dueDateLabel_validInputMatrix_returnsExpectedLabelsWithoutThrowing() {
        // GIVEN - Valid nullable due dates and completion states on 2026-10-18
        val inputs = listOf(
            null to false,
            LocalDate.of(2026, 10, 17) to false,
            LocalDate.of(2026, 10, 18) to false,
            LocalDate.of(2026, 10, 19) to false,
            null to true,
        )

        // WHEN - A label is produced for every valid input pair
        val labels = inputs.map { (dueDate, isCompleted) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // THEN - No call throws and every exact label is returned
        assertEquals(listOf("", "D+1", "D-Day", "D-1", "CLEAR"), labels)
    }

    @Test
    fun dueDateLabel_datesAroundToday_returnsBoundaryLabels() {
        // GIVEN - Due dates immediately before, on, and after 2026-10-18
        val dueDates = listOf(
            LocalDate.of(2026, 10, 17),
            LocalDate.of(2026, 10, 18),
            LocalDate.of(2026, 10, 19),
        )

        // WHEN - A label is produced for every boundary date
        val labels = dueDates.map { dueDate ->
            dueDateLabel(dueDate, today, isCompleted = false)
        }

        // THEN - Overdue, today, and upcoming formats are exact
        assertEquals(listOf("D+1", "D-Day", "D-1"), labels)
    }

    @Test
    fun dueDateLabel_datesAcrossMonthBoundary_returnsTotalDayLabels() {
        // GIVEN - Dates immediately before, on, and after 2026-03-01 from 2026-01-31
        val monthStart = LocalDate.of(2026, 1, 31)
        val dueDates = listOf(
            LocalDate.of(2026, 2, 28),
            LocalDate.of(2026, 3, 1),
            LocalDate.of(2026, 3, 2),
        )

        // WHEN - A label is produced across the month boundary
        val labels = dueDates.map { dueDate ->
            dueDateLabel(dueDate, monthStart, isCompleted = false)
        }

        // THEN - The labels contain the total calendar-day distances
        assertEquals(listOf("D-28", "D-29", "D-30"), labels)
    }

    @Test
    fun dueDateLabel_datesAcrossYearBoundary_returnsTotalDayLabels() {
        // GIVEN - Dates immediately before, on, and after 2027-01-01 from 2026-12-01
        val yearStart = LocalDate.of(2026, 12, 1)
        val dueDates = listOf(
            LocalDate.of(2026, 12, 31),
            LocalDate.of(2027, 1, 1),
            LocalDate.of(2027, 1, 2),
        )

        // WHEN - A label is produced across the year boundary
        val labels = dueDates.map { dueDate ->
            dueDateLabel(dueDate, yearStart, isCompleted = false)
        }

        // THEN - The labels contain the total calendar-day distances
        assertEquals(listOf("D-30", "D-31", "D-32"), labels)
    }

    @Test
    fun dueDateLabel_datesAcrossLeapDay_returnsTotalDayLabels() {
        // GIVEN - Dates immediately before, on, and after leap day from 2028-02-01
        val leapMonthStart = LocalDate.of(2028, 2, 1)
        val dueDates = listOf(
            LocalDate.of(2028, 2, 28),
            LocalDate.of(2028, 2, 29),
            LocalDate.of(2028, 3, 1),
        )

        // WHEN - A label is produced across the leap-day boundary
        val labels = dueDates.map { dueDate ->
            dueDateLabel(dueDate, leapMonthStart, isCompleted = false)
        }

        // THEN - February 29 is included in the total calendar-day distances
        assertEquals(listOf("D-27", "D-28", "D-29"), labels)
    }
}
