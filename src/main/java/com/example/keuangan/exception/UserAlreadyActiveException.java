package com.example.keuangan.exception;

public class UserAlreadyActiveException extends RuntimeException {

    public UserAlreadyActiveException() {
        super("Akun sedang aktif di perangkat lain");
    }

    public UserAlreadyActiveException(String message) {
        super(message);
    }
}
