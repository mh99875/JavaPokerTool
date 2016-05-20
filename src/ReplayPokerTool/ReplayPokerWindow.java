/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/**
 *
 *
 * 
 * <p>Poker Window represents the window on the screen the contains the Replay 
 * Poker game.</p>
 * 
 * <p>I would like the class to handle multi-tabling/multiple windows but as of the
 * current version this is neither tested nor fully fleshed out so the functions
 *for multiple windows are placeholders.</p>
 * 
 * <ol>
 * <li>CTOR: private all window requests go through newWindow() below</li>
 * 
 * <li>PokerWindow   newWindow()</li>
 * throws  InvalidScreenPositionException, AnchorNotFoundException, 
 * NoFreeScreensException
 * </ol>
 * 
 * <p>Poker Window represents the window on the screen the contains the Replay 
 * Poker game.</p>
 * 
 * <p>I would like the class to handle multi-tabling/multiple windows but as of the
 * current version this is neither tested nor fully fleshed out so the functions
 * for multiple windows are placeholders.</p>
 * 
 * 
 * <p>CTOR: private all window requests go through newWindow() below
 * made private so I can use the build pattern and guarantee all components
 * are properly constructed and ready to use before returning object or
 * fail with an exception when there's a chance for recover or exit for terminal 
 * cases (see below)</p>
 * 
 * <p>PokerWindow   newWindow() 
 * throws  InvalidwindowPositionException, AnchorNotFoundException, 
 *  NoMoreWindowsException</p>
 * 
 * <p>
 * Find window anchor
 * Read table name
 * </p>
 * 
 * 
 * <p>the table name is placed in the Window by Replay, logically. That is if the
 * user moves the window we have to re-read the Anchor and create the table 
 * locations but we don't have to re-read the table name because we don't have
 * to re-create the window, we only move it.</p>
 * 
 * <p>Theoretically we don't have to re-create the table, only translate it but we
 * do anyway</p>
 * 
 * <p>Error Handling</p>
 * 
 * <ul>
 * <li>a. if the table name can't be read, for instance if reading a Quicktime movie
 * made from a live Replay Poker game and the window is selected a blank bar is
 * placed by quickime over the table name and no read is possible.</li>
 * 
 * <p>In this case the name is set to "Unknown Table"</p>
 * 
 *<li>if a GameFamilyNotSupportedException is thrown by an anchor finding 
 * function the application exits, there is no recovery possible</li>
 * 
 * <li>if the error is a bad anchor the function lets the caller handle it</li>
 * </ul>
 * 
 * 
 *
 *
 * @author Marcus Haupt
 * @version 1.0
 */


public class ReplayPokerWindow extends PokerWindow {
    
    // measured from anchor
    
    static  final   int     ANCHOR_HEIGHT_MIN           =   400;
    static  final   int     ANCHOR_WIDTH_MIN            =   475;
    static  final   int     ANCHOR_X_MIN                =   120;
                
    
    public  static   ReplayPokerWindow   newWindow() 
    throws  InvalidwindowPositionException, 
            AnchorNotFoundException, 
            NoMoreWindowsException
    {
        
        int window  =   nextFreeWindow();
        
        try {
            return new ReplayPokerWindow(window)
                            .findWindowAnchor()
                            .readTableName();
        }
        catch ( UnableToReadCharException e) {
            try {
                ReplayPokerWindow pts =   new ReplayPokerWindow(window)
                                          .findWindowAnchor();
            
                pts.windowName  =   "Unknown Table";
                return pts;
            }
            catch (GameFamilyNotSupportedException e1 ) {
                if ( Session.logLevelSet(Session.logType.ERROR)  )
                    Session.logMessageLine("Game Family: "  + e1.getMessage() + " Not Supported. Exiting");
                
                Session.exit(0);
            }
        }
        catch (GameFamilyNotSupportedException e) {
            if ( Session.logLevelSet(Session.logType.ERROR)  )
                Session.logMessageLine("Game Family: " + e.getMessage() + " Not Supported. Exiting");

            Session.exit(0);
        }
        
        return null;
    }
    
    
    public  synchronized    static int nextFreeWindow() 
    throws  NoMoreWindowsException
    {
        return 0;  
    }

    
    public  int screenNum() {
        return  windowNum;
    }
    
