/*
    File: BlackJack.java
    Name: Tsang Tsz Pan
    Class: IT114105/1A
    Student Number: 170048177
    Description: The BlackJack Game, ITP3914 Programming Assignment
 */

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.function.Function;

/**
 * The main class of BlackJack, included the utilities method, Game flow
 */
public class BlackJack {

    public static void main(String[] args) {
        //Define constant and variable
        final String LINE = "========================="; //this line is really ugly, don't show it again in my code
        final Scanner scanner = new Scanner(System.in); //Initialize scanner, claim as final because we won't create a new scanner again
        int numOfPlayer; //number of players
        Player[] players; //list of the player
        Dealer dealer = new Dealer(); //Dealer object
        Deck deck; //The deck

        /*
            Stage0: Pre-Start
                In this stage, you can choose enter test mode or not. Then input the number of players
         */

        int testMode = inputAndValidate((input -> input == 0 || input == 1), "Go to Test Mode (0-No, 1-Yes): ", "You must input 0 or 1!", scanner);
        if (testMode == 1) {
            /*
                Test Mode
                    In Test Mode, User can create the custom deck.
             */
            deck = new Deck(); //Create an empty deck
            int cardNumber = 1; //Card serial
            do {
                int testCard = inputAndValidate((input -> input >= 0 && input <= 52), "Input Card" + cardNumber + " in your deck (0 to end): ", "You must input a number in 0-52", scanner);
                if (testCard == 0) {
                    break;
                }
                deck.addCard(testCard - 1);
                cardNumber++;
            } while (true);
        } else {
            deck = Deck.buildStandardDeck(); //create the shuffled standard deck if not test mode
        }

        //input number of players
        numOfPlayer = inputAndValidate((input -> input > 0), "How many player? ", "Number of players must be greater than or equal to 1!", scanner);
        //Initialize array to store player's hand
        players = new Player[numOfPlayer];
        for (int i = 0; i < numOfPlayer; i++) {
            players[i] = new Player();
        }

        //The main game flow in the try block, it catch the exception and end the game peacefully without winner or loser when the deck is empty
        try {
            /*
                Stage1: Game Start
                    In this stage, player and dealer get a unrevealed card, and get a revealed card.
                    Add status If player have BlackJack here
             */
            System.out.println("\nGame Start (" + numOfPlayer + " players)");
            System.out.println(LINE);

            //Give each player the first card
            for (int i = 0; i < numOfPlayer; i++) {
                players[i].addHand(deck.draw());
            }
            //Give the first card to dealer
            dealer.addHand(deck.draw());

            //Draw players second card and print the hands
            for (int i = 0; i < numOfPlayer; i++) {
                players[i].addHand(deck.draw());
                int[] playerHand = players[i].getHand();
                //The first card is an unknown card
                System.out.println("Player " + (i + 1) + "'s Hand: [ Unknown " + getCardDisplayName(playerHand[1]) + " ]");

                //Add Status if they get BlackJack
                if (players[i].getPoints() == 21) {
                    players[i].setStatus(Status.BLACKJACK);
                }
            }

            //Draw dealer's second card and print the hands
            dealer.addHand(deck.draw());
            System.out.println("Dealer's Hand: [ Unknown " + getCardDisplayName(dealer.getHand()[1]) + " ]");

            //Add Status if dealer BlackJack
            if (dealer.getPoints() == 21) {
                dealer.setStatus(Status.BLACKJACK);
            }

            /*
                Stage2: Player's Round
                    In this stage, player will choose Stand or Hit.
             */
            System.out.println("\nPlayers' Round (" + numOfPlayer + " players)");
            System.out.println(LINE);

            for (int i = 0; i < numOfPlayer; i++) {
                System.out.println("Player " + (i + 1) + "'s Hand: " + players[i].getCardInfo());
                //Require player to input
                int standOrHit = 1;
                while (standOrHit == 1 && players[i].chkStatus() == Status.STAND && players[i].getPoints() != 21) {
                    standOrHit = BlackJack.inputAndValidate((input -> input == 0 || input == 1), "Player " + (i + 1) + ", do you want to Stand or Hit (0-Stand, 1-Hit) ", "You must input 0 or 1!", scanner);
                    if (standOrHit == 1) {
                        players[i].addHand(deck.draw());
                        System.out.println("Player " + (i + 1) + "'s Hand: " + players[i].getCardInfo());
                    }
                }
            }

            /*
                Stage3: Dealer's Round
                    In this stage, dealer will get card until their hand point is greater or equal to 17.
                    If all player is Bust or BlackJack, skip this round.
             */
            System.out.println("\nDealers' Round (" + numOfPlayer + " players)");
            System.out.println(LINE);

            boolean startDealerRound = false; //dealer's turn will not skip if this is true

            //Check player's status, if any player is standing, start dealer's turn
            for (Player player : players) {
                if (player.chkStatus() == Status.STAND) {
                    startDealerRound = true;
                    break;
                }
            }
            if (startDealerRound) {
                //Show all player's hands
                for (int i = 0; i < numOfPlayer; i++) {
                    System.out.println("Player " + (i + 1) + "'s Hand: " + players[i].getCardInfo());
                }

                //Show dealer's hands
                System.out.println("Dealer's Hand: " + dealer.getCardInfo());

                //Dealer will draw card until their hand point >= 17.
                while (dealer.getPoints() < 17) {
                    System.out.println("Lower than 17, add new cards!");
                    dealer.addHand(deck.draw());
                    dealer.chkStatus();
                    //Show dealer's hands after they get a card
                    System.out.println("Dealer's Hand: " + dealer.getCardInfo());
                }
            } else {
                //Show that dealer's round is skipped because all player won or lost.
                System.out.println("All players have won or lost the game!");
            }

            /*
                Stage4: Find the winner
                    In this stage, Player and Dealer will count this final points. Then find out the winner and loser.
             */

            int dealerPoint = dealer.getPoints(); //get dealer's point
            //find out each player's status
            for (int i = 0; i < numOfPlayer; i++) {
                if (players[i].chkStatus() == Status.STAND) { //We only check the standing player because we already know who bust or blackjack
                    //If dealer is not bust, check player's point
                    if (dealer.chkStatus() != Status.BUST) {
                        if (players[i].getPoints() > dealerPoint) {
                            players[i].setStatus(Status.WIN); //set status to win when their point is larger than dealer
                        } else if (players[i].getPoints() < dealerPoint) {
                            players[i].setStatus(Status.LOSE); //set status to lose when dealer's point is larger than player
                        } else {
                            players[i].setStatus(Status.PUSH); //set status to push when dealer and player get same point
                        }
                    } else {
                        //if dealer is bust, all standing player will win
                        players[i].setStatus(Status.WIN);
                    }
                } else if (players[i].chkStatus() == Status.BLACKJACK && dealer.chkStatus() == Status.BLACKJACK) { //PUSH if both player and dealer got blackjack
                    players[i].setStatus(Status.PUSH);
                }
            }

        } catch (ArrayIndexOutOfBoundsException e) { //when deck is empty, exception will throw
            //let them know the game terminated because out of card
            System.out.println("Deck is empty, game ended.");
        }

         /*
            Stage5: Show Result
                In this stage, Show player and dealer's game result
         */
        System.out.println("\nFinal Result (" + numOfPlayer + " players)");
        System.out.println(LINE);

        //print player's final hand and status
        for (int i = 0; i < numOfPlayer; i++) {
            System.out.println("Player " + (i + 1) + "'s Hand: " + players[i].getCardInfo());
        }
        //print dealer's final hand and status
        System.out.println("Dealer's Hand: " + dealer.getCardInfo());

    }


