package org.meisl.vereinsmelder.components;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import org.meisl.vereinsmelder.data.entity.Club;
import org.meisl.vereinsmelder.data.service.ClubService;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public class ClubSelectionDialog extends ConfirmDialog {

    private final Select<Club> select;

    public ClubSelectionDialog(ClubService clubService) {

        setHeader("Verein auswählen");

        CallbackDataProvider<Club, Void> clubDataProvider = DataProvider.fromCallbacks(query ->
                        clubService.list(PageRequest.of(query.getPage(), query.getPageSize())).get(),
                query -> clubService.count()
        );

        select = new Select<>();
        select.setWidthFull();
        select.setItems(clubDataProvider);

        setCancelable(true);
        add(select);
    }

    public Optional<Club> getSelection() {
        return select.getOptionalValue();
    }
}
