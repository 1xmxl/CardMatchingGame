package com.example.cardmatchinggame;

import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 实验四控制器（Controller）：用 GridLayout + Button 显示 24 张牌。
 * 实验五将替换为 RecyclerView + Adapter（见 Dev 分支）。
 */
public class GameActivity extends AppCompatActivity {

    private static final int CARD_COUNT = 24;
    private static final int COLUMN_COUNT = 4;

    private CardMatchingGame game;
    private final List<Button> cardButtons = new ArrayList<>();
    private TextView scoreView;
    private String[] faces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        scoreView = findViewById(R.id.text_score);
        faces = getResources().getStringArray(R.array.card_faces);
        game = new CardMatchingGame(CARD_COUNT);

        GridLayout grid = findViewById(R.id.grid_cards);
        grid.setColumnCount(COLUMN_COUNT);
        int height = getResources().getDimensionPixelSize(R.dimen.card_height);
        int margin = getResources().getDimensionPixelSize(R.dimen.card_margin);

        for (int i = 0; i < CARD_COUNT; i++) {
            Button button = new Button(this);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = height;
            lp.columnSpec = GridLayout.spec(i % COLUMN_COUNT, 1f);
            lp.rowSpec = GridLayout.spec(i / COLUMN_COUNT);
            lp.setMargins(margin, margin, margin, margin);
            button.setLayoutParams(lp);
            button.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.card_text_size));
            button.setAllCaps(false);
            final int index = i;
            button.setOnClickListener(v -> {
                game.chooseCardAtIndex(index);
                refresh();
            });
            cardButtons.add(button);
            grid.addView(button);
        }

        findViewById(R.id.button_reset).setOnClickListener(v -> {
            game = new CardMatchingGame(CARD_COUNT);
            refresh();
        });

        refresh();
    }

    private void refresh() {
        for (int i = 0; i < cardButtons.size(); i++) {
            Card card = game.cardAtIndex(i);
            Button button = cardButtons.get(i);
            if (card.isFaceUp() || card.isMatched()) {
                button.setText(faces[card.getGroupId() % faces.length]);
                button.setBackgroundColor(ContextCompat.getColor(this,
                        card.isMatched() ? R.color.card_matched : R.color.card_front));
            } else {
                button.setText(getString(R.string.card_back));
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.card_back_color));
            }
            button.setContentDescription(getString(R.string.card_content_description, i + 1));
            button.setEnabled(!card.isMatched());
        }
        scoreView.setText(getString(R.string.score_format, game.getScore()));
        if (game.isGameOver()) {
            Toast.makeText(this, R.string.game_over, Toast.LENGTH_SHORT).show();
        }
    }
}
