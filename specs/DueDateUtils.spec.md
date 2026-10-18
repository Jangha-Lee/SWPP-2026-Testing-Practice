# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Build the task-list D-day label from completion state and the total number of calendar days between `today` and `dueDate`. Completion takes precedence over the presence or value of a due date.

**Pre-conditions**
- `today` and every non-null `dueDate` are valid `LocalDate` values.

**Post-conditions**
- Success cases
  - A completed task returns exactly `"CLEAR"`, whether its due date is absent, overdue, today, or upcoming. `[Unit]`
    - Tip: Given `today = 2026-10-18`, `isCompleted = true`, and due dates `null`, `2026-10-17`, `2026-10-18`, and `2026-10-19` / When `dueDateLabel` is called for each due date / Then all four results are exactly `"CLEAR"`
  - An active task without a due date returns exactly the empty string `""`. `[Unit]`
    - Tip: Given `dueDate = null`, `today = 2026-10-18`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns exactly `""`
  - An active task due today returns exactly `"D-Day"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-18`, `today = 2026-10-18`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns exactly `"D-Day"`
  - An active task due after today returns exactly `"D-N"`, where `N` is the total number of calendar days from `today` to `dueDate`, without leading zeroes. `[Unit]`
    - Tip: Given `dueDate = 2026-10-25`, `today = 2026-10-18`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns exactly `"D-7"`
  - An active task overdue before today returns exactly `"D+N"`, where `N` is the total number of calendar days from `dueDate` to `today`, without leading zeroes. `[Unit]`
    - Tip: Given `dueDate = 2026-10-11`, `today = 2026-10-18`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns exactly `"D+7"`
- Error cases
  - None. Every combination of nullable `dueDate`, valid `today`, and Boolean `isCompleted` returns a string without throwing. `[Unit]`
    - Tip: Given `today = 2026-10-18` and `(dueDate, isCompleted)` pairs `(null, false)`, `(2026-10-17, false)`, `(2026-10-18, false)`, `(2026-10-19, false)`, and `(null, true)` / When `dueDateLabel` is called for each pair / Then no call throws and the results are exactly `""`, `"D+1"`, `"D-Day"`, `"D-1"`, and `"CLEAR"`, respectively
- Edge cases
  - At the overdue/today/upcoming boundary, the dates immediately before, at, and after `today` return exactly `"D+1"`, `"D-Day"`, and `"D-1"`. `[Unit]`
    - Tip: Given `today = 2026-10-18`, `isCompleted = false`, and due dates `2026-10-17`, `2026-10-18`, and `2026-10-19` / When `dueDateLabel` is called for each date / Then the results are exactly `"D+1"`, `"D-Day"`, and `"D-1"`, respectively
  - Total calendar-day distance remains exact for dates immediately before, at, and after a month boundary. `[Unit]`
    - Tip: Given `today = 2026-01-31`, `isCompleted = false`, and due dates `2026-02-28`, `2026-03-01`, and `2026-03-02` / When `dueDateLabel` is called for each date / Then the results are exactly `"D-28"`, `"D-29"`, and `"D-30"`, respectively
  - Total calendar-day distance remains exact for dates immediately before, at, and after a year boundary. `[Unit]`
    - Tip: Given `today = 2026-12-01`, `isCompleted = false`, and due dates `2026-12-31`, `2027-01-01`, and `2027-01-02` / When `dueDateLabel` is called for each date / Then the results are exactly `"D-30"`, `"D-31"`, and `"D-32"`, respectively
  - Total calendar-day distance includes February 29 and remains exact immediately before, on, and after the leap-day boundary. `[Unit]`
    - Tip: Given `today = 2028-02-01`, `isCompleted = false`, and due dates `2028-02-28`, `2028-02-29`, and `2028-03-01` / When `dueDateLabel` is called for each date / Then the results are exactly `"D-27"`, `"D-28"`, and `"D-29"`, respectively
