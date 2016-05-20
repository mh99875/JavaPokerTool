/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

/**
 *
 * @author marcushaupt
 */

public class Hand {
    public  final   int     STRAIGHTFLUSH       = 10;
    public  final   int     QUADS               = 9;
    public  final   int     FULLHOUSE           = 8;
    public  final   int     FLUSH               = 7;
    public  final   int     STRAIGHT            = 6;
    public  final   int     TRIPS               = 5;
    public  final   int     TWOPAIR             = 4;
    public  final   int     PAIR                = 3;
    public  final   int     HIGHCARD            = 2;
    public  final   int     UNKNOWN             = 1;
    
    
    int                 numRanks                = 0;
    Card.Rank []    ranks                   = new Card.Rank[13];
    int           []    found                   = new int[13];
    Card.Suit       suit                    = Card.Suit.UNKNOWN;
    CardList            cardsMissing;
    int                 numNeeded               = 0;
    boolean             made                    = false;
    String              name                    = "NONE";
    int                 strength                = UNKNOWN;
    boolean             debug                   = false;
    

    public Hand () {
        for (int i=0; i<13; i++) {
            found[i]    =   0;
            ranks[i]    =   Card.Rank.UNKNOWN;
        }
        cardsMissing =   new CardList();
    }

    public  Hand(Hand h) {
        numRanks        =   h.numRanks;
        numNeeded       =   h.numNeeded;
        suit            =   h.suit;
        made            =   h.made;
        strength        =   h.strength;
        cardsMissing    =   h.cardsMissing;
        
        for (int i=0; i<numRanks; i++) {
            ranks[i]    =   h.ranks[i];
            found[i]    =   h.found[i];
        }
        
        
    }
    public  Card.Rank highestRank() {
        for (int i=0; i<numRanks; i++)
            if ( found[i] > 0 )
                return ranks[i];
        
        return Card.Rank.UNKNOWN;
    }
    
    public  int compareTo(Hand other) {
        int comp    =   0; // neg if I am less, pos if more
        
        // two hands of the same strength
        if ( other.strength == strength ) {
        
            if ( ranks[0].ordinal() > other.ranks[0].ordinal() )
                comp    =   1;
            else if ( ranks[0].ordinal() < other.ranks[0].ordinal() )
                comp    =   -1;
            else {
                switch ( strength ) {
                    case QUADS:
                    case FULLHOUSE:
                        // we use the last card in the case of dueling quads
                        // which is the pair of a full house
                        if ( ranks[1].ordinal() > other.ranks[1].ordinal() )
                            comp    =   1;
                        else if ( ranks[1].ordinal() < other.ranks[1].ordinal() )
                            comp    =   -1;
                    break;
                    
                case FLUSH:
                case HIGHCARD:
                    // we go in order for the next four found getCardList
                    for (int i=1; i<5; i++) {
                        if ( ranks[i].ordinal() > other.ranks[i].ordinal() ) {
                            comp    =   1;
                            break;
                        } else if ( ranks[i].ordinal() < other.ranks[i].ordinal() ) {
                            comp    =   -1;
                            break;
                        }
                    }
                    break;
                    
                case STRAIGHT:
                    comp    =   0;
                    break;
                    
                case TRIPS:
                    for (int i=1; i<2; i++) {
                        if ( ranks[i].ordinal() > other.ranks[i].ordinal() ) {
                            comp    =   1;
                            break;
                        } else if ( ranks[i].ordinal() < other.ranks[i].ordinal() ) {
                            comp    =   -1;
                            break;
                        }
                    }
                    break;
                    
                    case TWOPAIR:
                        if ( ranks[3].ordinal() > other.ranks[3].ordinal() ) {
                            comp    =   1;
                            break;
                        } else if ( ranks[3].ordinal() < other.ranks[3].ordinal() ) {
                            comp    =   -1;
                            break;
                        }
                        break;
                        
                        
                    case PAIR:
                         for (int i=1; i<5; i++) {
                        if ( ranks[i].ordinal() > other.ranks[i].ordinal() ) {
                            comp    =   1;
                            break;
                        } else if ( ranks[i].ordinal() < other.ranks[i].ordinal() ) {
                            comp    =   -1;
                            break;
                        }
                    }
                    break;
                    
                }   
            }
            
        }
        else if ( strength < other.strength )
            comp = -1;
        else if ( strength > other.strength )
            comp    = 1;
        
        return comp;
    }

    public  String    name () {
        return  name;
    }
    
    public  CardList    missingCardsList () {
        return  cardsMissing;
    }
    
    public  int numRanksMissing() {
        return  numNeeded;
    }
    
