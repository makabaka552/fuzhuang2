package model;

import java.util.List;

public class ClearCartRequest {
    private List<Long> itemIds;

    // Getter
    public List<Long> getItemIds() {
        return itemIds;
    }

    // Setter
    public void setItemIds(List<Long> itemIds) {
        this.itemIds = itemIds;
    }
}