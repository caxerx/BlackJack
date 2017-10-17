/*
    File: Deck.java
    Name: Tsang Tsz Pan
    Class: IT114105/1A
    Student Number: 170048177
    Description: The BlackJack Game, ITP3914 Programming Assignment
 */

import java.util.Arrays;
import java.util.Random;

/**
 * Deck class, provide some method to control the deck array.
 */
public class Deck {
    private int[] deck = new int[0]; //To store the deck, initialize a 0 size array to prevent Null-Pointer Exception

    /**
     * Get a deck that shuffled with Fisher-Yates shuffle algorithm
     *
     * @return A shuffled 52-card standard deck.
     */
    public static Deck buildStandardDeck() {
        Deck deck = new Deck(); //create an empty deck
        // Then fill the number to the deck
        for (int i = 0; i < 52; i++) {
            deck.addCard(i);
        }
        // Finally shuffle the deck
        deck.shuffle();
        return deck;
    }

    /**
     * Add a card to the last of a deck
     *
     * @param card card to add
     */
    public void addCard(int card) {
        deck = BlackJack.arrayAppend(deck, card);
    }

    /**
     * Draw the card from the top of deck
     *
     * @return The top card in the deck
     */
    public int draw() {
        int draw = deck[0]; //store the top card
        deck = Arrays.copyOfRange(deck, 1, deck.length); //remove the first card from the deck
        return draw;
    }

    /**
     * Shuffle the deck with Fisher-Yates shuffle Method.
     * <p>
     * Fisher-Yates shuffle Method:
     * for i start from 51 down to 0,
     * each time roll a number j that between 0 to i,
     * swap deck[i] and deck[j] if i not equal to j
     */
    public void shuffle() {
        Random random = new Random();
        for (int i = 51; i > 1; i--) {
            int j = random.nextInt(i + 1);
            if (i != j) {
                int temp = deck[i];
                deck[i] = deck[j];
                deck[j] = temp;
            }
        }
    }
}