    public  boolean numberOfCardsThatFill() {
        return cardsMissing.size() == 1;
    }
   
    public  boolean madeHand() {
        return cardsMissing.size() == 0;
    }

    public  void    addRank(Card.Rank r) {
        ranks[numRanks] =   r;
        numRanks++;
    }


    public  void    addCard(Card c) {
        
        for (int i=0; i<numRanks; i++) {
            if ( ranks[i] == c.getRank() ) {
                if ( debug ) System.out.println(name() + "\tAdd Card: " + c.getSymbol());
                found[i]++;
                numNeeded--;
                cardsMissing.removeCard(c);
                break;
            }
            else if ( ranks[i] == Card.Rank.UNKNOWN)
                break;
        }       
    } // END FUNCTION ADDCARD


    public void addCards(Card ... cards) {
        for ( Card c : cards ) {
            if ( c != null)
                addCard(c);
        }
    }


    
} // END CLASS HAND


// describe from highest to lowest in ranking of elements
class Straight extends Hand {

    public Straight(Card.Rank ... r) {
        numNeeded   =   5;
        numRanks    =   5;
        name        =  "Straight";
        strength    =   STRAIGHT;
        
        if ( r.length == 5)  {
            int i=0;
            for ( Card.Rank cr : r ) {
                if (i == 0)
                    name    =   "Straight " + cr.toString() + " High";
                
                ranks[i++]        =   cr;
                // we're missing every rank, every suit
                cardsMissing.addCard(new Card(cr, Card.Suit.SPADE));
                cardsMissing.addCard(new Card(cr, Card.Suit.HEART));
                cardsMissing.addCard(new Card(cr, Card.Suit.DIAMOND));
                cardsMissing.addCard(new Card(cr, Card.Suit.CLUB));
            }
        }
    } // end STRAIGHT CTOR
    
    @Override
    public  void    addCard(Card c) {
        
        for (int i=0; i<numRanks; i++) {
            
            if ( ranks[i] == c.getRank() && (found[i] == 0) )  {
                if ( debug ) System.out.println(name + "\tRemove All of Rank " + c.getRank().toString());
                found[i]++;
                cardsMissing.removeCard(new Card(ranks[i], Card.Suit.SPADE)   );
                cardsMissing.removeCard(new Card(ranks[i], Card.Suit.HEART)   );
                cardsMissing.removeCard(new Card(ranks[i], Card.Suit.DIAMOND) );
                cardsMissing.removeCard(new Card(ranks[i], Card.Suit.CLUB)    );
                numNeeded--;
                break;
            }
            else if ( ranks[i] == Card.Rank.UNKNOWN)
                break;
        }       
        
        if ( debug ) System.out.println(name + "\tMissing " + numRanksMissing() + " rank(s)");
    } // END FUNCTION ADDCARD

    
    @Override
    public String name() {
        if ( numRanksMissing() == 0 ) {
            name    =   "STRAIGHT " + ranks[0].toString() + " HIGH";
        }
        return name;
    }
    
} // END CLASS STRAIGHT


class Flush extends Hand {
    // need the 13 possible ranks of this suit

    public Flush(Card.Suit s) {
        numNeeded       =   5;
        numRanks        =   13;
        name            =   "Flush";
        suit            =   s;
        strength        =   FLUSH;

        ranks[12]        =   Card.Rank.TWO;
        cardsMissing.addCard(new Card(Card.Rank.TWO, s));
        
        ranks[11]        =   Card.Rank.THREE;
        cardsMissing.addCard(new Card(Card.Rank.THREE, s));
        
        ranks[10]        =   Card.Rank.FOUR;
        cardsMissing.addCard(new Card(Card.Rank.FOUR, s));
        
        ranks[9]        =   Card.Rank.FIVE;
        cardsMissing.addCard(new Card(Card.Rank.FIVE, s));
        
        ranks[8]        =   Card.Rank.SIX;
        cardsMissing.addCard(new Card(Card.Rank.SIX, s));
        
        ranks[7]        =   Card.Rank.SEVEN;
        cardsMissing.addCard(new Card(Card.Rank.SEVEN, s));
        
        ranks[6]        =   Card.Rank.EIGHT;
        cardsMissing.addCard(new Card(Card.Rank.EIGHT, s));
        
        ranks[5]        =   Card.Rank.NINE;
        cardsMissing.addCard(new Card(Card.Rank.NINE, s));
        
        ranks[4]        =   Card.Rank.TEN;
        cardsMissing.addCard(new Card(Card.Rank.TEN, s));
        
        ranks[3]        =   Card.Rank.JACK;
        cardsMissing.addCard(new Card(Card.Rank.JACK, s));
        
        ranks[2]       =   Card.Rank.QUEEN;
        cardsMissing.addCard(new Card(Card.Rank.QUEEN, s));
        
        ranks[1]       =   Card.Rank.KING;
        cardsMissing.addCard(new Card(Card.Rank.KING, s));
        
        ranks[0]       =   Card.Rank.ACE;
        cardsMissing.addCard(new Card(Card.Rank.ACE, s));
        
    }

    
    @Override
    public String name() {
        for (int i=0; i<13; i++) {
            if ( found[i] > 0 ) {
                name    =   "FLUSH " + ranks[i].toString() + " HIGH";
                break;
            }
        }
        return name;
    }
    
