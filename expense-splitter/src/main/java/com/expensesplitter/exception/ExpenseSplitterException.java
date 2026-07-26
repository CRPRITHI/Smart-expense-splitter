package com.expensesplitter.exception;

/**
 * Base checked exception for all application-specific error conditions.
 * Keeping a common base lets calling code catch broadly when it just
 * needs to report "something went wrong with the domain logic".
 */
public class ExpenseSplitterException extends Exception {

    public ExpenseSplitterException(String message) {
        super(message);
    }

    public ExpenseSplitterException(String message, Throwable cause) {
        super(message, cause);
    }
}
