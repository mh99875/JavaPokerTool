/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 *
 * 
 * <p>class RGBGrid implements Comparable</p>
 * 
 * 
 * <p>a rectangular grid of pixels with a boolean representation used for pattern
 * matching</p>
 * 
 * <p>used to find and numbers on in the replay window
 * subclasses implement their own behavior for determining which range of
 * pixels should been seen as features (grid[x][y] == true) and how to 
 * interpret those features <b>charFromGrid()</b></p>
 
 <p>A grid can be filled multiple times with different RGB ranges or levels. The
 results are stored in int [] f and boolean [][] g but the pixel array is
 never touched unless the user explicitly does this using the get and set
 functions.</p>
 
 <p>the comparable for the case of extracting sub-grids from a larger grid and
 putting them back into their natural order after processing them 
 individually. This is used in the algorithm of iteratively increasing the
 number of pixels seen as features (either lightening or darkening) and 
 selecting the chars found possibly out of order and returning them to their
 natural order later.</p>
 
 
 <p>CTORs:</p>
 
 <p><b>RGBGrid(int nRows, int nCols)</b>
 * Pixels are initialized to 0 and grid to false
 * (no data/no features)</p>
 * 
 * 
 * <p><b>RGBGrid(BufferedImage b, int startX, int startY, int nRows, int nCols)</b>
 * create a grid from a BufferedIage reading data from the upper lefthand 
 * corner of (startX, startY) for nRow and nCols storing the pixels for later
 * analysis</p>
 * 
 * 
 * <p><b>RGBGrid(RGBGrid grid, int sCol, int eCol)</b>
 * Create a RGBGrid that is a sub-grid of a RGBGrid
 * copy all of the setting as well</p>
 * 
 * 
 * <p><b>toString()</b>
 * The Workhorse Function.
 * The major use of RGBGrid is to convert a set of pixels into a String that may
 * be converted to an int in the case of numbers or left as is for words such as
 * the Table Name or a Player Name.</p>
 * 
 * <p>This is done by breaking a larger grid into smaller grids and solving the
 * character hidden in each grid.</p>
 * 
 * <p>This is complicated by:</p>
 * <ul>
 * <li>some characters being split, that is a blank column 
 * separates two parts of a single char. These columns are one pixel wide, small
 * enough that the user will generally not notice or care, but wide enough to
 * break an algorithm that treats blank columns as separators.</li>
 * 
 * <li>some characters being joined. 2,3  or more characters may be fused such
 * that these is no blank column between them.</li>
 * 
 * <li>Characters may be split of joined in numbers and words alike, in any of
 * the RGBGrid contexts, frequently, in normal configurations and at random.</li>
 * </ul>
 * 
 * 
 * <p><b>public  {@literal <T extends RGBGrid>} String  toString(T grid, int minWidth, 
 * int whiteSpace) </b></p>
 * 
 * 
 * <p>the problems involved with identifying letters and numbers on a poker screen
 * are handled by a combination of joining split characters and separating 
 * joined ones.</p>
 * 
 * 
 * <p>the first step is for this function to break the Grid into sub-grid, each
 * representing a feature that may be a complete char, a piece of a split char,
 * or a joined char.</p>
 * 
 * <p><b>public  {@literal <T extends RGBGrid> ArrayList<T>} breakIntoItems(T model, 
 * int minWidth, int whiteSpace)</b></p>
 
 <p>Separates the Grid into individual components based on blank columns 
 (f[x] == 0) an array set whenever the grid if filled (e.g. fillGrid() )</p>
 
 <p>Adds whitespace when indicated by the caller, e.g. when reading a table
 name as opposed to a player's screen name</p>
 
 <p>returns the array of items without transformation or interpretation</p>
 
 <p>using generics and reflection to prevent slicing</p>
 
 
 <p>toString() again</p>
 
 <p>now has an ArrayList of the proper RGBGrid type
 with the correct functions (charFromGrid()) to interpret the data as a 
 String (possibly to be later converted to int)</p>
 
 <p>each element of the list is processed in order, calling charFromGrid. If the
 function throws <b>charTooWideException</b> separateJoinedChars is called,
 * which uses the greedy method to strip chars one by one from the right.</p>
 * 
 *<p>The separated chars are added to the result.</p>
 * 
 * <p>Reading Character Names is the Big Kahuna and as such has it's own special
 * class called <b>PlayerNamesReader</b> to handle it although it uses the 
 * RGBGrid framework to do so.</p>
 * 
 *<p>For historical reasons player names are recognized in a separate class called
 * <b>PlayerNamesReader</b> which uses the RGBGrid framework, functions, etc...
 * and may be merged with <b>toString()</b> at some point.</p>
 * 
 * 
 * 
 * <p>this class provides utilities such as</p>
 * 
 * <ul><b>Grid Manipulation</b>
 * <li>fillGrid</li>
 * <li>printGrid</li>
 * <li>set and clear individual pixels</li>
 * <li>etc...</li></ul>
 * 
 * <ul><b>Feature Recognition</b>
 * <li>charsAbove</li>
 * <li>charsBelow</li>
 * <li>indentRight</li>
 * <li>indentLeft</li>
 * <li>indentTop</li>
 * <li>indentBottom</li>
 * <li>etc..</li>
 * </ul>
 * 
 * 
 * 
 * 
 * @author Marcus Haupt
 * @version 1.0
 * 
 */

public class RGBGrid implements Comparable<RGBGrid> {

        private int [][]        pixel;              
        private boolean [][]    g;    
        private int []          f;                     
        private boolean         initialized     =   false;
        private int             numCols;   
        private int             numRows;
        private int             gridStartCol;   
        private int             gridEndCol;          
        private int             minrgb         =   30;
        private boolean         debug          =   false;
        
        // chop separates the grid into items
        private int             chopRGB;
        
        // reveal inflates the pixels within each item to readable levels
        private int             revealRGB;
        
        private String          value          =   null;
        
        public  RGBGrid(int nRows, int nCols) {
            
            numCols        =   nCols;
            numRows        =   nRows;
            pixel          =   new int     [nRows][nCols]; 
            g              =   new boolean [nRows][nCols];
            f              =   new int            [nCols];     
            
            initialized    =   true;
            
            int i,j;
            for (i=0; i<numRows; i++)
                for (j=0; j<numCols; j++) {
                    pixel[i][j]    =   0;
                    g[i][j]        =   false;
                }
            
            for (j=0; j<numCols; j++)
                f[j]    =   0;   
        }
        
        
        // create and fill the map from the buffer
        public  RGBGrid(BufferedImage b, int startX, int startY, int nRows, int nCols) {
            
            if ( debug )
                Session.logMessageLine("RGBGrid(startX, StartY,...) Start @" + startX + " , " + startY );
            
            numCols        =   nCols;
            numRows        =   nRows;
            pixel          =   new int     [nRows][nCols]; 
            g              =   new boolean [nRows][nCols];
            f              =   new int            [nCols];     
            
            initialized    =   true;
                        
            int i,j,y;
            for ( i=0; i<nRows; i++) {
                for (j=0; j<nCols; j++) {
                    pixel[i][j]    =   b.getRGB(startX+j, startY+i);
                    g[i][j]        =   false;
                }
            }
            
            for (y=0; y<numCols; y++)
                f[y]    =   0;   
            
        }
        
        
        public  RGBGrid(RGBGrid grid, int sRow, int eRow, int sCol, int eCol) {
            String  prefix  =   this.getClass().getSimpleName() + ":RGBGrid(RGBGrid, sRow, eRow, sCol, eCol)";
            
            numRows         =   (eRow-sRow) + 1;
            numCols         =   (eCol-sCol) + 1;
            
            gridStartCol    =   sCol;
            gridEndCol      =   eCol;
            
            minrgb          =   grid.minrgb();
            debug           =   grid.debug();
            
            pixel           =   new int     [numRows][numCols]; 
            g               =   new boolean [numRows][numCols];
            f               =   new int               [numCols];     
            
            
            if ( debug ) {
                Session.logMessageLine(prefix + "\tCreate With Rows from "  +
                    sRow + " to " + eRow + " numRows: " + numRows);
                Session.logMessageLine(prefix + "\tCreate with Cols from "  +
                    sCol + " to " + eCol + " numCols: " + numCols);
            }
            
            int tRow, tCol;
            int r,c;
            for (c=sCol, tCol=0; c <=eCol; c++, tCol++) {
                for (r = sRow, tRow = 0; r<=eRow; r++, tRow++ ) {
                    pixel[tRow][tCol]       =   grid.pixel[r][c];
                    g[tRow][tCol]           =   grid.g[r][c];
                    f[tCol]                  =   grid.f[c];
                    
                }
            }
            
            initialized     =   true;
        }
        
        
        // create a new map using a chunk of another map
        public  RGBGrid(RGBGrid grid, int sCol, int eCol) {
            if ( (sCol < 0) || (eCol <0) )
                return;
            
            String  prefix  =   this.getClass().getSimpleName() + ":RGBGrid(RGBGrid, scol, eCol)";
            
            
            
            numRows        =   grid.numRows();
            numCols        =   (eCol-sCol) + 1;
            gridStartCol       =   sCol;
            gridEndCol         =   eCol;
            minrgb         =   grid.minrgb();
            debug          =   grid.debug();
            
            pixel          =   new int     [numRows][numCols]; 
            g              =   new boolean [numRows][numCols];
            f              =   new int               [numCols];     
            
            int col         =   0, c, r;  
            
            if ( debug )
                Session.logMessageLine(prefix + "\tCreate from "  +
                    sCol + " to " + eCol + " numRows: " + numRows + " numCols: " + numCols );
            
            for (c=sCol; c <= eCol; c++) {
                for (r = 0; r<numRows; r++ ) {
                    pixel[r][col]      =   grid.pixel[r][c];
                    g[r][col]          =   grid.g[r][c];
                    f[col]             =   grid.f[c];
                    
                }
                col++;
            }
            
            initialized     =   true;
        }
        
        
        public  void  updateGridParams() {
            
            int     nCols   =   0;
            int     nRows   =   0;
            
            boolean v;
            
            try {
                while ( true ) {
                    v = g[0][nCols];
                    nCols++;
                }
            }
            catch ( NullPointerException e ) {
                nCols--;
            }
            
            
            
            try {
                while ( true ) {
                    v = g[nRows][0];
                    nRows++;
                }
            }
            catch ( NullPointerException e ) {
                nRows--;
            }
            
            
            numCols        =   nCols;
            numRows        =   nRows;
               
        }
        
        
        public  String  charFromGrid() 
        throws  UnableToReadCharException {
            return  null;
        }
        
