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
 *
 * @author marcushaupt
 */
public class RGBGridCardRank  extends RGBGrid {
    
    ArrayList<RGBGridCardRank>  charsFound  =   new ArrayList<>();
    
    final       int COLLECTRGB  =   100;
    
    Card.Rank   rank;

    
    public  RGBGridCardRank(BufferedImage b, int startX, int startY, int nRows, int nCols) {
         super(b, startX, startY, nRows, nCols);
    }
    
    public  RGBGridCardRank(RGBGrid grid, int sRow, int eRow, int sCol, int eCol) {
        super(grid, sRow, eRow, sCol, eCol);
    }
    
    public  RGBGridCardRank(RGBGridCardRank map, int start, int end) 
    {
         super(map, start, end);
    }
    
    public  Card.Rank  rankFromGrid() 
    throws  UnableToReadCharException {
        
        String  prefix  =   this.getClass().getSimpleName() + ":charFromGrid";
        
        rank            =   Card.Rank.UNKNOWN;
        
        int     tRow                =   0;
        int     bRow                =   height()-1;
        int     lCol                =   0;
        int     rCol                =   width()-1;
        
        
        for (int i=0; i<6; i++) {
            if ( filledInRow(tRow) < 2 )
                tRow++;
            else
                break;
        }
        
        
        for (int i=0; i<6; i++) {
            if ( filledInColumn(lCol) < 2 )
                lCol++;
            else
                break;
        }
         
        
        for (int i=0; i<6; i++) {
            if ( filledInColumn(rCol) < 2)
                rCol--;
        }
        
        for (int i=0; i<6; i++) {
            if ( filledInRow(bRow) < 2)
                bRow--;
        }
        
        if ( filledInColumn(0) == height() )
            lCol++;
        
        
        
        RGBGridCardRank icon    =   new RGBGridCardRank(this, tRow, bRow, lCol, rCol);
        return  icon.cardRank();
    }
    
   
    