    @Override
    public  String  windowName() {
        return  windowName;
    }
    
    
    public  int bigBlindBet() {
        return  bigBlindBet;
    }
    
     public  int smallBlindBet() {
        return  smallBlindBet;
    }
    
    // make a defensive copy while could go stale
    @Override
    public  Point   getWindowAnchorCopy() 
    throws AnchorNotFoundException
    {
        if ( windowAnchor == null )
            throw   new AnchorNotFoundException(
                    this.getClass().getSimpleName() + ":getSrceenAnchorCopy");
        
        return  windowAnchor;
    }
    
    @Override
    public  int windowAnchorX() 
    throws AnchorNotFoundException
    {
        if ( windowAnchor == null )
            throw   new AnchorNotFoundException(
                    this.getClass().getSimpleName() + ":windowAnchorX Failed to find anchor");
         
        return  windowAnchor.x;
    }
    
    @Override
    public  int windowAnchorY() 
    throws AnchorNotFoundException
    {
        if ( windowAnchor == null )
            throw   new AnchorNotFoundException(
                    this.getClass().getSimpleName() + ":windowAnchorY Failed to find anchor");
        
        return  windowAnchor.y;
    }
    
     
    public  ReplayPokerWindow findWindowAnchor() 
    throws AnchorNotFoundException, InvalidwindowPositionException, GameFamilyNotSupportedException
    {
        
        String  prefix          =   this.getClass().getSimpleName() + ":findWindowAnchor";
                
        Dimension size          =   Toolkit.getDefaultToolkit().getScreenSize();
        
        int maxX                =   (int)size.getWidth();
        int maxY                =   (int)size.getHeight();
        
        BufferedImage   b       =   CardUtilities.getCurrentBoard();
        int x,y;
        int i, j;
            
        findAnchor:
        for (x = 100; x < maxX - 800; x++ ) {
            for (y = 50; y < maxY - 75; y++ ) {

                if ( candidateForAnchor(b, x, y) ) {

                    if ( Session.logLevelSet(Session.logType.ANCHOR)  )
                        Session.logMessageLine(prefix + "\tFound Candidate (" + x + "," + y + ")");

                    int nGreen  =   numLegalTablepixels(x+50, y+100, 100, 100);

                    if ( nGreen > 1000 ) {
                        if ( Session.logLevelSet(Session.logType.ANCHOR)  )
                            Session.logMessageLine(prefix + "\tFound Green = " + nGreen);

                        RGBGridCharAnchor  anchor     =   new RGBGridCharAnchor(b, x-46, y-13, 7, 44);
                        
                        if ( Session.logLevelSet(Session.logType.ANCHOR)  )
                            anchor.setDebug(true);
                        
                        anchor.setMinrgb(175);
                        anchor.fillGridGreaterThan();

                        String  previous    =   anchor.toString();

                        if ( Session.logLevelSet(Session.logType.ANCHOR)  )
                            Session.logMessageLine(prefix + "\tCandidate String Word: " + previous);

                        if ( previous.equals("Previous") ) {

                            if ( Session.logLevelSet(Session.logType.ANCHOR)  )
                                Session.logMessageLine(prefix + "\tMatched with Previous");
                            
                            windowAnchor    =   new Point();
                            windowAnchor.x  =   x;
                            windowAnchor.y  =   y;

                            if ( Session.logLevelSet(Session.logType.ANCHOR)  ) {
                                Session.logMessageLine(prefix + "\tAnchor Found at (" + x + "," + y + ")");

                                Session.logMessageLine(prefix + "\tScreen Width: " + size.getWidth() );
                                Session.logMessageLine(prefix + "\tScreen Height: " + size.getHeight());
                            }

                            int bottomEdge  =   y   +   360;
                            int leftEdge    =   x   -   85;
                            int rightEdge   =   x   +   610;

                            if ( Session.logLevelSet(Session.logType.ANCHOR)  ) {
                                Session.logMessageLine(prefix + "\tBottom Edge: " + bottomEdge);
                                Session.logMessageLine(prefix + "\tLeft Edge: " + leftEdge);
                                Session.logMessageLine(prefix + "\tRight Edge: " + rightEdge);
                            }

                            if ( leftEdge < 32 )
                                 throw new InvalidwindowPositionException("Table Too Close to Left Edge: " 
                                    + "(" + windowAnchor.x + "," + windowAnchor.y + ")");

                            if ( rightEdge >= size.getWidth() )
                                 throw new InvalidwindowPositionException("Table Is Past Left Edge: " 
                                    + "(" + windowAnchor.x + "," + windowAnchor.y + ")");

                            if ( bottomEdge > size.getHeight() )
                                 throw new InvalidwindowPositionException("Table Falls Beneath Bottom Edge: " 
                                    + "(" + windowAnchor.x + "," + windowAnchor.y + ")");

                            if ( Session.logLevelSet(Session.logType.ANCHOR)  ) {
                                Session.logMessageLine(prefix + "\tScreen Anchor OK");
                                Session.logMessageLine(prefix + "\tScreen Anchor == null? " +
                                    (windowAnchor == null) );
                            }

                            return this;
                        }

                    } // end if green table pixels
                } // end if on a corner that could be "Replay Previous"
            } // end for y
        } // end for x // end for x // end for x // end for x
        
        throw   new AnchorNotFoundException(prefix + " Unable to find anchor");
    }
    
