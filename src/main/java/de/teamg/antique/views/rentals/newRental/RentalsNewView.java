package de.teamg.antique.views.rentals.newRental;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.teamg.antique.data.entity.Car;
import de.teamg.antique.data.entity.Person;
import de.teamg.antique.data.entity.Rental;
import de.teamg.antique.data.exception.RentalNotFoundException;
import de.teamg.antique.data.service.CarService;
import de.teamg.antique.data.service.PersonService;
import de.teamg.antique.data.service.RentalService;
import de.teamg.antique.views.MainLayout;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringComponent
@UIScope
@PageTitle("Verträge ➤ AntiQue-Autovermietung")
@Route(value = "rentals/new", layout = MainLayout.class)
public class RentalsNewView extends VerticalLayout implements BeforeEnterObserver, HasUrlParameter<String> {

    private final String RENTAL_LIST_ROUTE_TEMPLATE = "rentals";

    private final Button backButton;

    private final RentalService rentalService;
    private final CarService carService;
    private final PersonService personService;

    private final RentalsNewCars rentalsNewCars;
    private final RentalsNewCustomers rentalsNewCustomers;
    private final RentalsNewForm rentalsNewForm;

    @Getter
    private static Map<String, List<String>> parametersMap = new HashMap<>();

    public RentalsNewView(RentalService rentalService, CarService carService, PersonService personService, RentalsNewCars rentalsNewCars, RentalsNewCustomers rentalsNewCustomers, RentalsNewForm rentalsNewForm) {
        this.rentalService = rentalService;
        this.carService = carService;
        this.personService = personService;
        this.rentalsNewCars = rentalsNewCars;
        this.rentalsNewCustomers = rentalsNewCustomers;
        this.rentalsNewForm = rentalsNewForm;

        addClassName("rentals-new-view");

        setSpacing(false);
        setSizeFull();
        configureForm();

        backButton = new Button("Abbrechen");
        backButton.addClickListener(click -> UI.getCurrent().navigate(RENTAL_LIST_ROUTE_TEMPLATE));

        add(backButton, rentalsNewCustomers);
    }

    private void configureForm() {
        rentalsNewForm.setWidth("50em");
        rentalsNewForm.addSaveListener(this::saveRental);
        rentalsNewForm.addCloseListener(event -> closeEditor());
    }

    private void saveRental(RentalsNewForm.SaveEvent event) {
        try {
            rentalService.updateRental(event.getRental());
        } catch (RentalNotFoundException exception) {
            Rental rental = event.getRental();
            Car car = carService.getCarById(Long.parseLong(parametersMap.get("car").get(0)));
            Person customer = personService.getPersonById(Long.parseLong(parametersMap.get("customer").get(0)));
            Person employee = personService.getPersonById(1);

            rental.setCar(car);
            rental.setCustomerPerson(customer);
            rental.setEmployeePerson(employee);

            rental.setPricePerDay(car.getPricePerDay());
            rental.setPricePerKm(car.getPricePerKm());

            rentalService.createRental(rental);
        }

        closeEditor();
    }

    private void closeEditor() {
        UI.getCurrent().navigate(RENTAL_LIST_ROUTE_TEMPLATE);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        rentalsNewForm.setRental(new Rental());
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();

        parametersMap = queryParameters.getParameters();

        removeAll();

        add(backButton, rentalsNewCustomers);

        if (parametersMap.containsKey("customer")) {
            remove(rentalsNewCustomers);
            add(rentalsNewCars);

            Rental rental = rentalsNewForm.getRental();
            rental.setCustomerPerson(personService.getPersonById(Long.parseLong(parametersMap.get("customer").get(0))));
            rentalsNewForm.setRental(rental);
        }

        if (parametersMap.containsKey("car") && parametersMap.containsKey("customer")) {
            remove(rentalsNewCars);
            add(rentalsNewForm);

            Rental rental = rentalsNewForm.getRental();
            rental.setCar(carService.getCarById(Long.parseLong(parametersMap.get("car").get(0))));
            rental.setCustomerPerson(personService.getPersonById(Long.parseLong(parametersMap.get("customer").get(0))));
            rentalsNewForm.setRental(rental);
            H2 car = RentalsNewForm.getCar();
            car.setText("Auto: " + rental.getCar().getDesignation());
            RentalsNewForm.setCar(car);
        }
    }

}
