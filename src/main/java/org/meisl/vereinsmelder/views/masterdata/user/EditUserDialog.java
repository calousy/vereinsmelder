package org.meisl.vereinsmelder.views.masterdata.user;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.meisl.vereinsmelder.data.entity.Club;
import org.meisl.vereinsmelder.data.entity.User;

import java.util.List;
import java.util.UUID;

public class EditUserDialog extends ConfirmDialog {

    private final User user;

    public User getUser() {
        return user;
    }

    public EditUserDialog(User user, List<Club> clubList) {
        this.user = user;

        setCancelable(true);

        UUID id = user.getId();
        boolean isNewUser = id == null;
        setHeader(isNewUser ? "Benutzer erstellen" : "Benutzer bearbeiten");

        FormLayout fl = new FormLayout();
        TextField usernameTf = new TextField();
        TextField firstnameTf = new TextField();
        TextField lastnameTf = new TextField();
        EmailField email = new EmailField();
        Select<Club> clubSelect = new Select<>();
        clubSelect.setLabel("Verein");
        clubSelect.setItems(clubList);
        clubSelect.setItemLabelGenerator(Club::getName);
        fl.addFormItem(usernameTf, "Benutzername");
        fl.addFormItem(firstnameTf, "Vorname");
        fl.addFormItem(lastnameTf, "Nachname");
        fl.addFormItem(email, "E-Mail");
        fl.add(clubSelect);

        //PasswordField passwordField = new PasswordField();
        //fl.addFormItem(passwordField, "Passwort");

        setText(fl);

        Binder<User> userBinder = new Binder<>();
        userBinder.bind(usernameTf, User::getUsername, User::setUsername);
        userBinder.bind(firstnameTf, User::getFirstname, User::setFirstname);
        userBinder.bind(lastnameTf, User::getLastname, User::setLastname);
        userBinder.bind(email, User::getEmail, User::setEmail);
        userBinder.bind(clubSelect, User::getManagerOf, User::setManagerOf);
        //userBinder.bind(passwordField, User::getHashedPassword, User::setHashedPassword);

        userBinder.setBean(user);
    }
}
