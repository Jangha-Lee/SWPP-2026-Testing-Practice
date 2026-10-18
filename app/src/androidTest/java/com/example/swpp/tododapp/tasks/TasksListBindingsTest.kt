package com.example.swpp.tododapp.tasks

import android.view.View
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.swpp.tododapp.data.Task
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class TasksListBindingsTest {

    private val specToday = LocalDate.of(2026, 10, 18)
    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @Test
    fun setDueDateLabel_activeTaskWithLabel_setsExactTextAndVisible() {
        // GIVEN - The specified device date, a hidden stale view, and a task due in seven days
        assertDeviceDateMatchesSpec()
        lateinit var actualText: String
        var actualVisibility = View.VISIBLE

        instrumentation.runOnMainSync {
            val textView = TextView(instrumentation.targetContext).apply {
                text = "old"
                visibility = View.GONE
            }
            val task = Task(isCompleted = false, dueDate = LocalDate.of(2026, 10, 25))

            // WHEN - The task is bound to the due-date view
            setDueDateLabel(textView, task)
            actualText = textView.text.toString()
            actualVisibility = textView.visibility
        }

        // THEN - The exact label is visible
        assertEquals("D-7", actualText)
        assertEquals(View.VISIBLE, actualVisibility)
    }

    @Test
    fun setDueDateLabel_completedTaskWithoutDueDate_setsClearAndVisible() {
        // GIVEN - The specified device date, a hidden stale view, and a completed task
        assertDeviceDateMatchesSpec()
        lateinit var actualText: String
        var actualVisibility = View.GONE

        instrumentation.runOnMainSync {
            val textView = TextView(instrumentation.targetContext).apply {
                text = "old"
                visibility = View.GONE
            }
            val task = Task(isCompleted = true, dueDate = null)

            // WHEN - The completed task is bound to the due-date view
            setDueDateLabel(textView, task)
            actualText = textView.text.toString()
            actualVisibility = textView.visibility
        }

        // THEN - CLEAR is visible even without a due date
        assertEquals("CLEAR", actualText)
        assertEquals(View.VISIBLE, actualVisibility)
    }

    @Test
    fun setDueDateLabel_activeTaskWithoutDueDate_clearsTextAndHides() {
        // GIVEN - The specified device date, a visible stale label, and an active task without a due date
        assertDeviceDateMatchesSpec()
        lateinit var actualText: String
        var actualVisibility = View.VISIBLE

        instrumentation.runOnMainSync {
            val textView = TextView(instrumentation.targetContext).apply {
                text = "D-7"
                visibility = View.VISIBLE
            }
            val task = Task(isCompleted = false, dueDate = null)

            // WHEN - The task is bound to the due-date view
            setDueDateLabel(textView, task)
            actualText = textView.text.toString()
            actualVisibility = textView.visibility
        }

        // THEN - Stale text is cleared and the view is gone
        assertEquals("", actualText)
        assertEquals(View.GONE, actualVisibility)
    }

    @Test
    fun setDueDateLabel_nullTask_clearsTextAndHides() {
        // GIVEN - The specified device date and a visible stale CLEAR label
        assertDeviceDateMatchesSpec()
        lateinit var actualText: String
        var actualVisibility = View.VISIBLE

        instrumentation.runOnMainSync {
            val textView = TextView(instrumentation.targetContext).apply {
                text = "CLEAR"
                visibility = View.VISIBLE
            }

            // WHEN - A null task is bound to the due-date view
            setDueDateLabel(textView, null)
            actualText = textView.text.toString()
            actualVisibility = textView.visibility
        }

        // THEN - Stale text is cleared and the view is gone
        assertEquals("", actualText)
        assertEquals(View.GONE, actualVisibility)
    }

    @Test
    fun setDueDateLabel_validTaskThenNull_returnsExpectedStatesWithoutThrowing() {
        // GIVEN - The specified device date, a visible stale view, a task due today, and null
        assertDeviceDateMatchesSpec()
        val states = mutableListOf<Pair<String, Int>>()

        instrumentation.runOnMainSync {
            val textView = TextView(instrumentation.targetContext).apply {
                text = "old"
                visibility = View.VISIBLE
            }
            val taskDueToday = Task(
                isCompleted = false,
                dueDate = LocalDate.of(2026, 10, 18),
            )

            // WHEN - The valid task and then null are bound to the same view
            setDueDateLabel(textView, taskDueToday)
            states += textView.text.toString() to textView.visibility
            setDueDateLabel(textView, null)
            states += textView.text.toString() to textView.visibility
        }

        // THEN - Neither call throws and both exact states are produced
        assertEquals(
            listOf(
                "D-Day" to View.VISIBLE,
                "" to View.GONE,
            ),
            states,
        )
    }

    @Test
    fun setDueDateLabel_boundaryRebindings_replaceTextAndVisibility() {
        // GIVEN - The specified device date, one stale view, and tasks across every label boundary
        assertDeviceDateMatchesSpec()
        val states = mutableListOf<Pair<String, Int>>()
        val tasks = listOf(
            Task(isCompleted = false, dueDate = null),
            Task(isCompleted = false, dueDate = LocalDate.of(2026, 10, 17)),
            Task(isCompleted = false, dueDate = LocalDate.of(2026, 10, 18)),
            Task(isCompleted = false, dueDate = LocalDate.of(2026, 10, 19)),
            Task(isCompleted = false, dueDate = null),
        )

        instrumentation.runOnMainSync {
            val textView = TextView(instrumentation.targetContext).apply {
                text = "stale"
                visibility = View.VISIBLE
            }

            // WHEN - Every task is bound to the same recycled view in order
            tasks.forEach { task ->
                setDueDateLabel(textView, task)
                states += textView.text.toString() to textView.visibility
            }
        }

        // THEN - Every exact text and visibility transition is applied
        assertEquals(
            listOf(
                "" to View.GONE,
                "D+1" to View.VISIBLE,
                "D-Day" to View.VISIBLE,
                "D-1" to View.VISIBLE,
                "" to View.GONE,
            ),
            states,
        )
    }

    private fun assertDeviceDateMatchesSpec() {
        assertEquals(
            "These specification tests require the device local date to be 2026-10-18",
            specToday,
            LocalDate.now(),
        )
    }
}
