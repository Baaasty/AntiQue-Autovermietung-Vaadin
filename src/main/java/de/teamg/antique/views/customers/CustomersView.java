package de.teamg.antique.views.customers;

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
import de.teamg.antique.data.exception.CarNotFoundException;
import de.teamg.antique.data.exception.PersonNotFoundException;
import de.teamg.antique.data.service.PersonService;
import de.teamg.antique.views.MainLayout;

import java.util.Optional;

@SpringComponent
@UIScope
@PageTitle("Kunden ➤ AntiQue-Autovermietung")
@Route(value = "customers/:customerID?/:action?(edit)", layout = MainLayout.class)
public class CustomersView extends VerticalLayout implements BeforeEnterObserver {

    private final String CUSTOMER_ID = "customerID";
    private final String CUSTOMER_LIST_ROUTE_TEMPLATE = "customers";
    private final String CUSTOMER_EDIT_ROUTE_TEMPLATE = "customers/%s/edit";
    private final String CUSTOMER_NEW_ROUTE_TEMPLATE = "customers/new";

    private final Grid<Person> grid = new Grid<>(Person.class);
    private final TextField filterText = new TextField();
    private final CustomersForm form;
    private final PersonService personService;

    public CustomersView(CustomersForm customersForm, PersonService personService) {
        this.form = customersForm;
        this.personService = personService;

        addClassName("customers-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassNames("customers-grid");
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
        grid.addColumn(new LocalDateTimeRenderer<>(Person::getUpdatedAt, "dd.MM.yyyy HH:mm:ss")).setComparator(Person::getUpdatedAt).setHeader("Aktualisiert am");
        grid.addColumn(new LocalDateTimeRenderer<>(Person::getCreatedAt, "dd.MM.yyyy HH:mm:ss")).setComparator(Person::getCreatedAt).setHeader("Erstellt am");

        grid.getColumns().forEach(col -> col.setAutoWidth(true).setSortable(true).setResizable(true));

        grid.asSingleSelect().addValueChangeListener(event -> editCustomer(event.getValue()));
    }

    private void configureForm() {
        form.setWidth("25em");
        form.addSaveListener(this::saveCustomer);
        form.addDeleteListener(this::deleteCustomer);
        form.addCloseListener(e -> closeEditor());
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Suche");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(event -> updateList());

        Button addCustomerButton = new Button("Kunde hinzufügen");
        addCustomerButton.addClickListener(click -> UI.getCurrent().navigate(CUSTOMER_NEW_ROUTE_TEMPLATE));

        var toolbar = new HorizontalLayout(filterText, addCustomerButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("customers-content");
        content.setSizeFull();
        return content;
    }

    private void saveCustomer(CustomersForm.SaveEvent event) {
        try {
            personService.updatePerson(event.getCustomer());
        } catch (PersonNotFoundException exception) {
            Person customer = event.getCustomer();

            customer.setEmployee(false);

            personService.createPerson(customer);
        }

        updateList();
        closeEditor();
    }

    private void deleteCustomer(CustomersForm.DeleteEvent event) {
        personService.deletePerson(event.getCustomer().getId());
        updateList();
        closeEditor();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> optRouteParameter = event.getRouteParameters().get(CUSTOMER_ID);

        filterText.setValue("");

        if (optRouteParameter.isEmpty()) {
            updateList();
            closeEditor();
            return;
        }

        String routeParameter = optRouteParameter.get();

        if (routeParameter.equals("new")) {
            addCustomer();
            return;
        }

        try {
            long customerId = Long.parseUnsignedLong(routeParameter);

            editCustomer(personService.getPersonById(customerId));
        } catch (NumberFormatException | CarNotFoundException exception) {
            Notification.show(String.format("Es gibt keinen Kunde mit der ID '%s'", routeParameter), 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            grid.select(null);
            grid.getDataProvider().refreshAll();
            event.forwardTo(CUSTOMER_LIST_ROUTE_TEMPLATE);
        }
    }

    public void editCustomer(Person customer) {
        if (customer == null) {
            closeEditor();
        } else {
            UI.getCurrent().navigate(String.format(CUSTOMER_EDIT_ROUTE_TEMPLATE, customer.getId()));
            form.setCustomer(customer);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setCustomer(null);
        form.setVisible(false);
        removeClassName("editing");
        UI.getCurrent().navigate(CUSTOMER_LIST_ROUTE_TEMPLATE);
    }

    private void addCustomer() {
        grid.asSingleSelect().clear();
        editCustomer(new Person());
    }


    private void updateList() {
        grid.setItems(filterText.isEmpty() ? personService.getAllCustomers() : personService.getAllCustomersFiltered(filterText.getValue()));
    }

}
