package com.example.wms_outbound_menu;

import java.io.Serializable;

public class PickListBinModel implements Serializable {
    private String binLoc;
    private String qtyBinLoc;

    public PickListBinModel(String binLoc, String qtyBinLoc) {
        this.binLoc = binLoc;
        this.qtyBinLoc = qtyBinLoc;
    }

    public String getBinLoc() {
        return binLoc;
    }

    public void setBinLoc(String binLoc) {
        this.binLoc = binLoc;
    }

    public String  getQtyBinLoc() {
        return qtyBinLoc;
    }

    public void setQtyBinLoc(String  qtyBinLoc) {
        this.qtyBinLoc = qtyBinLoc;
    }
}
