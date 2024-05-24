package org.meisl.vereinsmelder.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.Set;

import org.meisl.vereinsmelder.data.Role;
import org.meisl.vereinsmelder.data.entity.User;
import org.meisl.vereinsmelder.security.AuthenticatedUser;
import org.meisl.vereinsmelder.views.about.AboutView;
import org.meisl.vereinsmelder.views.competition.CompetitionsView;
import org.meisl.vereinsmelder.views.masterdata.club.ClubsView;
import org.meisl.vereinsmelder.views.masterdata.user.UsersView;
import org.meisl.vereinsmelder.views.myclub.MyClub;
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

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        if (accessChecker.hasAccess(CompetitionsView.class)) {
            nav.addItem(new SideNavItem("Wettbewerbe", CompetitionsView.class, VaadinIcon.GLOBE.create()));
        }

        authenticatedUser.get().ifPresent(u -> {
            Set<Role> roles = u.getRoles();
            if (roles.contains(Role.SUPERMANAGER) || roles.contains(Role.ADMIN)) {
                if (u.getManagerOf() != null) {
                    nav.addItem(new SideNavItem("Mein Verein", MyClub.class));
                }
            }
            if (roles.contains(Role.ADMIN)) {
                SideNavItem stammdaten = new SideNavItem("Stammdaten");
                nav.addItem(stammdaten);
                stammdaten.addItem(new SideNavItem("Benutzer", UsersView.class, VaadinIcon.USERS.create()));
                stammdaten.addItem(new SideNavItem("Vereine", ClubsView.class, VaadinIcon.FLAG.create()));
            }
        });

        if (accessChecker.hasAccess(AboutView.class)) {
            nav.addItem(new SideNavItem("About", AboutView.class, VaadinIcon.FILE.create()));

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
            Anchor registerLink = new Anchor("register", "Jetzt registrieren!");
            registerLink.setClassName(LumoUtility.FontSize.XSMALL);
            layout.add(loginLink, registerLink);
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
