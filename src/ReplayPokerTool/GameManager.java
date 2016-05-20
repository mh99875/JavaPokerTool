/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

/**
 
 * Game Manager
 * 
 * implements Runnable
 * 
 * Follows the poker game running in the Replay Poker window and saves the
 * the data to a Game object which it creates in Run()
 * 
 * Listens for PokerTableScreen move events.
 * <b>startScreenMoveListener()</b>

 In the event of a moved table being playerFound again it creates a new 
 ReplayPokerTable and assigns it to game. In this way reading of the game
 continues with minimal interruption in non-pathological cases (e.g. window
 was minimized).
 
 Run:
 
 calls startScreenMoveListener() to watch for screen move events then goes 
 into an infinite loop reading game after game
 
 Waits until the userName sitting in seat 0, in the upper left hand corner,
 receives hole getCardList. This is the signal for the reading of the game to begin.
 Many poker sites that allow "bots" to collect data do not allow for a bot to
 collect data for a hand that the userName is not playing. This application 
 adheres to those guidelines.
 
 The board must also be empty to start a hand. This precludes the app from
 starting to read in the middle of a hand.
 
 If the game is ready to start, the app checks to see if there is already a
 table for this screen, if not (the first time), it creates one.
 
 
 If all is well
 1. read the hand number from the table
 2. create a new game
 3. set the hand number for the game
 4. set the userName'actor hole getCardList for the game
 5. call readHand() to read the hand from pre-flop to pot distribution
 6. wait for the next hand to start
 
 if all is not well
 1. if the Table Anchor is invalid run() calls waitForNewAnchor() and sits
 in an endless loop waiting for the anchor to return. 
 
 
 readHand()
 
 1. Upon Entry - Init State
 The function starts reading state at the moment when the blind bets and 
 missed blind bets are placed
 
 read small blind seat and amount, big blind seat and amount save to game
 state

 Read initial bets on the table besides the blinds when 
 players returning to the game after sitting out agree to pay the missed
 blinds.
 
 1. Read Pre-flop bets 
 
 check for the case of the game ending without any getCardList being seen
 the app checks for this in several ways
 there are several fail-safe cases because the worst thing that can happen is
 for the app to not recognize end-of-game. Due to the fact that the app is not
 creating it'actor own interface there is a chance that an unexpected event could
 throw it off the mark, it needs to be able to reset itself during operation
 and continue in any case.
 
 a. the userName'actor getCardList disappear which means the game has ended and the app
 missed the event. This is a fail-safe case
 
 b. The pot disappears from the table
 someone has won and we missed it, another fail-safe case
 
 c. If another player shows their getCardList the game is over, except if they are
 all-in, in which case the full board must be displayed before the game is
 over. Note that if a player goes all in and everyone folds and that player 
 decides to show their getCardList we'll catch the end of game in one of the above cases.
 
 2. Read the round Flop, Turn, River all in the following manner
 
 a. check for any player names we missed because they had the button at the
 time of reading
 
 b. read the card(actor) added to the board and store them in the game
 
 c. even out any missed bets, that is if the previousShield round ended with an
 auto-action of "call," the app will not recognize this (see reading bets 
 below for why). It recovers the state by checking every player in the hand
 at the beginning of the round and conforming that they have either 
 i. placed the same amount of money in the pot as every other player
 ii. are all-in
 
 if they have added less, we add a bet for them which makes the pot good.
 
 d. go to the player with the clock and wait for them to make a decision
 
 i. When the clock is playerFound, check to see if any bets were missed, i.e. made
 through auto action and record those
 
 ii. If all players were to auto-action check, nobody would have the clock
 and the next card(actor) would show or if it were the river then we go straight
 to pot distribution
 
 
 e. When the player with the clock releases it the app checks for their action
 by reading the shield. If it is a bet the amount is read from the number on
 the table indicating their bet. There are some instances where the number is
 hidden/obscured by a chip stack in which case we either have to rely on using
 the previousShield bet as an indication or implement a new RGBGrid class to read
 chip stacks in these rare instances which may occur at higher stake levels.
 
 Check to see if any actions were missed between this clock and the last 
 player to have the clock, add them and set the current bet if the missed
 player bet or raised.
 
 add the current player'actor action.
 
 f. If any player'actor action == SHOW or MUCK then hand is over.
 Note that we cannot guarantee the state of the game because Replay controls
 it, offers no documentation and no interface so we must be prepared for 
 anything and at the very least recover and resume
 
 g. Return to start of loop, waiting for new getCardList, reading actions based off
 of the clock or waiting for an end-of-hand signal
 
 Note that Replay often "hangs" that is sits in a state for several seconds
 including an intermediate state such as only placing 2 out of 3 flop getCardList on
 the board. The program waits until such states are complete before resuming
 
 
 h. Call endOfHandProcessing(Game game) - see below 
 
 i. Ask the game to write it'actor HandHistory
 
 j. Return and let Run() wait for the next game to begin
 
 
 
 
 <b>endOfHandProcessing(Game game)</b>
 * Called when the game is in a non-fail-safe end-of-hand state
 * 
 * When the app witnesses an orderly end-of-hand, e.g. someone has <b>SHOW</b>n
 * or <b>MUCK</b>ed, the next step for Replay is to move the winning chips to 
 the player(actor) who have won. This function captures that event.
 
 this function loops reading screens in rapid succession until it sees one
 which assigns chios to a player which comes in the form of a valid bet. That
 is to say the the bet field for a player hold both their bet and their chips
 won at the end of the game.
 
 Table.ReadPlayerBet() is called 
 
 When a winner receives chips all player also have their getCardList showing and the
 app reads the player'actor exposed hole getCardList, if any. A game can also end on a
 muck with players exposing their getCardList if there is no "showdown".
 
 The winners are recorded in the <b>game</b> as are the hole getCardList, if any.
 
  
 Follows the poker game running in the Replay Poker window and saves the
 the data to a Game object which it creates in Run()
 
 Listens for ReplayPokerWindow move events.
 <b>startWindowMoveObserver()</b>

 In the event of a moved table being playerFound again it creates a new 
 ReplayPokerTable and assigns it to game. In this way reading of the game
 continues with minimal interruption in non-pathological cases (e.g. window
 was minimized).
 
 Run:
 
 calls startWindowMoveObserver() to watch for screen move events then goes 
 into an infinite loop reading game after game
 
 Waits until the userName sitting in seat 0, in the upper left hand corner,
 receives hole getCardList. This is the signal for the reading of the game to begin.
 Many poker sites that allow "bots" to collect data do not allow for a bot to
 collect data for a hand that the userName is not playing. This application 
 adheres to those guidelines.
 
 The board must also be empty to start a hand. This precludes the app from
 starting to read in the middle of a hand.
 
 If the game is ready to start, the app checks to see if there is already a
 table for this screen, if not (the first time), it creates one.
 
 
 If all is well
 1. read the hand number from the table
 2. create a new game
 3. set the hand number for the game
 4. set the userName'actor hole getCardList for the game
 5. call readHand() to read the hand from pre-flop to pot distribution
 6. wait for the next hand to start
 
 if all is not well
 1. if the Table Anchor is invalid run() calls waitForNewAnchor() and sits
 in an endless loop waiting for the anchor to return. 
 
 
 readHand()
 
 1. Upon Entry - Init State
 The function starts reading state at the moment when the blind bets and 
 missed blind bets are placed
 
 read small blind seat and amount, big blind seat and amount save to game
 state

 Read initial bets on the table besides the blinds when 
 players returning to the game after sitting out agree to pay the missed
 blinds.
 
 1. Read Pre-flop bets 
 
 check for the case of the game ending without any getCardList being seen
 the app checks for this in several ways
 there are several fail-safe cases because the worst thing that can happen is
 for the app to not recognize end-of-game. Due to the fact that the app is not
 creating it'actor own interface there is a chance that an unexpected event could
 throw it off the mark, it needs to be able to reset itself during operation
 and continue in any case.
 
 a. the userName'actor getCardList disappear which means the game has ended and the app
 missed the event. This is a fail-safe case
 
 b. The pot disappears from the table
 someone has won and we missed it, another fail-safe case
 
 c. If another player shows their getCardList the game is over, except if they are
 all-in, in which case the full board must be displayed before the game is
 over. Note that if a player goes all in and everyone folds and that player 
 decides to show their getCardList we'll catch the end of game in one of the above cases.
 
 2. Read the round Flop, Turn, River all in the following manner
 
 a. check for any player names we missed because they had the button at the
 time of reading
 
 b. read the card(actor) added to the board and store them in the game
 
 c. even out any missed bets, that is if the previousShield round ended with an
 auto-action of "call," the app will not recognize this (see reading bets 
 below for why). It recovers the state by checking every player in the hand
 at the beginning of the round and conforming that they have either 
 i. placed the same amount of money in the pot as every other player
 ii. are all-in
 
 if they have added less, we add a bet for them which makes the pot good.
 
 d. go to the player with the clock and wait for them to make a decision
 
 i. When the clock is playerFound, check to see if any bets were missed, i.e. made
 through auto action and record those
 
 ii. If all players were to auto-action check, nobody would have the clock
 and the next card(actor) would show or if it were the river then we go straight
 to pot distribution
 
 
 e. When the player with the clock releases it the app checks for their action
 by reading the shield. If it is a bet the amount is read from the number on
 the table indicating their bet. There are some instances where the number is
 hidden/obscured by a chip stack in which case we either have to rely on using
 the previousShield bet as an indication or implement a new RGBGrid class to read
 chip stacks in these rare instances which may occur at higher stake levels.
 
 Check to see if any actions were missed between this clock and the last 
 player to have the clock, add them and set the current bet if the missed
 player bet or raised.
 
 add the current player'actor action.
 
 f. If any player'actor action == SHOW or MUCK then hand is over.
 Note that we cannot guarantee the state of the game because Replay controls
 it, offers no documentation and no interface so we must be prepared for 
 anything and at the very least recover and resume
 
 g. Return to start of loop, waiting for new getCardList, reading actions based off
 of the clock or waiting for an end-of-hand signal
 
 Note that Replay often "hangs" that is sits in a state for several seconds
 including an intermediate state such as only placing 2 out of 3 flop getCardList on
 the board. The program waits until such states are complete before resuming
 
 
 h. Call endOfHandProcessing(Game game) - see below 
 
 i. Ask the game to write it'actor HandHistory
 
 j. Return and let Run() wait for the next game to begin
 
 
 
 
 <b>endOfHandProcessing(Game game)</b>
 Called when the game is in a non-fail-safe end-of-hand state
 
 When the app witnesses an orderly end-of-hand, e.g. someone has <b>SHOW</b>n or
 <b>MUCK</b>ed, the next step for Replay is to move the winning chips to the
 player(actor) who have won. This function captures that event.
 
 this function loops reading screens in rapid succession until it sees one
 which assigns chios to a player which comes in the form of a valid bet. That
 is to say the the bet field for a player hold both their bet and their chips
 won at the end of the game.
 
 Table.ReadPlayerBet() is called 
 
 When a winner receives chips all player also have their getCardList showing and the
 app reads the player'actor exposed hole getCardList, if any. A game can also end on a
 muck with players exposing their getCardList if there is no "showdown".
 
 The winners are recorded in the <b>game</b> as are the hole getCardList, if any.
 * 
 * @author Marcus Haupt
 * @version 1.0
 */

