package com.utt.application.isradeleon.vendedorescorso;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Isra on 4/11/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String db_name="log_data.sqlite";
    private static final int db_version=1;

    public DBHelper(Context context) {
        super(context, db_name, null, db_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DataTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DataTable.TABLE_NAME);
        onCreate(db);
    }
}
