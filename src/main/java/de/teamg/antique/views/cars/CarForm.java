package de.teamg.antique.views.cars;


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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldBase;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.UIScope;
import de.teamg.antique.data.entity.Car;
import lombok.Getter;

@UIScope
public class CarForm extends FormLayout {
    TextField licensePlate = new TextField("Kennzeichen");
    TextField designation = new TextField("Fahrzeugbezeichnung");
    IntegerField modelYears = new IntegerField("Baujahr");
    IntegerField hp = new IntegerField("PS");
    IntegerField cc = new IntegerField("ccm");
    TextField fuel = new TextField("Treibstoff");
    TextField insuranceNumber = new TextField("Versicherungsnummer");
    DatePicker tuv = new DatePicker("TÜV");
    NumberField pricePerDay = new NumberField("€/Tag");
    NumberField pricePerKm = new NumberField("€/Km");


    Button save = new Button("Speichern");
    Button delete = new Button("Löschen");
    Button close = new Button("Abbrechen");

    Binder<Car> binder = new BeanValidationBinder<>(Car.class);

    public CarForm() {
        addClassName("car-form");

        binder.forField(licensePlate).asRequired().bind("licensePlate");
        binder.forField(designation).asRequired().bind("designation");
        binder.forField(modelYears).asRequired().bind("modelYears");
        binder.forField(hp).asRequired().bind("hp");
        binder.forField(cc).asRequired().bind("cc");
        binder.forField(hp).asRequired().bind("hp");
        binder.forField(fuel).asRequired().bind("fuel");
        binder.forField(insuranceNumber).asRequired().bind("insuranceNumber");
        binder.forField(tuv).asRequired().bind("tuv");
        binder.forField(pricePerDay).asRequired().bind("pricePerDay");
        binder.forField(pricePerKm).asRequired().bind("pricePerKm");

        binder.bindInstanceFields(this);

        add(
                licensePlate,
                designation,
                modelYears,
                hp,
                cc,
                fuel,
                insuranceNumber,
                tuv,
                pricePerDay,
                pricePerKm,
                createButtonsLayout()
        );

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
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }


    public void setCar(Car car) {
        binder.setBean(car);
    }

    @Getter
    public static abstract class CarFormEvent extends ComponentEvent<CarForm> {
        private final Car car;

        protected CarFormEvent(CarForm source, Car car) {
            super(source, false);
            this.car = car;
        }

    }

    public static class SaveEvent extends CarFormEvent {
        SaveEvent(CarForm source, Car car) {
            super(source, car);
        }
    }

    public static class DeleteEvent extends CarFormEvent {
        DeleteEvent(CarForm source, Car car) {
            super(source, car);
        }

    }

    public static class CloseEvent extends CarFormEvent {
        CloseEvent(CarForm source) {
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
