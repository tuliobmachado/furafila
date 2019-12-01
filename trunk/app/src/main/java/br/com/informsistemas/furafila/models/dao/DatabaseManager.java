package br.com.informsistemas.furafila.models.dao;

import android.content.Context;

public class DatabaseManager {

    private static DatabaseManager instance;
    private DatabaseHelper helper;

    public static void init(Context context){
        if (null == instance){
            instance = new DatabaseManager(context);
        }
    }

    public static DatabaseManager getInstance(){
        return instance;
    }

    private DatabaseManager(Context context){
        helper = new DatabaseHelper(context);
    }

    public DatabaseHelper getHelper(){
        return helper;
    }
}
