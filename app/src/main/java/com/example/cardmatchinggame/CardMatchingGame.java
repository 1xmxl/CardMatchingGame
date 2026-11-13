package com.example.cardmatchinggame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 模型层（Model）：翻牌游戏逻辑（实验四：两张牌配对）。
 * 该类不引用任何 Android API，便于在 Activity / Adapter 之间复用。
 */
public class CardMatchingGame {

    private static final int MATCH_BONUS = 8;
    private static final int MISMATCH_PENALTY = 2;
    private static final int FLIP_COST = 1;

    private final List<Card> cards = new ArrayList<>();
    private final int cardCount;
    private int score;

    public CardMatchingGame(int cardCount) {
        if (cardCount <= 0 || cardCount % 2 != 0) {
            throw new IllegalArgumentException("cardCount must be a positive even number");
        }
        this.cardCount = cardCount;
        deal();
    }

    private void deal() {
        cards.clear();
        List<Integer> deck = new ArrayList<>();
        for (int group = 0; group < cardCount / 2; group++) {
            deck.add(group);
            deck.add(group);
        }
        Collections.shuffle(deck, new Random());
        for (int groupId : deck) {
            cards.add(new Card(groupId));
        }
        score = 0;
    }

    public Card cardAtIndex(int index) {
        if (index < 0 || index >= cards.size()) {
            return null;
        }
        return cards.get(index);
    }

    public int getCardCount() {
        return cardCount;
    }

    public int getScore() {
        return score;
    }

    /** 实验四：翻开某张牌，若已有另一张正面朝上的牌则立即判定两张是否配对 */
    public void chooseCardAtIndex(int index) {
        Card card = cardAtIndex(index);
        if (card == null || card.isMatched() || card.isFaceUp()) {
            return;
        }
        card.setFaceUp(true);

        for (Card other : cards) {
            if (other.isFaceUp() && !other.isMatched() && other != card) {
                if (card.match(other)) {
                    card.setMatched(true);
                    other.setMatched(true);
                    score += MATCH_BONUS;
                } else {
                    other.setFaceUp(false);
                    score -= MISMATCH_PENALTY;
                }
                return;
            }
        }
        score -= FLIP_COST;
    }

    public boolean isGameOver() {
        for (Card card : cards) {
            if (!card.isMatched()) {
                return false;
            }
        }
        return true;
    }
}
