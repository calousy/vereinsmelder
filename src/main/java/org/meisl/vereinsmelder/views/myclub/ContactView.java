package org.meisl.vereinsmelder.views.myclub;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.meisl.vereinsmelder.views.MainLayout;

import jakarta.annotation.security.RolesAllowed;

@PageTitle("Mein Verein")
@Route(value = "my-club/contact", layout = MainLayout.class)
@RolesAllowed({"SUPERMANAGER", "ADMIN"})
public class ContactView extends FormLayout {

    public ContactView() {

        setResponsiveSteps(new ResponsiveStep("0", 1),
                new ResponsiveStep("700px", 2),
                new ResponsiveStep("900px", 3),
                new ResponsiveStep("1300px", 4)
                );

        TextField firstNameTf = new TextField();
        TextField lastNameTf = new TextField();
        TextField streetName = new TextField();
        NumberField zipCode = new NumberField();
        TextField venue = new TextField();
        TextField phone = new TextField();
        TextField mobilePhone = new TextField();
        EmailField emailField = new EmailField();

        addFormItem(firstNameTf, "Vorname");
        addFormItem(lastNameTf, "Nachname");
        addFormItem(streetName, "Straße");
        addFormItem(zipCode, "PLZ");
        addFormItem(venue, "Ort");
        addFormItem(phone, "Telefon");
        addFormItem(mobilePhone, "Handy");
        addFormItem(emailField, "E-Mail");

    }
}
