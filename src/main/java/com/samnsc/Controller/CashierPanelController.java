package com.samnsc.Controller;

import com.samnsc.Database;
import com.samnsc.Model.Item;
import com.samnsc.Model.User;
import com.samnsc.Model.Worker;
import com.samnsc.View.CashierPanelView;

import javax.swing.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CashierPanelController {
    private final CashierPanelView cashierPanelView;
    private final Map<String, ItemController> addedItems;
    private final Worker cashier;

    public CashierPanelController(Worker cashier) {
        this.cashier = cashier;
        addedItems = new HashMap<>();
        cashierPanelView = new CashierPanelView(e -> addItemToCart(), e -> finalizePurchase());
    }

    public CashierPanelView getCashierPanelView() {
        return cashierPanelView;
    }

    private void addItemToCart() {
        String productCode = cashierPanelView.getProductInputFieldText();
        float productAmount = cashierPanelView.getSpinnerValue();

        ItemController result = addedItems.get(productCode);
        try {
            if (result == null) {
                ItemController newItem = new ItemController(new Item(productCode, productAmount), this);

                addedItems.put(productCode, newItem);
                cashierPanelView.addToItemList(newItem.getItemView());

                cashierPanelView.setErrorLabelVisibility(false);

                cashierPanelView.clearInputs();
            } else {
                cashierPanelView.setErrorLabelText("Item já adicionado à lista!");
                cashierPanelView.setErrorLabelVisibility(true);
            }
        } catch (InstantiationException exception) {
            cashierPanelView.setErrorLabelText("Item não encontrado!");
            cashierPanelView.setErrorLabelVisibility(true);
        } catch (SQLException exception) {
            cashierPanelView.setErrorLabelText("Erro ao conectar à base de dados!");
            cashierPanelView.setErrorLabelVisibility(true);
        } catch (IllegalArgumentException exception) {
            cashierPanelView.setErrorLabelText("Quantidade do item não pode ser zero ou decimal!");
            cashierPanelView.setErrorLabelVisibility(true);
        }
    }

    public void removeItem(String productCode) {
        ItemController itemController = addedItems.get(productCode);
        cashierPanelView.removeFromItemList(itemController.getItemView());

        addedItems.remove(productCode);
    }

    private void finalizePurchase() {
        if (addedItems.isEmpty()) {
            cashierPanelView.setErrorLabelText("Não é possível realizar uma compra sem itens!");
            cashierPanelView.setErrorLabelVisibility(true);
            return;
        }

        List<Item> items = new ArrayList<>();
        double purchasePrice = 0;
        for (Map.Entry<String, ItemController> entry : addedItems.entrySet()) {
            Item item = entry.getValue().getItem();
            items.add(item);
            purchasePrice += item.getAmount() * item.getProduct().getSellingPrice();
        }

        String clientIdentification = JOptionPane.showInputDialog(null, "Digite o CPF do cliente no formato (xxx.xxx.xxx-xx):");
        User user = User.getUserFromIdentification(clientIdentification);

        if (clientIdentification != null && !clientIdentification.isEmpty() && user == null) {
            cashierPanelView.setErrorLabelText("Dados do cliente não foram encontrados!");
            cashierPanelView.setErrorLabelVisibility(true);
            return;
        }

        String[] options = {"Cancelar", "Dinheiro", "Cartão de Crédito", "Cartão de Débito", "Cheque", "Pix"};
        int choiceIndex = JOptionPane.showOptionDialog(
                null,
                String.format("O preço total foi: R$%.2f\nO número total de itens foi: %d", purchasePrice, items.size()),
                "Método de Pagamento",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choiceIndex == 0 || choiceIndex == JOptionPane.CLOSED_OPTION) return;

        String paymentMethod = switch (choiceIndex) {
            case 1 -> "MONEY";
            case 2 -> "CREDIT_CARD";
            case 3 -> "DEBIT_CARD";
            case 4 -> "CHECK";
            case 5 -> "PIX";
            default -> throw new IllegalStateException("Unexpected value: " + choiceIndex);
        };

        boolean successful = addPurchaseToDatabase(items, purchasePrice, paymentMethod, user == null ? -1 : user.getId());

        if (!successful) {
            cashierPanelView.setErrorLabelText("Ocorreu um erro ao finalizar a compra!");
            cashierPanelView.setErrorLabelVisibility(true);
        } else {
            for (Map.Entry<String, ItemController> entry : addedItems.entrySet()) {
                cashierPanelView.removeFromItemList(entry.getValue().getItemView());
            }
            addedItems.clear();
            JOptionPane.showMessageDialog(null, "Compra concluída com sucesso!");
        }
    }

    private boolean addPurchaseToDatabase(List<Item> items, double purchasePrice, String paymentMethod, int userId) {
        int generatedKey = -1;
        try (PreparedStatement purchaseStatement = Database.getConnection().prepareStatement("INSERT INTO \"purchase\"" +
                "   (purchase_price, payment_method, cashier_id, user_id)" +
                "VALUES" +
                "   (?, ?, ?, ?);", PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement itemStatement = Database.getConnection().prepareStatement("INSERT INTO \"item\"" +
                     "  (purchase_id, product_id, amount, individual_price)" +
                     "VALUES" +
                     "  (?, ?, ?, ?);")
        ) {
            purchaseStatement.setDouble(1, purchasePrice);
            purchaseStatement.setString(2, paymentMethod);
            purchaseStatement.setInt(3, cashier.getId());
            if (userId == -1) {
                purchaseStatement.setNull(4, Types.INTEGER);
            } else {
                purchaseStatement.setInt(4, userId);
            }

            purchaseStatement.execute();
            ResultSet generatedKeyResultSet = purchaseStatement.getGeneratedKeys();
            generatedKey = generatedKeyResultSet.getInt(1);

            for (Item item : items) {
                itemStatement.setInt(1, generatedKey);
                itemStatement.setInt(2, item.getProduct().getId());
                itemStatement.setDouble(3, item.getAmount());
                itemStatement.setDouble(4, item.getProduct().getSellingPrice());

                itemStatement.addBatch();
            }
            itemStatement.executeBatch();
        } catch (SQLException exception) {
            if (generatedKey != -1) {
                try (PreparedStatement removePurchaseStatement = Database.getConnection().prepareStatement("DELETE FROM \"purchase\" WHERE id = ?")) {
                    removePurchaseStatement.setInt(1, generatedKey);
                    removePurchaseStatement.execute();
                } catch (SQLException sqlException) {
                    System.exit(1); // something has gone catastrophically wrong
                }
            }

            return false;
        }

        return true;
    }
}
