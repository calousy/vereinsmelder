package org.meisl.vereinsmelder.views.myclub;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.meisl.vereinsmelder.data.Role;
import org.meisl.vereinsmelder.data.entity.User;
import org.meisl.vereinsmelder.data.service.UserService;
import org.meisl.vereinsmelder.security.AuthenticatedUser;
import org.meisl.vereinsmelder.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.security.RolesAllowed;

@PageTitle("Mein Verein")
@Route(value = "my-club", layout = MainLayout.class)
@RolesAllowed({"SUPERMANAGER", "ADMIN"})
public class MyClub extends VerticalLayout {
    private final AuthenticatedUser authenticatedUser;
    private final UserService userService;

    public MyClub(@Autowired AuthenticatedUser authenticatedUser, UserService userService) {
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;

        H2 h2 = new H2(authenticatedUser.getManagedClub().getName());
        h2.addClassName(LumoUtility.Margin.Top.NONE);
        TabSheet tabSheet = new TabSheet();
        tabSheet.add("Benutzer", getUserManagement());
        tabSheet.add("Kontakt", new ContactView());
        tabSheet.setWidthFull();

        add(h2, tabSheet);
    }

    private Component getUserManagement() {

        CallbackDataProvider<User, Void> dataProvider = DataProvider.fromCallbacks(q -> userService.findByClub(authenticatedUser.getManagedClub(),
                VaadinSpringDataHelpers.toSpringPageRequest(q)).stream(), q -> (int)userService.findByClub(authenticatedUser.getManagedClub(),
                VaadinSpringDataHelpers.toSpringPageRequest(q)).stream().count());

        Grid<User> grid = new Grid<>();
        grid.addColumn(User::getLastname).setHeader("Nachname");
        grid.addColumn(User::getFirstname).setHeader("Vorname");
        grid.addComponentColumn(x -> {
            Checkbox checkbox = new Checkbox();
            boolean isManager = x.getRoles().contains(Role.MANAGER);
            checkbox.setValue(isManager);
            checkbox.setReadOnly(true);
            Button toggleBtn = new Button(VaadinIcon.EXCHANGE.create());
            toggleBtn.setTooltipText("Rolle " + (isManager ? "entfernen" : "zuweisen"));
            toggleBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            toggleBtn.addClickListener(listener -> {
                userService.toggleRole(x, Role.MANAGER);
                dataProvider.refreshAll();
            });
            HorizontalLayout horizontalLayout = new HorizontalLayout(checkbox, toggleBtn);
            horizontalLayout.setAlignItems(Alignment.CENTER);
            return horizontalLayout;
        }).setHeader("Manager");
        grid.addComponentColumn(x -> {
            Checkbox checkbox = new Checkbox();
            boolean isSuperManager = x.getRoles().contains(Role.SUPERMANAGER);
            checkbox.setValue(isSuperManager);
            checkbox.setReadOnly(true);
            Button toggleBtn = new Button(VaadinIcon.EXCHANGE.create());
            toggleBtn.setTooltipText("Rolle " + (isSuperManager ? "entfernen" : "zuweisen"));
            toggleBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            toggleBtn.addClickListener(listener -> {
                userService.toggleRole(x, Role.SUPERMANAGER);
                dataProvider.refreshAll();
            });
            HorizontalLayout horizontalLayout = new HorizontalLayout(checkbox, toggleBtn);
            horizontalLayout.setAlignItems(Alignment.CENTER);
            return horizontalLayout;
        }).setHeader("Super-Manager");

        grid.setItems(dataProvider);

        Html html = new Html("<p><u><i>Manager:</i></u> Können Wettbewerbe melden und abmelden.<br/>" +
                "<u><i>Super-Manager:</i></u> Können Benutzer zu Managern machen. Können Wettbewerbe melden und abmelden.</p>");

        return new VerticalLayout(html, grid);
    }
}
