/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 
 * <p>subclass of PokerTable
 * this is done as a thought towards using the tool to read data from other
 * poker sites>/p>
 * 
 * <p>the primary function of the table is to know where things are on the screen,
 * that is which pixels ranges represent which data features that are needed to
 * read to game.</p>
 * 
 * <p><b>CTOR:ReplayPokerTable(PokerTableScreen pw)</b>
 * throws NoSeatsFoundException, AnchorNotFoundException</p>
 * 
 * <p>tries to determine how many seats are at the table, which ones are currently
 * occupied</p>
 * 
 * <p>uses the Anchor to find seats, creating a ShieldAnchor which is turn is used
 * to find player names, stack sizes and bets</p>
 * 
 * <p>if it can't find the ShieldAnchors throws NoSeatsFoundException</p>
 * 
 * <p>if the ScreenAnchor is invalid throws AnchorNotFoundException</p>
 * 
 * <p>Does not read player names, an expensive function, nor player stacks (a 
 * somewhat cheaper function)</p>
 * 
 * <p>Hold the location of the pots, players and bets for functions that need to
 * read data from the screen.</p>
 * 
 * <p>Holds the table utility functions such as</p>
 * 
 * <ul>
 * <li>who has the clock</li>
 * <li>who has the button</li>
 * <li>who has live cards</li>
 * <li>who's cards are exposed</li>
 * <li>what's the player's bet</li>
 * <li>what's the current pot</li>
 * <li>what's the rake</li>
 * <li>is a player seated or sitting out this hand</li>
 * </ul>
 * 
 
 <p>subclass of PokerTable
 this is done as a thought towards using the tool to read data from other
 poker sites</p>
 
 <p>the primary function of the table is to know where things are on the screen,
 that is which pixels ranges represent which data features that are needed to
 read to game.</p>
 
 <p> CTOR: <b>ReplayPokerTable(ReplayPokerWindow pw)</b>
 throws NoSeatsFoundException, AnchorNotFoundException</p>
 
 <p>tries to determine how many seats are at the table, which ones are currently
 occupied</p>
 
 <p>uses the Anchor to find seats, creating a ShieldAnchor which is turn is used
 to find player names, stack sizes and bets</p> 
 
 <p>if it can't find the ShieldAnchors throws NoSeatsFoundException</p>
 
 <p>if the ScreenAnchor is invalid throws AnchorNotFoundException</p>
 
 <p>Does not read player names, an expensive function, nor player stacks (a 
 somewhat cheaper function)</p>
 
 <p>Hold the location of the pots, players and bets for functions that need to
 read data from the screen.<p>
 
 * @author  Marcus Haupt
 * @version 1.0
 * 
 */

public class ReplayPokerTable extends PokerTable {
    
    public ReplayPokerTable(ReplayPokerWindow rpw) 
    throws  NoSeatsFoundException, AnchorNotFoundException
    {
        super(rpw);
        
        int i;
        int x, y;
        int rd,bl;
        
        String              prefix  =   "ReplayPokerTable:CTOR";
        
        BufferedImage       b       =   CardUtilities.getCurrentBoard();
        
        if ( ! rpw.anchorIsValid() )
            throw   new AnchorNotFoundException(prefix + "\tError. Anchor Invalid.");
        
        Point               anchor  =   rpw.getWindowAnchorCopy();
        
        
        if ( Session.logLevelSet(Session.logType.INFO) )
            Session.logMessageLine(prefix + "\tPTS Valid Anchor: (" + anchor.x + "," + anchor.y + ")");
        
        
        potLocation             = new Point[9];
        for (i=0;i < 9;i++) {
            potLocation[i]      = new Point();
        }
               
        // main below 
        potLocation[0].x           =   anchor.x    +   262;
        potLocation[0].y           =   anchor.y    +   225;
        
        // above right
        potLocation[1].x           =   anchor.x    +   388;
        potLocation[1].y           =   anchor.y    +   110;
        
        // first split below
        potLocation[2].x           =   anchor.x    +   181;
        potLocation[2].y           =   anchor.y    +   225;
        
        // second split below
        potLocation[3].x           =   anchor.x    +   105;
        potLocation[3].y           =   anchor.y    +   207;
        
        // above before flop
        potLocation[4].x           =   anchor.x    +   268;
        potLocation[4].y           =   anchor.y    +   111;
        
        // third split
        potLocation[5].x           =   anchor.x    +   105;
        potLocation[5].y           =   anchor.y    +   206;
       
        // fourth split
        potLocation[6].x           =   anchor.x    +   105;
        potLocation[6].y           =   anchor.y    +   248;
        
        // Above Pre-Flop
        potLocation[7].x           =   anchor.x    +   267;
        potLocation[7].y           =   anchor.y    +   111;
        
        // Rake
        potLocation[8].x           =   anchor.x    +   261;
        potLocation[8].y           =   anchor.y    +   99;
                
        // here we would check to see how many seats
        // and set the seats that nobody can sit at to (x,y) = 0;
        
        // bootstrap load the number of seats and their locations by
        // trying to find the seat [2] (the third seat starting clockwise
        // at the upper left hand side
        //seats_[2].x             = 490;
        //seats_[2].y             = 80;
        // if it'p there, we have a 9-handed table, else 6
        // haven't tested on 2 and 4-handed yet
                
        int wood    =   0;
        for (x=anchor.x+500;x<anchor.x+550; x++) {
            for (y=anchor.y+70;y<anchor.y + 100; y++) {
                rd   =   CardUtilities.pixelRedValue(b.getRGB(x,y));
                bl   =   CardUtilities.pixelBlueValue(b.getRGB(x,y));
                
                if ( (rd>50) && (rd<100) ) {
                    if ( bl < 25 )
                        wood++;
                }
            }
        }
        
        
        if ( Session.logLevelSet(Session.logType.INFO) )
            Session.logMessageLine(prefix + "\tWood (Rail/Shield) at Seat[2] : " + wood );
        
        
        if ( wood > 200  ) {
        
            if ( Session.logLevelSet(Session.logType.INFO) )
                Session.logMessageLine(prefix + "\t6-Handed Table");
            
            numSeats   = 6; 
            
        } else {
             // and 4-seated, disambiguate by looking at seat position 3
            
            wood = 0;
            for (x=anchor.x + 530;x<anchor.x + 580; x++) {
                for (y=anchor.y + 175;y<anchor.y + 225; y++) {
                    rd   =   CardUtilities.pixelRedValue(b.getRGB(x,y));
                    bl   =   CardUtilities.pixelBlueValue(b.getRGB(x,y));
                
                    if ( (rd>50) && (rd<100) ) {
                        if ( bl < 25 )
                            wood++;
                    }
                }
            }
            
            if ( Session.logLevelSet(Session.logType.INFO) )
                Session.logMessageLine(prefix + "\tWood (Rail/Shield) at Seat[3] : " + wood );
            
            if ( wood > 200 ) {
                numSeats   = 4;
                if ( Session.logLevelSet(Session.logType.INFO) )
                    Session.logMessageLine(prefix + "\t4-Handed Table");
            } else {
                numSeats   = 9;
                if ( Session.logLevelSet(Session.logType.INFO) )
                    Session.logMessageLine(prefix + "\t9-Handed Table");
            }
        }
        
        seats          =       new SeatLocation[numSeats];
        
        if ( Session.logLevelSet(Session.logType.INFO) )
            Session.logMessageLine(prefix + "\tNum seats Table: " + numSeats);
        
        try {
            for (i=0; i<numSeats; i++)
                seats[i]        =   new SeatLocation(i, numSeats);
        }
        catch ( InvalidSeatLocationException e ) {
            
        }
        
        // the seat positions are approximate and will be set
        // when the shield is found in findSeats()
        // the betOffset is from the found seat location
        // finding the upper-right-hand-corner of the chips
        // setting params for 9-handed, will change in findSeats 
        // below if found
        // to be a 6-seater
        
        
        int seatsFound  =   0;
        // based on where we think they shields might be
        // find the exact location of each shield
        for (i=0;i<numSeats;i++) {
                     
            // move across until we find the black/gold border
            findXandY:
            for (x=(seats[i].shieldX() + anchor.x);x<(seats[i].shieldX()+anchor.x+50);x++) {    
                for ( y=seats[i].shieldY()+anchor.y; y < seats[i].shieldY()+anchor.y+30; y++) {
                
                    if ( atGoldShieldEdge(b,x,y) || atBrightGoldShieldEdge(b,x,y) || atGreyShieldEdge(b,x,y) ) {
                        seats[i].setShieldLocations(x+1, y);

                        if ( Session.logLevelSet(Session.logType.INFO) )
                            Session.logMessageLine(prefix + "\tSeat[" + i + "] Found @ ("
                                                + seats[i].shieldX() + "," + seats[i].shieldY() + ")" );
                       
                        seatsFound++;
                        break   findXandY;
                       
                    }
                } // end for Y
            } // end for X
                                              
        } // end for every seat at the table
        
        // minimum number of seats at a poker table
        if ( seatsFound < 2 ) 
            throw   new NoSeatsFoundException();
    }
    
    
    public final  boolean  atGoldShieldEdge(BufferedImage b, int x, int y ) {
        
        return (   CardUtilities.isBlackPixel(b.getRGB(x,y))              
                && CardUtilities.isGoldShieldPixel(b.getRGB(x+1,y))  
                && CardUtilities.isGoldShieldPixel(b.getRGB(x+2,y))       
                && CardUtilities.AllRGBBelowValue(100, b.getRGB(x-1,y-1)) 
                && CardUtilities.AllRGBBelowValue(100, b.getRGB(x,y+1))   
                && CardUtilities.AllRGBBelowValue(100, b.getRGB(x,y+2))
                && CardUtilities.AllRGBBelowValue(100, b.getRGB(x+1,y+2)) 
                && CardUtilities.AllRGBBelowValue(100, b.getRGB(x,y+3))
                );
    }
    
    
     public final  boolean  atBrightGoldShieldEdge(BufferedImage b, int x, int y ) {
        
        return (   CardUtilities.AllRGBBelowValue(75, b.getRGB(x-1,y-1))  &&  CardUtilities.isBrightGoldShieldPixel(b.getRGB(x,y-1)) 
                && CardUtilities.AllRGBBelowValue(75, b.getRGB(x-1,y-2))  &&  CardUtilities.isBrightGoldShieldPixel(b.getRGB(x,y-2)) 
                && CardUtilities.AllRGBBelowValue(75, b.getRGB(x-1,y-3))  &&  CardUtilities.isBrightGoldShieldPixel(b.getRGB(x,y-3)) 
                && CardUtilities.AllRGBBelowValue(75, b.getRGB(x-1,y-4))  &&  CardUtilities.isBrightGoldShieldPixel(b.getRGB(x,y-4)) 
                
                );
    }
    
