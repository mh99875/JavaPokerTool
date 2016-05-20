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
public class HandEvaluator {
    
    
    public  static  int     VALUE_LOW               =   0;
    public  static  int     VALUE_MED               =   1;
    public  static  int     VALUE_HIGH              =   2;
    
    public  static  boolean SUITED                  =   false;
    public  static  boolean CONNECTED               =   false;
    public  static  boolean HIGH                    =   false;
    public  static  boolean GAPPED                  =   false;
    public  static  boolean PAIRED                  =   false;
    
    public  static  int     ACTION_FOLD             =   0;
    public  static  int     ACTION_CHECK            =   1;    
    public  static  int     ACTION_CALL             =   2;
    public  static  int     ACTION_BET              =   3; 
    public  static  int     ACTION_RAISE            =   4; 
    public  static  int     ACTION_CALL_BET         =   5; 
    public  static  int     ACTION_CHECK_CALL       =   6; 
    
    public  static  final   int NUM_MULTIPLES       =   13;
    public  static  final   int NUM_FLUSHES         =   4;
    public  static  final   int NUM_STRAIGHTS       =   10;
    
    
    Card []         holeCards                       =   new Card[2];
       
    // pair, two pair, trips and full houses all come from multiples
    Multiple  []    multiples                       =   new Multiple[13];
    Flush     []    flushes                         =   new Flush[4];
    Straight  []    straights                       =   new Straight[10];
    HighCard        highCard                        =   new HighCard();
    
    
   
    public  void    addHoleCards(Card c0, Card c1 ) {
        
        int i;
       
        for (i=0; i<NUM_FLUSHES; i++)
            flushes[i].addCards(c0, c1);
        
        for (i=0; i<NUM_STRAIGHTS; i++)
            straights[i].addCards(c0, c1);
        
        for (i=0; i<NUM_MULTIPLES; i++) {
            multiples[i].addCards(c0, c1);
        }   
        
        highCard.addCards(c0, c1);
    }
    
    public void addFlopCards(Card c0, Card c1, Card c2) {
        String  prefix  =   "HE:addFlopCards";
        int i;

        if ( (c0 == null) || (c1 == null) || (c2 == null) ) {
            System.out.println(prefix + "\tError. Null Cards Passed");
        }
        for (i=0; i<NUM_FLUSHES; i++)
            flushes[i].addCards(c0, c1, c2);
        
        for (i=0; i<NUM_STRAIGHTS; i++)
            straights[i].addCards(c0, c1, c2);
        
        for (i=0; i<NUM_MULTIPLES; i++) {
            multiples[i].addCards(c0, c1, c2);
        }   
        
        highCard.addCards(c0, c1, c2);
    }
    
    
    
    public void addTurnCard(Card c0) {
        String  prefix  =   "HE:addTurnCard";
        int i;
        
        if ( c0 == null )
            return;
        
        for (i=0; i<NUM_FLUSHES; i++)
            flushes[i].addCards(c0);
        
        for (i=0; i<NUM_STRAIGHTS; i++)
            straights[i].addCards(c0);
        
        for (i=0; i<NUM_MULTIPLES; i++) {
            multiples[i].addCards(c0);
        }   
        
        highCard.addCards(c0);
       
    }
    
    
    public void addRiverCard(Card c0) {
        String  prefix  =   "HE:addRiverCard";
        int i;
        
        if ( c0 == null )
            return;
        
        for (i=0; i<NUM_FLUSHES; i++)
            flushes[i].addCards(c0);
        
        for (i=0; i<NUM_STRAIGHTS; i++)
            straights[i].addCards(c0);
        
        for (i=0; i<NUM_MULTIPLES; i++) {
            multiples[i].addCards(c0);
        }   
        
        highCard.addCards(c0);
    }
    
    
    