public class GameManager implements Runnable {
    
    
    private final   ReplayPokerWindow       pts;
    private final   CardUtilities           cu;
    private         Game                    currentGame;
    private final   String                  unknownUser =   "*Unknown User*";
    
    private         Thread                  windowObserver;
    private         Thread                  playerMoveObserver;
    private         Thread                  readUsernameFromShieldDaemon;
    private         Robot                   robot; 
    
    private         int                     userSeat;
    private         int                     button;
    private         int                     previousShield;
    private         boolean                 allInToFinish;
    
    private  final  Set<Integer>        playersAllIn    =   new HashSet<>();
    
    public  enum    Display { NONE, ALL, BETS, POT, CLOCK, ACTIONS, HANDEVAL }
    
    GameManager(ReplayPokerWindow pts, CardUtilities ca) {
        
        this.pts    =   pts;
        cu          =   ca;
        button      =   -1;
        
        try {
            robot       =  new Robot();
        }
        catch (  AWTException e ) {
        }
         
    }
    
    ReplayPokerWindow    pts() {
        return  pts;
    }
    
    CardUtilities    cu() {
        return  cu;
    }
    
    Game    currentGame() {
        return  currentGame;
    }
    
    int     previousShield() {
        return  previousShield;
    }
    
    void     setPreviousShield(int ps) {
        previousShield =   ps;
    }
    
    boolean allInToTheFinish() {
        return  allInToFinish;
    }
    
    void    setAllInToTheFinish(boolean v) {
        allInToFinish = v;
    }
    
    int     button() {
        return button;
    }
    
    void    setButton(int b) {
        button =   b;
    }
    
    int     userSeat() {
        return  userSeat;
    }
    
    void    setUserSeat(int seat) {
        userSeat    =   seat;
    }
    
    /**
     * 
     * Called by PlayerMoveObserver when the userName changes seats
 
 Need to find the new button location
 
 And clear the previousShield pointer
     * 
     */
    
