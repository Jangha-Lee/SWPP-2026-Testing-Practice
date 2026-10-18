# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: Bind a task's current D-day label to its list-row `TextView`. A non-empty label is visible; an absent label clears and hides the view so recycled rows never retain stale text.

**Pre-conditions**
- `textView` is a valid Android `TextView` usable as a task-list row view.
- The device's current local date supplies `today` for an active task's D-day calculation.

**Post-conditions**
- Success cases
  - An active task with a non-empty D-day label replaces any existing text with the exact label and sets visibility to `View.VISIBLE`. `[Integration]`
    - Tip: Given the device date is `2026-10-18`, a `TextView` with text `"old"` and visibility `View.GONE`, and an active task with `dueDate = 2026-10-25` / When `setDueDateLabel` is called / Then the text is exactly `"D-7"` and visibility is exactly `View.VISIBLE`
  - A completed task replaces any existing text with exactly `"CLEAR"` and sets visibility to `View.VISIBLE`, even when its due date is absent. `[Integration]`
    - Tip: Given the device date is `2026-10-18`, a `TextView` with text `"old"` and visibility `View.GONE`, and a completed task with `dueDate = null` / When `setDueDateLabel` is called / Then the text is exactly `"CLEAR"` and visibility is exactly `View.VISIBLE`
  - An active task without a due date clears the text to exactly `""` and sets visibility to `View.GONE`. `[Integration]`
    - Tip: Given the device date is `2026-10-18`, a `TextView` with text `"D-7"` and visibility `View.VISIBLE`, and an active task with `dueDate = null` / When `setDueDateLabel` is called / Then the text is exactly `""` and visibility is exactly `View.GONE`
  - A null task clears the text to exactly `""` and sets visibility to `View.GONE`. `[Integration]`
    - Tip: Given the device date is `2026-10-18` and a `TextView` with text `"CLEAR"` and visibility `View.VISIBLE` / When `setDueDateLabel` is called with `task = null` / Then the text is exactly `""` and visibility is exactly `View.GONE`
- Error cases
  - None. A valid `TextView` can be bound with either a non-null task or `null` without throwing. `[Integration]`
    - Tip: Given the device date is `2026-10-18`, a `TextView` with text `"old"` and visibility `View.VISIBLE`, an active task with `dueDate = 2026-10-18`, and `task = null` / When `setDueDateLabel` is called first with the active task and then with `null` / Then neither call throws and the successive text/visibility states are exactly `"D-Day"`/`View.VISIBLE` and `""`/`View.GONE`
- Edge cases
  - Rebinding across the absent/overdue/today/upcoming/absent boundaries replaces both text and visibility on every call, covering hidden-to-visible and visible-to-hidden transitions. `[Integration]`
    - Tip: Given the device date is `2026-10-18`, one `TextView` with text `"stale"` and visibility `View.VISIBLE`, and active tasks due on `null`, `2026-10-17`, `2026-10-18`, `2026-10-19`, and `null` / When `setDueDateLabel` binds those tasks in that order / Then the successive text/visibility states are exactly `""`/`View.GONE`, `"D+1"`/`View.VISIBLE`, `"D-Day"`/`View.VISIBLE`, `"D-1"`/`View.VISIBLE`, and `""`/`View.GONE`
