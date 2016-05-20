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
public class RGBGridShieldNumber extends RGBGrid {
    
    public  RGBGridShieldNumber (int nRows, int nCols) {
        super(nRows, nCols);
    }
    
    public  RGBGridShieldNumber(BufferedImage b, int startX, int startY, int nRows, int nCols) {
        super (b, startX, startY, nRows, nCols);
    }
              
    public  RGBGridShieldNumber(RGBGridShieldNumber map, int sCol, int eCol) {
        super(map, sCol, eCol);
    }
    
    
    @Override
    public String     charFromGrid() 
    throws  UnableToReadCharException {
        
        if ( Session.logLevelSet(Session.logType.SHIELD_NUMBER)  )
            setDebug(true);
        
        String  prefix  =   this.getClass().getSimpleName() + ":charFromGrid";
           
        if ( Session.logLevelSet(Session.logType.SHIELD_NUMBER)  ) 
            Session.logMessageLine(prefix + "\t--------------Start Filling and Printing Grid------------" );
        
        fillGridLessThan(70);
        
        if ( Session.logLevelSet(Session.logType.SHIELD_NUMBER)  ) {
            printGrid();
            Session.logMessageLine(prefix + "\t-------------Done Filling and Printing Grid---------------" );
        }
        
        String  value   =   "";
        int     lc      =   width()-1;
        int     i, j;
        boolean zero    =   false;
        
        
        if ( filledRowCol(2,0) && filledRowCol(3,0) && filledRowCol(4,0) &&
             filledRowCol(5,0) && filledRowCol(6,0) && filledRowCol(7,0) ) {
            
            if ( Session.logLevelSet(Session.logType.SHIELD_NUMBER)  )
                Session.logMessageLine(prefix + "\tCheck for Zero Passed First Test");
            
            
            for ( i=1; i< lc; i++) {
                if ( filledRowCol(0,i) && filledRowCol(8,i) ) {
                    
                    if ( Session.logLevelSet(Session.logType.SHIELD_NUMBER)  )
                        Session.logMessageLine(prefix + 
                            "\tCheck for Zero Candidate Column [" + i + "]");
                    
                    zero    =   true;
                    for ( j = 2; j<=6; j++) {
                        if ( filledRowCol(j,i) ) {
                            zero    =   false;
                        }
                    }
                    if ( zero )
                        break;
                }
            }
        }
        
        if (  zero ) {
            value   =  "0";
        }
        else if ( (width() == 1)  ) {
            if ( filledInColumn(0) == numRows() )
                value   =   "1";
        }
        else if ( (width() == 2)  ) {
            if ( filledInColumn(1) == numRows() )
                value   =   "1";
        }
        else if ( !filledRowCol(7,0) && !filledRowCol(7,1)  &&
             !filledRowCol(8,0) && !filledRowCol(8,1) ) {
            value   =   "4";
        }
        else if ( filledRowCol(0,0) &&  filledRowCol(0,1) &&
             filledRowCol(1,0) &&  filledRowCol(1,1) &&
            !filledRowCol(2,0) && !filledRowCol(2,1) ) {
            
            value   =   "7";
        }
        else if ( (!filledRowCol(5,0) || !filledRowCol(6,0)) &&
                   !filledRowCol(2,lc) ) {
            value   =   "5";
        }
        else if ( !filledRowCol(6,0) && !filledRowCol(6,1) && !filledRowCol(6,2) &&
                  filledRowCol(1,0)  && filledRowCol(2,0)  && filledRowCol(3,0)  &&
                  filledRowCol(4,0) ) {
            
            value   =   "9";
        }
        else if ( !filledRowCol(2,lc) ) {
            value   =   "6";
        }
        else if ( !filledRowCol(3,0) && !filledRowCol(4,0) && !filledRowCol(5,0) ) {
            // 2 or 3
           if ( !filledRowCol(7,lc) && !filledRowCol(6,lc)  )
               value    =   "2";
           else
               value    =   "3";  
        }
        else  {
            if ( filledRowCol(0,1) && filledRowCol(0,2) && filledRowCol(0,3) )
                value   =   "8";
            else if ( filledRowCol(5,lc) && filledRowCol(6,lc) && filledRowCol(7, lc) )
                value   =   "8";
        }
        
        
        if ( value.length() == 0 ) {
            if ( filledInColumn(0) == 2 )
                value   =   ""; // comma
            else
                throw   new UnableToReadCharException();
        }
        
        
        return value;
         
     }
    
    
    
}