    void seatMoveEventHandler(int newSeat) {
        int s;
        
        BufferedImage   b   =   CardUtilities.getCurrentBoard();
        
        setPreviousShield(-1);
        
        userSeat            =   newSeat;
        
        for (s=0; s<currentGame().table().numSeats(); s++) { 

            if (currentGame().table().hasButton(s, b) ) {
                setButton(s);
                break;
            }
        }
    }
    
    public  void    startReadNameFromShieldDaemon(int seat) {
        readUsernameFromShieldDaemon          =   new Thread(new ReadUsernameFromShield(seat, this) );
        readUsernameFromShieldDaemon.setDaemon(true);
        readUsernameFromShieldDaemon.start();        
    }
    
    public  void startWindowMoveObserver() {
        windowObserver          =   new Thread(new PokerWindowObserver(this) );
        windowObserver.setDaemon(true);
        windowObserver.start();        
    }
    
    
    public  void startPlayerMoveObserver(int userSeat) {
        
        String  prefix  =   this.getClass().getSimpleName() + ":startPlayerMoveListener";
        
        if ( Session.logLevelSet(Session.logType.PLAYER_MOVE)  )
            Session.logMessageLine(prefix + "\tStarting Daemon");
        
        playerMoveObserver          =   new Thread(new PlayerMoveObserver(this, userSeat) );
        playerMoveObserver.setDaemon(true);
        playerMoveObserver.start();        
    }
    
    
    public String playerNameTabs(Game game, int s) {
        String tabs    =   "\t";
        
        if ( game.getPlayerName(s).length() < 8 ) 
            tabs    =   "\t\t";    
        
        return tabs;
    }
    
    
    public  void    readExposedHoleCards(Game game) {
        String  prefix = this.getClass().getSimpleName() + ":readExposedHoleCards";
        
        ReplayPokerTable    t   =   game.table();
        BufferedImage       b   =   CardUtilities.getCurrentBoard();
        
        //CardUtilities.printBufferSection(0, 0, 800, 800, b, "ExposedHoleCards");
        
        for (int s=0; s<t.numSeats(); s++ ) {
            int gIndex  =   gameIndexForSeat(s);

            if ( (s != userSeat()) && game.missingHoleCards(gIndex) && t.hasCardsShowing(s, b) ) {
                if ( Session.logLevelSet(Session.logType.INFO)  ) 
                    Session.logMessageLine(prefix + "\tSEAT[" + s + "] Reading Hole Cards");

                Card hc0 = cu().readHoleCard((s*2),  t.seatX(s), t.seatY(s), b);
                Card hc1 = cu().readHoleCard((s*2)+1,t.seatX(s), t.seatY(s), b);

                if ( (hc0 != null) && (hc1 != null) ) {
                    game.addHoleCards(gIndex, hc0,hc1);

                    if ( Session.logLevelSet(Session.logType.GAME) ) { 
                        Session.logMessageLine(prefix + "\t[" + s + "] HC0 " + hc0.getSymbol() );
                        Session.logMessageLine(prefix + "\t[" + s + "] HC1 " + hc1.getSymbol() );
                    }
                }
            }
        }
    }
    
    
    public  void    endOfHandProcessing(Game game) 
    throws AnchorNotFoundException
    {
         String  prefix = this.getClass().getSimpleName() + ":endOfHandProcessing";

        int     s, won;
        int     finalPot                =   0;
        
        BufferedImage       b           =   CardUtilities.getCurrentBoard();
        ReplayPokerTable    t           =   currentGame().table();
        
        int                 numSeats    =   t.numSeats();
        int                 gIndex;
        int                 nCardsBoard =   CardUtilities.numCardsOnBoard(pts().windowAnchorX(), pts().windowAnchorY()+147, b);
        
        // we shouldn't be in here with an invalid user seat but just in case
        // leave right away or we'll be waiting a long time
        if ( userSeat() < 0 )
            return;
        
        
        if ( Session.logLevelSet(Session.logType.INFO) ) {
            Session.logMessageLine(prefix + "\tEntering End of Hand Processing");
            Session.logMessageLine(prefix + "\t" + nCardsBoard + " Cards on board");
            Session.logMessageLine(prefix + "\tUser Sitting at Seat: " + userSeat() );
            Session.logMessageLine(prefix + "\tUser Cards Live? " + t.holeCardsAreLive(userSeat(), b));
        }
        
        int lastBets []  =   new int[numSeats];
        
        for (s=0; s<numSeats; s++) {
            try {
                // read player bet throws an exception if there's a bet but we can't read it
                lastBets[s] = t.readPlayerBet(s, b);
                
                if ( Session.logLevelSet(Session.logType.INFO) )
                    Session.logMessageLine(prefix + "\tSeat[" +s + "] last bet: " + lastBets[s]);
            }
            catch ( UnableToReadNumberException e) {
                lastBets[s]  =   0;
            }
        }
        
        endOfHandProcessing:
        while ( true ) {
            
            // read the final pot
            if ( finalPot == 0 ) {
                try {
                    finalPot            = t.potValue(b, Game.Pot.UNCALLED); 
                    
                    game.setFinalPot(finalPot);

                    if ( Session.logLevelSet(Session.logType.GAME) )
                        Session.logMessageLine(prefix + "\tFinal Pot: " + finalPot);
                }
                catch ( UnableToReadNumberException e) {
                }

                if ( Session.logLevelSet(Session.logType.INFO)  )
                    CardUtilities.printBufferSection(0, 0, 800, 800, b, "PotDistAndHoleCards");
            }
            
            // read hole getCardList
            // hole getCardList are shown before winnings are distributed
            readExposedHoleCards(game);
            
            // wait for someone to win the chips
            for (s=0; s<numSeats; s++ ) {
                try {
                        won = t.readPlayerBet(s, b);
                        if ( won > lastBets[s] ) {
                            if ( Session.logLevelSet(Session.logType.INFO) )
                                Session.logMessageLine(prefix + "\tSeat[" + s + "] has extra chips. End of processing");
                            break   endOfHandProcessing;
                        }
                    }
                    catch ( UnableToReadNumberException e) {
                        if ( Session.logLevelSet(Session.logType.INFO) )
                            Session.logMessageLine(prefix + "\tSeat[" + s + "] has extra chips we can't read. End of processing");
                        
                        break   endOfHandProcessing;
                    }
            }
               
            // end of hand when the board getCardList disappear
            b   =   CardUtilities.getCurrentBoard();
            
            if ( nCardsBoard > 0 ) {
                if ( 0 == CardUtilities.numCardsOnBoard(pts().windowAnchorX(), pts().windowAnchorY()+147, b) ) {
                    if ( Session.logLevelSet(Session.logType.INFO) )
                        Session.logMessageLine(prefix + "\tCards Disappeared. End of Processing.");
                    break;
                }
            } else {
                // in he even that the hand finished without board getCardList, leave when the user's getCardList
                // disappear
                if ( t.userCardsMissing(userSeat(), b) )
                    break;
            }
        } // end while loop
            
        if ( Session.logLevelSet(Session.logType.INFO) )
            Session.logMessageLine(prefix + "\tReading Chips Distributed to Winners");
        
        // read the rest of the players winnings
        for (s=0; s<numSeats; s++ ) {
            try {
                won = t.readPlayerBet(s, b);
                if ( won > lastBets[s] ) {
                    won -= lastBets[s]; // subtract the uncalled bet, which the dealer returns
                    
                    if ( Session.logLevelSet(Session.logType.GAME) )
                        Session.logMessageLine(prefix + "\tSeat[" + s + "] Won: " + won);
                    
                    game.setChipsWon(gameIndexForSeat(s), won);
                }
                  
            } catch ( UnableToReadNumberException e) {
                if ( Session.logLevelSet(Session.logType.INFO) )
                    Session.logMessageLine(prefix + "\tSeat[" + s + "] Won but we failed to read");
            }
        }
        
        // wait until all shown getCardList are removed before trying to find a new game
        int n=numSeats;
        while ( n >0 ) {
          
            for (s=0; s<t.numSeats(); s++) {
                n=0;
                if ( t.hasCardsShowing(s, CardUtilities.getCurrentBoard())) {
                    n++;
                    break;
                }
            }
        }
        
        // seen the getCardList and the pot, time to go
        if ( Session.logLevelSet(Session.logType.INFO) )
            Session.logMessageLine(prefix + "\tFinished End of Hand Processing");
           
    }
                
   
    public  void    findUnknownPlayers(BufferedImage b, Game game) {
        
        // there will be at least one unknwn player due to the clock blocking one
        // player during init
        
        // during the initializationkeep track of how many names we didn't read
        
        // the missing names are wrt the button as the zero seat
        
        // find the button then find the index of the seats we missed 
        
        // then read those seats and put them in the game
        
        
    }
   
    
    // returns the max amount bet that was missed
    public  int    readMissedBets(Game game, Game.Round round, BufferedImage b, int previous, int actual) {
        
        int maxBet          =   0, bet;   
        int first           =   (previous == 8) ? 1 : (previous+1);
        Game.ActionType action;
        
        
        for (int m=first; m<actual; m++) {
            if ( m >= game.numSeats() )
                m    =   0;
                  
            if ( game.table().holeCardsAreLive(m,  b )) {
                try {
                    bet     = game.table().readPlayerBet(m,  b);
                    action  = game.table().readShieldAction(m, b);
                            
                    game.addAction(m, round, action, bet);
                
                    if ( bet > maxBet)
                        maxBet  =   bet;
                }
                catch ( UnableToReadNumberException e) {
                    
                }
            }
        }
        
        return maxBet;
    }
    
    
    /**
     * 
     * @param game is the current game
     * @param b Buffered Image the current screen capture
     * @return the big blind
 
 
 Read all bets on the table at start of game which includes blinds and
 extra bets placed by people who have missed the blinds
 
 Add the players to the game using the button as player number 1
 This way if the userName rotates the table actions will be recorded  relative
 to the button
     */
   
    
    public  int readInititalBets(Game game, BufferedImage b) {
        
        String  prefix  =    this.getClass().getSimpleName() + ":readInititalBets";
       
        int bet;
        int s;
        int gIndex; // index into game'actor fixed array
        
        // read the bets on the table at start of hand
        // blinds plus extra bets
        
        // always re-find the button in this function
        setButton(-1);
        for (s=0; s<game.numSeats(); s++) { 
            
            if (game.table().hasButton(s, b) ) {
                setButton(s);
                 
                if ( Session.logLevelSet(Session.logType.GAME)  ) 
                    Session.logMessageLine(prefix + "\tButton at Seat[" + button() + "]");
                
                break;
            }
        }
        
        if ( button() < 0 ) {
            // unable to read button, unable to read game
            if ( Session.logLevelSet(Session.logType.INFO)  ) 
                Session.logMessageLine(prefix + "\tButton Not Found.");
            return -1;
        }
        
        // if we jump into the middle of a hand we can't read the blinds
        // we should have read the blnds while we were waiting for the user name
        try {
            if ( cu().numCardsOnBoard(b) != 0  ) {
                return 0;
            }
        }
        catch ( AnchorNotFoundException e) {
            
        }
        
        /**
         * Add players to the Game starting with the button
         * 
         * we could have the game read the chip stack and create a dummy name
         * to be filled in later
         */
                
        int seat            =   button();
                        
        boolean smallBlindSeen  =   false;
        boolean bigBlindSeen    =   false;
        boolean playerFound;
        
        int     initialBets []  =   new int[game.numSeats()];
        
        for (gIndex=0; gIndex<game.numSeats(); gIndex++) {
            
            playerFound   =   false;
            
            // try to add the player at this seat
            if ( game.addPlayer(seat, gIndex, b) ) {
                playerFound   =    true;
            }
            else if ( !game.table().isSittingOut(seat, b) ) {
                game.addUnknownPlayer(seat, gIndex);
            }
            
            
            // add the bets for all players including those sitting out
            try {
                bet  =   game.table().readPlayerBet(seat, b);
                
                initialBets[gIndex] =   bet;
                    
                if ( playerFound )
                    game.addAction(gIndex, Game.Round.PREFLOP, Game.ActionType.ACTION_BET, bet);
                
                // the first two bets seen after the button are the blinds
                if ( (bet > 0) && (gIndex > 0) ) {
                    if ( !smallBlindSeen ) {
                        smallBlindSeen = true;
                         
                        if ( Session.logLevelSet(Session.logType.GAME)  )
                            Session.logMessageLine(prefix + "\tSmall Blind @ gIndex[" + gIndex + "] Amt = " + bet);
                        
                        game.setSmallBlindIndex(gIndex);
                        
                       
                    }
                    else if ( !bigBlindSeen ) {
                        bigBlindSeen = true;
                        
                        if ( Session.logLevelSet(Session.logType.GAME)  )
                            Session.logMessageLine(prefix + "\tBig Blind @ gIndex[" + gIndex + "] Amt = " + bet);
                         
                        game.setBigBlindBet(bet);
                        game.setBigBlindIndex(gIndex);
                    }
                }
            }
            catch ( UnableToReadNumberException e) {
                // they have a bet but we can't read it
                initialBets[gIndex] =   0;
            }
            
            seat++;
            
            if ( seat >= game.numSeats() )
                seat = 0;
        }

        
        // write down the extra bets and the antes
        for (gIndex=0; gIndex<game.numSeats(); gIndex++) {
            if ( (gIndex != game.bigBlindIndex()) && (gIndex != game.smallBlindIndex()) ) {
                if ( initialBets[gIndex] >= game.bigBlindBet() ) {
                    game.addAction(gIndex, Game.Round.PREFLOP, Game.ActionType.ACTION_BET, initialBets[gIndex]);
                }
                else if (  initialBets[gIndex] > 0 ) {
                    game.addAction(gIndex, Game.Round.PREFLOP, Game.ActionType.ACTION_ANTE, initialBets[gIndex]);
                }
            }
        }
        
        if ( Session.logLevelSet(Session.logType.GAME)  ) {
            for (s=0; s<game.numSeats(); s++)
                Session.logMessageLine(prefix + "\tSeat[" + s + "] " + game.getPlayerName(s));
        }
       
        return game.bigBlindBet();
    }
    
    
    /**
     * 
     * 
     * @param game is the current game
     * @param round the current round
     * @param currentBet the value of the current bet
     * @throws HandOverException 
     * 
     * Wait for someone to get the clock and act, thereby releasing it
     */
    