        boolean greaterThan() {
            return true;
        }
        
        int chopRGB() {
            return  chopRGB;
        }
        
        void    setChopRGB(int rgb) {
            chopRGB    =    rgb;
        }
        
        int revealRGB() {
            return  revealRGB;
        }
        
        void    setRevealRGB(int rgb) {
            revealRGB    =    rgb;
        }
        
        String  value() {
            return  value;
        }
        
        void    setValue(String value) {
            this.value  =   value;
        }
        
        void setPixel(int r, int c, int value)  {
            pixel[r][c]    =   value;
        }
        
        int getPixel(int r, int c) {
            return  pixel[r][c];
        }
        
        boolean getGrid(int r, int c) {
            return  g[r][c];
        }
        
        void setGrid(int r, int c) {
            if ( (r >= 0) && (r < height()) )
                if ( (c>=0) && (c < width())) {
                    g[r][c]        =   true;
                }
        }
        
        void clearRow(int r) {
            for (int c=0; c<width(); c++)
                clearGridPoint(r,c);
        }
        
        void clearGridPoint(int r, int c) {
            if ( (r >= 0) && (r < height()) )
                if ( (c>=0) && (c < width())) {
                    g[r][c]        =   false;
                    pixel[r][c]    =   0;
                    f[c]--;
                }
        }
        
        void setMinrgb(int mrgb) {
            minrgb =   mrgb;
        }
        
        int getMinrgb() {
            return minrgb;
        }
        
        int startCol() {
            return  gridStartCol;
        }
        
        int endCol() {
            return  gridEndCol;
        }
        
        int numCols() {
           return   numCols; 
        }
        
        int numRows() {
            return  numRows;
        }
        
        
        boolean initialized() {
            return initialized;
        }
        
        
        public  void    printMinRGB (int minrgb) {
           
            String  prefix = this.getClass().getSimpleName() + ":printMinRGB[" + minrgb + "]";
            
            Session.logMessageLine(prefix + "\tStart Col = " + gridStartCol);
            
            for (int r=0; r<numRows(); r++) {
                for (int c=0; c<numCols(); c++) {
                    if ( (pixel[r][c] != 0) && CardUtilities.pixelRedValue(pixel[r][c]) <= minrgb ) {
                        Session.logMessageChar("#");
                    } else
                        Session.logMessageChar(" ");
                    }
                Session.logMessageChar("\n");
            }    
        }
        
        
        public  void    printGrid(int nCols) {
            
            if ( nCols > numCols() )
                nCols   =   numCols();
            
            Session.logMessageLine(this.getClass().getSimpleName() + ":printGrid[" + nCols + " cols ]");
            
            for (int r=0; r<numRows(); r++) {
                for (int c=0; c<nCols; c++) {
                    if ( g[r][c])
                        Session.logMessageChar("#");
                    else
                        Session.logMessageChar(" ");
                }
                Session.logMessageChar("\n");
            }
        }
        
        
        public  void    printGrid(int sCol, int eCol) {
            
            if ( eCol >= numCols() )
                eCol   =   numCols()-1;
            
            Session.logMessageLine(this.getClass().getSimpleName() + ":printGrid[" + startCol() 
                    + " to " + eCol + "]");
            
            for (int r=0; r<numRows(); r++) {
                for (int c=sCol; c<=eCol; c++) {
                    if ( g[r][c])
                        Session.logMessageChar("#");
                    else
                        Session.logMessageChar(" ");
                }
                Session.logMessageChar("\n");
            }
        }
        
        
        public  void    printGrid(String name) {
            Session.logMessageLine(this.getClass().getSimpleName() + "" + name 
                    + ":printGrid[starting at " + startCol() + "]");
            
            for (int r=0; r<numRows(); r++) {
                for (int c=0; c<numCols(); c++) {
                    if ( g[r][c])
                        Session.logMessageChar("#");
                    else
                         Session.logMessageChar(" ");
                }
                 Session.logMessageChar("\n");
            }
            
            Session.logMessageLine(this.getClass().getSimpleName() + "" + name 
                    + ":End of printGrid[starting at " + startCol() + "]");
        }
        
        public  void    printGrid () {
           
            Session.logMessageLine(this.getClass().getSimpleName() + ":printGrid[starting at " + startCol() + "]");
            
            for (int r=0; r<numRows(); r++) {
                for (int c=0; c<numCols(); c++) {
                    if ( g[r][c])
                        Session.logMessageChar("#");
                    else
                         Session.logMessageChar(" ");
                }
                 Session.logMessageChar("\n");
            }
        }
       
        
    public  void cleanGrid() {
            int eCol    =   numCols() -1;
            
            if ( (numCols() > 10) || (numCols()<= 1) )
                return;
            
            if ( debug() )
                Session.logMessageLine(this.getClass().getSimpleName() + ":cleanGrid\tnumCols before clean " + numCols() );
            
            if ( (f[eCol] == 1) && ( g[10][eCol] || g[11][eCol]) ) {
                pixel[10][eCol]    =   pixel[11][eCol]    =   0;
                 numCols--;
            }
            
           
            if ( debug() )
                Session.logMessageLine(this.getClass().getSimpleName() + ":cleanGrid\tnumCols before fill " + numCols());
            
            fillGridLessThan(minrgb);
    }

    public  int minrgb() {
        return  minrgb;
    }    
        
    public  int width () {
        return numCols();
    }

    public  int height () {
        return numRows();
    }
    
    public  boolean debug() {
        return  debug;
    }
    
    public  void setDebug(boolean d) {
        debug  =   d;
    }
    
    public  int whiteSpaceLen() {
        return 4;
    }
    
    public  void    wipeOut()
    throws  IllegalArgumentException
    {
        
        pixel          =   null;              
        g              =   null;    
        f              =   null;                     
        initialized    =   false;
        numCols        =   numRows    = 0;
        gridStartCol       =   gridEndCol     = -1;          
        value          =   "";
         
    }
    
    public  int filledInColumn(int col ) {
        
        int fill   = 0;
        
        if ( (col<0) || (col>=width()))
            return 0;
        
        for (int r=0; r<height(); r++)
            if ( g[r][col] )
                fill++;
        
        return fill;
    }
    
