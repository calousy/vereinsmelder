package org.meisl.vereinsmelder.views.user;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.meisl.vereinsmelder.data.entity.Club;
import org.meisl.vereinsmelder.data.entity.User;
import org.meisl.vereinsmelder.data.service.UserService;
import org.meisl.vereinsmelder.security.AuthenticatedUser;
import org.meisl.vereinsmelder.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;
import java.util.Optional;

@PermitAll
@PageTitle("Benutzereinstellungen")
@Route(value = "user-settings", layout = MainLayout.class)
public class UserSettingsView extends VerticalLayout {

    private final Binder<User> userBinder = new Binder<>();
    private final AuthenticatedUser authenticatedUser;
    public UserSettingsView(@Autowired AuthenticatedUser authenticatedUser,
                            UserService userService) {
        this.authenticatedUser = authenticatedUser;

        User user = authenticatedUser.get().get();

        FormLayout fl = new FormLayout();
        TextField usernameTf = new TextField();
        TextField firstnameTf = new TextField();
        TextField lastnameTf = new TextField();
        EmailField email = new EmailField();
        TextField clubSelect = new TextField();
        fl.addFormItem(usernameTf, "Benutzername");
        fl.addFormItem(firstnameTf, "Vorname");
        fl.addFormItem(lastnameTf, "Nachname");
        fl.addFormItem(email, "E-Mail");
        fl.addFormItem(clubSelect, "Mein Verein");

        PasswordField passwordField = new PasswordField();
        fl.addFormItem(passwordField, "Passwort");


        Button saveButton = new Button("Speichern", VaadinIcon.DISC.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(listener -> {
            userService.update(user);
        });

        Button resetButton = new Button("Zurücksetzen");
        resetButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        resetButton.addClickListener(listener -> {
            Optional<User> loadedUser = userService.get(user.getId());
            loadedUser.ifPresent(userBinder::setBean);
        });

        add(fl, saveButton);

        userBinder.bindReadOnly(usernameTf, User::getUsername);
        userBinder.bind(firstnameTf, User::getFirstname, User::setFirstname);
        userBinder.bind(lastnameTf, User::getLastname, User::setLastname);
        userBinder.bind(email, User::getEmail, User::setEmail);
        userBinder.bindReadOnly(clubSelect, ((ValueProvider<User, String>) user1 -> {
            Club managerOf = user1.getManagerOf();
            return managerOf != null ? managerOf.getName() : "";
        }));
        //userBinder.bind(passwordField, User::getHashedPassword, User::setHashedPassword);

        userBinder.setBean(user);
    }
}
