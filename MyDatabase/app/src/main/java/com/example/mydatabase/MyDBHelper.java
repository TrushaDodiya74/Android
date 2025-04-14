package com.example.mydatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {

    public MyDBHelper(Context context) {
        super(context, "studentdatabse", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE students (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, " + "roll_no INTEGER, address TEXT, city TEXT, age TEXT, email TEXT, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public long insertStudent(String name, String roll_no, String address, String city, int age, String email, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("roll_no", roll_no);
        values.put("address", address);
        values.put("city", city);
        values.put("age", age);
        values.put("email", email);
        values.put("password", password);
        long result = db.insert("students", null, values);
        db.close();
        return result;
    }
    public boolean checkLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM students WHERE email  = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isValid;
    }

    public Cursor allData()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM students", null);
    }

    public void delStud(String roll_no)
    {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("roll_no",roll_no);
        db.execSQL("delete from students where roll_no=roll_no");
        db.close();
    }
}
