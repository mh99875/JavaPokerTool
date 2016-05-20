/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * The first file created. Holds possibly too much stuff including the functions 
 * used to recognize cards from pixels on the screen.<br>
 * Which could arguably be move to PokerTable but for now stay here.<br>
 * Held the logic for reading cards from the deal before GameManager took over
 * the responsibilities<br>
 * Many useful functions are declared <b>static</b> and many parts of the 
 * application simply call the static functions<br>
 * GetCurrentBoard() which provides the BufferedImage object representing the
 * user's display is probably the most called followed by the pixel analysis
 * routines such as getPixelRedValue() et al<br>
 * 
 * 
 * @author marcushaupt
 * @version     1.0
 * 
 */
public class CardUtilities  {
        
    public static int   WIDTH                           = 12;
    public static int   HEIGHT                          = 14;
    
    public static int   START_COL_DATA_POINTS           = 3;
    public static int   START_ROW_DATA_POINTS           = 2;
    
    public static int   CHECKSUM_DIST                   = 5;
    
    public static int  STATE_CARDS_UNKNOWN              = 5;    
    public static int  STATE_BOARD_EMPTY                = 10;
    public static int  STATE_FLOP_AVAILABLE             = 20;
    public static int  STATE_TURN_AVAILABLE             = 30;
    public static int  STATE_RIVER_AVAILABLE            = 40;
 
    public static int   COLOR_BLACK         = 1;
    public static int   COLOR_RED           = 2;
    public static int   COLOR_UNKNOWN       = -1;
    
    int                 dealState           =   STATE_CARDS_UNKNOWN;
    
    boolean             riverSeen           =   false;
    boolean             turnSeen            =   false;
    boolean             flopSeen            =   false;
   
    private final   Card []     boardCards          =   new Card[5];
    private final   Card []     holeCards           =   new Card[18]; // the max number of players, 2 cards each
    
    int                 hcLeftEdge          =   0;
    int                 hcTopEdge           =   0;
    int []              holeCardChecksum    =   new int[100];
    boolean             handFolded          =   false;
    
    BufferedImage       board;
    
    ReplayPokerWindow   pts;            
       
    
    /**
     * At some point this CTOR will go away and CardUtilities will be stateless
     * and purely static, probably in version 1.0 as the vestigial state-ness
     * is not breaking anything at this point
     * 
     * @param rpw Replay Poker Window
     */
    public CardUtilities(ReplayPokerWindow rpw) 
    {
        
        String  prefix  =    this.getClass().getSimpleName() + ":CTOR(" + rpw.screenNum() + ")";
        
        int i;
         
        this.pts                        =   rpw;
        hcLeftEdge                      =   0;
        hcTopEdge                       =   0;
        
        for (i=0;i<100;i++)
            holeCardChecksum[i] = 0;
           
        for ( i=0;i<5;i++) {
            boardCards[i]       = null;
        }
        
        for (i=0;i<18;i++) {
            holeCards[i]        = null;
        }    
    }
    
    public  Card    flopCard(int fc) {
        if ( (fc<0) || (fc>2) )
            return null;
        
        return  new Card(boardCards[fc]);
    }
    
    public  Card    turnCard() {
        return  new Card(boardCards[3]);
    }
    
    public  Card    riverCard() {
        return  new Card(boardCards[4]);
    }
    
    private void  setDealState(int state) {
        dealState = state;
    }
    
    public int  getDealState() {
    
        return dealState;
    }
    
    public  ReplayPokerWindow    pts() {
        return  pts;
    }
    