    public  boolean charsAbove(int row) {

        if ( row <= 1 )
            return false;
        
        if ( row >= numRows() )
            row = numRows();
        
        for (int c=0; c<numCols(); c++) {
            for (int r=0; r<row; r++)
                if ( g[r][c] ) {
                    return   true;
                }
        }

        return false;
    }


    public  boolean charsBelow(int row) {

        if ( row >= numRows() -1 )
            return false;
        
        if ( row < 0 )
            return false;
        
        for (int c=0; c<numCols(); c++) {
            for (int r = row+1; r < numRows(); r++ )  {
                if ( g[r][c] ) {
                    return  true;
                }
            }
        }

        return false;
    }
    
    
    // zero out a portion of the map
    public  void    zeroOut(int sCol, int eCol) 
    throws  IllegalArgumentException
    {
        String  prefix  =    this.getClass().getSimpleName() + ":zeroOut";

        if ( (sCol < 0) || (eCol <0) )
            throw   new IllegalArgumentException(prefix + "\tsCol: " + sCol 
                    + " eCol: " + eCol + " numCols: " + numCols());

        if ( eCol >= numCols())
             throw   new IllegalArgumentException(prefix + "\tsCol: " + sCol 
                    + " eCol: " + eCol + " numCols: " + numCols());

        if ( sCol >= numCols())
             throw   new IllegalArgumentException(prefix + "\tsCol: " + sCol 
                    + " eCol: " + eCol + " numCols: " + numCols());
        
        // always delete the shadow next to the right of the char

        if ( sCol > 0 )
            sCol--;
        eCol++;

        for (int c=sCol; c<=eCol; c++) { 
            for (int r=0; r<numRows(); r++) {
                pixel[r][c]      =   -1;
                g[r][c]        =   false;
            }
        }
    }
    
    
    public  int maxItemWidth() {
        return width();
    }
    
    public  boolean isEmpty() {
        if ( width() == 0 )
            return true;
        
        int n=0;
        for (int c=0; c<numCols(); c++)
            n   +=  filledInColumn(c);
        
        return n == 0;
    }
    
   
    public  void    deleteEmptyColumns() {
       
        while ( true ) {
            int d=0;
            for (int i=0; i<numCols(); i++) {
                if ( f[i] == 0 ) {
                    deleteColumn(i);
                    d++;
                }
            }
            if ( (d == 0) || (numCols() ==0) )
                break;
        }
    }
    
    
    public  void    deleteColumn(int dCol) {
        // create a new map w/o the column then copy that map to ourselves
        String prefix   =   this.getClass().getSimpleName() + ":deleteColumn";
        
        if ( width() == 0 )
            return;
        
        int newWidth    =   width()-1;
        
        if ( debug()  ) {
            Session.logMessageLine(prefix + "\tGrid Before Delete");
            printGrid();
        }
        
        // if we delete the only column..poof goes the data
        if ( newWidth == 0 ) {
            numCols        =   0;
            numRows        =   0;
            pixel          =   null; 
            g              =   null;
            f              =   null;     
            
            return;
        }
        
        // make a copy of our data before deleting
        RGBGrid  copy    =   new RGBGrid(this, 0, width()-1);
        
        if ( debug()  ) {
            copy.printGrid();
            Session.logMessageLine(prefix + "\tCopy:  = " + copy.height() + " x " + copy.width() );
        }
       
        numCols    =   newWidth;
        
        pixel          =   new int     [numRows()][newWidth]; 
        g              =   new boolean [numRows()][newWidth];
        f              =   new int                [newWidth];  
        
        int c,r;
                
        // copy the data up to the row we're removing
        for (c=0; c<dCol; c++) {
            for (r=0; r<numRows(); r++) {
                pixel[r][c]    =   copy.getPixel(r, c);
                g[r][c]        =   copy.getGrid(r, c);
                f[c]++;
            }
        }
        
        // then copy the data after it
        for (c=dCol+1; c<copy.width(); c++) {
            for (r=0; r<numRows(); r++) {
                pixel[r][c-1]    =   copy.getPixel(r, c);
                g[r][c-1]        =   copy.getGrid(r, c);
                f[c-1]++;
            }
        }        
        
        if ( debug()  ) {
            Session.logMessageLine(prefix + "\tGrid After Delete");
            printGrid();
        }
    }
    

    public  void    fillGridLessThan() {
        fillGridLessThan(minrgb);
    }
    
    
    // fill the map with values below this rgb in red
    public  void    fillGridLessThan(int minrgb) {

        String  prefix  =   this.getClass().getSimpleName() + ":fillGridLessThan";
        int r,c;

        this.minrgb =   minrgb;

        if ( debug() )
            Session.logMessageLine(prefix +"\tRows: " + numRows() + " nCols: " + numCols());
        
        if ( numCols() == 0 )
            return;

        for (c=0; c<numCols(); c++)
            f[c]    =   0;

        for (r=0; r<numRows(); r++) {
            for (c=0; c<numCols(); c++) {

                if ( (pixel[r][c] != 0) && CardUtilities.pixelRedValue(pixel[r][c]) <= this.minrgb ) {
                    g[r][c]   =   true;
                    f[c]++;
                } else {
                    g[r][c]   =   false;
                }                    
            }
        }
    }

    
    // use only the red pixel value to determine membership
     public  void    fillGridAllRGBRange(int minRGB, int maxRGB) {

        String prefix  =   this.getClass().getSimpleName() + ":fillGridALLRGBRange";
         
        int r,c, px;

        minrgb =   minRGB;
        
        if ( debug() ) {
            Session.logMessageLine(prefix + "\tNum Rows: " + numRows() );
            Session.logMessageLine(prefix + "\tNum Cols: " + numCols() );
            Session.logMessageLine(prefix + "\tMin RGB: " + minRGB );
            Session.logMessageLine(prefix + "\tMax RGB: " + maxRGB );
        }

        for (c=0; c<numCols(); c++)
            f[c]    =   0;

        for (r=0; r<numRows(); r++) {
            for (c=0; c<numCols(); c++) {
                g[r][c]   =   false;
                
                if ( this.pixel[r][c] != 0 )  {
                    px =  this.pixel[r][c];
                    
                    if ( CardUtilities.AllRGBAboveValue(minRGB, px) && 
                         CardUtilities.AllRGBBelowValue(maxRGB, px) ) {
                    
                        g[r][c]   =   true;
                        f[c]++;
                    }
                }
            }
        } // end outer for loop
        
        if ( debug()  )
            Session.logMessageLine(prefix + "\tDone");
    }
    
     
    public  void    fillGridAnyBelow(int RGB) {

        String prefix  =   this.getClass().getSimpleName() + ":fillGridAnyRGBBelow";
         
        int r,c, px;
        int rd,gr,bl;
        
        if ( debug() ) {
            Session.logMessageLine(prefix + "\tNum Rows: "  +   numRows() );
            Session.logMessageLine(prefix + "\tNum Cols: "  +   numCols() );
            Session.logMessageLine(prefix + "\tMax RGB: "   +   RGB );
        }

        for (c=0; c<numCols(); c++)
            f[c]    =   0;

        for (r=0; r<numRows(); r++) {
            for (c=0; c<numCols(); c++) {
                g[r][c]   =   false;
                
                if ( this.pixel[r][c] != 0 )  {
                    px  =  this.pixel[r][c];
                    
                    rd  =   CardUtilities.pixelRedValue(px);
                    gr  =   CardUtilities.pixelGreenValue(px);
                    bl  =   CardUtilities.pixelBlueValue(px);
                    
                    if ((rd<RGB) || (gr<RGB) || (bl<RGB) ) {
                        g[r][c]   =   true;
                        f[c]++;
                    }
                }
            }
        } // end outer for loop
        
        if ( debug()  )
            Session.logMessageLine(prefix + "\tDone");
    }
    
    public  void    fillGridGreaterThan() {
        fillGridGreaterThan(minrgb);
    }
    
