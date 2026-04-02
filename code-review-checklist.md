# Code Review Checklist

**Reviewer Name:** Aiden Stevens
**Date:** 2/24/26
**Branch:** Review

## Instructions
Review ALL source files (in main not test) in the project and identify defects using the categories below. Log at least 5 defects total:
- At least 1 from CS (Coding Standards)
- At least 1 from CG (Code Quality/General)
- At least 1 from FD (Functional Defects)
- Remaining can be from any category

## Review Categories

- **CS**: Coding Standards (naming conventions, formatting, style violations)
- **CG**: Code Quality/General (design issues, code smells, maintainability)
- **FD**: Functional Defects (logic errors, incorrect behavior, bugs)
- **MD**: Miscellaneous (documentation, comments, other issues)

## Defect Log

| Defect ID | File          | Line(s) | Category | Description                                                                             | Severity |
|-----------|---------------|---------|----------|-----------------------------------------------------------------------------------------|----------|
| 1 | Patron.java   | 151     | CG       | Else statment has <br/>no body                                                          | Medium   |
| 2 | Checkout.java | 246     | FD       | Comparing strings with '==' instead of .equals()                                        | High     |
| 3 | Patron.java   | 130     | CS       | The naming convention confusing, should be isSuspended                                  | Low      |
| 4 | Checkout.java | 15      | CG       | bookList is a Map not a list                                                            | Low      |
| 5 | Book.java     | 1       | CS       | ArrayList and List imports aren't used                                                  | Low      |
| 6 | Patron.java   | 130     | CG       | chkSuspended is the same as isAccountSuspended                                          | Low      |
| 7 | Book.java     | 106     | FD       | The if statement checks if availableCopies < 100, when it should check if < totalCopies | Medium   |
| 8 | Patron.java   | 117     | CS       | Uses many if and else statements when it should just use a switch                       | Low      |
| 9 | Patron.java   | 218     | CS       | The return has no spaces making it hard to read                                         | Low      |
| 10 |               |         |          |                                                                                         |          |

**Severity Levels:**
- **Critical**: Causes system failure, data corruption, or security issues
- **High**: Major functional defect or significant quality issue
- **Medium**: Moderate issue affecting maintainability or minor functional problem
- **Low**: Minor style issue or cosmetic problem

## Example Entry

| Defect ID | File          | Line(s) | Category | Description                                | Severity |
|-----------|---------------|---------|----------|--------------------------------------------|----------|
| 1 | Checkout.java | 17      | CS       | Variable bookList misleading - Map not List | Medium |
| 2 | Book.java     | 107     | FD       | Magic number 100 should be totalCopies      | High |

## Notes
- Be specific with line numbers
- Provide clear, actionable descriptions
- Consider: readability, maintainability, correctness, performance, security
- Focus on issues that impact code quality or functionality
