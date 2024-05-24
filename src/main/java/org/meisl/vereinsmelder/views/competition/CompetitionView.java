package org.meisl.vereinsmelder.views.competition;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.meisl.vereinsmelder.components.ClubSelectionDialog;
import org.meisl.vereinsmelder.data.entity.Club;
import org.meisl.vereinsmelder.data.entity.Competition;
import org.meisl.vereinsmelder.data.entity.Team;
import org.meisl.vereinsmelder.data.service.ClubService;
import org.meisl.vereinsmelder.data.service.CompetitionService;
import org.meisl.vereinsmelder.data.service.MelderService;
import org.meisl.vereinsmelder.data.service.TeamService;
import org.meisl.vereinsmelder.security.AuthenticatedUser;
import org.meisl.vereinsmelder.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@PageTitle("Wettbewerb")
@Route(value = "wettbewerb", layout = MainLayout.class)
@AnonymousAllowed
public class CompetitionView extends VerticalLayout implements
        HasUrlParameter<String> {

    public static final String DATE_TIME_PATTERN = "dd.MM.YYYY HH:M:ss";
    private final AuthenticatedUser authenticatedUser;
    private final CompetitionService competitionService;
    private final ClubService clubService;
    private final MelderService melderService;
    private final Grid<Team> grid;
    private final H2 h2 = new H2();
    private Competition competition;

    public CompetitionView(@Autowired AuthenticatedUser authenticatedUser,
                           TeamService teamService,
                           CompetitionService competitionService,
                           ClubService clubService, MelderService melderService) {
        this.authenticatedUser = authenticatedUser;
        this.competitionService = competitionService;
        this.clubService = clubService;
        this.melderService = melderService;

        Club managerOfClub = authenticatedUser.getManagedClub();

        setMargin(true);

        HorizontalLayout actionLayout = new HorizontalLayout();
        actionLayout.setAlignItems(Alignment.CENTER);

        if (managerOfClub != null) {
            Button meldenButton = new Button("Mannschaft melden");
            meldenButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            meldenButton.addClickListener(listener -> addTeams(Set.of(managerOfClub)));
            Span span = new Span(managerOfClub.getName());
            span.setClassName(LumoUtility.FontWeight.BOLD);
            actionLayout.add(new Text("Meine Mannschaft "), span, meldenButton);
        }

        if (currentUserIsAdmin()) {
            Button selectTeamButton = new Button("(Admin) Mannschaft melden");
            selectTeamButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            selectTeamButton.addClickListener(listener -> addTeamsFromSelection());
            actionLayout.add(selectTeamButton);
        }

        grid = new Grid<>();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        grid.addColumn(LitRenderer.of("${index + 1}")).setHeader("#")
                .setAutoWidth(true).setFlexGrow(0).setResizable(true);

        if (managerOfClub != null) {
            grid.setClassNameGenerator(team -> {
                if (team.getClub().equals(managerOfClub)) {
                    return "my-club";
                }
                return null;
            });
        }

        grid.addColumn(Team::getName).setHeader("Name");
        if (currentUserIsAdmin()) {
            grid.addColumn(new LocalDateTimeRenderer<>(Team::getRegistered, DATE_TIME_PATTERN)).setHeader("Gemeldet")
                    .setFlexGrow(0).setAutoWidth(true);
            grid.addColumn(new LocalDateTimeRenderer<>(Team::getUpdated, DATE_TIME_PATTERN)).setHeader("Aktualisiert")
                    .setFlexGrow(0).setAutoWidth(true);
        }
        if (currentUserIsAdmin() || managerOfClub != null) {
            grid.addComponentColumn(team -> {
                if ((currentUserIsAdmin() || team.getClub().equals(managerOfClub))
                        && team.isEnabled()) {
                    Button abmeldenBtn = new Button("Abmelden");
                    abmeldenBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
                    abmeldenBtn.addClickListener(listener -> removeTeam(team));
                    return abmeldenBtn;
                }
                return new Text("");
            }).setResizable(true);
        }

        h2.addClassNames(LumoUtility.Margin.Vertical.NONE);
        HorizontalLayout headerLayout = new HorizontalLayout(h2);
        add(headerLayout);

        if (currentUserIsAdmin()) {
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            editButton.addClickListener(listener -> {
                EditCompetitionDialog editCompetitionDialog = new EditCompetitionDialog(competition);
                editCompetitionDialog.addConfirmListener(editListener -> {
                    Competition editedCompetition = editCompetitionDialog.getCompetition();
                    competitionService.update(editedCompetition);
                    h2.setText(editedCompetition.getName());
                });
                editCompetitionDialog.open();
            });
            headerLayout.add(editButton);
        }
        if (authenticatedUser.get().isPresent()) {
            add(actionLayout);
        }

        Paragraph information = new Paragraph("Liste gemeldeter Mannschaften - keine Startliste!");
        add(information, grid);

        CallbackDataProvider<Team, Void> objectVoidCallbackDataProvider = DataProvider.fromCallbacks(query ->
                        teamService.listByCompetition(competition, PageRequest.of(query.getPage(), query.getPageSize())).get(),
                query ->
                        teamService.listByCompetitionCount(competition));

        grid.setItems(objectVoidCallbackDataProvider);
    }

    private void addTeamsFromSelection() {
        ClubSelectionDialog dialog = new ClubSelectionDialog(clubService);
        dialog.addConfirmListener(listener -> {
            Optional<Set<Club>> selection = dialog.getSelection();
            selection.ifPresent(this::addTeams);
        });
        dialog.open();
    }

    private boolean currentUserIsAdmin() {
        return authenticatedUser.isAdmin();
    }

    private void addTeams(Set<Club> club) {
        for (Club cl : club) {
            melderService.melden(competition, cl);
        }
        grid.getDataProvider().refreshAll();
    }

    private void removeTeam(Team team) {
        ConfirmDialog confirmDialog = new ConfirmDialog("Mannschaft abmelden",
                "Wollen Sie die Mannschaft wirklich abmelden?",
                "Abmelden", listener -> {
            melderService.abmelden(team, competition);
            grid.getDataProvider().refreshAll();
        });
        confirmDialog.setCancelable(true);
        confirmDialog.setCancelText("Abbrechen");
        confirmDialog.open();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String uuid) {
        Optional<Competition> competition = competitionService.get(UUID.fromString(uuid));
        this.competition = competition.orElseThrow();
        h2.setText(this.competition.getName());
    }
}
