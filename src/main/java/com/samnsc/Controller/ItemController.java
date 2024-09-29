package com.samnsc.Controller;

import com.samnsc.Model.Item;
import com.samnsc.View.ItemView;

public class ItemController {
    private final Item item;
    private final ItemView itemView;

    public ItemController(Item item) {
        this.item = item;

        itemView = new ItemView(item);
    }

    public Item getItem() {
        return item;
    }

    public ItemView getItemView() {
        return itemView;
    }
}
