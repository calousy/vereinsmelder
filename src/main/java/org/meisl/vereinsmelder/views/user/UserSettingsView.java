package org.meisl.vereinsmelder.views.user;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.meisl.vereinsmelder.data.entity.Club;
import org.meisl.vereinsmelder.data.entity.User;
import org.meisl.vereinsmelder.data.service.UserService;
import org.meisl.vereinsmelder.security.AuthenticatedUser;
import org.meisl.vereinsmelder.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.security.PermitAll;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@PermitAll
@PageTitle("Benutzereinstellungen")
@Route(value = "user-settings", layout = MainLayout.class)
public class UserSettingsView extends VerticalLayout {

    private final Binder<User> userBinder = new Binder<>();
    private final AuthenticatedUser authenticatedUser;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private final TabSheet tabSheet = new TabSheet();

    public UserSettingsView(@Autowired AuthenticatedUser authenticatedUser,
                            UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

        User user = authenticatedUser.get().get();

        tabSheet.add("Einstellungen", getSettingsForm(user));
        tabSheet.add("Passwort ändern", getPasswordForm(user));
        tabSheet.setSizeFull();
        addAndExpand(tabSheet);
    }

    private VerticalLayout getSettingsForm(User user) {
        FormLayout fl = new FormLayout();
        TextField usernameTf = new TextField();
        TextField firstnameTf = new TextField();
        TextField lastnameTf = new TextField();
        EmailField email = new EmailField();
        TextField clubSelect = new TextField();
        fl.setColspan(usernameTf, 2);
        fl.addFormItem(usernameTf, "Benutzername");
        fl.addFormItem(firstnameTf, "Vorname");
        fl.addFormItem(lastnameTf, "Nachname");
        fl.addFormItem(email, "E-Mail");
        fl.addFormItem(clubSelect, "Mein Verein");

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

        userBinder.bindReadOnly(usernameTf, User::getUsername);
        userBinder.bind(firstnameTf, User::getFirstname, User::setFirstname);
        userBinder.bind(lastnameTf, User::getLastname, User::setLastname);
        userBinder.bind(email, User::getEmail, User::setEmail);
        userBinder.bindReadOnly(clubSelect, ((ValueProvider<User, String>) user1 -> {
            Club managerOf = user1.getManagerOf();
            return managerOf != null ? managerOf.getName() : "";
        }));

        userBinder.setBean(user);

        return new VerticalLayout(fl, new HorizontalLayout(resetButton, saveButton));
    }

    private VerticalLayout getPasswordForm(User user) {
        PasswordField passwordField = new PasswordField();
        PasswordField newPasswordField1 = new PasswordField();
        PasswordField newPasswordField2 = new PasswordField();
        FormLayout fl = new FormLayout();
        FormLayout.FormItem oldPasswordFormItem = fl.addFormItem(passwordField, "Altes Passwort");
        fl.setColspan(oldPasswordFormItem, 2);
        fl.addFormItem(newPasswordField1, "Neues Passwort");
        fl.addFormItem(newPasswordField2, "Neues Passwort wiederholen");

        passwordField.setRequired(true);
        newPasswordField1.setRequired(true);
        newPasswordField2.setRequired(true);

        Button saveButton = new Button("Speichern", VaadinIcon.DISC.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(listener -> {
            if (!newPasswordField1.getValue().equals(newPasswordField2.getValue())) {
                newPasswordField2.setErrorMessage("nicht identisch");
                newPasswordField2.setInvalid(true);
                return;
            }
            boolean matches = passwordEncoder.matches(newPasswordField1.getValue(), user.getHashedPassword());
            if (!matches) {
                passwordField.setErrorMessage("Altes Passwort falsch");
                passwordField.setInvalid(true);
                return;
            }
            user.setHashedPassword(passwordEncoder.encode(newPasswordField1.getValue()));
            userService.update(user);
        });

        Button resetButton = new Button("Zurücksetzen");
        resetButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        resetButton.addClickListener(listener -> {
            Optional<User> loadedUser = userService.get(user.getId());
            loadedUser.ifPresent(userBinder::setBean);
        });

        return new VerticalLayout(fl, saveButton);
    }
}
