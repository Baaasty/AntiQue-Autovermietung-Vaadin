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
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.teamg.antique.data.entity.Person;
import de.teamg.antique.data.service.PersonService;
import lombok.Getter;

@SpringComponent
@UIScope
public class EmployeesForm extends FormLayout {
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

    public EmployeesForm(PersonService personService) {
        this.personService = personService;

        addClassName("employee-form");

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


    public void setEmployee(Person employee) {
        binder.setBean(employee);

        if (employee != null)
            delete.setVisible(personService.employeeExistsById(employee.getId()));
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
