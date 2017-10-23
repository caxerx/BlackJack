/*
    File: Dealer.java
    Name: Tsang Tsz Pan
    Class: IT114105/1A
    Student Number: 170048177
    Description: The BlackJack Game, ITP3914 Programming Assignment
 */

/**
 * Dealer class, inheritance from Player class. Change the points calculation method.
 */
public class Dealer extends Player {

    /**
     * Get the final calculated point of the dealer, override player's method
     *
     * @return dealer's final hand point
     */
    @Override
    public int getPoints() {
        int rawPoint = getRawPoint(); //get point without calculate from superclass because the calculate method is different here

        //when dealer's have an ace and sum of other hand's point between 6 to 10, count the ace as 11
        return (rawPoint >= 7 && rawPoint <= 11 && hasFirstAce()) ? rawPoint + 10 : rawPoint;
    }

}