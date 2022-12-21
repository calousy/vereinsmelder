package org.meisl.vereinsmelder.views.login;

import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.meisl.vereinsmelder.security.AuthenticatedUser;
import org.meisl.vereinsmelder.views.MainLayout;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login", layout = MainLayout.class)
public class LoginView extends LoginOverlay implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;

    public LoginView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setAdditionalInformation(null);
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Vereinsmelder");
        //i18n.getHeader().setDescription("Login using user/user or admin/admin");
        LoginI18n.Form i18nForm = i18n.getForm();
        i18nForm.setPassword("Passwort");
        i18nForm.setUsername("Benutzername oder E-Mail");
        i18nForm.setForgotPassword("Passwort vergessen");
        i18nForm.setTitle("Anmelden");

        setI18n(i18n);

        setForgotPasswordButtonVisible(true);
        addForgotPasswordListener(listener -> {
            getUI().ifPresent(ui -> ui.navigate(PasswordForgottenView.class));
        });
        setOpened(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
            setOpened(false);
            event.forwardTo("");
        }

        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}
