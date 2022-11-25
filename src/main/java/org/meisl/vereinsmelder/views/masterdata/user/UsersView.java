package org.meisl.vereinsmelder.views.masterdata.user;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.meisl.vereinsmelder.data.entity.Club;
import org.meisl.vereinsmelder.data.entity.User;
import org.meisl.vereinsmelder.data.service.ClubService;
import org.meisl.vereinsmelder.data.service.UserService;
import org.meisl.vereinsmelder.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@PageTitle("Stammdaten - Benutzer")
@Route(value = "stammdaten/benutzer", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UsersView extends VerticalLayout {

    private final Grid<User> grid;
    private final UserService userService;
    private final ClubService clubService;

    public UsersView(@Autowired UserService userService, ClubService clubService) {
        this.userService = userService;
        this.clubService = clubService;

        grid = new Grid<>();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        grid.addColumn(User::getUsername).setHeader("Benutzername");
        grid.addColumn(User::getName).setHeader("Name");
        grid.addColumn(User::getEmail).setHeader("E-Mail");
        grid.addColumn(User::getRoles).setHeader("Rollen");
        grid.addColumn(User::getManagerOf).setHeader("Verein");
        grid.addComponentColumn(user -> {
            Button button = new Button(VaadinIcon.EDIT.create());
            button.addThemeVariants(ButtonVariant.LUMO_SMALL);
            button.addClickListener(listener -> editUser(user));
            return button;
        }).setFlexGrow(0).setAutoWidth(true);
        grid.addComponentColumn(user -> {
            Button button = new Button(VaadinIcon.TRASH.create());
            button.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            return button;
        }).setFlexGrow(0).setAutoWidth(true);

        Button addButton = new Button(VaadinIcon.PLUS.create());
        addButton.addClickListener(event -> editUser(new User()));

        add(addButton, grid);

        CallbackDataProvider<User, Void> objectVoidCallbackDataProvider = DataProvider.fromCallbacks(query ->
                        userService.list(PageRequest.of(query.getPage(), query.getPageSize())).get(),
                query -> userService.count());

        grid.setItems(objectVoidCallbackDataProvider);
    }

    private void editUser(User user) {
        List<Club> clubList = clubService.list(Pageable.unpaged()).stream().toList();
        EditUserDialog dialog = new EditUserDialog(user, clubList);
        dialog.addConfirmListener(listener -> {
            userService.update(dialog.getUser());
            grid.getDataProvider().refreshAll();
        });
        dialog.open();
    }
}
