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
public class RGBGridHandNumber extends RGBGrid {
    
    final   int COLLECTRGB  =   100;
    
    ArrayList<RGBGridHandNumber>  charsFound  =   new ArrayList<>();
    
    public  RGBGridHandNumber(BufferedImage b, int startX, int startY, int nRows, int nCols) {
         super(b, startX, startY, nRows, nCols);
    }
    
    public  RGBGridHandNumber(RGBGridHandNumber map, int start, int end) 
    {
         super(map, start, end);
    }
    
    @Override
    boolean greaterThan() {
        return false;
    }
    
    public  String    getData() {
        
        String  prefix  =   this.getClass().getSimpleName() + ":readNumber";
        
        // this should chop it into digits grouped by dark pixels
        ArrayList<RGBGridHandNumber>   digits  =  breakIntoItemsLessThan(this, 20, 60, 4);
                        
        String    number    =   "";
        
        for ( RGBGridHandNumber elem : digits ) {
            try {
                elem.fillGridLessThan(60);
                elem.clean();
                 
                if ( elem.width() > 0 )  {
                    number += elem.charFromGrid();
                    
                    if ( Session.logLevelSet(Session.logType.HAND_NUMBER)  )
                        Session.logMessageLine(prefix + "\t" + number);
                }
            }
            catch ( UnableToReadCharException e ) {
                if ( Session.logLevelSet(Session.logType.HAND_NUMBER)  ) {
                    Session.logMessageLine(prefix + "\t" + e.getMessage());
                    Session.logMessageLine(prefix + "\t" + number);
                }
            }
        }
        
        //digits.forEach( (elem) -> { elem.fillMap(60); elem.printGrid(); } );
        
        return number;
    }
    
    
    
    boolean acceptChar(String ch ) {
        return true;
    }
    
    
    
    @Override
    public  String  charFromGrid() 
    throws UnableToReadCharException
    {
        String  prefix  =   this.getClass().getSimpleName() + ":charFromGrid";
        String  ch      =   null;
        
        if ( Session.logLevelSet(Session.logType.HAND_NUMBER)  )
            setDebug(true);
        
        fillGridLessThan(100);
        
        if ( Session.logLevelSet(Session.logType.HAND_NUMBER)  ) {
            Session.logMessageLine(prefix + "\t*****************Char Grid*********");
            
            printGrid();
            Session.logMessageLine(prefix + "\t*****************End Char Grid*********");
        }
        
        int lc    =   width()-1;
        
        if ( Session.logLevelSet(Session.logType.HAND_NUMBER)  ) {
            Session.logMessageLine(prefix + "\t WIDTH: " + width());
            Session.logMessageLine(prefix + "\t Fir[0]: " + filledInRow(0));
            Session.logMessageLine(prefix + "\t Fir[1]: " + filledInRow(1));
        }
        
        if ( (filledInRow(0) == 0) && (filledInRow(1)==0) )
            return  "";
        else if ( (width()>=4) && barAcross(2) && barAcross(3) ) {
            return  "";
        }
        
        
        switch ( width() ) {
            case 0:
                ch  =   "";
                break;
            
            case 1:
                if ( filledInColumn(0) >= 8 )
                    ch  =   "1";
                break;
                
            case 2:
            case 3:
                // do nothing
                ch  =   "";
                break;
                
                
            case 4:
            case 5:                
                if ( filledInRow(5) == width() ) {
                    ch  =   "9";
                }
                else {
                    if ( !filledRowCol(2,lc) &&  !filledRowCol(2,lc-1) &&
                         !filledRowCol(2,lc-2) ) {
                        
                        if ( !indentLeftSide(1,7) )
                            ch  =   "6";
                        else
                            ch  =   "5";
                    }
                    else {
                        if ( indentLeftSide(3,5) ) {
                            // 2, 3, 7
                            if ( (filledInRow(0) >= width())  && ( filledInRow(8) <= 2) ) {
                                ch  =   "7";
                            }
                            else {
                                if ( filledRowCol(4,2) &&
                                     filledRowCol(5,1) ) {
                                    ch  =   "2";
                                }
                                else {
                                    if (filledInColumn(0) >= 5 ) 
                                        ch  =   "3";
                                }
                            }
                        }
                        else {
                            if ( openMiddle(0, 8, 2, 6) ) {
                                ch  =   "0";
                            }
                            else {
                                // 4, 6, 7
                                if ( (filledInRow(0) == width()) &&
                                          (filledInRow(1) == width()) ) {
                                        
                                    ch  =   "7";
                                }
                                else {
                                    if ( !filledRowCol(7,0) && !filledRowCol(7,1) &&
                                         !filledRowCol(8,0) && !filledRowCol(8,1)) {
                                        ch  =   "4";
                                    }
                                    else {
                                        if ( (filledInRow(4) >= 3) && !indentLeftSide(2,7) &&
                                             !indentLeftSide(2,7) )
                                            ch  =   "8";
                                    }
                                }
                            }
                            
                        } // indent left side 3-5
                    } // indent right side row 2
                } // filledRow(5)
                break;
                
            case 6:
                if ( openMiddle(0,8,2,6)) {
                    if ( !indentLeftSide(1,7) && !indentRightSide(1,7) )
                        ch  =   "0";
                }
                break;
                
            default:
                throw   new CharTooWideException(prefix + width());
                
        } // end switch
                        
        
        if ( ch != null ) {
            
            if ( acceptChar(ch) ) {
                
                if ( Session.logLevelSet(Session.logType.HAND_NUMBER)  )
                    Session.logMessageLine(prefix + "\t-=-=-=-=-=-=-=-=-=-==-=-=--->" + ch);
                
                setValue(ch);
                return  ch;
            }
            else {
                throw   new UnableToReadCharException(prefix + "\tUNABLE TO PROCESS:" + ch);
            }
        }
        
       
        
        throw   new UnableToReadCharException();
    }
    
}
