package com.example.wms_outbound_menu;

import java.io.Serializable;

public class PickListBatchModel implements Serializable {
    private String batchNo;
    private String avlQty;
    private String expDate;

    public PickListBatchModel(String batchNo, String avlQty, String expDate) {
        this.batchNo = batchNo;
        this.avlQty = avlQty;
        this.expDate = expDate;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getAvlQty() {
        return avlQty;
    }

    public void setAvlQty(String avlQty) {
        this.avlQty = avlQty;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }
}
