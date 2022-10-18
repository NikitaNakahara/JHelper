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
    private final String KANJI_TABLE_NAME = "KANJI";

    private final String ID_COL = "ID_COL";

    private final String W_JAP_TEXT_COL = "JAP";
    private final String W_RUS_TEXT_COL = "RUS";
    private final String W_DESC_TEXT_COL = "DESCRIPTION";

    private final String M_TEXT_COL = "TEXT";
    private final String M_USING_COL = "USING_COL";

    private final String K_KANJI_TEXT_COL = "KANJI_COL";
    private final String K_ONYOMI_TEXT_COL = "ONYOMI";
    private final String K_KUNYOMI_TEXT_COL = "KUNYOMI";
    private final String K_TRANSL_TEXT_COL = "TRANSLATION";

    public Database(@Nullable Context context) {
        super(context, "dictionary.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + WORDS_TABLE_NAME +
                "(" +
                ID_COL + " INTEGER," +
                W_JAP_TEXT_COL + " TEXT," +
                W_RUS_TEXT_COL + " TEXT," +
                W_DESC_TEXT_COL + " TEXT" +
                ");"
        );

        db.execSQL("CREATE TABLE " + MARKERS_TABLE_NAME +
                "(" +
                ID_COL + " INTEGER, " +
                M_TEXT_COL + " TEXT," +
                M_USING_COL + " TEXT" +
                ");"
        );

        db.execSQL("CREATE TABLE " + KANJI_TABLE_NAME +
                "(" +
                ID_COL + " INTEGER, " +
                K_KANJI_TEXT_COL + " TEXT," +
                K_ONYOMI_TEXT_COL + " TEXT," +
                K_KUNYOMI_TEXT_COL + " TEXT," +
                K_TRANSL_TEXT_COL + " TEXT" +
                ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WORDS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MARKERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + KANJI_TABLE_NAME);
        onCreate(db);
    }

    public void addWord(int id, String text, String translations, String description) {
        ContentValues map = new ContentValues();
        map.put(ID_COL, id);
        map.put(W_JAP_TEXT_COL, text);
        map.put(W_RUS_TEXT_COL, translations);
        map.put(W_DESC_TEXT_COL, description);

        getWritableDatabase().insert(WORDS_TABLE_NAME, null, map);
    }

    public void addMarker(int id, String text, String using) {
        ContentValues map = new ContentValues();
        map.put(ID_COL, id);
        map.put(M_TEXT_COL, text);
        map.put(M_USING_COL, using);

        getWritableDatabase().insert(MARKERS_TABLE_NAME, null, map);
    }

    public void addKanji(int id, String kanji, String onyomi, String kunyomi, String translations) {
        ContentValues map = new ContentValues();
        map.put(ID_COL, id);
        map.put(K_KANJI_TEXT_COL, kanji);
        map.put(K_ONYOMI_TEXT_COL, onyomi);
        map.put(K_KUNYOMI_TEXT_COL, kunyomi);
        map.put(K_TRANSL_TEXT_COL, translations);

        getWritableDatabase().insert(KANJI_TABLE_NAME, null, map);
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
                        strings[1],
                        strings[2],
                        false
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
                        strings[1],
                        false
                );
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void loadKanji() {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT " + K_KANJI_TEXT_COL +
                        ", " + K_ONYOMI_TEXT_COL +
                        ", " + K_KUNYOMI_TEXT_COL +
                        ", " + K_TRANSL_TEXT_COL +
                        " FROM " + KANJI_TABLE_NAME,
                null);

        new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if(cursor.moveToFirst()) {
                    do {
                        int kanjiIndex = cursor.getColumnIndex(K_KANJI_TEXT_COL);
                        int onyomiIndex = cursor.getColumnIndex(K_ONYOMI_TEXT_COL);
                        int kunyomiIndex = cursor.getColumnIndex(K_KUNYOMI_TEXT_COL);
                        int translationsIndex = cursor.getColumnIndex(K_TRANSL_TEXT_COL);
                        String kanji = cursor.getString(kanjiIndex);
                        String onyomi = cursor.getString(onyomiIndex);
                        String kunyomi = cursor.getString(kunyomiIndex);
                        String translations = cursor.getString(translationsIndex);

                        publishProgress(kanji, onyomi, kunyomi, translations);
                    } while (cursor.moveToNext());
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(String... strings) {
                Dictionary.addKanji(
                        strings[0],
                        strings[1],
                        strings[2],
                        strings[3],
                        false
                );
            }
        }.execute();
    }

    public void updateWord(int id, String jap, String rus, String desc) {
        ContentValues map = new ContentValues();
        map.put(ID_COL, id);
        map.put(W_JAP_TEXT_COL, jap);
        map.put(W_RUS_TEXT_COL, rus);
        map.put(W_DESC_TEXT_COL, desc);

        getWritableDatabase().update(WORDS_TABLE_NAME, map, ID_COL + " = " + id, null);
    }

    public void updateMarker(int id, String marker, String using) {
        ContentValues map = new ContentValues();
        map.put(ID_COL, id);
        map.put(M_TEXT_COL, marker);
        map.put(M_USING_COL, using);

        getWritableDatabase().update(MARKERS_TABLE_NAME, map, ID_COL + " = " + id, null);
    }

    public void updateKanji(int id, String kanji, String onyomi, String kunyomi, String knows) {
        ContentValues map = new ContentValues();
        map.put(ID_COL, id);
        map.put(K_KANJI_TEXT_COL, kanji);
        map.put(K_ONYOMI_TEXT_COL, onyomi);
        map.put(K_KUNYOMI_TEXT_COL, kunyomi);
        map.put(K_TRANSL_TEXT_COL, knows);

        getWritableDatabase().update(KANJI_TABLE_NAME, map, ID_COL + " = " + id, null);
    }

    public void deleteWord(int id) {
        getWritableDatabase().delete(
                WORDS_TABLE_NAME,
                ID_COL + " = " + id,
                null
        );
    }

    public void deleteMarker(int id) {
        getWritableDatabase().delete(
                MARKERS_TABLE_NAME,
                ID_COL + " = " + id,
                null
        );
    }

    public void deleteKanji(int id) {
        getWritableDatabase().delete(
                KANJI_TABLE_NAME,
                ID_COL + " = " + id,
                null
        );
    }
}
