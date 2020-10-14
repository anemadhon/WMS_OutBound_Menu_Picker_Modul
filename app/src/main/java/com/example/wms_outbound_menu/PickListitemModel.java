package com.example.wms_outbound_menu;

import java.io.Serializable;

public class PickListitemModel implements Serializable {
    private String itemCode;
    private String itemName;
    private String totalToPick;
    private String remainQty;
    private String uom;

    public PickListitemModel(String itemCode, String itemName, String totalToPick, String remainQty, String uom) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.totalToPick = totalToPick;
        this.remainQty = remainQty;
        this.uom = uom;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getTotalToPick() {
        return totalToPick;
    }

    public void setTotalToPick(String totalToPick) {
        this.totalToPick = totalToPick;
    }

    public String getRemainQty() {
        return remainQty;
    }

    public void setRemainQty(String remainQty) {
        this.remainQty = remainQty;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }
}
