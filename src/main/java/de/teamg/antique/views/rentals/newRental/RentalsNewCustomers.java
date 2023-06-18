package de.teamg.antique.views.rentals.newRental;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.teamg.antique.data.entity.Person;
import de.teamg.antique.data.exception.CarNotFoundException;
import de.teamg.antique.data.service.CarService;
import de.teamg.antique.data.service.PersonService;

import java.util.HashMap;
import java.util.Map;

@SpringComponent
@UIScope
public class RentalsNewCustomers extends VerticalLayout implements BeforeEnterObserver {

    private final String RENTAL_NEW_ROUTE_TEMPLATE = "rentals/new";

    private final Grid<Person> grid = new Grid<>(Person.class);
    private final TextField filterText = new TextField();

    private final PersonService personService;
    private final CarService carService;

    public RentalsNewCustomers(PersonService personService, CarService carService) {
        this.personService = personService;
        this.carService = carService;

        addClassNames("rentals-new-content");
        setSizeFull();
        configureGrid();

        add(getToolbar(), grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames("rentals-new-grid");
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
        grid.addItemClickListener(event -> selectPerson(event.getItem()));
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Suche");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(event -> updateList());

        var toolbar = new HorizontalLayout(filterText);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        filterText.setValue("");
        updateList();
    }

    private void updateList() {
        grid.setItems(filterText.isEmpty() ? personService.getAllCustomers() : personService.getAllCustomersFiltered(filterText.getValue()));
    }

    private void selectPerson(Person person) {
        Map<String, String[]> newParametersMap = new HashMap<>();

        long carId;

        try {
            carId = Long.parseLong(RentalsNewView.getParametersMap().get("car").get(0));
            carService.getCarById(carId);

            newParametersMap.put("car", new String[]{String.valueOf(carId)});
        } catch (NullPointerException | NumberFormatException | CarNotFoundException exception) {
            newParametersMap.clear();
        } finally {
            newParametersMap.put("customer", new String[]{String.valueOf(person.getId())});
            UI.getCurrent().navigate(RENTAL_NEW_ROUTE_TEMPLATE, QueryParameters.full(newParametersMap));
        }
    }

}