    public final  boolean  atGreyShieldEdge(BufferedImage b, int x, int y ) {
        
        int grey = 0;
        int pixel;
        
        int r,g;
        
        for (int rw=0; rw<4; rw++) {
            for (int c=3; c<28; c++) {
                pixel   =   b.getRGB(x+c,y-rw);
                
                r = CardUtilities.pixelRedValue(pixel);
                g = CardUtilities.pixelGreenValue(pixel);
                
                if ( (r>100) && (g>100) && (r<200) && (g<200) )
                    grey++;
            }
        }
                
        if ( grey < 100 )
            return false;
                
        return (    CardUtilities.isBlackPixel(b.getRGB(x,y)) 
                && !CardUtilities.isBlackPixel(b.getRGB(x,y+3))  
                && !CardUtilities.isBlackPixel(b.getRGB(x,y+4))  
                &&  CardUtilities.AllRGBBelowValue(100, b.getRGB(x-1,y-1)) 
                &&  CardUtilities.AllRGBBelowValue(100, b.getRGB(x,y+1))   
                &&  CardUtilities.AllRGBBelowValue(100, b.getRGB(x,y+2))
                &&  CardUtilities.AllRGBBelowValue(100, b.getRGB(x+1,y+2)) 
                &&  CardUtilities.AllRGBBelowValue(100, b.getRGB(x,y+3))
                );
    }
    
    
    @Override
    public  int    seatX(int s) 
    {
         if ( (s < 0) || (s>=numSeats()) )
             return 0;
         
        return seats[s].shieldX();
    }
    
    @Override
    public  int    seatY(int s) {
        if ( (s < 0) || (s>=numSeats()) )
             return 0;
         
        return  seats[s].shieldY();
    }
    
    
    public  int     betX(int s) {
        if ( (s < 0) || (s>=numSeats()) )
             return 0;
         
        return  seats[s].betX();
    }
    
     public  int     betY(int s) {
        if ( (s < 0) || (s>=numSeats()) )
             return 0;
         
        return  seats[s].betY();
    }
     
     
     public int buttonX(int s) {
          if ( (s < 0) || (s>=numSeats()) )
             return 0;
         
        return  seats[s].buttonX();
     }
    
     
     public int buttonY(int s) {
          if ( (s < 0) || (s>=numSeats()) )
             return 0;
         
        return  seats[s].buttonY();
     }
     
   
    @Override
    public int setTableSeatPositions(BufferedImage b)  {   
        return 0;
    }
    
    /**
     * @deprecated 
     * 
     * if we don't find a perfect match, find the closest
       match that we can. The userName's name could be hidden by the 
       clock so make sure all shield are checked before using the value from
       this function
       * 
       * DEPRECATED: GameManager finds the user via exposed hole card during
       * normal game play (e.g. not All-in or End-of-Hand)
     * 
     * @param username read from command line or properties file
     * 
     * @return int
     * 
     *
    */ 

    public int  closestMatchToUser(String username) {
        
        // is every active player'p name in the array?
        int livePlayers = 0;
        int namesKnown = 0;
                
        BufferedImage   b   =   CardUtilities.getCurrentBoard();
        
        // remember the player list is only the known players and not the
        // seat they are in as they userName can rotate seats as will
        
        try {
            int p;
            
            for (p=0; p<numSeats(); p++ ) {
                if ( !isSittingOut(p, b) ) {
                livePlayers++;
                }
                if ( shieldNames != null )
                    namesKnown++;
                else {
                    // try to read the player name
                    shieldNames.set(p, readShieldName(p,b) );
                    namesKnown++;
                }
            }
            
            if ( namesKnown != livePlayers )
                return -1;
            
            // which one is the closest?
            // rank them and pick the best
            ArrayList<Integer> matches = new ArrayList<>();
            
            int match;
            int idx;
            int prev;
            
            for (p=0; p<numSeats(); p++) {                
                match   =   0;
                prev    =   -1;
                
                // for each letter in the username see how many
                // chars are in the player name, in the same relative order
                for (int i=0; i<numSeats(); i++) {
                    if ( !isSittingOut(i,b) ) {
                        for (int j=0; j<username.length(); j++) {
                            idx =   shieldNames.get(i).indexOf(username.charAt(j), prev+1);
                            if ( idx > 0 ) {
                                prev    =    idx;
                                match++;
                            }
                        }
                    }
                        
                    matches.add(match);
                }
            }
            
            return Collections.max(matches);
            
            // if two have the same "best match" 
            // we could choose the shorter or longer one...
            
        }
        catch (  CantReadNameException | SeatXYException e) {
            
        }
        
        return -1;
    }
  
    
    @Override
    public   void    readAllPlayerNames(BufferedImage b)  {
        
        String  prefix  =   this.getClass().getSimpleName() + ":readAllPlayerNames";
        
        shieldNames     =   new ArrayList<>();
                
        for (int s=0; s<numSeats(); s++) {
        
            if ( !isSittingOut(s,b) ) {
                try {                
                    shieldNames.add(pnr().readShieldName(s, b));    
                
                    if ( Session.userName() != null ) {
                        if ( shieldNames.get(s).equals(Session.userName()) )
                            userSeat    =   s;
                    }
                }
                catch ( CantReadNameException | SeatXYException e ) {
                    shieldNames.add("Unknown");
                }
            }
            else {
                // the "* is an illegal char for Replay Names
                shieldNames.add("*Sitting Out*");
            }
        }
    }
    
    
    /**
     * 
     * @param s seat number with 0 in the upper left hand corner of the table
     * @param b BufferedImage
     * @return Player object
     * @throws CantReadNameException
     * @throws SeatXYException 
     */
    @Override
    Player readPlayer(int s, BufferedImage b) 
    throws CantReadNameException, SeatXYException
    {
        String prefix = this.getClass().getSimpleName() + ":readPlayer";
        
        // read the player's name from the shield
        // see if we already know the player by name
        
        try {
            String      pName       =   pnr().readShieldName(s, b);
            Player      player      =   SessionFullGame.findOrAddPlayer(pName);
            return player;

        }
        catch ( SeatXYException e ) {
            if ( Session.logLevelSet(Session.logType.ERROR) ) {
                Session.logMessageLine(prefix + "\tSeat Location Error.");
                Session.logMessageLine( e.getMessage() );
            }
        }
        
        return null;
    }
    
    
    /**
     * 
     * @param s seat number with 0 in the upper left hand corner of the table
     * @param b Buffered Image
     * @return String screen name
     * @throws CantReadNameException
     * @throws SeatXYException 
     * 
     * Read the shield name via PlayerNameReader.readShieldName
     */
    @Override
    String readShieldName(int s, BufferedImage b) 
    throws CantReadNameException, SeatXYException
    {
        String prefix = this.getClass().getSimpleName() + ":readPlayer";
        
        // read the player's name from the shield
        // see if we already know the player by name
       
        return pnr().readShieldName(s, b);
        
    }
    
    
    
    @Override
    void findSeats(BufferedImage b) 
    throws  AnchorNotFoundException
    {
        int     s,x,y;
        
        String  prefix  =   this.getClass().getSimpleName() + ":findSeatedPlayers";
        boolean foundShield;
        
        try {
            Robot robot = new Robot(); 
            robot.delay(1000);
        }
        catch ( AWTException e ) {
            Session.logMessageLine(prefix + "\tRobot Error: " + e.toString());
        }
       
        Point   anchor              =   pw().getWindowAnchorCopy();
        
        if ( Session.logLevelSet(Session.logType.INFO) )
            Session.logMessageLine(prefix + "\tANCHOR: (" + anchor.x + "," + anchor.y + ")");
        
        
        for (s=0;s<numSeats();s++) {
                               
            foundShield =   false;
            findShield:
            // move across until we find the black/gold border
            for (x=(seatX(s) + anchor.x);x<(seatX(s)+anchor.x+50);x++) {    
                for ( y=seatY(s)+anchor.y; y < seatY(s)+anchor.y+30; y++) {
                
                    if (   CardUtilities.isBlackPixel(b.getRGB(x,y))              && CardUtilities.isGoldShieldPixel(b.getRGB(x+1,y))  
                        && CardUtilities.isGoldShieldPixel(b.getRGB(x+2,y))       && CardUtilities.AllRGBBelowValue(100, b.getRGB(x-1,y-1)) 
                        && CardUtilities.AllRGBBelowValue(100, b.getRGB(x,y+1))   && CardUtilities.AllRGBBelowValue(100, b.getRGB(x,y+2))
                        && CardUtilities.AllRGBBelowValue(100, b.getRGB(x+1,y+2)) && CardUtilities.AllRGBBelowValue(100, b.getRGB(x,y+3)) ) 
                    {

                        seats[s].setShieldLocations(x+1, y); 
                        
                        if ( Session.logLevelSet(Session.logType.INFO) )
                            Session.logMessageLine(prefix + "\tSeat[" + s + "] Found @ ("
                                                + seats[s].shieldX() + "," + seats[s].shieldY() + ")" );
                       
                        foundShield =   true;
                       
                    }
                    if ( foundShield )
                        break findShield;
                } // end for Y
                
                if ( foundShield )
                    break;
            } // end for X
                                              
        } // end for every seat at the table
         
    }   // end function find seated players
   
    
    @Override
    public boolean  foldedHand(int s, BufferedImage b) {
        
        if ( s == userSeat() )
            return hasCardLeftEdge(s, b);
        
        boolean folded  =   false;
        String  prefix  =   this.getClass().getSimpleName() + ":foldedHand";
        
        
        
        int y       =  seatY(s) - 30;
        int x       =  seatX(s) + 1;
        int value   =   0;
        
        for (int i=0; i<20; i++)
            for (int j=0; j<10; j++) {
                if ( s == userSeat() ) {
                    if ( CardUtilities.isWhitePixel(b.getRGB(x+i,y-j)) ) 
                        value++;
                } else {
                    if ( CardUtilities.isRedPixel(b.getRGB(x+i,y-j)) ) 
                        value++;   
                }
            }
        
        
        if ( value < 20 )
            folded  =   true;
        
        if ( Session.logLevelSet(Session.logType.INFO) )
            Session.logMessageLine(prefix + "\t(" + x + "," + y + ") Value: " + value );
        
        return folded;
    }
    
    
    
    @Override
    public  int numPlayersInHand() {
        int n   = 0;
        
        boolean debug =   Session.logLevelSet(Session.logType.INFO);
        
        String  prefix  = this.getClass().getSimpleName() + ":numPlayersInHand";
        
        BufferedImage b =   CardUtilities.getCurrentBoard();
        
        for (int s=0; s<numSeats(); s++ ) {
            if ( hasCardLeftEdge(s, b)  ) {
                n++;
                if ( debug ) Session.logMessageLine(prefix + "\tIN HAND: " + n + "\tSeat[" + s + "]" );
            }
        }
        return n;
    }
    
    
    @Override
    public  int numPlayersWithLiveCards() {
        int n   =   0;
        String  prefix  = this.getClass().getSimpleName() + ":numPlayersWithLiveCards";
        
        BufferedImage b =   CardUtilities.getCurrentBoard();
        
        for (int s=0; s<numSeats(); s++ ) {
            if ( hasCardLeftEdge(s, b) ) {
                n++;
            }
        }
        
        return n;
    }
    
    
    
    @Override
    public boolean playerShowingCards(int s, BufferedImage b) {
        return hasCardsShowing(s,b);
    }
    
