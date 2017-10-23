/*
    File: Player.java
    Name: Tsang Tsz Pan
    Class: IT114105/1A
    Student Number: 170048177
    Description: The BlackJack Game, ITP3914 Programming Assignment
 */

/**
 * Player class, store player's status, hand and point. calculate the point of player's hand
 */
public class Player {
    private Status status = Status.STAND; //Player's status, default to stand
    private int[] hand = new int[0]; //Player's hand
    private boolean firstAce = false; //Become true if player get a first ace
    private int point = 0; //Player's hand point, except the first hand

    /**
     * Add an card to player's hand and recalculate the hand point
     *
     * @param card card to add
     * @return Player object
     */
    public void addHand(int card) {
        //add a hand
        hand = BlackJack.arrayAppend(hand, card);

        //add the point of the new card to hand point
        int cardPoint = BlackJack.getCardPoint(card);
        point += cardPoint;

        //toggle when player get the first ace
        if (cardPoint == 1 && !firstAce) {
            firstAce = true;
        }

        //check whether player bust
        if (point > 21) {
            status = Status.BUST;
        }
    }

    /**
     * Get player's hand
     *
     * @return player's hand
     */
    public int[] getHand() {
        return hand;
    }

    /**
     * Get the card list and status of the player like "[ RANK:SUIT RANK:SUIT ... ] Status!" , use to show on console
     *
     * @return Formatted card's Info
     */
    public String getCardInfo() {
        String cardInfo = "[ ";
        for (int card : hand) {
            cardInfo = cardInfo + BlackJack.getCardDisplayName(card) + " ";
        }
        return cardInfo + "] " + status.getDisplayName();
    }

    /**
     * Get the final calculated point of the player
     *
     * @return player's final hand point
     */
    public int getPoints() {
        //when player's have an ace and sum of other hand's point is < 10, count the ace as 11
        return (point <= 11 && firstAce) ? point + 10 : point;
    }

    /**
     * Change player's status
     *
     * @param status status to change
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get player's hand status
     *
     * @return Status of player's hand
     */
    public Status chkStatus() {
        //return status
        return status;
    }

    /**
     * Get player's raw point without final calculation
     *
     * @return player's raw point
     */
    public int getRawPoint() {
        return point;
    }

    /**
     * Get whether player already has a ace.
     *
     * @return do player get a ace yet
     */
    public boolean hasFirstAce() {
        return firstAce;
    }
}
    

