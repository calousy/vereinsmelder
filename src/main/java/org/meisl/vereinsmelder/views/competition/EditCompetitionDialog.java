package org.meisl.vereinsmelder.views.competition;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import org.meisl.vereinsmelder.data.entity.Competition;
import org.meisl.vereinsmelder.data.entity.CompetitionCategory;

import java.util.UUID;

public class EditCompetitionDialog extends ConfirmDialog {

    private final Competition competition;

    public EditCompetitionDialog(Competition competition) {
        this.competition = competition;

        setCancelable(true);

        UUID id = competition.getId();
        boolean isNewUser = id == null;
        setHeader(isNewUser ? "Wettbewerb erstellen" : "Wettbewerb bearbeiten");

        FormLayout fl = new FormLayout();
        TextField competitionNameTf = new TextField();
        DatePicker datePicker = new DatePicker();
        DateTimePicker dateTimePicker = new DateTimePicker();
        Select<CompetitionCategory> categorySelection = new Select<>();
        categorySelection.setItems(CompetitionCategory.getAllCategories());
        categorySelection.setItemLabelGenerator(CompetitionCategory::getName);

        fl.addFormItem(competitionNameTf, "Name");
        fl.addFormItem(datePicker, "Datum");
        fl.addFormItem(categorySelection, "Kategorie");
        fl.addFormItem(dateTimePicker, "Ende Registrierung");
        add(fl);

        Binder<Competition> competitionBinder = new Binder<>();
        competitionBinder.bind(competitionNameTf, Competition::getName, Competition::setName);
        competitionBinder.bind(datePicker, Competition::getDate, Competition::setDate);
        competitionBinder.bind(categorySelection, (ValueProvider<Competition, CompetitionCategory>) comp ->
                        CompetitionCategory.get(comp.getCategory()),
                (Setter<Competition, CompetitionCategory>) (comp, competitionCategory) -> {
                    if (competitionCategory != null) {
                        comp.setCategory(competitionCategory.getName());
                    } else {
                        comp.setCategory(null);
                    }
                });
        competitionBinder.bind(dateTimePicker, Competition::getRegistrationEnd, Competition::setRegistrationEnd);

        competitionBinder.setBean(competition);
    }

    public Competition getCompetition() {
        return competition;
    }
}