    public  Card.Rank   cardRank() {
        String  prefix  =   this.getClass().getSimpleName() + ":charFromGrid";
                
        boolean upperRightOpen  =   upperRightEmptyNbyM(2,2);
        boolean upperLeftOpen   =   upperLeftEmptyNbyM(2,2);
        boolean lowerRightOpen  =   lowerRightEmptyNbyM(2,2);
        boolean lowerleftOpen   =   lowerLeftEmptyNbyM(2,2);
        
        int     botRow          =   height()-1;
                        
        if ( Session.logLevelSet(Session.logType.CARDRANK)  ) {
            printGrid();
            
            Session.logMessageLine(prefix + "\tHeight: " + height());
            Session.logMessageLine(prefix + "\tWidth: " + width());            
        }

        
       
        if ( upperLeftEmptyNbyM(3,3)  && upperRightEmptyNbyM(3,3) &&
            !lowerRightEmptyNbyM(3,3) && !lowerRightEmptyNbyM(3,3) ) {
            
            if ( Session.logLevelSet(Session.logType.CARDRANK)  ) {
                Session.logMessageLine(prefix + "\tA: Lower Right and Left filled");
                Session.logMessageLine(prefix + "\t====================>    A");
            }
            
            
            rank    =   Card.Rank.ACE;
            return  rank;
        }
        
        
        if ( indentLeftSideToDepth(4, 2, 6) && indentLeftSideToDepth(4, 7, height()-2) ) {
            if ( Session.logLevelSet(Session.logType.CARDRANK)  ) {
                Session.logMessageLine(prefix + "\t3,4,7 Two Left Side Indents");
                // 2, 3, 4, 7
            }

            if ( upperLeftEmptyNbyM(3,3) && lowerLeftEmptyNbyM(3,3) ) {
                if ( Session.logLevelSet(Session.logType.CARDRANK)  ) {
                    Session.logMessageLine(prefix + "\tb. Upper Left and Lower Left Empty");
                    Session.logMessageLine(prefix + "\t====================>    4");
                }
            
                rank    =   Card.Rank.FOUR;
                return  rank;    
            }
            else {
                for (int i=0; i<4; i++) {
                    if ( filledInRow(i) == width() ) {
                        if ( Session.logLevelSet(Session.logType.CARDRANK)  ) {
                            Session.logMessageLine(prefix + "\tc. Top Row Filled");
                            Session.logMessageLine(prefix + "\t====================>    7");
                        }
                        
                        rank    =   Card.Rank.SEVEN;
                        return  rank;    
                    }
                }
                
                if ( Session.logLevelSet(Session.logType.CARDRANK)  ) {
                    Session.logMessageLine(prefix + "\td. Not Upper Left and Lower Left Empty");
                }
                
                for (int i=1; i<8; i++) {
                    if ( filledRowCol(height()-i,width()-1) &&
                        !filledRowCol(height()-i, width()-3) && 
                        !filledRowCol(height()-i, width()-4) ) {
                            
                            if ( Session.logLevelSet(Session.logType.CARDRANK)  ) {
                                Session.logMessageLine(prefix + "\te. Hole in Row " + i );
                                Session.logMessageLine(prefix + "\t====================>    2");
                            }

                            rank    =   Card.Rank.TWO;
                            return  rank;
                    }
                }
                 
                if ( Session.logLevelSet(Session.logType.CARDRANK)  ) {
                    Session.logMessageLine(prefix + "\te. No Hole in Row");
                    Session.logMessageLine(prefix + "\t====================>    3");
                }
                
                rank    =   Card.Rank.THREE;
                return  rank;    
            }
        }    
        
        
        if ( upperEye() && (width()<=10) ) {
            
            if ( Session.logLevelSet(Session.logType.CARDRANK)  ) {
                Session.logMessageLine(prefix + "\t8, 9 Upper Eye");
            }
                
            if ( indentLeftSideToDepth(4, 2, height()-2) ) {
                if ( Session.logLevelSet(Session.logType.CARDRANK)  ) {
                    Session.logMessageLine(prefix + "\t9 Indent Left Side");
                     Session.logMessageLine(prefix + "\t====================>    9");
                }
                
                rank    =   Card.Rank.NINE;
                return  rank;
            }
            else {
                if ( Session.logLevelSet(Session.logType.CARDRANK)  ) {
                    Session.logMessageLine(prefix + "\tNo Indent Left Side");
                     Session.logMessageLine(prefix + "\t====================>    8");
                }
                
                rank    =   Card.Rank.EIGHT;
                return  rank;
            }
        }
        
        
        for (int i=1; i<=5; i++) {
            if ( filledInColumn(width()-i) == height() && (width()>=10) &&
                !lowerLeftEmptyNbyM(2,2)) {
                
                if ( Session.logLevelSet(Session.logType.CARDRANK)  ) {
                    Session.logMessageLine(prefix + "\tT,K: Filled In Column[" 
                            + (height()-i) + "] = " + height() + " and left corners filled");
                }
                
                if ( indentRightSideToDepth(3, 2, 7) ) {
                    if ( Session.logLevelSet(Session.logType.CARDRANK)  ) 
                        Session.logMessageLine(prefix + "\t====================>    K");
                    
                    rank    =   Card.Rank.KING;
                    return  rank;
                }
                else {
                    if ( Session.logLevelSet(Session.logType.CARDRANK)  ) 
                        Session.logMessageLine(prefix + "\t====================>    T");
                    
                    rank    =   Card.Rank.TEN;
                    return  rank;
                }
            }
        }
        
        
        // the madness that is a Jack
        if ( width() < 8 ) {
            if ( Session.logLevelSet(Session.logType.CARDRANK)  ) {
                Session.logMessageLine(prefix + "\tJ: Very Narrow");
                Session.logMessageLine(prefix + "\t====================>    J");
            }
            rank    =   Card.Rank.JACK;
            return  rank;
        }
        
        
        if ( lowerLeftEmptyNbyM(3,3) && bigEye() ) {
            
            if ( Session.logLevelSet(Session.logType.CARDRANK)  ) {
                Session.logMessageLine(prefix + "\tQ: Three Corners Full");
                Session.logMessageLine(prefix + "\t====================>    Q");
            }
            rank    =   Card.Rank.QUEEN;
            return  rank;    
        }
        
        
        
        if ( indentRightSideToDepth(4, 2, 7) ) {
           
            if ( Session.logLevelSet(Session.logType.CARDRANK)  ) {
                Session.logMessageLine(prefix + "\t5, K: Indent right Side");
            }
            
            if ( indentLeftSideToDepth(4, 7, height()-2) ) {
                if ( Session.logLevelSet(Session.logType.CARDRANK)  ) {
                    Session.logMessageLine(prefix + "\t5: Indent Left Side");
                    Session.logMessageLine(prefix + "\t====================>    5");
                }
                
                rank    =   Card.Rank.FIVE;
                return  rank;    
            }
            else {
                if ( Session.logLevelSet(Session.logType.CARDRANK)  ) {
                    Session.logMessageLine(prefix + "\t5b No : Indent Left Side");
                }
                
                if ( upperLeftEmptyNbyM(2,2)) {
                    if ( Session.logLevelSet(Session.logType.CARDRANK)  ) {
                        Session.logMessageLine(prefix + "\t5c Upper Left Empty");
                        Session.logMessageLine(prefix + "\t====================>    6");
                    }   
                    
                    rank    =   Card.Rank.SIX;
                    return  rank;    
                }
                else {
                    if ( Session.logLevelSet(Session.logType.CARDRANK)  ) {
                        Session.logMessageLine(prefix + "\t5d Should we be here?");
                        Session.logMessageLine(prefix + "\t====================>    K");
                    }  
                    
                    rank    =   Card.Rank.KING;
                    return  rank;   
                }
            }
        }
    
        rank    =   Card.Rank.UNKNOWN;
        return rank;
    }
}

    
