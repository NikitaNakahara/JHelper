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
    private final String W_JAP_TEXT_COL = "JAP";
    private final String W_RUS_TEXT_COL = "RUS";
    private final String W_DESC_TEXT_COL = "DESCRIPTION";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WORDS_TABLE_NAME);
        onCreate(db);
    }

    public void addWord(String text, String translations, String description) {
        ContentValues map = new ContentValues();
        map.put(W_JAP_TEXT_COL, text);
        map.put(W_RUS_TEXT_COL, translations);
        map.put(W_DESC_TEXT_COL, description);

        getWritableDatabase().insert(WORDS_TABLE_NAME, null, map);
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
}