    public  boolean holeCardsAreFolded(int s) {
        boolean debug           =   Session.logLevelSet(Session.logType.INFO);
        int     vertical;
        
        String  prefix          =   this.getClass().getSimpleName() + ":userHoleCardsAreFolded[" + s +"]";
        
        BufferedImage   b       =   CardUtilities.getCurrentBoard();
        
        if ( (seatX(s) < 0) || (seatY(s)<0) || isSittingOut(s,b) || hasCardLeftEdge(s,b) )
            return false;
        
        int pixel;
        int n = 100;
        
        
        checking:
        for (int i=-2; i<10; i++) {
            vertical=0;
            for (int j=40; j<50; j++) {
                pixel   =   b.getRGB(seatX(s)+i, seatY(s)-j);
                
                if ( debug )
                    Session.logMessageLine(prefix + "\t[" + i + "][" + j + "]: " +
                        CardUtilities.pixelRGBValues(pixel) );
                
                if ( CardUtilities.AllRGBAboveValue(100, pixel) && CardUtilities.AllRGBBelowValue(200, pixel ) ) {
                    vertical++;
                }
            }
                
            if ( debug )
                Session.logMessageLine(prefix + "\tVertical[" + i + "] = " + vertical);
            
            if ( vertical >= 10 )  {
                n   =   0;
                
                for (int x =seatX(s)-5; x < seatX(s) + 20; x++ ) {
                    for (int y = seatY(s)-60; y < seatY(s)-40; y++ ) {
                        if ( CardUtilities.AllRGBBelowValue(75, b.getRGB(x,y)) )
                            n++;
                    }
                }

                if ( debug )
                    Session.logMessageLine(prefix + "\tN: " + n);
                break;
            }
        }
                
        if ( debug )
            Session.logMessageLine(prefix + "\tFinal n: " + n);
        
        return  n == 0;        
    }
    
    
    @Override
    public  boolean holeCardsAreLive(int s, BufferedImage b ) {
        
        boolean debug   =   Session.logLevelSet(Session.logType.INFO);
        String  prefix  =   this.getClass().getSimpleName() + ":holeCardsAreLive[" + s + "]";
        
        int     x       =   seatX(s);
        int     y       =   seatY(s);
                
        if ( (x <=0) || (y<=0) || (b==null) || isSittingOut(s,b) )
            return false;
        
        return  hasCardLeftEdge(s,b);
    }
    
    
    public  boolean hasCardRightEdge(int s) {
        return  cardRightEdge(s) > 0;
    }
    
    
    @Override
    boolean  hasCardLeftEdge(int s, BufferedImage b) {
        return  cardLeftEdge(s) > 0;
    }
    
    
    boolean hasCardTopEdge(int s) {
        return  cardTopEdge(s) > 0;
    }
    
    public  int cardTopEdge(int s) {
        
        int             leftEdge    =   cardLeftEdge(s);
        
        if ( leftEdge == 0 )
            return 0;
        
        BufferedImage   b           =   CardUtilities.getCurrentBoard();
        int             horizontal;
        
        for (int y=seatY(s)-75; y > seatY(s)-80; y--) {
            if ( CardUtilities.isBlackPixel(b.getRGB(seatX(s)+5, y)) ) {   
                horizontal=0;
                for ( int x = seatX(s)+5; x < seatX(s)+75; x++) {
                    if ( CardUtilities.AllRGBBelowValue(200, b.getRGB(x,y)) )
                        horizontal++;
                }
                if ( horizontal >= 65 ) {
                    return y;
                }
            }
        }
        
        return  0;
    }
    
    
    public  int cardRightEdge(int s) {
        int leftEdge    =   cardLeftEdge(s);
        
        if ( leftEdge == 0 )
            return 0;
            
        BufferedImage   b           =   CardUtilities.getCurrentBoard();
        int             vertical;
        
        for (int x=leftEdge+75; x<leftEdge+80; x++) {
            if ( CardUtilities.isBlackPixel(b.getRGB(x, seatY(s)-40 )) ) {  
                vertical = 0;
                for ( int y = seatY(s)-40; y < seatY(s)-30; y++) {
                    if ( CardUtilities.isBlackPixel(b.getRGB(x, y)) ) {
                        vertical++;
                    }
                }
                if ( vertical == 10 ) {
                    return x;
                }
            }
        }
        
        return  0;
    }
    
    
    public  int cardLeftEdge(int s) {
        
        boolean         debug           =   Session.logLevelSet(Session.logType.INFO);
        int             vertical        =   0;
        int             horizontal      =   0;
        String          prefix          =   this.getClass().getSimpleName() + ":cardLeftEdge[" + s +"]";
        BufferedImage   b               =   CardUtilities.getCurrentBoard();
        
        int x   =   seatX(s)-5;
        int y   =   seatY(s)-30;
        
        // find the vertical black line
        int i,j, leftEdge=0;
        
        if ( (x < 0 ) || (y<0) ) {
            if ( debug )
                Session.logMessageLine(prefix + "\t(" + x + "," + y +") invalid location");
            return 0;
        }
        
        // try 5 columns
        for (i=0; i<10; i++) {
            vertical=0;
            for (j=0; j<30; j++) {
                if ( CardUtilities.isBlackPixel(b.getRGB(x+i, y-j)) ) {
                    vertical++;
                }
            }
            if ( vertical >= 30 ) {
                leftEdge    =   x+i;
                break;
            }
        }
        
        // look for horizontal
        if ( vertical >=30 ) {
            x   =   seatX(s);
            y   =   seatY(s)-75;
            
            for (i=0; i<10; i++) {
                for (j=0; j<40; j++) {
                    if ( CardUtilities.isBlackPixel(b.getRGB(x+j, y-i)) ) {
                        horizontal++;
                    }
                }
                if ( horizontal >= 30 )
                    break;
            }
        }
        
        if ( debug ) 
            Session.logMessageLine(prefix + "\tVertical:" + vertical + 
                    "\tHorizontal:" + horizontal + " T/F " + (horizontal >= 30) );
        
        if ( (horizontal >= 30) || (vertical >=30) )
            return  leftEdge;
        else
            return  0;
    }
    
    
    @Override
    public boolean userCardsMissing(int userSeat, BufferedImage b) {
        
        // there will be either a solid black vertical line for cards showing
        // or a solid grey
       
        if ( (seatX(userSeat) <=0) || (seatY(userSeat) <=0) ) {
            return true;
        }
                
        int y   =   seatY(userSeat) - 35;
        for (int x=seatX(userSeat)-3; x<seatX(userSeat)+4; x++) {
            
            if ( CardUtilities.AllRGBBelowValue(75, b.getRGB(x,y))      && 
                 CardUtilities.AllRGBBelowValue(75, b.getRGB(x,y-1))    &&
                 CardUtilities.AllRGBBelowValue(75, b.getRGB(x,y-2))    && 
                 CardUtilities.AllRGBBelowValue(75, b.getRGB(x,y-3))    &&
                 CardUtilities.AllRGBBelowValue(75, b.getRGB(x,y-4))    && 
                 CardUtilities.AllRGBBelowValue(75, b.getRGB(x,y-5))    &&
                 CardUtilities.AllRGBBelowValue(75, b.getRGB(x,y-6))    && 
                 CardUtilities.AllRGBBelowValue(75, b.getRGB(x,y-7))    &&
                 CardUtilities.AllRGBBelowValue(75, b.getRGB(x,y-8))    && 
                    
                 CardUtilities.AllRGBAboveValue(225, b.getRGB(x+1,y))      && 
                 CardUtilities.AllRGBAboveValue(225, b.getRGB(x+1,y-1))    &&
                 CardUtilities.AllRGBAboveValue(225, b.getRGB(x+1,y-2))    && 
                 CardUtilities.AllRGBAboveValue(225, b.getRGB(x+1,y-3))    &&
                 CardUtilities.AllRGBAboveValue(225, b.getRGB(x+1,y-4))    && 
                 CardUtilities.AllRGBAboveValue(225, b.getRGB(x+1,y-5))    &&
                 CardUtilities.AllRGBAboveValue(225, b.getRGB(x+1,y-6))    && 
                 CardUtilities.AllRGBAboveValue(225, b.getRGB(x+1,y-7))    &&
                 CardUtilities.AllRGBAboveValue(225, b.getRGB(x+1,y-8))  ) {
                
                    return true;
            }
        }
        
        
        for (int x=seatX(userSeat)-3; x<seatX(userSeat)+4; x++) {
            
            if ( CardUtilities.AllRGBWithinRange(125, 200, b.getRGB(x,y))   && 
                 CardUtilities.AllRGBWithinRange(125, 200, b.getRGB(x,y-1)) &&
                 CardUtilities.AllRGBWithinRange(125, 200, b.getRGB(x,y-2)) && 
                 CardUtilities.AllRGBWithinRange(125, 200, b.getRGB(x,y-3)) &&
                 CardUtilities.AllRGBWithinRange(125, 200, b.getRGB(x,y-4)) && 
                 CardUtilities.AllRGBWithinRange(125, 200, b.getRGB(x,y-5)) &&
                 CardUtilities.AllRGBWithinRange(125, 200,b.getRGB(x,y-6))  && 
                 CardUtilities.AllRGBWithinRange(125, 200, b.getRGB(x,y-7)) &&
                 CardUtilities.AllRGBWithinRange(125, 200, b.getRGB(x,y-8)) && 
                    
                 CardUtilities.AllRGBAboveValue(225, b.getRGB(x+1,y))      && 
                 CardUtilities.AllRGBAboveValue(225, b.getRGB(x+1,y-1))    &&
                 CardUtilities.AllRGBAboveValue(225, b.getRGB(x+1,y-2))    && 
                 CardUtilities.AllRGBAboveValue(225, b.getRGB(x+1,y-3))    &&
                 CardUtilities.AllRGBAboveValue(225, b.getRGB(x+1,y-4))    && 
                 CardUtilities.AllRGBAboveValue(225, b.getRGB(x+1,y-5))    &&
                 CardUtilities.AllRGBAboveValue(225, b.getRGB(x+1,y-6))    && 
                 CardUtilities.AllRGBAboveValue(225, b.getRGB(x+1,y-7))    &&
                 CardUtilities.AllRGBAboveValue(225, b.getRGB(x+1,y-8))  ) {
                
                    return true;
            }
        }
        
        return false;
        
    }
         
    
    @Override
    public boolean hasCardsShowing(int s, BufferedImage b) {
        
        int x,y;
        boolean showing     = false;
        String  prefix      = this.getClass().getSimpleName() + ":hasCardsShowing Seat[" + s + "]";
        int i, n, pixel, pixelA, white;
        boolean debug           =   Session.logLevelSet(Session.logType.INFO);
         
        if ( hasCardLeftEdge(s, b) ) {
            
            //look at the band from up -30 to up -50 Y
            // over 0 to over +20 X
                        
            if ( debug )
                Session.logMessageLine(prefix + "\tHas Card Edges");
            
            // for certain card combinations (like JJ) the edge may not
            // be black so we need a different test
            // check for the suit in
            // the left margin
           
            // first see if we have white where the left margin should be
            white =0;
            for (x=seatX(s)+5; x<seatX(s)+15; x++ ) {
                for (y=seatY(s)-35; y>seatY(s)-45; y--) {
                     pixel   = b.getRGB(x,y);
                     
                     if ( CardUtilities.isWhitePixel(pixel) ) 
                        white++;
                }
            }
            
            
            if ( white > 50 ) {
            
                if ( debug )
                    Session.logMessageLine(prefix + "\tSubstantial White in Margin: white = " + white);
                 
                findBorder:
                for (x=seatX(s); x<seatX(s)+20; x++ ) {
                    for (y=seatY(s)-20; y>seatY(s)-60; y--) {
                        // find and (red/black)-white border pixels indicating a suit
                        // red 
                        n = 0;
                        for (i=0; i<5; i++) {
                            pixel   = b.getRGB(x,y);
                            pixelA  = b.getRGB(x,y-1); // pixel above

                            if ( (CardUtilities.isBlackPixel(pixel) && CardUtilities.isBlackPixel(pixelA)) ||
                                 (CardUtilities.isRedPixel(pixel)   && CardUtilities.isRedPixel(pixelA)) ) {

                                n++;
                            }
                        }
                        if ( n >= 5 ) {
                             if ( debug )
                                Session.logMessageLine(prefix + 
                                        "\tFound two rows of black/red among White in Margin: white, showing =- true");
                             
                            showing     =   true;
                            break       findBorder;
                        }
                    }
                }
            }
            else if ( debug ) {
                if ( debug )
                    Session.logMessageLine(prefix + "\tNot Enough White in Margin: white = " + white);
            }
        }
        else {
              if ( debug )
                Session.logMessageLine(prefix + "\tDoes Not Have Card Edges");
        }
        
        if ( debug )
            Session.logMessageLine(prefix + "\tSeat[" + s + "]\tShowing: " + showing );
        
        return showing;
    }
    
    

