package de.teamg.antique.views.cars;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.teamg.antique.data.entity.Car;
import de.teamg.antique.data.service.CarService;
import de.teamg.antique.views.MainLayout;

import java.util.Optional;

@SpringComponent
@UIScope
@PageTitle("Autos ➤ AntiQue-Autovermietung")
@Route(value = "cars/:carID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class CarsView extends VerticalLayout implements BeforeEnterObserver {

    private final String CAR_ID = "carID";
    private final String CAR_EDIT_ROUTE_TEMPLATE = "cars/%s/edit";

    Grid<Car> grid = new Grid<>(Car.class);
    TextField filterText = new TextField();
    CarForm form;
    CarService carService;

    public CarsView(CarService carService) {
        this.carService = carService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new CarForm();
        form.setWidth("25em");
        form.addSaveListener(this::saveCar);
        form.addDeleteListener(this::deleteCar);
        form.addCloseListener(e -> closeEditor());
    }

    private void saveCar(CarForm.SaveEvent event) {
        carService.updateCar(event.getCar());
        updateList();
        closeEditor();
    }

    private void deleteCar(CarForm.DeleteEvent event) {
        carService.deleteCar(event.getCar().getId());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassNames("contact-grid");
        grid.setSizeFull();

        grid.setColumns("licensePlate", "designation", "modelYears", "hp", "cc", "fuel", "insuranceNumber", "tuv", "pricePerDay", "pricePerKm");

        grid.getColumnByKey("licensePlate").setHeader("Kennzeichen");
        grid.getColumnByKey("designation").setHeader("Fahrzeugbezeichnung");
        grid.getColumnByKey("modelYears").setHeader("Baujahr");
        grid.getColumnByKey("hp").setHeader("PS");
        grid.getColumnByKey("cc").setHeader("ccm");
        grid.getColumnByKey("fuel").setHeader("Treibstoff");
        grid.getColumnByKey("insuranceNumber").setHeader("Versicherungsnummer");
        grid.getColumnByKey("tuv").setHeader("TÜV");
        grid.getColumnByKey("pricePerDay").setHeader("€/Tag");
        grid.getColumnByKey("pricePerKm").setHeader("€/Tag");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> {
            editCar(event.getValue());
        });
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filter");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addCarButton = new Button("Auto hinzufügen");
        addCarButton.addClickListener(click -> addCar());

        var toolbar = new HorizontalLayout(filterText, addCarButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> carId = event.getRouteParameters().get(CAR_ID).map(Long::parseLong);
        if (carId.isPresent()) {
            Optional<Car> optCar = carService.getOptionalCarById(carId.get());
            if (optCar.isPresent()) {
                editCar(optCar.get());
            } else {
                Notification.show(String.format("Es gibt kein Auto mit der ID '%s'", carId.get()), 3000, Notification.Position.BOTTOM_START);
                grid.select(null);
                grid.getDataProvider().refreshAll();
                event.forwardTo(CarsView.class);
            }
        }
    }

    public void editCar(Car car) {
        if (car == null) {
            closeEditor();
            UI.getCurrent().navigate(CarsView.class);
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
    }

    private void addCar() {
        grid.asSingleSelect().clear();
        editCar(new Car());
    }


    private void updateList() {
        grid.setItems(filterText.isEmpty() ? carService.getAllCars() : carService.getAllCarsFiltered(filterText.getValue()));
    }

}