    public  void waitForAction(Game game, Game.Round round, int currentBet) 
    throws HandOverException
    {
        String              prefix  =   this.getClass().getSimpleName() + ":waitForAction";
        
        Game.ActionType     action; 
        BufferedImage       b;
        int actor;

        
        if ( Session.logLevelSet(Session.logType.CLOCK)  )
            Session.logMessageLine(prefix + "\tInside Function");
        
        // find the player with the clock
        for (actor=0; actor<game.numSeats(); actor++) {
            b           =   CardUtilities.getCurrentBoard();
                
            if ( Session.logLevelSet(Session.logType.CLOCK)  )
                Session.logMessageLine(prefix + "\tChecking Seat[" + actor + "]");
                
            // only check people with live shields
            // although in a tournament everyone can folds to a sitting out
            // player, at which point I believe they muck automatically, and the 
            // game is over
            if ( !game.table().isSittingOut(actor, b) ) {

                if ( game.table().hasClock(actor, b ) ) {
                    if ( Session.logLevelSet(Session.logType.CLOCK)  )
                        Session.logMessageLine(prefix + "\tSeat[" + actor + "] has the clock");
                    
                    // how many people auto-acted before this shield?
                    if ( previousShield() >= 0 ){
                        int skipped=previousShield();
                        
                        for (int i=1; i<game.table().numSeats(); i++) {
                            skipped++;
                            
                            if ( skipped >= game.table().numSeats())
                                skipped =   0;
                            
                            if ( skipped == actor )
                                break;
                            
                            processShieldAction(game, b, skipped, round, currentBet);
                        }
                    }
                    
                    // return when the clock is released
                    while ( game.table().hasClock(actor, b ) )  {
                        b           =   CardUtilities.getCurrentBoard();
                    }
                    
                    setPreviousShield(actor);
                    if ( Session.logLevelSet(Session.logType.CLOCK)  )
                                    Session.logMessageLine(prefix + "\tSeat Has Acted[" + actor + "]");
                    
                    processShieldAction(game, b, actor, round, currentBet);
                }
                else {
                    // does a player show/muck while we are waiting for a clock
                    action  =   game.table().readShieldAction(actor,  b);

                    if ( (action == Game.ActionType.ACTION_SHOW) ||
                         (action == Game.ActionType.ACTION_MUCK) ) {

                         if ( Session.logLevelSet(Session.logType.GAME) || Session.logLevelSet(Session.logType.CLOCK) )  
                            Session.logMessageLine(prefix + 
                                "\tSEAT[" + actor + "] SHOW/MUCK HAND OVER");    
                         
                         throw  new HandOverException(prefix + "\tShow/Muck on shield");
                    } 
                }
            }
            else {
                if ( Session.logLevelSet(Session.logType.INFO) )
                    Session.logMessageLine(prefix + "\tChecking Seat[" + actor + "] is Sitting Out");
            }
        }
    }
    
    
    public  int gameIndexForSeat(int seat) {
        
        if ( button() < 0 ) {
            String              prefix  =   this.getClass().getSimpleName() + ":gameIndexForSeat";
            
            BufferedImage       b       =   CardUtilities.getCurrentBoard();
            
            for (int s=0; s<currentGame().numSeats(); s++) { 
            
                if (currentGame().table().hasButton(s, b) ) {
                    setButton(s);
                 
                    if ( Session.logLevelSet(Session.logType.GAME)  ) 
                        Session.logMessageLine(prefix + "\tButton at Seat[" + button() + "]");
                
                    break;
                }
            }
        }
        
        if ( seat == button() )
            return 0;
        
        int numSeats    =   currentGame().table().numSeats();
        int check       =   button();
        
        for (int index=1; index<numSeats; index++) {
            check++;
            
            if ( check >= numSeats )
                check = 0;
            
            if( check == seat )
                return index;
        }
        
        // not playerFound, fatal error
        return -1;
        
    }
    
    
    /**
     * 
     * @param game is the current game
     * @param b is the current screen capture
     * @param seat is the shield to be read
     * @param round the current round
     * @param currentBet the most recent bet
     * @return the current bet
     * 
     * @throws HandOverException signals that the hand is over and control 
     * should switch to reading the final pot and winners
     */
    public  int processShieldAction(Game game, BufferedImage b, int seat, Game.Round round, int currentBet) 
    throws HandOverException
    {
        
        String              prefix  =   this.getClass().getSimpleName() + ":processShieldAction";
        
        Game.ActionType     action;       
        
        action  = game.table().readShieldAction( seat,  b);
        
        // if the userName changes seats we'll never read this shield action
        // just return if we are caught in here
        while ( action ==  Game.ActionType.NOT_AN_ACTION ) {
            b       =    CardUtilities.getCurrentBoard();
            action  = game.table().readShieldAction( seat,  b);
            
            if ( userSeat() != game.table().userSeat() )
                return currentBet;
        }
        
        int bet;
        
        try { 
            bet     = game.table().readPlayerBet(seat,  b);
        }
        catch ( UnableToReadNumberException e) {
            bet     =   currentBet;
        }
            
        if ( Session.logLevelSet(Session.logType.GAME) )
            Session.logMessageLine(prefix + "\t[" + round + "] S[" + 
                seat + "] " + action.toString() + " " + bet);

        switch ( action ) {
            case ACTION_SHOW:
            case ACTION_MUCK:
                if ( Session.logLevelSet(Session.logType.GAME) ) 
                    Session.logMessageLine(prefix + 
                            "\tROUND[" + round.toString() + "]\tSEAT[" + seat + "] SHOW/MUCK HAND OVER");
                
                throw new HandOverException(prefix);


            case ACTION_ALLIN:
                if ( bet > currentBet )
                    currentBet = bet;

                game.addAction(gameIndexForSeat(seat), round, action, bet);
                playersAllIn.add(seat);
                
                // how many players are left in the game
                int nPlayersLeft=0;
                for (int i=0; i<game.table().numSeats(); i++) {
                    if ( game.table().hasGoldShield(i, b) )
                        nPlayersLeft++;
                }
                
                if ( playersAllIn.size() >= nPlayersLeft-1)
                    setAllInToTheFinish(true);
                
                // does this make the game all-in to the end?
                // that is are there at least 2 players left who can bet?
                for (int s=0; s<game.table().numSeats(); s++) {
                    
                }
                
                if ( Session.logLevelSet(Session.logType.INFO)  ) {
                    try {
                        if ( round == Game.Round.PREFLOP ) {
                            if ( Session.logLevelSet(Session.logType.INFO)  )
                                Session.logMessageLine(prefix + "\tPOT AFTER BET: " + 

                            game.table().potValue(b, Game.Pot.PREFLOP) );
                        }else {
                            if ( Session.logLevelSet(Session.logType.INFO)  )
                                Session.logMessageLine(prefix + "\tPOT AFTER BET: " + 

                            game.table().potValue(b, Game.Pot.UNCALLED) );
                        }
                    }
                    catch ( UnableToReadNumberException | AnchorNotFoundException e) {

                    }
                }
                break;


            case ACTION_CHECK:
                game.addAction(gameIndexForSeat(seat), round, action, 0);
                break;


            case    ACTION_CALL:
            case    ACTION_BET:
            case    ACTION_RAISE:

                if ( bet > currentBet ) {
                    currentBet  =   bet;
                } 

                game.addAction(gameIndexForSeat(seat), round, action, currentBet); 

                if ( Session.logLevelSet(Session.logType.INFO)  ) {
                    try {
                        if ( round == Game.Round.PREFLOP ) {
                            if ( Session.logLevelSet(Session.logType.INFO)  )
                                Session.logMessageLine(prefix + "\tPOT AFTER BET: " + 

                            game.table().potValue(b, Game.Pot.PREFLOP) );
                        } else    {
                            if ( Session.logLevelSet(Session.logType.INFO)  )
                                Session.logMessageLine(prefix + "\tPOT AFTER BET: " + 

                            game.table().potValue(b, Game.Pot.UNCALLED) );
                        }
                    }
                    catch ( UnableToReadNumberException | AnchorNotFoundException e) {

                    }
                }
                break;
                

            case ACTION_FOLD:
                if ( Session.logLevelSet(Session.logType.INFO)  )
                    Session.logMessageLine(prefix + "\tROUND[" + round.toString() + "] SEAT[" + seat + "] FOLD");

                game.addAction(gameIndexForSeat(seat), round, action, 0);
                break;
        } // end switch
            
        
        return currentBet;
    }
    
    
    public  boolean    processFlop(Game game, BufferedImage b) {
        
        String  prefix  =   this.getClass().getSimpleName() + ":processFlop";
        
        try {
            if ( Session.logLevelSet(Session.logType.INFO)  )
                            Session.logMessageLine(prefix + "\tFLOP CARDS OUT");
            
            findUnknownPlayers(b,game);
            cu().readFlopCards();
            
            Card    fc0 =   cu().flopCard(0);
            Card    fc1 =   cu().flopCard(1);
            Card    fc2 =   cu().flopCard(2);
            
            if ( (fc0 == null) || (fc1==null) || (fc2 == null ) ) 
                return false;
            
            game.addBoardCards(fc0, fc1, fc2); 

            if ( Session.logLevelSet(Session.logType.INFO)  )
                Session.logMessageLine(prefix + 
                    fc0.getSymbol() + " " + 
                    fc1.getSymbol() + " " + 
                    fc2.getSymbol() );

            if ( Session.logLevelSet(Session.logType.POT)  ) {
                b                   = CardUtilities.getCurrentBoard();
                try {
                    Session.logMessageLine(prefix + "Uncalled Pot: " 
                            + game.table().potValue(b, Game.Pot.UNCALLED));
                }
                catch ( UnableToReadNumberException e) {

                }
            }

            game.evenOutBets(Game.Round.PREFLOP, b);
        }
        catch ( AnchorNotFoundException e) {
            return false;
        }
        
        return true;
    }
    
    
    public  boolean    processTurn(Game game, BufferedImage b) {
        
        String  prefix  =   this.getClass().getSimpleName() + ":processTurn";
        
        try {
            
            if ( Session.logLevelSet(Session.logType.INFO)  )
                Session.logMessageLine(prefix + "\tTURN CARD OUT");
                                    
            Card    turn    =   cu().readTurnCard();
            
            if ( turn == null ) {
                if ( Session.logLevelSet(Session.logType.INFO)  )
                    Session.logMessageLine(prefix + "\tFalse Alarm Turn Card: " + 
                        cu().numCardsOnBoard( b ) );
                return false;
            }
                        
            game.addBoardCard(turn);

            findUnknownPlayers(b,game);

            
            if ( Session.logLevelSet(Session.logType.POT)  ) {
                b                   = CardUtilities.getCurrentBoard();
                try {
                    Session.logMessageLine(prefix + "Uncalled Pot: " 
                            + game.table().potValue(b, Game.Pot.UNCALLED));
                }
                catch ( UnableToReadNumberException e) {

                }
            }

           game.evenOutBets(Game.Round.FLOP, b);
        
        }
        catch ( AnchorNotFoundException e ) {
            return false;
        }
                     
        return true;
    }
    
    
    
