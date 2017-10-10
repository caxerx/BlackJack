/*
    File: Status.java
    Name: Tsang Tsz Pan
    Class: IT114105/1A
    Student Number: 170048177
    Description: The BlackJack Game, ITP3914 Programming Assignment
 */

/**
 * Status enum class, store the constant of status.
 */
public enum Status {
    STAND(""), BLACKJACK("BlackJack!"), BUST("Bust!"), WIN("Win!"), LOSE("Lose!"), PUSH("Push!");

    private String displayName; //What to show when a player is in this status

    Status(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get status's display name. It show on player is in that status.
     *
     * @return Status's Display name
     */
    public String getDisplayName() {
        return displayName;
    }
}