    @Override
    public boolean  nameObstructed(int s, BufferedImage b) {
        int x,y;
        int wr   = 0;
        String  prefix  =    this.getClass().getSimpleName() + ":nameObstructed";
        
        for (x=seatX(s)+65;x<=(seatX(s)+80);x++)
            for (y=seatY(s)-12;y>(seatY(s)-22); y--) {
                if ( CardUtilities.isWhitePixel(b.getRGB(x,y)) || CardUtilities.isRedPixel(b.getRGB(x,y)) ) {
                    //Session.logMessageLine("NmObByClk White/Red at " + x + "," + y);
                    wr++;
                }
            }
             
        
        if ( wr > 50 )  {
            if ( Session.logLevelSet(Session.logType.INFO) )
                Session.logMessageLine(prefix + "\tSeat " + s + " White/Red: " + wr);
            return true;
        } else
            return false;
              
    }
    
    
    @Override
    public  boolean hasGoldShield(int s, BufferedImage b) {
        
        int x   = seatX(s);
        int y   = seatY(s);
        int n   = 0;
        
        if ( (x<0) || (y<0) )
            return false;
        
        for (int i=5; i<25; i++) {
            for (int j = 0; j<20; j++ ) {
                if ( CardUtilities.isGoldShieldPixel(b.getRGB(x+i, y-j)) )
                    n++;
            }
        }
        
       if ( Session.logLevelSet(Session.logType.INFO) )
            Session.logMessageLine(this.getClass().getSimpleName() + ":hasGoldShield: n = " + n);
        
        return n > 100;
    }
    
    
    @Override
    public  boolean shieldInTransition(int s, BufferedImage b) {
        String  prefix  =   this.getClass().getSimpleName() + ":shieldInTransition[" + s + "]";
        
        int x   =   seatX(s);
        int y   =   seatY(s);
        int p;
        int gold=0, black=0, other=0;
        boolean debug           =   Session.logLevelSet(Session.logType.INFO);
         
        if ( (x < 0) || (y<50) ) {
            Session.logMessageLine(prefix + "\tCan't Use (" + x + "," + y + ")");
            return false;
        }
        
        if ( b == null )
            return false;
        
        for (int i=0; i<= 75; i++) {
            for (int j = 0; j<=15; j++) {
                p   = b.getRGB(x+i, y-j);
                int     r   =    CardUtilities.pixelRedValue(p);
                if ( r > 125 )
                    gold++;
                else if ( r < 50 )
                    black++;
                else
                    other++;
            }
        }
        
        if ( debug )
            Session.logMessageLine(prefix + "\tGold: " + gold + " Black " + black + 
                            " Other: " + other);
        
        return other > black*5;
    }
    
    
    @Override
    public boolean   hasClock(int s, BufferedImage b) {
        int         x, y, brightGold = 0, i,j,p;
        boolean     found       = false;
        String      prefix      =   this.getClass().getSimpleName() + ":hasClockUsingColor[" + s + "]";
        boolean debug           =   Session.logLevelSet(Session.logType.INFO);
         
        if ( isSittingOut( s,  b) ) {
            return false;
        }
        
        x   =   seatX(s);
        y   =   seatY(s);
                
        if ( (x < 0) || (y<0) )
            return false;
        
        for (i=5; i<30; i++) {
            for (j=0; j<20; j++ ) { 
                p   = b.getRGB(x+i, y-j);
                if ( (CardUtilities.pixelRedValue(p) >= 225) && 
                     (CardUtilities.pixelGreenValue(p) >= 225 ) )
                    brightGold++;
            }
        }
        
        if ( debug ) {
            Session.logMessageLine(prefix + "\tGold:" + brightGold);
        }
        
        // the shield isn't always brightened to show who has the clock
        
        if ( brightGold > 25 )
            return true;
        else {
            int clockcolor  =   0;
            int rd, gr;
            
            
            // check for the white/red round symbol in the corner
            for (int r=10; r<20; r++) {
                for (int c=70; c<75; c++ ) { 
                    p   =   b.getRGB(x+c, y-r);
                    
                    rd  =   CardUtilities.pixelRedValue(p);
                    gr  =   CardUtilities.pixelGreenValue(p);
                    
                    if ( rd >= 100 ) {
                        if ( rd > 240 )
                            clockcolor++;
                        else if ( rd > gr * 2 )
                            clockcolor++;
                    } 
                }
            }
            
            if ( debug ) {
                Session.logMessageLine(prefix + "\tClockcolor:" + clockcolor);
        }
            
            return  clockcolor  > 10;
        }        
    }
    
    
    @Override
    public boolean   isSeatedUsingColor(int s, BufferedImage b) {
        int         x, y, gold = 0, i,j,p;
        boolean     found = false;
        String      prefix  =   this.getClass().getSimpleName() + ":isSeatedUsingColor[" + s + "]";
        boolean debug           =   Session.logLevelSet(Session.logType.INFO);
         
        if ( (s < 0) || (s >= numSeats()) )
            return false;
        
        x   =   seatX(s);
        y   =   seatY(s);
        
        if ( (x < 0) || (y<0) ) {
            if ( debug )
                
                Session.logMessageLine(prefix + "\t(" + x + "," + y + ") invalid ");
            
            return false;
        }
        
        
        for (i=5; i<70; i++) {
            for (j=0; j<20; j++ ) { 
                p   = b.getRGB(x+i, y-j);
                if ( (CardUtilities.pixelRedValue(p) >= 150)     &&
                     (CardUtilities.pixelGreenValue(p) >= 150)   &&
                     (CardUtilities.pixelBlueValue(p) < 100)  )
                    gold++;
            }
        }
        
        if ( debug ) {
            Session.logMessageLine(prefix + "\tGold:" + gold);
        }
        
        return gold > 100 ;
        
    }
    
     
     

