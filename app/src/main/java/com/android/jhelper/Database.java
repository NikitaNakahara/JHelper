package com.android.jhelper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import java.util.regex.Pattern;

public class Database extends SQLiteOpenHelper {
    private final String WORDS_TABLE_NAME = "WORDS";
    private final String MARKERS_TABLE_NAME = "MARKERS";

    private final String W_JAP_TEXT_COL = "JAP";
    private final String W_RUS_TEXT_COL = "RUS";
    private final String W_DESC_TEXT_COL = "DESCRIPTION";

    private final String M_TEXT_COL = "TEXT";
    private final String M_USING_COL = "USING_COL";

    public Database(@Nullable Context context) {
        super(context, "dictionary.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + WORDS_TABLE_NAME +
                "(" +
                W_JAP_TEXT_COL + " TEXT," +
                W_RUS_TEXT_COL + " TEXT," +
                W_DESC_TEXT_COL + " TEXT" +
                ");"
        );

        db.execSQL("CREATE TABLE " + MARKERS_TABLE_NAME +
                "(" +
                M_TEXT_COL + " TEXT," +
                M_USING_COL + " TEXT" +
                ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WORDS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MARKERS_TABLE_NAME);
        onCreate(db);
    }

    public void addWord(String text, String translations, String description) {
        ContentValues map = new ContentValues();
        map.put(W_JAP_TEXT_COL, text);
        map.put(W_RUS_TEXT_COL, translations);
        map.put(W_DESC_TEXT_COL, description);

        getWritableDatabase().insert(WORDS_TABLE_NAME, null, map);
    }

    public void addMarker(String text, String using) {
        ContentValues map = new ContentValues();
        map.put(M_TEXT_COL, text);
        map.put(M_USING_COL, using);

        getWritableDatabase().insert(MARKERS_TABLE_NAME, null, map);
    }

    @SuppressLint("StaticFieldLeak")
    public void loadWords() {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT " + W_JAP_TEXT_COL +
                        ", " + W_RUS_TEXT_COL +
                        ", " + W_DESC_TEXT_COL +
                        " FROM " + WORDS_TABLE_NAME,
                null);

        new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if(cursor.moveToFirst()) {
                    do {
                        int japIndex = cursor.getColumnIndex(W_JAP_TEXT_COL);
                        int rusIndex = cursor.getColumnIndex(W_RUS_TEXT_COL);
                        int descIndex = cursor.getColumnIndex(W_DESC_TEXT_COL);
                        String jap = cursor.getString(japIndex);
                        String rus = cursor.getString(rusIndex);
                        String desc = cursor.getString(descIndex);

                        publishProgress(jap, rus, desc);
                    } while (cursor.moveToNext());
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(String... strings) {
                Dictionary.addWord(
                        strings[0],
                        Pattern.compile("/").split(strings[1]),
                        strings[2]
                );
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void loadMarkers() {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT " + M_TEXT_COL +
                        ", " + M_USING_COL +
                        " FROM " + MARKERS_TABLE_NAME,
                null);

        new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if(cursor.moveToFirst()) {
                    do {
                        int textIndex = cursor.getColumnIndex(M_TEXT_COL);
                        int usingIndex = cursor.getColumnIndex(M_USING_COL);
                        String text = cursor.getString(textIndex);
                        String using = cursor.getString(usingIndex);

                        publishProgress(text, using);
                    } while (cursor.moveToNext());
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(String... strings) {
                Dictionary.addMarker(
                        strings[0],
                        strings[1]
                );
            }
        }.execute();
    }
}
