package com.kristautas2.ridethebus.core.model;

public class Player {
    public int defaultBet = 10;
    private long balance = 100; // New: Track available funds
    private int currentBet;
    private int totalWinnings;
    private long highScore;

    public Player() {
        this.currentBet = 0;
        this.totalWinnings = 0;
    }

    public void placeBet(int amount)  {
        defaultBet = Integer.parseInt(Integer.toString(amount));
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

    public long getBalance() {
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
        if(highScore < balance){
            highScore = balance;
        }
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

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void newPlayer() {
        balance = 100;
        defaultBet = 10;
    }

    public int getHighScore() {
        return Math.toIntExact(highScore);
    }

    public void setHighScore(int i) {
        highScore = i;
    }
}