    @Override
    public boolean  hasButton(int seat, BufferedImage b) {
        
         if ( (seat < 0) || (seat >= numSeats()) )
            return false;
        
        int         x, y, gold = 0;
        String      prefix  =   this.getClass().getSimpleName() + ":hasButton[" + seat + "]";
         
        
        for (x = buttonX(seat); x <buttonX(seat)+30; x++ ) {
            for ( y = buttonY(seat); y < buttonY(seat)+30; y++) {
                if ( CardUtilities.isGoldPixel(b.getRGB(x,y)) )
                            gold++;
            }
        }
        
        if ( Session.logLevelSet(Session.logType.INFO) ) {
            Session.logMessageLine(prefix + "\tSearching from (" + 
                    buttonX(seat) + "," + buttonY(seat) + ") to (" +
                (buttonX(seat)+30) + "," + (buttonY(seat)+30) + ")" );
            Session.logMessageLine(prefix + "\tGold = " + gold );
        }
        
        return gold > 175;
    }
    
    
    @Override
    public  int rake(BufferedImage b) 
    throws UnableToReadNumberException, AnchorNotFoundException
    {
        return  potValue(b, Game.Pot.RAKE);
    }
    
    
    @Override
    int potValue(BufferedImage b, Game.Pot pot) 
    throws UnableToReadNumberException, AnchorNotFoundException
    {
        
        int         i,  j,  pb;
        int         x,  y;
        int         pixel;
        
        int         potX        =   -1;
        int         potY        =   -1;
        int         chipY       =   -1;
        int         grY         =   -1;
        int         grX         =   -1;
        
        boolean debug           =   Session.logLevelSet(Session.logType.INFO);
        
        boolean     allGreen;
        
        String      prefix      =   "Unknown Pot Location";
        Point       a           =   pw().getWindowAnchorCopy();
        
        if ( a == null ) 
            throw new UnableToReadNumberException(prefix + "\tNull Anchor");
        
        
        // find the upper left hand corner of this number, if it exists
        // certain pots have chips above them, others don't
        
        switch ( pot  ) {
            
            case CALLED:
                prefix  =   className() + ":potValue CALLED BETS (below)";
                
                // first find the chips below the cards
                if ( debug ) {
                    Session.logMessageLine(prefix + 
                            "\tSearch for chips X: from " + (a.x+215) + " to " +
                            (a.x+275) );
                    
                     Session.logMessageLine(prefix + 
                            "\tSearch for chips Y: from " + (a.y+200) + " to " +
                            (a.x+220) );
                }
                    
                
                findChipY:
                for (x=a.x+215; x<a.x+275; x++ ) {
                    for (y=a.y+200; y < a.y+220; y++) {
                        pixel   =   b.getRGB(x,y);
                        if ( CardUtilities.pixelRedValue(pixel) > 150 ) {
                            chipY   =   y;
                            break findChipY;
                        }
                    }
                    if ( chipY > 0 )
                        break;
                }
                
                
                // then find the green below the chips
                if ( chipY > 0 ) {
                    if ( debug ) 
                        Session.logMessageLine(prefix + "\tFound Chips @ Y  = " + chipY);
                    
                    grY =   -1;
                    for ( y=chipY; y < chipY+40; y++) { 
                        allGreen    =   true;
                        for (x=a.x+200; x<a.x+275; x++ ) {
                            
                            pixel           =   b.getRGB(x,y);
                            
                            if ( (CardUtilities.pixelRedValue(pixel) > 110)  ||
                                 (CardUtilities.pixelRedValue(pixel) < 30) ) {
                                allGreen    =   false;
                                grY         =   y;
                                break;
                            } 
                        }
                        if ( allGreen ) {
                            if ( debug ) 
                                Session.logMessageLine(prefix + "\tFound Green @ Y  = " + grY);
                            
                            break;
                        }
                    }
                    
                    if ( grY > 0 ) {
                        if ( debug ) 
                            Session.logMessageLine(prefix + "\tLooking for Pot X");
                        
                        // find the fist column X with a white pixel
                        potX    =    potY   =   -1;
                        findPotX:
                        for (x = a.x+200; x < x+75; x++) {
                            for (y = grY; y < grY+10; y++) {
                                pixel   =   b.getRGB(x,y);
                                if ( CardUtilities.pixelRedValue(pixel) > 150 ) {
                                     if ( debug ) 
                                        Session.logMessageLine(prefix + 
                                                "\tFound potX:" + x);
                                    // this is our X
                                    potX    =   x;
                                    break findPotX;
                                }
                            }
                            if ( potX > 0 ) {
                                break;
                            }
                        }
                        
                        // find the first row Y with a white pixel
                        findPotY:
                        for ( y=grY; y<grY+10; y++) {
                            if ( debug ) 
                                Session.logMessageLine(prefix + "\tLooking for Pot Y");
                            
                            for (x = potX; x < potX+75; x++) { 
                                pixel   =   b.getRGB(x,y);
                                
                                if ( CardUtilities.pixelRedValue(pixel) > 110 ) {
                                    // this is our Y
                                    if ( debug ) 
                                        Session.logMessageLine(prefix + 
                                                "\tFound potY:" + y);
                                    
                                    potY    =   y;
                                    break   findPotY;
                                }
                            }
                            if ( potY > 0 )
                                break;
                        }
                            
                    }
                    
                }
                else {
                    if ( debug ) 
                        Session.logMessageLine(prefix + "\tNo Chips Found");
                    return 0;
                }                
                break;
            
                
                
            case    RAKE:
                prefix  =   this.getClass().getSimpleName() + "potValue RAKE (below)";
                
                // first find the chips below the add chips icon
                findRakeY: 
                for (x=a.x+200; x<a.x+300; x++ ) {
                    for (y=a.y+75; y < a.y+85; y++) {
                        pixel   =   b.getRGB(x,y);
                        if ( CardUtilities.pixelRedValue(pixel) > 150 ) {
                            chipY   =   y;
                            break findRakeY;
                        }
                    }
                    if ( chipY > 0 )
                        break;
                }
                
                // then find the green below the chips
                if ( chipY > 0 ) {
                    if ( debug ) 
                        Session.logMessageLine(prefix + "\tFound Chips @ Y  = " + chipY);
                    
                    for ( y=chipY; y < chipY+40; y++) { 
                        allGreen    =   true;
                        for (x=a.x+200; x<a.x+300; x++ ) {
                            
                            pixel           =   b.getRGB(x,y);
                            
                            if ( (CardUtilities.pixelRedValue(pixel) > 110)  ||
                                 (CardUtilities.pixelRedValue(pixel) < 30) ) {
                                allGreen    =   false;
                                grY         =   y;
                                grX         =   a.x+200;
                                break;
                            } 
                        }
                        if ( allGreen ) {
                            if ( debug ) 
                                Session.logMessageLine(prefix + "\tFound Green @ Y  = " + grY);
                            
                            break;
                        }
                    }
                    
                }
                else {
                    if ( debug ) 
                        Session.logMessageLine(prefix + "\tNo Chips Found");
                    return 0;
                }                
                break;
                
                
                
            case PREFLOP:
                prefix  =   this.getClass().getSimpleName() + "potValue PRE-FLOP";
                
                // we can just look for the number because there are no chips
                // above because the rake hasn't yet been computed
                
                if ( debug )
                    Session.logMessageLine(prefix + "\tLooking for pot x,x");
                
                findPreflopX:
                for (x = a.x + 200; x < a.x+300; x++ ) {
                    for ( y = a.y+100; y<a.y+120; y++) {
                        pixel   =   b.getRGB(x,y);
                        if ( CardUtilities.pixelRedValue(pixel) > 150 ) {
                            // upper left hand of the word pot
                            potX    =   x;
                            potY    =   y;
                            break   findPreflopX;
                        }
                    }
                    if ( potX > 0 )
                        break;
                }
                
                 if ( debug )
                    Session.logMessageLine(prefix + "\tPot @ (x,y) = (" + 
                            potX   +   "," + potY + ")" );
                break;

                
                
            case UNCALLED:
                prefix  =   this.getClass().getSimpleName() + "potValue UNCALLED Bets";
                
                // we can just look for the number because there are no chips
                // above because the rake hasn't yet been computed
                
                if ( debug )
                    Session.logMessageLine(prefix + "\tLooking for pot from x: " +
                             (a.x + 300) + " to " + (a.x+400) +
                             " y: " + (a.y+100) + " to "  + (a.y+110) );
                
                findUncalledX:
                for (x = a.x + 300; x < a.x+400; x++ ) {
                    for ( y = a.y+100; y<a.y+110; y++) {
                        pixel   =   b.getRGB(x,y);
                        if ( CardUtilities.pixelRedValue(pixel) > 150 ) {
                            // upper left hand of the word pot
                            potX    =   x;
                            potY    =   y;
                            break   findUncalledX;
                        }
                    }
                    if ( potX > 0 )
                        break;
                }
                
                 if ( debug )
                    Session.logMessageLine(prefix + "\tPot @ (x,y) = (" + 
                            potX   +   "," + potY + ")" );
                break;                
                
           
            default:
                Session.logMessageLine(prefix + "\tPot Reading of " + pot + " Not Supported");
                return -1;
                
        }
        
        if ( (grY > 0) && (grX >0) ) {
            if ( debug )
                Session.logMessageLine(prefix + "\tLooking for PotX and PotY: (grY,grX) = "
                        + "( " + grX + "," + grY + ")" );
                
            // find the row with white
            findWhiteRow:
            for (y=grY; y<grY+10; y++) {
                for (x=grX; x<grX+100; x++) {
                
                    pixel   =   b.getRGB(x,y);
                    
                    if ( CardUtilities.pixelRedValue(pixel) > 100 ) {
                        if ( debug )
                            Session.logMessageLine(prefix + "\tFound PotY = " + y);
                        
                        potY    =   y;
                        break   findWhiteRow;
                    }
                }
                if ( potY > 0 )
                    break;
            }
            
            // now find the first column with white
            findWhiteCol:
            for (x=grX; x<grX+100; x++) {
                //Session.logMessageLine(prefix + "\tChecking for PotX[" + x + "][" + potY + "+10 ]");
                
                for (y=potY; y<potY+10; y++) {
                    pixel   =   b.getRGB(x,y);
                    
                    if ( CardUtilities.pixelRedValue(pixel) > 100 ) {
                        potX    =   x;
                        break   findWhiteCol;
                    }
                }
                if ( potX > 0 )
                    break;
            }
        }
        
        
        
        if ( potX > 0 ) {
           
            if ( debug )
                Session.logMessageLine(prefix + "\tpotX (upper left corner provisional) @ (" +
                        potX   +   "," +    potY  + ")");
            // is there a white line at the top 
            
            // if there a green line 10 rows below (past the comma) or are we reading a mismash of chips
            // or we are on the wrong line
            int lineBelow   =   0;
            for (x=potX; x<potX+75; x++) {
                pixel   =   b.getRGB(x, potY+10);
                if ( (CardUtilities.pixelRedValue(pixel) > 150) ||
                     (CardUtilities.pixelRedValue(pixel) < 25 ) ) {
                    if ( debug ) {
                        Session.logMessageLine(prefix + "\tNot Green (" +
                                x + "," + (potY+10) + ") = " +
                                CardUtilities.pixelRGBValues(pixel) );
                    }
                    lineBelow++;
                    break;
                }
            }
            
            if ( lineBelow > 5 ) {
                if ( debug )
                    Session.logMessageLine(prefix + "\tCan't Read Pot. No Green Line @ Y = " + 
                            (potY+10));
                return  -1;
            }
                
            // we have an upper left, do we have a clean line of green below, so we're not
            // merged with a chip stack, we can read
            // find where the Pot ends
            // if none of the first 4 rows are > 100 and the next 4 are it'p the start of
            // a number
            int numberStartX =   -1;
            
            if ( (pot == Game.Pot.PREFLOP) || ( pot == Game.Pot.UNCALLED) ) {
            
                for (x=potX; x<potX+50; x++) {

                    if ( (CardUtilities.pixelRedValue(b.getRGB(x, potY))   < 100) &&
                         (CardUtilities.pixelRedValue(b.getRGB(x, potY+1)) < 100) &&
                         (CardUtilities.pixelRedValue(b.getRGB(x, potY+2)) < 100) &&
                         (CardUtilities.pixelRedValue(b.getRGB(x, potY+3)) < 100) &&


                        ((CardUtilities.pixelRedValue(b.getRGB(x+1, potY))   > 100) ||
                         (CardUtilities.pixelRedValue(b.getRGB(x+1, potY+1)) > 100) ||
                         (CardUtilities.pixelRedValue(b.getRGB(x+1, potY+2)) > 100) ||
                         (CardUtilities.pixelRedValue(b.getRGB(x+1, potY+3)) > 100) ||
                         (CardUtilities.pixelRedValue(b.getRGB(x+1, potY+4)) > 100) ||
                         (CardUtilities.pixelRedValue(b.getRGB(x+1, potY+5)) > 100) ||
                         (CardUtilities.pixelRedValue(b.getRGB(x+1, potY+6)) > 100) ||
                         (CardUtilities.pixelRedValue(b.getRGB(x+1, potY+7)) > 100) ) ) {

                            numberStartX    =   x+1;
                            break;
                    }
                }
            }
            else {
                numberStartX    =   potX;
            }
                        
            try {
                pb = readTableNumber(numberStartX, potY, b);
                
                if ( debug ) 
                    Session.logMessageLine(prefix + "\tPot Size: " + pb);
          
            }
            catch ( UnableToReadNumberException e ) {
                throw new UnableToReadNumberException();
            }
        }
        else {
            if ( debug )
                Session.logMessageLine(prefix + "\tpotX not found");
            return  -1;
        }
        
        return pb;
    }
    
    
    @Override
    int readAllSplitPots(BufferedImage b) 
    throws UnableToReadNumberException, AnchorNotFoundException
    {
        int tot     =   0;
        int pot;
        
        pot    =   potValue(b, Game.Pot.SPLIT1);
        if ( pot > 0 )
            tot     +=  pot;
        
        pot    =   potValue(b, Game.Pot.SPLIT2);
        if ( pot > 0 )
            tot     +=  pot;
        
        pot    =   potValue(b, Game.Pot.SPLIT3);
        if ( pot > 0 )
            tot     +=  pot;
        
        pot    =   potValue(b, Game.Pot.SPLIT4);
        if ( pot > 0 )
            tot     +=  pot;
        
        
        return tot;            
    }
 
    
    @Override
    boolean potsMissingFromTable(BufferedImage b) 
    throws AnchorNotFoundException
    {
       
        String  prefix  =   this.getClass().getSimpleName() + ":potsMissingFromTable";
        
        int pixel;
        int n       = 0;
        
        if ( pw().getWindowAnchorCopy() == null )
            throw new AnchorNotFoundException(prefix);
        
        for (int x =    pw().windowAnchorX()+230;
                 x <    pw().windowAnchorX()+280;
                 x++)  {
            for (int y =    pw().windowAnchorY()+200;
                     y <    pw().windowAnchorY()+230;
                     y++ ) {
                     
                     pixel = b.getRGB(x,y);
        
                     if ( CardUtilities.pixelRedValue(pixel) > 110 )
                         n++;
            }
        }
       
        //Session.logMessageLine(prefix + "\tN: " + n);
        
        return n == 0;
    }
    
    
    
