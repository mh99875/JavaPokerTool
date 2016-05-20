/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.awt.image.BufferedImage;

/**
 *
 * @author marcushaupt
 * @version     1.0
 */
public class RGBGridTableNumber extends RGBGrid {
    
    public  RGBGridTableNumber (int nRows, int nCols) {
        super(nRows, nCols);
    }
    
    public  RGBGridTableNumber(BufferedImage b, int startX, int startY, int nRows, int nCols) {
        super (b, startX, startY, nRows, nCols);
    }
              
    public  RGBGridTableNumber(RGBGridTableNumber map, int sCol, int eCol) {
        super(map, sCol, eCol);
    }
    
    @Override 
    public int  maxItemWidth() {
        return 5;
    }
    
    @Override
    public  String  charFromGrid() 
    throws  UnableToReadCharException {
        
       
        String  prefix  =   this.getClass().getSimpleName() + ":charFromGrid";
        int lc          =   width()-1;
        String  value   =   "";
        
        if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  ) {
            Session.logMessageLine(prefix + "\tWidth: " + width());
            Session.logMessageLine(prefix + "\tLast Column: " + filledInColumn(width()-1) );
        }
            
        
        switch( width() ) {
            case 0:
            case 1:
                return "";
                
            case 2:
                 if ( filledInColumn(0) == 1 && (filledInColumn(1)==height()) && filledRowCol(1,0) )
                    value   =   "1";
                break;
                
                
            case 3:
            case 4:
            case 5:
                if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  ) {
                    Session.logMessageLine(prefix + "\tChar Width: " + width());
                    Session.logMessageLine(prefix + "\tRow  Width: " + filledInRow(4));
                }
                
