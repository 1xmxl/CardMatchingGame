package com.example.cardmatchinggame;

/**
 * 模型层（Model）：一张牌。
 * 实验四版本：两张牌的图案编号相同即视为配对成功。
 */
public class Card {

    private final int groupId;
    private boolean faceUp;
    private boolean matched;

    public Card(int groupId) {
        this.groupId = groupId;
        this.faceUp = false;
        this.matched = false;
    }

    public int getGroupId() {
        return groupId;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    /** 实验四：比较两张牌的图案编号 */
    public boolean match(Card other) {
        return other != null && other != this && this.groupId == other.groupId;
    }
}