    public   void    evalHoleCards(Card hc0, Card hc1 ) {
        
        int     value; // low, med, high
        boolean suited          =   false;
        boolean connected       =   false;
        boolean paired          =   false;
        boolean gapped          =   false;
        
        String  prefix          =   "HE:evalHoleCards";
        
        if ( (hc0 == null) || (hc1 == null) ) {
            System.out.println(prefix + "\tError. Hole Card/s is/are null. Folding.");
            return;
        }
        else {
            System.out.println(prefix + "\tCards: " + 
                    hc0.getRank().toString() + " of " + hc0.getSuit().toString() + " AND " +
                    hc1.getRank().toString() + " of " + hc1.getSuit().toString() );
        }
        
            
        if ( holeCardsRoyal() || 
             (hc0.getRank().number() >= 9 && hc1.getRank().number() >= 9)) { 
            value   =   VALUE_HIGH;
            System.out.println(prefix + "\tHIGH VALUE");

        }
        else if ( hc0.getRank().number() >= 6 &&
                  hc1.getRank().number() >= 6 ) {
            value   =   VALUE_MED;
            System.out.println(prefix + "\tMED VALUE");

        }
        else if ( (hc0.getRank() == Card.Rank.ACE) ||
                  (hc1.getRank() == Card.Rank.ACE) ) {
            value   =   VALUE_MED;
        }    
        else {
            value   =   VALUE_LOW;
            System.out.println(prefix + "\tLOW VALUE");
        }

        // when to call / bet / raise / fold pre-flop
        if ( holeCardsSuited() ) {
            suited          =   true;
            System.out.println(prefix + "\tSUITED");

        }
        else if ( holeCardsPaired() ) {
            paired    =   true;
            System.out.println(prefix + "\tPAIRED");
        }

        if ( holeCardsConnected() ) {
            connected   =   true;
            System.out.println(prefix + "\tCONNECTED");
        }
        else if ( holeCardsGapped() ) {
            gapped      =   true;
            System.out.println(prefix + "\tGAPPED");
        }
        
        //here's where we would add position/read adjustments
        // make sure we don't fold when we have the matching bet in front of us 
        // when we first enter the game
        int action;
        
        // add dialog boxes to display the decision
        // and the logic behind it

        if ( paired && (value == VALUE_HIGH )) {
            action  = ACTION_RAISE;
        }
        else if ( paired ) {
            action  =   ACTION_CALL_BET;
        }
        else if ( suited ) {
            action  =   ACTION_CHECK_CALL;
      
            if ( value == VALUE_HIGH )
                action  =   ACTION_RAISE;
        }
        else if ( connected && (suited || (value == VALUE_HIGH)) ) {
            action  =   ACTION_CALL_BET;
        }
        else if ( gapped && (value == VALUE_HIGH)) {
            action  =   ACTION_CHECK_CALL;
        }
        else if ( gapped && connected  ) {
            action  =   ACTION_CHECK_CALL;
        }
        else if ( value == VALUE_HIGH ) {
            action  =   ACTION_CHECK_CALL;
        }
 
        else {
            action  =   ACTION_FOLD;
        }

        System.out.println(prefix + "\tACTION = " + actionName(action));
        
    }
    
    
    public String  actionName(int action) {
        String  res =   "UNKNOWN ACTION";
        
        if ( action == ACTION_FOLD )
            res =   "FOLD";
        else if ( action == ACTION_CHECK )
            res =   "CHECK";
        else if ( action == ACTION_CALL )
            res =   "CALL";
        else if ( action == ACTION_BET )
            res =   "BET";
        else if ( action == ACTION_RAISE )
            res =   "RAISE";        
        else if ( action == ACTION_CHECK_CALL ) 
            res = "CHECK OR CALL";
        else if ( action == ACTION_CALL_BET ) 
            res = "CHECK OR BET";
        
        return res;
    }
    
    
    public  boolean holeCardsSuited() {
        
        return ( holeCards[0].getSuit() == holeCards[1].getSuit() );
    }

    
    public  boolean holeCardsRoyal() {
        
       if ( (holeCards[0].getRank() == Card.Rank.TEN )
        ||  (holeCards[0].getRank() == Card.Rank.JACK )
        ||  (holeCards[0].getRank() == Card.Rank.QUEEN )
        ||  (holeCards[0].getRank() == Card.Rank.KING )
        ||  (holeCards[0].getRank() == Card.Rank.ACE ) ) {
           
           if ( (holeCards[1].getRank() == Card.Rank.TEN )
            ||  (holeCards[1].getRank() == Card.Rank.JACK )
            ||  (holeCards[1].getRank() == Card.Rank.QUEEN )
            ||  (holeCards[1].getRank() == Card.Rank.KING )
            ||  (holeCards[1].getRank() == Card.Rank.ACE ) ) {
               
               return   true;
           }
       }
       
       return false;
    }
  
    
    public  boolean holeCardsPaired() {
        return ( holeCards[0].getRank() == holeCards[1].getRank() );
    }
    
    
    public  boolean holeCardsConnected() {
        
        boolean connected   =   false;
        
        if ( !holeCardsPaired() ) {
            
           int diff =   holeCards[0].getRank().number() - holeCards[1].getRank().number();
           
           if ( Math.abs(diff) == 1)
               connected    =   true;
           else if ( (holeCards[0].getRank() == Card.Rank.ACE) &&   
                     (holeCards[1].getRank() == Card.Rank.KING) ) 
               connected    =   true;
            else if ( (holeCards[0].getRank() == Card.Rank.KING) &&   
                      (holeCards[1].getRank() == Card.Rank.ACE) ) 
                connected   =   true;            
        }
                
        return  connected;
    }
    
    
    public  boolean holeCardsGapped() {
        
        boolean gapped   =   false;
        
        return  gapped;
    }
    
    
    