    boolean processRiver(Game game, BufferedImage b) {
        
        String  prefix  =   this.getClass().getSimpleName() + ":processRiver";
        
        try {
        
            if ( Session.logLevelSet(Session.logType.INFO)  )
                            Session.logMessageLine(prefix + "\tRIVER CARD OUT");
                        
                findUnknownPlayers(b,game);
                        
                Card    river   =    cu().readRiverCard();  
                
                if ( river == null) 
                    return false;
                
                game.addBoardCard(river);
                
                if ( Session.logLevelSet(Session.logType.POT)  ) {
                    b                   = CardUtilities.getCurrentBoard();
                    try {
                        Session.logMessageLine(prefix + "Uncalled Pot: " 
                                + game.table().potValue(b, Game.Pot.UNCALLED));
                    }
                    catch ( UnableToReadNumberException e) {

                    }
                }
                        
            game.evenOutBets(Game.Round.TURN, b);            
        }
        catch ( AnchorNotFoundException e) {
            return false;
        }
        
        return true;
    }
    
    
    public  void    readMissedNames(BufferedImage b) {
        Game                g       =   currentGame();
        ReplayPokerTable    t       =   g.table();
        String              prefix  =   this.getClass().getSimpleName() + ":printPlayerStats";
        
        
        int s, gIndex;

        // check every seat for someone we didn't read
        for (s=0; s< t.numSeats(); s++) {
           if ( !t.hasClock(s, b) ) {
               gIndex   =   gameIndexForSeat(s);
               
                // if this shield hasn't been read, read it
                if ( g.getPlayerName(gIndex).equals("Unknown") ) {
                    try {
                        g.setPlayerName(gIndex, t.readShieldName(s, b) );
                        
                        if ( Session.logLevelSet(Session.logType.INFO)  )
                            Session.logMessageLine(prefix + "\tAdding Missed Name " +
                                t.readShieldName(s, b) + " at Seat[" + s + "] to Game Index[" + 
                                gIndex + "]" );
                    }
                    catch ( CantReadNameException  | SeatXYException e) {
                        
                    }
                }
            }
        }
        
    }
    
    
    public  void printPlayerStats(Game game, BufferedImage b, int currentBet) {
        
        String  prefix  =   this.getClass().getSimpleName() + ":printPlayerStats";
        
        int s;
        
         // maybe print this out for game too
        if ( Session.logLevelSet(Session.logType.INFO)  ) {
            Session.logMessageLine(prefix + "\tPRE FLOP RUNDOWN\n");
            Session.logMessageLine(prefix + "\tCurrent Bet: " + currentBet );
        
            for (s=0; s<game.numSeats(); s++) {
                if ( game.playerKnown(s) ) {

                    if ( game.getChipStack(s) == 0 ) {
                        try {
                            game.setChipStack(s, game.table().readShieldChipStack(s, b));
                        }
                        catch ( UnableToReadNumberException e) {

                        }
                    }

                    String msg = String.format("%s\tSEAT[%d]%15s : %d",
                            prefix, s, game.playerAtSeat(s).getScreenName(), game.getChipStack(s) );

                    Session.logMessageLine(msg);
                }    
            }
        }
        
    }
            
    
    public  boolean readHand(Game game, int numHandsPlayed ) 
    throws AnchorNotFoundException
    {
        String              prefix          =   
                this.getClass().getSimpleName() + ":readHand[" + numHandsPlayed + "]";
        
        BufferedImage       b;
        boolean             processFlop     =   true;
        boolean             processTurn     =   true;
        boolean             processRiver    =   true;
        int                 currentBet;
        Game.Round          round           =   Game.Round.PREFLOP; 

        b = CardUtilities.getCurrentBoard();
       
        // current bet is the big blind
        currentBet = readInititalBets(game,  b);
        
        // didn't read the button, wait for next hand
        if ( currentBet < 0 )
            return true;
        
        untilEndOfHand:
        while ( true  ) {            
            b                   =   CardUtilities.getCurrentBoard();
             
            switch ( cu().numCardsOnBoard(b) ) {
               
                case 3:
                    if ( processFlop && (round == Game.Round.PREFLOP) ){
                        if ( processFlop(game,b) ) {
                            round               =   Game.Round.FLOP;
                            processFlop         =   false;
                            currentBet          =   0;
                            setPreviousShield(-1);
                        }
                    }
                    break;
                
                    
                case 4:
                    if ( processTurn && (round == Game.Round.FLOP) )  {
                        
                        if ( processTurn(game, b) ) {
                            round               =   Game.Round.TURN;
                            processTurn         =   false;
                            currentBet          =   0;
                            setPreviousShield(-1);
                        }                        
                    }
                    break;
                    
                    
                case 5:
                    if ( processRiver && (round == Game.Round.TURN) ) {
                        if ( processRiver(game, b)) {
                            round               =   Game.Round.RIVER;
                            processFlop         =   false;
                            currentBet          =   0;
                            setPreviousShield(-1);
                            
                            // at least P-1 players went all in meaning no more
                            // betting, show all getCardList and see who wins
                            if ( allInToTheFinish() ) {
                                endOfHandProcessing(game);
                                break   untilEndOfHand;
                            }
                        }
                    }
                    break;
            }// end process board getCardList
                
            // wait until there'actor a shield to process
            // loops looking for a shield or EndOfHand indication
            try {
                if ( !allInToTheFinish() )
                    waitForAction(game, round, currentBet);
                
                // read any missed names while we're in the first few rounds
                if ( (round == Game.Round.PREFLOP) || (round == Game.Round.FLOP) ) {
                    readMissedNames(b);
                }
                
            }
            catch ( HandOverException e ) {
                if ( Session.logLevelSet(Session.logType.INFO)  ||  Session.logLevelSet(Session.logType.CLOCK)  )
                    Session.logMessageLine(prefix + "\tEnd of Hand Signal Received");  
                    
                endOfHandProcessing(game);
                break;
            }            
        } // while true (until end of hand); // while true (until end of hand); // while true (until end of hand); // while true (until end of hand); // while true (until end of hand); // while true (until end of hand); // while true (until end of hand); // while true (until end of hand); // while true (until end of hand); // while true (until end of hand); // while true (until end of hand); // while true (until end of hand); // while true (until end of hand); // while true (until end of hand); // while true (until end of hand); // while true (until end of hand);
        
        return true;
    }
    
    
    
