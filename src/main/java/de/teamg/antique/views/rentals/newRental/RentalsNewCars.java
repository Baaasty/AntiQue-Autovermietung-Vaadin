package de.teamg.antique.views.rentals.newRental;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.teamg.antique.data.entity.Car;
import de.teamg.antique.data.exception.PersonNotFoundException;
import de.teamg.antique.data.service.CarService;
import de.teamg.antique.data.service.PersonService;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

@SpringComponent
@UIScope
public class RentalsNewCars extends VerticalLayout implements BeforeEnterObserver {

    private final String RENTAL_NEW_ROUTE_TEMPLATE = "rentals/new";

    private final Grid<Car> grid = new Grid<>(Car.class);
    private final TextField filterText = new TextField();

    private final CarService carService;
    private final PersonService personService;

    public RentalsNewCars(CarService carService, PersonService personService) {
        this.carService = carService;
        this.personService = personService;

        addClassNames("rentals-new-content");
        setSizeFull();
        configureGrid();

        add(getToolbar(), grid);
        updateList();
    }

    private void configureGrid() {

        grid.addClassNames("contact-grid");
        grid.setSizeFull();

        grid.removeAllColumns();

        grid.addColumn(new TextRenderer<>(Car::getLicensePlate)).setComparator(Car::getLicensePlate).setHeader("Kennzeichen");
        grid.addColumn(new TextRenderer<>(Car::getDesignation)).setComparator(Car::getDesignation).setHeader("Fahrzeugbezeichnung");
        grid.addColumn(new TextRenderer<>(car -> String.valueOf(car.getModelYears()))).setComparator(Car::getModelYears).setHeader("Baujahr");
        grid.addColumn(new NumberRenderer<>(Car::getHp, NumberFormat.getIntegerInstance())).setComparator(Car::getHp).setHeader("PS");
        grid.addColumn(new NumberRenderer<>(Car::getCc, NumberFormat.getIntegerInstance())).setComparator(Car::getCc).setHeader("ccm");
        grid.addColumn(new TextRenderer<>(Car::getFuel)).setComparator(Car::getFuel).setHeader("Treibstoff");
        grid.addColumn(new LocalDateRenderer<>(Car::getTuv, "dd.MM.yyyy")).setComparator(Car::getTuv).setHeader("TÜV");
        grid.addColumn(new NumberRenderer<>(Car::getPricePerDay, NumberFormat.getCurrencyInstance())).setComparator(Car::getPricePerDay).setHeader("€/Tag");
        grid.addColumn(new NumberRenderer<>(Car::getPricePerKm, NumberFormat.getCurrencyInstance())).setComparator(Car::getPricePerKm).setHeader("€/Km");

        grid.getColumns().forEach(col -> col.setAutoWidth(true).setSortable(true).setResizable(true));
        grid.addItemClickListener(event -> selectCar(event.getItem()));

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
        updateList();
    }

    private void updateList() {
        grid.setItems(filterText.isEmpty() ? carService.getAllCars() : carService.getAllCarsFiltered(filterText.getValue()));
    }

    private void selectCar(Car car) {
        Map<String, String[]> newParametersMap = new HashMap<>();

        long customerId;

        try {
            customerId = Long.parseLong(RentalsNewView.getParametersMap().get("customer").get(0));
            personService.getPersonById(customerId);

            newParametersMap.put("customer", new String[]{String.valueOf(customerId)});
        } catch (NullPointerException | NumberFormatException | PersonNotFoundException exception) {
            newParametersMap.clear();
        } finally {
            newParametersMap.put("car", new String[]{String.valueOf(car.getId())});
            UI.getCurrent().navigate(RENTAL_NEW_ROUTE_TEMPLATE, QueryParameters.full(newParametersMap));
        }
    }

}
