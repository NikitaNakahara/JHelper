package com.android.jhelper;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    private static final boolean[] refactorIsOpen = {false};

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

        markersMap.put(text, lastMarkerIndex);

        if (addToDB) {
            db.addMarker(
                    lastMarkerIndex,
                    text,
                    using
            );
        }

        lastMarkerIndex++;
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

        kanjiMap.put(kanji, lastKanjiIndex);

        if (addToDB) {
            db.addKanji(
                    lastKanjiIndex,
                    kanji,
                    onyomi,
                    kunyomi,
                    translations
            );
        }

        lastKanjiIndex++;
    }

    public static void search(String text) {
        searchWords(text);
        searchMarkers(text);
        searchKanji(text);
    }

    private static void searchWords(String text) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams hiddenParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0
        );

        for (int i = 0; i < wordsLayout.getChildCount(); i++) {
            LinearLayout wordsElem = (LinearLayout) wordsLayout.getChildAt(i);

            TextView word = (TextView) wordsElem.getChildAt(2);
            LinearLayout rusLayout = (LinearLayout) wordsElem.getChildAt(3);
            TextView rus = (TextView) rusLayout.getChildAt(1);
            LinearLayout descLayout = (LinearLayout) wordsElem.getChildAt(4);
            TextView desc = null;
            if (descLayout != null) {
                desc = (TextView) descLayout.getChildAt(1);
            }

            if (desc != null) {
                if (
                        word.getText().toString().contains(text) ||
                        rus.getText().toString().contains(text) ||
                        desc.getText().toString().contains(text)
                ) {
                    wordsElem.setLayoutParams(params);
                } else {
                    wordsElem.setLayoutParams(hiddenParams);
                }
            } else {
                if (
                        word.getText().toString().contains(text) ||
                        rus.getText().toString().contains(text)
                ) {
                    wordsElem.setLayoutParams(params);
                } else {
                    wordsElem.setLayoutParams(hiddenParams);
                }
            }
        }
    }

    private static void searchMarkers(String text) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams hiddenParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0
        );

        for (int i = 0; i < markersLayout.getChildCount(); i++) {
            LinearLayout markersElem = (LinearLayout) markersLayout.getChildAt(i);

            TextView marker = (TextView) markersElem.getChildAt(2);
            LinearLayout usingLayout = (LinearLayout) markersElem.getChildAt(3);
            TextView using = (TextView) usingLayout.getChildAt(1);

            if (
                    marker.getText().toString().contains(text) ||
                    using.getText().toString().contains(text)
            ) {
                markersElem.setLayoutParams(params);
            } else {
                markersElem.setLayoutParams(hiddenParams);
            }
        }
    }

    private static void searchKanji(String text) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams hiddenParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0
        );

        for (int i = 0; i < kanjiLayout.getChildCount(); i++) {
            LinearLayout kanjiElem = (LinearLayout) kanjiLayout.getChildAt(i);

            TextView kanji = (TextView) kanjiElem.getChildAt(2);
            LinearLayout onyomiLayout = (LinearLayout) kanjiElem.getChildAt(3);
            TextView onyomi = (TextView) onyomiLayout.getChildAt(1);
            LinearLayout kunyomiLayout = (LinearLayout) kanjiElem.getChildAt(4);
            TextView kunyomi = (TextView) kunyomiLayout.getChildAt(1);
            LinearLayout knowsLayout = (LinearLayout) kanjiElem.getChildAt(5);
            TextView knows = (TextView) knowsLayout.getChildAt(1);

            if (
                    kanji.getText().toString().contains(text) ||
                    onyomi.getText().toString().contains(text) ||
                    kunyomi.getText().toString().contains(text) ||
                    knows.getText().toString().contains(text)
            ) {
                kanjiElem.setLayoutParams(params);
            } else {
                kanjiElem.setLayoutParams(hiddenParams);
            }
        }
    }

    private static LinearLayout createLayout(int parent) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams hiddenParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0
        );

        LinearLayout elemLayout = new LinearLayout(context);
        LinearLayout refactorLayout = new LinearLayout(context);

        refactorLayout.setGravity(Gravity.CENTER);
        refactorLayout.setOrientation(LinearLayout.VERTICAL);
        refactorLayout.setLayoutParams(hiddenParams);

        createRefactorLayout(refactorLayout, parent);

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
                    TextView text = (TextView) elemLayout.getChildAt(2);
                    int id = wordsMap.get(text.getText());
                    wordsMap.remove(text.getText());
                    db.deleteWord(id);
                    wordsLayout.removeView(elemLayout);
                    break;
                }

                case MARKERS: {
                    TextView text = (TextView) elemLayout.getChildAt(1);
                    int id = markersMap.get(text.getText());
                    markersMap.remove(text.getText());
                    db.deleteMarker(id);
                    markersLayout.removeView(elemLayout);
                    break;
                }

                case KANJI: {
                    TextView text = (TextView) elemLayout.getChildAt(1);
                    int id = kanjiMap.get(text.getText());
                    kanjiMap.remove(text.getText());
                    db.deleteKanji(id);
                    kanjiLayout.removeView(elemLayout);
                    break;
                }
            }
        });
        final boolean[] menuIsOpen = {false};

        Button refactor = createButton("изменить");
        refactor.setOnClickListener(v -> {
            refactorIsOpen[0] = true;
            hiddenMenu(buttons);
            menuIsOpen[0] = false;

            switch (parent) {
                case WORDS: {
                    updateWord(elemLayout, refactorLayout, params, hiddenParams);
                    break;
                }

                case MARKERS: {
                    updateMarker(elemLayout, refactorLayout, params, hiddenParams);
                    break;
                }

                case KANJI: {
                    updateKanji(elemLayout, refactorLayout, params, hiddenParams);
                    break;
                }
            }
        });

        buttons.addView(refactor);
        buttons.addView(delete);

        LinearLayout.LayoutParams buttonsParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0
        );
        buttons.setLayoutParams(buttonsParams);


        elemLayout.addView(buttons);
        elemLayout.addView(refactorLayout);

        elemLayout.setOnClickListener(v -> {
            if (!refactorIsOpen[0]) {
                if (!menuIsOpen[0]) {
                    showMenu(buttons);

                    menuIsOpen[0] = true;
                } else {
                    hiddenMenu(buttons);

                    menuIsOpen[0] = false;
                }
            }
        });

        return elemLayout;
    }

    private static void updateWord(
            LinearLayout elemLayout, LinearLayout refactorLayout,
            LinearLayout.LayoutParams params, LinearLayout.LayoutParams hiddenParams
    ) {
        final TextView[] japView = {(TextView) elemLayout.getChildAt(2)};
        final LinearLayout[] rusLayout = {(LinearLayout) elemLayout.getChildAt(3)};
        final TextView[] rusView = {(TextView) rusLayout[0].getChildAt(1)};
        final LinearLayout[] descLayout = {(LinearLayout) elemLayout.getChildAt(4)};
        final TextView[] descView = new TextView[1];
        if (descLayout[0] != null) {
            descView[0] = (TextView) descLayout[0].getChildAt(1);
        }

        final EditText[] jap = {(EditText) refactorLayout.getChildAt(0)};
        final EditText[] rus = {(EditText) refactorLayout.getChildAt(1)};
        final EditText[] desc = {(EditText) refactorLayout.getChildAt(2)};

        jap[0].setText(japView[0].getText());
        rus[0].setText(convertString((String) rusView[0].getText()));
        if (descView[0] != null) {
            desc[0].setText(descView[0].getText());
        }

        refactorLayout.setLayoutParams(params);
        refactorLayout.setPadding(0, dpToPx(10), 0, dpToPx(10));

        Button confirmButton = (Button)refactorLayout.getChildAt(3);
        confirmButton.setOnClickListener(v -> {
            japView[0].setText(jap[0].getText());
            rusView[0].setText(
                    reconvertString(convertString(parseString(rus[0].getText().toString())))
            );
            String descText = desc[0].getText().toString();
            if (descText.length() != 0) {
                if (descView[0] != null) {
                    descView[0].setText(descText);
                } else {
                    descLayout[0] = createTextLayout(
                            "описание",
                            parseString(desc[0].getText().toString())
                    );
                    elemLayout.addView(descLayout[0], 4);
                }
            } else {
                elemLayout.removeView(descLayout[0]);
            }

            refactorLayout.setLayoutParams(hiddenParams);
            refactorLayout.setPadding(0, 0, 0, 0);

            db.updateWord(
                    wordsMap.get(jap[0].getText().toString()),
                    jap[0].getText().toString(),
                    rus[0].getText().toString(),
                    desc[0].getText().toString()
            );

            refactorIsOpen[0] = false;
        });
    }

    private static void updateMarker(
            LinearLayout elemLayout, LinearLayout refactorLayout,
            LinearLayout.LayoutParams params, LinearLayout.LayoutParams hiddenParams
    ) {
        final TextView[] markerView = {(TextView) elemLayout.getChildAt(2)};
        LinearLayout usingLayout = (LinearLayout) elemLayout.getChildAt(3);
        final TextView[] usingView = {(TextView) usingLayout.getChildAt(1)};

        final EditText[] marker = {(EditText) refactorLayout.getChildAt(0)};
        final EditText[] using = {(EditText) refactorLayout.getChildAt(1)};

        marker[0].setText(markerView[0].getText());
        using[0].setText(convertString((String) usingView[0].getText()));

        refactorLayout.setLayoutParams(params);
        refactorLayout.setPadding(0, dpToPx(10), 0, dpToPx(10));

        Button confirmButton = (Button)refactorLayout.getChildAt(2);
        confirmButton.setOnClickListener(v -> {
            markerView[0].setText(marker[0].getText());
            usingView[0].setText(
                    reconvertString(convertString(parseString(using[0].getText().toString())))
            );

            refactorLayout.setLayoutParams(hiddenParams);
            refactorLayout.setPadding(0, 0, 0, 0);

            db.updateMarker(
                    markersMap.get(marker[0].getText().toString()),
                    marker[0].getText().toString(),
                    using[0].getText().toString()
            );

            refactorIsOpen[0] = false;
        });
    }

    private static void updateKanji(
            LinearLayout elemLayout, LinearLayout refactorLayout,
            LinearLayout.LayoutParams params, LinearLayout.LayoutParams hiddenParams
    ) {
        final TextView[] kanjiView = {(TextView) elemLayout.getChildAt(2)};
        LinearLayout onyomiLayout = (LinearLayout) elemLayout.getChildAt(3);
        final TextView[] onyomiView = {(TextView) onyomiLayout.getChildAt(1)};
        LinearLayout kunyomiLayout = (LinearLayout) elemLayout.getChildAt(4);
        final TextView[] kunyomiView = {(TextView) kunyomiLayout.getChildAt(1)};
        LinearLayout knowsLayout = (LinearLayout) elemLayout.getChildAt(5);
        final TextView[] knowsView = {(TextView) knowsLayout.getChildAt(1)};

        final EditText[] kanji = {(EditText) refactorLayout.getChildAt(0)};
        final EditText[] onyomi = {(EditText) refactorLayout.getChildAt(1)};
        final EditText[] kunyomi = {(EditText) refactorLayout.getChildAt(2)};
        final EditText[] knows = {(EditText) refactorLayout.getChildAt(3)};

        kanji[0].setText(kanjiView[0].getText());
        onyomi[0].setText(convertString((String) onyomiView[0].getText()));
        kunyomi[0].setText(convertString((String) kunyomiView[0].getText()));
        knows[0].setText(convertString((String) knowsView[0].getText()));

        refactorLayout.setLayoutParams(params);
        refactorLayout.setPadding(0, dpToPx(10), 0, dpToPx(10));

        Button confirmButton = (Button)refactorLayout.getChildAt(4);
        confirmButton.setOnClickListener(v -> {
            kanjiView[0].setText(kanji[0].getText());
            onyomiView[0].setText(
                    reconvertString(convertString(parseString(onyomi[0].getText().toString())))
            );
            kunyomiView[0].setText(
                    reconvertString(convertString(parseString(kunyomi[0].getText().toString())))
            );
            knowsView[0].setText(
                    reconvertString(convertString(parseString(knows[0].getText().toString())))
            );

            refactorLayout.setLayoutParams(hiddenParams);
            refactorLayout.setPadding(0, 0, 0, 0);

            db.updateKanji(
                    kanjiMap.get(kanji[0].getText().toString()),
                    kanji[0].getText().toString(),
                    onyomi[0].getText().toString(),
                    kunyomi[0].getText().toString(),
                    knows[0].getText().toString()
            );

            refactorIsOpen[0] = false;
        });
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

    private static void createRefactorLayout(LinearLayout layout, int dictMode) {
        switch (dictMode) {
            case WORDS: {
                createWordRefactorLayout(layout);
                break;
            }

            case MARKERS: {
                createMarkerRefactorLayout(layout);
                break;
            }

            case KANJI: {
                createKanjiRefactorLayout(layout);
                break;
            }
        }
    }

    private static void createWordRefactorLayout(LinearLayout layout) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(300),
                dpToPx(40)
        );
        params.setMargins(0, 0, 0, dpToPx(10));

        EditText jap = new EditText(context);
        EditText rus = new EditText(context);
        EditText desc = new EditText(context);

        jap.setLayoutParams(params);
        rus.setLayoutParams(params);
        desc.setLayoutParams(params);

        jap.setPadding(dpToPx(7), dpToPx(7), dpToPx(7), dpToPx(7));
        rus.setPadding(dpToPx(7), dpToPx(7), dpToPx(7), dpToPx(7));
        desc.setPadding(dpToPx(7), dpToPx(7), dpToPx(7), dpToPx(7));

        jap.setHint("слово");
        rus.setHint("значения (через /)");
        desc.setHint("описание (необязательно)");

        jap.setBackgroundResource(R.drawable.word_input);
        rus.setBackgroundResource(R.drawable.word_input);
        desc.setBackgroundResource(R.drawable.word_input);

        layout.addView(jap);
        layout.addView(rus);
        layout.addView(desc);

        Button button = new Button(context);
        button.setLayoutParams(new LinearLayout.LayoutParams(
                dpToPx(100),
                dpToPx(33)
        ));
        button.setBackgroundResource(R.drawable.confirm_button);
        button.setText("подтвердить");
        button.setTextSize(11);

        layout.addView(button);
    }

    private static void createMarkerRefactorLayout(LinearLayout layout) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(300),
                dpToPx(40)
        );
        params.setMargins(0, 0, 0, dpToPx(10));

        EditText marker = new EditText(context);
        EditText using = new EditText(context);

        marker.setLayoutParams(params);
        using.setLayoutParams(params);

        marker.setPadding(dpToPx(7), dpToPx(7), dpToPx(7), dpToPx(7));
        using.setPadding(dpToPx(7), dpToPx(7), dpToPx(7), dpToPx(7));

        marker.setHint("маркер");
        using.setHint("применение (через /)");

        marker.setBackgroundResource(R.drawable.word_input);
        using.setBackgroundResource(R.drawable.word_input);

        layout.addView(marker);
        layout.addView(using);

        Button button = new Button(context);
        button.setLayoutParams(new LinearLayout.LayoutParams(
                dpToPx(100),
                dpToPx(33)
        ));
        button.setBackgroundResource(R.drawable.confirm_button);
        button.setText("подтвердить");
        button.setTextSize(11);

        layout.addView(button);
    }

    private static void createKanjiRefactorLayout(LinearLayout layout) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(300),
                dpToPx(40)
        );
        params.setMargins(0, 0, 0, dpToPx(10));

        EditText kanji = new EditText(context);
        EditText onyomi = new EditText(context);
        EditText kunyomi = new EditText(context);
        EditText knows = new EditText(context);

        kanji.setLayoutParams(params);
        onyomi.setLayoutParams(params);
        kunyomi.setLayoutParams(params);
        knows.setLayoutParams(params);

        kanji.setPadding(dpToPx(7), dpToPx(7), dpToPx(7), dpToPx(7));
        onyomi.setPadding(dpToPx(7), dpToPx(7), dpToPx(7), dpToPx(7));
        kunyomi.setPadding(dpToPx(7), dpToPx(7), dpToPx(7), dpToPx(7));
        knows.setPadding(dpToPx(7), dpToPx(7), dpToPx(7), dpToPx(7));

        kanji.setHint("иероглиф");
        onyomi.setHint("он-ёми (через /)");
        kunyomi.setHint("кун-ёми (через /)");
        knows.setHint("значения (через /)");

        kanji.setBackgroundResource(R.drawable.word_input);
        onyomi.setBackgroundResource(R.drawable.word_input);
        kunyomi.setBackgroundResource(R.drawable.word_input);
        knows.setBackgroundResource(R.drawable.word_input);

        layout.addView(kanji);
        layout.addView(onyomi);
        layout.addView(kunyomi);
        layout.addView(knows);

        Button button = new Button(context);
        button.setLayoutParams(new LinearLayout.LayoutParams(
                dpToPx(100),
                dpToPx(33)
        ));
        button.setBackgroundResource(R.drawable.confirm_button);
        button.setText("подтвердить");
        button.setTextSize(11);

        layout.addView(button);
    }

    private static int dpToPx(int dp) {
        Resources resources = context.getResources();

        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }

    /**
     * конвертирует строку из вида "a, b, c" в вид "a/b/c"
     * @param text исходная строка вида "a, b, c"
     * @return ковертированная строка вида "a/b/c"
     */
    private static String convertString(String text) {
        String[] strings = text.split(", ");

        StringBuilder returnString = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) {
                returnString.append(strings[i]);
            } else {
                returnString.append("/").append(strings[i]);
            }
        }

        return returnString.toString();
    }

    /**
     * конвертирует строку из вида "a/b/c" в вид "a, b, c"
     * @param text исходная строка вида "a/b/c"
     * @return ковертированная строка вида "a, b, c"
     */
    private static String reconvertString(String text) {
        String[] strings = text.split("/");

        StringBuilder returnString = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) {
                returnString.append(strings[i]);
            } else {
                returnString.append(", ").append(strings[i]);
            }
        }

        return returnString.toString();
    }

    /**
     * конвертирует массив строк в строку вида "a/b/c"
     * @param strings массив строк
     * @return строка вида "a/b/c"
     */
    private static String convertString(String[] strings) {
        StringBuilder returnString = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) {
                returnString.append(strings[i]);
            } else {
                returnString.append("/").append(strings[i]);
            }
        }

        return returnString.toString();
    }

    private static String[] parseString(String string) {
        return Pattern.compile("/").split(string);
    }

    private static void showMenu(LinearLayout menu) {
        ValueAnimator animator = ValueAnimator.ofInt(0, dpToPx(51));
        animator.setDuration(200);
        animator.addUpdateListener(animation ->
                menu.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        (int) animation.getAnimatedValue()
                )));
        animator.start();
    }

    private static void hiddenMenu(LinearLayout menu) {
        ValueAnimator animator = ValueAnimator.ofInt(dpToPx(51), 0);
        animator.setDuration(200);
        animator.addUpdateListener(animation ->
                menu.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        (int) animation.getAnimatedValue()
                )));
        animator.start();
    }
}
