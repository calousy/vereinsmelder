package org.meisl.vereinsmelder.data.service.exception;

public class EmailExistsException extends Throwable {
    public EmailExistsException(String email) {
        super("E-Mail '" + email + "' ist bereits registriert!");
    }
}
