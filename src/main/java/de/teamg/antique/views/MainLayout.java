package de.teamg.antique.views;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;
import de.teamg.antique.views.cars.CarsView;
import de.teamg.antique.views.customers.CustomersView;
import de.teamg.antique.views.employee.EmployeesView;
import de.teamg.antique.views.rentals.RentalsView;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
@UIScope
@Route(value = "")
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
        addDarkModeSwitch();
    }

    private void addDrawerContent() {
        Image appLogo = new Image(new StreamResource("logo.png", () -> getClass().getResourceAsStream("/images/logo.png")), "AntiQue Logo");
        appLogo.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
        appLogo.setMaxWidth(3, Unit.EM);

        H4 appText = new H4("Fahrzeugvermietung");

        Header header = new Header(appLogo, appText);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller);
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDarkModeSwitch() {
        Checkbox themeToggle = new Checkbox("Darkmode");
        themeToggle.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.FontWeight.SEMIBOLD, LumoUtility.Margin.MEDIUM);
        themeToggle.addValueChangeListener(event -> setTheme(event.getValue()));

        addToDrawer(themeToggle);
    }

    private void setTheme(boolean dark) {
        var js = "document.documentElement.setAttribute('theme', $0)";

        getElement().executeJs(js, dark ? Lumo.DARK : Lumo.LIGHT);
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Autos", CarsView.class, LineAwesomeIcon.CAR_SIDE_SOLID.create()));
        nav.addItem(new SideNavItem("Kunden", CustomersView.class, LineAwesomeIcon.USERS_SOLID.create()));
        nav.addItem(new SideNavItem("Mitarbeiter", EmployeesView.class, LineAwesomeIcon.USER_TIE_SOLID.create()));
        nav.addItem(new SideNavItem("Verträge", RentalsView.class, LineAwesomeIcon.FILE_CONTRACT_SOLID.create()));

        return nav;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle().split(" ➤ ")[0]);
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
