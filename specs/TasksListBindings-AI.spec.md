# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: Bind a task's current D-day label to its list-row `TextView`. Show the view only when a non-empty label exists, and fully clear stale state when the row is rebound without a label.

**Pre-conditions**
- `textView` is a valid Android `TextView` attached to, or usable as, a task-list row view.
- The device's current local date is used as `today` when calculating an active task's label.

**Post-conditions**
- Success cases
  - An active task with a non-empty D-day label replaces the view's text with that label and sets its visibility to `View.VISIBLE`. `[Integration]`
    - Tip: Given the device date is `2026-10-18`, a visible `TextView` containing `"old"`, and an active task with `dueDate = 2026-10-25` / When `setDueDateLabel` is called / Then the text is exactly `"D-7"` and visibility is exactly `View.VISIBLE`
  - A completed task replaces the view's text with `"CLEAR"` and sets its visibility to `View.VISIBLE`, even when it has no due date. `[Integration]`
    - Tip: Given the device date is `2026-10-18`, a `TextView`, and a completed task with `dueDate = null` / When `setDueDateLabel` is called / Then the text is exactly `"CLEAR"` and visibility is exactly `View.VISIBLE`
  - An active task without a due date clears the view's text to `""` and sets its visibility to `View.GONE`. `[Integration]`
    - Tip: Given the device date is `2026-10-18`, a visible `TextView` containing `"D-7"`, and an active task with `dueDate = null` / When `setDueDateLabel` is called / Then the text is exactly `""` and visibility is exactly `View.GONE`
  - A null task clears the view's text to `""` and sets its visibility to `View.GONE`. `[Integration]`
    - Tip: Given the device date is `2026-10-18` and a visible `TextView` containing `"CLEAR"` / When `setDueDateLabel` is called with `task = null` / Then the text is exactly `""` and visibility is exactly `View.GONE`
- Error cases
  - None. A valid `TextView` can be bound with either a task or `null`. `[Integration]`
- Edge cases
  - Rebinding across the empty/non-empty boundary replaces all prior state: yesterday, today, and tomorrow remain visible with their exact labels, while a subsequent task without a due date is gone with empty text. `[Integration]`
    - Tip: Given the device date is `2026-10-18`, one `TextView`, and active tasks due on `2026-10-17`, `2026-10-18`, `2026-10-19`, and `null` / When `setDueDateLabel` binds those tasks in that order / Then the successive text/visibility states are exactly `"D+1"`/`View.VISIBLE`, `"D-Day"`/`View.VISIBLE`, `"D-1"`/`View.VISIBLE`, and `""`/`View.GONE`
