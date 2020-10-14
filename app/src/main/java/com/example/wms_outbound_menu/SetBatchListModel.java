package com.example.wms_outbound_menu;

public class SetBatchListModel {
    private String batchNo;
    private String expDate;
    private Float batchQuantity;

    public SetBatchListModel(String batchNo, String expDate, Float batchQuantity) {
        this.batchNo = batchNo;
        this.expDate = expDate;
        this.batchQuantity = batchQuantity;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public Float getBatchQuantity() {
        return batchQuantity;
    }

    public void setBatchQuantity(Float batchQuantity) {
        this.batchQuantity = batchQuantity;
    }
}
