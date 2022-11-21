package org.meisl.vereinsmelder.views.competition;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.meisl.vereinsmelder.data.Role;
import org.meisl.vereinsmelder.data.entity.Competition;
import org.meisl.vereinsmelder.data.service.CompetitionService;
import org.meisl.vereinsmelder.security.AuthenticatedUser;
import org.meisl.vereinsmelder.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Wettbewerbe")
@Route(value = "wettbewerbe", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class CompetitionsView extends VerticalLayout {

    private final CompetitionService competitionService;
    private final Grid<Competition> grid;


    public CompetitionsView(@Autowired AuthenticatedUser authenticatedUser,
                            CompetitionService competitionService) {
        this.competitionService = competitionService;

        grid = new Grid<>();
        grid.setSizeFull();
        grid.addItemClickListener(listener ->
                grid.getUI().ifPresent(ui -> ui.navigate(CompetitionView.class, listener.getItem().getId().toString())));
        grid.addColumn(Competition::getDate).setHeader("Datum");
        grid.addColumn(Competition::getName).setHeader("Name");
        grid.addColumn(Competition::getCategory).setHeader("Kategorie");

        authenticatedUser.get().ifPresent(user -> {
            if (!user.getRoles().contains(Role.ADMIN)) {
                return;
            }
            grid.addComponentColumn(competition -> {
                Button button = new Button(VaadinIcon.EDIT.create());
                button.addThemeVariants(ButtonVariant.LUMO_SMALL);
                button.addClickListener(listener -> {
                    editCompetition(competition);
                });
                return button;
            });

            Button addButton = new Button(VaadinIcon.PLUS.create());
            add(addButton);
            addButton.addClickListener(listener -> editCompetition(new Competition()));
        });
        add(grid);

        CallbackDataProvider<Competition, Void> objectVoidCallbackDataProvider = DataProvider.fromCallbacks(query ->
                        competitionService.list(PageRequest.of(query.getPage(), query.getPageSize())).get()
                , query ->
                        competitionService.count());

        grid.setItems(objectVoidCallbackDataProvider);
    }

    private void editCompetition(Competition competition) {
        EditCompetitionDialog dialog = new EditCompetitionDialog(competition);
        dialog.addConfirmListener(confirmEvent -> {
            competitionService.update(competition);
            grid.getDataProvider().refreshAll();
        });
        dialog.open();
    }

}
