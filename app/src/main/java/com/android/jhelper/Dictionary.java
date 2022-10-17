package com.android.jhelper;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class Dictionary {
    private static Map<String, Integer> wordsMap = new HashMap<>();
    private static Map<String, Integer> markersMap = new HashMap<>();
    private static Map<String, Integer> kanjiMap = new HashMap<>();

    private static int lastWordIndex = 0;
    private static int lastMarkerIndex = 0;
    private static int lastKanjiIndex = 0;

    private static final int WORDS = 1;
    private static final int MARKERS = 2;
    private static final int KANJI = 3;

    private static Context context;
    private static LinearLayout wordsLayout;
    private static LinearLayout markersLayout;
    private static LinearLayout kanjiLayout;

    private static Database db;

    public static void setContext(Context _context) {
        context = _context;
        db = new Database(context);
    }
    public static void setWordsLayout(LinearLayout _layout) { wordsLayout = _layout; }
    public static void setMarkersLayout(LinearLayout _layout) { markersLayout = _layout; }
    public static void setKanjiLayout(LinearLayout _layout) { kanjiLayout = _layout; }


    public static void addWord(String text, String translations, String description, boolean addToDB) {
        LinearLayout elemLayout = createLayout(WORDS);

        elemLayout.addView(createTextView(text));

        elemLayout.addView(createTextLayout("значения", parseString(translations)));

        if (description.length() != 0) {
            elemLayout.addView(createTextLayout("описание", new String[]{description}));
        }

        wordsLayout.addView(elemLayout, 0);

        wordsMap.put(text, lastWordIndex);

        if (addToDB) {
            db.addWord(
                    lastWordIndex,
                    text,
                    translations,
                    description
            );
        }

        lastWordIndex++;
    }

    public static void addMarker(String text, String using, boolean addToDB) {
        LinearLayout elemLayout = createLayout(MARKERS);

        elemLayout.addView(createTextView(text));

        elemLayout.addView(createTextLayout("применение", parseString(using)));

        markersLayout.addView(elemLayout, 0);

        wordsMap.put(text, lastMarkerIndex);

        if (addToDB) {
            db.addMarker(
                    lastMarkerIndex++,
                    text,
                    using
            );
        }
    }

    public static void addKanji(String kanji,
                                String onyomi,
                                String kunyomi,
                                String translations,
                                boolean addToDB
    ) {
        LinearLayout elemLayout = createLayout(KANJI);

        elemLayout.addView(createTextView(kanji));
        elemLayout.addView(createTextLayout("он-ёми", parseString(onyomi)));
        elemLayout.addView(createTextLayout("кун-ёми", parseString(kunyomi)));
        elemLayout.addView(createTextLayout("значения", parseString(translations)));

        kanjiLayout.addView(elemLayout, 0);

        wordsMap.put(kanji, lastKanjiIndex);

        if (addToDB) {
            db.addKanji(
                    lastKanjiIndex++,
                    kanji,
                    onyomi,
                    kunyomi,
                    translations
            );
        }
    }

    private static LinearLayout createLayout(int parent) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        LinearLayout elemLayout = new LinearLayout(context);

        elemLayout.setOrientation(LinearLayout.VERTICAL);
        elemLayout.setPadding(
                dpToPx(15),
                dpToPx(10),
                dpToPx(15),
                dpToPx(11)
        );
        elemLayout.setLayoutParams(params);

        LinearLayout buttons = new LinearLayout(context);
        buttons.setLayoutParams(params);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(Gravity.CENTER);
        buttons.setPadding(0, dpToPx(10), 0, dpToPx(10));

        Button delete = createButton("удалить");
        delete.setOnClickListener(v -> {
            switch (parent) {
                case WORDS: {
                    TextView text = (TextView)elemLayout.getChildAt(1);
                    int index = wordsMap.get(text.getText());
                    db.deleteWord(index);
                    wordsLayout.removeView(elemLayout);
                    break;
                }

                case MARKERS: {
                    TextView text = (TextView) elemLayout.getChildAt(1);
                    int index = markersMap.get(text.getText());
                    db.deleteMarker(index);
                    markersLayout.removeView(elemLayout);
                    break;
                }

                case KANJI: {
                    TextView text = (TextView) elemLayout.getChildAt(1);
                    int index = kanjiMap.get(text.getText());
                    db.deleteKanji(index);
                    kanjiLayout.removeView(elemLayout);
                    break;
                }
            }
        });
        buttons.addView(createButton("изменить"));
        buttons.addView(delete);
        LinearLayout.LayoutParams buttonsParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0
        );
        buttons.setLayoutParams(buttonsParams);

        elemLayout.addView(buttons);

        final boolean[] menuIsOpen = {false};
        elemLayout.setOnClickListener(v -> {
            if (!menuIsOpen[0]) {
                ValueAnimator animator = ValueAnimator.ofInt(0, dpToPx(51));
                animator.setDuration(200);
                animator.addUpdateListener(animation ->
                        buttons.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                (int) animation.getAnimatedValue()
                        )));
                animator.start();

                menuIsOpen[0] = true;
            } else {
                ValueAnimator animator = ValueAnimator.ofInt(dpToPx(51), 0);
                animator.setDuration(200);
                animator.addUpdateListener(animation ->
                        buttons.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                (int) animation.getAnimatedValue()
                        )));
                animator.start();

                menuIsOpen[0] = false;
            }
        });

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

    private static Button createButton(String text) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(100),
                dpToPx(31)
        );

        if (Objects.equals(text, "изменить")) {
            params.setMargins(0, 0, dpToPx(15), 0);
        }

        Button button = new Button(context);
        button.setLayoutParams(params);
        button.setText(text);
        button.setTextSize(10);
        button.setBackgroundResource(R.drawable.button);

        return button;
    }

    private static int dpToPx(int dp) {
        Resources resources = context.getResources();

        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }

    private static String[] parseString(String string) {
        return Pattern.compile("/").split(string);
    }
}
