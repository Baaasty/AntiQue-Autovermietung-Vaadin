package de.teamg.antique.views.rentals;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.teamg.antique.data.entity.Rental;
import de.teamg.antique.data.exception.RentalNotFoundException;
import de.teamg.antique.data.service.RentalService;
import de.teamg.antique.views.MainLayout;

import java.util.Optional;

@SpringComponent
@UIScope
@PageTitle("Verträge ➤ AntiQue-Autovermietung")
@Route(value = "rentals/:rentalID?/:action?(edit)", layout = MainLayout.class)
public class RentalsView extends VerticalLayout implements BeforeEnterObserver {

    private final String RENTAL_ID = "rentalID";
    private final String RENTAL_LIST_ROUTE_TEMPLATE = "rentals";
    private final String RENTAL_EDIT_ROUTE_TEMPLATE = "rentals/%s/edit";
    private final String RENTAL_NEW_ROUTE_TEMPLATE = "rentals/new";

    private final Grid<Rental> grid = new Grid<>(Rental.class);
    private final TextField filterText = new TextField();
    private final RentalsForm form;
    private final RentalService rentalService;

    public RentalsView(RentalsForm rentalsForm, RentalService rentalService) {
        this.form = rentalsForm;
        this.rentalService = rentalService;

        addClassName("rentals-view");
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
        content.addClassNames("rentals-content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form.setWidth("25em");
        form.addSaveListener(this::saveRental);
        form.addDeleteListener(this::deleteRental);
        form.addCloseListener(event -> closeEditor());
    }

    private void saveRental(RentalsForm.SaveEvent event) {
        try {
            rentalService.updateRental(event.getRental());
        } catch (RentalNotFoundException exception) {
            rentalService.createRental(event.getRental());
        }

        updateList();
        closeEditor();
    }

    private void deleteRental(RentalsForm.DeleteEvent event) {
        rentalService.deleteRental(event.getRental().getId());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassNames("contact-grid");
        grid.setSizeFull();

        grid.setColumns("rentalStart", "rentalEnd", "kmStart", "kmEnd", "pricePerDay", "pricePerKm", "updatedAt", "createdAt");

        grid.getColumnByKey("rentalStart").setHeader("Mietbeginn");
        grid.getColumnByKey("rentalEnd").setHeader("Mietende");
        grid.getColumnByKey("kmStart").setHeader("Kilometerstand zu Beginn");
        grid.getColumnByKey("kmEnd").setHeader("Kilometerstand zu Ende");
        grid.getColumnByKey("pricePerDay").setHeader("€/Tag");
        grid.getColumnByKey("pricePerKm").setHeader("€/Tag");
        grid.getColumnByKey("updatedAt").setHeader("Aktualisiert am");
        grid.getColumnByKey("createdAt").setHeader("Erstellt am");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> editRental(event.getValue()));
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Suche");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addRentalButton = new Button("Vertrag abschließen");
        addRentalButton.addClickListener(click -> UI.getCurrent().navigate(RENTAL_NEW_ROUTE_TEMPLATE));

        var toolbar = new HorizontalLayout(filterText, addRentalButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> optRouteParameter = event.getRouteParameters().get(RENTAL_ID);

        if (optRouteParameter.isEmpty()) {
            updateList();
            closeEditor();
            return;
        }

        String routeParameter = optRouteParameter.get();

        try {
            long rentalId = Long.parseUnsignedLong(routeParameter);

            editRental(rentalService.getRentalById(rentalId));
        } catch (NumberFormatException | RentalNotFoundException exception) {
            Notification.show(String.format("Es gibt kein Vertrag mit der ID '%s'", routeParameter), 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            grid.select(null);
            grid.getDataProvider().refreshAll();
            event.forwardTo(RENTAL_LIST_ROUTE_TEMPLATE);
        }
    }

    public void editRental(Rental rental) {
        if (rental == null) {
            closeEditor();
        } else {
            UI.getCurrent().navigate(String.format(RENTAL_EDIT_ROUTE_TEMPLATE, rental.getId()));
            form.setRental(rental);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setRental(null);
        form.setVisible(false);
        removeClassName("editing");
        UI.getCurrent().navigate(RENTAL_LIST_ROUTE_TEMPLATE);
    }


    private void updateList() {
        grid.setItems(filterText.isEmpty() ? rentalService.getAllRentals() : rentalService.getAllRentalsFiltered(filterText.getValue()));
    }

}