    public void resetAllCards() {
        riverSeen       =   turnSeen    =   flopSeen    =   false;

                        
        for (int i=0;i<5;i++) {
            boardCards[i]     = null;
        }
        
        for (int i=0;i<18;i++) {
            holeCards[i]      = null;
        }
       
    }
            
     
    public static void printPixelARGB(int i, int j, int pixel) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        
        Session.logMessageLine(i + " " + j + " = " + red + " " + green + " " + blue);
    }
    
    
    public static boolean  isTableGreenPixel(int pixel) {
        int red     = (pixel >> 16) & 0xff;
        int green   = (pixel >> 8) & 0xff;
        int blue    = (pixel) & 0xff;
        
        boolean     tg = false;
        
        if ( (red>10) && (green>20) && (blue>10) )
            if ( green > (1.5 * blue ) )
                tg = true;
        
        
        return tg;
    }
    
     public static boolean  isTableBluePixel(int pixel) {
        int r   = (pixel >> 16) & 0xff;
        int g   = (pixel >> 8) & 0xff;
        int b   = (pixel) & 0xff;
        
        return  ((g > r) && ( b > r * 3 ));
        
    }
    
    
    public  static boolean isGoldPixel(int p) {
        boolean gold = false;
        
        int r       = (p >> 16) & 0xff;
        int g       = (p >> 8) & 0xff;
        int b       = (p) & 0xff;
        
        if ( (r>100) || (g>100)) {
            if ( (r>g) && (g>b) && (r>75) && (g>75) )
                gold = true;
        }
        
        return gold;
    }
    
     public  static boolean isGoldShieldPixel(int p) {
        boolean gold = false;
        
        int r       = (p >> 16) & 0xff;
        int g       = (p >> 8) & 0xff;
        int b       = (p) & 0xff;
        
        if ( (r>120) && (g>120) && (b>120))
            return false;
        
        if ( (r<100) && (g<100) && (b<100))
            return false;
        
        if ( (r>100) || (g>100)) {
            if ( (r>g) && (g>b) && (r>75) && (g>75) )
                gold = true;
        }
        
        return gold;
    }
     
     public  static boolean isBrightGoldShieldPixel(int p) {
        
        int r       = (p >> 16) & 0xff;
        int g       = (p >> 8) & 0xff;
        int b       = (p) & 0xff;
        
        return ( (r>200) && (g>200) && (b<175) );
    }
     
    
    public  static boolean isGrayPixel(int p) {
        boolean gray = false;
        
        int r       = (p >> 16) & 0xff;
        int g       = (p >> 8) & 0xff;
        int b       = (p) & 0xff;
        
        if ( (r>125) && (g>125) && (b>125)) {
            if ( (r<200) && (g<200) && (b<200)) {
                gray    =   true;
            }
        }
        
        return gray;
    }
    
    
    public  static  boolean AllRGBBelowValue(int value, int p ) {
        boolean below   =   false;
        
        int r       = (p >> 16) & 0xff;
        int g       = (p >> 8) & 0xff;
        int b       = (p) & 0xff;
        
        
        if ( (r<value) && (g<value) && (b<value) ) 
            below   = true;
        
        return below;
       
    }
    
    public  static  boolean AnyRGBBelowValue(int value, int p ) {
        int r       = (p >> 16) & 0xff;
        int g       = (p >> 8) & 0xff;
        int b       = (p) & 0xff;
        
        return ( (r<value) || (g<value) || (b<value) );
    }
    
    public  static  int AverageRGB(int p ) {
        int r       = (p >> 16) & 0xff;
        int g       = (p >> 8) & 0xff;
        int b       = (p) & 0xff;
        
        return (r + g + b ) / 3;
    }
    
    public  static boolean AllRGBAboveValue(int value, int p ) {
        boolean above   =   false;
        
        int r       = (p >> 16) & 0xff;
        int g       = (p >> 8) & 0xff;
        int b       = (p) & 0xff;
        
        
        if ( (r>value) && (g>value) && (b>value) ) 
            above   = true;
        
        return above;
    }
    
        public  static boolean AllRGBWithinRange(int upper, int lower, int p ) {
        
            int u, l;
            
            if ( upper > lower ) {
                u   =   upper;
                l   =   lower;
            } else {
                l   =   upper;
                u   =   lower;
            }
            
            return ( AllRGBAboveValue(l,p) && AllRGBBelowValue(u,p) );
            
        }

    
    
    
    
    
    public  static boolean isDarkShieldPixel(int p) {
        boolean dark = true;
        
        int r       = (p >> 16) & 0xff;
        int g       = (p >> 8) & 0xff;
        int b       = (p) & 0xff;
        
        if ( (r>100) || (g>100) || (b>100)) {
                dark = false;
        }
        
        return dark;
    }
    
     public  static boolean isDarkShieldPixelB(int p) {
        boolean dark = true;
        
        int r       = (p >> 16) & 0xff;
        int g       = (p >> 8) & 0xff;
        int b       = (p) & 0xff;
        
        if ( (r>750) || (g>750) || (b>750)) {
                dark = false;
        }
        
        return dark;
    }
    
      public  static boolean  isHalfDarkOrMore (int p) {
          
        boolean halfdark = false;
        
        int r       = (p >> 16) & 0xff;
        int g       = (p >> 8) & 0xff;
        int b       = (p) & 0xff;
        
        int nDark   =   0;
        
        if ( r < 100 )  nDark++;
        if ( g < 100 )  nDark++;
        if ( g < 100 )  nDark++;
        
        if ( nDark >= 2)
            halfdark    =   true;
        
        return halfdark;
        
      }
    
    public static boolean isRedCardBackPixel(int pixel) {
        
        int red     = (pixel >> 16) & 0xff;
        int green   = (pixel >> 8) & 0xff;
        int blue    = (pixel) & 0xff;
        
        return ( red > 150 && green < 100  );
    }
    
    
    public static boolean isRedPixel(int pixel) {
        
        int red     = (pixel >> 16) & 0xff;
        int green   = (pixel >> 8) & 0xff;
        int blue    = (pixel) & 0xff;
        
        //if ( red > 130 && green < 90  )
        return ( (red > 100) && (red > green * 1.5) );
    }
    
    
    public static boolean isStrongPixel(int pixel) {
        int r   = (pixel >> 16) & 0xff;
        int g   = (pixel >> 8)  & 0xff;
        int b   = (pixel)       & 0xff;
        
       // strong black
       if ( (r<50) && (g<50) && (b<50) )        return true;
       
       if ( (r == g) && (g == b) && (r<=125))   return true;
                                  
       // strong red
       if ( (r > 0) && ( r > (g*1.7)) )           return true;
       
       return ( (r > 0) && ( r > (g+50)) );
       
        
    }
    
    
    public static boolean isStrongSuitPixel(int pixel)  {
        int r   = (pixel >> 16) & 0xff;
        int g   = (pixel >> 8)  & 0xff;
        int b   = (pixel)       & 0xff;
        
       // strong black
       if ( (r<10) && (g<10) && (b<10) )        return true;
       
       // strong red
       return ( (r > 0) && ( r > (g*2.0)) );
       
    }
            
            
    
    
    public  static boolean isGreyPixel(int p) {
        int r     = (p >> 16) & 0xff;
        int g   = (p >> 8) & 0xff;
        int b    = (p) & 0xff;
        
        if ( isGreenPixel(p) )
            return false;
        
        return ( (r<100) && (g<100) && (b<100) );
    }
    
    
    
    public static boolean isWhitePixel(int pixel) {
        int red     = (pixel >> 16) & 0xff;
        int green   = (pixel >> 8) & 0xff;
        int blue    = (pixel) & 0xff;
        boolean white = false;
        
        if ( (red>225) && (blue>225) && (green >225) ) white = true; 
       
        return white;
    }
    
    
    public static boolean isBlackPixel(int p) {
        int r   = (p >> 16) & 0xff;
        int g   = (p >> 8)  & 0xff;
        int b   = (p)       & 0xff;
        
        
        if ( r>50 ) 
            return false;
        
        if ( g > 50 )
            return false;
        
        return ( b < 50 );
    }
    
    
    public static boolean isDarkBlackPixel(int p) {
        int r   = (p >> 16) & 0xff;
        int g   = (p >> 8)  & 0xff;
        int b   = (p)       & 0xff;
        
        return ( (r<25) && (g<25) && (b<25) );
    }
    
    public static boolean isDarkPixel2(int p) {
        int r   = (p >> 16) & 0xff;
        int g   = (p >> 8)  & 0xff;
        int b   = (p)       & 0xff;
        
        return ( (r<100) && (g<100) && (b<100) );
    }
        
   
    public static boolean isGreenPixel(int pixel) {
        
        int r   = (pixel >> 16) & 0xff;
        int g   = (pixel >> 8) & 0xff;
        int b   = (pixel) & 0xff;
        
        
        if ( g < 50 )       return false;
        if ( g > 150 )      return false;
        
        return !( b > g / 2.0 );     
        
    }
    
     public static boolean isGreenCardBorderPixel(int pixel) {
        
        int g   = (pixel >> 8) & 0xff;
        
        return  (g > 50) && (g<250);
    }
    
    
    public static boolean isAnyGreenPixel(int pixel) {
        
        int r     = (pixel >> 16) & 0xff;
        int g   = (pixel >> 8) & 0xff;
        int b    = (pixel) & 0xff;
        
        
        if ( isWhitePixel(pixel) )      return false;
        
        if ( isBlackPixel(pixel))       return false;
        
        if ( isGreyPixel(pixel))        return false;
        
        if ( g > b + 50 )               return true;     
        
        return ( g > (b*2) );
                
    }
    
    public static boolean isCardWhite(int p) {
        int r   = (p >> 16) & 0xff;
        int g   = (p >> 8)  & 0xff;
        int b   = (p)       & 0xff;
        
        return ( (r>200) && (g>200) && (b>200) );
    }
    
    public static boolean isLightPixel(int p) {
        int r   = (p >> 16) & 0xff;
        int g   = (p >> 8)  & 0xff;
        int b   = (p)       & 0xff;
        
        
        return ( (r>100) && (g>100) && (b>100) );
    }
    
    public static boolean isLightPixel2(int p) {
        int r   = (p >> 16) & 0xff;
        int g   = (p >> 8)  & 0xff;
        int b   = (p)       & 0xff;
        
        return ( (r>105) || (g>105) || (b>105) );
    }
    
    
    public static boolean isDarkPixel(int p) {
        int r   = (p >> 16) & 0xff;
        int g   = (p >> 8)  & 0xff;
        int b   = (p)       & 0xff;
        
        return ( (r<100) && (g<100) && (b<100) );
    }
       
    
    public static String  pixelRGBValues(int pixel) {
        String s = getPixelRedValue(pixel) + " " + 
                   getPixelGreenValue(pixel) + " " + 
                   getPixelBlueValue(pixel);
        return s;
    }
       
    
    public  static boolean isLightGreenPixel(int p) {
    
        boolean result =   false;
        int r   =       (p >> 16) & 0xff;
        int g   =       (p >> 8)  & 0xff;
        int b   =       (p)       & 0xff;
        
        if ( (g>100) && (r<190) && (g<190) && (b<190) ) {
            if ( (g>r+15) && (g>b+15))
                result =    true;
        }
        
        return result;
    }
    
    
    public static int getPixelRedValue(int pixel) {
        return (pixel >> 16) & 0xff;
    }
    
    
    public static int getPixelGreenValue(int pixel) {
        return (pixel >> 8) & 0xff;
    }
    
    
    public static int getPixelBlueValue(int pixel) {
        return (pixel) & 0xff;
    }
    
    public static int pixelRedValue(int pixel) {
        return (pixel >> 16) & 0xff;
    }
    
    
    public static int pixelGreenValue(int pixel) {
        return (pixel >> 8) & 0xff;
    }
    
    
    public static int pixelBlueValue(int pixel) {
        return (pixel) & 0xff;
    }
    
    public String    cardSymbol(Card c) {
        
        String r    = c.getRank().symbol();
        String s    = c.getSuit().symbol();
        
        return r+s;

    }
    
     
    
    public  static void printBufferSection(int x,int y, int w, int h, 
            BufferedImage b, String fName) {
        
        try {            
            File f = new File(fName + ".png");
            ImageIO.write(b.getSubimage( x,  y, w, h), "PNG", f);
        } catch (IOException e) {
            Session.logMessageLine("CardAnalyzer:printBufferSection Failed: "  + e.getMessage());
        }
    }
    
   
    /**
     * 
     * Save Card found as file named
     * c.getRank().toString() + " of " + c.getSuit().toString() + " (offset-1,-1)Found At: (" + x + "," + y + ")";
     * 
     * @param x int
     * @param y int
     * @param b BufferedImage
     * @param c Card
     * @param cd  CardDimensions
     */
    public void saveCardFoundToFile( int x, int y, BufferedImage b, Card c, CardDimensions cd) {

        try { 
        
            Robot robot = new Robot(); 
            
            //x -= 10;
            //y -= 10;
            
            
           
            String s = c.getRank().toString() + " of " + c.getSuit().toString() + " (offset-1,-1)Found At: (" + x + "," + y + ")";
            
            Session.logMessageLine(s);
            
            //Capture image on screen and use image icon to show it
            
            //Rectangle captureSize = new Rectangle(x, y, 100, 100);
            //BufferedImage image = robot.createScreenCapture(captureSize);
            ImageIcon matcharea = new ImageIcon(b.getSubimage( x-1,  y-1, cd.rankWidth *2, cd.rankHeight*2));
            JFrame frame = new JFrame();
            JLabel label = new JLabel(s, matcharea, 0);
            frame.add(BorderLayout.CENTER, label);
            frame.setSize(300, 150);
            frame.setVisible(true);
            
            File f = new File(s + ".png");
            ImageIO.write(b.getSubimage( x-1,  y-1, cd.rankWidth+5, cd.rankHeight+5), "PNG", f);
            
            
        } catch (AWTException | IOException e) {
        }
    }
    

    /**
     * Find the left edge of the rank box for the Card located at CardDimensions
     * 
     * @param cd CardDimensions
     * @param startX starting X for search
     * @param startY starting Y for search
     * @param b BufferedImage
     * @return 
     */
    public  int getRankLeftEdge(CardDimensions cd, int startX, int startY, BufferedImage b) {
        int x, y, s;
        int leftEdge = 0;
          
            for (x = startX; x<startX+10;x++) {
                s=0;
                for (y=cd.rankTopEdge; y<=cd.rankTopEdge+16; y++) {
                    if ( isStrongPixel(b.getRGB(x,y)) ) {
                        s++;
                    }
                }
                if ( s > 0 ) {
                    leftEdge = x;
                    break;
                }
            }
            
            return leftEdge;
    }

    /**
     * Find the right edge of the Rank box for the Card located at 
     * CardDimensions
     * 
     * @param cd CardDimension
     * @param b BufferedImage 
     * @return 
     */
    public  int getRankRightEdge(CardDimensions cd, BufferedImage b) {
        // now move across to the border to the pip area
            int x, y, s;
            int rightEdge = 0;
            
            for (x = cd.leftEdge+5; x<cd.leftEdge+25;x++) {
                s=0;
                for (y=cd.rankTopEdge; y<=cd.rankTopEdge+20; y++) {
                    if ( isStrongPixel(b.getRGB(x,y)) ) {
                        //Session.logMessageLine("Strong (" + x + "," + y + ")" );
                        s++;
                    }
                }
               // Session.logMessageLine("RNKRT X:" + x + " S: " + s);
                if ( s == 0 ) {
                    rightEdge = x-1;
                    break;
                }
            }
            
            return rightEdge;
    }
 
    
    /**
     * 
     * Find the top edge of the rank box located within CardDimensions
     * 
     * @param cd CardDimensions
     * @param startX starting X
     * @param startY starting Y
     * @param b BufferedImage
     * @return 
     */
    public int getRankTopEdge(CardDimensions cd, int startX, int startY, BufferedImage b) {
        int i=0, j=0, x, y, rankTopEdge = 0;
        
        if ( cd.leftEdge <= 0 ) {
            if ( Session.logLevelSet(Session.logType.CARDREAD) ) 
                Session.logMessageLine("Error Setting Top Edge. No Left Edge Found");
        } else {
            
            if ( Session.logLevelSet(Session.logType.CARDREAD) ) 
                Session.logMessageLine("Find Top Edge Starting at " + startX + "," + startY);
            
            int grn;
            
            // move Y until we find the black/green border
            for (y=startY;y>(startY-150); y--) {
                grn = 0;
                for (x=startX;x<startX+15;x++) {
                    if ( isGreenPixel(b.getRGB(x, y)) ) grn++;
                }
                //Session.logMessageLine("Find B/G Border: (" + x + "," + y + ") GRN: " + grn);
                if ( grn > 10 ) {
                    rankTopEdge = y+4;
                    break;
                }
            }
            
        }
          
        return rankTopEdge;
    }
    
    /**
     * What's the bottom edge of the rank portion of the Card located on the
     * screen at CardDimensions?
     * 
     * @param cd CardDimensions
     * @param b BufferedImage
     * @return 
     */
    public int getRankBotEdge(CardDimensions cd, BufferedImage b) {
        int y, x, s;
        int rankBotEdge = 0;
        
        if ( cd.rankTopEdge <= 0 ) {
            return 0;
        }
        
        if ( Session.logLevelSet(Session.logType.CARDREAD) ) 
            Session.logMessageLine("Find Bot Edge at TOP:" +cd.rankTopEdge + " LEFT:" +cd.leftEdge);
        
        // find the row with all whitespace 
        // start ten rows deep into the structure
        for (y=cd.rankTopEdge+10; y<cd.rankTopEdge+50;y++) {    
            s=0;
            for (x=cd.leftEdge+1; x<cd.rightEdge; x++) {
                if ( !isLightPixel(b.getRGB(x,y)) ) {
                    s++;
                }   
            }
            //Session.logMessageLine("Find Bottom Edge: (" + x + "," + y + ") S: " + s);
            if ( s == 0 ) {
                rankBotEdge = y-1;
                break;
            }
        }
   
        return rankBotEdge;
    }
    
    /**
     * Is there a flop on the board?
     * 
     * @param b BufferedImage
     * @return boolean
     * @throws AnchorNotFoundException 
     */
    public boolean flopAvailable(BufferedImage b) 
    throws  AnchorNotFoundException
    {
        return (numCardsOnBoard(b) >= 3);
    } 
    
    
    /**
     * Is the turn card on the screen?
     * 
     * @param b BufferedImage b
     * @return boolean
     * @throws AnchorNotFoundException 
     */
    public boolean  turnAvailable(BufferedImage b ) 
    throws  AnchorNotFoundException
    {
        int n = numCardsOnBoard(b);
        
        if ( Session.logLevelSet(Session.logType.CARDREAD) ) {
            Session.logMessageLine(this.getClass().getSimpleName() + ":turnAvailable\tCards On Board: " + n );
        }
        return (n >= 4);
    }

    /**
     * Is the river card on the board?
     * 
     * @param b BufferedImage
     * @return boolean
     * @throws AnchorNotFoundException 
     */
    public boolean  riverAvailable(BufferedImage b) 
    throws  AnchorNotFoundException
    {
        //printBufferSection(0, 0, 600, 400, b, "River On Board");
        return (numCardsOnBoard(b) == 5);
    }
    
    /**
     * How many card pixels are on the board
     * Stop counting after minPixels are seen
     * 
     * @param b BufferedImage
     * @param minPixels int
     * @return
     * @throws AnchorNotFoundException 
     */
    public int numCardPixelsOnBoard(BufferedImage b, int minPixels) 
    throws  AnchorNotFoundException
    {
        int startX              = pts().windowAnchorX()     +   240;   
        int startY              = pts().windowAnchorY()     +   175;
        int i,j, rd, gr, bl, p;
        int cardColor           =   0;
        boolean green;
        String  prefix          =   this.getClass().getSimpleName() + ":numCardPixelsOnBoard";
        boolean debug           =   Session.logLevelSet(Session.logType.CARDREAD);
        
        for (i=0; i<350; i++) {
            for (j=120; j<150; j++) {
                p   =   b.getRGB(startX+i, startY);
                rd  = pixelRedValue(p);
                gr  = pixelGreenValue(p);
                bl  = pixelBlueValue(p);
            
                green   =   false;
            
                if ( (rd>50) || (gr>50) || (bl>50)) {
                
                    if ( gr > 2*bl )
                        green   =   true;
                }
           
                if ( !green ) 
                    cardColor++;
            }
            
            if ( cardColor > minPixels)
                break;
        }
        
         
        if ( Session.logLevelSet(Session.logType.CARDREAD) ) 
             Session.logMessageLine(prefix + "\tCard Color = " + cardColor );
         
         return cardColor;
    }
    
    public boolean spaceBelowCardsIsEmptyWhy(BufferedImage b) 
    throws  AnchorNotFoundException
    {
        String  prefix  =   this.getClass().getSimpleName() + ":spaceBelowCardsIsEmpty";
        
        int startX      = pts().windowAnchorX()+150;   
        int startY      = pts().windowAnchorY()+210;
        int i,j, filled=0;
        
        for (i=0; i<200; i++)
            for (j=0; j<10; j++) 
                if ( isWhitePixel(b.getRGB(startX+i, startY+j)) ||
                     isBlackPixel(b.getRGB(startX+i, startY+j)) )
                    //if ( debug ) Session.logMessageLine(prefix + "\t(" + (startX+i) + "," + (startY+j) + ") = " + pixelRGBValues(b.getRGB(startX+i, startY+j)));
                    filled++;
        
        if ( Session.logLevelSet(Session.logType.CARDREAD) ) 
            Session.logMessageLine(prefix + "\t" + filled);
        
        return ( filled == 0 );
    }
    
    
    
    public  static boolean isBoardCardPixel(int p) {
        
        int r,g,b;
        
        r   =   getPixelRedValue(p);
        g   =   getPixelGreenValue(p);
        b   =   getPixelBlueValue(p);

        return (r >= g) || (r >= 100);
    }
    
    
    
    /**
     * Is the board free of cards?
     * 
     * @param screenAnchor Point
     * @param b BufferedImage
     * @return 
     */
    public  static  boolean boardIsEmpty(Point screenAnchor, BufferedImage b) {
                
        int startX      = screenAnchor.x;   
        int startY      = screenAnchor.y+147;
        int i,j;
        int colorFlop   =   0;
        
        // check the flop
        for (i=125; i<250; i++) {
            for ( j = 0; j<10; j++) {
                if ( CardUtilities.isBoardCardPixel(b.getRGB(startX+i, startY+j)) )
                    colorFlop++;
            }
        }
        
        if ( Session.logLevelSet(Session.logType.CARDREAD) ) 
            Session.logMessageLine("CardAnalyzer:boardIsEmpty = " + colorFlop);
        
        return colorFlop < 50 ;
    }
    
    
    public  int numCardsOnBoard(BufferedImage b) 
    throws AnchorNotFoundException
    {
        if ( pts().getWindowAnchorCopy() == null )
            throw   new AnchorNotFoundException("CardUtilities:numCardsOnBoard(BufferedImage b)");
        
        return  numCardsOnBoard(pts().windowAnchorX(), pts().windowAnchorY()+147, b);
    }
    
    
    static int numCardsOnBoard(int startX, int startY, BufferedImage b) 
    throws  AnchorNotFoundException
    {
        
        String  prefix  =  "CardUtilities:numCardsOnBoard(x,y,b)";
        
        if ( (startX <=0) || (startY<=0) )
            throw   new AnchorNotFoundException("CardUtilities:numCardsOnBoard(BufferedImage b)");
        
        int i,j;
        int colorFlop   =   0;
        int colorTurn   =   0;
        int colorRiver  =   0;
        int numCards;
        int MIN_WHITE   =   150;
        
        // check the flop
        for (i=125; i<250; i++) {
            for ( j = 0; j<10; j++)
                if ( isBoardCardPixel(b.getRGB(startX+i, startY+j)) )
                    colorFlop++;
        }
        
        // check the turn
        for (i=300; i<325; i++) {
            for (j=0; j<10; j++) {
                //Session.logMessageLine(prefix + "\t(" + i + "," + j + ") = " + 
                //        pixelRGBValues(b.getRGB(startX+i, startY+j)));
                
                if ( isBoardCardPixel(b.getRGB(startX+i, startY+j)) )
                    colorTurn++;
            }
                
        }
        
        // check the river
        for (i=350; i<400; i++) {
            for (j=0; j<10; j++) 
                if ( isBoardCardPixel(b.getRGB(startX+i, startY+j)) )
                    colorRiver++;
        }
           
        
        
        numCards    =   0;
        if ( colorFlop >= MIN_WHITE*3 ) {
            numCards    =   3;
            
            if ( colorTurn >= MIN_WHITE ) {
                numCards    =   4;
                
                if ( colorRiver >= MIN_WHITE  ) {
                    numCards    =   5;
                }
            }      
        }
        
        if ( Session.logLevelSet(Session.logType.CARDREAD) ) {
            Session.logMessageLine(prefix + "\tnumCards: " + numCards + " " +
                    "FLOP: "    + colorFlop    +   "\t" +
                    "TURN: "    + colorTurn     +   "\t" +
                    "RIVER: "   + colorRiver);
        }
                
        return numCards;
        
    }
    
   
    
    /**
     * read board card number Num
     * 
     * @param num card number
     * @param b BufferedImage
     * @return
     * @throws AnchorNotFoundException 
     */
    public Card readBoardCard(int num, BufferedImage b) 
    throws  AnchorNotFoundException
    {
        
        int i, j, x, y;
        int startX, startY;
        
        String  prefix      =   this.getClass().getSimpleName() + ":ReadBoardCard[" + num + "]";
        boolean debug       =   Session.logLevelSet(Session.logType.CARDREAD);
        
        CardDimensions  cd  =   new CardDimensions();
        
        startX = pts().windowAnchorX();
        startY = pts().windowAnchorY()    +   130;

        boardCards[num] =   null;
        
       if ( debug )  {
            Session.logMessageLine(prefix + "\tSession Anchor: " 
                    + "(" + pts().windowAnchorX() + "," + pts().windowAnchorY() + ")");

            Session.logMessageLine(prefix + 
                "Start Search For Card Edges @(" + startX + 
                "," + startY + ")");
        }
        
        // let's do everything from scratch
        // find how many green-black edges there are
        boolean cardStartFound = false;
        
        if ( debug ) 
            Session.logMessageLine(prefix + "\tLook for Card Start");
        
        
        switch ( num ) {
            case 0:
                startX      += 110;
                break;
                
            case 1:
                startX      += 160;
                break;
                
            case 2:
                startX      += 220;
                break;
                
            case 3:
                startX      += 280;
                break;
                
            case 4:
                startX      += 340;
                break;
        }
        
        for (i=0; i<30; i++) {
            int line    =   0;
            for (j=0; j<20; j++) {
                if ( isGreenCardBorderPixel(b.getRGB(startX,   startY+j)) 
                  && isBlackPixel(b.getRGB(startX+1, startY+j)) 
                  && isWhitePixel(b.getRGB(startX+2, startY+j))
                        ) {
                    line++;
                }
            }
            startX++;

            
           if ( debug ) 
               Session.logMessageLine(prefix + "\tCARD[" + num + "] StartX = " +
                    startX + " LINE: " + line);

            // we've found the card
            if ( line == 20 ) {

                if ( debug ) 
                    Session.logMessageLine(prefix + "Card = " + num 
                            + " @ X = "  + (startX));


                cardStartFound  =   true;                        

                break;
            } // end if we found 20 in a row
        } // end for up to 30 pixels
            
        if ( !cardStartFound ) {
            if ( debug ) 
                Session.logMessageLine(prefix + "\tCard Start Not Found");
            return null;
        }
        
        cardStartFound  =   false;
        // find the black and white border
        for (i=0;i<5;i++) {
            int c1 = b.getRGB(startX+i,     startY);
            int c2 = b.getRGB(startX+i+1,   startY);
            
            if ( isBlackPixel(c1) && isWhitePixel(c2) ) {
               if ( debug)  Session.logMessageLine(prefix + "\tCard Data Start at " + (startX+i+2));
                cardStartFound = true;
                break;
            }
        }
        
        if ( debug )  {
            if ( !cardStartFound ) {
                Session.logMessageLine(prefix + "\tCard Start Not Found from (" 
                        + (startX+i) + "," + startY);
            }
        }
                    
        
        if ( cardStartFound ) {
            
            if ( !isLightPixel(startX+i+2)) {
                if ( debug ) 
                    Session.logMessageLine(prefix + "\tLight Pixel at i+2: " + (startX+i+2));
                cd.leftEdge = startX+i+2;
            } else if ( isLightPixel(startX+i+3)) {
                if ( debug ) 
                    Session.logMessageLine(prefix + "\tLight Pixel at i+3: " + (startX+i+3));
                cd.leftEdge = startX+i+3;
            } else {
                if ( debug ) 
                    Session.logMessageLine(prefix + "\tCardStart Invalid. Returning");
               return null;
            }
                                
            //find card boundaries and read data
            cd.leftEdge          = startX+i+3;
            cd.leftBlackLine     = cd.leftEdge;
            
            if ( debug ) 
                Session.logMessageLine(prefix + "\tRANK LEFT EDGE: " + (startX+i+3));
        
            cd.rankTopEdge = getRankTopEdge(cd, (startX+i+2), startY, b);
            if ( 0 == cd.rankTopEdge ) {
                return null;
            }
            if ( debug ) Session.logMessageLine(prefix + "\tRANK TOP EDGE\t" + cd.rankTopEdge);
            
            
            cd.rightEdge = getRankRightEdge(cd, b);
            if ( 0 == cd.rightEdge ) {
                return null;
            }
            if ( debug ) Session.logMessageLine(prefix + "\tRANK RIGHT EDGE\t" + cd.rankTopEdge);
            
            
            cd.rankBotEdge = getRankBotEdge(cd,  b);
            if ( 0 == cd.rankBotEdge ) {
                return null;
            }
            if ( debug ) Session.logMessageLine(prefix + "\tRANK BOT EDGE\t" + cd.rankTopEdge);

            
            cd.rankHeight    = (cd.rankBotEdge-cd.rankTopEdge)+1;
            cd.rankWidth     = (cd.rightEdge-cd.leftEdge)+1;
            
            
            if ( (cd.leftEdge == 0) || (cd.rightEdge==0) || (cd.rankTopEdge==0) || (cd.rankBotEdge==0) ) {
                Session.logMessageLine(prefix + "\tCan't Find Card Boundaries");
                Session.logMessageLine(prefix + "\tTop "   + cd.rankTopEdge);
                Session.logMessageLine(prefix + "\tBot "   + cd.rankBotEdge);
                Session.logMessageLine(prefix + "\tLeft "  + cd.leftEdge);
                Session.logMessageLine(prefix + "\tRight " + cd.rightEdge);
                
            } else {
                if ( debug ) 
                    Session.logMessageLine(prefix + "\tTE: " + cd.rankTopEdge + " BE: " + cd.rankBotEdge +
                    " LE: " + cd.leftEdge + " RE: " + cd.rightEdge);
            
                                
                Card.Rank rank          = readRankUsingGrid(num, cd, b);
                
                Card.Suit suit          = findSuitByMatrix( num, cd,  b);

                if ( (rank!= null) && (suit != null) ) {
                    boardCards[num]         = new Card(rank, suit);
                  
                    if ( debug ) 
                        Session.logMessageLine(prefix + "\tFOUND [" + num+ "] @  " + startX + " : "  +
                            boardCards[num].getRank().toString() + " OF " + 
                            boardCards[num].getSuit().toString()  );
                }
            }
             
        } 
        
        return boardCards[num];
    }
    
    
    /**
     * 
     * Find the suit using a matrix of filled pixels as a map
     * 
     * @param n card number
     * @param cd card dimensions
     * @param b BufferedImage
     * @return 
     */
    public  Card.Suit findSuitByMatrix(int n, CardDimensions cd, BufferedImage b) {
       
        String          prefix      =   this.getClass().getSimpleName() + ":findSuitByMatrix[" + n + "]";
    
        Card.Suit        suit        =   Card.Suit.UNKNOWN;
        int             x,y,i,j;
        boolean [][]    matrix  =   new boolean[17][17];
        int []          columns =   new int[17];
        int []          rows    =   new int[17];
        int             startY  =   0;
        int             startX  =   0;
        int             clr;
        int             color   =   -1;
        
        boolean debug           =  Session.logLevelSet(Session.logType.CARDREAD);
                        
        for (x=0;x<17;x++)
            for ( y=0;y<17;y++)
                matrix[x][y]    =   false;

        if ( debug ) {
            Session.logMessageLine(prefix + "\tRank Top Edge: "     + cd.rankTopEdge);        
            Session.logMessageLine(prefix + "\tRank Bottom Edge: "  + cd.rankBotEdge);
            Session.logMessageLine(prefix + "\tRank Left Edge: "    + cd.leftEdge);
            Session.logMessageLine(prefix + "\tRank Right Edge: "   + cd.rightEdge);
            
        }
        
        
        
        // find the first y with a non-white
        for ( y = cd.rankBotEdge+1; y < (cd.rankBotEdge+20); y++) {
            for (x=cd.leftEdge+1; x < cd.rightEdge-1;x++) {
                clr =   b.getRGB(x,y);
                if (debug ) Session.logMessageLine(prefix + "\tRGB(" +x + "," + y + ") = " +  pixelRGBValues(clr));
                if ( isDarkPixel(clr) || isRedPixel(clr) ) {
                    if ( color < 0 ) {
                        if ( isRedPixel(clr) ) 
                            color   =   0;
                        else
                            color   =   1;
                    }
                    startY  =   y;
                    break;
                }
            }
                
            if ( startY > 0 )
                break;
        }
        
        int notdata;
        for (y=startY; y<startY+10; y++) {
            notdata=0;
            j=0;
            
            for (x=cd.leftEdge+1; x < cd.rightEdge-1;x++) {
                j++;
                clr =   b.getRGB(x,y);
                if ( !isDarkPixel(clr) && !isRedPixel(clr) ) 
                    notdata++;
            }
            if ( debug ) Session.logMessageLine(prefix + "\tnotdata pixel at " +  j + 
                    " at " + y + " = " + notdata);
            if ( notdata == j ) {
                startY  =   y+1;
            }
        }
        
        if ( debug ) {
            if ( color == 0 )
                Session.logMessageLine(prefix + "\tCOLOR = RED");
            else
                Session.logMessageLine(prefix + "\tCOLOR = BLACK");
                
        }
        
        
        
        // find the first x with a non-white
        for (x=cd.leftEdge+1; x < cd.rightEdge-1;x++) {
            for (y=startY;y<startY+17;y++) {
                clr =   b.getRGB(x,y);
                if ( isDarkPixel(clr) || isRedPixel(clr) ) {
                        startX  =   x;
                        break;
                }
            }
            if ( startX > 0)
                break;
        }
        

        for (i=0;i<13;i++) {
            for (j=0;j<17;j++) {
                clr =  b.getRGB(startX+i,startY+j);
                if ( isDarkPixel(clr) || isRedPixel(clr) ) {
                    matrix[i][j]    =   true;
                    columns[i]++;
                    rows[j]++;
                }
            }
        }
        
        if ( debug ) {
            for (i=0;i<17;i++){
                for (j=0;j<17;j++) {
                    if ( matrix[j][i] )
                        Session.logMessageChar("#");
                    else
                        Session.logMessageChar(" ");
                }
                Session.logMessageChar("\n");
            }
        
        
            if ( Session.logLevelSet(Session.logType.CARDREAD) ) {
                for (i=0;i<17;i++)
                    Session.logMessageLine(prefix + "\tColumns[" + i + "] = " + columns[i]);

                for (i=0;i<17;i++)
                    Session.logMessageLine(prefix + "\tRows[" + i + "] = " + rows[i]);
            }
        }
        
        boolean inccol  =   true;
        boolean incrow  =   true;
        int     nCols   =   0;
        int     nRows   =   0;
        
        for (i=0;i<17;i++) {
            if ( rows[i] > 0)
                nRows++;
            else
                break;
        }
        
        for (i=0;i<17;i++) {
            if ( columns[i] > 0)
                nCols++;
            else
                break;
        }
        
        // clubs    15x13
        // diamond  14x11, 14x12
        // spade    14x12
        // heart    14x12, 11x12
        
             
        if ( Session.logLevelSet(Session.logType.CARDREAD) )  {
            Session.logMessageLine(prefix + "\tNUM ROWS: " + nRows);
            Session.logMessageLine(prefix + "\tNUM COLS: " + nCols);
        }
        
        if ( rows[0] == 6 ) {
            
                suit            =   Card.Suit.HEART;
        }
        else    if ( nRows >=15 ) {
                
                suit            =       Card.Suit.CLUB;  
                
                if ( color == 0 )
                    suit        =   Card.Suit.DIAMOND;
        } 
        else    if ( color == 0 ) {
            
                suit            =       Card.Suit.DIAMOND;
        } 
        else    if ( color == 1 ) {
            
                suit            =   Card.Suit.SPADE;
        }
        
        if ( Session.logLevelSet(Session.logType.CARDREAD) ) {
            Session.logMessageLine(prefix + "\tSuit = " + suit.toString());
        }
        
        
        return suit;
    }
    
    
    /**
     * Trace the edge of the card and find features based on how and where the
     * trace of the card differs from a perfect rectangle
     * 
     * @param num card number referring to it's position on the board
     * @param cd card dimensions
     * @param b BufferedImage
     * @return 
     */
    public Card.Rank readRankUsingGrid(int num, CardDimensions cd, BufferedImage b) {
        
        Card.Rank         rank;
                
        String      prefix          =   this.getClass().getSimpleName() + ":readRankByIndents[" + num + "]";
        boolean     debug           =   Session.logLevelSet(Session.logType.CARDREAD);
        
        if ( debug  ) {
            Session.logMessageLine(prefix + "\tStart Reading Data At " +
            "TE: " + cd.rankTopEdge + " " + 
            "BE: " + cd.rankBotEdge + " " +
            "LE: " + cd.leftEdge + " " +
            "RnkHgt: " + cd.rankHeight + " " + 
            "RnkWdh: " + cd.rankWidth );
        
       
            Session.logMessageLine(prefix + "\tRead from (" + cd.leftEdge + "," + cd.rankTopEdge + ") " + 
                " for " + cd.rankHeight + " Rows and " + cd.rankWidth + " columns" );
        }
        
        if ( cd.rankHeight <= 10 ) 
             cd.rankHeight  = 14;
                 
        RGBGridCardRank grid = new RGBGridCardRank(b, cd.leftEdge, cd.rankTopEdge, cd.rankHeight, cd.rankWidth);
        
        if ( debug  )
            Session.logMessageLine(prefix + "\tCreated the Grid");
        
        grid.fillGridAnyBelow(225);

        try {
            if ( debug  )
                Session.logMessageLine(prefix + "\tCalling Rank From Grid");
            
            return grid.rankFromGrid();
        }
        catch (UnableToReadCharException e ) {
            return Card.Rank.UNKNOWN;
        }
        
        
    }
   
    
    
   /**
    * 
    * @deprecated old code
    * @param b
    * @return 
    */
    public Point findBoardAnchor(BufferedImage b) {
         
            Point   anchor      = new Point();
            boolean foundAnchor = false;
            int x,y,i,j;

            // read across the screen instead of up and down, this way the same anchor card is found 
            // for 6-handed as for 9-handed
           
            // find where the flop starts
            // across the screen by columns
           
            for (x=0;x<500 && !foundAnchor;x++) {
                for (y=0;y<500;y++) {
                   
                    if (    isGreenPixel(b.getRGB(x,y)) &&
                            isGreenPixel(b.getRGB(x-1,y-1)) &&
                            isGreenPixel(b.getRGB(x+1,y-1)) &&
                            isGreenPixel(b.getRGB(x-1,y)) &&
                            isGreenPixel(b.getRGB(x+1,y)) &&
                            isGreenPixel(b.getRGB(x+1,y+1)) &&
                            isGreenPixel(b.getRGB(x,y+1)) &&
                            isGreenPixel(b.getRGB(x+1,y+1)) ) {
                    
                            foundAnchor = true;
                            for (i=0;i<80;i++) {
                                for (j=0;j<80;j++)
                                    if ( !isGreenPixel(b.getRGB(x+i,y+j)) ) {
                                        foundAnchor = false;
                                        break;
                                    }
                            }   
                             
                            if ( foundAnchor ) {
                                
                                anchor.x = x+=55; //card width
                                anchor.y = y+ 45; //  a card height +10
                                
                                if ( Session.logLevelSet(Session.logType.CARDREAD))  
                                    Session.logMessageLine("Found Table Felt at (" + anchor.x + "," 
                                        + anchor.y + ")");
                                break;
                            }
                    }    
                }
            }
                    
          if (!foundAnchor) {
              Session.logMessageLine("No Felt Found");
          }  
          
          
          return anchor;
    }
   
   
    /**
     * To be moved to PokerTable
     * 
     * @return boolean
     * @throws AnchorNotFoundException 
     */
    public Card readRiverCard() 
    throws  AnchorNotFoundException
    {
        boolean foundRiver  =   false;
        String  prefix      =   this.getClass().getSimpleName() + ":readRiverCard";
        boolean debug       =   Session.logLevelSet(Session.logType.CARDREAD);
        
        if ( debug ) Session.logMessageLine(prefix + "\tWaiting for River");
       
         
        boardCards[4]   =   null;
        try {
            BufferedImage b = getCurrentBoard();

            readBoardCard(4, b);

            while ( boardCards[4] == null ) {
                
                    Robot robot = new Robot(); 
                    robot.delay(500);
                    
                    b = getCurrentBoard();

                    if ( numCardsOnBoard(b) == 0)
                        break;

                    readBoardCard(4, b);
            } 

            if ( boardCards[4] != null ) {
                setDealState(STATE_RIVER_AVAILABLE);
                foundRiver = true;
            }
            else
            {
                Session.logMessageLine(prefix + "\tRiver Card Unrecognized.");
            }

        } catch ( AWTException e ) {
        }        
        
        return new Card(boardCards[4]);
    }
    
    
    public Card readTurnCard() 
    throws  AnchorNotFoundException
    {
        boolean foundTurn   =   false;
        boolean debug       =   Session.logLevelSet(Session.logType.CARDREAD);
        String  prefix      =   this.getClass().getSimpleName() + ":treadTurnCard";
        
        // the turn is also a race condition
        
        boardCards[3]   =   null;
        int iter    =   0;
        while ( true ) {
            try {
                BufferedImage b = getCurrentBoard();
                Robot robot = new Robot(); 
                robot.delay(500);

                if ( numCardsOnBoard(b) < 4) {
                    setDealState(STATE_BOARD_EMPTY);
                    return null;
                }
                
                if ( debug ) Session.logMessageLine(prefix + "\tReading Turn. Iter[" + iter++ + "[");
                readBoardCard(3, b);
                                
                if ( boardCards[3] != null ) {
                     
                    if ( debug ) Session.logMessageLine(prefix + "\tTurn Seen");
                    setDealState(STATE_TURN_AVAILABLE);
                    foundTurn = true;
                    break;
                }
                else 
                    robot.delay(500);
                        
                if ( iter > 4 ) {
                    readBoardCard(3, b);
                    break;
                }
            } catch ( AWTException e ) {
            }
        }
        
        return new Card(boardCards[3]);
    }
    
    
    
    
    public void displayAllBoardCards( ) {
        
        int i;

        String  prefix  =   this.getClass().getSimpleName() + ":displayAllBoardCards";

        for (i=0;i<18;i++) {
            if ( holeCards[i] != null ) {
                Session.logMessageLine(prefix + "\tHole" + i + ": " + 
                        holeCards[i].getRank().toString() + "\t"  + holeCards[i].getSuit().toString());
            }
        }

        String  name    =   "";
        for (i=0;i<5;i++) {
            if (i==0)   name    =   "Flop1";
            if (i==1)   name    =   "Flop2";
            if (i==2)   name    =   "Flop3";
            if (i==3)   name    =   "Turn ";
            if (i==4)   name    =   "River";

            if ( boardCards[i] != null )
                Session.logMessageLine(prefix + "\t" + name + ": " + boardCards[i].getRank().toString() + "\t" + boardCards[i].getSuit().toString());
        }
        

    }
    
    
    
    
    
    public boolean  holeCardsAreFolded() {
        BufferedImage b = getCurrentBoard();
        String prefix = this.getClass().getSimpleName() + ":holeCardsAreFolded";
        
        boolean folded      =   true;
        int     x           =   hcLeftEdge  +5;
        int     y           =   hcTopEdge   +5;
        int     i;
        int     j;
        int     clr;
        int     liveColor   =   0;
        boolean debug       =   Session.logLevelSet(Session.logType.CARDREAD);
        
        for (i=0;i<10;i++) {  
            for (j=0;j<10;j++) {  
            
                clr =  b.getRGB(x+i,y+j);
                
                if (    (getPixelRedValue(clr)    < 100)
                    ||  (getPixelGreenValue(clr)  < 100)
                    ||  (getPixelBlueValue(clr)   < 100) ) {
                
                    liveColor++;
                }
            }
        }
        
        if ( debug )  {
            Session.logMessageLine(prefix + "\tSearch from " + hcLeftEdge + "\t" + hcTopEdge);
            Session.logMessageLine(prefix + "\tLive Color = " + liveColor);
        }
        
        if ( liveColor > 10 )
            folded = false;
           
        return folded;
    }
    
    
    public boolean  newFlopCardsFromChecksum() {
        boolean changed;
        int i       =   0, x,  y;
        int matched =   0;
        
        BufferedImage b = getCurrentBoard();
        
        // if the hole cards are folded, ignore
        
        
        
        for (x=hcLeftEdge+5;x<hcLeftEdge+5+CHECKSUM_DIST;x++)
            for (y=hcTopEdge+5;y<hcTopEdge+5+CHECKSUM_DIST;y++) {
                int clr = b.getRGB(x,y);
                
                if (  (Math.abs(getPixelRedValue(holeCardChecksum[i])   - getPixelRedValue(clr))    < 5) 
                  &&  (Math.abs(getPixelGreenValue(holeCardChecksum[i]) - getPixelGreenValue(clr))  < 5)
                  &&  (Math.abs(getPixelBlueValue(holeCardChecksum[i])  - getPixelBlueValue(clr))   < 5) ) {
                        
                    matched++;
                }
                i++;    
        }
        
        //if ( DEBUG_MODE ) Session.logMessageLine("CardUtilities\tnewFlopCardsFromChecksum: matched " + matched);
        
        double pctChanged = (matched * 1.0)/(i * 1.0);
       
        /*if ( DEBUG_MODE ) Session.logMessageLine("CardUtilities\tnewFlopCardsFromChecksum Matched: " + matched + 
                        " out of " + i + 
                        " Pct Changed:" + pctChanged);*/

        changed = false;
        if ( matched < 15 )
            changed = true;
         
        return changed;
    }
    
    
    
    public boolean readFlopCards() 
    throws  AnchorNotFoundException
    {
        boolean found       =   false;
        String  prefix      =   this.getClass().getSimpleName() + ":readFlopCards";   
        boolean debug       =   Session.logLevelSet(Session.logType.CARDREAD);
                     
        int     startX      =   pts().windowAnchorX() + 140;
        int     startY      =   pts().windowAnchorY() + 130;
        
        int     columns     =   0;
        
        int c, r, n, pixel []     =   {0,0,0};

        try {
            Robot robot = new Robot(); 
            BufferedImage b;

            // we need to see two columns of green signifying the cards are non-overlapping
            
            while ( columns != 2 )  {    
                b = getCurrentBoard();
                columns     =   0;
                for (c=startX; c<pts().windowAnchorX()+275; c++) {
                   pixel[0] =   b.getRGB(c,   startY);
                   pixel[1] =   b.getRGB(c+1, startY);
                   pixel[2] =   b.getRGB(c+2, startY);
                   
                    if ( AllRGBBelowValue(100, pixel[0]) && AllRGBBelowValue(200, pixel[0]) && AllRGBBelowValue(100, pixel[2]) ) {
                        n=0;
                        for (r=1; r<=10; r++) {
                            if ( AllRGBBelowValue(100, b.getRGB(c,   startY+r)) && 
                                AllRGBBelowValue(200, b.getRGB(c+1, startY+r)) && 
                                AllRGBBelowValue(100, b.getRGB(c+2, startY+r)) ) {
                               n++;
                            }
                        }
                        if ( n == 10 ) {
                            if ( debug )
                                Session.logMessageLine(prefix + "\tFound A Column at: " + c);
                            
                            columns++;
                        }
                    }
                }
            }
           
            if ( debug )
                Session.logMessageLine(prefix + "\tFound Two Columns");
            
            
            
            // add another delay just to make sure
            robot.delay(250);
            b   =   getCurrentBoard();
           
            readBoardCard(0, b);
            readBoardCard(1, b);
            readBoardCard(2, b);
            
        }
        catch ( AWTException e) {
            Session.logMessageLine(prefix + "\tRobot Error: " + e);
        }
   
        return found;
    }
    
  
    
    static BufferedImage getCurrentBoard() {
                
        try {
            
            Robot robot = new Robot(); 
            return robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            
         } catch ( AWTException e ) {
             Session.logMessageLine("CardAnalyzer:getCurrentBoard()" + "\tFatal Error: " + e);
             Session.exit(0);
        }
    
        return null;
    }
    
    
    // eventually this function will read the player's hole cards as well
    public Card readHoleCard(int num, int shieldX, int shieldY, BufferedImage b) {
        
        String  prefix  =   this.getClass().getSimpleName() + ":readHoleCard[" + num + "]";
        int     x,y, light;
        int     leftEdgeData1, leftEdgeData2, rightEdgeData1;
        int     rightEdgeData2,topRowData = 0, holeCardAnchor = 0;
        int     rankBotEdge1 =0, rankBotEdge2=0;
        
        boolean debug           =  Session.logLevelSet(Session.logType.CARDREAD);
        
        
        CardDimensions  cd  =   new CardDimensions();
        
        if ( b == null ) {
            Session.logMessageLine(prefix + "\tError. Buffered Image is null");
            return null;
        }
        
        // find this pattern
        // Lt/Grn    Lt/Grn   Lt/Grn   Gray
        // Lt/grn    Gray    Gray    Gray
        // Lt/Grn    Gray    Light   Light
        // Gray      Gray    Light   Light
        
        int clr, i, j, red=0, green =0, blue=0;
        int dark;
        
        if ( debug ) {
            Session.logMessageLine(prefix + "\tShield X: " + shieldX + " ShieldY " + shieldY );
        }
              
        x=shieldX+10;
        for (y=(shieldY-60);y>(shieldY-80);y--) {
            dark=0;
            for (i=0;i<10;i++) {
                if ( isBlackPixel(b.getRGB(x+i, y)) )
                    dark++;
            }
            
            if ( debug ) Session.logMessageLine(prefix + "\tY: " + y + " DARK: " + dark);
            
            if ( dark == 10 ) {
                topRowData = y+2;
                if ( debug ) Session.logMessageLine(prefix + "\tTop Row Found at : " + topRowData);
                break;
            }
        }
                  
        if ( topRowData == 0 ) {
            if ( debug ) {
                Session.logMessageLine(prefix + "\tShieldX:" + shieldX + " ShieldY: " + shieldY);
                Session.logMessageLine(prefix + "\tTopRowData == 0 Exiting");
            }
            
            return null;
        } 
        
        leftEdgeData1   = shieldX           + 2;
        rightEdgeData1  = shieldX           +15;
        leftEdgeData2   = rightEdgeData1    + 10;
        rightEdgeData2  = leftEdgeData2     + 15;
        
        int bottomEdge  = 0;
        int leftEdge;
        int rightEdge;
        
        if ( (num % 2) == 0 ) {
            leftEdge    = leftEdgeData1;
            rightEdge   = rightEdgeData1;
            
            if ( debug )  {
                Session.logMessageLine(prefix + "\tLeft Hole Card Left Edge:" + leftEdge + " RightEdge: " + rightEdge);
            }
            
        } else {
            leftEdge    = leftEdgeData2;
            rightEdge   = rightEdgeData2;
            
            if ( debug )  {
                Session.logMessageLine(prefix + "\tRight Hole Card Left Edge:" + leftEdge + " RightEdge: " + rightEdge);
            }
        }
            
        if ( debug )  {
            Session.logMessageLine(prefix + "\tTop Data Row: " + topRowData );
        }

        
        for (i=topRowData+10;i<topRowData+20;i++) {
            int n   =   0;
            light   =   0;
            
            for (j=leftEdge;j<rightEdge;j++) {
                clr = b.getRGB(j,i);
                //if ( debug ) Session.logMessageLine(prefix + "\tRow[" + i + "] Col [" + j + "] = "  + pixelRGBValues(clr));
                n++;
                if ( isLightPixel(clr) ) 
                    light++;
                else {
                    //if ( debug ) Session.logMessageLine(prefix + "\tRow[" + i + "] Col [" + j + "] = "  + pixelRGBValues(clr));                    
                }
            }
            
            //if ( debug ) Session.logMessageLine(prefix + "\tRow[" + i + "] N:" + n + " Light: " + light);
            
            if ( light == n ) {
                bottomEdge = i;
                break;
            }
            
        }
                
        // what data items need to be set to get rank and suit?
        cd.rankWidth         = (rightEdge-leftEdge)      + 1;
        cd.rankHeight        = (bottomEdge - topRowData) + 1;
        cd.rankTopEdge       = topRowData;
        cd.rankBotEdge       = bottomEdge;
        cd.leftEdge          = leftEdge;
        cd.rightEdge         = rightEdge;
        cd.leftBlackLine     = leftEdge-1;
        
        if ( debug ) {
            Session.logMessageLine(prefix + "\trankWidth: "     + cd.rankWidth);
            Session.logMessageLine(prefix + "\trankHeight: "    + cd.rankHeight);
            Session.logMessageLine(prefix + "\trankTopEdge "    + cd.rankTopEdge);
            Session.logMessageLine(prefix + "\trankBotEdge: "   + cd.rankBotEdge);
            Session.logMessageLine(prefix + "\tleftEdge: "      + cd.leftEdge);
            Session.logMessageLine(prefix + "\trightEdge: "     + cd.rightEdge);
            Session.logMessageLine(prefix + "\tleftBlackLine: " + cd.leftBlackLine);
        }

        //printBufferSection(0, 0, 800, 800, b, "WonkyHoleCard" + num);
        
        Card.Rank rank            = readRankUsingGrid(num, cd, b);
        
        if ( debug ) { 
            Session.logMessageLine(prefix + "\tRank: " + rank.toString());
        }
        
        Card.Suit suit            = findSuitByMatrix( num, cd, b);
        
        if ( debug ) { 
            Session.logMessageLine(prefix + "\tSuit: " + suit.toString());
        }
                
        if ( (rank == Card.Rank.UNKNOWN) || ( rank == null) ) {
            if ( Session.logLevelSet(Session.logType.ERROR) )
                Session.logMessageLine(prefix + "\tError Reading Rank.");
            return  null;
        }
        
        if ( (suit ==  Card.Suit.UNKNOWN) || ( suit == null) ) {
            if ( Session.logLevelSet(Session.logType.ERROR) )
                Session.logMessageLine(prefix + "\tError Reading Suit.");
            return null;
        }
                       
        holeCards[num]  =   new Card(rank, suit);
        
        if ( debug ) 
            Session.logMessageLine(prefix + "\tHole Card Found\t" +
                "Rank: " + holeCards[num].getRank().toString()  + 
                " Suit: " + holeCards[num].getSuit().toString() );
                   
        return  new Card(holeCards[num]);
    }
    

    public boolean readUserHoleCards(BufferedImage b) 
    throws  AnchorNotFoundException
    {
        
        String  prefix  = this.getClass().getSimpleName() + ":readUserHoleCards";
        
        int     x,y,i,j=0, k=0, g=0, w =0, clr =0, len;
        
        boolean found           =   false;
        boolean result;
        boolean debug           =  Session.logLevelSet(Session.logType.CARDREAD);
               
        // find the first black+white boundary above the shield
        // find the last one and see it it's at the proper location
        // for both cards to have been displayed
        
        // start from scracth every time
        hcLeftEdge  =   0;
        
        // find the top vertical black line
        if ( hcLeftEdge == 0 ) {
            y = pts().windowAnchorY();
            int maxlen=0;
            Point maxlenPt = new Point();
            for (x=pts().windowAnchorX()+90;x<pts().windowAnchorX()+120;x++) {
                len=0;
                if ( isBlackPixel(b.getRGB(x,y) ) ) {
                    for (i=0;i<30;i++) {
                        if ( isBlackPixel(b.getRGB(x, y-i)) ) {
                            if ( debug ) Session.logMessageLine(prefix + "\t[" + len + "] @ (" + 
                                    x + "," + (y-i) + ")" );
                            len++;
                        } else
                            break;
                    }

                    if ( debug ) Session.logMessageLine(prefix + 
                            "\tLooking for leftmost verical cards border @ Y=" + y + " len = " + len); 
                        
                    if (len == 30 ) {
                        found = true;
                        hcLeftEdge = x+2;
                        break;
                    } else {
                        if ( len > maxlen ) {
                            maxlen = len;
                            maxlenPt.x = x;
                            maxlenPt.y = y;
                        }
                    }
                }
            }

            if ( !found ) {
                if ( debug ) Session.logMessageLine(prefix + "\ttop vertical line not found.");
                return false;
            }
            
            if ( debug ) Session.logMessageLine(prefix + "\tverical line found @ y = " + y );
            
            // move in a few pixels then move up until we find the vertical black line
            found = false;
            for (;y>50;y--) {
                len=0;
                if ( isBlackPixel(b.getRGB(hcLeftEdge+10, y) ) ) {
                    for (i=0;i<30;i++) {
                        if ( !isWhitePixel(b.getRGB(hcLeftEdge+10+i,y) ) )
                            len++;
                        else 
                            break;
                    }
                    
                    if ( debug ) Session.logMessageLine(prefix + "\tHole Card Top edge Found: Y:" + y + " LEN: " + len);
                    
                    if (len == 30 ) {
                        found = true;
                        hcTopEdge=y+2;
                        break;
                    }
                }
            }
        } // end if left edge not already found
        else {
            found   =   true;
        }
        
        // is data in the row above the top edge?
        for (i=hcLeftEdge; i<hcLeftEdge+15; i++) {
            if ( !isWhitePixel(b.getRGB(i, hcTopEdge-1)) ) {
                hcTopEdge--;
                if ( debug ) Session.logMessageLine(prefix + "\tMoved Top Edge From:" 
                                          + (hcTopEdge+1) + " to " + hcTopEdge);
                break;
            }
        }
                  
        if ( holeCardsAreFolded() ) {
            if ( debug ) Session.logMessageLine(prefix + "\tCards Are Folded");
            return false;
        }
      
        if ( found )  {
            if ( debug ) Session.logMessageLine(prefix + "\tCalling readHoleCards(" + hcLeftEdge + " , " + hcTopEdge + ")");
            result = readHoleCards(0, hcLeftEdge, hcTopEdge, b);
            if ( debug ) Session.logMessageLine(prefix + "\tResult: " + result);
        } else {
             if ( debug ) Session.logMessageLine(prefix + "\tNot Found");
            result  =   false;
        }
        
        try {
            if ( holeCards[0] != null ) {
                if ( holeCards[0].getRank() == Card.Rank.UNKNOWN )
                    holeCards[0] = null;
        
                if ( holeCards[0].getSuit() == Card.Suit.UNKNOWN )
                    holeCards[0] = null;
            }
        
            if ( holeCards[1] != null ) {
                if ( holeCards[1].getRank() == Card.Rank.UNKNOWN )
                    holeCards[1] = null;
        
                if ( holeCards[1].getSuit() == Card.Suit.UNKNOWN )
                    holeCards[1] = null;
            }
        }
        catch ( Exception e) {
            holeCards[0] = holeCards[1] = null;
            result = false;
        }
        
        
        if ( (holeCards[0] == null) || (holeCards[1] == null) )
           result   =   false;
        
        return result;
    }
    
    
    
    public boolean readHoleCards(int seat, int leftEdge, int topEdge, BufferedImage b) {
     
        // seat numbering start from the upper left corner of the table
        String  prefix  =   this.getClass().getSimpleName() + ":readHoleCards";
        boolean found;
        int x,y,i=0,j=0, k=0, g=0, w =0, clr, len;
        int hc=seat*2;
        
        if ( b == null ) {
            Session.logMessageLine(prefix + "\tError. Buffered Inage is null");
            return  false;
        }
        
        int leftEdge1, rightEdge1, leftEdge2, rightEdge2, topEdgeRank1;
        int botEdgeRank1, botEdgeRank2, topEdgeRank2;
        int s = 0, h;
        boolean debug           =  Session.logLevelSet(Session.logType.CARDREAD);
        int leftBlackLine1,leftBlackLine2;

        // set the reference values to the
        // standard before adjusting them for
        // the whitespaces
        leftEdge1       = leftEdge;
        leftBlackLine1  = leftEdge1;
        topEdgeRank1    = topEdge;
        topEdgeRank2    = topEdge;
        rightEdge1      = leftEdge + 14;
        leftBlackLine2  = leftEdge + 23;
        leftEdge2       = leftBlackLine2;
        rightEdge2      = leftEdge2 + 15;

        botEdgeRank1    = 0;
        botEdgeRank2    = 0;

        // find the bottom of the suit/top of rank
        for (y=topEdge+22;y>topEdge;y--) {
            len=0;
            for (x=leftEdge1;x<=rightEdge1;x++) {
                
                clr = b.getRGB(x, y);
                if ( !isLightPixel(clr) )
                    len++;

            }
            //Session.logMessageLine("Find Rank/Suit Border Y: " + y + 
            //        " LEN1: " + len);
            if ( 0 == len ) {
                botEdgeRank1 = y;
                break;
            }
        }
        
        for (y=topEdge+22;y>topEdge;y--) {
            len=0;
            for (x=leftEdge2;x<=rightEdge2;x++) {
                clr = b.getRGB(x, y);
                if ( !isLightPixel(clr) )
                    len++;

            }
            //Session.logMessageLine("Find Rank/Suit Border Y: " + y + 
            //        " LEN2: " + len);
            if ( 0 == len ) {
                botEdgeRank2 = y;
                break;
            }
        }
        
        if ( botEdgeRank2 == 0 ) {
            Session.logMessageLine(prefix + "\tError: BotEdgeRank2 Missing.");
            return false;
        }
            
        boolean data;
        for (y=topEdge+10; y<topEdge+20; y++) {
            data    =   false;
            for (x=leftEdge1;x<=rightEdge1;x++) {
                if ( isStrongPixel(b.getRGB(x,y)))
                    data    =   true;
            }
            if ( !data ) {
                if ( debug ) Session.logMessageLine(prefix + "\tBotEdgeRank1 = " + (y-1) + " vs. " + botEdgeRank1);
                botEdgeRank1    =   y-1;
                break;
            }
        }
        
        for (y=topEdge+10; y<topEdge+20; y++) {
            data    =   false;
            for (x=leftEdge2;x<=rightEdge2;x++) {
                if ( isStrongPixel(b.getRGB(x,y)))
                    data    =   true;
            }
            if ( !data ) {
                if ( debug ) Session.logMessageLine(prefix + "\tBotEdgeRank2 = " + (y-1) + " vs. " + botEdgeRank2);
                botEdgeRank2    =    (y-1);
                break;
            }
        }
        
        // check for rank/suit boxes boundaries being equal to zero
        // Add rank bottom
        // Remove whitespace on top or rank
        // Remove whitespace on left and right side of rank
        // send to find rank/suit
        int top = 0;
        found = false;
        for (y=topEdgeRank1;y<botEdgeRank1 && !found ;y++) {
            for (x=leftEdge1; x<=rightEdge1;x++) {
                clr = b.getRGB(x,y);
                if (!isLightPixel(clr)) {
                    top = y;
                    found = true;
                    break;
                }
            }
        }
        topEdgeRank1 = y;

        top=0;
        found = false;
        for (y=topEdgeRank2;y<botEdgeRank2 && !found ;y++) {
            for (x=leftEdge2; x<=rightEdge2;x++) {
                clr = b.getRGB(x,y);
                if (!isLightPixel(clr)) {
                    top = y;
                    found = true;
                    break;
                }
            }
        }
        topEdgeRank2 = y;


        // print the four boxes we found for ranks and suits
        w = (rightEdge1-leftEdge1) +1;
        h = (botEdgeRank1 - topEdgeRank1) +1;
        //printBufferSection( leftEdge1,      topEdgeRank1, w, h, b, "HoleCards A");
        w = (rightEdge2-leftEdge2) +1;
        h = (botEdgeRank2 - topEdgeRank2) +1;
        
        if ( debug ) {
            Session.logMessageLine("botEdgeRank1:" + botEdgeRank1);
            Session.logMessageLine("topEdgeRank1:" + topEdgeRank1);
            Session.logMessageLine("botEdgeRank2:" + botEdgeRank2);
            Session.logMessageLine("topEdgeRank2:" + topEdgeRank2);
        }
        
        //printBufferSection( leftEdge2,      topEdgeRank2, w, h, b, "HoleCards B");
        w = 16;
        //printBufferSection( leftEdge1,   botEdgeRank1+1, w, 20, b, "HoleCards C");
        //printBufferSection( leftEdge2,   botEdgeRank2+1, w, 20, b, "HoleCards D");

        CardDimensions  cd      =   new CardDimensions();
        cd.leftBlackLine        = leftBlackLine1;
        cd.leftEdge             = leftEdge1;
        cd.rightEdge            = rightEdge1;
        cd.rankTopEdge          = topEdgeRank1;
        cd.rankBotEdge          = botEdgeRank1;
        cd.rankHeight           = (botEdgeRank1-topEdgeRank1)       + 1;
        cd.rankWidth            = (rightEdge1 - leftEdge1)  + 1;

        if ( debug ) {
            Session.logMessageLine(prefix + "\tLeft Edge 1     "    + cd.leftEdge);
            Session.logMessageLine(prefix + "\tRight Edge 1    "    + cd.rightEdge);
            Session.logMessageLine(prefix + "\tRank Top 1      "    + cd.rankTopEdge);
            Session.logMessageLine(prefix + "\tRank Bottom 1   "    + cd.rankBotEdge);
            Session.logMessageLine(prefix + "\tHeight 1        "    + cd.rankHeight);
            Session.logMessageLine(prefix + "\tWidth 1         "    + cd.rankWidth);
        }

        Card.Rank rank      = readRankUsingGrid(0, cd ,b);
        
        Card.Suit suit      = findSuitByMatrix(0, cd,  b);

        holeCards[hc]           =  new Card(rank, suit);    
                   
                
        // CARD 2
        CardDimensions  cd1      =   new CardDimensions();
        cd1.leftBlackLine      = leftBlackLine2;
        cd1.leftEdge           = leftEdge2;
        cd1.rightEdge          = rightEdge2;
        cd1.rankTopEdge        = topEdgeRank2;
        cd1.rankBotEdge        = botEdgeRank2;
        cd1.rankHeight         = (botEdgeRank2-topEdgeRank2)       + 1;
        cd1.rankWidth          = (rightEdge2 - leftEdge2)  + 1;

        if ( debug ) {
            Session.logMessageLine(prefix + "\tLeft Edge 2     "  + cd1.leftEdge);
            Session.logMessageLine(prefix + "\tRight Edge 2    "  + cd1.rightEdge);
            Session.logMessageLine(prefix + "\tRank Top 2      "  + cd1.rankTopEdge);
            Session.logMessageLine(prefix + "\tRank Bottom 2   "  + cd1.rankBotEdge);
            Session.logMessageLine(prefix + "\tHeight 2        "  + cd1.rankHeight);
            Session.logMessageLine(prefix + "\tWidth 2         "  + cd1.rankWidth);
        }

        rank                = readRankUsingGrid(1, cd1, b); 
        
        suit                = findSuitByMatrix(1, cd1, b);
        
        holeCards[hc+1]     = new Card(rank, suit);
        
        if (  (holeCards[hc].getRank()   == Card.Rank.UNKNOWN) ||  ( holeCards[hc].getSuit()   == Card.Suit.UNKNOWN) 
           || (holeCards[hc+1].getRank() == Card.Rank.UNKNOWN) ||  ( holeCards[hc+1].getSuit() == Card.Suit.UNKNOWN) ) {
                
               
            found           =   false;
            holeCards[hc]   =   holeCards[hc+1] =   null;
        }
        else {
        
            if ( debug ) {
                Session.logMessageLine(prefix + "\tHole Card(" + hc     + ") Seat [" + seat + "] " + 
                        holeCards[hc].getRank().toString()    + " of " + holeCards[hc].getSuit().toString());
                Session.logMessageLine(prefix + "\tHole Card(" + (hc+1) + ") Seat [" + seat + "] " + 
                        holeCards[hc+1].getRank().toString()  + " of " + holeCards[hc+1].getSuit().toString());
            }
         
            found           =   true;
                        
        }
      
        return found;
    }
    

    
}