    @Override public   void addCard(Card c) {
        if ( c.getSuit() != suit)
            return;
        
        if ( debug ) System.out.println(name + "\tCalling Super Add Card");
        super.addCard(c);
    }
    

} // END CLASS FLUSH


class Multiple extends Hand {

    public  Multiple (Card.Rank cr) {
        numNeeded   =   4;
        numRanks    =   1;
        name        =   "Multiple";
        ranks[0]    =   cr;
        
        cardsMissing.addCard(new Card(cr, Card.Suit.SPADE));
        cardsMissing.addCard(new Card(cr, Card.Suit.HEART));
        cardsMissing.addCard(new Card(cr, Card.Suit.DIAMOND));
        cardsMissing.addCard(new Card(cr, Card.Suit.CLUB));
    } 
    
    @Override
    public  String name() {
        name    =   "Multiples of " + ranks[0].toString();
        
        switch ( found[0] ) {
            case 4:
                name        =  "QUAD " + ranks[0].toString();
                strength    =   QUADS;
                break;
                
            case 3:
                name        =  "TRIP " + ranks[0].toString();
                strength    =   TRIPS;
                break;

            case 2:
                name        =  "PAIR OF " + ranks[0].toString();
                strength    =   PAIR;
                break;
        }
        return name;
    }
    
    public  int numFound() {
        return  found[0];
    }
    
    public   Card.Rank rank() {
        return ranks[0];
    }

} // END CLASS MULTIPLE


class   TwoPair extends Hand {
    
    public TwoPair(Card.Rank p1, Card.Rank p2) {
        numNeeded   =   0;
        name        =   "Two Pair";
        ranks[0]    =   p1;
        ranks[1]    =   p2;
        ranks[2]    =   Card.Rank.UNKNOWN;
        strength    =   TWOPAIR;
    }
    
    @Override
    public String name() {
        if ( (ranks[0] != Card.Rank.UNKNOWN) &&
             (ranks[1] != Card.Rank.UNKNOWN) ) {
            name    =   "TWO PAIR " + ranks[0].toString() +
                    " AND " + ranks[1].toString();
        }
        return name;
    }
    
}

class FullHouse extends Hand {
    
    public  FullHouse (Card.Rank p1, Card.Rank p2) {
        numNeeded   =   0;
        name        =   "Ful House";
        ranks[0]    =   p1;
        ranks[1]    =   p2;
        strength    =   FULLHOUSE;
    }
    
    @Override
    public String name() {
        if ( (ranks[0] != Card.Rank.UNKNOWN) &&
             (ranks[1] != Card.Rank.UNKNOWN) ) {
            name    =   "FULL HOUSE " + ranks[0].toString() +
                    " FULL OF " + ranks[1].toString();
        }
        return name;
    }

} // END CLASS FULL HOUSE 


class HighCard extends Hand {
    
    
    public HighCard () {
        numRanks    =   13;
        numNeeded   =   0;
        name        =   "HighCard";
        strength    =   HIGHCARD;
        
        ranks[0]        =   Card.Rank.ACE;
        ranks[1]        =   Card.Rank.KING;
        ranks[2]        =   Card.Rank.QUEEN;
        ranks[3]        =   Card.Rank.JACK;
        ranks[4]        =   Card.Rank.TEN;
        ranks[5]        =   Card.Rank.NINE;
        ranks[6]        =   Card.Rank.EIGHT;
        ranks[7]        =   Card.Rank.SEVEN;
        ranks[8]        =   Card.Rank.SIX;
        ranks[9]        =   Card.Rank.FIVE;
        ranks[10]       =   Card.Rank.FOUR;
        ranks[11]       =   Card.Rank.THREE;
        ranks[12]       =   Card.Rank.TWO;        
        
    }
    
 
    @Override 
    public String  name() {
        
        for (int i=0; i<13; i++) {
            if ( found[i] > 0 ) {
                name    =   "High Card " + ranks[i].toString();
                break;
            }
        }
                
        return name;
    }
}



