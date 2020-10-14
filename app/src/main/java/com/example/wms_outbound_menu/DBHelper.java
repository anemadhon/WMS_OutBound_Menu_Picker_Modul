package com.example.wms_outbound_menu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "wmsMobileSQL.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("Create Table picker(id INTEGER primary key autoincrement, json TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("Drop Table if Exists picker");
    }

    public boolean insertPickerData(String json) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("json", json);

        long data = DB.insert("picker", null, contentValues);
        if (data == -1)
            return false;
        else
            return true;
    }

    public boolean deletePickerData() {
        SQLiteDatabase DB = this.getWritableDatabase();

        long data = DB.delete("picker",null,null);
        if (data == -1)
            return false;
        else
            return true;
    }

    public Cursor getPickerData() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select json From picker", null);
        return cursor;
    }
}
