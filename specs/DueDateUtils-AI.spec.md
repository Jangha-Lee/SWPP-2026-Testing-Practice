# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Build the task-list D-day label from completion state and the number of calendar days between `today` and `dueDate`. Completion takes precedence over the due date.

**Pre-conditions**
- `today` and every non-null `dueDate` are valid `LocalDate` values in the same calendar system.

**Post-conditions**
- Success cases
  - A completed task returns `"CLEAR"`, regardless of whether its due date is past, present, future, or absent. `[Unit]`
    - Tip: Given `today = 2026-10-18`, `isCompleted = true`, and due dates `null`, `2026-10-17`, `2026-10-18`, and `2026-10-19` / When `dueDateLabel` is called for each due date / Then every result is exactly `"CLEAR"`
  - An active task without a due date returns the empty string `""`. `[Unit]`
    - Tip: Given `dueDate = null`, `today = 2026-10-18`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns exactly `""`
  - An active task due today returns `"D-Day"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-18`, `today = 2026-10-18`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns exactly `"D-Day"`
  - An active task due after today returns `"D-N"`, where `N` is the exact number of calendar days from `today` to `dueDate`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-25`, `today = 2026-10-18`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns exactly `"D-7"`
  - An active task overdue before today returns `"D+N"`, where `N` is the exact number of calendar days from `dueDate` to `today`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-11`, `today = 2026-10-18`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns exactly `"D+7"`
- Error cases
  - None. Every value satisfying the pre-conditions produces a label. `[Unit]`
- Edge cases
  - The overdue/today/upcoming boundary is classified by exact calendar day: one day before is `"D+1"`, the same day is `"D-Day"`, and one day after is `"D-1"`. `[Unit]`
    - Tip: Given `today = 2026-10-18`, `isCompleted = false`, and due dates `2026-10-17`, `2026-10-18`, and `2026-10-19` / When `dueDateLabel` is called for each date / Then the results are exactly `"D+1"`, `"D-Day"`, and `"D-1"`, respectively
  - Calendar-day distance remains exact immediately before, on, and after a month boundary. `[Unit]`
    - Tip: Given `today = 2026-01-31`, `isCompleted = false`, and due dates `2026-02-28`, `2026-03-01`, and `2026-03-02` / When `dueDateLabel` is called for each date / Then the results are exactly `"D-28"`, `"D-29"`, and `"D-30"`, respectively
  - Calendar-day distance remains exact on both sides of a year boundary. `[Unit]`
    - Tip: Given `today = 2026-12-31`, `isCompleted = false`, and due dates `2026-12-30`, `2026-12-31`, and `2027-01-01` / When `dueDateLabel` is called for each date / Then the results are exactly `"D+1"`, `"D-Day"`, and `"D-1"`, respectively
  - Calendar-day distance includes February 29 in a leap year and remains exact immediately before, on, and after that boundary. `[Unit]`
    - Tip: Given `today = 2028-02-29`, `isCompleted = false`, and due dates `2028-02-28`, `2028-02-29`, and `2028-03-01` / When `dueDateLabel` is called for each date / Then the results are exactly `"D+1"`, `"D-Day"`, and `"D-1"`, respectively
