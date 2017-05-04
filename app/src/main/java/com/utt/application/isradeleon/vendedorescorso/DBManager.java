package com.utt.application.isradeleon.vendedorescorso;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Isra on 4/11/2017.
 */

public class DBManager {
    private DBHelper dbHelper;
    SQLiteDatabase database;

    public DBManager(Context context) {
        dbHelper=new DBHelper(context);
        database=dbHelper.getWritableDatabase();
    }

    public void truncateTable(){
        database.execSQL("delete from "+DataTable.TABLE_NAME);
    }

    public void updateToken(String newtoken){
        ContentValues cv=new ContentValues();
        cv.put("token",newtoken);
        Cursor cursor=getData();
        if (cursor.moveToFirst()){
            database.update(
                    DataTable.TABLE_NAME,
                    cv,DataTable.C_ID+"=?",
                    new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex(DataTable.C_ID)))}
            );
        }
    }

    public Cursor getData(){
        return database.query(DataTable.TABLE_NAME,new String[]{"*"},null,null,null,null,null);
    }

    public void insertData(int id, String name, String email, String token, int id_token){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DataTable.C_ID,id);
        contentValues.put(DataTable.C_EMAIL,email);
        contentValues.put(DataTable.C_NAME,name);
        contentValues.put(DataTable.C_ID_TOKEN,id_token);
        contentValues.put(DataTable.C_TOKEN,token);
        database.insert(DataTable.TABLE_NAME,null,contentValues);
    }

    public boolean isEmpty(){
        Cursor c=database.query(
                DataTable.TABLE_NAME,
                new String[]{DataTable.C_ID,DataTable.C_NAME,DataTable.C_EMAIL,DataTable.C_TOKEN,DataTable.C_TOKEN},
                null,null,null,null,null);
        if (c.getCount()==0)
            return true;
        return false;
    }
}
