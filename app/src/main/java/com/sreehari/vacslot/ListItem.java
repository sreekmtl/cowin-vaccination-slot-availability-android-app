package com.sreehari.vacslot;

public class ListItem {

    private final String centerName;

    public String getCenterName() {
        return centerName;
    }

    public String getFare() {
        return fare;
    }

    public String getSessions() {
        return sessions;
    }

    public String getDistrictName(){
        return districtName;
    }
    public String getPincode(){
        return pincode;
    }

    private final String fare;
    private final String sessions;
    private final String districtName;
    private final String pincode;

    public ListItem(String centerName, String fare, String sessions, String districtName, String pincode) {
        this.centerName = centerName;
        this.fare = fare;
        this.sessions = sessions;
        this.districtName=districtName;
        this.pincode=pincode;
    }
}
