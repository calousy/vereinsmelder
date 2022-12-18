package org.meisl.vereinsmelder.data.service.exception;

public class UsernameExistsException extends Throwable {
    public UsernameExistsException(String username) {
        super("Benutzer '"+username+"' ist bereits registriert!");
    }
}
