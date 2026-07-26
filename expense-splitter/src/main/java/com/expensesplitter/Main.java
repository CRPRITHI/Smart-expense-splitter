package com.expensesplitter;

import com.expensesplitter.dao.GroupDAO;
import com.expensesplitter.dao.SettlementDAO;
import com.expensesplitter.dao.UserDAO;
import com.expensesplitter.db.DBConnection;
import com.expensesplitter.exception.ExpenseSplitterException;
import com.expensesplitter.exception.ValidationException;
import com.expensesplitter.model.Expense;
import com.expensesplitter.model.Group;
import com.expensesplitter.model.Settlement;
import com.expensesplitter.model.Split;
import com.expensesplitter.model.Transaction;
import com.expensesplitter.model.User;
import com.expensesplitter.service.BalanceService;
import com.expensesplitter.service.ExpenseService;
import com.expensesplitter.service.SettlementService;
import com.expensesplitter.util.InputValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Console entry point for the Smart Expense Splitter application.
 * Provides a simple text menu to exercise every feature of the system.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final UserDAO userDAO = new UserDAO();
    private static final GroupDAO groupDAO = new GroupDAO();
    private static final SettlementDAO settlementDAO = new SettlementDAO();
    private static final ExpenseService expenseService = new ExpenseService();
    private static final BalanceService balanceService = new BalanceService();
    private static final SettlementService settlementService = new SettlementService();

    public static void main(String[] args) {
        System.out.println("=== Smart Expense Splitter ===");
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> createUser();
                    case "2" -> listUsers();
                    case "3" -> createGroup();
                    case "4" -> addMemberToGroup();
                    case "5" -> addExpense();
                    case "6" -> viewGroupExpenses();
                    case "7" -> viewBalances();
                    case "8" -> settleUp();
                    case "0" -> {
                        running = false;
                        System.out.println("Goodbye!");
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (ExpenseSplitterException e) {
                // Domain-level errors: expected, show a friendly message.
                System.out.println("Error: " + e.getMessage());
            } catch (SQLException e) {
                // Infrastructure errors: show details to help debugging.
                System.out.println("Database error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }

      
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n------------------------------------------");
        System.out.println("1. Add User");
        System.out.println("2. List Users");
        System.out.println("3. Create Group");
        System.out.println("4. Add Member to Group");
        System.out.println("5. Add Expense");
        System.out.println("6. View Group Expenses");
        System.out.println("7. View Group Balances");
        System.out.println("8. Settle Up (Simplify Debts)");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private static void createUser() throws SQLException, ExpenseSplitterException {
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        if (!InputValidator.isNonEmpty(name)) {
            throw new ValidationException("Name cannot be empty.");
        }
        if (!InputValidator.isValidEmail(email)) {
            throw new ValidationException("Invalid email format.");
        }

       User user = new User(name, email, "");
if (userDAO.addUser(user)) {
    System.out.println("User added successfully.");
} else {
    System.out.println("Failed to add user.");
}
    }

    private static void listUsers() throws SQLException {
        List<User> users = userDAO.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users yet.");
            return;
        }
        users.forEach(System.out::println);
    }

    private static void createGroup() throws SQLException {
        System.out.print("Group name: ");
        String name = scanner.nextLine().trim();
      Group group = new Group(name);

boolean result = groupDAO.createGroup(group);

if(result) {
    System.out.println("Group created successfully");
} else {
    System.out.println("Group creation failed");
}
        System.out.println("Created: " + group);
    }

    private static void addMemberToGroup() throws SQLException {
        int groupId = readInt("Group ID: ");
        int userId = readInt("User ID: ");
        groupDAO.addMember(groupId, userId);
        System.out.println("Added user " + userId + " to group " + groupId);
    }

    private static void addExpense() throws SQLException, ExpenseSplitterException {
        int groupId = readInt("Group ID: ");
        int paidBy = readInt("Paid by (User ID): ");
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        BigDecimal amount = readAmount("Amount: ");

        System.out.print("Split type (EQUAL / EXACT / PERCENT): ");
        String type = scanner.nextLine().trim().toUpperCase();

        Expense expense;
        switch (type) {
            case "EQUAL" -> expense = expenseService.addEqualExpense(groupId, paidBy, description, amount);
            case "EXACT" -> expense = expenseService.addExactExpense(groupId, paidBy, description, amount,
                    collectShares(groupId, "exact amount"));
            case "PERCENT" -> expense = expenseService.addPercentExpense(groupId, paidBy, description, amount,
                    collectShares(groupId, "percentage"));
            default -> throw new ValidationException("Unknown split type: " + type);
        }

        System.out.println("Saved: " + expense);
        for (Split s : expense.getSplits()) {
            System.out.println("   " + s);
        }
    }

    private static Map<Integer, BigDecimal> collectShares(int groupId, String label) throws SQLException {
        Map<Integer, BigDecimal> shares = new HashMap<>();
       List<User> members = groupDAO.getGroupMembers(groupId);
        System.out.println("Enter " + label + " for each member (group members: " + members + "):");
        for (User u : members) {
           BigDecimal value = readAmount("  " + u.getName() + " (User " + u.getId() + "): ");
            System.out.println("DEBUG Percentage = " + value);
shares.put(u.getId(), value);
        }
        return shares;
    }

    private static void viewGroupExpenses() throws SQLException {
        int groupId = readInt("Group ID: ");
        List<Expense> expenses = expenseService.getExpensesForGroup(groupId);
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded for this group.");
            return;
        }
        for (Expense e : expenses) {
            System.out.println(e);
            for (Split s : e.getSplits()) {
                System.out.println("   " + s);
            }
        }
    }

    private static void viewBalances() throws SQLException, ExpenseSplitterException {
        int groupId = readInt("Group ID: ");
        Map<Integer, BigDecimal> balances = balanceService.calculateGroupBalances(groupId);

        System.out.println("Net balances:");
        for (Map.Entry<Integer, BigDecimal> entry : balances.entrySet()) {
            String status = entry.getValue().compareTo(BigDecimal.ZERO) >= 0 ? "is owed" : "owes";
            System.out.printf("  User %d %s %.2f%n", entry.getKey(), status, entry.getValue().abs());
        }
    }

    private static void settleUp() throws SQLException, ExpenseSplitterException {
        int groupId = readInt("Group ID: ");
        Map<Integer, BigDecimal> balances = balanceService.calculateGroupBalances(groupId);
        List<Transaction> transactions = settlementService.simplifyDebts(balances);

        if (transactions.isEmpty()) {
            System.out.println("Everyone is already settled up!");
            return;
        }

        System.out.println("Simplified settlement plan (" + transactions.size() + " transaction(s)):");
        for (Transaction t : transactions) {
            System.out.println("  " + t);
        }

        System.out.print("Record these transactions as settled in the database? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            for (Transaction t : transactions) {
                Settlement settlement = new Settlement(
             t.getPayerId(),
             t.getReceiverId(),
             t.getAmount()
);

settlementDAO.addSettlement(settlement);       
            System.out.println("Settlements recorded.");
        }
    }
      }

    // ---------------------------------------------------------------
    // Small input helpers with basic exception handling for bad input
    // ---------------------------------------------------------------

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static BigDecimal readAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return new BigDecimal(line).setScale(2, RoundingMode.HALF_UP);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid amount (e.g. 250.00).");
            }
        }
    }
}
