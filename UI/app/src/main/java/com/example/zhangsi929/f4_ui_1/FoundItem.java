package com.example.zhangsi929.f4_ui_1;
import java.util.Date;

/**
 * Created by zhangsi929 on 17/3/7.
 */
// Data modal for Found Item
public class FoundItem {
    private Date foundTime;
    private Date postedTime;
    private long reportPhone;
    private String itemName;
    private String reportEmail;
    private String foundLocation;
    private String foundDescription;
    private String reporterName;
    private String encodedImage;
    private int found_category;
    // get private data
    public Date getFoundTime() {
        return foundTime;
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
    public String getFoundLocation(){
        return foundLocation;
    }
    public String getFoundDescription() {
        return foundDescription;
    }
    public String getReporterName() {
        return reporterName;
    }
    public String getEncodedImage() {
        return encodedImage;
    }
    public int getCategory() {return found_category; }
    //set private data
    public void setFoundTime(Date foundTime){
        this.foundTime = foundTime;
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
    public void setFoundLocation(String foundLocation) {
        this.foundLocation = foundLocation;
    }
    public void setFoundDescription(String foundDescription){
        this.foundDescription = foundDescription;
    }
    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }
    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }
    public void setCategory(int category) { this.found_category = category; }
}