    public  void    fillGridGreaterThan(int minrgb) {

        int r,c;

        this.minrgb =   minrgb;

        if ( debug() ) {
            Session.logMessageLine(this.getClass().getSimpleName() + 
                    ":fillGridGreaterThan\tnumCols() " + numCols());
            
            Session.logMessageLine(this.getClass().getSimpleName() + 
                    ":fillGridGreaterThan\tnumCols() " + numRows());
        }

        for (c=0; c<numCols(); c++)
            f[c]    =   0;

        for (r=0; r<numRows(); r++) {
            for (c=0; c<numCols(); c++) {

                if ( (pixel[r][c] != 0) && CardUtilities.pixelRedValue(pixel[r][c]) >= this.minrgb ) {
                    g[r][c]   =   true;
                    f[c]++;
                } else {
                    g[r][c]   =   false;
                }                    
            }
        }
    }
        
        
    @Override
    public  int compareTo(RGBGrid o) {

        if ( startCol() < o.startCol())
            return -1;
        else if ( startCol() > o.startCol() )
            return 1;
        else
            return 0;
    }
    
    
    public  boolean charIsFT1() {
        return charIs1() || charIsF() || charIsT();
    }
    
    
    public boolean  charIs1() {
        
        if ( (width() <= 4) && (f[width()-1] == 10) )
            return ( !g[0][0] && !g[0][1] &&
                     !g[1][0] &&  g[1][1] &&
                      g[2][0] &&  g[2][1] &&
                     !g[3][0]);
        else
            return false;
    }
    
  
    
    public  boolean charIsF() {
        int lc  =   width() - 1;
    
        if ( (width() < 2)|| (width()>4))
            return false;
        
        if ( debug )
            Session.logMessageLine("charIsF: " + (g[0][lc]) + " " + g[1][lc] + " " +
                g[2][lc] + " " + g[3][lc]  + "Hidden? " 
                    + CardUtilities.pixelRGBValues(getPixel(0, lc)) );
        
        // traditionally well-formed f
        if ( g[0][lc] && !g[1][lc] && g[2][lc] && !g[3][lc] && !g[4][lc] )
             return true;
       
        if ( g[0][lc] && !g[1][lc] && g[2][lc] && g[3][lc] && !g[4][lc] )
             return true;
        
        // possibly hidden f
        else if ( !g[0][lc] && !g[1][lc] && g[2][lc] && !g[3][lc] && !g[4][lc] ) {
            
            return CardUtilities.AllRGBBelowValue(90, getPixel(0, lc));
        }
        
        return false;
    }
        
        
    public  boolean charIsT() {
        int lc  =   width() - 1;
        
        if ( (width() < 2)|| (width()>4))
            return false;
    
        if ( debug ) {
            Session.logMessageLine("charIsT: " + width());
            Session.logMessageLine("charIsT: " + (g[0][lc]) + " " + g[1][lc] + " " +
                g[2][lc] + " " + g[3][lc] );
            Session.logMessageLine("charIsT: " + (g[0][0]) + " " + g[1][0] + " " +
                g[2][0] + " " + g[3][0] );
        }
                
        if (  !g[0][lc] && !g[1][lc] && g[2][lc] && g[3][lc] && !g[4][lc] && !g[5][lc] )
            return true;
        
        if (  (width() == 3) && !g[0][0] && !g[1][0] && g[2][lc] && !g[3][0] && !g[4][0]  )
            return true;
               
        // this is a t with this grid pattern unless the top row is a hidden f
        return ( !g[0][lc] && !g[1][lc] && g[2][lc] && !g[3][lc] && !g[4][lc] );
    }
    
    
    public  boolean startsAndEndsLikeT() {
        
        int lc  =   width() -1;
         
        return ( (width() >= 3) && (f[0] == 2) && (f[lc]==2) &&
                  g[0][0] && g[0][lc] &&
                  g[1][0] && g[1][lc] );
        
    }
    
    
    public  int rightEdgeOfPixel(int x, int y) {
        
        for (int i=x; i<width(); i++)
            if ( filledRowCol(i,y) )
                return i;
        
        return  -1;
    }
    
    
    public  int leftEdgeOfPixel(int x, int y) {
        
        for (int i=x; i>=0; i--)
            if ( filledRowCol(i,y) )
                return i;
        
        return  -1;
    }
    
    
    public  int topEdgeOfPixel(int x, int y) {
        
        for (int i=y; i>=0; i--)
            if ( filledRowCol(x,y) )
                return i;
        
        return  -1;
    }
     
     
    public  int botEdgeOfPixel(int x, int y) {
        
        for (int i=y; i<height(); i++)
            if ( filledRowCol(x,y) )
                return i;
        
        return  -1;
    }
    
    public  boolean enclosedPoint(int x, int y) {
        return (topEdgeOfPixel(x,y) >= 0) && (botEdgeOfPixel(x,y) >= 0) &&
               (rightEdgeOfPixel(x,y) >= 0) && (leftEdgeOfPixel(x,y) >= 0);
    }
    
   
    
    public  boolean openMiddle(int topRow, int botRow, int emptyFromRow, int emptyToRow, int sCol, int eCol ) {
        
        String  prefix  =   "openMiddle(top,bot,from,to,sCol,eCol)";
        
        int empty;
        int len     = (emptyToRow - emptyFromRow) + 1;
        
        if ( (topRow < 0) || (topRow>=height()))
            return false;
        
        if ( (botRow <= topRow) || (botRow>=height()))
            return false;
        
        if ( (emptyFromRow < 0) || (emptyFromRow>=height()))
            return  false;
        
        if ( (emptyToRow <= emptyFromRow) || (emptyToRow>=height()))
            return  false;
        
       
        for (int c = sCol; c < eCol; c++) {
            empty   =   0;
                        
            
            // has to be pixels above and below the hole for it to be a hole
            if ( (g[topRow][c] || g[topRow+1][c]) &&
                 (g[botRow][c] || g[botRow-1][c]) ) {
               
               
                for (int r = emptyFromRow; r <= emptyToRow; r++) {
                
                    if ( !g[r][c] ) {
                        
                        empty++;
                    }
                }
               
                if ( empty == len  )
                    return true;
            }
        }
        
        return false;
    }
    
    
    public  boolean openMiddle(int topRow, int botRow, int fromRow, int toRow) {
       
        String  prefix  =   "openMiddle(top,bot,from,to)";
        
        return openMiddle(topRow, botRow, fromRow, toRow, 1, width()-1);
    } 
    
    
    public  boolean indentRightSideAtRow(int row) {
        String  prefix  =   this.getClass().getSimpleName() + ":indentRightSideAtRow";
        
        int     eCol    =   width();
        
        if ( (width() < 2) || (row<0) || (row>=height()) )
            return false;
        
        return ( !g[row][eCol-1] && !g[row][eCol-2] );
        
    }
    
    public  boolean indentRightSide(int sRow, int eRow) {
        String  prefix  =   this.getClass().getSimpleName() + ":indentRightSide";

        if ( debug() ) {
            Session.logMessageLine(prefix + "\tCheck Starting at Row: " + sRow + " width: " + width());
            Session.logMessageLine(prefix + "\tEnding at Row: " + eRow );
        }
        
        boolean indent  =   false;
        
        int     eCol    =   width();
        
        if ( width() < 3 )
            return false;
        
        if ( debug() ) {
            Session.logMessageLine(prefix + "\tWidth: "     + width() );
            Session.logMessageLine(prefix + "\tHeight: "    + height() );
        }
        
        for (int i=sRow; i <= eRow; i++ ) {     

            if ( !g[i][eCol-1] && !g[i][eCol-2] ) {
                indent =   true;
                break;
            }
        }
        return  indent;
    }
    
    
    public  boolean indentLeftSide(int sRow, int eRow) {
        String  prefix  =   this.getClass().getSimpleName() + ":indentLeftSide";

        if ( debug()  )
            Session.logMessageLine(prefix + "\t\tCheck Starting at Row: " + sRow 
                + " width: " + width() + " endingAtRow: " + eRow );

        if ( (width() < 3) || (height() < 3) )
            return false;
        
        boolean indent =    false;

        for (int i=sRow; i <= eRow; i++ ) {     

            if ( !g[i][0] && !g[i][1] ) {
                indent =   true;
                break;
            }
        }

        return  indent;
    }
    
    
    public  boolean bigEye() {
        boolean top     =   false;
        boolean bot     =   false;
        boolean left    =   true;
        boolean right   =   true;
        
        
        for (int i=0; i<5; i++) {
            if ( filledInColumn(i) >=10 ) {
                left    =   true;
                break;
            }   
        }
        
        for (int i=1; i<=5; i++) {
            if ( filledInColumn(width()-i) >=10 ) {
                right    =   true;
                break;
            }   
        }
        
        for (int i=0; i<3; i++) {
            if ( (filledInRow(i) >=8) &&
                !filledRowCol(i,0) && !filledRowCol(i,width()-1)) {
                
                top =   true;
                break;
            }
        }
        
        for (int i=9; i<height()-3; i++) {
            if ( (filledInRow(i) >=8) &&
                !filledRowCol(i,0) && !filledRowCol(i,width()-1)) {
                
                bot =   true;
                break;
            }
        }
        
        
        if ( top && bot ) {
            for (int x=2; x<7; x++) {
                for (int y=2; y<5; y++) {
                    
                    if ( !filledRowCol(x,y) && !filledRowCol(x+1,y) )
                        return true;
                
                    if ( !filledRowCol(x,y) && !filledRowCol(x,y+1) )
                        return true;
                }
            }
        }
    
        return  false;
    }
    
    
    
