# Smart Expense Splitter (Splitwise Clone)

A Java console application for recording shared group expenses, calculating
net balances, and simplifying settlements down to the minimum number of
payments — like Splitwise.

## Tech Stack
- Java 17
- JDBC + MySQL
- OOP (models, DAO, service layers)
- Java Collections Framework (`HashMap`, `PriorityQueue`, `List`)
- Basic DSA (greedy debt-simplification algorithm using two max-heaps)
- Custom checked exceptions
- Maven (build) / Git (version control)

## Project Structure
```
expense-splitter/
├── pom.xml
├── sql/
│   └── schema.sql              # MySQL schema (run this first)
├── lib/                        # (optional) manual JDBC driver jar if not using Maven
└── src/main/java/com/expensesplitter/
    ├── Main.java                # CLI entry point / menu
    ├── db/
    │   └── DBConnection.java    # JDBC connection manager
    ├── model/
    │   ├── User.java
    │   ├── Group.java
    │   ├── Expense.java
    │   ├── Split.java
    │   ├── SplitType.java
    │   └── Transaction.java     # output of debt simplification
    ├── dao/
    │   ├── UserDAO.java
    │   ├── GroupDAO.java
    │   ├── ExpenseDAO.java
    │   └── SettlementDAO.java
    ├── service/
    │   ├── ExpenseService.java   # validation + orchestration
    │   ├── BalanceService.java   # net balance calculation
    │   └── SettlementService.java# greedy debt simplification
    ├── util/
    │   ├── SplitCalculator.java  # EQUAL / EXACT / PERCENT split logic
    │   └── InputValidator.java
    └── exception/
        ├── ExpenseSplitterException.java
        ├── UserNotFoundException.java
        ├── InvalidSplitException.java
        ├── InsufficientDataException.java
        └── ValidationException.java
```

## 1. Set up MySQL

```sql
-- from a MySQL client / MySQL Workbench / mysql CLI
SOURCE sql/schema.sql;
```

This creates the `expense_splitter` database with tables: `users`,
`expense_groups`, `group_members`, `expenses`, `expense_splits`, `settlements`.

## 2. Configure the connection

`DBConnection.java` defaults to:
```
URL:      jdbc:mysql://localhost:3306/expense_splitter
USER:     root
PASSWORD: root
```

Override any of these with environment variables before running, instead of
editing source:
```bash
export DB_URL="jdbc:mysql://localhost:3306/expense_splitter?useSSL=false&serverTimezone=UTC"
export DB_USER="root"
export DB_PASSWORD="your_password"
```

## 3. Build & Run

### Option A — Maven (recommended)
```bash
mvn clean package
java -jar target/expense-splitter.jar
```

### Option B — Plain javac (no Maven)
1. Download `mysql-connector-j-8.3.0.jar` into `lib/`.
2. Compile:
   ```bash
   javac -cp "lib/*" -d out $(find src -name "*.java")
   ```
3. Run:
   ```bash
   java -cp "out:lib/*" com.expensesplitter.Main
   ```
   (On Windows use `;` instead of `:` in the classpath.)

## 4. Using the app

The console menu lets you:
1. Add users
2. List users
3. Create a group
4. Add a user to a group
5. Add an expense (EQUAL / EXACT / PERCENT split)
6. View all expenses in a group
7. View each member's net balance
8. Settle up — computes the minimum set of payments needed to clear all
   balances, and optionally records them to the `settlements` table

### Example walkthrough
```
1 -> Add User        (Alice, alice@example.com)   => user 1
1 -> Add User        (Bob,   bob@example.com)     => user 2
1 -> Add User        (Charlie, charlie@example.com) => user 3
3 -> Create Group     "Goa Trip"                  => group 1
4 -> Add Member        group 1, user 1
4 -> Add Member        group 1, user 2
4 -> Add Member        group 1, user 3
5 -> Add Expense        group 1, paid by 1, "Hotel", 3000, EQUAL
7 -> View Balances      group 1
8 -> Settle Up          group 1
```

With one EQUAL expense of 3000 paid by Alice among 3 people:
- Alice is owed 2000 (paid 3000, owes 1000 of her own share)
- Bob owes 1000
- Charlie owes 1000

`Settle Up` will produce exactly 2 transactions:
```
User 2 pays User 1: 1000.00
User 3 pays User 1: 1000.00
```

## Design notes

- **Transactions**: `ExpenseDAO.create()` writes the expense row and every
  split row inside a single JDBC transaction (commit/rollback), so a failure
  partway through never leaves an expense without its splits.
- **Debt simplification**: `SettlementService` uses two `PriorityQueue`
  max-heaps (creditors and debtors). On each iteration it matches the
  largest creditor with the largest debtor and settles the smaller of the
  two amounts, pushing any remainder back onto the heap. This is the
  standard greedy approximation for "optimal account balancing" and runs in
  O(n log n).
- **Split types**: `SplitCalculator` supports EQUAL (with remainder cents
  distributed fairly), EXACT (caller-specified amounts, validated to sum to
  the total), and PERCENT (caller-specified percentages, validated to sum to
  100).
- **Exception handling**: all expected domain errors (bad input, missing
  user, unbalanced splits, empty group) raise a specific checked exception
  under `ExpenseSplitterException`, caught and shown as a friendly message
  in `Main`, while unexpected `SQLException`s are reported separately.
