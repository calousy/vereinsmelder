package org.meisl.vereinsmelder.views.competition;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.meisl.vereinsmelder.components.ClubSelectionDialog;
import org.meisl.vereinsmelder.data.Role;
import org.meisl.vereinsmelder.data.entity.Club;
import org.meisl.vereinsmelder.data.entity.Competition;
import org.meisl.vereinsmelder.data.entity.Team;
import org.meisl.vereinsmelder.data.entity.User;
import org.meisl.vereinsmelder.data.service.ClubService;
import org.meisl.vereinsmelder.data.service.CompetitionService;
import org.meisl.vereinsmelder.data.service.TeamService;
import org.meisl.vereinsmelder.security.AuthenticatedUser;
import org.meisl.vereinsmelder.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@PageTitle("Wettbewerb")
@Route(value = "wettbewerb", layout = MainLayout.class)
@AnonymousAllowed
public class CompetitionView extends VerticalLayout implements
        HasUrlParameter<String> {

    private final AuthenticatedUser authenticatedUser;
    private final TeamService teamService;
    private final CompetitionService competitionService;
    private final ClubService clubService;
    private final Grid<Team> grid;
    private final H2 h2;
    private Competition competition;

    public CompetitionView(@Autowired AuthenticatedUser authenticatedUser,
                           TeamService teamService,
                           CompetitionService competitionService,
                           ClubService clubService) {
        this.authenticatedUser = authenticatedUser;
        this.teamService = teamService;
        this.competitionService = competitionService;
        this.clubService = clubService;

        Optional<User> user = authenticatedUser.get();
        Club managerOfClub = user.map(User::getManagerOf).orElse(null);

        setMargin(true);

        HorizontalLayout actionLayout = new HorizontalLayout();
        actionLayout.setAlignItems(Alignment.CENTER);

        if (managerOfClub != null) {
            Button meldenButton = new Button("Mannschaft melden");
            meldenButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            meldenButton.addClickListener(listener -> addTeam(managerOfClub));
            Span span = new Span(managerOfClub.getName());
            span.setClassName(LumoUtility.FontWeight.BOLD);
            actionLayout.add(new Label("Meine Mannschaft "), span, meldenButton);
        }

        if (isAdmin()) {
            Button selectTeamButton = new Button("(Admin) Mannschaft melden");
            selectTeamButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            selectTeamButton.addClickListener(listener -> addTeamFromSelection());
            actionLayout.add(selectTeamButton);
        }

        grid = new Grid<>();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        grid.addColumn(LitRenderer.of("${index + 1}")).setHeader("#")
                .setAutoWidth(true).setFlexGrow(0);

        grid.addColumn(Team::getName).setHeader("Name");
        if (isAdmin() || managerOfClub != null) {
            grid.addComponentColumn(team -> {
                if (isAdmin() || team.getClub().equals(managerOfClub)) {
                    Button abmeldenBtn = new Button("Abmelden");
                    abmeldenBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
                    abmeldenBtn.addClickListener(listener -> removeTeam(team));
                    return abmeldenBtn;
                }
                return new Label("");
            });
        }
        Paragraph p = new Paragraph("Liste gemeldeter Mannschaften - keine Startliste!");

        h2 = new H2();
        h2.addClassNames(LumoUtility.Margin.Vertical.NONE);
        add(h2);
        if (user.isPresent()) {
            add(actionLayout);
        }
        add(p, grid);

        CallbackDataProvider<Team, Void> objectVoidCallbackDataProvider = DataProvider.fromCallbacks(query ->
                        teamService.listByCompetition(competition, PageRequest.of(query.getPage(), query.getPageSize())).get()
                , query ->
                        teamService.listByCompetitionCount(competition));

        grid.setItems(objectVoidCallbackDataProvider);
    }

    private void addTeamFromSelection() {
        ClubSelectionDialog dialog = new ClubSelectionDialog(clubService);
        dialog.addConfirmListener(listener -> {
            Optional<Club> selection = dialog.getSelection();
            selection.ifPresent(this::addTeam);
        });
        dialog.open();
    }

    private boolean isAdmin() {
        Optional<User> user = authenticatedUser.get();
        return user.isPresent() && user.get().getRoles().contains(Role.ADMIN);
    }

    private void addTeam(Club club) {
        long count = competition.getTeams().stream().filter(t -> t.getClub().equals(club)).count();
        Team newTeam = new Team();
        newTeam.setCompetition(competition);
        newTeam.setClub(club);
        String suffix = "";
        if (count > 0) {
            suffix = " " + (count + 1);
        }
        newTeam.setName(club.getName() + suffix);
        competition.getTeams().add(newTeam);
        teamService.update(newTeam);

        grid.getDataProvider().refreshAll();
    }

    private void removeTeam(Team team) {
        Club club = team.getClub();
        List<Team> sorted = competition.getTeams().stream().filter(t -> t.getClub().equals(club))
                .sorted(Comparator.comparing(Team::getRegistered)).toList();
        Team teamToRemove = sorted.get(sorted.size() - 1);
        competition.getTeams().remove(teamToRemove);
        teamService.delete(teamToRemove.getId());
        grid.getDataProvider().refreshAll();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String uuid) {
        Optional<Competition> competition = competitionService.get(UUID.fromString(uuid));
        this.competition = competition.orElseThrow();
        h2.setText(this.competition.getName());
    }
}