    // read by method of RGBGrid
    @Override
    int readPlayerBet(int seat, BufferedImage b ) 
    throws UnableToReadNumberException
    {
        
        String  prefix  =   this.getClass().getSimpleName() + ":readPlayerBet[" + seat + "]";
               
        int x, y;
        int r, ra; // row and row above
        int c, cl, cr; // column, colum left and right
        int topY    =-1, leftX  =-1;
        int nCols;
        int filled;
        
        if ( (betX(seat) <= 0) || (betY(seat)<=0) ) {
            if ( Session.logLevelSet(Session.logType.TABLE_NUMBER) )
                Session.logMessageLine(prefix + "\tBad Seat X,Y");
            return  0;
        }
        
        final   int BLANKRGB    = 100;
                
        if ( Session.logLevelSet(Session.logType.TABLE_NUMBER) ) {
            Session.logMessageLine(prefix + "\tSeat(X,Y) = (" + 
                seatX(seat) + "," + seatY(seat) + ")");
        
            Session.logMessageLine(prefix + "\tBet (X,Y) (" + 
                betX(seat) + "," + betY(seat) + ")");
        
            // find the top of the number, if it exists
            Session.logMessageLine(prefix + "\tLooking for Top from: " +
                betY(seat) + " to " + (betY(seat)-10) );
        }
        
        int maxLeft     =   betX(seat) - 30;
        int maxRight    =   betX(seat) + 10;
        
        if ( Session.logLevelSet(Session.logType.TABLE_NUMBER) )
            Session.logMessageLine(prefix + "\tRange of X is from " + 
                maxLeft + " to " + maxRight );
                
        for (y=betY(seat); y>betY(seat)-10; y--) {
            r = ra = 0;
            for (x=maxLeft; x<maxRight; x++) {
                if ( CardUtilities.AllRGBAboveValue(150, b.getRGB(x,y)))
                    r++;
                if ( CardUtilities.AllRGBAboveValue(150, b.getRGB(x,y-1)))
                    ra++;
            }
            
            if ( (r>0) && (ra==0) ) {
                topY    =   y;
                break;
            }
        }
        
        if ( topY > 0 ) {
            if ( Session.logLevelSet(Session.logType.TABLE_NUMBER) )
                Session.logMessageLine(prefix + "\ttopY: " + topY);
        }
        else {
            if ( Session.logLevelSet(Session.logType.TABLE_NUMBER) )
                Session.logMessageLine(prefix + "\ttopY Not Found: " + topY);
            
            return 0;
        }
        
        // find the left most column
        // which means 3 blank columns to the left
        if ( Session.logLevelSet(Session.logType.TABLE_NUMBER) )
            Session.logMessageLine(prefix + "\tMoving Left to Find First Col");
        
        for (x=betX(seat); x>betX(seat)-100; x--) {
            filled  =   0;
            
            for (int i=1; i<=3; i++) {
                for (y=topY; y<topY+7; y++) {
                    if ( CardUtilities.AllRGBAboveValue(BLANKRGB, b.getRGB(x-i, y)) )
                        filled++;
                }
            }
            
            if ( filled == 0 ) {
                leftX   =   x;
                break;
            }
        }
        
        
        if ( leftX > 0 ) {
            if ( Session.logLevelSet(Session.logType.TABLE_NUMBER) )
                Session.logMessageLine(prefix + "\tleftX: " + leftX);
            
            nCols   =   0;
            for (x=leftX; x<leftX+100; x++) {
                
                filled = 0;
                for (int i=x; i<x+3; i++) {
                    for (int j=topY; j<topY+7; j++) {
                        if ( CardUtilities.AllRGBAboveValue(BLANKRGB, b.getRGB(i,j)) ) {
                            filled++;
                        }
                    }
                }
                                
                if ( filled == 0 ) {
                    break;
                }
                else {
                    nCols++;
                }
                
            }
           
            nCols   +=  2;
            
            // create a grid for the number
            try {
                
                RGBGridTableNumber  number =   new RGBGridTableNumber(b, leftX, topY, 7, nCols);

                number.fillGridGreaterThan(150);
                
                if ( Session.logLevelSet(Session.logType.TABLE_NUMBER) ) {
                    // set debug for the Grid object so we can see how it'p processed
                    number.printGrid();
                    number.setDebug(true);
                }
                                
                return  Integer.parseInt(number.toString());
            }
            catch ( Exception e ) {
                throw   new  UnableToReadNumberException(prefix);
            }
        }
        else {
            if ( Session.logLevelSet(Session.logType.TABLE_NUMBER) )
                Session.logMessageLine(prefix + "\tleftX: Not Found");
            
            return 0;
        }       
    }
    
    
    
   
     boolean lightTabNumPixel(int p) {
        
        return !CardUtilities.AllRGBBelowValue(150, p);
        
    }
    
