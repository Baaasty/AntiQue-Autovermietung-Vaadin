package de.teamg.antique.views.rentals.newRental;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
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
public class RentalsNewView extends VerticalLayout implements HasUrlParameter<String> {

    private final String CAR_LIST_ROUTE_TEMPLATE = "rentals";

    private final Button backButton;
    private final RentalsNewCars rentalsNewCars;
    private final RentalsNewCustomers rentalsNewCustomers;
    private final RentalService rentalService;

    @Getter
    private static Map<String, List<String>> parametersMap = new HashMap<>();

    public RentalsNewView(RentalsNewCars rentalsNewCars, RentalsNewCustomers rentalsNewCustomers, RentalService rentalService) {
        this.rentalsNewCars = rentalsNewCars;
        this.rentalsNewCustomers = rentalsNewCustomers;
        this.rentalService = rentalService;

        addClassName("rentals-new-view");

        setSpacing(false);
        setSizeFull();

        backButton = new Button("Abbrechen");
        backButton.addClickListener(click -> UI.getCurrent().navigate(CAR_LIST_ROUTE_TEMPLATE));

        add(backButton, rentalsNewCustomers);
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
        }

        if (parametersMap.containsKey("car")) remove(rentalsNewCars);
    }

}
