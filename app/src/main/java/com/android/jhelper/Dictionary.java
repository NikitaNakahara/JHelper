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

    private static Context context;
    private static LinearLayout wordsLayout;
    private static LinearLayout markersLayout;

    public static void setContext(Context _context) { context = _context; }
    public static void setWordsLayout(LinearLayout _layout) { wordsLayout = _layout; }
    public static void setMarkersLayout(LinearLayout _layout) { markersLayout = _layout; }


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

        wordsLayout.addView(elemLayout, 0);
    }

    public static void addMarker(String text, String[] using) {
        LinearLayout elemLayout = createLayout();

        TextView marker = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        marker.setLayoutParams(params);
        marker.setText(text);
        marker.setTextSize(23);

        elemLayout.addView(marker);


        LinearLayout usingLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        usingLayout.setLayoutParams(layoutParams);
        usingLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView usingHeader = new TextView(context);
        usingHeader.setLayoutParams(params);
        usingHeader.setText("применение: ");
        usingHeader.setTextSize(17);

        usingLayout.addView(usingHeader);

        TextView usingView = new TextView(context);
        usingView.setLayoutParams(params);
        usingView.setTextSize(17);
        usingView.setTextColor(Color.parseColor("#515253"));

        StringBuilder usingText = new StringBuilder();
        for (int i = 0; i < using.length; i++) {
            if (i == 0) {
                usingText.append(using[i]);
            } else {
                usingText.append(", ").append(using[i]);
            }
        }

        usingView.setText(usingText);

        usingLayout.addView(usingView);

        elemLayout.addView(usingLayout);

        markersLayout.addView(elemLayout, 0);
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
