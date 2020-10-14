package com.example.wms_outbound_menu;

import java.io.Serializable;
import java.util.List;

public class SetItemDetailListModel implements Serializable {
    private String materialNo;
    private String uom;
    private String num;
    private Float outstandingQty;
    private Float grQuantity;
    private List<SetBatchListModel> listBatches;

    public SetItemDetailListModel(String materialNo, String uom, String num, Float outstandingQty, Float grQuantity, List<SetBatchListModel> listBatches) {
        this.materialNo = materialNo;
        this.uom = uom;
        this.num = num;
        this.outstandingQty = outstandingQty;
        this.grQuantity = grQuantity;
        this.listBatches = listBatches;
    }

    public String getMaterialNo() {
        return materialNo;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public Float getOutstandingQty() {
        return outstandingQty;
    }

    public void setOutstandingQty(Float outstandingQty) {
        this.outstandingQty = outstandingQty;
    }

    public List<SetBatchListModel> getListBatches() {
        return listBatches;
    }

    public void setListBatches(List<SetBatchListModel> listBatches) {
        this.listBatches = listBatches;
    }

    public float getGrQuantity() {
        return grQuantity;
    }

    public void setGrQuantity(float grQuantity) {
        this.grQuantity = grQuantity;
    }
}
