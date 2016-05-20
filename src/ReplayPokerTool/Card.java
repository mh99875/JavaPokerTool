/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import  java.io.Serializable;

/**
 *
 * 
 * 
 * 1. Object representing playing cards used by the app and the database
 * 
 * 2. Cards are numbered 0-51 so they can be packed tightly into an array if need
 * be
 * 
 * Ace is numbered 0, King is 12
 * 
 * Spade, Heart, Diamond, Club are 0-4
 * 
 * 3. Enums
 * 
 * enum Rank {UNKOWN, TWO, THREE,...} 
 * 
 * enum Rank {UNKOWN, SPADE, HEART,...} 
 * 
 * have functions 
 * 
 * a. int number() 
 *      returns 0-12 for ranks
 *      return  0-3  for suits
 * 
 * b. String symbol() 
 *      returns a single letter for rank = ?,A,2,3,4..K
 *      returns a single letter for suit = S,H,D,C
 * 
 * 
 * 3. deckIndex returns the number 0-51 for each cards set as:
 *  deckIndex = rank.number() + (suit.number()* 13)
 * 
 * 4. Cards have hasCode nd equals so they can be used in functions and 
 * applications checking for hand value/strength, etc...
 * 
 * @author Marcus Haupt
 * 
 * @version 1.0
 * 
 */

public final class Card implements Serializable {
   
    private  static final   long    serialVersionUID = 100000043333L;
    
    boolean                 found           =   false;
    
    // 0-12     Spade
    // 13-25    Heart
    // 26-38    Diamond
    // 39-51    Club
    
    private Rank            rank            =   Rank.UNKNOWN;
    private Suit            suit            =   Suit.UNKNOWN;
    
    /**
     * deckIndex 
     * 0-12     Spade<br>
     * 13-25    Heart<br>
     * 26-38    Diamond<br>
     * 39-51    Club<br>
     * 
     */
    private int             deckIndex       =   -1;
        
    
    public enum Rank{ 
        UNKNOWN(-1, "?"),  ACE(0, "A"), TWO(1, "2"), THREE(2, "3"), FOUR(3, "4"), FIVE(4, "5"), 
        SIX(5, "6"), SEVEN(6, "7"), EIGHT(7, "8"), NINE(8, "9"), TEN(9, "T"), JACK(10, "J"), 
        QUEEN(11, "Q"), KING(12, "K");
        
        private final int       number;
        private final String    symbol;
        
        private Rank(int v, String s) {
            number  =   v;
            symbol  =   s;
        }
        
        public  int number () {
            return  number;
        }
        
        public  String  symbol() {
            return  symbol;
        }
    }
        
    
    public enum Suit { 
        UNKNOWN(-1, "?"), SPADE(0, "S"), HEART(1, "H"), DIAMOND(2, "D"), CLUB(3, "C");
        
        private final int       number;
        private final String    symbol;
        
        private Suit(int v, String s) {
            number  =   v;
            symbol  =   s;
        }
        
        public  int number () {
            return  number;
        }
        
        public  String  symbol() {
            return  symbol;
        }
    }
   
    public  Suit getSuit() {
        return suit;
    }
    
    public Rank getRank() {
        return rank;
    }
    
    public int getDeckIndex() {
        return deckIndex;
    }
    
    public void setDeckIndex() {
        deckIndex    =  rank.number() + ((suit.number()-1) * 13);
    }
    
    public  String  getSymbol() {
        return  rank.symbol() + suit.symbol();
    }
    
    @Override
    public boolean equals(Object o) {
        
        if ( o == this)
            return true;
        
        if ( !(o instanceof Card) )
            return false;
           
        Card c = (Card)o;

        return ( (c.rank == this.rank) && (c.suit == this.suit) );
    }
    
    @Override
    public int hashCode () {
        return deckIndex;
    }
    
    /**
     * Create a card of rank and suit, assign deckIndex(0-51)
     * No error checking
     * 
     * @param r Rank
     * @param s Suit
     */
   public   Card(Rank r, Suit s) {
       rank         =   r;
       suit         =   s;
       found        =   false;
       found        =   true;
       deckIndex    =  rank.number() + (suit.number()* 13);
          
   }
  
    /**
     * Copy Constructor
     * @param c Card
     */
    public  Card(Card c) {
        this.rank       =   c.rank;
        this.suit       =   c.suit;
        this.found      =   c.found;
        this.deckIndex  =   c.deckIndex; 
    }
    
    public Card() {
                
    }
    
}


    
    