    public  boolean upperEye() {
        
        boolean top =   false;
        boolean bot =   false;
        
        if ( filledRowCol(2,0) && filledRowCol(3,0) && filledRowCol(4,0) && 
             filledRowCol(5,0) && filledRowCol(6,0)  ) {
             top    =   true;
        }
        
        if ( filledRowCol(2,1) && filledRowCol(3,1) && filledRowCol(4,1) && 
             filledRowCol(5,1) && filledRowCol(6,1)  ) {
             top    =   true;
        }
        
        for (int i=5; i<=8; i++ ) {
            if ( filledRowCol(i,2) && filledRowCol(i,3) &&  filledRowCol(i,4) && 
                 filledRowCol(i,5) && filledRowCol(i,6) ) {
                bot     =   true;
                break;
            }
        }
        
                 
        if ( top && bot && ! indentRightSideToDepth(4, 2, 6)) {
            for (int x=2; x<=6; x++) {
                for (int y=2; y<=6; y++) {
                    
                    if ( !filledRowCol(x,y)  )
                        return true;
                }
            }
        }
        
        return false;
    }
    
    
    
    public  boolean lowerEye() {
        
        boolean top =   false;
        boolean bot =   false;
        
        if ( filledRowCol(2,0) && filledRowCol(3,0) && 
             filledRowCol(4,0) && filledRowCol(5,0) ) {
             top    =   true;
        }
        else if ( filledRowCol(2,1) && filledRowCol(3,1) && 
             filledRowCol(4,1) && filledRowCol(5,1) ) {
             top    =   true;
        }
        
        if ( filledRowCol(2,5) && filledRowCol(3,5) && 
             filledRowCol(4,5) && filledRowCol(5,5) ) {
            bot     =   true;
        }
        else  if ( filledRowCol(2,6) && filledRowCol(3,6) && 
             filledRowCol(4,6) && filledRowCol(5,6) ) {
            bot     =    true;
        }
                 
        if ( top && bot ) {
            for (int x=2; x<7; x++) {
                for (int y=2; y<5; y++) {
                    
                    if ( !filledRowCol(x,y) )
                        return true;
                                
                }
            }
        }
        
        return false;
    }
    
    public  boolean indentLeftSideToDepth(int depth, int sRow, int eRow) {
        String  prefix  =   this.getClass().getSimpleName() + ":indentLeftSideDepth";

        if ( depth >= width() )
            return false;
        
        if ( (sRow < 0) || (eRow >= height() ))
            return false;
        
        if ( debug()  )
            Session.logMessageLine(prefix + "\t\tCheck Starting at Row: " + sRow 
                + " width: " + width() + " endingAtRow: " + eRow );

        if ( (width() < 3) || (height() < 3) )
            return false;
        
        boolean indented    =   false;
        
        for (int i=sRow; i <= eRow; i++ ) {     
            for ( int c=0; c<depth; c++ ) {
                indented    =   true;
                if ( g[i][c] ) {
                    indented    =   false;
                    break; // next row
                }
            }
            if ( indented )
                break;
        }

        return  indented;
    }
    
    public  boolean indentRightSideToDepth(int depth, int sRow, int eRow) {
        String  prefix  =   this.getClass().getSimpleName() + ":indentLeftSideDepth";

        if ( debug()  )
            Session.logMessageLine(prefix + "\t\tCheck Starting at Row: " + sRow 
                + " width: " + width() + " endingAtRow: " + eRow );

        if ( (width() < 3) || (height() < 3) )
            return false;
        
        boolean indented    =   false;
        
        for (int i=sRow; i <= eRow; i++ ) {     
            for ( int c=1; c<=depth; c++ ) {
                indented    =   true;
                if ( g[i][width()-c] ) {
                    indented    =   false;
                    break; // next row
                }
            }
            if ( indented )
                break;
        }

        return  indented;
    }
    
    
    public  boolean indentTopToDepth(int depth, int sCol, int eCol) {
        
        String  prefix  =   this.getClass().getSimpleName() + ":indentTopToDepth";

        if ( debug()  )
            Session.logMessageLine(prefix + "\t\tCheck Starting at Column: " + sCol 
                + " width: " + width() + " ending at column: " + eCol );

        
        boolean indented    =   false;
        

        return  indented;
    }
    
    
    public  boolean emptyColumnInGrid(int fromCol, int toCol, int fromRow, int toRow) {
        String  prefix  =   this.getClass().getSimpleName() + ":emptyColumnInGrid";
        
        if ( (fromCol <0) || ( fromCol>=width()) )
            return false;
        
        if ( (toCol <0) || (toCol>=width()) || (toCol <= fromCol) )
            return false;
        
        if ( (fromRow <0) || ( fromCol>=height()) )
            return false;
        
        if ( (toRow <0) || ( toRow>=height()) || (toRow <= fromRow) )
            return false;
                
        int r,c;
        
        int empty;
        
        int target  =   (toRow-fromRow) +   1;
        
        Session.logMessageLine(prefix + "\tfind empty column of spaces: " + target);
        
        for (c=fromCol; c<toCol; c++) {
            empty   =   0;
            
            for (r=fromCol; r<=toCol; r++) {
                if ( g[r][c] )
                    break;
                else
                    empty++;
            }
            
            if ( empty == target ) 
                return true;
        }
        
        
        return  false;
    }
    
    public  int maxColumnFill() {
        int max =   0;
        int fil;
        
        for (int i=0; i<width(); i++) {
            fil   =   filledInColumn(i);
            
            if ( fil >max )
                max =   fil;
        }
        
        return max;
    }
    
    
    public  boolean indentBottom(int sRow) {
        boolean indent =    false;
        
        String  prefix  =   this.getClass().getSimpleName() + ":indentBottom";
        
         if ( height() < 3 )
            return false;
        
        for (int i=0; i<numCols(); i++) {
            if ( !g[sRow][i] && !g[sRow-1][i] ) {
                indent =   true;
                break;
            }
        }
        
        return  indent;
    }
    
    /**
     * 
     * Find a bottom indent, which is two blank rows starting at iRow and going 
     * up
     * 
     * @param iRow indent Row. Indent if blank at iRow and iRow-1 
     * @param sCol start column
     * @param eCol end column
     * @return 
     */
    public  boolean indentBottom(int iRow, int sCol, int eCol) {
        boolean indent =    false;
        
        String  prefix  =   this.getClass().getSimpleName() + ":indentBottom";
        
         if ( height() < 3 )
            return false;
         
         if ( eCol >= width() )
             eCol   = width()-1;  
        
        for (int i=sCol; i<eCol; i++) {
            if ( !g[iRow][i] && !g[iRow-1][i] ) {
                indent =   true;
                break;
            }
        }
        
        return  indent;
    }
    
    
    public  boolean upperLeftBoxEmpty() {
        if ( width() >= 2)
            return (!g[0][0]) && (!g[0][1]) && (!g[1][0]) && (!g[1][1]);
        else
            return (!g[0][0]) && (!g[1][0]);
    }
    
    public  boolean lowerLeftBoxEmpty() {
        
        if ( width() > 1 )
            return (!g[8][0]) && (!g[8][1]) && (!g[9][0]) && (!g[9][1]);
        else
            return (!g[8][0]) && (!g[9][0]);
    }
    
    
    public  boolean upperLeftEmptyNbyM(int rows, int cols) {
        
        if ( (cols >= width() ) || ( rows >= height() ) )
                return false;
        
        int r,c;
        
        for (r=0; r<rows; r++) 
            for (c=0; c<cols; c++)
                if ( g[r][c] )
                    return false;
        
       return   true;
    }
    