    /**
     * Utilities method, Get the display name of a card by a card number
     *
     * @param card The number that represent a card
     * @return The card's display name shown by "RANK:SUIT", return "Unknown" if card not exist;
     */
    public static String getCardDisplayName(int card) {
        final String[] ranks = {"Spades", "Hearts", "Clubs", "Diamonds"};
        final String[] suits = {"Ace", "02", "03", "04", "05", "06", "07", "08", "09", "10","Jack","Queen","King"};
        String rank = (card / 13) < 4 ? ranks[(card / 13)] : "Unknown";
        String suit = (card % 13) < 13 ? suits[(card % 13)] : "Unknown";
        return suit + ":" + rank;
    }

    /**
     * Utilities method, Get the point of a card by a card number
     *
     * @param card The number that represent a card
     * @return card's point, ace will return 1 and a card that not exist will return 0
     */
    public static int getCardPoint(int card) {
        //return 0 when the number is not representing a standard deck
        if (card > 51 || card < 0) {
            return 0;
        }
        final int[] point = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        return (card % 13) < 9 ? point[(card % 13)] : 10;
    }


    /**
     * Utilities method, Require user to input a value within a provided condition. If their input don't match the condition, show an message and let them input again.
     *
     * @param condition A function that check the input is valid.
     * @param prompt    Prompt message when input.
     * @param msgErr    Error message when the input is not valid.
     * @param scanner   The scanner that should be use to receive the input.
     * @return The user input that match the provided condition.
     */
    public static int inputAndValidate(Function<Integer, Boolean> condition, String prompt, String msgErr, Scanner scanner) {
        Integer input = null; //To store the input value, not using primitive int type here because we want a null as init value
        do {
            System.out.print(prompt); //prompt msg before input
            try { //to catch exception of the non-Integer input
                input = scanner.nextInt(); //wait for input
                if (!condition.apply(input)) { //Determine input is valid, show Error message if not.
                    System.out.println(msgErr);
                }
            } catch (InputMismatchException e) {
                scanner.nextLine(); //skip the input causing exception
                System.out.println(msgErr); //Show an error msg
            }
        } while (input == null || !condition.apply(input)); //repeat requiring input until user input a valid input
        return input;
    }

    /**
     * Utilities method, Add an item to a int array
     *
     * @param originArray The origin array
     * @param itemToAdd   Item to add
     * @return The array after item is added
     */
    public static int[] arrayAppend(int[] originArray, int itemToAdd) {
        //if the original array is zero-size or null, create a 1 slot array with the data.
        if (originArray == null || originArray.length == 0) {
            originArray = new int[]{itemToAdd};
        } else { //otherwise create copy of the original array but have an extra slot
            originArray = Arrays.copyOf(originArray, originArray.length + 1);
            //finally add the item to the last slot of the array
            originArray[originArray.length - 1] = itemToAdd;
        }
        return originArray;
    }


}