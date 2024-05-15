package com.example.havenwomansafetyapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.havenwomansafetyapp.LocationData;

import java.util.ArrayList;
import java.util.List;

public class LocationDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "location.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "location";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_SYNCED = "synced"; // Added column for synced status

    public LocationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LATITUDE + " REAL, " +
                COLUMN_LONGITUDE + " REAL, " +
                COLUMN_ADDRESS + " TEXT, " +
                COLUMN_SYNCED + " INTEGER DEFAULT 0)"; // Default value for synced column is 0
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertLocation(double latitude, double longitude, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_ADDRESS, address);
        return db.insert(TABLE_NAME, null, values);
    }

    public void markLocationAsSynced(int locationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SYNCED, 1); // Mark location as synced by setting synced column to 1
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(locationId)});
        db.close();
    }

    @SuppressLint("Range")
    public List<LocationData> getUnsyncedLocations() {
        List<LocationData> unsyncedLocations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_SYNCED + " = 0";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    LocationData location = new LocationData();
                    location.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                    location.setLatitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE)));
                    location.setLongitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE)));
                    location.setAddress(cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)));
                    unsyncedLocations.add(location);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return unsyncedLocations;
    }
}