    public  boolean  readUserNameFromShield(int s, ReplayPokerTable table) {
    
        BufferedImage   b       =   CardUtilities.getCurrentBoard();
        String          prefix  =   this.getClass().getName() + ":readUserNameFromShield";
        
        boolean         debug   =   Session.logLevelSet(Session.logType.GAME);
        
        
        if ( table.hasClock(s,b)  || (table.readShieldAction(s, b) != Game.ActionType.NOT_AN_ACTION ) ) {
            // if the shield is obscured send the job to the daemon
            //ReadUsernameFromShield
            startReadNameFromShieldDaemon(s);
            Session.setUserName(unknownUser);
            return false;
                    
        }
        else {
            // read it right here, right now
            try {
                if ( debug )
                    Session.logMessageLine(prefix + "\tAttmpting to read name");

                String userName    =   table.readShieldName(s, b);

                if ( userName   != null ) {
                    Session.setUserName(userName);

                    if ( debug )
                        Session.logMessageLine(prefix + "\tSet User Name to " + Session.userName());
                }
                return true;
            }
            catch ( ActionNotNameException e) {
                // give the shield more time to release the action and return to a name

                robot.delay(1000);
            }
            catch ( CantReadNameException | SeatXYException  e ) {
                if ( debug )
                    Session.logMessageLine(prefix + "\tError Reading Screen Name: " + e.toString()); 
                Session.setUserName(unknownUser);
                // fatal errors when it comes to reading names
            }
        }
        
        return false;
    }
            
    
        
