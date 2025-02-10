package org.wildcodeschool.myblog.exception;

public class InvalidCategoryException extends RuntimeException {
    public InvalidCategoryException(String message) {
        super(message);
    }
}
