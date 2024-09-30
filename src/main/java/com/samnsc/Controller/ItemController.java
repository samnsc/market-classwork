package com.samnsc.Controller;

import com.samnsc.Model.Item;
import com.samnsc.View.ItemView;

public class ItemController {
    private final Item item;
    private final ItemView itemView;
    private final CashierPanelController parent;

    public ItemController(Item item, CashierPanelController parent) {
        this.item = item;
        this.parent = parent;

        itemView = new ItemView(item, e -> parent.removeItem(this.item.getProduct().getProductCode()));
    }

    public Item getItem() {
        return item;
    }

    public ItemView getItemView() {
        return itemView;
    }
}