    public  boolean upperRightEmptyNbyM(int rows, int cols) {
        
        if ( (cols >= width() ) || ( rows >= height() ) )
                return false;
        
        int r,c;
        
        for (r=0; r<rows; r++) 
            for (c=1; c<=cols; c++)
                if (g[r][width()-c] )
                    return false;
        
       return   true;
    }
    
    public  boolean lowerRightEmptyNbyM(int rows, int cols) {
        
        if ( (width() < cols+1) || (height()<rows+1) || isEmpty() )
            return false;
        
        
        try {
        
        int r,c;
        
        for (r=1; r<=rows; r++) 
            for (c=1; c<=cols; c++)
                if (g[height()-r][width()-c] )
                    return false;
        }
        catch (Exception e ) {
            Session.logMessageLine("lowerRightEmptyNbyM: Height:" + height() + " Width: " + width());
            Session.logMessageLine("lowerRightEmptyNbyM: " + e.toString());
            printGrid();
            Session.exit(0);
        }
        
       return   true;
    }
    
     public  boolean lowerLeftEmptyNbyM(int rows, int cols) {
        
        String  prefix  =   this.getClass().getSimpleName() + ":lowerLeftEmptyNbyM";
        
        if ( (cols >= width() ) || ( rows >= height() ) )
                return false;
        
        int r,c;
        
        if ( debug )
            Session.logMessageLine("lowerLeftEmptyNbyM Called");
        
        for (r=1; r<=rows; r++) 
            for (c=0; c<=cols; c++)
                if (g[height()-r][c] ) {
                    if ( debug )
                        Session.logMessageLine("lowerLeftEmptyNbyM filled at [" + (height()-r) +"][" +
                            c + "]");
                    return false;
                }
        
        if ( debug )
            Session.logMessageLine("LowerLeftFilled Empty");
       return   true;
    }
     
    
    public  boolean lowerLeftBelowBoxEmpty() {
        if ( width() >= 2)
            return (!g[10][0]) && (!g[10][1]) && (!g[11][0]) && (!g[11][1]);
        else
            return (!g[10][0]) &(!g[11][0]);
    }
    
    public  boolean lowerLeftCornerEmpty() {
        return (!g[9][0]);
    }
    
    public  boolean upperRightBoxEmpty() {
        int lc    =   width()-1;
        
        if ( width() > 1)
            return (!g[0][lc]) && (!g[0][lc-1]) && (!g[1][lc]) && (!g[1][lc-1]);
        else
            return  (!g[0][lc]) && (!g[1][lc]);
    }
    
    public  boolean upperLefttBoxEmpty() {
        int lc    =   width()-1;
        
        switch ( width() ) {
            case 0:
                return false;
                
            case 1:
                return (!g[0][0]) && (!g[1][0]);
                
            default:
                return (!g[0][0]) && (!g[0][1]) && (!g[1][0]) && (!g[1][1]);
        }
    }
    
    public  boolean lowerRightBoxEmpty() {
        int lc    =   width()-1;
        
        if ( width() == 1)
            return (!g[8][lc]) && (!g[9][lc]);
        else
            return (!g[8][lc]) && (!g[8][lc-1]) && (!g[9][lc]) && (!g[9][lc-1]);
    }
    
    public  boolean lowerRightBelowBoxEmpty() {
        int lc    =   width()-1;
    
        if ( width() > 1)
            return (!g[10][lc]) && (!g[10][lc-1]) && (!g[11][lc]) && (!g[11][lc-1]);
        else
            return (!g[10][lc]) && (!g[11][lc]);
    }
     
    public  boolean lowerRightCornerEmpty() {
        int lc    =   width()-1;
        
        return (!g[9][lc]);
    }
    
    public  boolean indentTopStartingAtRow(int row) {
        return  indentTop(row);
    }
    
    public  boolean filledRowCol(int row, int col) {
        if ( (row <0) || (row>=height()) ||
             (col<0)  || (col>=width()))
            return false;
        
         return  g[row][col];
    }
    
    public boolean allPixelsInsideBox(int top, int bot) {
        return !charsAbove(top) && !charsBelow(bot) ;
    }
 
    public  int indentColumnFromRow(int col, int topRow) {
        
        int indent  =   0;
        String  prefix  =   this.getClass().getSimpleName() + ":indentColumnFromRow";
        
        if ( debug()  )
            Session.logMessageLine(prefix + "\tCheck Starting at Row: " + topRow);
        
         if ( height() < topRow +2 )
            return 0;
        
        
        for (int r=topRow; r < height(); r++) {
            if ( !g[r][col] )
               indent++;
            else 
                break;
        }
        
        return  indent;
    }
    
    public  boolean indentTop(int row, int sCol, int eCol) {
        boolean indent  =   false;
        String  prefix  =   this.getClass().getSimpleName() + ":indentTop(row, sCol, eCol)";
        
        if ( debug()  )
            Session.logMessageLine(prefix + "\tCheck Starting at Row: " + row);
        
         if ( height() < row +2 )
            return false;
        
        
        for (int i=sCol; i <=eCol; i++) {
            if ( !g[row][i] && !g[row+1][i] ) {
                indent =   true;
                break;
            }
        }
        
        return indent;
    }
    
    
    
    public  boolean indentTop(int row) {
        
        boolean indent  =   false;
        String  prefix  =   this.getClass().getSimpleName() + ":indentTop";
        
        if ( debug()  )
            Session.logMessageLine(prefix + "\tCheck Starting at Row: " + row);
        
         if ( height() < row +2 )
            return false;
        
        
        for (int i=0; i <numCols()-1; i++) {
            if ( !g[row][i] && !g[row+1][i] ) {
               indent =   true;
                break;
            }
        }
        
        return  indent;
        
    }
    
    public  boolean barAcross(int row ) {
        
        boolean bar =    true;
        
        if ( width() == 0 )
            return false;
        
        for (int i=0; i<numCols(); i++) {
            if ( !g[row][i]) {
                bar =   false;
                break;
            }
        }
        
        return  bar;
    }
    
     public  int filledInRow(int row ) {
         
        int fil=0;
         
        if ( (width() == 0) || ( row>=height()) || (row<0))
            return fil;
        
        for (int i=0; i<numCols(); i++)
            if ( g[row][i])
                fil++;
        
        return  fil;
    }
    
    
    
    public  boolean barAcrossBetween(int sCol, int eCol, int row ) {
        
        boolean bar =    true;
        
        if ( width() == 0 )
            return false;
        
        for (int i=sCol; i<eCol; i++) {
            if ( !g[row][i]) {
                bar =   false;
                break;
            }
        }
        
        return  bar;
    }
    
    
     // remove any chars not attached to other chars
    public  void    deepClean() {
        int  x,y;
       
        String  prefix  =   this.getClass().getSimpleName() + ":deepClean";
                
        if ( width() > 1 ) {
            
            for (x=0; x<width(); x++) {
                if ( f[x] == 1 ) {
                    deleteColumn(x);
                }
            }
        }
        
    }
    
    // remove any chars not attached to other chars
    public  void    fillMissingDataColumns() {
        int  x,y;
       
        String  prefix  =   this.getClass().getSimpleName() + ":fillMissingColumns";
        
        for (x=0; x<width(); x++) {
            switch ( f[x] ) {
                case 9:
                    break;
                    
                case 7:
                    break;
                    
                case 6:
                    if ( g[4][x] && g[4][x] && g[6][x] &&
                         g[7][x] && g[8][x] && g[9][x] ) {
                        
                        g[2][x]    =   true;
                        g[3][x]    =   true;
                    }
                    break;
            }
        }
    }
    
    public  void    deleteSpuriousRows() {
        int  n=0;
       
        String  prefix  =   this.getClass().getSimpleName() + ":deleteSpuriousRows";
       
        for (int i=0; i<width(); i++) {
            if ( g[11][i] )
                n++;
        }
        
        if ( n == 1 ) {
            for (int i=0; i<width(); i++) {
                g[11][i] = false;
            }
        }   
    }
    
    
    
    // get rid of the border near the top 
    // of a map that starts at a shield edge
   
