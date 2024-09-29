package com.samnsc.Controller;

import com.samnsc.Model.Item;
import com.samnsc.View.CashierPanelView;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CashierPanelController {
    private final CashierPanelView cashierPanelView;
    private final Map<String, ItemController> addedItems;

    public CashierPanelController() {
        addedItems = new HashMap<>();
        cashierPanelView = new CashierPanelView(e -> CashierPanelView());
    }

    public CashierPanelView getCashierPanelView() {
        return cashierPanelView;
    }

    private void CashierPanelView() {
        String productCode = cashierPanelView.getProductInputFieldText();
        float productAmount = cashierPanelView.getSpinnerValue();

        ItemController result = addedItems.get(productCode);
        try {
            if (result == null) {
                ItemController newItem = new ItemController(new Item(productCode, productAmount));

                addedItems.put(productCode, newItem);
                cashierPanelView.addToItemList(newItem.getItemView());

                cashierPanelView.setErrorLabelVisibility(false);
            } else {
                cashierPanelView.setErrorLabelText("Item já adicionado à lista!");
                cashierPanelView.setErrorLabelVisibility(true);
            }
        } catch (InstantiationException exception ) {
            cashierPanelView.setErrorLabelText("Item não encontrado!");
            cashierPanelView.setErrorLabelVisibility(true);
        } catch (SQLException exception) {
            cashierPanelView.setErrorLabelText("Erro ao conectar à base de dados!");
            cashierPanelView.setErrorLabelVisibility(true);
        }
    }
}
