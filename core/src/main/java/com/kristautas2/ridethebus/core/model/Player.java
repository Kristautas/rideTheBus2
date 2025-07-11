package com.kristautas2.ridethebus.core.model;

public class Player {
    private int balance = 100; // New: Track available funds
    private int currentBet;
    private int totalWinnings;

    public Player() {
        this.currentBet = 0;
        this.totalWinnings = 0;
    }

    public void placeBet(int amount)  {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (amount > balance) {
            System.out.println("Not enough money");
            //throw new IllegalArgumentException("Insufficient balance");
        }
        balance -= amount;
        currentBet = amount;
    }

    public int getBalance() {
        return balance;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public int getTotalWinnings() {
        return totalWinnings;
    }

    public void addWinningsToBalance() {
        balance += totalWinnings; // Add winnings to balance
    }

    public void addWinnings(int amount){
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        totalWinnings = amount;
    }

    public void reset() {
        currentBet = 0;
        totalWinnings = 0;
    } // Combines resetBet and resetWinnings
}