    public  void    initializeUser(Game g) {
    
        String              prefix      =   this.getClass().getSimpleName() + ":initializeUser";
        BufferedImage       b;
        ReplayPokerTable    t           =   g.table();
        int                 showing;
        int                 user        =   -1;
        boolean             clock;
                    
        Card                hc0;
        Card                hc1;
        
        while ( true  ) {
            b           =   CardUtilities.getCurrentBoard();
            showing     =   0;
            clock       =   false;
            
            for (int s=0; s<t.numSeats(); s++ ) {
                if ( t.hasCardsShowing(s, b) ) {
                    showing++;
                    user    =   s;
                }
            }
            
            for (int s=0; s<t.numSeats(); s++ ) {
                if ( t.hasClock(s, b) ) {
                    clock   =   true;   
                    break;
                }
            }
          
            if ( Session.logLevelSet(Session.logType.GAMETRANSITION) )
                Session.logMessageLine("GM:Init" + "\tCLK: " + clock + " SHW: " + 
                showing + " USR: " + user + " RT: " + t.hasCardRightEdge(user) +
                " TP:" + t.hasCardTopEdge(user));
                        
             
            if ( clock && (showing == 1) && (user>=0) && t.hasCardRightEdge(user) && 
                 t.hasCardTopEdge(user) ) {
                // one person showing, clock on the table, this is our user
                
                 if ( Session.logLevelSet(Session.logType.GAME) )
                    Session.logMessageLine(prefix + "\tSeat[" + user + "] Setting User Seat-> " + user);
                 
                setUserSeat(user);
                
                if ( Session.logLevelSet(Session.logType.GAME) )
                    Session.logMessageLine(prefix + "\tSeat[" + user + "] Reading Hole Cards");

                
                hc0     =   cu().readHoleCard((user*2),   t.seatX(user), t.seatY(user), b);
                hc1     =   cu().readHoleCard((user*2)+1, t.seatX(user), t.seatY(user), b);
                
                if ( (hc0 != null) && (hc1 != null) ) {
                
                    if ( Session.logLevelSet(Session.logType.GAME) )
                        Session.logMessageLine(prefix + "\tSeat[" + user + "] Adding Hole Cards");
                    
                    if ( Session.logLevelSet(Session.logType.GAMETRANSITION) ) {
                        Session.logMessageLine(prefix + "\t" + hc0.getSymbol() + " " + hc1.getSymbol());
                    }
                
                    g.addHoleCards(gameIndexForSeat(user), hc0, hc1);
                                
                    if ( (Session.userName() == null) || ( Session.userName().equals(unknownUser)) ) {
                        if ( Session.logLevelSet(Session.logType.GAME) )
                            Session.logMessageLine(prefix + "\tSeat[" + user + "] Get the User Name");
                     
                        readUserNameFromShield(user, t);
                    }
                    
                    return;
                }
                else {
                     if ( Session.logLevelSet(Session.logType.GAMETRANSITION) ) {
                        Session.logMessageLine(prefix + "\tError Reading Cards. Trying Again");                        
                    }
                }
            }
            else {
                
                if ( !clock )
                    CardUtilities.printBufferSection(0, 0, 800, 800, b, "WonkyClock");
               
                
                // find user by virtue of having cards folded
                if ( !t.hasCardLeftEdge(user, b) && t.holeCardsAreFolded(user) ) {
                    setUserSeat(user);
                    readUserNameFromShield(user, t);
                    return;
                }
            }
        }
        
    }
    
    
 
