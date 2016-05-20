/**
 * 
 * 
 * <p>Object that stores game state to be saved to a file or a persistent medium</p?
 * 
 * <p>has a ReplayPokerTable
 * in the event that the user moves the screen, the GameManager may assign a 
 * new table to the Game</p?
 * 
 * <p>keeps track of which players are known meaning the PlayerNamesReader has
 * read their name and their seat has been found</p?
 * 
 * 
 * <b>CTOR</b>
 * 
 * <p>initializes all of the arrays and lists</p>
 * 
 * <p>evens our bets as described in GameManager by summing up all the best made
 * by a player and seeing if they are whole (if they match the others who
 * haven't gone all-in)</p>
 * 
 *
 * @author  Marcus Haupt
 * @version 1.0
 * 
 */
package ReplayPokerTool;

import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Game {
    
    private         ReplayPokerTable                table;
            
    private final       String                      handHistoryFile         =   "Replay_Hand_History_";
    private final       String                      boardCardHistoryFile    =   "Replay_BoardCard_History.txt";    
    
    private final       Player          []          seatedPlayers;
    private final       int             []          chipStacks;
    private final       int             []          chipsWon;
    private final       PlayerActions   []          playerActions;
    private final       ArrayList<PlayerAction>     actions                 =   new ArrayList<>();
    private final       CardList        []          holeCards;
    private final       CardList                    boardCards              =   new CardList();
    
    private final       ReplayHistoryWriter         hhWriter;   
    private final       ReplayHistoryWriter         bcWriter;
             
    private         int                             finalPot                =   0;
    private         String                          handNumber;
    private         int                             bigBlindBet             =   0;
    private         int                             bigBlindIIndex          =   -1;
    private         int                             smallBlindIndex         =   -1;
    private final   int                             buttonIndex             =   0;
    
    
    public enum    Pot      { RAKE, PREFLOP, CALLED, UNCALLED, SPLIT1, SPLIT2, SPLIT3, SPLIT4 }
    
    public enum    Round    { PREFLOP(0), FLOP(1), TURN(2), RIVER(3), POTDIST(4);
                        
                        private Round(int value) {
                            this.value  =   value;
                        }
                        
                        int value() {
                            return value;
                        }
                        
                        private final   int value;
                    }
    
    
    public enum ActionType {
        ACTION_VOID, ACTION_ERROR, ACTION_ANTE, ACTION_CALL, ACTION_RAISE, ACTION_BET, ACTION_ALLIN, ACTION_FOLD, 
        ACTION_MUCK, ACTION_SHOW, ACTION_CHECK, NOT_AN_ACTION, ACTION_OUT, ACTION_POST_BB, 
        ACTION_POST_SB, ACTION_SIT_IN, ACTION_SIT_OUT, ACTION_WAITING, ACTION_ALREADY_OUT;
        
    }
    
    public  ReplayPokerTable   table() {
        return  table;
    }
    
    
    public  void    setPokerTable(ReplayPokerTable t) {
        table  =   t;
    }
          
    public  String handNumber() {
        return  handNumber;
    }
    
    public void setHandNumber(String hn) {
        
        if ( Session.logLevelSet(Session.logType.GAME) )
            Session.logMessageLine("Game:\tHandNumber = " + hn);
        
        handNumber   = hn;
    }
    
    
    public int bigBlindBet() {
        return  bigBlindBet;
    }
    
    public  void setBigBlindBet(int bbb) {
        if ( Session.logLevelSet(Session.logType.GAME) )
            Session.logMessageLine("Game:\tBig Blind Bet = " + bbb);
        
        bigBlindBet    =   bbb;
    }
    
    public  int bigBlindIndex() {
        return  bigBlindIIndex;
    }
    
    public  void setBigBlindIndex(int i) {
        if ( Session.logLevelSet(Session.logType.GAME) )
            Session.logMessageLine("Game:\tBig Blind Index = " + i);
         
        bigBlindIIndex =   i;
    }
    
    public int  smallBlindIndex() {
        return  smallBlindIndex;
    }
    
    public  void setSmallBlindIndex(int i) {
        if ( Session.logLevelSet(Session.logType.GAME) )
            Session.logMessageLine("Game:\tSmall Blind Index = " + i);
        
        smallBlindIndex =   i;
    }
    
  
    public String   getPlayerName(int s) {
        if ( seatedPlayers[s] == null )
            return "";
        
        return seatedPlayers[s].getScreenName();
    }
    
    public  int numSeats() {
        return table.numSeats();
    }
   
    
    public  void    setFinalPot(int p) {
         if ( Session.logLevelSet(Session.logType.GAME) )
            Session.logMessageLine("Game:\tSet Final Pot = " + p);
         
        finalPot   =   p;
    }
    
    
    public int  finalPot() {
        return  finalPot;
    }
    
    
    public  void    saveHistory(int userIndex) {
        String  prefix  =   this.getClass().getSimpleName() + ":saveHistory";
        try {
            
            ( new SimpleDateFormat( "yyyy-MM-dd" ) )
                    .format( Calendar.getInstance().getTime() );
                
            saveHandHistory();  
            saveBoardCardHistory();
        }
        catch (Exception e ) {
            Session.logMessageLine(prefix + "\tError Writing Files: " + e.toString());
        }
    }
    
    
    public  void    saveBoardCardHistory() {
        
        StringBuilder  msg =    new StringBuilder();
        
        msg.append(handNumber());
        msg.append(" ");
       
        int i;
                
        for (i=0; i<5; i++) {
            if ( i < boardCards.size() ) {
                msg.append(boardCards.cardNumber(i).getSymbol());
                msg.append(" ");
            }            
        }
        
        msg.append("\n");
        bcWriter.write(msg.toString(), true);
    }
    
    
    void    printBoardCards() {
        Session.logMessageLine("printBoardCards Enter");
        for (int i=0; i<boardCards.size(); i++) {
            Session.logMessageChar(boardCards.cardNumber(i).getSymbol() + "\t");
        }
        Session.logMessageLine("printBoardCards Exit");
    }
    
    public  void    saveHandHistory() {
        
        StringBuilder  msg  =   new StringBuilder();
        
        msg.append("HAND\t");
        msg.append(handNumber);
        msg.append("\n");
        
        msg.append("TABLE\t");
        msg.append(table.getName());
        msg.append("\n");
        
        int p,c;
        int numSeats    =   numSeats();
        
        for ( p=0; p<numSeats; p++)  {
            if ( seatedPlayers[p] != null ) {
                msg.append(p);
                msg.append("\t");
                msg.append(seatedPlayers[p].getScreenName());
                msg.append("\n");
            }
        }
        
        msg.append("FinalPot\t");
        msg.append(finalPot);
        msg.append("\n");
        
        for ( p=0; p<numSeats; p++)  {
            if ( chipsWon[p] > 0 )  {
                msg.append("WINNER\t");
                msg.append(p);
                msg.append("\t");
                msg.append(chipsWon[p]);
                msg.append("\n");
            }
        }
        
        msg.append("BU\t");
        msg.append(buttonIndex);
        msg.append("\n");
        msg.append("SB\t");
        msg.append(smallBlindIndex);
        msg.append("\t");
        msg.append(bigBlindBet/2);
        msg.append("\n");
        msg.append("BB\t");
        msg.append(bigBlindIIndex);
        msg.append("\t");
        msg.append(bigBlindBet);
        msg.append("\n");
        
        msg.append("BD\t");
        
        for (c=0; c<boardCards.size(); c++) {
            msg.append(boardCards.cardNumber(c).getSymbol()); 
            msg.append("\t");
        }
        
        msg.append("\n");
        
        // write the hole getCardList
        for ( p=0; p<numSeats; p++)  {
            if ( holeCards[p] != null ) {
                if ( holeCards[p].size() == 2 ) {
                    msg.append("HC");
                    msg.append(p);
                    msg.append("\t");
                    msg.append(holeCards[p].cardNumber(0).getSymbol());
                    msg.append("\t");
                    msg.append(holeCards[p].cardNumber(1).getSymbol());
                    msg.append("\n");
                }
            }
        }
        
        // write the player actions
        for (c=0; c< actions.size(); c++ ) {
            msg.append(actions.get(c).seat());
            msg.append("\t");
            msg.append(actions.get(c).round().toString());
            msg.append("\t");
            msg.append(actions.get(c).action().toString());
            msg.append("\t");
            msg.append(actions.get(c).amount());
            msg.append("\n");
        }
        
        hhWriter.write(msg.toString(), false);
    }
    
    
    public  void    printGameSummary() {
        String  prefix  =   this.getClass().getSimpleName() + ":printGameSummary";
        
        
        Player  player;
        // I could also go by round as follows
        // If I had a call to PlayerAcitons::actionType(Type, Round)
        //Round   []      round   =  { Round.PREFLOP, Round.FLOP, Round.TURN, Round.RIVER };
    
        
        int bets, calls, raises, allin;
        
        Session.logMessageLine("\n\n");
        
        Session.logMessageLine(prefix + "\tFINAL POT: " + finalPot);
        
        
        for ( int p=0; p<numSeats(); p++)  {
            
           bets = calls = raises  =   0;
            
            if ( seatedPlayers[p] != null ) {
                player  =   seatedPlayers[p];
               
                calls    +=  playerActions[p].actionType(ActionType.ACTION_CALL);
                bets     +=  playerActions[p].actionType(ActionType.ACTION_BET);
                raises   +=  playerActions[p].actionType(ActionType.ACTION_RAISE);
                allin    =   playerActions[p].actionType(ActionType.ACTION_ALLIN);
            
                Session.logMessageLine(String.format("%s\tSEAT[%d]%12s Call %d Bet %d Raise %d Allin %d",
                              prefix, p, player.getScreenName(), calls, bets, raises, allin));
            }   
        }   
    }
    
    
    public  void    evenOutBets(Round round, BufferedImage b) 
    throws AnchorNotFoundException
    {
        String  prefix  =   this.getClass().getSimpleName() + ":evenOutBets";
        
        if ( round == Round.POTDIST )
            return;
       
        // find the max value bet by each player for this round
        // we're counting those in at start of round which maynot be exact
        // if people drop out between rounds
        
        int maxbet  =   0;
        int thisbet;
        
        //what is the max bet for this round
        for ( int p=0; p<numSeats(); p++)  {
            if ( table().holeCardsAreLive(p,b) ) {
                
                thisbet = playerActions[p].maxInvestmentForRound(round);
                if ( thisbet > maxbet )
                    maxbet  =   thisbet;
            }
        }
       
        
        // now find out who didn't match the bet
        // we should also consider their stack size might be zero
        // found by subtracting all bets explicit and implicit from
        // the original stack size
       for ( int p=0; p<numSeats(); p++)  {
            if ( table().holeCardsAreLive(p,b) ) {
                thisbet = playerActions[p].maxInvestmentForRound(round);
                if ( thisbet < maxbet ) {
                    // make up the difference
                    addAction(p, round, ActionType.ACTION_BET, (maxbet-thisbet) );
                }
            }
        }
        
        // Display All The Bets So Far
        if ( Session.logLevelSet(Session.logType.INFO) ) {
            Player  player;
            for ( int p=0; p<numSeats(); p++)  {

                if ( seatedPlayers[p] != null ) {
                    player  =   seatedPlayers[p];

                        String  msg =   String.format("%s\tSEAT[%d][%s]%12s BETS %d",
                               prefix, p, round.toString(), player.getScreenName(), 
                               playerActions[p].maxInvestmentForRound(round) );

                        Session.logMessageLine(msg);             
                }                
            }
        }
    }
   
    
    
    void   addAction(int index, Round round, ActionType action, int amount) {
         String prefix  =   this.getClass().getSimpleName() + ":addAction";
         
         // the bet is the total bet so far this round
         // to find the individual bets we need to take the differences
         
         // playerActions is an object that contains all player actions and 
         // can answer questions such as how much have they bet this round, so far etc...
         
         // we would like to make sure that we haven't already added this bet (duplicate)
         //meaning that this bet is bigger than the previous bet known for this round
         
        if ( action == Game.ActionType.ACTION_ERROR ) {
            int  [] a = new int[0];
            a[399] = 6;
            return;
        }
         
         
        if ( Session.logLevelSet(Session.logType.INFO) ) 
            Session.logMessageLine(prefix + "\tIndex[" + index + "] " + 
                        action.toString() + (amount > 0 ? " AMT: " + amount : "" ) );
       
        PlayerAction    pa  =   new PlayerAction(index, action, amount, round);
        actions.add(pa);
        
         if ( amount > playerActions[index].maxInvestmentForRound(round) )
            playerActions[index].addAction(pa);
           
     }
     
    
    void printPlayerNames() {
        String  prefix  =   this.getClass().getSimpleName()  + ":printPlayerNames";
        
        for (int s=0; s<numSeats(); s++) {
            if ( seatedPlayers[s] != null ) {
                Session.logMessageLine(prefix + "\tSeat[" + s + "] = " + seatedPlayers[s].getScreenName());
            } else {
                Session.logMessageLine(prefix + "\tSeat[" + s + "] = EMPTY");
            }
        }
    }
    
    
    public  int numSeated() {
        
        int n = 0, s;
        
        for ( s = 0; s < numSeats(); s++)
            if ( seatedPlayers[s] != null )
                n++;
        
        return n;
    }
    
    public  int numInHand(BufferedImage b) {
        int n = 0;
        
        for (int s=0; s<numSeats(); s++) {
            
            if ( (seatedPlayers[s] != null) && !table().isSittingOut(s, b) ) {
                n++;
            }
        }
        
        return n;
    }
                
    public  boolean playerKnown(int s)  
    throws  IllegalArgumentException
    {
        if ( (s<0) || ( s > numSeats()) )
            throw new IllegalArgumentException ("playerKnown " + s + " invalid");
                
        return  seatedPlayers[s] != null;
    }
    
    public  Player playerAtSeat(int s)
    throws  IllegalArgumentException
    {
    
        if ( seatedPlayers[s] == null )
            throw   new IllegalArgumentException ("PlayerAtSeat\tNo Player at Seat[" + s + "]");
        return seatedPlayers[s];
    }
    
    public  String playerNameAtSeat(int s) 
    throws  IllegalArgumentException
    {
        if ( seatedPlayers[s] == null )
            throw   new IllegalArgumentException ("playerNameAtSeat\tNo Player at Seat[" + s + "]");
        
        return seatedPlayers[s].getScreenName();
    }
    
    
    public  int getChipsWon(int index) {
        return chipsWon[index];
    }
    
    
    public  void setChipsWon(int index, int amount) {
       
        if ( (amount > 0) && ( chipsWon[index] == 0) ) { 
            if ( Session.logLevelSet(Session.logType.GAME) )
                Session.logMessageLine("Game:\tChips Won[" + index + "] " + amount);
             
            chipsWon[index] =   amount;
        }
    }
    
    
    public  int getChipStack(int seat) {
        return  chipStacks[seat];
    }
    
    public  void setChipStack(int seat, int value) {
        chipStacks[seat]   =   value;
    }
   
    public  boolean missingHoleCards(int index) {
        return   holeCards[index] == null;    
    }
    
    public  Card    holeCard0(int index) {
        
        if ( holeCards[index] != null )
            return  new Card(holeCards[index].cardNumber(0));
        
        return null;
    }
    
    public  Card    holeCard1(int index) {
        
        if ( holeCards[index] != null )
            return  new Card(holeCards[index].cardNumber(1));
        
        return null;
    }
    
    public  void showHoleCards(int index) {
        Session.logMessageLine("showHoleCards HC0:" + holeCards[index].cardNumber(0).getSymbol());
        Session.logMessageLine("showHoleCards HC1:" + holeCards[index].cardNumber(1).getSymbol());
    }
    
    public  void    addHoleCards(int index, Card hc0, Card hc1) {
        
        if ( holeCards[index] == null )
            holeCards[index]    =   new CardList();
        
        if ( Session.logLevelSet(Session.logType.GAME) )
            Session.logMessageLine("Game:\tAdd Hole Cards: Index[" + index + "] " + hc0.getSymbol() + " " + hc1.getSymbol() );
        
        holeCards[index].addCards(hc0, hc1);    
    }
    
    public  Card    boardCard(int i) {
        if ( (i<0) || (i >= boardCards.size()) )
            return null;
        
        return  new Card( boardCards.cardNumber(i));
    }
    
    
    public  void    addBoardCard(Card c) {
        if ( Session.logLevelSet(Session.logType.GAME) )
            Session.logMessageLine("Game:addBoardCard\t" + 
                    ((boardCards.size() == 3) ? "TURN: " : "RIVER: ") + c.getSymbol());
        boardCards.addCard(c);
    }
    
    public  void    addBoardCards(Card c1, Card c2, Card c3) {
        if ( Session.logLevelSet(Session.logType.GAME) )
            Session.logMessageLine("Game:addBoardCards\tFLOP: " + 
                c1.getSymbol() + " " +  c2.getSymbol() + " " +  c3.getSymbol() );
                
        boardCards.addCards(c1, c2, c3);
    }
    
    
    final boolean addPlayerNameForIndex(int seat, int index, BufferedImage b) {
        try {
            Player          player      =   table().readPlayer(index, b);
            
            seatedPlayers[index]        =   player;
            
            player.addGamePlayed(this);
            return true;
        }
        catch ( CantReadNameException | SeatXYException e ) {
            return false;
        }
    }
    
    final   void addUnknownPlayer(int seat, int index ) {
        Player      player      =   new Player("Unknown");
    
        seatedPlayers[index]    =   player;
    }
    
    final   void    setPlayerName(int index, String name) {
        seatedPlayers[index].setScreenName(name);
    }
    
    final   boolean addPlayer(int seat, int index, BufferedImage b) {
        String prefix =  this.getClass().getSimpleName() + ":addPlayer";
        
        try {
            Player          player      =   table().readPlayer(seat, b);
            
            seatedPlayers[index]        =   player;
            
            player.addGamePlayed(this);
            
        }
        catch ( CantReadNameException | SeatXYException e ) {
            return false;
        }
        
        try {
            chipStacks[index]           =   table().readShieldChipStack(seat, b);
        }
        catch ( UnableToReadNumberException e ) {
            if ( Session.logLevelSet(Session.logType.GAME)  ) {
                Session.logMessageLine(prefix + "\tSEAT[" + seat + "] STACK " + chipStacks[index] );
            }
        }
        
        return true;
    }
    
    
    
    // on startup locates all the players seats, is supposed to 
    // recognize the number of seats at the tabel = {2,4,6,9}
    // but for know we only recognize 
    public Game(ReplayPokerTable table) {
        // startX, startY are the leftEdge and topEdge of the player's getCardList
        int i;
        
        if ( Session.logLevelSet(Session.logType.GAME) )
            Session.logMessageLine("Game(CTOR):\tAt Table: " + table.getName() );
        
        this.table              = table;
               
        finalPot                = 0;
        handNumber              = "";
        bigBlindBet             = 0;
    
        
        hhWriter                =   new ReplayHistoryWriter(handHistoryFile + Calendar.getInstance().getTime().toString() + ".txt");
        
        bcWriter                =   new ReplayHistoryWriter(boardCardHistoryFile);
        
        if ( Session.logLevelSet(Session.logType.INFO)  )
            Session.logMessageLine(this.getClass().getSimpleName() + ":CTOR, Num Seats: " + this.table.numSeats());
        
        seatedPlayers           = new Player        [this.table.numSeats()];
        chipStacks              = new int           [this.table.numSeats()];
        chipsWon                = new int           [this.table.numSeats()];
        playerActions           = new PlayerActions [this.table.numSeats()];
        holeCards               = new CardList      [this.table.numSeats()];
        
        // the table is understood as well as it's going to be
        // now read the players
        for (i=0; i < this.table.numSeats(); i++ ) {
            playerActions[i]    =   new PlayerActions();
            holeCards[i]        =   null;
            chipsWon[i]         =   0;            
        }
    }
    
}


