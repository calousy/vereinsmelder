package org.meisl.vereinsmelder.views.masterdata.club;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.meisl.vereinsmelder.data.entity.Club;

import java.util.UUID;

public class EditClubDialog extends ConfirmDialog {

    private final Club club;

    public Club getClub() {
        return club;
    }

    public EditClubDialog(Club club) {
        this.club = club;

        setCancelable(true);

        UUID id = club.getId();
        boolean isNewClub = id == null;
        setHeader(isNewClub ? "Verein erstellen" : "Verein bearbeiten");

        TextField vereinNameTf = new TextField();

        FormLayout fl = new FormLayout();
        fl.addFormItem(vereinNameTf, "Verein");
        add(fl);

        Binder<Club> clubBinder = new Binder<>();
        clubBinder.bind(vereinNameTf, Club::getName, Club::setName);

        clubBinder.setBean(club);

        vereinNameTf.focus();
    }
}
