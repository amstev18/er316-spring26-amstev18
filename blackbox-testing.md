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

### Table 1: Patron Exists
| Partition ID | State  | Valid/Invalid | Input Condition | Expected Return | Expected Behavior    |
|--------------|--------|---------------|-----------------|-----------------|----------------------|
| EP 1.1       | Null   | Invalid       | patron == null  | 3.1             | Checkout Rejected    |
| EP 1.2       | Exists | Valid         | patron != null  | Success         | Continues validation |

### Table 2: Patron Account
| Partition ID | State     | Valid/Invalid | Input Condition                      | Expected Return | Expected Behavior   |
|--------------|-----------|---------------|--------------------------------------|-----------------|---------------------|
| EP 2.1       | Suspended | Invalid       | patron.isAccountSuspended() == true  | 3.0             | Checkout Rejected   |
| EP 2.2       | Active    | Valid         | patron.isAccountSuspended() == false | Success         | Continues validaion |

### Table 3: Patron Overdue
| Partition ID | State       | Valid/Invalid | Input Condition                        | Expected Return | Expected Behavior             |
|-----------|-------------|---------------|----------------------------------------|-----------------|-------------------------------|
| EP 3.1    | 0 overdue   | Valid         | overdueCount == 0                      | Success         | Checkout Succeed              |
| EP 3.2    | 1-2 overdue | Valid         | overdueCount >= 1 && overdueCount <= 2 | 1.0             | Checkout Succeed with warning |
| EP 3.3    | 3+ overdue  | Invalid       | overdueCount > 2                       | 4.0             | Checkout Rejected             |

### Table 4: Patron Fines
| Partition ID | State       | Valid/Invalid | Input Condition     | Expected Return | Expected Behavior |
|--------------|-------------|---------------|---------------------|-----------------|------------------|
| EP 4.1       | No fines    | Valid         | fineBalance == 0.0  | Success         | Checkout Succeed |
| EP 4.2       | Fines < 10  | Valid         | fineBalance < 10.0  | Success         | Checkout Succeed |
| EP 4.3       | Fines >= 10 | Invalid       | fineBalance >= 10.0 | 4.1             | Checkout Rejected |

### Table 5: Checkout Limit
| Partition ID | State       | Valid/Invalid | Input Condition                                     | Expected Return | Expected Behavior              |
|--------------|-------------|---------------|-----------------------------------------------------|-----------------|--------------------------------|
| EP 5.1       | Below limit | Valid         | checkoutCount < limit - 2                           | Success         | Checkout Succeeds              |
| EP 5.2       | Within 2    | Valid         | checkoutCount >= limit - 2 && checkoutCount < limit | 1.1             | Checkout Succeeds with warning |
| EP 5.3       | At limit    | Invalid       | checkoutCount >= limit                              | 3.2             | Checkout Rejected              |

### Table 6: Book Exists
| Partition ID | State  | Valid/Invalid | Input Condition | Expected Return | Expected Behavior    |
|--------------|--------|---------------|-----------------|-----------------|----------------------|
| EP 6.1       | Null   | Invalid       | book == null    | 2.1             | Checkout Rejected    |
| EP 6.2       | Exists | Valid         | book != null    | Success         | Continues Validation |

### Table 7: Book Type
| Partition ID | State              | Valid/Invalid | Input Condition                 | Expected Return | Expected Behavior    |
|--------------|--------------------|---------------|---------------------------------|-----------------|----------------------|
| EP 7.1       | Reference only     | Invalid       | book.isReferenceOnly() == true  | 5.0             | Checkout Rejected    |
| EP 7.2       | Not Reference only | Valid         | book.isReferenceOnly() == false | Success         | Continues Validation |       | Continues Validation |

### Table 8: Book Availability
| Partition ID | State            | Valid/Invalid | Input Condition                | Expected Return | Expected Behavior    |
|--------------|------------------|---------------|--------------------------------|-----------------|----------------------|
| EP 8.1       | 0 copies         | Invalid       | book.getAvailableCopies() <= 0 | 2.0             | Checkout Rejected    |
| EP 8.2       | 1 or more copies | Valid         | book.getAvailableCopies() > 0  | Success         | Continues Validation |

### Table 9: Renewal
| Partition ID | State             | Valid/Invalid | Input Condition                                   | Expected Return | Expected Behavior |
|--------------|-------------------|---------------|---------------------------------------------------|-----------------|-------------------|
| EP 9.1       | Has Book          | Valid         | patron.hasBookCheckedOut(book.getIsbn()) == true  | 0.1             | Due date updated  |
| EP 9.2       | Doesn't Have Book | Valid         | patron.hasBookCheckedOut(book.getIsbn()) == false | Success         | Checkout Success  |

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

| Test ID | Boundary | Input Value | Expected Return | Rationale |
|---------|----------|-------------|-----------------|-----------|
| BVA ___ | | | | |

---

## Part 3: Test Cases Designed

List at least **20** test cases you designed based on your EP/BVA analysis.

### Test Case Table
At least some of your tests should verify observable state changes, not just return values.

**Checkout0-3 Columns:** Mark each implementation as Pass (✓) or Fail (✗) for this test case. This helps you track which implementations have bugs and will be useful for Part 4 analysis.

| Test ID Name | EP/BVA | Input Description | Expected Return | Expected State Changes | Checkout0 | Checkout1 | Checkout2 | Checkout3 |
|--------------|--------|-------------------|-----------------|------------------------|-----------|-----------|-----------|-----------|
| T1 testUnavailableBook | EP 1.1 | Book unavailable (0 copies), eligible patron | 2.0 | No state change | ✓ | ✓ | ✗ | ✓ |
| T2 testBookAvailable | EP 1.2 | Book available (1+ copies), eligible patron, no warnings normal checkout | 0.0 | Patron map updated; copies of book change | ✗ | ✗ | ✓ | ✓ |

(Add rows until you have at least 20.)

---

## Part 4: Bug Analysis

### Easter Eggs Found
List any easter egg messages you observed:
- 
- 

### Implementation Results

| Implementation | Bugs Found (count) |
|----------------|---------------------|
| Checkout0      | |
| Checkout1      | |
| Checkout2      | |
| Checkout3      | |

### Bugs Discovered
List distinct bugs you identified for each implementation. Each bug must cite at least one test case that revealed it.

**Checkout0:**
- Bug 1: [Brief description] — Revealed by: [Test ID]

**Checkout1:**
- Bug 1: [Brief description] — Revealed by: [Test ID]

**Checkout2:**
- Bug 1: [Brief description] — Revealed by: [Test ID]

**Checkout3:**
- Bug 1: [Brief description] — Revealed by: [Test ID]

### Comparative Analysis
Compare the four implementations:
- Which bugs are most critical (cause the worst failures)?
- Which implementation would you use if you had to choose?
- Why? Justify your choice considering bug severity and frequency.

---

## Part 5: Reflection

**Which testing technique was most effective for finding bugs?**

**What was the most challenging aspect of this assignment?**

**How did you decide on your EP and BVA?**

**Describe one test where checking only the return value would NOT have been sufficient to detect a bug.**

