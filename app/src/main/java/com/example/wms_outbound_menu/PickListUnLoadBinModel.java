package com.example.wms_outbound_menu;

import java.io.Serializable;

public class PickListUnLoadBinModel implements Serializable {
    private String unLoadBin;

    public PickListUnLoadBinModel(String unLoadBin) {
        this.unLoadBin = unLoadBin;
    }

    public String getUnLoadBin() {
        return unLoadBin;
    }

    public void setUnLoadBin(String unLoadBin) {
        this.unLoadBin = unLoadBin;
    }
}