    // -1 means number not read
    //  x is the left edge
    //  y is the top row
     @Override
    int readTableNumber(int x, int y, BufferedImage b)
    throws  UnableToReadNumberException
    {
       
        int     i, j;
        int     pixel,r;
        int     firstCol    =   x;
        int     lastCol     =   -1;
        String  prefix      =   this.getClass().getSimpleName() + "readTableNumber(" + x + "," + y + ")";
        boolean debug       =   Session.logLevelSet(Session.logType.TABLE_NUMBER);
        
        boolean debugNumberReading  =   debug;
        
        // read top down, right to left: X-, Y+
        //if ( debug ) Session.logMessageLine(prefix + "\tParameters Passed " + x + "," + y);

        if ( (x <= 0) || (y <= 0) )  {
            if ( debug ) Session.logMessageLine(prefix + "\tBad starting Point " + x + "," + y);
            throw new UnableToReadNumberException();
        }
        
        // how many columns is this number?
        // and, do we even have a number?
        boolean filled;
        for (i=x; i<x+100; i++) {
            filled  =   false;
            for (j=y; j<y+7; j++) {
                if ( (CardUtilities.pixelRedValue( b.getRGB(i,  j)) > 100) ||
                     (CardUtilities.pixelRedValue( b.getRGB(i+1,j)) > 100) ||
                     (CardUtilities.pixelRedValue( b.getRGB(i+2,j)) > 100) ||
                     (CardUtilities.pixelRedValue( b.getRGB(i+3,j)) > 100) ) {
                    filled  =   true;
                }
            }
            if ( !filled ) {
                lastCol =   i;
                break;
            }
            else if ( debug )
                Session.logMessageLine(prefix + "\tFilled[" + i + "]");
        }
        
        if ( lastCol < 0 ) {
            if ( debug )
                Session.logMessageLine(prefix + "\tError Finding End of Number");
        }
        
        int         rows    =   7;
        int         cols    =   lastCol - firstCol + 1 + 10;
        int     []  f       =   new int [cols];           
        boolean [][] m      =   new boolean[rows][cols]; 
        // add an extra col the beginning as a start-of-char marker
        // and an extra at the end 
        
        if ( debug ) {
            Session.logMessageLine(prefix + "\tLast Column: " + lastCol);
            Session.logMessageLine(prefix + "\tCols:  " + cols);
        }
        
        for (i=0; i<rows; i++) {
            for (j=0; j<cols; j++) {
                m[i][j]   =   false;
                f[j]        =   0;
            }
        }
        
        
        // shift data over 5 columns so we have space to check ahead
        // we need it empty to check for what char it is
        
        for (i=0; i< cols-5; i++) {
            for (j=0; j<7; j++)  {
                pixel   =   b.getRGB(firstCol + i,y+j);
                if ( CardUtilities.pixelRedValue(pixel) >= 150 ) {
                    m[j][i+5]   =   true;
                    f[i+5]++;
                }
            }
        }
        
         
        int c;
        
        if ( debugNumberReading ) {
            for  (r=0; r<7; r++) {
                Session.logMessageChar("" + (y+r));
                for (c=0; c < cols; c++) {
                    Session.logMessageChar( m[r][c] ? "#" : "_");
                }
                Session.logMessageChar("\n");
            }
        }
        
        // count digits by couting heads
        
        double partsum =   0.0;
        double place   =   0.0;
        
        // read the number from right to left
        // minus moves to left
        // plus to right
        for (x=cols-1; x>3; x--) {
            if ( m[0][x] && !m[0][x-1] ) {
                if ( debug ) {
                     Session.logMessageLine("\n" + prefix + "\tDigit End @ x = " + x );
                     Session.logMessageLine(prefix + "\tf[" + x + "] = " + f[x] );
                }
                
                
                switch ( f[x] ) {
                    
                    case 1: //7
                        if ( debug ) 
                            Session.logMessageLine(prefix + "\t7");
                        
                        partsum += 7.0 * Math.pow(10.0,(1.0 * place));
                    break;
                    
                      
                    case 2: // 0
                        if ( m[0][x] && m[6][x] && (f[x-1]==0) ) {
                            if ( debug ) 
                                Session.logMessageLine(prefix + 
                                        "\t7 m[0,6] f[x-1]=0");
                        
                            partsum += 7.0 * Math.pow(10.0,(1.0 * place));
                        }
                        else if ( (f[x+1]==7) && (f[x+2]==0) ) {
                            if ( debug ) 
                                Session.logMessageLine(prefix + 
                                        "\t4 f[x+1]=7 && f[x+2]=0");
                        
                            partsum += 4.0 * Math.pow(10.0,(1.0 * place));
                        }
                        else if ( !m[1][x]  && !m[1][x+1] && 
                                  !m[1][x+2] && !m[1][x+3] ) {
                            if ( debug ) 
                                Session.logMessageLine(prefix + 
                                        "\t5 !m[1][x,1,2,3");
                        
                            partsum += 5.0 * Math.pow(10.0,(1.0 * place));
                        }
                    break;
                    
                    
                    
                    case 3: // 3,4,5,9
                        if ( m[0][x] && m[3][x] && m[6][x] &&
                             (f[x-1]<=3)
                                ) {
                            if ( debug ) 
                                Session.logMessageLine(prefix + "\t3 m[0][1] && m[3][x] && m[6]");
                        
                            partsum += 3.0 * Math.pow(10.0,(1.0 * place));
                        }
                        else if ( f[x+1]==7 ) {
                            
                            if ( debug ) 
                                Session.logMessageLine(prefix + "\t4 f[x+1]=7");
                        
                            partsum += 4.0 * Math.pow(10.0,(1.0 * place));
                        }
                        else if ( !m[1][x]   && !m[2][x]   && !m[3][x] &&
                                  !m[1][x+1] && !m[2][x+1] && !m[3][x+1]  ) {
                            
                            if ( debug ) 
                                Session.logMessageLine(prefix + 
                                        "\t9 !m[1,2,3][x,x+1]");
                        
                            partsum += 9.0 * Math.pow(10.0,(1.0 * place));
                        }
                        else if ( m[1][x-1] && m[2][x-1] ) {
                            if ( debug ) 
                                    Session.logMessageLine(prefix + "\tm[1][x-1] m[2][x-1");
                            
                            if ( m[4][x-1] && m[5][x-1] ) {
                                if ( debug ) 
                                    Session.logMessageLine(prefix + "\t6 m[4,5][x-1");
                                
                                partsum += 6.0 * Math.pow(10.0,(1.0 * place));
                            }
                            else {
                                if ( debug ) 
                                    Session.logMessageLine(prefix + "\t5 !m[4,5][x-1]");
                        
                                partsum += 5.0 * Math.pow(10.0,(1.0 * place));
                            }
                        }
                        else if ( !m[4][x-1] && m[4][x]) {
                            
                            if ( debug ) 
                                Session.logMessageLine(prefix + "\t9 !m[4]");
                        
                            partsum += 9.0 * Math.pow(10.0,(1.0 * place));
                        }
                    break;
                    
                    
                    case 4://0,2, 3, 5, 8, 9
                        if ( (f[x-1]==4) &&
                              m[1][x-1] && m[2][x-1] &&
                              m[4][x-1] && m[5][x-1] ) {
                            
                            if ( debug ) 
                                Session.logMessageLine(prefix + 
                                        "\t8 f[x-1]=4, m[1,2][x-1] && m[4,5][x-1]");
                        
                            partsum += 8.0 * Math.pow(10.0,(1.0 * place));
                        }
                        else if ( m[0][x] && m[4][x] && m[5][x] && m[6][x] ) {
                            if ( debug ) 
                                Session.logMessageLine(prefix + 
                                        "\t2 f[x-1]=4, m[0,4,5,6]");
                        
                            partsum += 2.0 * Math.pow(10.0,(1.0 * place));
                        }
                        else if ( m[0][x] && m[1][x] && m[5][x] && m[6][x] ) { 
                            if ( debug )
                                Session.logMessageLine(prefix + 
                                        "\t m[0,1,5,6]");
                             
                             if ( !m[2][x+1] && !m[3][x+1] && 
                                  !m[2][x-1] && !m[3][x-1]   
                                     ) {
                                if ( debug ) 
                                    Session.logMessageLine(prefix + 
                                        "\t2 ![2,3][x-1] && !m[2,3][x+1]");
                        
                                partsum += 2.0 * Math.pow(10.0,(1.0 * place));
                            }
                            else if ( (f[x+1]==3) &&
                                      m[0][x+1] && m[3][x+1] && m[6][x+1] ) {
                                
                                 if ( debug ) 
                                    Session.logMessageLine(prefix + 
                                        "\t3 f[x+1=3, m[0,3,6][x+1]");
                        
                                partsum += 3.0 * Math.pow(10.0,(1.0 * place));
                            }
                             
                        }
                        else if ( m[3][x+1] && m[3][x+2]) {
                            
                            if ( debug ) 
                                Session.logMessageLine(prefix + "\t!m[3][1] && m[3][x=2,3]");
                            
                            if ( m[1][x-1] && m[2][x-1]) {
                                if ( debug ) 
                                    Session.logMessageLine(prefix + "\t8 m[1][x-1] && m[2][x-1]");
                                
                                partsum += 8.0 * Math.pow(10.0,(1.0 * place));
                            }
                            else {
                                if ( debug ) 
                                    Session.logMessageLine(prefix + "\t3 !m[1][x-1] && !m[2][x-1]");
                                
                                partsum += 3.0 * Math.pow(10.0,(1.0 * place));
                            }
                        }
                        else if ( !m[2][x] && !m[4][x] && !m[5][x] ) {
                             if ( debug ) 
                                Session.logMessageLine(prefix + "\t5 !m[3,4,5]");
                        
                            partsum += 5.0 * Math.pow(10.0,(1.0 * place));
                        }
                        else if ( m[0][x] && m[2][x] && m[3][x] && m[6][x] ) {
                            if ( debug ) 
                                Session.logMessageLine(prefix + "\t8 m[0,2,3,6]");
                        
                            partsum += 8.0 * Math.pow(10.0,(1.0 * place));
                        }
                        else if ( m[3][x+1] && m[3][x+2]) {
                            if ( debug ) 
                                Session.logMessageLine(prefix + "\t9 m[3][x=1,2,3]");
                        
                            partsum += 9.0 * Math.pow(10.0,(1.0 * place));
                        }
                      
                    break;
                      
                    case 5:
                    case 6: //0, 2, 5, 6, 9
                        if ( m[6][x] && m[6][x+1] && m[6][x+2] && m[6][x+3]) {
                            if ( debug ) 
                                Session.logMessageLine(prefix + "\t2 m[6][x+0,1,2,3]");
                        
                            partsum += 2.0 * Math.pow(10.0,(1.0 * place));
                        }
                        else if ( !m[2][x] && m[2][x+1] && !m[2][x+2] ) {
                            if ( debug ) 
                                Session.logMessageLine(prefix + 
                                            "\t6 !m[2][x] m[2][x+1] !m[2][x+2]");
                        
                            partsum += 6.0 * Math.pow(10.0,(1.0 * place));
                        }
                        else if ( !m[2][x]    &&  !m[2][x+1] && 
                                  !m[2][x+2]  &&  !m[2][x+3] ) {
                            if ( debug ) 
                                Session.logMessageLine(prefix + 
                                            "\t6 !m[2][x+0,1,2,3]");
                        
                            partsum += 6.0 * Math.pow(10.0,(1.0 * place));
                        }
                        else if ( f[x+1] == 3 ) {
                            if ( debug ) 
                                Session.logMessageLine(prefix + "\tf[x+1]=3");
                            
                            // the bar across
                            if ( m[2][x+1] && m[2][x+2]) {
                                if ( debug ) 
                                    Session.logMessageLine(prefix + "\t5 m[2][x+1,2,3]");
                        
                                partsum += 5.0 * Math.pow(10.0,(1.0 * place));
                            }
                            else if ( m[3][x+1] && m[3][x+2]) {
                                if ( debug ) 
                                    Session.logMessageLine(prefix + "\t6 m[3][x=1,2,3]");
                        
                                partsum += 6.0 * Math.pow(10.0,(1.0 * place));
                            }
                            else if ( !m[2][x] ) {
                                if ( debug )
                                    Session.logMessageLine(prefix + "\t! m[2][x]");
                                
                                if ( (f[x-1] == 4)  && 
                                     !m[0][x-1] && !m[1][x-1] && !m[6][x-1] ) {
                                    if ( debug ) 
                                        Session.logMessageLine(prefix + "\t4 f[x-1]==4, !m[0,1,6]");
                        
                                    partsum += 6.0 * Math.pow(10.0,(1.0 * place));
                                }
                                else {
                                    if ( debug ) 
                                        Session.logMessageLine(prefix + "\t9 m[2]");
                        
                                    partsum += 9.0 * Math.pow(10.0,(1.0 * place));
                                }
                            }
                            else if ( !m[4][x] ) {
                                if ( debug ) 
                                    Session.logMessageLine(prefix + "\t9 !m[4]");
                        
                                partsum += 9.0 * Math.pow(10.0,(1.0 * place));
                            }
                        }
                    break;
                    
                    
                    case 7: //0, 1, 4, 6, 8
                        if ( debug )
                            Session.logMessageLine(prefix + "\tf[x+1] = " + f[x+1]);
                        
                        if ( (f[x-1] == 1) && m[1][x-1] ) {
                            if ( debug ) 
                                Session.logMessageLine(prefix + "\t1 f[x-1]=1");
                        
                            partsum += 1.0 * Math.pow(10.0,(1.0 * place));
                        }
                        else if ( (f[x-1]==7) && (f[x-2]==1) && m[1][x-2] ) {
                             if ( debug ) 
                                Session.logMessageLine(prefix + "\t1 f[x-2]=1");
                        
                             partsum += 1.0 * Math.pow(10.0,(1.0 * place));
                        }
                        else if ( (f[x-3]==0) && (f[x-2]==1) && (f[x-1]>0)) {
                             if ( debug ) 
                                Session.logMessageLine(prefix + "\t1 f[x-3]=0, f[x-2]=1, f[x-1]>0");
                        
                             partsum += 1.0 * Math.pow(10.0,(1.0 * place));
                        }
                        else if ( f[x+1]==0 ) {
                            if ( debug ) 
                                Session.logMessageLine(prefix + "\t4 f[x+1]=0");
                        
                            partsum += 4.0 * Math.pow(10.0,(1.0 * place));
                        }
                        else if ( f[x+1]==1 ) {
                            if ( debug ) 
                                Session.logMessageLine(prefix + "\t4 f[x+1]=1");
                        
                            partsum += 4.0 * Math.pow(10.0,(1.0 * place));
                        }
                        else if ( f[x+1] == 3 ) {
                            if ( debug )
                                Session.logMessageLine(prefix + "\tf[x+1]=3");
                            
                            if (m[1][x+2] && m[2][x+2]) {
                                if ( debug ) 
                                    Session.logMessageLine(prefix + "\t8 m[1,2][x+2]");
                        
                                partsum += 8.0 * Math.pow(10.0,(1.0 * place));
                            }
                            else if (m[1][x+3] && m[2][x+3]) {
                                if ( debug ) 
                                    Session.logMessageLine(prefix + "\t8 m[1,2][x+3]");
                        
                                partsum += 8.0 * Math.pow(10.0,(1.0 * place));
                            }
                            else {
                                if ( debug ) 
                                    Session.logMessageLine(prefix + "\t6 (default)");
                        
                                partsum += 6.0 * Math.pow(10.0,(1.0 * place));
                            }
                        }
                    break;
                }
                
                // every top of digit is a new power of 10
                place++;
            } // end if top of digit break
        } // end for all columns
            
        Double  fullvalue =    partsum;
        
        if ( debug  ) 
            Session.logMessageLine(prefix + "\t=======>NUMBER: " + fullvalue.intValue());
        
        return fullvalue.intValue();
        
    } // end function

  
    
    
    @Override
    Game.ActionType readShieldAction(int s, BufferedImage b) {
        
        if ( (s < 0) || ( s > numSeats() ) ) 
            return  Game.ActionType.ACTION_ERROR;
        
        int startY              = -1;
        int startX              = -1;
        int x                   =   seatX(s);
        int y                   =   seatY(s);
        int i,j;
        boolean debug           =   Session.logLevelSet(Session.logType.INFO);
        
        boolean number          =   false;
        Game.ActionType      action;
        
        String  prefix  =   this.getClass().getSimpleName() + ":readShieldAction[" + s + "]";
                 
        if ( (x <= 0) || ( y <= 0) ) 
            return Game.ActionType.ACTION_ERROR;
        
        int p, r,g;
        for (y=seatY(s); y <= seatY(s)+5; y++) {
            for (x=seatX(s)+30; x < seatX(s)+60; x++) {
                p   =   b.getRGB(x,y);
                r   =   CardUtilities.pixelRedValue(p);
                g   =   CardUtilities.pixelGreenValue(p);
                
                if ( r<100 && g <100 ) {
                    number    =   true;
                } 
            }
        }
    
        
        if ( number ) {
            if ( debug ) Session.logMessageLine(prefix + "\tNo Action on the Shield.");
            return Game.ActionType.NOT_AN_ACTION;
        }
        
        if ( debug ) Session.logMessageLine(prefix + "\tSearching for Start of Action at: " + x + "," + y);
                            
        // find the lower left hand edge of the action
        int darkinrow;
        findLowerLeft:
        for ( y = seatY(s) ; y > seatY(s)-15; y-- ) {
            darkinrow   =   0;
            for (x = seatX(s)+20;x<seatX(s)+50; x++) {
                if ( CardUtilities.isDarkShieldPixel(b.getRGB(x,y)) ) {
                    if ( darkinrow == 0 )
                        startX  =   x;
                    
                    darkinrow++;
                    if ( darkinrow > 5 ) {
                        if ( debug ) Session.logMessageLine(prefix + "\tFound Start of Action at: " + startX + "," + y);
                            
                        startY  =   y;
                        break findLowerLeft;
                    }
                }
            }
        }
        
        if ( startY < 0 ) {
            if ( debug ) Session.logMessageLine(prefix + "\tStart Y not found");
            return Game.ActionType.NOT_AN_ACTION;
        }
       
        int firstcol    =   0;
        int secondcol   =   0;
        int thirdcol    =   0;
        int above       =   0;
        
        for (i=0;i<11;i++) {
            
            if ( CardUtilities.isDarkShieldPixel(b.getRGB(startX,startY-i)) )
                firstcol++;
            
            if ( CardUtilities.isDarkShieldPixel(b.getRGB(startX+1,startY-i)) )
                secondcol++;
            
             if ( CardUtilities.isDarkShieldPixel(b.getRGB(startX+2,startY-i)) )
                thirdcol++;
        }
        
        boolean lineabovebox    =    false;
        for (i=0; i<25 ;i++) {
            
            if ( !lineabovebox ) 
                if ( CardUtilities.isDarkShieldPixel(b.getRGB(startX+i,startY-9)) )
                    lineabovebox = true;
          
            if (CardUtilities.isDarkShieldPixel(b.getRGB(startX+i,startY-10)))
                above++;
        }   
        
        if ( debug ) Session.logMessageLine(prefix + "\tLine Above Box: " + lineabovebox + "\tAbove: " + above);
        
        action  =   Game.ActionType.NOT_AN_ACTION;
           
        // ALL_IN, BET, CALL, CHECK, FOLD, MUCK, RAISE, SHOW
        
        if ( action != Game.ActionType.ACTION_ERROR ) {
            // allin, bet, check, call, fold, muck, raise, show
            if ( (firstcol >= 10) || (secondcol>=10) ) {
                if ( CardUtilities.isDarkShieldPixel(b.getRGB(startX-1,startY-7)) ||
                     CardUtilities.isDarkShieldPixel(b.getRGB(startX-1,startY-8)) ||
                     CardUtilities.isDarkShieldPixel(b.getRGB(startX-1,startY-9)) )
                    
                    action  =   Game.ActionType.ACTION_FOLD;
                else
                    action = Game.ActionType.ACTION_BET;
            }
            else if ( ((above ==2) || (above==3)) && (lineabovebox == false)) {
                action  =   Game.ActionType.ACTION_RAISE;
            }
            else if ( above == 4 ) {
                action  =   Game.ActionType.ACTION_CALL;
            }
            else if ( above > 5 ) {
                action  =   Game.ActionType.ACTION_ALLIN;
            }
            // ALLIN, SHOW, CHECK, RAISE, CALL, FOLD, MUCK

            // CHECK or SHOW
            else if (  above > 0 ) {
        
                if ( CardUtilities.isDarkShieldPixel(b.getRGB(startX+2,startY-4)) ||
                     CardUtilities.isDarkShieldPixel(b.getRGB(startX+3,startY-4))  ) 
                    
                    action  =   Game.ActionType.ACTION_SHOW;
                else
                    action  =   Game.ActionType.ACTION_CHECK;
                
            }
            else if ( (firstcol == 9) && (secondcol == 9) && (thirdcol == 9) ) {

                    action  =   Game.ActionType.ACTION_MUCK;
            }
            else
                action  = Game.ActionType.ACTION_ERROR;
        } // end if the top of the action box wasn't filled in
          
        if ( (action == Game.ActionType.ACTION_ERROR) && debug )  {
            Session.logMessageLine(prefix + "\tPrinting Matrix for action: " + action);
            for (y=startY-12;y<=startY;y++) {
                for (x=startX-1;x<=startX+30;x++) {
                    if ( CardUtilities.isDarkShieldPixel(b.getRGB(x,y)) )
                        Session.logMessageChar("#");
                    else
                        Session.logMessageChar("_");
                }
                Session.logMessageChar("\n");
            }     
            Session.logMessageLine(prefix + "\tDone Printing Matrix.");
            
            if ( action == Game.ActionType.ACTION_ERROR ) {
                Session.logMessageLine(prefix + "\tError. Unable to understand action.");
            }
                
        }
        
        if ( debug )
             Session.logMessageLine(prefix + "\tACTION = " + action.toString() );
         
        return action;

    }
    
