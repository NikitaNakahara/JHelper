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
    private static LinearLayout kanjiLayout;

    public static void setContext(Context _context) { context = _context; }
    public static void setWordsLayout(LinearLayout _layout) { wordsLayout = _layout; }
    public static void setMarkersLayout(LinearLayout _layout) { markersLayout = _layout; }
    public static void setKanjiLayout(LinearLayout _layout) { kanjiLayout = _layout; }


    public static void addWord(String text, String[] translations, String description) {
        LinearLayout elemLayout = createLayout();

        elemLayout.addView(createTextView(text));

        elemLayout.addView(createTextLayout("значения", translations));

        if (description.length() != 0) {
            elemLayout.addView(createTextLayout("описание", new String[]{description}));
        }

        wordsLayout.addView(elemLayout, 0);
    }

    public static void addMarker(String text, String[] using) {
        LinearLayout elemLayout = createLayout();

        elemLayout.addView(createTextView(text));

        elemLayout.addView(createTextLayout("применение", using));

        markersLayout.addView(elemLayout, 0);
    }

    public static void addKanji(String kanji,
                                String[] onyomi,
                                String[] kunyomi,
                                String[] translations
    ) {
        LinearLayout elemLayout = createLayout();

        elemLayout.addView(createTextView(kanji));
        elemLayout.addView(createTextLayout("он-ёми", onyomi));
        elemLayout.addView(createTextLayout("кун-ёми", kunyomi));
        elemLayout.addView(createTextLayout("значения", translations));

        kanjiLayout.addView(elemLayout, 0);
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

    private static TextView createTextView(String text) {
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setTextSize(23);

        return textView;
    }

    private static LinearLayout createTextLayout(String title, String[] strings) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setLayoutParams(params);
        textLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView titleView = new TextView(context);
        titleView.setLayoutParams(params);
        titleView.setText(title + ": ");
        titleView.setTextSize(17);

        textLayout.addView(titleView);

        TextView textView = new TextView(context);
        textView.setLayoutParams(params);
        textView.setTextSize(17);
        textView.setTextColor(Color.parseColor("#515253"));

        StringBuilder text = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) {
                text.append(strings[i]);
            } else {
                text.append(", ").append(strings[i]);
            }
        }

        textView.setText(text);

        textLayout.addView(textView);

        return textLayout;
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
