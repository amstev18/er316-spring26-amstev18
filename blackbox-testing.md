# Black Box Testing Report - Assignment 2

**Student Name:** [Your Name]  
**ASU ID:** [Your ASU ID]  
**Date:** [Date]

---

## Part 1: Equivalence Partitioning (EP)

Identify equivalence partitions for the `checkoutBook(Book book, Patron patron)` method based on the specification (JavaDoc).

Create **multiple tables**, one per partition category (e.g., book state, patron state, renewal, limits, etc.).

Do **not** put everything into one table.

**Column Explanations:**
- **Partition ID**: Unique identifier (e.g., EP 1.1, EP 2.1)
- **State**: A human-readable label for this partition (e.g., "Unavailable", "At checkout limit"). This is a descriptive name.
- **Valid/Invalid**: Whether this partition allows the operation to proceed. **Invalid** = an error/rejection code is expected (e.g., 2.0, 3.0, 4.0, 5.0). **Valid** = the checkout can proceed (may still return warning codes like 1.0 or 1.1).
- **Input Condition**: The precise programmatic condition that defines this partition (e.g., `availableCopies == 0`). Use "AND other conditions allow checkout" to indicate you're isolating this one condition.
- **Expected Return**: The specific return code (e.g., 2.0) or "Success" meaning successful checkout (0.0, 0.1, 1.0, or 1.1 depending on warning conditions).
- **Expected Behavior**: What should happen to the system state.

**FAQ - Table Organization:**
- The number of tables and how you organize them is **flexible**. There is no single "correct" way.
- Organize by logical categories that make sense to you (e.g., book state, patron eligibility, limits, renewals).
- The goal is clarity - a grader should easily understand your partitions.

### Example EP Table: Book Availability

| Partition ID | State | Valid/Invalid | Input Condition | Expected Return | Expected Behavior |
|--------------|-------|---------------|----------------|-----------------|------------------|
| EP 1.1 | Unavailable (0 copies) | Invalid | availableCopies == 0 AND other conditions allow checkout | 2.0 | No copies to checkout |
| EP 1.2 | Available (1+ copies) | Valid | availableCopies > 0 AND other conditions allow checkout | Success | Book can be checked out |

**Example test cases:** `testBookAvailable()`, `testUnavailableBook()`

---

### Your EP Tables (add as many as needed)

### Table 2: Patron Exists
| Partition ID | State  | Valid/Invalid | Input Condition | Expected Return | Expected Behavior    |
|--------------|--------|---------------|-----------------|-----------------|----------------------|
| EP 2.1       | Null   | Invalid       | patron == null  | 3.1             | Checkout Rejected    |
| EP 2.2       | Exists | Valid         | patron != null  | Success         | Continues validation |

### Table 3: Patron Account
| Partition ID | State     | Valid/Invalid | Input Condition                      | Expected Return | Expected Behavior   |
|--------------|-----------|---------------|--------------------------------------|-----------------|---------------------|
| EP 3.1       | Suspended | Invalid       | patron.isAccountSuspended() == true  | 3.0             | Checkout Rejected   |
| EP 3.2       | Active    | Valid         | patron.isAccountSuspended() == false | Success         | Continues validaion |

### Table 4: Patron Overdue
| Partition ID | State       | Valid/Invalid | Input Condition                        | Expected Return | Expected Behavior             |
|--------------|-------------|---------------|----------------------------------------|-----------------|-------------------------------|
| EP 4.1       | 0 overdue   | Valid         | overdueCount == 0                      | Success         | Checkout Succeed              |
| EP 4.2       | 1-2 overdue | Valid         | overdueCount >= 1 && overdueCount <= 2 | 1.0             | Checkout Succeed with warning |
| EP 4.3       | 3+ overdue  | Invalid       | overdueCount > 2                       | 4.0             | Checkout Rejected             |

### Table 5: Patron Fines
| Partition ID | State       | Valid/Invalid | Input Condition     | Expected Return | Expected Behavior |
|--------------|-------------|---------------|---------------------|-----------------|------------------|
| EP 5.1       | No fines    | Valid         | fineBalance == 0.0  | Success         | Checkout Succeed |
| EP 5.2       | Fines < 10  | Valid         | fineBalance < 10.0  | Success         | Checkout Succeed |
| EP 5.3       | Fines >= 10 | Invalid       | fineBalance >= 10.0 | 4.1             | Checkout Rejected |