    @Override
    public void run() {
        
        startWindowMoveObserver();
        
        String              handNumber;
        BufferedImage       b;
        
        int                 numHandsPlayed  =   0;
        String              prefix          =   this.getClass().getSimpleName() + ":\trun";
        ReplayPokerTable    table;
        Card                hc0=null, hc1=null;
        
        // read games in a forever cycle
        while ( true ) {
            
            try {
                // always use a new table for every game
                
                if ( Session.logLevelSet(Session.logType.NEWGAME)  )
                    Session.logMessageLine(prefix + "\tTop of while loop");
                
                table                       =   new ReplayPokerTable( pts() );
                
                //cu().readHoleCard((5*2+1),  table.seatX(5), table.seatY(5),
                //        CardUtilities.getCurrentBoard() );
                //Session.exit(0);
                
                setPreviousShield(-1);
                setAllInToTheFinish(false);
                
                playersAllIn.clear();
                
                handNumber                  =   table.readHandNumber(CardUtilities.getCurrentBoard());

                currentGame                 =   new Game(table);
                
                currentGame().setHandNumber(handNumber);   
                
                if ( Session.logLevelSet(Session.logType.GAME)  )
                    Session.logMessageLine(prefix + "\tInitialze User");       
                
                // find the user if we have no idea who's playing this game
                // read their hole cards as well
                // we have to see the hole cards exposed if we're looking for the user for the first time
                
                initializeUser(currentGame());

                if ( Session.logLevelSet(Session.logType.GAME) )
                    Session.logMessageLine(prefix + "\tUser: " + Session.userName() 
                    + " Sitting at Seat: " + userSeat());
                
                startPlayerMoveObserver(table.userSeat());

                readHand(currentGame(), numHandsPlayed++);
                               
                currentGame().saveHistory(userSeat);
                
                if ( Session.logLevelSet(Session.logType.GAME)  ) {
                    Session.logMessageLine(prefix + "\t---------------------- END READING HAND # " 
                                    + handNumber  + " (" + numHandsPlayed + 
                                    " hand played) ----------------------");
                }

                hc0 =   hc1 =   null;
                cu().resetAllCards();
                
            }
            catch ( UnableToReadCharException | NoSeatsFoundException e ) {
                if ( Session.logLevelSet(Session.logType.INFO) ) {
                    Session.logMessageLine(e.getMessage());
                    Session.logMessageLine(prefix + "\tReading Hand Failed.");
                }
            }
            catch ( AnchorNotFoundException e ) {
                // the screen was moved and we need a new pw
                if ( Session.logLevelSet(Session.logType.INFO) )
                    Session.logMessageLine(prefix + "\tThe screen has moved");                
            } 
            if ( Session.logLevelSet(Session.logType.NEWGAME) )
                Session.logMessageLine(prefix + "\tBack to the Top of the GameManager::run loop");
        } // end while 
    }
}
