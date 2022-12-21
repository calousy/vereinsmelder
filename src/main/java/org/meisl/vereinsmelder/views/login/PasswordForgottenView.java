package org.meisl.vereinsmelder.views.login;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.meisl.vereinsmelder.views.MainLayout;

@AnonymousAllowed
@PageTitle("Passwort vergessen")
@Route(value = "password", layout = MainLayout.class)
public class PasswordForgottenView extends VerticalLayout {
    public PasswordForgottenView() {
        add(new TextField("Benutzername oder E-Mail"));
        Button sendBtn = new Button("Jetzt senden", listener -> {
            Notification.show("Not yet implemented"); // TODO
        });
        add(sendBtn);
    }
}
