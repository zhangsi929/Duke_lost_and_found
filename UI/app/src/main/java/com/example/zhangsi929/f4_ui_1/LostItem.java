package com.example.zhangsi929.f4_ui_1;

import java.util.Date;

/**
 * Created by zhangsi929 on 17/3/7.
 */
public class LostItem {
    private Date lostTime;
    private Date postedTime;
    private long reportPhone;
    private String itemName;
    private String reportEmail;
    private String lostLocation;
    private String lostDescription;
    private String reporterName;
    private String encodedImage;
    private int lost_category;
    // get private data
    public Date getLostTime() {
        return lostTime;
    }
    public Date getPostedTime() {
        return postedTime;
    }
    public long getReportPhone() {
        return  reportPhone;
    }
    public String getItemName() {
        return itemName;
    }
    public String getReportEmail(){
        return reportEmail;
    }
    public String getLostLocation(){
        return lostLocation;
    }
    public String getLostDescription() {
        return lostDescription;
    }
    public String getReporterName() {
        return reporterName;
    }
    public String getEncodedImage() {
        return encodedImage;
    }
    public int getCategory() { return lost_category; }
    //set private data
    public void setLostTime(Date lostTime){
        this.lostTime = lostTime;
    }
    public void setReportPhone(long reportPhone){
        this.reportPhone = reportPhone;
    }
    public void setItemName(String itemName){
        this.itemName = itemName;
    }
    public void setPostedTime(Date postedTime) {
        this.postedTime = postedTime;
    }
    public void setReportEmail(String reportEmail) {
        this.reportEmail = reportEmail;
    }
    public void setLostLocation(String lostLocation) {
        this.lostLocation = lostLocation;
    }
    public void setLostDescription(String lostDescription){
        this.lostDescription = lostDescription;
    }
    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }
    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }
    public void setCategory(int category) { this.lost_category = category; }

}
