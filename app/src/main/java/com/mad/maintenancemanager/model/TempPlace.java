package com.mad.maintenancemanager.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by lucaswinterbottom on 4/6/17.
 */

public class TempPlace {
    private String mID;
    private String mName;

    public TempPlace(String ID, String name) {
        mID = ID;
        mName = name;
    }

    public String getID() {
        return mID;
    }

    public void setID(String ID) {
        mID = ID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
