package com.android.jhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.ViewFlipper;

import java.util.regex.Pattern;

public class App extends AppCompatActivity {
    Database db = new Database(this);

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

        db.loadWords();

        findViewById(R.id.add_element).setOnClickListener(v -> {
            newElem();
        });

        Button[] dictModeButtons = new Button[] {
                findViewById(R.id.words_dict),
                findViewById(R.id.markers_dict),
                findViewById(R.id.kanji_dict)
        };

        RelativeLayout.LayoutParams activeParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        activeParams.setMargins(0, dpToPx(103), 0, 0);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
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
                        dictModeButton.setBackgroundResource(R.drawable.active_word_button);
                        wordDict.setLayoutParams(activeParams);
                        markerDict.setLayoutParams(params);
                        kanjiDict.setLayoutParams(params);
                        break;

                    case "маркеры":
                        dictModeButton.setBackgroundResource(R.drawable.active_marker_button);
                        wordDict.setLayoutParams(params);
                        markerDict.setLayoutParams(activeParams);
                        kanjiDict.setLayoutParams(params);
                        break;

                    case "кандзи":
                        dictModeButton.setBackgroundResource(R.drawable.active_kanji_button);
                        wordDict.setLayoutParams(params);
                        markerDict.setLayoutParams(params);
                        kanjiDict.setLayoutParams(activeParams);
                        break;
                }
            });
        }
    }

    private void newElem() {
        final String[] mode = new String[1];
        mode[0] = "слово";

        ViewFlipper flipper = findViewById(R.id.flipper);
        flipper.showNext();

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
                parseString(translationsEdit.getText().toString()),
                descriptionEdit.getText().toString()
        );

        db.addWord(
                wordEdit.getText().toString(),
                translationsEdit.getText().toString(),
                descriptionEdit.getText().toString()
        );
    }

    private void addMarker() {

    }

    private void addKanji() {

    }

    private String[] parseString(String string) {
        return Pattern.compile("/").split(string);
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