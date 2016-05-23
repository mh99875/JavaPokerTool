/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;


import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

/**
 * * 
 * 
 * <p>Separate class to read Player Names</p>
 * 
 * <p>The functionality here will one day be completely merged into RGBGrid so that
 * toString() can be called but for now it remains a separate class</p>
 * 
 * 
 * <ol>
 * <li><b>String  cr(int s, BufferedImage b, int debugLevel ) </b>
 * throws  CantReadNameException, SeatXYException</li>
 * 
 * reads the RGBGrid read from the player's shield into a grid then calls
 * repairName() to clean up the mess
 * 
 * 
 * <li><b> String  {@literal repairName(ArrayList<RGBGridCharShield>)}  itemsFound, 
 * int revealRGB )</b></li>
 * </ol>
 * 
 * <p>checks each element in the list to see if it's a valid char</p>
 * 
 * <p>if it's not, it's either too wide or a piece of a split char</p>
 * 
 * <p>pieces of split chars are marked with "?" by <b>RGBGridCharShield</b></p>
 * 
 * <p>A "?" Grid may only merge with another "?" (split into two pieces) 
 * or with "l" (split into a piece that looks like an "l" and another piece)</p>
 * 
 * <p>A match is tested for both the char + left char and char + right char 
 * if the left and right chars are available for merger</p>
 * 
 * <p>Once all the merges have been made each char is checked for being joined
 * using <b>String  greedySeparateRight(RGBGridCharShield e)</b> which returns
 * the original char or a string of the peeled chars. Uses the greedy method so
 * it finds the longest possible valid char before peeling it off and trying to
 * find another char, so m is not seen as n + something for instance</p>
 * 
 * <p>Maybe this could be added to toString() sublcassed by RGBGridChar using a
 * class PlayerNameReader just to get things moving in the right direction...</p?
 * 
 * 
 * 
 *
 
 <p>Separate class to read Player Names</p?
 
 <p>The functionality here will one day be completely merged into RGBGrid so that
 toString() can be called but for now it remains a separate class</p>
 
 
 * <ol>
 <<li>1. <b>String  readShieldName(int s, BufferedImage b, int debugLevel ) </b>
 throws  CantReadNameException, SeatXYException</li>
 
 reads the RGBGrid read from the player's shield into a grid then calls
 repairName() to clean up the mess
 
 
 <li><b> String  {@literal repairName(ArrayList<RGBGridCharShield>)}  itemsFound, 
 int revealRGB )</b></li>
 * </ol>
 
 <p>checks each element in the list to see if it's a valid char</p>
 
 <p>if it's not, it's either too wide or a piece of a split char</p>
 
 <p>pieces of split chars are marked with "?" by <b>RGBGridCharShield</b></p>
 
 <p>A "?" Grid may only merge with another "?" (split into two pieces) 
 or with "l" (split into a piece that looks like an "l" and another piece)</p>
 
 <p>A match is tested for both the char + left char and char + right char 
 if the left and right chars are available for merger</p>
 
 <p>Once all the merges have been made each char is checked for being joined
 using <b>String  greedySeparateRight(RGBGridCharShield e)</b> which returns
 the original char or a string of the peeled chars. Uses the greedy method so
 it finds the longest possible valid char before peeling it off and trying to
 find another char, so m is not seen as n + something for instance</p>
 
 <p>Maybe this could be added to toString() sublcassed by RGBGridChar using a
 class PlayerNameReader just to get things moving in the right direction...</p>
 * 
 * @author Marcus Haupt
 * @version 1.0;
 * 
 */
public class PlayerNamesReader {
    PokerTable      table;

    // use the Game for info
    public PlayerNamesReader(PokerTable t, Boolean d) {
        table      =   t;
    }
    
    public  PokerTable      table()      {   return table;  }

