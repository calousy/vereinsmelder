package org.meisl.vereinsmelder.views.masterdata.club;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.meisl.vereinsmelder.data.entity.Club;
import org.meisl.vereinsmelder.data.entity.User;
import org.meisl.vereinsmelder.data.service.ClubService;
import org.meisl.vereinsmelder.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import jakarta.annotation.security.RolesAllowed;

import java.util.HashSet;
import java.util.Set;

@PageTitle("Stammdaten - Vereine")
@Route(value = "stammdaten/vereine", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class ClubsView extends VerticalLayout {

    private final Grid<Club> grid;
    private final ClubService clubService;

    public ClubsView(@Autowired ClubService clubService) {
        this.clubService = clubService;

        grid = new Grid<>();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        grid.addColumn(Club::getName).setHeader("Name");
        grid.addComponentColumn(club -> {
                    Set<User> managers = club.getManagers() == null ? new HashSet<>() : club.getManagers();
                    String join = String.join("; ", managers.stream().map(User::getName).toList());
                    return new Label(join);
                })
                .setHeader("Managers");
        grid.addComponentColumn(club -> {
            Button button = new Button(VaadinIcon.EDIT.create());
            button.addThemeVariants(ButtonVariant.LUMO_SMALL);
            button.addClickListener(listener -> editClub(club));
            return button;
        }).setFlexGrow(0).setAutoWidth(true);
        grid.addComponentColumn(club -> {
            Button button = new Button(VaadinIcon.TRASH.create());
            button.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            return button;
        }).setFlexGrow(0).setAutoWidth(true);

        CallbackDataProvider<Club, Void> objectVoidCallbackDataProvider =
                DataProvider.fromCallbacks(query -> clubService.list(PageRequest.of(query.getPage(),
                                query.getPageSize())).get(),
                        query -> clubService.count());
        grid.setItems(objectVoidCallbackDataProvider);

        Button addButton = new Button(VaadinIcon.PLUS.create());
        addButton.addClickListener(event -> editClub(new Club()));

        add(addButton, grid);
    }

    private void editClub(Club club) {
        EditClubDialog editClubDialog = new EditClubDialog(club);
        editClubDialog.addConfirmListener(confirmEvent -> {
           clubService.update(editClubDialog.getClub());
           grid.getDataProvider().refreshAll();
        });
        editClubDialog.open();
    }
}
