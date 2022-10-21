package com.android.jhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.regex.Pattern;

public class App extends AppCompatActivity {
    Database db = new Database(this);

    String lastMode = "слово";
    LinearLayout lastLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_JHelper);

        super.onCreate(savedInstanceState);

        mainView();
    }

    private void mainView() {
        setContentView(R.layout.flipper);

        Dictionary.setContext(this);

        Dictionary.setWordsLayout(findViewById(R.id.words_dict_layout));
        Dictionary.setMarkersLayout(findViewById(R.id.markers_dict_layout));
        Dictionary.setKanjiLayout(findViewById(R.id.kanji_dict_layout));

        db.loadWords();
        db.loadMarkers();
        db.loadKanji();

        findViewById(R.id.add_element).setOnClickListener(v -> newElem());
        final boolean[] searchIsShow = {false};

        // search EditText update listener
        EditText edit = findViewById(R.id.search_input);
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                Dictionary.search(s.toString());
            }
        });

        findViewById(R.id.search).setOnClickListener(v -> {
            search(searchIsShow[0]);
            searchIsShow[0] = !searchIsShow[0];
        });

        Button[] dictModeButtons = new Button[] {
                findViewById(R.id.words_dict),
                findViewById(R.id.markers_dict),
                findViewById(R.id.kanji_dict)
        };

        LinearLayout.LayoutParams activeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        ScrollView wordDict = findViewById(R.id.words_scroll);
        ScrollView markerDict = findViewById(R.id.markers_scroll);
        ScrollView kanjiDict = findViewById(R.id.kanji_scroll);

        for (Button dictModeButton : dictModeButtons) {
            dictModeButton.setOnClickListener(v -> {
                for (Button button : dictModeButtons) {
                    button.setBackgroundResource(R.drawable.button);
                }

                switch (dictModeButton.getText().toString()) {
                    case "слова":
                        lastLayout = (LinearLayout) wordDict.getChildAt(0);
                        dictModeButton.setBackgroundResource(R.drawable.active_word_button);
                        wordDict.setLayoutParams(activeParams);
                        markerDict.setLayoutParams(params);
                        kanjiDict.setLayoutParams(params);
                        break;

                    case "маркеры":
                        lastLayout = (LinearLayout) markerDict.getChildAt(0);
                        dictModeButton.setBackgroundResource(R.drawable.active_marker_button);
                        wordDict.setLayoutParams(params);
                        markerDict.setLayoutParams(activeParams);
                        kanjiDict.setLayoutParams(params);
                        break;

                    case "кандзи":
                        lastLayout = (LinearLayout) kanjiDict.getChildAt(0);
                        dictModeButton.setBackgroundResource(R.drawable.active_kanji_button);
                        wordDict.setLayoutParams(params);
                        markerDict.setLayoutParams(params);
                        kanjiDict.setLayoutParams(activeParams);
                        break;
                }
            });
        }
    }

    private void search(boolean searchIsShow) {
        if (!searchIsShow) {
            showSearch();
        } else {
            hiddenSearch();
        }
    }

    private void showSearch() {
        LinearLayout searchLayout = findViewById(R.id.search_layout);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) searchLayout.getLayoutParams();
        LinearLayout dictLayout = findViewById(R.id.dict_layout);
        RelativeLayout.LayoutParams dictParams = (RelativeLayout.LayoutParams) dictLayout.getLayoutParams();

        ValueAnimator anim = ValueAnimator.ofInt(dpToPx(61), dpToPx(103));
        anim.setDuration(200);
        anim.addUpdateListener(animation -> {
            params.setMargins(0, (int)animation.getAnimatedValue(), 0, 0);
            searchLayout.setLayoutParams(params);
            dictParams.setMargins(0, (int)animation.getAnimatedValue() + 75, 0, 0);
            dictLayout.setLayoutParams(dictParams);
        });
        anim.start();
    }

    private void hiddenSearch() {
        LinearLayout searchLayout = findViewById(R.id.search_layout);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) searchLayout.getLayoutParams();
        LinearLayout dictLayout = findViewById(R.id.dict_layout);
        RelativeLayout.LayoutParams dictParams = (RelativeLayout.LayoutParams) dictLayout.getLayoutParams();

        ValueAnimator anim = ValueAnimator.ofInt(dpToPx(103), dpToPx(61));
        anim.setDuration(200);
        anim.addUpdateListener(animation -> {
            params.setMargins(0, (int)animation.getAnimatedValue(), 0, 0);
            searchLayout.setLayoutParams(params);
            dictParams.setMargins(0, (int)animation.getAnimatedValue() + 75, 0, 0);
            dictLayout.setLayoutParams(dictParams);
        });
        anim.start();
    }

    private void newElem() {
        final String[] mode = new String[1];
        mode[0] = lastMode;

        ViewFlipper flipper = findViewById(R.id.flipper);
        flipper.showNext();

        findViewById(R.id.back_to_dict).setOnClickListener(v -> flipper.showPrevious());

        Button[] buttons = new Button[] {
            findViewById(R.id.word_mode),
            findViewById(R.id.marker_mode),
            findViewById(R.id.kanji_mode)
        };

        LinearLayout wordLayout = findViewById(R.id.word_layout);
        LinearLayout markerLayout = findViewById(R.id.marker_layout);
        LinearLayout kanjiLayout = findViewById(R.id.kanji_layout);

        LinearLayout.LayoutParams activeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        for (Button button : buttons) {
            button.setOnClickListener(v -> {
                mode[0] = button.getText().toString();
                lastMode = mode[0];
                for (Button button1 : buttons) {
                    button1.setBackgroundResource(R.drawable.button);
                }

                switch (button.getText().toString()) {
                    case "слово":
                        button.setBackgroundResource(R.drawable.active_word_button);
                        wordLayout.setLayoutParams(activeParams);
                        markerLayout.setLayoutParams(params);
                        kanjiLayout.setLayoutParams(params);
                        break;

                    case "маркер":
                        button.setBackgroundResource(R.drawable.active_marker_button);
                        wordLayout.setLayoutParams(params);
                        markerLayout.setLayoutParams(activeParams);
                        kanjiLayout.setLayoutParams(params);
                        break;

                    case "кандзи":
                        button.setBackgroundResource(R.drawable.active_kanji_button);
                        wordLayout.setLayoutParams(params);
                        markerLayout.setLayoutParams(params);
                        kanjiLayout.setLayoutParams(activeParams);
                        break;
                }
            });
        }

        findViewById(R.id.confirm_button).setOnClickListener(v -> {
            switch (mode[0]) {
                case "слово":
                    addWord();
                    break;

                case "маркер":
                    addMarker();
                    break;

                case "кандзи":
                    addKanji();
                    break;
            }

            flipper.showPrevious();
        });
    }

    private void addWord() {
        EditText wordEdit = findViewById(R.id.jap_text);
        EditText translationsEdit = findViewById(R.id.rus_text);
        EditText descriptionEdit = findViewById(R.id.description_text);
        Dictionary.addWord(
                wordEdit.getText().toString(),
                translationsEdit.getText().toString(),
                descriptionEdit.getText().toString(),
                 true
        );
    }

    private void addMarker() {
        EditText marker = findViewById(R.id.marker_text);
        EditText using = findViewById(R.id.using_text);
        Dictionary.addMarker(
                marker.getText().toString(),
                using.getText().toString(),
                true
        );
    }

    private void addKanji() {
        EditText kanji = findViewById(R.id.kanji_text);
        EditText onyomi = findViewById(R.id.on_yomi);
        EditText kunyomi = findViewById(R.id.kun_yomi);
        EditText translations = findViewById(R.id.knows_text);

        Dictionary.addKanji(
                kanji.getText().toString(),
                onyomi.getText().toString(),
                kunyomi.getText().toString(),
                translations.getText().toString(),
                true
        );
    }

    public int dpToPx(int dp) {
        Resources resources = this.getResources();

        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }
}