    @Override
    public boolean  isSittingOut(int s, BufferedImage b) {
        int gold    = 0, grey=0;
        int x,    y;
        String      prefix  =   this.getClass().getSimpleName() + "isSittingOut[" + s + "]";
        
        if ( (seatX(s)<=0) || (seatY(s)<=0) )
            return true;
        
        try {
        for (x=seatX(s);x < seatX(s)+15;x++) 
            for ( y = seatY(s)-15; y <= seatY(s); y++){
                int p   =   b.getRGB(x,y);
                
                if ( CardUtilities.AllRGBAboveValue(150, p) )
                    grey++;
                else {
                
                    int r   =   CardUtilities.pixelRedValue(p);
                    int g   =   CardUtilities.pixelGreenValue(p);
                
                    if ( (r>150) && (g>150) ) {
                        gold++;
                    }
                }
            }
        }
        catch ( Exception e ) {
            if ( Session.logLevelSet(Session.logType.INFO) )
                Session.logMessageLine(prefix + "\tSeat X: " +  seatX(s) + " seatY: " + seatY(s) );
        }
        
        if ( Session.logLevelSet(Session.logType.INFO) )
             Session.logMessageLine(prefix + "\tGOLD = " + gold + " GREY: " + grey);
        
        return ( gold <  50 );
          
    }
   
    
    
    public boolean  isDarkPixel(int pixel) {
        
        int     red     =   CardUtilities.getPixelRedValue(pixel);
        int     green   =   CardUtilities.getPixelGreenValue(pixel);
                
        return ( (red < 100) && (green < 100) );
          
    }
    
    public boolean  darkPixel(int pixel ) {
        return ( CardUtilities.AllRGBBelowValue(75, pixel) );
            
    }
    
    
    
    @Override
    public int readShieldChipStack(int s, BufferedImage b) 
    throws UnableToReadNumberException
    {
        String  prefix          =   this.getClass().getSimpleName() + ":readShieldChipStack[" + s + "]";
        boolean debug           =   Session.logLevelSet(Session.logType.SHIELD_NUMBER);
        
        if ( shieldInTransition(s,b) ) {
            throw new UnableToReadNumberException();
        }
        
        final int   NUMROWS     =   9;
        final int   MINGRB      =   85;
        int x,y;
        int dark, pixel,red, green;
        int i,j;
        // centered on the column we found with data, 0 = to the left, 2 = to the right
        boolean [][] m      =   new boolean[NUMROWS][5];
        int     []   f      =   new int[5]; // fill for these three columns
        int black           =   0;
        int startY          =   -1;
        
        if (debug )
            Session.logMessageLine(prefix + "\tReading Stack @ (" + seatX(s) + "," + seatY(s) + ")");
        
        if ( (seatX(s) < 0) || ( seatY(s) < 0 ) )
            throw new UnableToReadNumberException(prefix + "\tReading Stack @ (" + seatX(s) + "," + seatY(s) + ")");
        
        
        for (x=seatX(s)+20;x<seatX(s)+50;x++) {
            for ( y = seatY(s)-5; y < seatY(s)+5; y++)
            if ( CardUtilities.isDarkShieldPixel(b.getRGB(x, y))) {
                black++;
            }
        }
        
        if ( black < 20 ) {
            if ( debug ) 
                Session.logMessageLine(prefix + "\tDoes not display a number.");
            
            return -1;
        }
        
        // find the top y
        for (y=seatY(s); y > seatY(s)-10; y--)  {
            dark    =   0;
            for ( x = seatX(s); x < seatX(s) + 70; x++) {
                pixel   =   b.getRGB(x,y);
                red     =   CardUtilities.getPixelRedValue(pixel);
                green   =   CardUtilities.getPixelGreenValue(pixel);
                
                if ( (red < MINGRB) && (green < MINGRB) ) {
                    dark++;
                }
            }
            
            if ( debug ) 
                Session.logMessageLine(prefix + "\t Y = " + y + " Dark = " + dark);
            
            if ( dark == 0 )  {
                startY  =   y+1;
                break;
            }
        }
        
        if ( startY < 0 ) 
            throw new UnableToReadNumberException();
                
        final   int NROWS   =   9;
        
        RGBGridShieldNumber  number = new RGBGridShieldNumber(b, seatX(s)+5, startY, NROWS, 70);
        
        number.fillGridLessThan(35);
            
        if ( debug )
            number.printGrid();
        
        try {
            return  Integer.parseInt(number.toString());
        }
        catch ( NumberFormatException e) {
            if ( Session.logLevelSet(Session.logType.SHIELD_NUMBER) )
                Session.logMessageLine(prefix + "\tUnable To Format: " + number.toString());
            
            throw new UnableToReadNumberException(prefix);
        }
        
        //return number.numberFromGrid();
    }
    
    
    @Override
    String readHandNumber(BufferedImage b) 
    throws  UnableToReadCharException,  AnchorNotFoundException
    {
        
        
        int             x,    y;
        int             b0                  = 0,    bm1 = 0;// b-zero and b-minus 1
        int             i;
        int             b1                  = 0;
        boolean         found               = false;
        int             HY                  = 0;
        double          power;
        double          partsum;
        double          value;
        int             off;
        int             handNumberX         = 0;
        int             handNumberY         = 0;
        boolean         debug               = Session.logLevelSet(Session.logType.HAND_NUMBER);
        String          prefix              = this.getClass().getSimpleName() + ":readHandNumber";
        
        if ( debug ) {
            Session.logMessageLine(prefix + "\t: Inside Function");
             Session.logMessageLine(prefix + "\tAnchor (" + pw().windowAnchorX() 
                     + "," +    pw().windowAnchorY() + ")" );
        }
        
        if ( (pw().windowAnchorX() == 0) || (pw().windowAnchorY()==0) ) {
            throw   new UnableToReadCharException(prefix + "\tInvalid Anchor: (" 
                    + pw().windowAnchorX() + "," + pw().windowAnchorY() + ")");
        }

        // from the screen anchor go up past the numbers then
        // come back down
        
        for (y=pw().windowAnchorY()-55;y<pw().windowAnchorY()-45;y++) {
            for (x=pw().windowAnchorX()-20;x<pw().windowAnchorX()+20;x++) {
                if ( CardUtilities.isBlackPixel(b.getRGB(x,y)))     b0++;
                if ( CardUtilities.isBlackPixel(b.getRGB(x,y+1)))   bm1++;
            }
           
            if ( debug )  {
                Session.logMessageLine(prefix + "Y = " + y + " b0 = " + b0 + " b1 = " + b1);
            }
            
            if ( (b0 > 0) && (b1 == 0 ) ) {
                handNumberY =   y;
                break;
            }
        }

        // if we found an y let'p find the x
        if ( handNumberY != 0 ) {
            
            if ( debug ) {
                Session.logMessageLine(prefix + "\tStart looking for end at X = " + (pw().windowAnchorX()+50));
                Session.logMessageLine(prefix + "\tStart looking for end at Y = " + handNumberY + " Y = " + y );
            }
            
            y   =   handNumberY;
            
            findHandX:
            for ( x = pw().windowAnchorX()+50; x > pw().windowAnchorX(); x--) {
                for ( i = 0; i < 9; i++ ) {
                    if ( CardUtilities.isBlackPixel(b.getRGB(x,y+i)) ) {
                        if ( debug ) Session.logMessageLine(prefix + "\tHand X: " + x);
                        handNumberX = x;
                        break findHandX;
                    }
                }
                
                if ( found )
                    break;
            }
        } else {
            throw   new UnableToReadCharException(prefix + "\tHand Y not found");
        }
        
        if ( (handNumberX < 0) || ( handNumberY < 0) ) {
            throw   new UnableToReadCharException(prefix + "\tHand X or Y not found: (" +
                        handNumberX + "," + handNumberY );
        }
        
        // print the hand number as digits
        int NUMLEN  =   70;
        
        y   = handNumberY;
        x   = handNumberX-(NUMLEN-3);
        
        if (debug )
            Session.logMessageLine(prefix + "\tRead Hand Number @ (" + x + ","  + y + " for " + NUMLEN + " columns");
        
        RGBGridHandNumber  digitMap    = new RGBGridHandNumber(b, x,  y, 9, NUMLEN);
        
        digitMap.fillGridLessThan(30);
                
        if ( debug ) {
            digitMap.printGrid();
            digitMap.setDebug(true);
        }
            
        return digitMap.toString();
    }
    
}