### Table 6: Checkout Limit
| Partition ID | State       | Valid/Invalid | Input Condition                                     | Expected Return | Expected Behavior              |
|--------------|-------------|---------------|-----------------------------------------------------|-----------------|--------------------------------|
| EP 6.1       | Below limit | Valid         | checkoutCount < limit - 2                           | Success         | Checkout Succeeds              |
| EP 6.2       | Within 2    | Valid         | checkoutCount >= limit - 2 && checkoutCount < limit | 1.1             | Checkout Succeeds with warning |
| EP 6.3       | At limit    | Invalid       | checkoutCount >= limit                              | 3.2             | Checkout Rejected              |

### Table 7: Book Exists
| Partition ID | State  | Valid/Invalid | Input Condition | Expected Return | Expected Behavior    |
|--------------|--------|---------------|-----------------|-----------------|----------------------|
| EP 7.1       | Null   | Invalid       | book == null    | 2.1             | Checkout Rejected    |
| EP 7.2       | Exists | Valid         | book != null    | Success         | Continues Validation |

### Table 8: Book Reference
| Partition ID | State              | Valid/Invalid | Input Condition                 | Expected Return | Expected Behavior    |
|--------------|--------------------|---------------|---------------------------------|-----------------|----------------------|
| EP 8.1       | Reference only     | Invalid       | book.isReferenceOnly() == true  | 5.0             | Checkout Rejected    |
| EP 8.2       | Not Reference only | Valid         | book.isReferenceOnly() == false | Success         | Continues Validation |

### Table 9: Book Availability
| Partition ID | State            | Valid/Invalid | Input Condition                | Expected Return | Expected Behavior    |
|--------------|------------------|---------------|--------------------------------|-----------------|----------------------|
| EP 9.1       | 0 copies         | Invalid       | book.getAvailableCopies() <= 0 | 2.0             | Checkout Rejected    |
| EP 9.2       | 1 or more copies | Valid         | book.getAvailableCopies() > 0  | Success         | Continues Validation |

### Table 10: Renewal
| Partition ID | State             | Valid/Invalid | Input Condition                                   | Expected Return | Expected Behavior |
|--------------|-------------------|---------------|---------------------------------------------------|-----------------|-------------------|
| EP 10.1      | Has Book          | Valid         | patron.hasBookCheckedOut(book.getIsbn()) == true  | 0.1             | Due date updated  |
| EP 10.2      | Doesn't Have Book | Valid         | patron.hasBookCheckedOut(book.getIsbn()) == false | Success         | Checkout Success  |

---

## Part 2: Boundary Value Analysis (BVA)

Important BVA cases may overlap with EP. That is OK. You can reference all relevant EP/BVA coverage in Part 3.

### Example BVA Table: Overdue Count (Threshold: 3)

| Test ID | Boundary | Input Value | Expected Return | Rationale |
|---------|----------|-------------|-----------------|-----------|
| BVA 1.1 | Below | overdueCount = 0 | Success (depends on other setup) | Below warning threshold |
| BVA 1.2 | Warning High | overdueCount = 2 | 1.0 | Just below reject threshold |
| BVA 1.3 | At | overdueCount = 3 | 4.0 | At rejection boundary |
| BVA 1.4 | Above | overdueCount = 4 | 4.0 | Above rejection boundary |

---

### Your BVA Tables (add more as needed)

### Table 2: Fine Balance
| Test ID | Boundary     | Input Value         | Expected Return | Rationale                   |
|---------|--------------|---------------------|-----------------|-----------------------------|
| BVA 2.1 | Below        | fineBalance = 0     | Success         | Success                     |
| BVA 2.2 | Warning High | fineBalance = 9.99  | Success         | Just below reject threshold |
| BVA 2.3 | At           | fineBalance = 10.00 | 4.1             | At rejection threshold      |
| BVA 2.3 | Above        | fineBalance = 10.01 | 4.1             | Above rejection threshold   |

### Table 3: Checkout Limit (for student)
| Test ID | Boundary     | Input Value        | Expected Return | Rationale                      |
|---------|--------------|--------------------|-----------------|--------------------------------|
| BVA 3.1 | Below        | checkoutCount = 7  | Success         | Success                        |
| BVA 3.2 | Warning High | checkoutCount = 9  | 1.1             | Just below rejection threshold |
| BVA 3.3 | At           | checkoutCount = 10 | 3.2             | At rejection threshold         |
| BVA 3.4 | Above        | checkoutCount = 11 | 3.2             | Above rejection threshold      |

