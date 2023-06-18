package de.teamg.antique.views.employee;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.teamg.antique.data.entity.Person;
import de.teamg.antique.data.exception.PersonNotFoundException;
import de.teamg.antique.data.service.PersonService;
import de.teamg.antique.views.MainLayout;

import java.util.Optional;

@SpringComponent
@UIScope
@PageTitle("Mitarbeiter ➤ AntiQue-Autovermietung")
@Route(value = "employees/:employeeID?/:action?(edit)", layout = MainLayout.class)
public class EmployeesView extends VerticalLayout implements BeforeEnterObserver {

    private final String EMPLOYEE_ID = "employeeID";
    private final String EMPLOYEE_LIST_ROUTE_TEMPLATE = "employees";
    private final String EMPLOYEE_EDIT_ROUTE_TEMPLATE = "employees/%s/edit";
    private final String EMPLOYEE_NEW_ROUTE_TEMPLATE = "employees/new";

    private final Grid<Person> grid = new Grid<>(Person.class);
    private final TextField filterText = new TextField();
    private final EmployeesForm form;
    private final PersonService personService;

    public EmployeesView(EmployeesForm employeesForm, PersonService personService) {
        this.form = employeesForm;
        this.personService = personService;

        addClassName("employees-view");
        setSizeFull();
        updateList();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        closeEditor();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("employees-content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form.setWidth("25em");
        form.addSaveListener(this::saveEmployee);
        form.addDeleteListener(this::deleteEmployee);
        form.addCloseListener(e -> closeEditor());
    }

    private void saveEmployee(EmployeesForm.SaveEvent event) {
        try {
            personService.updatePerson(event.getEmployee());
        } catch (PersonNotFoundException exception) {
            Person employee = event.getEmployee();

            employee.setEmployee(true);

            personService.createPerson(employee);
        }

        updateList();
        closeEditor();
    }

    private void deleteEmployee(EmployeesForm.DeleteEvent event) {
        personService.deletePerson(event.getEmployee().getId());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassNames("contact-grid");
        grid.setSizeFull();

        grid.removeAllColumns();

        grid.addColumn(new TextRenderer<>(Person::getFirstName)).setComparator(Person::getFirstName).setHeader("Vorname");
        grid.addColumn(new TextRenderer<>(Person::getLastName)).setComparator(Person::getLastName).setHeader("Nachname");
        grid.addColumn(new TextRenderer<>(Person::getStreet)).setComparator(Person::getStreet).setHeader("Straße");
        grid.addColumn(new TextRenderer<>(Person::getCity)).setComparator(Person::getCity).setHeader("Stadt");
        grid.addColumn(new TextRenderer<>(Person::getPostCode)).setComparator(Person::getPostCode).setHeader("Postleitzahl");
        grid.addColumn(new TextRenderer<>(Person::getCountry)).setComparator(Person::getCountry).setHeader("Land");
        grid.addColumn(new LocalDateRenderer<>(Person::getDateOfBirth, "dd.MM.yyyy")).setComparator(Person::getDateOfBirth).setHeader("Geburtsdatum");
        grid.addColumn(new TextRenderer<>(Person::getPhone)).setComparator(Person::getPhone).setHeader("Telefonnummer");
        grid.addColumn(new LocalDateTimeRenderer<>(Person::getUpdatedAt, "dd.MM.yyyy HH:mm:ss:SSS")).setComparator(Person::getUpdatedAt).setHeader("Aktualisiert am");
        grid.addColumn(new LocalDateTimeRenderer<>(Person::getCreatedAt, "dd.MM.yyyy HH:mm:ss:SSS")).setComparator(Person::getCreatedAt).setHeader("Erstellt am");

        grid.getColumns().forEach(col -> col.setAutoWidth(true).setSortable(true).setResizable(true));

        grid.asSingleSelect().addValueChangeListener(event -> editEmployee(event.getValue()));
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Suche");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(event -> updateList());

        Button addEmployeeButton = new Button("Mitarbeiter hinzufügen");
        addEmployeeButton.addClickListener(click -> UI.getCurrent().navigate(EMPLOYEE_NEW_ROUTE_TEMPLATE));

        var toolbar = new HorizontalLayout(filterText, addEmployeeButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> optRouteParameter = event.getRouteParameters().get(EMPLOYEE_ID);

        if (optRouteParameter.isEmpty()) {
            updateList();
            closeEditor();
            return;
        }

        String routeParameter = optRouteParameter.get();

        if (routeParameter.equals("new")) {
            addEmployee();
            return;
        }

        try {
            long employeeId = Long.parseUnsignedLong(routeParameter);

            editEmployee(personService.getPersonById(employeeId));
        } catch (NumberFormatException | PersonNotFoundException exception) {
            Notification.show(String.format("Es gibt keinen Mitarbeiter mit der ID '%s'", routeParameter), 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            grid.select(null);
            grid.getDataProvider().refreshAll();
            event.forwardTo(EMPLOYEE_LIST_ROUTE_TEMPLATE);
        }
    }

    public void editEmployee(Person employee) {
        if (employee == null) {
            closeEditor();
        } else {
            UI.getCurrent().navigate(String.format(EMPLOYEE_EDIT_ROUTE_TEMPLATE, employee.getId()));
            form.setEmployee(employee);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setEmployee(null);
        form.setVisible(false);
        removeClassName("editing");
        UI.getCurrent().navigate(EMPLOYEE_LIST_ROUTE_TEMPLATE);
    }

    private void addEmployee() {
        grid.asSingleSelect().clear();
        editEmployee(new Person());
    }


    private void updateList() {
        grid.setItems(filterText.isEmpty() ? personService.getAllEmployees() : personService.getAllEmployeesFiltered(filterText.getValue()));
    }

}