    public  boolean anchorIsValid() {
        
        if ( (windowAnchor == null ) || (windowAnchor.x == 0) || (windowAnchor.y==0) ) 
            return false;
        
        try {
            return  candidateForAnchor(CardUtilities.getCurrentBoard(), 
                    windowAnchorX(), windowAnchorY());
        }
        catch ( AnchorNotFoundException e ) {
            return false;
        }
    }   
    
    public  void    setAnchorToInvalid() {
        windowAnchor.x  =   windowAnchor.y  =   0;
    }
     
    public  boolean candidateForAnchor( BufferedImage b, int x, int y) {
       
        try {
            return ( (x >0) && (y>0) &&
            CardUtilities.AllRGBAboveValue(225,  b.getRGB(x,y))     &&  //the pixel in question
                
            CardUtilities.AllRGBAboveValue(200,  b.getRGB(x,y-1))   &&  // above 1
            CardUtilities.AllRGBAboveValue(150,  b.getRGB(x,y-2))   &&  // above 2
            CardUtilities.AllRGBAboveValue(70,   b.getRGB(x,y-3))   &&  // above 2
                
            // x
            CardUtilities.AllRGBAboveValue(225,  b.getRGB(x-1,y))   &&  // left 1
            CardUtilities.AllRGBAboveValue(150,  b.getRGB(x-2,y))   &&  // left 2 
            CardUtilities.AllRGBAboveValue(70,   b.getRGB(x-3,y))   &&  // left 2

            CardUtilities.AllRGBBelowValue(150,  b.getRGB(x-1,y-1)) &&  // diag left 1
            CardUtilities.AllRGBBelowValue(50,   b.getRGB(x-2,y-2)) &&
            CardUtilities.AllRGBBelowValue(50,   b.getRGB(x-3,y-3))   ) ;
        }
        catch ( ArrayIndexOutOfBoundsException e ) {
            return false;
        }
    }

