package de.teamg.antique.views.employee;


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
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.teamg.antique.data.entity.Person;
import de.teamg.antique.data.service.PersonService;
import lombok.Getter;

@SpringComponent
@UIScope
public class EmployeesForm extends FormLayout {

    private final Button save = new Button("Speichern");
    private final Button delete = new Button("Löschen");
    private final Button close = new Button("Abbrechen");

    PersonService personService;

    Binder<Person> binder = new BeanValidationBinder<>(Person.class);

    public EmployeesForm(PersonService personService) {
        this.personService = personService;

        addClassName("employee-form");

        TextField firstName = new TextField("Vorname");
        TextField lastName = new TextField("Nachname");
        TextField street = new TextField("Straße");
        TextField city = new TextField("Stadt");
        TextField postCode = new TextField("Postleitzahl");
        TextField country = new TextField("Land");
        DatePicker dateOfBirth = new DatePicker("Geburtsdatum");
        TextField phone = new TextField("Telefonnummer");

        binder.forField(firstName).asRequired().bind("firstName");
        binder.forField(lastName).asRequired().bind("lastName");
        binder.forField(street).asRequired().bind("street");
        binder.forField(city).asRequired().bind("city");
        binder.forField(postCode).asRequired().bind("postCode");
        binder.forField(country).asRequired().bind("country");
        binder.forField(dateOfBirth).asRequired().bind("dateOfBirth");
        binder.forField(phone).asRequired().bind("phone");

        binder.bindInstanceFields(this);

        add(firstName, lastName, street, city, postCode, country, dateOfBirth, phone, createButtonsLayout());

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

        binder.addStatusChangeListener(event -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }


    public void setEmployee(Person employee) {
        binder.setBean(employee);

        if (employee != null) delete.setVisible(personService.employeeExistsById(employee.getId()));
    }

    @Getter
    public static abstract class EmployeeFormEvent extends ComponentEvent<EmployeesForm> {
        private final Person employee;

        protected EmployeeFormEvent(EmployeesForm source, Person employee) {
            super(source, false);
            this.employee = employee;
        }

    }

    public static class SaveEvent extends EmployeeFormEvent {
        SaveEvent(EmployeesForm source, Person employee) {
            super(source, employee);
        }
    }

    public static class DeleteEvent extends EmployeeFormEvent {
        DeleteEvent(EmployeesForm source, Person employee) {
            super(source, employee);
        }

    }

    public static class CloseEvent extends EmployeeFormEvent {
        CloseEvent(EmployeesForm source) {
            super(source, null);
        }
    }

    public void addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        addListener(DeleteEvent.class, listener);
    }

    public void addSaveListener(ComponentEventListener<SaveEvent> listener) {
        addListener(SaveEvent.class, listener);
    }

    public void addCloseListener(ComponentEventListener<CloseEvent> listener) {
        addListener(CloseEvent.class, listener);
    }

}
