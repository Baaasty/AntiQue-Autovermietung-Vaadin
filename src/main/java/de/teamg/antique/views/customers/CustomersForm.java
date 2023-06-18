package de.teamg.antique.views.customers;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.teamg.antique.data.entity.Person;
import de.teamg.antique.data.service.PersonService;
import lombok.Getter;

@SpringComponent
@UIScope
public class CustomersForm extends FormLayout {
    TextField firstName = new TextField("Vorname");
    TextField lastName = new TextField("Nachname");
    TextField street = new TextField("Straße");
    TextField city = new TextField("Stadt");
    TextField postCode = new TextField("Postleitzahl");
    TextField country = new TextField("Land");
    DatePicker dateOfBirth = new DatePicker("Geburtsdatum");
    TextField phone = new TextField("Telefonnummer");
    
    Button save = new Button("Speichern");
    Button delete = new Button("Löschen");
    Button close = new Button("Abbrechen");

    PersonService personService;

    Binder<Person> binder = new BeanValidationBinder<>(Person.class);

    public CustomersForm(PersonService personService) {
        this.personService = personService;

        addClassName("customer-form");

        binder.forField(firstName).asRequired().bind("firstName");
        binder.forField(lastName).asRequired().bind("lastName");
        binder.forField(street).asRequired().bind("street");
        binder.forField(city).asRequired().bind("city");
        binder.forField(postCode).asRequired().bind("postCode");
        binder.forField(country).asRequired().bind("country");
        binder.forField(dateOfBirth).asRequired().bind("dateOfBirth");
        binder.forField(phone).asRequired().bind("phone");
        
        binder.bindInstanceFields(this);

        add(
                firstName,
                lastName,
                street,
                city,
                postCode,
                country,
                dateOfBirth,
                phone,
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


    public void setCustomer(Person customer) {
        binder.setBean(customer);

        if (customer != null)
            delete.setVisible(personService.customerExistsById(customer.getId()));
    }

    @Getter
    public static abstract class CustomerFormEvent extends ComponentEvent<CustomersForm> {
        private final Person customer;

        protected CustomerFormEvent(CustomersForm source, Person customer) {
            super(source, false);
            this.customer = customer;
        }

    }

    public static class SaveEvent extends CustomerFormEvent {
        SaveEvent(CustomersForm source, Person customer) {
            super(source, customer);
        }
    }

    public static class DeleteEvent extends CustomerFormEvent {
        DeleteEvent(CustomersForm source, Person customer) {
            super(source, customer);
        }

    }

    public static class CloseEvent extends CustomerFormEvent {
        CloseEvent(CustomersForm source) {
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