                if ( ((filledInRow(4) == width()) || (filledInRow(4) == width()-1)) &&
                     !filledRowCol(5,0) && !filledRowCol(6,0) &&
                     !filledRowCol(5,1) && !filledRowCol(6,1) ) {
                    
                    if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                        Session.logMessageLine(prefix + "\t--------------------->4-bar");
                        
                    value =  "4";
                }
                else {
                    if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                        Session.logMessageLine(prefix + "\t--------------------->No 4-bar");
                    
                    if ( (width() == 3) && (filledInColumn(lc) == height()) &&
                         ((filledInColumn(0)==1) || (filledInColumn(0)==2)) ) {
                        if ( filledRowCol(1,0)) {
                            if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                Session.logMessageLine(prefix + "\t---------------------3 cols last filled, first 1 or 2");
                                    
                            value =  "1";
                        }
                    }
                    else {
                        if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                Session.logMessageLine(prefix + "\t---------------------Not 3 cols last filled, first 1 or 2");
                        
                        if ( indentLeftSide(1,5) ) {
                            if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                Session.logMessageLine(prefix + "\t--------------------->Indented Left Side");
                            //2,3,5,7
                            
                            if ( indentRightSide(1,5) ) {
                                if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                    Session.logMessageLine(prefix + "\t--------------------->Indented Right Side");
                                // 2,5,7
                                
                                if ( !indentLeftSide(1,2) && indentRightSide(1,2)) {
                                    if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                        Session.logMessageLine(prefix + "\t--------------------->5-indent pattern on top");
                                    value   =   "5";
                                }
                                else {
                                    if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                        Session.logMessageLine(prefix + "\t--------------------->No 5-indent pattern on top");
                                    
                                    if ( filledInRow(6) == width() ) {
                                        if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                                Session.logMessageLine(prefix + "\t--------------------->Bottom Row Full");
                                        value =  "2";
                                    }
                                    else {
                                        if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                            Session.logMessageLine(prefix + "\t--------------------->Bottom Row Not Full");
                                        if ( filledInRow(0) == width() )
                                            value   =   "7";
                                    }
                                }
                            }
                            else if ( (!filledRowCol(2,0) &&
                                       filledRowCol(3,0) &&
                                      !filledRowCol(4,0) ) 
                                    
                                    ||
                                    
                                      (!filledRowCol(2,0)  && !filledRowCol(2,1) &&
                                       !filledRowCol(3,0)  &&  filledRowCol(3,1) &&
                                       !filledRowCol(4,0)  && !filledRowCol(4,1) ) 
                                    
                                    ||
                                    
                                    (!filledRowCol(2,0)  && !filledRowCol(2,1) && !filledRowCol(2,2) &&
                                     !filledRowCol(3,0)  && !filledRowCol(3,1) && !filledRowCol(2,2) &&
                                     !filledRowCol(4,0)  && !filledRowCol(4,1) && !filledRowCol(4,2) )
                                    
                                    ) {
                                if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                    Session.logMessageLine(prefix + "\t--------------------->Prong of a Three");
                                value = "3";
                            }
                            else {
                                 if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                    Session.logMessageLine(prefix + "\t--------------------->No Prong of a Three");
                            }
                        }
                        else {
                            if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                Session.logMessageLine(prefix + "\t--------------------->not Indented Left Side");
                            
                            if( openMiddle(0, 6, 2, 4) ) {
                                if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                    Session.logMessageLine(prefix + "\t--------------------->Open Middle.");
                                value =  "0";
                            }
                            else {
                                if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                    Session.logMessageLine(prefix + "\t--------------------->No Open Middle.");
                                
                                //6, 8, 9 
                                
                                if ( filledInRow(4) == width()-1 ) {
                                    if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                        Session.logMessageLine(prefix + "\t--------------------->9-bar");
                        
                                    if ( !indentLeftSide(2,5) ) {
                                        if ( !filledRowCol(4,0)) {
                                            if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                                Session.logMessageLine(prefix + "\t--------------------->9-nick on left");
                                            
                                            value =  "9";
                                        }
                                        else {
                                            if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                                Session.logMessageLine(prefix + "\t--------------------->No 9-nick on left");
                                            
                                            if ( !filledRowCol(2,lc-1) && !filledRowCol(2,lc) ) {
                                                if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                                    Session.logMessageLine(prefix + "\t--------------------->6-nick on right");
                                                
                                                value   =   "6";
                                            }
                                            else {
                                                if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                                    Session.logMessageLine(prefix + "\t--------------------->No 6-nick on right");
                                                
                                                value = "8";
                                            }
                                        }
                                    }
                                }
                                else {
                                    if ( (filledInRow(0) >= 3 ) && (filledInRow(0)<=6) &&
                                         (filledRowCol(1,0) && filledRowCol(2,0)) &&
                                         !(filledRowCol(4,0)) ) {
                                        
                                        if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                            Session.logMessageLine(prefix + "\t--------------------->Left Column of a 9");
                                        
                                        value   =   "9";
                                    }
                                    else {
                                        if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                            Session.logMessageLine(prefix + "\t--------------------->Not the Left Column of a 9");
                                    
                                    
                                        if ( (filledInRow(3) >= 3) && filledRowCol(3,1) && filledRowCol(3,2) && filledRowCol(3,3) ) {
                                            if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                                Session.logMessageLine(prefix + "\t--------------------->8-bar");
                                        
                                            if ( !filledRowCol(2,lc) && (filledInColumn(lc) > 1) ){
                                                if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                                        Session.logMessageLine(prefix + "\t--------------------->6-nick on right");
                                                value   =   "6";
                                            }
                                            else  {
                                                if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                                    Session.logMessageLine(prefix + "\t--------------------->No 6-nick on right");
                                            
                                                value   =   "8";
                                            }
                                        }
                                        else {
                                            if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                                Session.logMessageLine(prefix + "\t--------------------->No 8-bar");
                                    
                                            if ( (filledInRow(0)<=5) && filledRowCol(1,0) && filledRowCol(2,0) &&
                                                  (filledInRow(6) >= 2)  
                                                    ) {
                                                if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                                    Session.logMessageLine(prefix + "\t--------------------->Mangled 9");
                                                value =  "9";
                                            }
                                            else {
                                                if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
                                                    Session.logMessageLine(prefix + "\t--------------------->Not Mangled 9");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                break;
                   
                
            default: 
                // break this char into chunks
                throw   new CharTooWideException(prefix);
                           
        }
        
        
        if ( value.length() == 0 )
            throw   new UnableToReadCharException(prefix);
        
        this.setValue(value);
        
        if ( Session.logLevelSet(Session.logType.TABLE_NUMBER)  )
            Session.logMessageLine(prefix + "\t-------------->" + value);
        
        return value;
    }
    
    
}
