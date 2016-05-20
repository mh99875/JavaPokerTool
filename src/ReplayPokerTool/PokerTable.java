/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Abstract Base class for ReplayPokerTable
 * 
 * generalizable to a poker screen from any online poker site with the 
 * modification of hasGoldShield() to something more generic like isLivelayer()<br>
 * 
 * 
 *
 * @author marcushaupt
 * @version 1.0
 * 
 */
public abstract class PokerTable {
    
    /**
     * The number of seats, pots and their location is up the the implementing
     * table
     */
    
    private     final           String              tableName;
    protected                   SeatLocation []     seats; 
    protected                   Point []            potLocation;    
    protected                   int                 numSeats;
    protected                   int                 userSeat        =   -1;
    protected                   ArrayList<String>   shieldNames     =   null;             
    private     final           PlayerNamesReader   pnr;
    private     final           PokerWindow         pw;
    private     final           String              className       =   this.getClass().getSimpleName();
    
    
    /**
     * Abstract functions to be implemented in base classes
     * 
     * @param s seat number
     * @param b BufferedImage
     * @return 
     */
    
    abstract    boolean         foldedHand(int s, BufferedImage b);
    abstract    boolean         hasButton(int s, BufferedImage b);
    abstract    boolean         hasCardLeftEdge(int s, BufferedImage b);
    abstract    boolean         hasCardsShowing(int s, BufferedImage b);
    abstract    boolean         hasClock(int s, BufferedImage b);   
    abstract    boolean         hasGoldShield(int s, BufferedImage b);
    abstract    boolean         holeCardsAreLive(int s, BufferedImage b );
    abstract    boolean         isSeatedUsingColor(int s, BufferedImage b);
    abstract    boolean         isSittingOut(int s, BufferedImage b);
    abstract    boolean         userCardsMissing(int userSeat, BufferedImage b);
    abstract    boolean         nameObstructed(int s, BufferedImage b);
    abstract    int             numPlayersInHand();
    abstract    int             numPlayersWithLiveCards();
    abstract    boolean         playerShowingCards(int s, BufferedImage b);
    abstract    void            readAllPlayerNames(BufferedImage b);
    abstract    Game.ActionType readShieldAction(int s, BufferedImage b);
    abstract    int             seatX(int s);
    abstract    int             seatY(int s);
    abstract    int             setTableSeatPositions(BufferedImage b);
    abstract    boolean         shieldInTransition(int s, BufferedImage b);
    
   
    /**
     * Abstract functions that throw exceptions
     * 
     * @param b BufferedImage
     * @throws AnchorNotFoundException 
     */
    abstract    void            findSeats(BufferedImage b)
                                throws AnchorNotFoundException;
    
    abstract    String          readHandNumber(BufferedImage b)
                                throws UnableToReadCharException, AnchorNotFoundException;
    
    abstract    boolean         potsMissingFromTable(BufferedImage b)
                                throws AnchorNotFoundException;
    
    abstract    int             potValue(BufferedImage b, Game.Pot pot) 
                                throws UnableToReadNumberException, AnchorNotFoundException;
    
    abstract    int             rake(BufferedImage b) 
                                throws UnableToReadNumberException, AnchorNotFoundException;
    
    abstract    int             readAllSplitPots(BufferedImage b) 
                                throws UnableToReadNumberException, AnchorNotFoundException;
    
    abstract    Player          readPlayer(int p, BufferedImage b) 
                                throws CantReadNameException, SeatXYException;
    
    abstract    String          readShieldName(int p, BufferedImage b) 
                                throws CantReadNameException, SeatXYException;
    
    abstract    int             readPlayerBet(int p, BufferedImage b)
                                throws UnableToReadNumberException;
    
    abstract    int             readShieldChipStack(int s, BufferedImage b) 
                                throws UnableToReadNumberException;
    
    abstract    int             readTableNumber(int x, int y, BufferedImage b)
                                throws  UnableToReadNumberException;
  
    
    /**
     * Create a new PokerTable from a ReplayPokerWindow<br>
     * 
     * Need to change to a PokerWindow to make this class able to handle 
     * poker tables from any site<br>
     * 
     * @param pw PokerWindow
     */
    PokerTable(PokerWindow pw) {
        tableName       =   pw.windowName();
        pnr             =   new PlayerNamesReader(this, false);
        this.pw         =   pw;
    }
    
    public String  getName() {
        return  tableName;
    }
    
    /**
     * make generic PokerWindow
     * 
     * @return ReplayPokerWindow
     */
    public PokerWindow pw() {
        return  pw;
    }
    
    public PlayerNamesReader   pnr() {
        return  pnr;
    }
    
    public String  className() {
        return  className;
    }
    
    public int numSeats() {
        return  numSeats;
    }
    
    public int userSeat()  {   
        return userSeat; 
    }
    
    public void setUserSeat(int s) {
        userSeat    =   s;
    }
    
    public  String  playerAtSeat(int s) {
        if ( (s>=0) && ( s<=numSeats()-1) && (shieldNames != null) )
            return ( shieldNames.get(s) );
        else
            return  null;
    }
}