    public  void    cleanCharsNearShieldLeftEdge() {
        
    }
    
    
    public  void    cleanCharsNearShieldRightEdge() {
        
    }
    
    
    @Override
    public  String  toString()
    {    
     
        if ( value == null ) {
            try {
                // set whitespace == width == no whitespace
                return toString(this, 1, width());
            }
            catch ( UnableToReadCharException e ) {
                return "";
            }
        }
        else {
            return value;
        }
    }
        
    
    public  <T extends RGBGrid> String  toString(T grid, int minWidth, int whiteSpace)
    throws  UnableToReadCharException
    {
        String  prefix  =   this.getClass().getSimpleName() + ":toString";
        String  s       = "";
        
        if ( debug()  )
            Session.logMessageLine(prefix + "\tCoverting to String");
        
        ArrayList<T>   array    =   breakIntoItems(grid, minWidth, whiteSpace);
            
        if ( debug()  )
            Session.logMessageLine(prefix + "\tBroke into " + array.size() + " items" );
        
        int     item   =0;
        String  ch;
        
        for (T elem : array ) {
            try {
                
                if ( debug()  ) {
                    Session.logMessageLine(prefix + "\t-------Start Item[" + item + "]---------");
                    elem.printGrid();
                }
                
                ch  =   elem.charFromGrid();
                               
                s   +=  ch; 
                
                item   ++;
                
                if ( debug()  ) {
                    Session.logMessageLine(prefix + "\tch = " + ch);
                    Session.logMessageLine(prefix + "\t-----End Item[" + item + "]-------------");
                
                    Session.logMessageLine(prefix + "\tString********>" + s);
                }
            }
            catch ( CharTooWideException e ) {
                if ( debug()  )
                    Session.logMessageLine(prefix + " \tToo Wide. Separate");
                
                // break this item into separate items
                T combinedChars = separateJoinedChars(elem);
                
                if ( combinedChars != null ) {
                    if ( debug()  ) {
                        Session.logMessageLine(prefix + "\tExracting Chars with separateJoinedChars: " + 
                            combinedChars.value() );
                    }
                    
                    s   +=  combinedChars.value();
                }
                else if ( debug()  ) {
                    Session.logMessageLine(prefix + "\tseparateJoinedChars chars failed");
                }
            }    
            catch ( UnableToReadCharException e ) {
                
            }
            catch ( Exception e) {
                if ( debug()  )
                    Session.logMessageLine(prefix + e);
                
                throw  new UnableToReadCharException(prefix);
            }
        }
        
        
        if ( s.length() == 0 )
            throw  new UnableToReadCharException(prefix + "No chars Read");
               
        return  s;
    }
    
    
    
    
    <T extends RGBGrid> T  separateJoinedChars(T e) {
        String              chars       =   "";
        
        String  prefix      =   this.getClass().getSimpleName() + ":separateJoinedChars";
        
        if ( debug()  ) {
            Session.logMessageLine(prefix + "\tStart With Elem: " + e.width() + " Cols Wide");
        
            Session.logMessageLine(prefix + "\t---------Display Char To Split---------");
            e.printGrid();
            Session.logMessageLine(prefix + "\t-----End Display Char To split---------");
        }
        
        // starting at the last column
        // add columns to until we find a char
        // delete this char and try again
        // until no more columns 
        
        T   back    =   extractRightChar(e);

        T   front   =   extractLeftChar(back, e);
                        
        T   middle  =   null;
        
        
        if ( (front != null) && (back != null) ) {
            
            if ( debug()  ) {
                Session.logMessageLine(prefix + "\tFront Char: " + front.value());
                Session.logMessageLine(prefix + "\tBack Char:  " + back.value());
            }
            
            int explWidth  =   front.width() + back.width();
            
            if ( debug()  ) {
                Session.logMessageLine(prefix + "\tTotal Width: " + e.width());
                Session.logMessageLine(prefix + "\tExplained Width: " + explWidth);
            }
            
            int missing =  e.width() - explWidth;
            
            if ( missing > 0 ) {
                
                try {
                   
                    if ( debug()  ) {
                        Session.logMessageLine(prefix + "\tTotal Width: "   + e.width());
                        Session.logMessageLine(prefix + "\tFront Width: "   + front.width());
                        Session.logMessageLine(prefix + "\tBack Width: "    + back.width());
                        
                    }
                    
                    middle = (T) e.getClass()
                                .getConstructor(e.getClass(), int.class , int.class) 
                                .newInstance(e, front.width(), e.width()-back.width()-1 );
                 
                    if ( debug()  )
                        middle.printGrid();
                    
                    String  mc =  middle.charFromGrid();
                }
                catch (UnableToReadCharException ex) {
                    
                }
                catch ( NoSuchMethodException | InstantiationException | 
                    IllegalAccessException | InvocationTargetException ex ) {
                    Session.logMessageLine(prefix + "\tError Using Reflection");
                    Session.logMessageLine(ex.getMessage());
                    return null;
                }
               
                if ( middle != null) {
                    if ( debug()  )
                        Session.logMessageLine(prefix + "\tChars in the middle: ");
                    
                    if ( debug()  )
                        middle.printGrid();
                }
            }
            
            
            
            if ( middle != null ) {
                if ( debug()  )
                    Session.logMessageLine(prefix + "\tIn the Middle: " + middle.value() );
                
                if ( middle.value() != null )
                    chars   =   front.value()   +   middle.value() + back.value();
                else
                    chars   =   front.value()   +   back.value();
            } else
                chars   =   front.value()   +   back.value();
        }

        e.setValue(chars);
        return e;
    }
    
    
    // find the char on the right side via the greedy method
    <T extends RGBGrid> T  extractRightChar(T e) {
        
        String  prefix      =   this.getClass().getSimpleName() + ":extractRightChar";
        
        int eCol        =   e.width()-1;
        int sCol        =   e.width()-2;
        
        T   validItem   =   null;
        
        while ( sCol > 0) {
                             
            try {
                
                 T item = (T) e.getClass()
                                .getConstructor(e.getClass(), int.class , int.class) 
                                .newInstance(e, sCol, eCol);
                
                if ( debug()  ) {
                    Session.logMessageLine(prefix + "\t.........Printing Sub Char from " + sCol + " to " + eCol + " .....");
                    item.printGrid();
                    Session.logMessageLine(prefix + "\t........Done Printing Sub Char........");
                }

                String  ch  =   item.charFromGrid();
                
                item.setValue(ch);
                
                validItem   =   item;
                
                if ( debug()  )
                    Session.logMessageLine(prefix + "\t================>>Found Char: \"" + ch + "\"" );
                                
            }
            catch ( UnableToReadCharException ex) {
            }
            catch ( NoSuchMethodException | InstantiationException | 
                    IllegalAccessException | InvocationTargetException ex ) {
                Session.logMessageLine(prefix + "\tError Using Reflection");
                Session.logMessageLine(ex.getMessage());
                return null;
            }
            
            sCol--;
            
        }
        
        return  validItem;
    }
    
    
    