    public  int numLegalTablepixels(int startX, int startY, int nRows, int nCols) 
    throws GameFamilyNotSupportedException
    {
        int green   =   0; // Hold 'em
        int blue    =   0; // Omaha / Omaha Hi-Lo
        int red     =   0; // Royal
        int i,j,pixel;
        int rd,gr,bl;
        
        BufferedImage   b   =   CardUtilities.getCurrentBoard();
        
        try {
            for (i=startX+50; i<startX+100; i++) {
                for ( j=startY+100; j<startY+150; j++) {
                    pixel   =   b.getRGB(i,j);
                    
                    rd      =   CardUtilities.pixelRedValue(pixel);
                    gr      =   CardUtilities.pixelGreenValue(pixel);
                    bl      =   CardUtilities.pixelBlueValue(pixel);
                    
                    if ( rd > 2 * bl )
                        red++;
                    else if ( gr > 3 * bl )
                        green++;
                    else if ( bl > rd * 3 )
                        blue++;
                }
            }
        }
        catch ( Exception e ) {
            // ran off the edge of the srcreen
            return 0;
        }
        
        if ( blue > 1000 ) {
            throw new GameFamilyNotSupportedException("Omaha");
        }
        
        
        // if we're not blue dominant return the greater of red and green
        return  green > red ? green : red;
    }
   
     
    private  ReplayPokerWindow (int num)
    throws  InvalidwindowPositionException, AnchorNotFoundException {
        
        windowNum =   num;
             
    }

    
    @Override
    public  ReplayPokerWindow  readTableName() 
    throws UnableToReadCharException
    {
        String  prefix  =    this.getClass().getSimpleName() + ":readTableName";
        
        if ( (windowAnchor == null ) ) 
            throw   new UnableToReadCharException(prefix + "\tScreen Anchor is Null" );
        
        int             x,y,n;
        int             pixel;
        int             greyStart       =   0;
        int             startX          =   0;
        int             nCols           =   0;
        int             startY          =   0;
        final   int     GREYHEIGHT      =   22;
        int             greyRGB;
                
        boolean         debug           =   Session.logLevelSet(Session.logType.TABLE_NAME);
        
        BufferedImage   b               =  CardUtilities.getCurrentBoard();
               
        // find the grey bar that contains the Table Name, if it exists
        findGrey:
        for ( y = windowAnchor.y - 85; y > windowAnchor.y - 115; y--) {
        
            // look at the 10 pixels to the left
            // they must all be over 200 and less than 255
            for (x=windowAnchor.x; x > windowAnchor.x-10; x--) {
                pixel   =    b.getRGB(x,y);
                
                if ( CardUtilities.AllRGBAboveValue(200, pixel) &&
                     CardUtilities.AllRGBBelowValue(255, pixel) ) {
                    greyStart   =   y;
                    break   findGrey;
                }                    
            }
        }
        
        if ( debug )  {
            Session.logMessageLine(prefix + "\tGrey Region Starts at " + greyStart);
        }
        
        
        if ( greyStart == 0 ) {
            throw   new UnableToReadCharException(prefix + "\tCan't Find Grey Start");
        }
        
        // inside the grey area
        // what is the averge grey value
        int g=0;
        n=0;
        
        for (y=greyStart; y>greyStart-GREYHEIGHT; y--) {
           g    +=  CardUtilities.getPixelRedValue(b.getRGB(windowAnchor.x,y) );
           n++;
        }
        
        greyRGB    =   g / n;
        
        if ( debug  ) {
            Session.logMessageLine(prefix + "\tBackground RGB = " + greyRGB);
            Session.logMessageLine(prefix + "\tFeature RGB = " + (greyRGB-20) );
        }
               
        
        int below;
        findFirstColumn:
        for ( x = windowAnchor.x; x < windowAnchor.x+250; x++) {
            below=0;
            for ( y = greyStart; y > greyStart-GREYHEIGHT; y--) {
                if ( CardUtilities.AllRGBBelowValue( greyRGB-20, b.getRGB(x,y)) ) {
                    below++;
                }
            }
            if ( debug )
                Session.logMessageLine(prefix + "\tX=" + x + " pixels below=" + below);
            
            if ( below > 5 ) {
                startX  =   x;
                break;
            }
        }
        
        if ( debug )
            Session.logMessageLine(prefix + "\tFirst Column of Name = " + startX);
               
        if ( startX == 0 ) {
            throw   new UnableToReadCharException(prefix + "\tCan't Find Start X");
        }
        
        // jump to about the middle of the name and move up until we fins a blank row
        // the first letter of the name is capitalized by convention but we check until 50
        // just to be sure
        
        for ( y=greyStart-10; y > greyStart - GREYHEIGHT; y--) {
            n = 0;
            for (x=startX; x<startX+50; x++) {
                if ( CardUtilities.AllRGBBelowValue(greyRGB-10, b.getRGB(x,y)) )  {
                    n = 1;
                    break;
                }
            }
            if ( n == 0 ) {
                startY  =   y+1;
                break;
            }
        }
        
        if ( startY == 0 ) {
            throw   new UnableToReadCharException(prefix + "\tCan't Find Start Y");
        }
        
        if ( debug )
            Session.logMessageLine(prefix + "\tFirst Row of Name = " + startY);
        
        // count the number of columns
        // ten blank columns in a row is the end
        for (x=startX; x < startX + 500; x++) {
            n   =   0;
            for ( int i=1; i<10; i++) {
                for (int j=0; j<10; j++) {
                    if ( CardUtilities.AllRGBBelowValue(greyRGB-10, b.getRGB(x+i,y+j)) ) {
                        n++;
                    }
                }
            }
            if ( n == 0 ) {
                nCols   =   (x - startX) + 5;
                break;
            }
          
        }
        
        if ( nCols == 0 )
            throw   new UnableToReadCharException(prefix + "\tCan't Find nCols");
        
        if ( debug ) {
            Session.logMessageLine(prefix + "\tStart X: " + startX );
            Session.logMessageLine(prefix + "\tStart Y: " + startY );
            Session.logMessageLine(prefix + "\tNum Cols: " + nCols );
        }
      
        int nRows       =   12;
        
        RGBGridTableName name = new RGBGridTableName(b, startX, startY, nRows, nCols);
        
        // what differentiates a feature pixel from the grey?
        int i,j, feature=0;
        n=0;
        
        for (i=0; i<10; i++) {
            for (j=0; j<nRows; j++) {
                pixel   =   b.getRGB(startX+i,startY+j);
                
                if ( CardUtilities.AllRGBBelowValue(greyRGB-10, j) ) {
                    feature +=  CardUtilities.getPixelRedValue(pixel);
                    n++;
                }
            }
        }
        
        feature     /=  n;
        
        try {
            // what is the average red value of non-grey pixels
            
            if ( debug ) {
                Session.logMessageLine(prefix + "\tFeature RGB:"  + feature) ;
                name.setDebug(true);
            }
            
            
            name.fillGridAnyBelow(150);
                                    
            String  longName    =   name.toString(name, 1, 3);
            
           if ( debug  ) {
                Session.logMessageLine(longName);            
                Session.logMessageLine(prefix + "\tRaw Name------->" + longName);
           }
        
            if (  longName.contains("-") ) {
                int     start       = longName.indexOf("-") + 1;
                int     end         = longName.indexOf("-", start+1);
            
                windowName         = longName.substring(start, end).trim();
           
                if ( debug  )
                    Session.logMessageLine(prefix + "\tTable Name------->" + windowName);
                
                // now get the blinds
                int   slash     = longName.indexOf("/", end+1);
                if ( slash > end ) {
                    start   =   end+1;
                    end     =   longName.indexOf("-", slash+1) -1;
                    
                    if ( debug ) {
                        Session.logMessageLine(prefix + "\tSmall Blind: "  +
                            longName.substring(start, slash).trim() );
                    
                        Session.logMessageLine(prefix + "\tBig Blind: "  +
                            longName.substring(slash+1, end).trim() );
                    }
                    
                    smallBlindBet  =   Integer.parseInt(longName.substring(start, slash).trim());
                    bigBlindBet    =   Integer.parseInt(longName.substring(slash+1, end).trim());                    
                }   
            }
        }
        catch ( UnableToReadCharException | NumberFormatException e ) {
            smallBlindBet =   bigBlindBet   =   0;
            throw new UnableToReadCharException(prefix);
        }        
        
        return this;
    }
    
}
