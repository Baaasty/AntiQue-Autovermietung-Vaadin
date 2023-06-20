package de.teamg.antique.views.rentals.newRental;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.teamg.antique.data.entity.Rental;
import de.teamg.antique.data.service.CarService;
import de.teamg.antique.data.service.RentalService;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;

@Getter
@SpringComponent
@UIScope
public class RentalsNewForm extends VerticalLayout {

    @Setter
    private static H2 car = new H2("Auto: ");

    private final DatePicker rentalStart = new DatePicker("Mietbeginn");
    private final DatePicker rentalEnd = new DatePicker("Mietende");
    private final IntegerField kmStart = new IntegerField("Kilometerstand zu Beginn");
    private final IntegerField kmEnd = new IntegerField("Kilometerstand zu Ende");
    private final H4 days = new H4("Tage: ");
    private final H4 kms = new H4("Kilometer: ");
    private final H4 pricePerDay = new H4("€/Tag: ");
    private final H4 pricePerKm = new H4("€/Km: ");
    private final H4 sum = new H4("Summe: ");
    private final H4 mwst = new H4("+19% MwSt: ");
    private final H4 sumOMG = new H4("Gesamtpreis: ");

    private final H4 daysValue = new H4();
    private final H4 kmsValue = new H4();
    private final H4 pricePerDayValue = new H4();
    private final H4 pricePerKmValue = new H4();
    private final H4 sumValue = new H4();
    private final H4 mwstValue = new H4();
    private final H4 sumOMGValue = new H4();

    Button save = new Button("Speichern");
    Button close = new Button("Abbrechen");

    RentalService rentalService;
    CarService carService;

    Binder<Rental> binder = new BeanValidationBinder<>(Rental.class);

    public RentalsNewForm(RentalService rentalService, CarService carService) {
        this.rentalService = rentalService;
        this.carService = carService;

        addClassName("customer-form");

        rentalStart.addValueChangeListener(event -> updatePrice());
        rentalEnd.addValueChangeListener(event -> updatePrice());
        kmStart.addValueChangeListener(event -> updatePrice());
        kmEnd.addValueChangeListener(event -> updatePrice());

        binder.forField(rentalStart).asRequired().bind("rentalStart");
        binder.forField(rentalEnd).asRequired().bind("rentalEnd");
        binder.forField(kmStart).asRequired().bind("kmStart");
        binder.forField(kmEnd).asRequired().bind("kmEnd");

        FormLayout formLayout = new FormLayout();
        binder.bindInstanceFields(formLayout);

        formLayout.add(rentalStart, rentalEnd, kmStart, kmEnd);

        add(car, formLayout, createButtonsLayout(), priceShit());

    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(event -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, close);
    }

    private Component priceShit() {
        HorizontalLayout daysVer = new HorizontalLayout();
        HorizontalLayout kmsVer = new HorizontalLayout();
        HorizontalLayout pricePerDayVer = new HorizontalLayout();
        HorizontalLayout pricePerKmVer = new HorizontalLayout();
        HorizontalLayout sumVer = new HorizontalLayout();
        HorizontalLayout mwstVer = new HorizontalLayout();
        HorizontalLayout sumOMGVer = new HorizontalLayout();

        days.setWidth("8em");
        kms.setWidth("8em");
        pricePerDay.setWidth("8em");
        pricePerKm.setWidth("8em");
        sum.setWidth("8em");
        mwst.setWidth("8em");
        sumOMG.setWidth("8em");

        daysVer.add(days, daysValue);
        kmsVer.add(kms, kmsValue);
        pricePerDayVer.add(pricePerDay, pricePerDayValue);
        pricePerKmVer.add(pricePerKm, pricePerKmValue);
        sumVer.add(sum, sumValue);
        mwstVer.add(mwst, mwstValue);
        sumOMGVer.add(sumOMG, sumOMGValue);

        VerticalLayout price = new VerticalLayout();

        price.add(daysVer, kmsVer, pricePerDayVer, pricePerKmVer, sumVer, mwstVer, sumOMGVer);

        return price;
    }

    private void validateAndSave() {
        if (binder.isValid()) fireEvent(new SaveEvent(this, binder.getBean()));
    }

    public Rental getRental() {
        return binder.getBean();
    }

    public void setRental(Rental rental) {
        binder.setBean(rental);
    }

    public void addSaveListener(ComponentEventListener<SaveEvent> listener) {
        addListener(SaveEvent.class, listener);
    }

    public void addCloseListener(ComponentEventListener<CloseEvent> listener) {
        addListener(CloseEvent.class, listener);
    }

    private void updatePrice() {
        try {
            long dayss = ChronoUnit.DAYS.between(rentalStart.getValue(), rentalEnd.getValue());
            long kmss = kmEnd.getValue() - kmStart.getValue();
            double ppd = carService.getCarById(Long.parseLong(RentalsNewView.getParametersMap().get("car").get(0))).getPricePerDay();
            double ppk = carService.getCarById(Long.parseLong(RentalsNewView.getParametersMap().get("car").get(0))).getPricePerKm();

            double s = dayss * ppd + kmss * ppk;
            double mw = s * 0.19;
            double sOMG = s + mw;

            DecimalFormat format = new DecimalFormat("#,##0.00");

            daysValue.setText(String.valueOf(dayss));
            kmsValue.setText(String.valueOf(kmss));
            pricePerDayValue.setText(format.format(ppd) + " €");
            pricePerKmValue.setText(format.format(ppk) + " €");
            sumValue.setText(format.format(s) + " €");
            mwstValue.setText(format.format(mw) + " €");
            sumOMGValue.setText(format.format(sOMG) + " €");
        } catch (Exception ignored) {

        }
    }

    @Getter
    public static abstract class RentalFormEvent extends ComponentEvent<RentalsNewForm> {
        private final Rental rental;

        protected RentalFormEvent(RentalsNewForm source, Rental rental) {
            super(source, false);
            this.rental = rental;
        }

    }

    public static class SaveEvent extends RentalFormEvent {
        SaveEvent(RentalsNewForm source, Rental rental) {
            super(source, rental);
        }
    }

    public static class CloseEvent extends RentalFormEvent {
        CloseEvent(RentalsNewForm source) {
            super(source, null);
        }
    }

}
