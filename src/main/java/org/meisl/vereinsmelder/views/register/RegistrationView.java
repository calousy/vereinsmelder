package org.meisl.vereinsmelder.views.register;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.meisl.vereinsmelder.data.entity.Club;
import org.meisl.vereinsmelder.data.entity.User;
import org.meisl.vereinsmelder.data.service.ClubService;
import org.meisl.vereinsmelder.data.service.UserService;
import org.meisl.vereinsmelder.data.service.exception.EmailExistsException;
import org.meisl.vereinsmelder.data.service.exception.UsernameExistsException;
import org.meisl.vereinsmelder.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@AnonymousAllowed
@PageTitle("Registrieren")
@Route(value = "register", layout = MainLayout.class)
public class RegistrationView extends VerticalLayout {

    private final Binder<User> userBinder = new Binder<>();

    private final User user = new User();

    public RegistrationView(@Autowired UserService userService, ClubService clubService) {
        FormLayout fl = new FormLayout();
        TextField usernameTf = new TextField();
        TextField firstnameTf = new TextField();
        TextField lastnameTf = new TextField();
        EmailField email = new EmailField();
        Select<Club> clubSelect = new Select<>();
        Page<Club> list = clubService.list(Pageable.unpaged());
        clubSelect.setItems(list.stream().toList());
        clubSelect.setItemLabelGenerator(Club::getName);
        fl.addFormItem(usernameTf, "Benutzername");
        fl.addFormItem(email, "E-Mail");
        fl.addFormItem(firstnameTf, "Vorname");
        fl.addFormItem(lastnameTf, "Nachname");
        fl.addFormItem(clubSelect, "Mein Verein");

        PasswordField passwordField = new PasswordField();
        fl.addFormItem(passwordField, "Passwort");
        PasswordField passwordField2 = new PasswordField();
        fl.addFormItem(passwordField2, "Passwort wiederholen");

        Button saveButton = new Button("Registrieren", VaadinIcon.DISC.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(listener -> {
            if (!passwordField.getValue().equals(passwordField2.getValue())) {
                passwordField2.setErrorMessage("Passwörter stimmen nicht überein");
                passwordField2.setInvalid(true);
                return;
            }
            BinderValidationStatus<User> validate = userBinder.validate();
            if (validate.hasErrors()) {
                return;
            }
            try {
                userService.register(user, passwordField.getValue());
            } catch (EmailExistsException e) {
                email.setInvalid(true);
                email.setErrorMessage(e.getMessage());
            } catch (UsernameExistsException e) {
                usernameTf.setInvalid(true);
                usernameTf.setErrorMessage(e.getMessage());
            }
        });

        add(fl, saveButton);

        userBinder.forField(usernameTf).asRequired().bind(User::getUsername, User::setUsername);
        userBinder.bind(firstnameTf, User::getFirstname, User::setFirstname);
        userBinder.bind(lastnameTf, User::getLastname, User::setLastname);
        userBinder.forField(email).withValidator(new EmailValidator(
                "This doesn't look like a valid email address"))
                .asRequired().bind(User::getEmail, User::setEmail);
        userBinder.forField(clubSelect).bind(User::getManagerOf, User::setManagerOf);

        userBinder.setBean(user);
    }
}