    String  readShieldName(int s, BufferedImage b) 
    throws  CantReadNameException, SeatXYException
    {
        String  name;
               
        String  prefix  =   this.getClass().getSimpleName() + ":readShieldName[" + s + "]";
                
        int     i,j,x,y;
        int     c;
        int     startY ;
        int     startX;
               
        int     blackInRow;
        int     seatX           = table().seatX(s);
        int     seatY           = table().seatY(s);
        int     pixel;
        int     r;
        int     blankRow        =   -1;
        int     tx, ty, idx;
        int     []  vals        =   new int[20]; // max 20 rows with shadow above and below
        
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) )
            Session.logMessageLine(prefix + "\tReading Screen Name");
        
        if ( table().hasClock(s, b) ) {
            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                Session.logMessageLine(prefix + "\t++HAS CLOCK");
            throw    new CantReadNameException();
        }
        
        
        if ( table().isSittingOut(s, b) ) {
            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                Session.logMessageLine(prefix + "\tSitting Out");
            
            throw    new CantReadNameException();
        }
        
        if ( (seatX <= 0) || (seatY <= 0) )
            throw   new SeatXYException(prefix + "(" + seatX + "," + seatY + ")");
        
           
        for (y=5; y<=11; y++) {
            
            blackInRow = 0;
            for (x=5; x<=70; x++) {
                
                pixel   =   b.getRGB(seatX+x, seatY-y);
                r       =   CardUtilities.pixelRedValue(pixel);
                if (  r < 100 ) {
                    blackInRow++;
                }
            } 
                
            if ( blackInRow == 0 ) {
                blankRow    =   seatY-y;
                break;
            }
        }
        
        if ( blankRow < 0 ) { 
           throw new ActionNotNameException(prefix + "\tAction on Shield Instead of Name");
        }
        
        // find bottom line of name by coming up from the bottom
        
        for (idx=0; idx<20; idx++)
            vals[idx]    =   0;
        
        idx     =   0;
        
        // add the data for every row we'll need it to know if this
        // is a valid name to read
        for (ty = blankRow; ty > blankRow-20; ty-- ) {
            for ( tx = seatX + 10; tx <= seatX+70; tx++) {
                pixel   =   b.getRGB(tx, ty);
                r       =   CardUtilities.pixelRedValue(pixel);
                if ( r < 50 ) {
                    vals[idx]++;
                }
            }
            idx++;
        }
        
        int emptyP  =   0;
        for (idx=0; idx<20; idx++) {
            //if ( debug ) 
            //    Session.logMessageLine(prefix + "\t[" + idx + "] = " + vals[idx]);
            if ( vals[idx] == 0 )
                emptyP++;
        }
        
        if ( emptyP > 12) {
             if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) ) {
                Session.logMessageLine(prefix + "\tToo Many Empty Rows: " + emptyP );
                
                for (i=19; i>=0; i--)
                    Session.logMessageLine(prefix + "\tvals[" + i + "] = " + vals[i] );
            }
            
           throw new CantReadNameException(prefix + "\tToo Many Empty Rows: " + emptyP );
        }
        
        // the bottom of the base, row 9
        // is either at +5 or +6 from the base of the vals array
        
        if ( (vals[4]>0)    && (vals[5]>0) && 
             (vals[4] <10)  && (vals[5]<10) )  {
            startY  =   blankRow - (6  + 9);
        }
        else if ( vals[5] == 0 ) {
            startY  =   blankRow - (6  + 9);
        }
        else {
            startY  =   blankRow - (5  + 9);
        }
        
        if ( startY < 0 ) {
            throw new CantReadNameException(prefix + "\tCan't find startY row");
        }
         
        int             nCols;
        int             endX;
        
        startX  =   table().seatX(s) -1;
        endX    =   table().seatX(s) + 79;
        
        nCols   =   (endX - startX) + 2;
                
        RGBGridCharShield  rawName     =   new RGBGridCharShield(b, startX, startY, 12, nCols);
         
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
             rawName.setDebug(true);
         
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) ) {
            Session.logMessageLine(prefix + "\tNumcols: "       + rawName.numCols());
            Session.logMessageLine(prefix + "\tInititalized: "  + rawName.initialized());
        }
                
        ArrayList<RGBGridCharShield>  itemsFound  =   new ArrayList<>();
        
        final   int COLLECTRGB  =   40;
        final   int REVEALRGB   =   80;
        
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
            rawName.printMinRGB(COLLECTRGB);
        
        rawName.fillGridLessThan(COLLECTRGB);
        
        int blankRows=0;
        for (i=0; i<12; i++)
            if ( rawName.filledInRow(i) == 0 )
                blankRows++;
        
        if ( blankRows > 4 ) {
            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                Session.logMessageLine(prefix + "\tToo Many Blank Rows: " + blankRows);
            
            // two above and two below can be blank
            throw new CantReadNameException(prefix + "\tToo Many Blank Rows = " + blankRows);
        }
        
        int lc;
        // CLEAN LEFT EDGE
        if ( !rawName.filledRowCol(0,0) && !rawName.filledRowCol(1,0) ) {
            // the name is not part of the upper part of the shield
            // find the first space to the right
            lc=0;
            while ( rawName.filledInColumn(lc+1) != 0 )
                lc++;
            
            if ( rawName.filledInColumn(lc) < 6 ) {
                // not a d or J e.g.
                for (i=0; i<=lc; i++) {
                    rawName.clearGridPoint(0, i);
                    rawName.clearGridPoint(1, i);
                }
            }
            
            // if there is an open middle, we may have a p with the shield
            // being the tail, else delete the shield egde in column 0
            if ( !rawName.openMiddle(2, 9, 4, 7, 1, lc-1) ) {
                rawName.deleteColumn(0);
            }
        }
        
        int d=1;
        while ( ((rawName.filledInColumn(d) == 2) && rawName.filledRowCol(0,d) && 
                rawName.filledRowCol(1,d))
               ||
                ((rawName.filledInColumn(d) == 1) && rawName.filledRowCol(0,d)) ) {
            d++;
        }
            
        for (i=0; i<d; i++)
            rawName.deleteColumn(0);
               
        
        // CLEAN RIGHT EDGE
        //delete everything up to the last free space
        lc=rawName.width()-1;
        while ( rawName.filledInColumn(lc) > 0 ) {
            rawName.deleteColumn(rawName.width()-1);
            lc--;
        }
        
        
        // find the last blank column before the shield edge
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  ) {
            Session.logMessageLine(prefix + "\tAfter Cleaning Edges");
            rawName.printGrid();
        }
        
        
        // how many blank columns vs non-blank between the data endpoints?
        int startCol = -1, endCol = -1;
        for (i=5; i<nCols; i++) {
            if ( rawName.filledInColumn(i) > 0 ) {
                startCol    =   i;
                break;
            }
        }
        
        for (i=nCols-6; i>startCol; i--) {
            if ( rawName.filledInColumn(i) > 0 ) {
                endCol    =   i;
                break;
            }
        }
        
        int blankCols=0;
        for (i=startCol; i<=endCol; i++) {
            if ( rawName.filledInColumn(i) == 0 ) {
                blankCols++;
            }
        }
       
        if ( blankCols > (endCol-startCol) /2 )  {
            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  ) {
                Session.logMessageLine(prefix + "\tToo Many Blank Columns = " + blankCols);
                Session.logMessageLine(prefix + "\tStart col: " + startCol);
                Session.logMessageLine(prefix + "\tEnd col: " + endCol);
            }
            
            throw new CantReadNameException(prefix + "\tToo Many Blank Columns = " + blankCols);
        }
        
        // check for underscores 
        r = rawName.height()-1;
        for (c=0; c < rawName.numCols() - 6; c++) {
            if ( !rawName.getGrid(r, c) ) {
                if ( CardUtilities.AllRGBBelowValue(REVEALRGB, rawName.getPixel(r, c))   &&
                     CardUtilities.AllRGBBelowValue(REVEALRGB, rawName.getPixel(r, c+1)) &&
                     CardUtilities.AllRGBBelowValue(REVEALRGB, rawName.getPixel(r, c+2)) &&
                     CardUtilities.AllRGBBelowValue(REVEALRGB, rawName.getPixel(r, c+3)) &&
                     CardUtilities.AllRGBBelowValue(REVEALRGB, rawName.getPixel(r, c+4)) ) {

                    // leave a space if one is found
                    rawName.setGrid(r, c+1);
                    rawName.setGrid(r, c+2);
                    rawName.setGrid(r, c+3);
                    rawName.setGrid(r, c+4);
                    rawName.setGrid(r, c+5);
                }
            }
        }
        
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
            rawName.printGrid();        
        
        
         // fill start and end
        int fStart = -1, fEnd;
        for (c=0; c<rawName.numCols(); c++) {

            // is this the start of a new char?
            if ( rawName.filledInColumn(c) > 0 ) {
                if ( fStart < 0 )
                    fStart   =   c;
            }
                
            // found a blank or its the last item
            if ( ((c ==rawName.numCols()-1) || (rawName.filledInColumn(c)==0))  && (fStart >= 0) ) {
                    
                fEnd        =   c-1;
                                        
                RGBGridCharShield item = new RGBGridCharShield(rawName, fStart, fEnd);
                
                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) )
                    item.printMinRGB(REVEALRGB);
                
                item.fillGridLessThan(REVEALRGB);
                
                if ( fStart <= 5 )
                    item.cleanCharsNearShieldLeftEdge();
                else if ( fStart + item.width() >= 79 )
                    item.cleanCharsNearShieldRightEdge();
                
                if ( item.width() > 0 )
                    itemsFound.add(item);
                
                rawName.zeroOut(fStart, fEnd);
                
                fStart = -1;
            }
        }
       
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) )  {
            Session.logMessageLine(prefix + "\tChars Found@[" + COLLECTRGB + "]");    
            itemsFound.forEach( (e) -> e.printMinRGB(REVEALRGB));
        }
                
        // sort elements to put them back into their original order
        Collections.sort(itemsFound);
                
        int chNo=0;
        for ( RGBGridCharShield elem : itemsFound ) {
            
            elem.fillGridLessThan(REVEALRGB);
            elem.cleanGrid();
                        
            try {
                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) ) {
                    Session.logMessageLine(prefix + "\t-------------------------------------");
                    Session.logMessageLine(prefix + "\t-Read Char[" + chNo + "] From Grid---------");
                    Session.logMessageLine(prefix + "\t-------------------------------------");
                
                    elem.printGrid();
                }
                            
                
                elem.charFromGrid();
                
                
                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) ) {
                    Session.logMessageLine(prefix + "\tChar = " + elem.value() == null ? "NULL" : elem.value());
                
                    Session.logMessageLine(prefix + "\t-------------------------------------");
                    Session.logMessageLine(prefix + "\t-Done Reading Char[" + chNo + "]From Grid---------");
                    Session.logMessageLine(prefix + "\t-------------------------------------");
                }
                
            }
            catch ( UnableToReadCharException e) {
                // either needs to be merged or sliced
                elem.setValue("?");
                
                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) ) {
                    Session.logMessageLine(prefix + "\tUnable To read char: width: " + elem.width());
                    Session.logMessageLine(prefix + "\t" + e.getMessage() );
                }
            }
            
            chNo++;
        }
        
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) ) {
            itemsFound.forEach( (found) -> Session.logMessageLine(found.value()) );
        
            Session.logMessageLine(prefix + "\n\n\n\n\n\t********REPAIRING NAME**********\n\n\n\n");
        }
        
        name    =   repairName(itemsFound, REVEALRGB);
        
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) )
            Session.logMessageLine(prefix + "\tRepaired Name = " + name);
        
        return name;
    }
    
    
    boolean repairWith(RGBGridCharShield e) {
        
        String  prefix  =   this.getClass().getSimpleName() + ":repairWith[" + e.value() + "]";
        
        
        if ( e.width() == 1 ) {
            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) )
                Session.logMessageLine(prefix + "\tRepair: width " + e.width());
            return true;
        } else if (e.value().equals("?") || e.value().equals("n") || 
                  ((e.width() ==1) || (e.width()==2) && !e.value().equals("i"))
                
                ) {
            
            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) )
                Session.logMessageLine(prefix + "\tUse for Repair");
            return true;
        }
        
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) )
            Session.logMessageLine(prefix + "\tDon't Use for Repair");
        return  false;
    }
    
    
    String  findHiddenTorF(RGBGridCharShield e) {
        int lc  =   e.width()-1;
        
        String  prefix  =   this.getClass().getSimpleName() + ":findHiddenTorF";
        
        if ( e.filledInColumn(lc) >= 9 ) {
            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) ) {
                Session.logMessageLine(prefix + "\tFilled in Last Column >= 9");
                Session.logMessageLine(prefix + "\t + Below-30:\n"
                    + CardUtilities.pixelRGBValues(e.getPixel(0, lc)) + "\n" +
                      CardUtilities.pixelRGBValues(e.getPixel(1, lc)) + "\n" + 
                      CardUtilities.pixelRGBValues(e.getPixel(2, lc)) );
            }
            
            if ( CardUtilities.AllRGBBelowValue(30, e.getPixel(0, lc)) &&
                !CardUtilities.AllRGBBelowValue(30, e.getPixel(1, lc)) &&
                 CardUtilities.AllRGBBelowValue(30, e.getPixel(2, lc)) ) {
                
                return  "f";
                
            }
            else if (!CardUtilities.AllRGBBelowValue(30, e.getPixel(0, lc)) &&
                     !CardUtilities.AllRGBBelowValue(30, e.getPixel(1, lc)) &&
                      CardUtilities.AllRGBBelowValue(30, e.getPixel(2, lc)) ) {
                return  "t";   
            }           
        }    
        return  "";
    }
    
    
    
    String  repairName(ArrayList<RGBGridCharShield>  itemsFound, int revealRGB ) {
        String              prefix  =   this.getClass().getSimpleName() + ":repairName";
        int                 i;
        int                 len         =  itemsFound.size(); 
        RGBGridCharShield   e;
        RGBGridCharShield   mergedLeft  =   null, mergedRight   =   null;
        String              leftChar, rightChar;
        String              repaired;
        String              lValue;
        String              rValue;
        String              ch;
        
        boolean             debug   =   
                Session.logLevelSet(Session.logType.REPAIR) || Session.logLevelSet(Session.logType.MERGE) ||
                Session.logLevelSet(Session.logType.SHIELD_CHAR);
        
        if ( debug) {
            Session.logMessageLine(prefix + "\tEntering Fucnton: " + len + " Items to Consider");
        }
        
        // can we merge with this fragment
        boolean [] mergeable    =   new boolean[len];
        
        for (i=0; i<len; i++) {
            e   =   itemsFound.get(i);
            
            // merge with anything unknown, n and anything 1 column wide that's
            // not an i
            
            // if we have a blank char that's not empty, set to "?"
            if ( e.value().equals("") && !e.isEmpty() )
                e.setValue("?");
            
            mergeable[i] = e.value().equals("?") || e.value().equals("n") ||
                    (((e.width() == 1 ) || (e.width()==2)) && !e.value().equals("i")); // we only merge with n, l and ?
            
            if ( debug )
                Session.logMessageLine(prefix + "\tITEM[" + i + "] = " + e.value() + " is Mergable? " +
                        mergeable[i] );
            
        }
        
        if ( debug ) {
            for (i=0; i<len; i++) {
                Session.logMessageLine(prefix + "\tITEM[" + i + "] : " + itemsFound.get(i).value() );
            }
        }
        
        i=0;
        while (i < len ) {
            
            e   =   itemsFound.get(i);
            
            // see if we have a char or if we need to merge
            if ( debug )
                Session.logMessageLine(prefix + "\tITEM[" + i + "] Check if Char is Valid");
            
            try {
                e.charFromGrid();
                
                if (debug)
                    Session.logMessageLine(prefix + "\tITEM[" + i + "] is Valid: " + e.value());
            }
            catch ( UnableToReadCharException ex) {
                if ( debug )
                    Session.logMessageLine(prefix + "\tITEM[" + i + "] is Invalid: " + e.value());    
            }
          
            if ( debug ) {
                Session.logMessageLine(prefix + "\tITEM[" + i + "] Repair Needed for \"" +
                        e.value() + "\" " + 
                        e.value().equals("") + " width: " + e.width() );
                e.printGrid("ITEM[" + i + "]");
                
                if ( i > 0 )
                    Session.logMessageLine(prefix + "\tITEM[" + i + "] Can Merge With [" + (i-1) +"] " + mergeable[i-1]);
                else
                    Session.logMessageLine(prefix + "\tITEM[" + i + "] At Index 0 Can't Merge Left");
                
                if ( i+1 < len )
                    Session.logMessageLine(prefix + "\tITEM[" + i + "] Can Merge With [" + (i+1) + "] " + mergeable[i+1]);
                
                else
                     Session.logMessageLine(prefix + "\tITEM[" + i + "] At Last Index Can't Merge Right");
            }
            
            rightChar   =   leftChar    =   "";
            
            if ( e.value().equals("?") || (e.value().equals("") && e.width() > 0) ) {
                
                if ( debug )
                    Session.logMessageLine(prefix + "\tITEM[" + i + "] Needs to Merge"); 
                
                // merge or trim?
                // if there a char to the left?
                if (  i - 1 >= 0 ) {        
                    lValue  =   itemsFound.get(i-1).value();

                    if ( mergeable[i-1] || lValue.equals("n") ) {
                        if ( debug )
                            Session.logMessageLine(prefix + "\tITEM[" + i + "] Try Merge Left With [" + (i-1) + "]  = " + lValue);

                        if ( repairWith(itemsFound.get(i-1)) ) {
                            mergedLeft  =   itemsFound.get(i-1).merge(e,revealRGB);
                            leftChar    =   evalChar( mergedLeft );
                            
                            if (debug ) {
                                Session.logMessageLine(prefix + "\tITEM [" + i + "] Merged Left with [" + (i-1) + "] = " + lValue);
                                Session.logMessageLine(prefix + "\tITEM [" + i + "] Merger Left Result = " + leftChar);
                                mergedLeft.printGrid("MergedLeft");
                            }
                            
                            if (debug ) {
                                mergedLeft.printGrid("ITEM [" + i + "] Left");
                                Session.logMessageLine(prefix + "\tITEM [" + i + "] Left Char = " + leftChar);
                            }
                        }
                    }
                }

                if ( debug ) {
                    Session.logMessageLine(prefix + "\tITEM [" + i + "] Can merge right: " +
                            ((i+1) < itemsFound.size()) );
                }
                
                // is there a char to the right?
                if (  i + 1 < itemsFound.size() ) {
                    rValue  =   itemsFound.get(i+1).value();

                    if ( debug )
                        Session.logMessageLine(prefix + "\tITEM [" + i + "] Consider Merge Right With [" + (i+1) + "]  = " + rValue);

                    if ( repairWith(itemsFound.get(i+1)) ) {
                        mergedRight =   e.merge(itemsFound.get(i+1), revealRGB);   
                        rightChar   =   evalChar( mergedRight );
                        
                        if ( debug )  {
                            mergedRight.printGrid("\tITEM [" + i + "] Merged Right");
                            Session.logMessageLine(prefix + "\tITEM [" + i + "] Merged Right = " + rightChar);
                        }
                        
                        
                        //if ( Session.logLevelSet(Session.logType.MERGE) ) 
                        //    Session.removeLogLevel(Session.logType.SHIELD_CHAR);
                        
                        if ( debug ) {
                            Session.logMessageLine(prefix + "\tITEM [" + i + "] Merged Right with [" + (i+1) + "] = " + rValue);
                            Session.logMessageLine(prefix + "\tITEM [" + i + "] Merger Right Result = " + rightChar);
                            mergedRight.printGrid("MergedRight");
                        }
                        
                       if ( debug ) {
                            mergedRight.printGrid("ITEM [" + i + "] MergedRight");
                            Session.logMessageLine(prefix + "\tRight Char = " + rightChar);
                        }
                    }
                }
               
                // see if one of the merges succeeded
                // which one do we use?
                boolean useLeft     =   false;
                boolean useRight    =   false;
                
                if ( debug) {
                    Session.logMessageLine(prefix + "\tITEM [" + i + "] Merger Results Left: " + leftChar 
                            + " Len: " + leftChar.length() + " equals ? " + leftChar.equals("?"));
                    
                    Session.logMessageLine(prefix + "\tITEM [" + i + "] Merger Results Right: " 
                            + rightChar + " Len: " + rightChar.length() + " equals ?: " + rightChar.equals("?"));
                }
                
                
                if ( (rightChar.length() > 0) && !rightChar.equals("?") ) {
                    if ( debug ) 
                        Session.logMessageLine(prefix + "\tITEM [" + i + "] Right Chair Available for Use");
                    useRight    =   true;
                }
                
                if ( (leftChar.length() > 0) && !leftChar.equals("?") ) {
                    if ( debug) 
                        Session.logMessageLine(prefix + "\tITEM [" + i + "] Left Chair Available for Use");
                    useLeft =    true;
                }
                                
                // if we have both use right because the left was already matched (probably)
                if ( useRight && useLeft ) {
                    if ( debug )
                        Session.logMessageLine(prefix + "\tITEM [" + i + "] Matched Both Left and Right");
                    useRight    = true;
                    useLeft     = false;
                }
                        
                
                if ( useLeft ) {
                    if ( debug ) 
                            Session.logMessageLine(prefix + "\tITEM [" + i + "] Using Left Merge = " + leftChar);
                        
                        itemsFound.get(i-1).wipeOut();
                        itemsFound.set(i, mergedLeft);
                        
                        i   +=  1;
                }
                else if ( useRight ) {
                    if (debug) 
                            Session.logMessageLine(prefix + "\tITEM [" + i + "] Using Right Merge = " + rightChar);
                        
                        e.wipeOut();
                        itemsFound.set(i+1, mergedRight);
                        
                        i   +=  2;    
                }
                else 
                {
                    if ( debug )
                        Session.logMessageLine(prefix + "\tITEM [" + i + "] Check for F or T");
                    
                    ch = "";
                    
                    if ( (e.width() ==3) || ( e.width() == 4)) {
                        // might be a t or and f hidden in the muck 
                        ch  =   findHiddenTorF(e);
                    
                        if ( ch.length() > 0 )
                            e.setValue(ch);
                    }
                
                    if ( ch.length() == 0 ) {
                        // replace the joined with a new char whose
                        // value is the string of joineds
                        
                        if ( debug )
                            Session.logMessageLine(prefix + "\tITEM [" + i + "] Try Separating Joined Chars in " + e.value() );
                        
                        ch  =    greedySeparateRight(e);
                        
                        e.setValue(ch);
                    }
                    i++;
                }
            }
            else {
                // hunky-dory 
                i++;
            }            
        } 
        
        repaired = itemsFound.stream()
                .map(is -> is.value())
                .collect(Collectors.joining("") );
        
        if ( debug )
            Session.logMessageLine(prefix + "\tRepaired: " + repaired);
                
        return  repaired;
    }
    
    
    String  evalChar(RGBGridCharShield e) {
        String              prefix  =   this.getClass().getSimpleName() + ":evalChar";
        
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) ) {
            Session.logMessageLine(prefix + "\t===============Merged Eval============");
            e.printGrid();
        }
       
        
        try {
            e.charFromGrid();
            
            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) ) {
                Session.logMessageLine(prefix + "\tSuggesting: " + e.value());
                Session.logMessageLine(prefix + "\t==================================");
            }
             
            return  e.value();
        }
        catch ( UnableToReadCharException ex ) {
            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) ) {
                Session.logMessageLine(prefix + "\tSuggesting: (empty string)");
                Session.logMessageLine(prefix + "\t==================================");
            }
            return "";
        }
    }
    
    
    /**
     * Calls greedyPeelRight(grid)
     * 
     * @param e grid element to split
     * @return string peeled from the right
     */
    String  greedySeparateRight(RGBGridCharShield e) {
        String              chars       =   "";
        String              prefix      =   this.getClass().getSimpleName() + ":greedySeparateRight";
        RGBGridCharShield   peel        =   greedyPeelRight(e);
        int                 charsLeft   =   e.width();
        
        try {
            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) ) {
                Session.logMessageLine(prefix + "\tComplete Grid");
                e.printGrid();
                Session.logMessageLine(prefix + "\tRemoved");
            }
        
            while ( true ) {
                
                chars       +=  peel.value();
                charsLeft   -=  peel.width();
                
                // clear the bump under if it exists
                if ( peel.value().equals("j") || peel.value().equals("g") || peel.value().equals("y") ) {
                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) )
                        e.printGrid();
                    
                    if ( !e.filledRowCol(10,charsLeft-1) && e.filledRowCol(11,charsLeft-1) ) {
                        // underhang
                        e.clearGridPoint(11, charsLeft-1);
                    }
                }
            
                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) ) {
                    Session.logMessageLine(prefix + "\tPeeledChars: " + chars);
                    Session.logMessageLine(prefix + "\tChars Left: " + charsLeft);
                }
                                 
                peel = greedyPeelRight(new RGBGridCharShield(e, 0, charsLeft-1));
            
                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) ) {
                    peel.printGrid();
                    Session.logMessageLine(prefix + "\tPeeled From");
                    e.printGrid();
                }
                
                if ( charsLeft <= 0 )
                    break;
            }
        } catch ( NullPointerException ex) {
            //no more peels
        }
        
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) )
            Session.logMessageLine(prefix + "\tPeel Was Null: Result Len: " + chars.length());
        
        String  ret;
        
        switch ( chars.length() ) {
            case 0:
                ret         =   "";
                break;
                
            case 1:
                ret         =   chars;
                break;
                
            case 2:
                ret         =   "" + chars.charAt(1) + chars.charAt(0);
                break;
                
                
            case 3:
                ret         =   "" + chars.charAt(2) + chars.charAt(1) + chars.charAt(0);
                break;
                
                
            default:
                ret         =   chars;
        }
                
           
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) )
            Session.logMessageLine(prefix + "\tReturn Value = " + ret);
                
        return  ret;
    }
    
    
    
    // peel the widest possible char from the right side of this grid
    RGBGridCharShield  greedyPeelRight(RGBGridCharShield e) {
        
        String              prefix      =   this.getClass().getSimpleName() + ":greedyPeelRight";
        
        int                 eCol        =   e.width()-1;
        int                 sCol        =   e.width()-2;
        RGBGridCharShield   knownChar   =   null;
        RGBGridCharShield   item;
        
        while ( sCol >= 0 ) {    
            item = new RGBGridCharShield(e, sCol, eCol);
           
            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) ) {
                Session.logMessageLine(prefix + "\t.........Printing Sub Char from " + sCol + " to " + eCol + " .....");
                item.printGrid();
                Session.logMessageLine(prefix + "\t........Done Printing Sub Char........");
            }
            
            try {
                String  ch  =   item.charFromGrid();
                
                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) )
                    Session.logMessageLine(prefix + "\tFound Char: " + ch);
                
                knownChar   =   item;
            }
            catch ( UnableToReadCharException ex) {
                // don't leave function
                
                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) )
                    Session.logMessageLine(prefix + "\tRejected Char " +ex.getMessage());
            }
            
            sCol--;
        }
        
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) )
            Session.logMessageLine(prefix + "\tFinished Peeling Char");
        
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) ) {
            if ( knownChar != null )
                Session.logMessageLine(prefix + "\tPeeled Char:" + knownChar.value() + "\tWidth: " + knownChar.width());
            else
                Session.logMessageLine(prefix + "\tNo Chars Peeled");
        }
        
        return  knownChar;
    }
    
    
            
        
    
    
    
    RGBGridCharShield  separateJoinedChars(RGBGridCharShield e) {
        String              chars       =   "?";
        
        String  prefix      =   this.getClass().getSimpleName() + ":separateJoinedChars";
        
        Session.logMessageLine(prefix + "\tStart With Elem: " + e.width() + " Cols Wide");
        e.printGrid();
        
        // starting at the last column
        // add columns to until we find a char
        // delete this char and try again
        // until no more columns 
        
        RGBGridCharShield   back    =   extractRightChar(e);

        RGBGridCharShield   front   =   extractLeftChar(back, e);
        
        RGBGridCharShield   middle  =   null;
        
        
        if ( (front != null) && (back != null) ) {
            Session.logMessageLine(prefix + "\tFront Char: " + front.value());
            
            Session.logMessageLine(prefix + "\tBack Char:  " + back.value());
            
            int explWidth  =   front.width() + back.width();
            
            Session.logMessageLine(prefix + "\tTotal Width: " + e.width());
            Session.logMessageLine(prefix + "\tExplained Width: " + explWidth);
            
            int missing =  e.width() - explWidth;
            
            if ( missing > 0 ) {
                // we might have another char stuck in here
                middle = new RGBGridCharShield(e, front.width(), 
                        front.width() + (missing-1));
                
                String  mc = evalChar(middle);
                Session.logMessageLine(prefix + "\tChars in the middle");
                middle.printGrid();
            }
            
            if ( (middle != null) && ( middle.value() != null))
                chars   =   front.value()   +   middle.value() + back.value();
            else
                chars   =   front.value()   +   back.value();
        }

        e.setValue(chars);
        return e;
    }
    
    
    RGBGridCharShield  extractRightChar(RGBGridCharShield e) {
        
        String  prefix      =   this.getClass().getSimpleName() + ":extractRightChar";
        
        int eCol    =   e.width()-1;
        int sCol    =   e.width()-2;
        
        while ( sCol > 0 ) {
            
            RGBGridCharShield item = new RGBGridCharShield(e, sCol, eCol);
                        
            item.deepClean();
            item.fillMissingDataColumns();
            item.deleteSpuriousRows();
            
            Session.logMessageLine(prefix + "\t.........Printing Sub Char from " + sCol + " to " + eCol + " .....");
            item.printGrid();
            Session.logMessageLine(prefix + "\t........Done Printing Sub Char........");
            
            try {
                String  ch  =   item.charFromGrid();
                
                Session.logMessageLine(prefix + "\tFound Char: " + ch);
                
                return item;
            }
            catch ( UnableToReadCharException ex) {
                sCol--;
            }
        }
        return  null;
    }
    
    
    
    
    RGBGridCharShield  extractLeftChar(RGBGridCharShield right, RGBGridCharShield e) {
        
        String  prefix      =   this.getClass().getSimpleName() + ":extractLeftChar";
        
        int eCol    =   1;
        int sCol    =   0;
        
        int dataEnd =   e.width();
        
        if ( right != null )
            dataEnd -=  right.width();
        
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) )
            Session.logMessageLine(prefix + "\tMax Width For Left Char: " + dataEnd);
        
        RGBGridCharShield found =   null;
        
        while ( eCol < dataEnd ) {
            
            RGBGridCharShield item = new RGBGridCharShield(e, sCol, eCol);
            
            item.fillGridLessThan(80);
            
            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) ) {
                Session.logMessageLine(prefix + "\t.........Printing Left Char Fragment from " + sCol + " to " + eCol + " .....");
                item.printGrid();
                Session.logMessageLine(prefix + "\t........Done Printing Left Char Fragment........");
            }
            
            try {
                String  ch  =   item.charFromGrid();
                
                if ( ch != null ) {
                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR) )
                        Session.logMessageLine(prefix + "\tFound Char: " + ch);
                    
                    found   =   item;
                }
                
            }
            catch ( UnableToReadCharException ex) {
            }
            
            eCol++;
            
        }
        
        return found;
    }
    
    
}