### Table 4: Book Availability
| Test ID | Boundary | Input Value         | Expected Return | Rationale |
|---------|----------|---------------------|-----------------|-----------|
| BVA 4.1 | Zero     | availableCopies = 0 | 2.0             | No copies |
| BVA 4.2 | Above    | availableCopies = 1 | Success         | Success   |

---

## Part 3: Test Cases Designed

List at least **20** test cases you designed based on your EP/BVA analysis.

### Test Case Table
At least some of your tests should verify observable state changes, not just return values.

**Checkout0-3 Columns:** Mark each implementation as Pass (✓) or Fail (✗) for this test case. This helps you track which implementations have bugs and will be useful for Part 4 analysis.

| Test ID Name                 | EP/BVA  | Input Description                                                        | Expected Return | Expected State Changes                           | Checkout0 | Checkout1 | Checkout2 | Checkout3 |
|------------------------------|---------|--------------------------------------------------------------------------|-----------------|--------------------------------------------------|-----------|-----------|-----------|-----------|
| T1 testUnavailableBook       | EP 1.1  | Book unavailable (0 copies), eligible patron                             | 2.0             | No state change                                  | ✓         | ✓         | ✗         | ✓         |
| T2 testBookAvailable         | EP 1.2  | Book available (1+ copies), eligible patron, no warnings normal checkout | 0.0             | Patron map updated; copies of book change        | ✗         | ✗         | ✓         | ✓         |
| T3 testPatronNull            | EP 2.1  | Patron doesn't exist (null)                                              | 3.1             | No state change                                  | ✓         | ✓         | ✓         | ✓         |
| T4 testPatronExists          | EP 2.2  | Patron exists                                                            | 0.0             | Booked added to checked list, available copies -1 | ✓         | ✗         | ✓         | ✓         |
| T5 testPatronSuspended       | EP 3.1  | Patron suspended                                                         | 3.0             | No state change                                  | ✓         | ✓         | ✓         | ✓         |
| T6 testPatronActive          | EP 3.2  | Patron is active                                                         | 0.0             | Booked added to checked list, available copies -1 | ✓         | ✗         | ✓         | ✓         |
| T7 testBookNull              | EP 7.1  | Book doesn't exist                                                       | 2.1             | No state change                                  | ✓         | ✓         | ✓         | ✓         |
| T8 testBookExists            | EP 7.2  | Book exists                                                              | 0.0             | Booked added to checked list, available copies -1 | ✓         | ✗         | ✓         | ✓         |
| T9 testReferenceOnly         | EP 8.1  | Book is reference only                                                   | 5.0             | No state change                                  | ✗         | ✓         | ✓         | ✓         |
| T10 testNotReference         | EP 8.2  | Book isn't reference only                                                | 0.0             | Booked added to checked list, available copies -1 | ✓         | ✗         | ✓         | ✓         |
| T11 testHasBook              | EP 10.1 | Patron has book                                                          | 0.1             | Due date updated                                 | ✓         | ✓         | ✗         | ✗         |
| T12 testDoesntHaveBook       | EP 10.2 | Patron doesn't have book                                                 | 0.0             | Booked added to checked list, available copies -1 | ✓         | ✗         | ✓         | ✓         |
| T13 testBelowFineLimit       | BVA 2.1 | Well below fine limit (under $9.99)                                      | 0.0             | Booked added to checked list, available copies -1 | ✓         | ✗         | ✓         | ✓         |
| T14 testWarningFineLimit     | BVA 2.2 | Close to fine limit (at $9.99)                                           | 0.0             | Booked added to checked list, available copies -1 | ✓         | ✗         | ✓         | ✓         |
| T15 testAtFineLimit          | BVA 2.3 | At fine limit (at $10)                                                   | 4.1             | No state change                                  | ✓         | ✓         | ✓         | ✓         |
| T16 testAboveFineLimit       | BVA 2.4 | Above fine limit (above $10)                                             | 4.1             | No state change                                  | ✓         | ✓         | ✓         | ✓         |
| T17 testBelowCheckoutLimit   | BVA 3.1 | Well below student checkout limit (under 8)                              | 0.0             | Booked added to checked list, available copies -1 | ✗         | ✗         | ✓         | ✗         |
| T18 testWarningCheckoutLimit | BVA 3.2 | Close to student checkout limit (at 8)                                   | 1.1             | Booked added to checked list, available copies -1 | ✓         | ✗         | ✓         | ✓         |
| T19 testAtCheckoutLimit      | BVA 3.3 | At student checkout limit (at 10)                                        | 3.2             | No state change                                  | ✓         | ✗         | ✗         | ✓         |
| T20 testAboveCheckoutLimit   | BVA 3.4 | Above student checkout limit (above 10)                                  | 3.2             | No state change                                  | ✓         | ✓         | ✓         | ✓         |
| T21 testOverdueWarning       | EP 4.2  | Student with =<2 overdue books                                           | 1.0             | Booked added to checked list, available copies -1| ✓         | ✗         | ✓         | ✗         |
| T22 testOverdueLimit         | EP 4.3  | Student with 3 or more overdue books                                     | 4.0             | No state change                                  | ✓         | ✓         | ✓         | ✓         |

