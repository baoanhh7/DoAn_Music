package com.example.doan_music.model;

import java.io.Serializable;

public class Types implements Serializable {

    private int TypeID;
    private String NameType;

    public Types(int typeID, String nameType) {
    }

    public int getTypeID() {
        return TypeID;
    }

    public void setTypeID(int typeID) {
        TypeID = typeID;
    }

    public String getNameType() {
        return NameType;
    }

    public void setNameType(String nameType) {
        NameType = nameType;
    }
    
}