    <T extends RGBGrid> T extractLeftChar(T right, T e) 
    {
        
        String  prefix      =   this.getClass().getSimpleName() + ":extractLeftChar";
        
        int eCol    =   1;
        int sCol    =   0;
                
        int dataEnd =   maxItemWidth();

        T found =   null;
        
        while ( eCol < dataEnd ) {
            
            try {
                
                if ( debug()  ) {
                    Session.logMessageLine(prefix + "\tChar Width:" + e.width() );
                    Session.logMessageLine(prefix + "\tExtract Char from " + sCol + " to " + eCol + " .....");
                }
                
                T item = (T) e.getClass()
                              .getConstructor(e.getClass(), int.class , int.class) 
                              .newInstance(e, sCol, eCol);
              
                if ( debug()  ) {
                    Session.logMessageLine(prefix + "\t.........Printing Sub Char from " + sCol + " to " + eCol + " .....");
                    item.printGrid();
                    Session.logMessageLine(prefix + "\t........Done Printing Sub Char........");
                }
           
                String  ch  =   item.charFromGrid();
                
                if ( !ch.equals(" ") ) {
                
                    if ( debug()  )
                        Session.logMessageLine(prefix + "\t================>>Found Char: \"" + ch + "\"" );
                    
                    item.setValue(ch);
                    found   =   item;
                }
                
            }
            catch ( UnableToReadCharException ex) {
            }
            catch ( NoSuchMethodException | InstantiationException | 
                    IllegalAccessException | InvocationTargetException ex ) {
                
                if ( debug()  ) {
                    Session.logMessageLine(prefix + "\tError");
                    Session.logMessageLine(ex.getMessage());
                }
                return found;
            }
            
            eCol++;
            
        }
        
        return found;
    }
    
    
    // if whiteSpace > 0 that contiguous blanks together will be added to the result
    // as a blank char
    public  <T extends RGBGrid> ArrayList<T> breakIntoItems(T model, int minWidth, int whiteSpace) 
    {                
        
        String  prefix          =   this.getClass().getSimpleName() + ":breakIntoItems";
        
        ArrayList<T> items      =    new ArrayList<>();
        
        if ( debug()  )
            Session.logMessageLine(prefix + "\tBreaking Into Items");
        
        int fStart  = -1, fEnd;
        int blanks  =  0; // count the number of contiguous blanks for word boundaries    
        
        for (int c = 0; c<numCols(); c++) {
            
            // is this the start of a new char?
            if ( f[c] > 0 ) {
                if ( fStart < 0 ) {
                    fStart   =   c;
                }
            }
            
            // found a blank
            // which ends a char if we have a char start and 
            // if it's greater than minWidth
            // or if we hit end of grid, the last thing is a char too
            else if ( (f[c]==0)  && (fStart >= 0) ) {
                
                fEnd        =   c-1;

                if ( ((fEnd - fStart)    +   1) >= minWidth ) {
                   
                    try {
                         if ( blanks >= whiteSpace ) {
                            //put the blanks into the result set
                            T b = (T) model.getClass()
                                .getConstructor(model.getClass(), int.class , int.class) 
                                .newInstance(this, fStart-blanks, fStart-1);

                            items.add(b);
                        }
                    
                        
                        T item = (T) model.getClass()
                                .getConstructor(model.getClass(), int.class , int.class) 
                                .newInstance(this, fStart, fEnd);

                        items.add(item);
                        zeroOut(fStart, fEnd);
                        fStart  =   -1;
                    }
                    catch ( NoSuchMethodException | InstantiationException | 
                            IllegalAccessException | InvocationTargetException ex ) {
                        Session.logMessageLine(prefix + "\tReflection failed");
                        Session.logMessageLine(ex.getMessage());
                        return null;
                    }
                    
                    blanks  =   0;
                }
            }
            else // part of a space
                blanks++;
        }
        
        return  items;
    }
    
    
    public  <T extends RGBGrid> ArrayList<T> breakIntoItemsLessThan(T model, int startRgb, int endRgb, int minWidth) 
    {                
        
        String  prefix          =   this.getClass().getSimpleName() + ":breakIntoItemsLessThan<>";
        
        ArrayList<T> items      =    new ArrayList<>();
        
        for (int minRgb = startRgb; minRgb <= endRgb; minRgb += 10 ) {
            fillGridLessThan(minRgb);
            
            // fill start and end
            int fStart = -1, fEnd;
            
            for (int c = 0; c<numCols(); c++) {

                // is this the start of a new char?
                if ( f[c] > 0 ) {
                    if ( fStart < 0 )
                        fStart   =   c;
                }
                
                // found a blank
                // might be a char
                else if ( f[c]==0  && (fStart >= 0) ) {
                    
                    fEnd        =   c-1;
                    
                    if ( ((fEnd - fStart)    +   1) >= minWidth ) {
                        try {

                            T item = (T) model.getClass()
                                    .getConstructor(model.getClass(), int.class , int.class ) 
                                    .newInstance(this, fStart, fEnd);

                            items.add(item);
                            zeroOut(fStart, fEnd);
                            fStart  =   -1;
                        }
                        catch ( NoSuchMethodException | InstantiationException | 
                                IllegalAccessException | InvocationTargetException e ) {
                            Session.logMessageLine(prefix + "\tFancy code failed");
                            Session.logMessageLine(e.getMessage());
                            return null;
                        }
                    }
                    
                }            
            }
             
            if ( debug() )
                items.forEach((  elem) -> { elem.printGrid();}  );
        }
        
        return  items;
    }
    
    
    
    
    public  <T extends RGBGrid> ArrayList<T> breakIntoItemsGreaterThan(T model, int startRgb, int endRgb, int minWidth) 
    {                
        
        String  prefix          =   this.getClass().getSimpleName() + ":breakIntoItemsGreaterThan<>";
        
        ArrayList<T> items      =    new ArrayList<>();
        
        for (int minRgb = startRgb; minRgb <= endRgb; minRgb += 10 ) {
            fillGridGreaterThan(minRgb);
            
            // fill start and end
            int fStart = -1, fEnd;
            
            for (int c = 0; c<numCols(); c++) {

                // is this the start of a new char?
                if ( f[c] > 0 ) {
                    if ( fStart < 0 )
                        fStart   =   c;
                }
                
                // found a blank
                // might be a char
                else if ( f[c]==0  && (fStart >= 0) ) {
                    
                    fEnd        =   c-1;
                    
                    if ( ((fEnd - fStart)    +   1) >= minWidth ) {
                        try {

                            T item = (T) model.getClass()
                                    .getConstructor(model.getClass(), int.class , int.class) 
                                    .newInstance(this, fStart, fEnd);

                            items.add(item);
                            zeroOut(fStart, fEnd);
                            fStart  =   -1;
                        }
                        catch ( NoSuchMethodException | InstantiationException | 
                                IllegalAccessException | InvocationTargetException e ) {
                            Session.logMessageLine(prefix + "\tFancy code failed");
                            Session.logMessageLine(e.getMessage());
                            return null;
                        }
                    }
                    
                }            
            }
             
            if ( debug() )
                items.forEach((  elem) -> { elem.printGrid();}  );
        }
        
        return  items;
    }
    
    
    public <T extends RGBGrid> T  merge(T b, int minrgb) {
        
        String  prefix  =   this.getClass().getSimpleName() + ":merge";
        
        if ( width() == 0 )
            return b;
        
        if ( b.width() == 0 )
            return null;
        
        int cw   =  width() + b.width();
        
        try {

            T merged = (T) this.getClass()
                    .getConstructor(int.class , int.class) 
                    .newInstance(this.height(), cw);
            
            int r, c, idx;
            
            for (c=0; c<width(); c++) {
                for (r=0; r<height(); r++) {
                    
                    merged.setPixel(r,c, getPixel(r,c));
                }
            }
            
            idx =  width();
            
            for (c=0; c<b.width(); c++) {
                for (r=0; r<b.height(); r++) {
                    
                    merged.setPixel(r,idx+c, b.getPixel(r,c));
                }
            }
            
            // set up the g and f arrays
            merged.fillGridLessThan(minrgb);
            
            return merged;
        }
        catch ( NoSuchMethodException | InstantiationException | 
                IllegalAccessException | InvocationTargetException e ) {
            Session.logMessageLine(prefix + "\tFancy code failed");
            Session.logMessageLine(e.getMessage());
            return null;
        }
                
    }
    
    
    // override with data specific cleaning functions
    void    clean() {
        
        boolean emptyCols   =   true;
        
        checkMoreCols:
        while ( emptyCols ) {

            for (int i=0; i<width(); i++) {
                if ( f[i] == 0 ) {
                    Session.logMessageLine("RGBGrid:Clean f_[" + i + "] = " + f[i] );
                    Session.logMessageLine("RGBGrid:Clean Width: " + width() );
                    deleteColumn(i);
                    continue checkMoreCols;
                }
            }
            break;
        }
        
    }
    
    //public void iterativeSplit(RGBGrid map, String chars) {
    public <T extends RGBGrid> String iterativeSplit(T model, String chars) { 

        int     startCol    =   0;
        int     endCol      =   model.width()-1;
        
        String  prefix      =   model.getClass().getSimpleName() + ":iterativeSplit";
        
        // start with N wide 
        // create a sub-map
        // solve it until we can't solve
        // then zero out the submap from the main
        
       
        while ( model.width() > 0 ) {
            
            Session.logMessageLine(prefix + "\tMap Width: " + model.width() );
            
            try {
                T sub = (T) model.getClass()
                        .getConstructor(model.getClass(), int.class , int.class) 
                        .newInstance(model, startCol, endCol);
                
                sub.clean();
                sub.printGrid();
                
                chars       +=  sub.charFromGrid();
                startCol    =   endCol+1;
                endCol      =   model.numCols()-1;
                
                Session.logMessageLine(prefix + "\tChars: " + chars);
                
                if ( startCol >= endCol)
                    break;
            }
            catch ( UnableToReadCharException e) {
                endCol--;
                                   
                if ( endCol <= 0 )
                    break;
            }
            catch ( NoSuchMethodException | InstantiationException | 
                    IllegalAccessException | InvocationTargetException ex ) {
                Session.logMessageLine(prefix + ":died and went to heaven. Exiting");
                return null;
            }
        }     
        
        return  chars;
    }
     
    
}