    public  HandState   evalHand(boolean villain, boolean debug) {
        String  prefix  =   "HE:evalHand";
        int i;
        HandState hs    =   new HandState();
        
        // Check for Quads
        for (i=0; i<13; i++) {
            if ( multiples[i].numFound() == 4 ) {
                    if ( debug ) System.out.println(prefix + "\tQUAD" + " " + 
                            multiples[i].toString());
                    hs.bestMade    =   multiples[i];
            }
        }
        
        
        // We didn't make Quads, see if we have a full house
        if ( hs.bestMade == null ) {
            Card.Rank   trip   =   Card.Rank.UNKNOWN;
            Card.Rank   pair;
            
            for (i=0; i<13; i++) {
                if ( trip   ==   Card.Rank.UNKNOWN ) {
                    if ( multiples[i].numFound() == 3 ) {
                        trip = multiples[i].rank();
                    }
                }
            }
            
            if ( trip  !=   Card.Rank.UNKNOWN)  {
                for (i=0; i<13; i++) {
                    if ( multiples[i].rank() != trip ) {
                        if ( multiples[i].numFound() >= 2 ) {
                            pair = multiples[i].rank();
                            if ( debug ) System.out.println(prefix + "\tFULL HOUSE");
                            hs.bestMade     =   new FullHouse(trip, pair);
                            break;
                        }
                    }
                }
            }   
        }
         
        // Flush
        if (  hs.bestMade == null ) {
            // Check for Flushes
            for (i=0; i<NUM_FLUSHES; i++) {
                if ( flushes[i].numRanksMissing() == 0 ) {
                    if ( debug ) System.out.println(prefix + "\tMADE FLUSH: " 
                            +flushes[i].suit.toString());                    
                    hs.bestMade = flushes[i];
                }
                // if we're missing one card add all matching getCardList to our draws
                else if ( flushes[i].numRanksMissing() == 1 ) {
                    if ( debug ) System.out.println(prefix + "\tFLUSH DRAW: " 
                            + flushes[i].suit.toString() );
                    hs.bestDraw =   new Hand(flushes[i]);
                    
                    for(Card c : flushes[i].missingCardsList().getCardList() ) {
                        hs.draws.addCard((Card) c);
                        if ( debug ) System.out.println(prefix + "\tDRAW TO FLUSH: " + c.getRank().toString());
                    }
                }
                else if ( villain && (flushes[i].numRanksMissing() == 1) ) {
                    if ( debug ) System.out.println(prefix + "\tFLUSH RUNNER: " 
                            + flushes[i].suit.toString()  );
                    
                    hs.bestRunner =   new Hand(flushes[i]);
                    
                    for(Card c : flushes[i].missingCardsList().getCardList() ) {
                        hs.draws.addCard((Card) c);
                        if ( debug ) System.out.println(prefix + "\tRUNNER TO FLUSH: " + c.getRank().toString());
                    }
                }
            }
        } 
        
       
        // Straight 
        if ( hs.bestMade == null ) {            
            for (i=0; i<NUM_STRAIGHTS; i++) {
                
                if ( straights[i].numRanksMissing() == 0 ) {
                    if ( debug ) System.out.println(prefix + "\tMADE STRAIGHT " + 
                           straights[i].ranks[0].toString() + " HIGH");
                     
                    if ( hs.bestMade == null )
                        hs.bestMade = straights[i];
                }
                else if ( straights[i].numRanksMissing() == 1 ) {
                    if ( debug ) System.out.println(prefix + "\tSTRAIGHT DRAW " + 
                            straights[i].ranks[0].toString() + " HIGH");
                    
                    if ( hs.bestDraw == null )
                        hs.bestDraw = new Hand(straights[i]);
                            
                    for(int j=0; j<5; j++ ) {
                        if ( straights[i].found[j] == 0)  {
                            for(Card c : straights[i].missingCardsList().getCardList() ) {
                                hs.draws.addCard((Card) c);
                                if ( debug ) System.out.println(prefix + "\tDRAW TO STRAIGHT: " 
                                    + c.getSymbol() );
                            }
                        }
                    }
                }
                else if ( villain && (straights[i].numRanksMissing() == 2) ) {
                    if ( debug ) System.out.println(prefix + "\tSTRAIGHT RUNNERS " + 
                           straights[i].ranks[0].toString() + " HIGH");
                    
                    if ( hs.bestRunner == null )
                        hs.bestRunner = new Hand(straights[i]);
                    
                    for(int j=0; j<5; j++ ) {
                        if ( straights[i].found[j] == 0)  {
                            for(Card c : straights[i].missingCardsList().getCardList() ) {
                                hs.draws.addCard((Card) c);
                                if ( debug ) System.out.println(prefix + "\tRUNNERS TO STRAIGHT: " 
                                    + c.getSymbol() );
                            }
                        }
                    }
                }
            }
        }
                    
            
        
        // Check for Three of a kind and draws to quads
        for ( i=0; i<NUM_MULTIPLES; i++) {
            if ( multiples[i].numFound() == 3 ) {
                if ( debug ) System.out.println(prefix + "\tMADE TRIP "  + 
                         multiples[i].rank().toString() );
                
                if ( hs.bestMade == null )
                        hs.bestMade    =   multiples[i];
                else {
                    if ( hs.bestDraw == null ) {
                        hs.bestDraw             = new Hand(multiples[i]);
                        hs.bestDraw.strength    = hs.bestDraw.QUADS;
                    }
                    
                    
                    for(Card c : multiples[i].missingCardsList().getCardList() ) {
                        hs.draws.addCard((Card) c);
                        if ( debug ) System.out.println(prefix + "\tDRAW TO QUADS: " 
                                + c.getSymbol() );
                    }
                }
            }
        }
        
        
        // Two Pair and One Pair
        // Two pair draws to a full house so we watch for it
        Card.Rank   pair1       =   Card.Rank.UNKNOWN;
        Card.Rank   pair2       =   Card.Rank.UNKNOWN;
        int             pair1Index  =   -1;
        int             pair2Index  =   -1;

        for (i=0; i<13; i++) {
             if (multiples[i].numFound() == 2) {
                if (pair1 == Card.Rank.UNKNOWN)  {
                    pair1          =   multiples[i].rank();
                    pair1Index     =   i;
                } else {
                    pair2   =   multiples[i].rank();
                    pair2Index     =   i;
                    break;
                }
             }
        }
        
        //  check for two pair
        if ( hs.bestMade == null ) {
            if ( pair2 != Card.Rank.UNKNOWN ) {
                hs.bestMade    =   new TwoPair(pair1, pair2);
                
                if ( debug ) System.out.println(prefix + "\tMADE PAIR: " + pair2.toString() );
                for(Card c : multiples[pair1Index].missingCardsList().getCardList() ) {
                        hs.draws.addCard((Card) c);
                        
                        hs.bestDraw             =  new Hand();
                        hs.bestDraw.strength    = hs.bestDraw.QUADS;
                        if ( debug ) System.out.println(prefix + "\tDRAW TO FULL HOUSE: " 
                                + c.getSymbol() );
                }
                 
                for(Card c : multiples[pair2Index].missingCardsList().getCardList() ) {
                        hs.draws.addCard((Card) c);
                        if ( debug ) System.out.println(prefix + "\tDRAW TO FULL HOUSE: " 
                                + c.getSymbol() );
                }
                  
            } else if ( pair1 != Card.Rank.UNKNOWN ) {
                hs.bestMade    =   multiples[pair1Index];
                
                for(Card c : multiples[pair1Index].missingCardsList().getCardList() ) {
                        hs.draws.addCard((Card) c);
                        if ( debug ) System.out.println(prefix + "\tDRAW TO TRIPS: " 
                                + c.getSymbol() );
                }
            }
        }
       
        
        // High Card, check if we would have top pair / overcards
        if (hs.bestMade   ==  null ) {
            hs.bestMade    =   highCard;
            
            if ( hs.bestDraw == null) {
                hs.bestDraw            = new Multiple(highCard.ranks[0]);
                hs.bestDraw.found[0]    =   2;
            }
        }
       
        
        return hs;
    } // end function analyzeFlop
    
    
    
    
    public HandEvaluator() {
                
        multiples[0]    =   new Multiple(Card.Rank.ACE);
        multiples[1]    =   new Multiple(Card.Rank.KING);
        multiples[2]    =   new Multiple(Card.Rank.QUEEN);
        multiples[3]    =   new Multiple(Card.Rank.JACK);
        multiples[4]    =   new Multiple(Card.Rank.TEN);
        multiples[5]    =   new Multiple(Card.Rank.NINE);
        multiples[6]    =   new Multiple(Card.Rank.EIGHT);
        multiples[7]    =   new Multiple(Card.Rank.SEVEN);
        multiples[8]    =   new Multiple(Card.Rank.SIX);
        multiples[9]    =   new Multiple(Card.Rank.FIVE);
        multiples[10]   =   new Multiple(Card.Rank.FOUR);
        multiples[11]   =   new Multiple(Card.Rank.THREE);
        multiples[12]   =   new Multiple(Card.Rank.TWO);
        
        flushes[0]      =   new Flush(Card.Suit.SPADE);
        flushes[1]      =   new Flush(Card.Suit.HEART);
        flushes[2]      =   new Flush(Card.Suit.DIAMOND);
        flushes[3]      =   new Flush(Card.Suit.CLUB);
        
        straights[9]    = new Straight(Card.Rank.FIVE, Card.Rank.FOUR, 
                Card.Rank.THREE, Card.Rank.TWO, Card.Rank.ACE);
        
        straights[8]    = new Straight(Card.Rank.SIX, Card.Rank.FIVE, 
                Card.Rank.FOUR, Card.Rank.THREE, Card.Rank.TWO);
        
        straights[7]    = new Straight(Card.Rank.SEVEN, Card.Rank.SIX, 
                Card.Rank.FIVE, Card.Rank.FOUR, Card.Rank.THREE);
        
        straights[6]    = new Straight(Card.Rank.EIGHT, Card.Rank.SEVEN, 
                Card.Rank.SIX, Card.Rank.FIVE, Card.Rank.FOUR);
        
        straights[5]    = new Straight(Card.Rank.NINE, Card.Rank.EIGHT, 
                Card.Rank.SEVEN, Card.Rank.SIX, Card.Rank.FIVE);
        
        straights[4]    = new Straight(Card.Rank.TEN, Card.Rank.NINE, 
                Card.Rank.EIGHT, Card.Rank.SEVEN, Card.Rank.SIX);
        
        straights[3]    = new Straight(Card.Rank.JACK, Card.Rank.TEN, 
                Card.Rank.NINE, Card.Rank.EIGHT, Card.Rank.SEVEN);
        
        straights[2]    = new Straight(Card.Rank.QUEEN, Card.Rank.JACK, 
                Card.Rank.TEN, Card.Rank.NINE, Card.Rank.EIGHT);
        
        straights[1]    = new Straight(Card.Rank.KING, Card.Rank.QUEEN, 
                Card.Rank.JACK, Card.Rank.TEN, Card.Rank.NINE);
        
        straights[0]    = new Straight(Card.Rank.ACE, Card.Rank.KING, 
                Card.Rank.QUEEN, Card.Rank.JACK, Card.Rank.TEN);
        
        
        
    }

       
    
}
