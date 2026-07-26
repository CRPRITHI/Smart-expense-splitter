package com.expensesplitter.exception;

public class UserNotFoundException extends ExpenseSplitterException {

    public UserNotFoundException(int userId) {
        super("User not found with ID: " + userId);
    }

    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }
}
