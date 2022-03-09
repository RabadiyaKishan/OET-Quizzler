package com.oet.quiz.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "UserData.db";
    public static final String TABLE_NAME = "UserData";

    public static final String ID = "ID";
    public static final String FirstName = "FirstName";
    public static final String LastName = "LastName";
    public static final String MobileNumber = "MobileNumber";
    public static final String DateTime = "DateTime";
    public static final String BoardID = "BoardID";
    public static final String StreamID = "StreamID";
    public static final String AuthID = "AuthID";
    public static final String StateID = "StateID";
    public static final String SchoolID = "SchoolID";
    public static final String CityID = "CityID";
    public static final String BoardName = "BoardName";
    public static final String StreamName = "StreamName";
    public static final String Email = "Email";
    public static final String PasswordHash = "PasswordHash";
    public static final String State = "State";
    public static final String SchoolName = "SchoolName";
    public static final String City = "City";

    private HashMap hp;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("+ID+" TEXT,"+FirstName+" TEXT,"+LastName+" TEXT,"+MobileNumber+" TEXT,"+DateTime+" TEXT,"+BoardID+" TEXT,"+StreamID+" TEXT,"+AuthID+" TEXT,"+StateID+" TEXT,"+SchoolID+" TEXT,"+CityID+" TEXT,"+BoardName+" TEXT,"+StreamName+" TEXT,"+Email+" TEXT,"+PasswordHash+" TEXT,"+State+" TEXT,"+SchoolName+" TEXT,"+City+" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + "");
        onCreate(db);
    }

    public void EmptyData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + "");
        onCreate(db);
    }

    public boolean insertContact(String ID,String FirstName,String LastName,String MobileNumber,String DateTime,String BoardID,String StreamID,String AuthID,String StateID,String SchoolID,String CityID,String BoardName,String StreamName,String Email,String PasswordHash,String State,String SchoolName,String City) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", ID);
        contentValues.put("FirstName", FirstName);
        contentValues.put("LastName", LastName);
        contentValues.put("MobileNumber", MobileNumber);
        contentValues.put("DateTime", DateTime);
        contentValues.put("BoardID", BoardID);
        contentValues.put("StreamID", StreamID);
        contentValues.put("AuthID", AuthID);
        contentValues.put("StateID", StateID);
        contentValues.put("SchoolID", SchoolID);
        contentValues.put("CityID", CityID);
        contentValues.put("BoardName", BoardName);
        contentValues.put("StreamName", StreamName);
        contentValues.put("Email", Email);
        contentValues.put("PasswordHash", PasswordHash);
        contentValues.put("State", State);
        contentValues.put("SchoolName", SchoolName);
        contentValues.put("City", City);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getData(int ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + ID + " = " + ID + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public Integer deleteContact(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ? ", new String[]{Integer.toString(id)});
    }

    public ArrayList<String> GetAllUserData() {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + "", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex(ID)));
            array_list.add(res.getString(res.getColumnIndex(FirstName)));
            array_list.add(res.getString(res.getColumnIndex(LastName)));
            array_list.add(res.getString(res.getColumnIndex(MobileNumber)));
            array_list.add(res.getString(res.getColumnIndex(DateTime)));
            array_list.add(res.getString(res.getColumnIndex(BoardID)));
            array_list.add(res.getString(res.getColumnIndex(StreamID)));
            array_list.add(res.getString(res.getColumnIndex(AuthID)));
            array_list.add(res.getString(res.getColumnIndex(StateID)));
            array_list.add(res.getString(res.getColumnIndex(SchoolID)));
            array_list.add(res.getString(res.getColumnIndex(CityID)));
            array_list.add(res.getString(res.getColumnIndex(BoardName)));
            array_list.add(res.getString(res.getColumnIndex(StreamName)));
            array_list.add(res.getString(res.getColumnIndex(Email)));
            array_list.add(res.getString(res.getColumnIndex(PasswordHash)));
            array_list.add(res.getString(res.getColumnIndex(State)));
            array_list.add(res.getString(res.getColumnIndex(SchoolName)));
            array_list.add(res.getString(res.getColumnIndex(City)));
            res.moveToNext();
        }
        return array_list;
    }

    public void TruncateTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + "");
        onCreate(db);
    }
}
