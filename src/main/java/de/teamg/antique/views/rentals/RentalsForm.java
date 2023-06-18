package de.teamg.antique.views.rentals;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.teamg.antique.data.entity.Rental;
import de.teamg.antique.data.service.RentalService;
import lombok.Getter;

@SpringComponent
@UIScope
public class RentalsForm extends FormLayout {

    DatePicker rentalStart = new DatePicker("Mietbeginn");
    DatePicker rentalEnd = new DatePicker("Mietende");
    IntegerField kmStart = new IntegerField("Kilometerstand zu Beginn");
    IntegerField kmEnd = new IntegerField("Kilometerstand zu Ende");
    NumberField pricePerDay = new NumberField("€/Tag");
    NumberField pricePerKm = new NumberField("€/Km");

    Button save = new Button("Speichern");
    Button delete = new Button("Löschen");
    Button close = new Button("Abbrechen");

    RentalService rentalService;

    Binder<Rental> binder = new BeanValidationBinder<>(Rental.class);

    public RentalsForm(RentalService rentalService) {
        this.rentalService = rentalService;

        addClassName("rental-form");

        binder.forField(rentalStart).asRequired().bind("rentalStart");
        binder.forField(rentalEnd).asRequired().bind("rentalEnd");
        binder.forField(kmStart).asRequired().bind("kmStart");
        binder.forField(kmEnd).asRequired().bind("kmEnd");
        binder.forField(pricePerDay).asRequired().bind("pricePerDay");
        binder.forField(pricePerKm).asRequired().bind("pricePerKm");

        binder.bindInstanceFields(this);

        add(rentalStart, rentalEnd, kmStart, kmEnd, pricePerDay, pricePerKm, createButtonsLayout());

    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) fireEvent(new SaveEvent(this, binder.getBean()));
    }

    public void setRental(Rental rental) {
        binder.setBean(rental);

        if (rental != null) delete.setVisible(rentalService.rentalExistsById(rental.getId()));
    }

    @Getter
    public static abstract class RentalFormEvent extends ComponentEvent<RentalsForm> {
        private final Rental rental;

        protected RentalFormEvent(RentalsForm source, Rental rental) {
            super(source, false);
            this.rental = rental;
        }

    }

    public static class SaveEvent extends RentalFormEvent {
        SaveEvent(RentalsForm source, Rental rental) {
            super(source, rental);
        }
    }

    public static class DeleteEvent extends RentalFormEvent {
        DeleteEvent(RentalsForm source, Rental rental) {
            super(source, rental);
        }

    }

    public static class CloseEvent extends RentalFormEvent {
        CloseEvent(RentalsForm source) {
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }

}
