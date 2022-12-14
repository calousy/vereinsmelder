package org.meisl.vereinsmelder.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import org.meisl.vereinsmelder.components.appnav.AppNav;
import org.meisl.vereinsmelder.components.appnav.AppNavItem;
import org.meisl.vereinsmelder.data.Role;
import org.meisl.vereinsmelder.data.entity.User;
import org.meisl.vereinsmelder.security.AuthenticatedUser;
import org.meisl.vereinsmelder.views.about.AboutView;
import org.meisl.vereinsmelder.views.competition.CompetitionsView;
import org.meisl.vereinsmelder.views.masterdata.club.ClubsView;
import org.meisl.vereinsmelder.views.masterdata.user.UsersView;
import org.meisl.vereinsmelder.views.user.UserSettingsView;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("Vereinsmelder");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();

        if (accessChecker.hasAccess(CompetitionsView.class)) {
            nav.addItem(new AppNavItem("Wettbewerbe", CompetitionsView.class, "la la-globe"));

        }

        authenticatedUser.get().ifPresent(u -> {
            if (u.getRoles().contains(Role.ADMIN)) {
                AppNavItem stammdaten = new AppNavItem("Stammdaten");
                nav.addItem(stammdaten);
                stammdaten.addItem(new AppNavItem("Benutzer", UsersView.class));
                stammdaten.addItem(new AppNavItem("Vereine", ClubsView.class));
            }
        });

        if (accessChecker.hasAccess(AboutView.class)) {
            nav.addItem(new AppNavItem("About", AboutView.class, "la la-file"));

        }
        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getName());
            StreamResource resource = new StreamResource("profile-pic",
                    () -> new ByteArrayInputStream(user.getProfilePicture()));
            avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            SubMenu subMenu = userName.getSubMenu();
            subMenu.addItem("Einstellungen", e -> getUI()
                    .ifPresent(ui -> ui.navigate(UserSettingsView.class)));
            subMenu.addItem("Abmelden", e -> {
                authenticatedUser.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Anmelden");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
