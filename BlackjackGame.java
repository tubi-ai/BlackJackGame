package com.company;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.Component;
import java.awt.Font;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;



public class BlackjackGame extends JFrame {
    private ArrayList<Card> playerHand;
    private ArrayList<Card> dealerHand;
    private JPanel playerCardPanel;
    private JPanel dealerCardPanel;
    private JLabel playerScoreLabel;
    private JLabel dealerScoreLabel;
    private JButton hitButton;
    private JButton standButton;
    private JButton newGameButton;
    private boolean gameOver;
    private Deck deck;

    // Kart sınıfı
    private class Card {
        private String rank;
        private String suit;
        private int value;

        public Card(String rank, String suit) {
            this.rank = rank;
            this.suit = suit;
            this.value = calculateValue(rank);
        }

        private int calculateValue(String rank) {
            if (rank.equals("A")) return 11;  // As başlangıçta 11
            if (rank.equals("K") || rank.equals("Q") || rank.equals("J")) return 10;
            return Integer.parseInt(rank);
        }

        public String getImageFileName() {
            return rank.toLowerCase() + suit.toLowerCase().charAt(0) + ".jpg";
        }

        public int getValue() { return value; }
        public void setAceToOne() { if (rank.equals("A")) value = 1; }
    }

    // Deste sınıfı
    private class Deck {
        private ArrayList<Card> cards;
        private final String[] RANKS = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        private final String[] SUITS = {"Club", "Spade", "Diamond", "Heart"};

        public Deck() {
            cards = new ArrayList<>();
            for (String suit : SUITS) {
                for (String rank : RANKS) {
                    cards.add(new Card(rank, suit));
                }
            }
            shuffle();
        }

        public void shuffle() {
            Collections.shuffle(cards);
        }

        public Card drawCard() {
            if (cards.isEmpty()) {
                resetDeck();
            }
            return cards.remove(0);
        }

        private void resetDeck() {
            cards.clear();
            for (String suit : SUITS) {
                for (String rank : RANKS) {
                    cards.add(new Card(rank, suit));
                }
            }
            shuffle();
        }
    }

    public BlackjackGame() {
        super("Blackjack Game");
        setLayout(new BorderLayout(10, 10));
        initializeGame();
        setupUI();
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initializeGame() {
        deck = new Deck();
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();
        gameOver = false;
    }

    private void setupUI() {
        // Dealer Panel
        JPanel dealerSection = new JPanel(new BorderLayout());
        dealerSection.setBorder(BorderFactory.createTitledBorder("Dealer's Hand"));
        dealerCardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dealerScoreLabel = new JLabel("Score: 0");
        dealerSection.add(dealerCardPanel, BorderLayout.CENTER);
        dealerSection.add(dealerScoreLabel, BorderLayout.SOUTH);

        // Player Panel
        JPanel playerSection = new JPanel(new BorderLayout());
        playerSection.setBorder(BorderFactory.createTitledBorder("Your Hand"));
        playerCardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        playerScoreLabel = new JLabel("Score: 0");
        playerSection.add(playerCardPanel, BorderLayout.CENTER);
        playerSection.add(playerScoreLabel, BorderLayout.SOUTH);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        hitButton = new JButton("Hit");
        standButton = new JButton("Stand");
        newGameButton = new JButton("New Game");
        
        hitButton.addActionListener(e -> playerHit());
        standButton.addActionListener(e -> playerStand());
        newGameButton.addActionListener(e -> startNewGame());

        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);
        buttonPanel.add(newGameButton);

        // Main Layout
        add(dealerSection, BorderLayout.NORTH);
        add(playerSection, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        startNewGame();
    }

    private void startNewGame() {
        playerHand.clear();
        dealerHand.clear();
        playerCardPanel.removeAll();
        dealerCardPanel.removeAll();
        deck = new Deck();
        gameOver = false;

        // Initial deal
        playerHand.add(deck.drawCard());
        dealerHand.add(deck.drawCard());
        playerHand.add(deck.drawCard());
        dealerHand.add(deck.drawCard());

        updateUI();
        hitButton.setEnabled(true);
        standButton.setEnabled(true);
    }

    private void playerHit() {
        if (!gameOver) {
            playerHand.add(deck.drawCard());
            updateUI();
            checkBust(playerHand, "Player");
        }
    }

    private void playerStand() {
        if (!gameOver) {
            gameOver = true;
            dealerTurn();
        }
    }

    private void dealerTurn() {
        while (calculateHandValue(dealerHand) < 17) {
            dealerHand.add(deck.drawCard());
            updateUI();
        }
        checkWinner();
    }

    private void updateUI() {
        playerCardPanel.removeAll();
        dealerCardPanel.removeAll();

        // Show player's cards
        for (Card card : playerHand) {
            playerCardPanel.add(new JLabel(resizeIcon(loadCardImage(card.getImageFileName()))));
        }

        // Show dealer's cards (first card face down if game not over)
        if (!gameOver) {
            dealerCardPanel.add(new JLabel(resizeIcon(loadCardImage("back.jpg"))));
            for (int i = 1; i < dealerHand.size(); i++) {
                dealerCardPanel.add(new JLabel(resizeIcon(loadCardImage(dealerHand.get(i).getImageFileName()))));
            }
        } else {
            for (Card card : dealerHand) {
                dealerCardPanel.add(new JLabel(resizeIcon(loadCardImage(card.getImageFileName()))));
            }
        }

        playerScoreLabel.setText("Score: " + calculateHandValue(playerHand));
        if (gameOver) {
            dealerScoreLabel.setText("Score: " + calculateHandValue(dealerHand));
        } else {
            dealerScoreLabel.setText("Score: ?");
        }

        revalidate();
        repaint();
    }

    private int calculateHandValue(ArrayList<Card> hand) {
        int value = 0;
        int aces = 0;

        for (Card card : hand) {
            if (card.getValue() == 11) aces++;
            value += card.getValue();
        }

        // As kartlarını gerektiğinde 1'e çevir
        while (value > 21 && aces > 0) {
            value -= 10;
            aces--;
        }

        return value;
    }

    private void checkBust(ArrayList<Card> hand, String player) {
        if (calculateHandValue(hand) > 21) {
            gameOver = true;
            hitButton.setEnabled(false);
            standButton.setEnabled(false);
            updateUI();
            JOptionPane.showMessageDialog(this, player + " busts! Game Over!");
        }
    }

    private void checkWinner() {
        int playerValue = calculateHandValue(playerHand);
        int dealerValue = calculateHandValue(dealerHand);

        updateUI();
        hitButton.setEnabled(false);
        standButton.setEnabled(false);

        String message;
        if (dealerValue > 21) {
            message = "Dealer busts! You win!";
        } else if (playerValue > dealerValue) {
            message = "You win!";
        } else if (dealerValue > playerValue) {
            message = "Dealer wins!";
        } else {
            message = "Push! It's a tie!";
        }
        JOptionPane.showMessageDialog(this, message);
    }

    private ImageIcon loadCardImage(String filename) {
        try {
            return new ImageIcon(getClass().getResource("/com/company/images/" + filename));
        } catch (Exception e) {
            System.out.println("Error loading image: " + filename);
            return new ImageIcon();
        }
    }

    private ImageIcon resizeIcon(ImageIcon icon) {
        if (icon.getImage() == null) return icon;
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(100, 150, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BlackjackGame().setVisible(true));
    }
}