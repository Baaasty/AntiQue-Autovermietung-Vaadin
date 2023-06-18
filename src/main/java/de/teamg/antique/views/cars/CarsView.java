package de.teamg.antique.views.cars;

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
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.teamg.antique.data.entity.Car;
import de.teamg.antique.data.exception.CarNotFoundException;
import de.teamg.antique.data.service.CarService;
import de.teamg.antique.views.MainLayout;

import java.text.NumberFormat;
import java.util.Optional;

@SpringComponent
@UIScope
@PageTitle("Autos ➤ AntiQue-Autovermietung")
@Route(value = "cars/:carID?/:action?(edit)", layout = MainLayout.class)
public class CarsView extends VerticalLayout implements BeforeEnterObserver {

    private final String CAR_ID = "carID";
    private final String CAR_LIST_ROUTE_TEMPLATE = "cars";
    private final String CAR_EDIT_ROUTE_TEMPLATE = "cars/%s/edit";
    private final String CAR_NEW_ROUTE_TEMPLATE = "cars/new";

    private final Grid<Car> grid = new Grid<>(Car.class);
    private final TextField filterText = new TextField();
    private final CarsForm form;
    private final CarService carService;

    public CarsView(CarsForm carsForm, CarService carService) {
        this.form = carsForm;
        this.carService = carService;

        addClassName("cars-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassNames("cars-grid");
        grid.setSizeFull();

        grid.removeAllColumns();

        grid.addColumn(new TextRenderer<>(Car::getLicensePlate)).setComparator(Car::getLicensePlate).setHeader("Kennzeichen");
        grid.addColumn(new TextRenderer<>(Car::getDesignation)).setComparator(Car::getDesignation).setHeader("Fahrzeugbezeichnung");
        grid.addColumn(new TextRenderer<>(car -> String.valueOf(car.getModelYears()))).setComparator(Car::getModelYears).setHeader("Baujahr");
        grid.addColumn(new NumberRenderer<>(Car::getHp, NumberFormat.getIntegerInstance())).setComparator(Car::getHp).setHeader("PS");
        grid.addColumn(new NumberRenderer<>(Car::getCc, NumberFormat.getIntegerInstance())).setComparator(Car::getCc).setHeader("ccm");
        grid.addColumn(new TextRenderer<>(Car::getFuel)).setComparator(Car::getFuel).setHeader("Treibstoff");
        grid.addColumn(new TextRenderer<>(Car::getInsuranceNumber)).setComparator(Car::getInsuranceNumber).setHeader("Versicherungsnummer");
        grid.addColumn(new LocalDateRenderer<>(Car::getTuv, "dd.MM.yyyy")).setComparator(Car::getTuv).setHeader("TÜV");
        grid.addColumn(new NumberRenderer<>(Car::getPricePerDay, NumberFormat.getCurrencyInstance())).setComparator(Car::getPricePerDay).setHeader("€/Tag");
        grid.addColumn(new NumberRenderer<>(Car::getPricePerKm, NumberFormat.getCurrencyInstance())).setComparator(Car::getPricePerKm).setHeader("€/Km");
        grid.addColumn(new LocalDateTimeRenderer<>(Car::getUpdatedAt, "dd.MM.yyyy HH:mm:ss")).setComparator(Car::getUpdatedAt).setHeader("Aktualisiert am");
        grid.addColumn(new LocalDateTimeRenderer<>(Car::getCreatedAt, "dd.MM.yyyy HH:mm:ss")).setComparator(Car::getCreatedAt).setHeader("Erstellt am");

        grid.getColumns().forEach(col -> col.setAutoWidth(true).setSortable(true).setResizable(true));

        grid.asSingleSelect().addValueChangeListener(event -> editCar(event.getValue()));
    }

    private void configureForm() {
        form.setWidth("25em");
        form.addSaveListener(this::saveCar);
        form.addDeleteListener(this::deleteCar);
        form.addCloseListener(event -> closeEditor());
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Suche");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addCarButton = new Button("Auto hinzufügen");
        addCarButton.addClickListener(click -> UI.getCurrent().navigate(CAR_NEW_ROUTE_TEMPLATE));

        var toolbar = new HorizontalLayout(filterText, addCarButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("cars-content");
        content.setSizeFull();
        return content;
    }

    private void saveCar(CarsForm.SaveEvent event) {
        try {
            carService.updateCar(event.getCar());
        } catch (CarNotFoundException exception) {
            carService.createCar(event.getCar());
        }

        updateList();
        closeEditor();
    }

    private void deleteCar(CarsForm.DeleteEvent event) {
        carService.deleteCar(event.getCar().getId());
        updateList();
        closeEditor();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> optRouteParameter = event.getRouteParameters().get(CAR_ID);

        filterText.setValue("");

        if (optRouteParameter.isEmpty()) {
            updateList();
            closeEditor();
            return;
        }

        String routeParameter = optRouteParameter.get();

        if (routeParameter.equals("new")) {
            addCar();
            return;
        }

        try {
            long carId = Long.parseUnsignedLong(routeParameter);

            editCar(carService.getCarById(carId));
        } catch (NumberFormatException | CarNotFoundException exception) {
            Notification.show(String.format("Es gibt kein Auto mit der ID '%s'", routeParameter), 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            grid.select(null);
            grid.getDataProvider().refreshAll();
            event.forwardTo(CAR_LIST_ROUTE_TEMPLATE);
        }
    }

    public void editCar(Car car) {
        if (car == null) {
            closeEditor();
        } else {
            UI.getCurrent().navigate(String.format(CAR_EDIT_ROUTE_TEMPLATE, car.getId()));
            form.setCar(car);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setCar(null);
        form.setVisible(false);
        removeClassName("editing");
        UI.getCurrent().navigate(CAR_LIST_ROUTE_TEMPLATE);
    }

    private void addCar() {
        grid.asSingleSelect().clear();
        editCar(new Car());
    }


    private void updateList() {
        grid.setItems(filterText.isEmpty() ? carService.getAllCars() : carService.getAllCarsFiltered(filterText.getValue()));
    }

}
