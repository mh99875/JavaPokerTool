/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author marcushaupt
 * @version     1.0
 */

public class RGBGridCharAnchor extends RGBGrid {
    
    ArrayList<RGBGridCharAnchor>  charsFound  =   new ArrayList<>();
    
    final   int COLLECTRGB  =   100;

    
    public  RGBGridCharAnchor(BufferedImage b, int startX, int startY, int nRows, int nCols) {
         super(b, startX, startY, nRows, nCols);
    }
    
    public  RGBGridCharAnchor(RGBGridCharAnchor map, int start, int end) 
    {
         super(map, start, end);
    }
    
    
    @Override
    public  String  charFromGrid() 
    throws  UnableToReadCharException {
        String  prefix = this.getClass().getSimpleName() + ":charFromGrid";
        
        if ( Session.logLevelSet(Session.logType.ANCHOR)  )
            setDebug(true);
        
        if ( Session.logLevelSet(Session.logType.ANCHOR)  ) 
            Session.logMessageLine(prefix + "\tInside Function: heightxwidth " +
                    height() + " x " + width() );
        
        if ( charsAbove(2) ) {
        
            if ( width() == 1 ) {
                if ( filledInRow(1) == 0 )
                    return  "i";
                else
                    return  "?";
            }
            
            if ( Session.logLevelSet(Session.logType.ANCHOR)  ) 
                Session.logMessageLine(prefix + "\tChars Above");
            
            // p, i
            if ( (filledInColumn(0) == height()) && (width() == 5) )
                return  "P";
            else if ( (filledInColumn(1) == height()) && (width() == 6) )
                return  "P";
            else if ( filledInRow(1) == 0 )
                return  "i";
        }
        else if ( width() == 7 ) {
            if ( Session.logLevelSet(Session.logType.ANCHOR)  ) 
                Session.logMessageLine(prefix + "\tWidth 7");
            
            // merged v and i
            if ( filledRowCol(0, width()-1) ) {
                if ( filledInRow(1) == 0 ) 
                    return  "vi";
            }
        }
        else  {
            if ( Session.logLevelSet(Session.logType.ANCHOR)  ) 
                Session.logMessageLine(prefix + "\tNo Chars Above");
            
            // r, e, v, i, o, u, s
            if ( width() <= 3 ) {
                if ( Session.logLevelSet(Session.logType.ANCHOR)  ) 
                    Session.logMessageLine(prefix + "\tWidth <= 3");
                        
                if ( filledInRow(height()-1) == width() )
                    return  "s";
                else
                    return  "r";
            }
            else if ( openMiddle(2, height()-1, 3, 5) ) {
                if ( Session.logLevelSet(Session.logType.ANCHOR)  ) 
                    Session.logMessageLine(prefix + "\tOpen middle");
                
                return  "o";
            }
            else {
                if ( indentTop(2) ) {
                    if ( Session.logLevelSet(Session.logType.ANCHOR)  ) 
                        Session.logMessageLine(prefix + "\tIndent Top");
                    
                    // v, u
                    if ( (filledInColumn(0) == 5) ||
                         (filledInColumn(width()-1) == 5) ) {
                        
                        if ( Session.logLevelSet(Session.logType.ANCHOR)  ) 
                            Session.logMessageLine(prefix + "\tFilled Up Column 0 or lc-1");
                        return  "u";
                        
                    }
                    else
                        return  "v";
                }
                else if ( indentLeftSide(2,height()-1) ) {
                    
                    if ( Session.logLevelSet(Session.logType.ANCHOR)  ) 
                        Session.logMessageLine(prefix + "\tIndent Left Side");
                    return  "s";
                }
                else  if ( filledInRow(4) == width() ) {
                    if ( Session.logLevelSet(Session.logType.ANCHOR)  ) 
                        Session.logMessageLine(prefix + "\tBar Row 4");
                    return  "e";
                }
            }
        }
        
        
        return  "?";
    }
    
    
    public  boolean matchesChars(String chars) 
    throws UnableToReadCharException
    {        
        String  prefix  =   this.getClass().getSimpleName() + ":matchesChars";
        
        fillGridGreaterThan(175);
        
        for (int minRgb = getMinrgb(); minRgb >= COLLECTRGB; minRgb -= 10 ) {
            fillGridGreaterThan(minRgb);
            
            // fill start and end
            int fStart = -1, fEnd;
            
            for (int c = 2; c<numCols(); c++) {

                // is this the start of a new char?
                if ( filledInColumn(c) > 0 ) {
                    if ( fStart < 0 )
                        fStart   =   c;
                }
                
                // found a blank
                // might be a char
                if ( filledInColumn(c) ==0  && (fStart >= 0) ) {
                    
                    fEnd        =   c-1;
                    charsFound.add(new RGBGridCharAnchor(this, fStart, fEnd));
                    zeroOut(fStart, fEnd);
                    fStart  =   -1;
                }            
            }
             
            if ( Session.logLevelSet(Session.logType.ANCHOR)  )
                charsFound.forEach( (  elem) -> { elem.printGrid();}  );
            
            boolean matched =   true;
            int     idx     =   0;
            int     missed  =   0;
            char    c;
            
            for ( RGBGridCharAnchor elem : charsFound ) {
                c   =   chars.charAt(idx++);
                
                if ( Session.logLevelSet(Session.logType.ANCHOR)  )
                    Session.logMessageLine("Matching: " + c);
                
                if ( !elem.matchesChar( c ) ) {
                    if ( Session.logLevelSet(Session.logType.ANCHOR)  )
                        Session.logMessageLine("No Match for : " + c);
                    
                    missed++;
                }
                else {
                    if ( Session.logLevelSet(Session.logType.ANCHOR)  )
                        Session.logMessageLine("Match for : " + c);
                    
                    if ( idx == chars.length() )
                        return true;
                }
            }
            
            if ( missed > 3 )
                throw   new UnableToReadCharException(prefix + "\tmissed " + missed );
        }
        
        
        return true;
    }
    
    
    
    boolean matchesChar(char c ) {
        
        int eCol    =    width()-1;
        
        switch ( c ) {
            
            case 'P':
                if ( ( filledInColumn(0) == 7)  && (width() == 4) )
                    return true;
                break;
                
            case 'r':
                if ( (width() == 3) && (filledInColumn(0) > 0) &&
                      !filledRowCol(0,0) && !filledRowCol(1,0) )
                    return true;
                break;
                
            case 'e':
                if ( (width() == 4) && (filledInColumn(0) > 0) &&
                      !filledRowCol(0,0) && !filledRowCol(1,0) ) {
                    
                    if ( barAcross(2) )
                        return true;
                }
                break;
                
            case 'v':
                if ( (width() == 4) && (filledInColumn(0) > 0) &&
                      !filledRowCol(0,0) && !filledRowCol(1,0) ) {
                    
                    if ( indentTop(2) )
                        return true;
                }
                break;
                
            case 'i':
                if ( (filledInColumn(0) == 6) && !filledRowCol(1,0) )
                    return true;
                break;
                
            case 'o':
                return  true;
                
            case 'u':
                if ( (width() == 5) && (filledInColumn(0) > 0) &&
                      !filledRowCol(0,0)  && !filledRowCol(1,0)  ) {
                    
                    if ( indentTop(2) )
                        return true;
                }
                break;
                
            case 's':
                if ( width() == 3 )
                    return true;
                
            default:
                return false;
        }
        
        return false;
    }
            
    
    
    
}