(Add rows until you have at least 20.)

---

## Part 4: Bug Analysis

### Easter Eggs Found
List any easter egg messages you observed:
- "Some bugs only appear under specific conditions."
- "Testing can show the presence of bugs,"
- "but never their absence"
- "- Dijkstra"
- "Limits exist to be thoroughly tested."
- "Boundaries are where bugs hide."
- "The difference between theory and practice is that in theory, there is no difference."
- "Renew, reuse, recycle... books."
- "A book renewed is a book re-loved."
- "Renewing a book is like giving it a second chance."
- "https://www.youtube.com/watch?v=xvFZjo5PgG0"
- "...xvFZjo5PgG0 (test renewal to complete!)"
- "The happy path matters too."
- "Null checking: because null pointer exceptions are not fun."
- "Remember to test all the edge cases."
- "The best code is no code at all... but this isn't it."
- "Availability testing finds the books that aren't there."
- "Can't check out what isn't there."
- "Good EP testing checks all partitions."
- "Testing the sad path matters."
- "Reference books are meant to be consulted, not carried home."
- "These books stay home."
- "Reference materials: look but don't touch."
- "Stay in the library, book!"

### Implementation Results

| Implementation | Bugs Found (count) |
|----------------|--------------------|
| Checkout0      | 3                  |
| Checkout1      | 2                  |
| Checkout2      | 3                  |
| Checkout3      | 3                  |

### Bugs Discovered
List distinct bugs you identified for each implementation. Each bug must cite at least one test case that revealed it.

**Checkout0:**
- Bug 1: Book availability not updated and available copies not decremented — Revealed by: T2
- Bug 2: Wrong return code for reference books, returns 2.0 instead of 5.0 — Revealed by: T9
- Bug 3: Off by one warning, returns 1.1 at 7 when it should at 8 — Revealed by: T17

**Checkout1:**
- Bug 1: Patron check out list never updated — Revealed by: T2
- Bug 2: Off by one max boundary, returns 1.1 instead of 3.2 when at max — Revealed by: T19

**Checkout2:**
- Bug 1: Availability check skipped, returns success for book with no availability — Revealed by: T1
- Bug 2: Renewal not shown, returns success instead of 0.1 when already has book — Revealed by: T11
- Bug 3: Off by one max boundary, returns 1.1 instead of 3.2 when at max — Revealed by: T19

**Checkout3:**
- Bug 1: Off by one warning, returns 1.1 at 7 when it should at 8 — Revealed by: T17
- Bug 2: Overdue warning not sent, returns success instead of 1.1 when there's 2 overdue books — Revealed by: T21
- Bug 3: Renewal still decrements available copies — Revealed by: T11

### Comparative Analysis
Compare the four implementations:
- Which bugs are most critical (cause the worst failures)?
- Which implementation would you use if you had to choose?
- Why? Justify your choice considering bug severity and frequency.

- The worst bug is checkout1 bug1, it causes another 12 failures just from the one bug, it lets books be forever checked out and not assigned
- Checkout3
- It only has 3 minor bugs that don't really cause many issues for other processes. The renewal decrementing is an issue, but not major compared to the other 3
---

## Part 5: Reflection

**Which testing technique was most effective for finding bugs?**
- BVA was best, there was a lot of one-off errors that ep's wouldn't check for.

**What was the most challenging aspect of this assignment?**
- It was just a lot of work, there were a ton of eps to be included, and writing out 20 full tests is a lot.

**How did you decide on your EP and BVA?**
- I went off of the throw codes and tried to associate a ep to each one, and then each ep that had a number change I assigned a bva to.

**Describe one test where checking only the return value would NOT have been sufficient to detect a bug.**
- T2 caught the issue on checkout0 where the method returned success, but if we didn't check book.IsAvailable, it would have been sent through as no issue.
