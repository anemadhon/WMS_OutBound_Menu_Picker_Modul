package com.example.wms_outbound_menu;

import java.util.List;

public class SetPostModel {
    private String doNo;
    private String postingDate;
    private String toPlant;
    private String plant;
    private String remark;
    private String storageLocation;

    private List<SetItemDetailListModel> details;

    public SetPostModel(String doNo, String postingDate, String toPlant, String plant, String remark, String storageLocation, List<SetItemDetailListModel> details) {
        this.doNo = doNo;
        this.postingDate = postingDate;
        this.toPlant = toPlant;
        this.plant = plant;
        this.remark = remark;
        this.details = details;
        this.storageLocation = storageLocation;
    }

    public String getDoNo() {
        return doNo;
    }

    public void setDoNo(String doNo) {
        this.doNo = doNo;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate = postingDate;
    }

    public String getToPlant() {
        return toPlant;
    }

    public void setToPlant(String toPlant) {
        this.toPlant = toPlant;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public List<SetItemDetailListModel> getDetails() {
        return details;
    }

    public void setDetails(List<SetItemDetailListModel> details) {
        this.details = details;
    }
}
