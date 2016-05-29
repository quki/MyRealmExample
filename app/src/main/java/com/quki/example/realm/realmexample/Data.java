package com.quki.example.realm.realmexample;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by quki on 2016-05-30.
 */
public class Data extends RealmObject {
    @Required // Name은 null이 될 수 없음
    private String name;
    private int number;
    public void setName(String name){
        this.name = name;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName(){
        return name;
    }
    public int getNumber(){
        return number;
    }
}
