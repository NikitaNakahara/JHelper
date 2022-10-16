package com.android.jhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import java.util.regex.Pattern;

public class App extends AppCompatActivity {

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

        findViewById(R.id.add_element).setOnClickListener(v -> {
            newElem();
        });
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