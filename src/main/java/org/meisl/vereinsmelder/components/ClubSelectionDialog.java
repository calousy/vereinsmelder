package org.meisl.vereinsmelder.components;

import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import org.meisl.vereinsmelder.data.entity.Club;
import org.meisl.vereinsmelder.data.service.ClubService;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.Set;

public class ClubSelectionDialog extends ConfirmDialog {

    private final MultiSelectComboBox<Club> select;

    public ClubSelectionDialog(ClubService clubService) {

        setHeader("Verein auswählen");

        CallbackDataProvider<Club, String> clubDataProvider = DataProvider.fromFilteringCallbacks(query ->
                        clubService.list(PageRequest.of(query.getPage(), query.getPageSize())).get(),
                query -> clubService.count()
        );

        select = new MultiSelectComboBox<>();
        select.setWidthFull();
        select.setItems(clubDataProvider);

        setCancelable(true);
        setText(select);
    }

    public Optional<Set<Club>> getSelection() {
        return select.getOptionalValue();
    }
}
