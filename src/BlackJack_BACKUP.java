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
 * The main class of BlackJack, included the utilities method, Game flow and global variable
 */
public class BlackJack_BACKUP {
    static final String LINE = "========================="; //this line is really ugly, don't show it again in my code
    static final Scanner scanner = new Scanner(System.in); //Initialize scanner, claim as final because we won't create a new scanner again
    static int numOfPlayer; //number of players
    static Player[] players; //list of the player
    static Dealer dealer = new Dealer(); //Dealer object
    static Deck deck; //The deck

    public static void main(String[] args) {

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
                int[] playerHand = players[i].addHand(deck.draw()).getHand();
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
                while (standOrHit == 1 && players[i].chkStatus() == Status.STAND) {
                    standOrHit = BlackJack_BACKUP.inputAndValidate((input -> input == 0 || input == 1), "Player " + (i + 1) + ", do you want to Stand or Hit (0-Stand, 1-Hit) ", "You must input 0 or 1!", scanner);
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
                if (player.chkStatus() == Status.STAND || player.chkStatus() == Status.WIN) {
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

                if (players[i].chkStatus() == Status.STAND || players[i].chkStatus() == Status.WIN) { //We only check the standing player because we already know who bust or blackjack
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
                }
            }

        } catch (ArrayIndexOutOfBoundsException e) { //when deck is empty, exception will throw
            //Finally, let them know the game terminated
            System.out.print("Deck is empty, game ended.");
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
        String rank;
        String suit;
        switch (card / 13) {
            case 0:
                suit = "Spades";
                break;
            case 1:
                suit = "Hearts";
                break;
            case 2:
                suit = "Clubs";
                break;
            case 3:
                suit = "Diamonds";
                break;
            default:
                return "Unknown";
        }
        switch (card % 13) {
            case 0:
                rank = "Ace";
                break;
            case 1:
                rank = "02";
                break;
            case 2:
                rank = "03";
                break;
            case 3:
                rank = "04";
                break;
            case 4:
                rank = "05";
                break;
            case 5:
                rank = "06";
                break;
            case 6:
                rank = "07";
                break;
            case 7:
                rank = "08";
                break;
            case 8:
                rank = "09";
                break;
            case 9:
                rank = "10";
                break;
            case 10:
                rank = "Jack";
                break;
            case 11:
                rank = "Queen";
                break;
            case 12:
                rank = "King";
                break;
            default:
                return "Unknown"; //n mod 13 < 13, This code should be unreachable
        }
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

        switch (card % 13) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            case 4:
                return 5;
            case 5:
                return 6;
            case 6:
                return 7;
            case 7:
                return 8;
            case 8:
                return 9;
            default:
                return 10;
        }
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