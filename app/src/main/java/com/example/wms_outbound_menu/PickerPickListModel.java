package com.example.wms_outbound_menu;

import java.io.Serializable;

public class PickerPickListModel implements Serializable {
    private String pickNumber;
    private String pickDate;
    private String pickMemo;
    private String pickCardCode;
    private String pickCardName;

    public PickerPickListModel(String pickNumber, String pickDate, String pickMemo, String pickCardCode, String pickCardName) {
        this.pickNumber = pickNumber;
        this.pickDate = pickDate;
        this.pickMemo = pickMemo;
        this.pickCardCode = pickCardCode;
        this.pickCardName = pickCardName;
    }

    public String getPickNumber() {
        return pickNumber;
    }

    public void setPickNumber(String pickNumber) {
        this.pickNumber = pickNumber;
    }

    public String getPickDate() {
        return pickDate;
    }

    public void setPickDate(String pickDate) {
        this.pickDate = pickDate;
    }

    public String getPickMemo() {
        return pickMemo;
    }

    public void setPickMemo(String pickMemo) {
        this.pickMemo = pickMemo;
    }

    public String getPickCardCode() {
        return pickCardCode;
    }

    public void setPickCardCode(String pickCardCode) {
        this.pickCardCode = pickCardCode;
    }

    public String getPickCardName() {
        return pickCardName;
    }

    public void setPickCardName(String pickCardName) {
        this.pickCardName = pickCardName;
    }
}
