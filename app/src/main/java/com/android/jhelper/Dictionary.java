package com.android.jhelper;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class Dictionary {
    private static ArrayList<Map<String, String>> words;
    private static ArrayList<Map<String, String>> markers;
    private static ArrayList<Map<String, String>> kanji;

    private static Context context;
    private static LinearLayout wordsLayout;

    public static void setContext(Context _context) { context = _context; }
    public static void setWordsLayout(LinearLayout _layout) { wordsLayout = _layout; }


    public static void addWord(String text, String[] translations, String description) {
        LinearLayout elemLayout = createLayout();

        TextView word = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        word.setLayoutParams(params);
        word.setText(text);
        word.setTextSize(23);

        elemLayout.addView(word);

        LinearLayout translationsLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        translationsLayout.setLayoutParams(layoutParams);
        translationsLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView translationsHeader = new TextView(context);
        translationsHeader.setLayoutParams(params);
        translationsHeader.setText("значения: ");
        translationsHeader.setTextSize(17);

        translationsLayout.addView(translationsHeader);

        TextView translationsView = new TextView(context);
        translationsView.setLayoutParams(params);
        translationsView.setTextSize(17);
        translationsView.setTextColor(Color.parseColor("#515253"));

        StringBuilder translationsText = new StringBuilder();
        for (int i = 0; i < translations.length; i++) {
            if (i == 0) {
                translationsText.append(translations[i]);
            } else {
                translationsText.append(", ").append(translations[i]);
            }
        }

        translationsView.setText(translationsText);

        translationsLayout.addView(translationsView);

        elemLayout.addView(translationsLayout);

        if (description.length() != 0) {
            LinearLayout descriptionLayout = new LinearLayout(context);
            descriptionLayout.setLayoutParams(layoutParams);
            descriptionLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView descriptionHeader = new TextView(context);
            descriptionHeader.setLayoutParams(params);
            descriptionHeader.setText("описание: ");
            descriptionHeader.setTextSize(17);

            descriptionLayout.addView(descriptionHeader);

            TextView descriptionView = new TextView(context);
            descriptionView.setLayoutParams(params);
            descriptionView.setTextSize(17);
            descriptionView.setTextColor(Color.parseColor("#515253"));

            descriptionView.setText(description);

            descriptionLayout.addView(descriptionView);

            elemLayout.addView(descriptionLayout);
        }

        wordsLayout.addView(elemLayout);
    }

    public static void addMarker() {

    }


    public static void addKanji() {

    }

    private static LinearLayout createLayout() {
        LinearLayout elemLayout = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        elemLayout.setOrientation(LinearLayout.VERTICAL);
        elemLayout.setPadding(
                dpToPx(15),
                dpToPx(10),
                dpToPx(15),
                dpToPx(11)
        );
        elemLayout.setLayoutParams(params);

        return elemLayout;
    }

    private static int dpToPx(int dp) {
        Resources resources = context.getResources();

        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }
}
