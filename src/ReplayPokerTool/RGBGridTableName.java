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
 * 
 */
public class RGBGridTableName extends RGBGrid {
    
    ArrayList<RGBGridCharAnchor>  charsFound  =   new ArrayList<>();
    
    final   int BOXBOTTOM   =   9;
    final   int BOXTOP      =   3;
    
    
    public  RGBGridTableName(BufferedImage b, int startX, int startY, int nRows, int nCols) {
         super(b, startX, startY, nRows, nCols);
    }
    
    public  RGBGridTableName(RGBGridTableName map, int start, int end) 
    {
         super(map, start, end);
    }
    
    
    @Override
    public  String  charFromGrid() 
    throws UnableToReadCharException
    {
        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  )
            setDebug(true);
        
        String  prefix  =   this.getClass().getSimpleName() + ":charFromGrid";
        String  ch      =   "?";
        
        
        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) {
            Session.logMessageLine(prefix + "\t==============Print Grid\n\n");
            printGrid();
            Session.logMessageLine(prefix + "\t=======Done Printing Grid\n\n");      
        }
        
        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) {
             Session.logMessageLine(prefix + "\tWIDTH: " + width());
             Session.logMessageLine(prefix + "\tIs Empty: " + isEmpty());
        }
        
        if ( isEmpty() ) {
            if ( width() > 0 )
                return  " ";
            else
                return  "";
        }
         
        
        switch ( width() ) {

            case 1:
            case 2:
            case 3:
                if ( charsAbove(BOXTOP) )
                    ch =  processCharAboveNarrow();

                else if ( charsBelow(BOXBOTTOM))
                    ch =  processCharBelowNarrow();

                else {
                    if ( (width() ==3) && (filledInColumn(0)>=7) && 
                         (filledInColumn(2) > 0) && (filledInColumn(2) <= 2) )
                        ch =  "r";
                }
                break;

            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                if ( charsAbove(BOXTOP) ) {
                    if ( charsBelow(BOXBOTTOM) ) {
                        ch =  processCharsAboveAndBelowBox();
                        
                        if ( ch.equals("?") )
                            throw new CharTooWideException(prefix);
                    }
                    else
                        ch = processCharAboveBox();

                } else if ( charsBelow(BOXBOTTOM))
                    ch =   processCharBelowBox();
                else 
                    ch = processCharInsideBox();
                break;

            // anything 9 or wider is an M, W or joined chars 
            case 9:
            case 10:
                ch =  processWideChar();
                break;

            default:
                throw new CharTooWideException(prefix);
        }
        
        if ( ch.equals("?") ) {
            throw new UnableToReadCharException(prefix);
        }
        
        return ch;
    }
            
    
    public  String  processWideChar() {
        String  prefix  =   this.getClass().getSimpleName() + ":processWideChar";
        
        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
            Session.logMessageLine(prefix + "\tWidth = " + width());
                
        boolean possbleM  =   false;
        
        int     lc          =   width()-1;
        
        int     topRow      =   0;
        if ( filledInRow(topRow) == 0 )
            topRow          =   1;
        
        if ( filledRowCol(BOXBOTTOM,0) && filledRowCol(BOXBOTTOM,4) && filledRowCol(BOXBOTTOM,lc) )
            possbleM  =   true;
        
        if ( filledRowCol(BOXBOTTOM,0) && filledRowCol(BOXBOTTOM,5) && filledRowCol(BOXBOTTOM,lc) )
            possbleM  =   true;

        if ( charsBelow(BOXBOTTOM))
            possbleM   =   false;
        
        if ( possbleM ) {
            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                Session.logMessageLine(prefix + "\tPossible = M/m");
            
            int blanks=0;
            for (int i=0; i<width(); i++)
                if ( !filledRowCol(BOXBOTTOM, i) )
                    blanks++;

            if ( blanks >= 3 ) {
                if ( charsAbove(BOXTOP) ) {
                    if ( (filledInColumn(lc)>=9) && (filledInColumn(lc-1)>=9) )
                        return "M";
                } else {
                    return  "m";
                }
            }
            else {
                 if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                Session.logMessageLine(prefix + "\tNot enough blanks on bottom row");
            }
        }
        else {
            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                Session.logMessageLine(prefix + "\tNot the M/m Footprint");
            
            if ( charsAbove(BOXTOP) ) {
                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                    Session.logMessageLine(prefix + "\tChars Above");
                
                if ( filledInRow(6) >= 5 ) {
                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                        Session.logMessageLine(prefix + "\tFilled in Row 6");
                    
                    if ( (filledInRow(topRow)>0) && (filledInRow(topRow)<=3) &&
                         (filledInColumn(0)<=4)
                            ) {
                        if ( !filledRowCol(0,0) ) {
                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                Session.logMessageLine(prefix + "\tFilled in Row " + topRow + "<=3");
                            return  "A";
                        }
                    }
                }
                else {
                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                        Session.logMessageLine(prefix + "\tNot Filled in Row 6");
                    
                    if ( indentRightSide(2,7) ) {
                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                            Session.logMessageLine(prefix + "\tIndented Right Side");
                        
                        if ( (filledInRow(5) >= 5)  && !indentLeftSide(2,8) ){
                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                Session.logMessageLine(prefix + "\tG bar");
                            return "G";
                        }
                        else {
                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                Session.logMessageLine(prefix + "\tNo G bar");
                        }
                    }
                }
            }
            else if ( !charsBelow(BOXBOTTOM) ) {
                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                    Session.logMessageLine(prefix + "\tAll chars inside box");
                                
                if ( (filledInColumn(0) >= 1)  && (filledInColumn(0) <= 4) &&
                     (filledInColumn(lc) >= 1) && (filledInColumn(0) <= 4) ) {
                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                        Session.logMessageLine(prefix + "\tEnd Columns OK for w");
                    
                    // could be a w
                    if ( filledRowCol(BOXTOP,0) && !filledRowCol(BOXBOTTOM,0) &&
                         filledRowCol(BOXTOP,lc) && !filledRowCol(BOXBOTTOM,lc) ) {
                         if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                            Session.logMessageLine(prefix + "\tRight Corners for w");
                         
                        if ( filledRowCol(BOXBOTTOM,2) || filledRowCol(BOXBOTTOM,3) ) {
                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                Session.logMessageLine(prefix + "\tInitial columns of a w");
                            
                            if ( !filledRowCol(BOXTOP,2) && !filledRowCol(BOXTOP,3) ) {
                                return "w";
                            }
                            else {
                                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                    Session.logMessageLine(prefix + "\tWrong columns top for w");
                            }
                        }
                        else {
                             if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                Session.logMessageLine(prefix + "\tWrong initial columns for w");
                        }
                    }
                    else {
                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                            Session.logMessageLine(prefix + "\tWrong Corners for w");
                    }
                } else {
                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                        Session.logMessageLine(prefix + "\tEnd Columns Wrong for w");
                    
                }
                
            }
            else {
                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                    Session.logMessageLine(prefix + "\tChars Below");
            }
        }
        
        return  "?";
    }
    
    
    public  String  processCharBelowNarrow() {
        String  prefix  =   this.getClass().getSimpleName() + ":processCharBelowNarrow";
        
        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
            Session.logMessageLine(prefix + "\tNarrow Below: width = " + width());
                
        return "?";
    }
    
    
     public  String  processCharAboveNarrow() {
        String  prefix  =   this.getClass().getSimpleName() + ":processCharAboveNarrow";
    
        int     topRow=0;
        if ( filledInRow(0) == 0 )
            topRow = 1;
        
        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
            Session.logMessageLine(prefix + "\tNarrow Above: width = " + width());
        
        switch ( width() ) {
            
            case 1:
            case 2:
                if ( (filledInRow(topRow+1) == 0 ) || (filledInRow(topRow+2) == 0 ) ) {
                    if ( !charsBelow(BOXBOTTOM))
                        return "i";
                } else if ( (filledInColumn(0) >= 9) || ( filledInColumn(1) >= 9) )
                    return  "l";
                break;
                
            case 3:
                
                if ((filledInColumn(0) > 0) &&  (filledInColumn(0) <= 2) &&
                    (filledInColumn(1) > 0) &&  (filledInColumn(1) <= 2) &&
                    (filledInColumn(2) >= 9 ) )
                    return "1";
                else {
                    if ( (filledInColumn(2) >= 9) && 
                         ((filledInRow(topRow+1) == 0 ) || (filledInRow(topRow+2) == 0) ) ) {
                        if ( charsBelow(BOXBOTTOM))
                            return "j";
                    } 
                }
                break;
        }
                
        return "?";
    }
    
        
    public  String  processCharInsideBox() {
         String  prefix  =   this.getClass().getSimpleName() + ":processCharsInsideBox";
        
        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
            Session.logMessageLine(prefix + "\tChars inside Box");
         
         
        if ( (filledInRow(BOXTOP) == 0) && (filledInRow(BOXBOTTOM) == 0) ) {
            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                Session.logMessageLine(prefix + "\tEmpty First and Last for of Box");
            
            if ( filledInRow(6) == width() )
                return "-";
        }
        else {
            if ( indentLeftSide(4,7) ) {
                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                    Session.logMessageLine(prefix + "\tIndent Left Side (4-7)");
                
                if ( indentRightSide(4,7) ) {
                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                        Session.logMessageLine(prefix + "\tIndent Right Side(4,7)");
                    
                    if ( (filledInRow(6) >= 4) && 
                        ((filledInColumn(0)>=4) || (filledInColumn(1)>=6))
                            ){
                        
                        if ( width() <= 6) {
                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                Session.logMessageLine(prefix + "\tFilled in row(6)");
                        
                            return "s";
                        }
                    }
                }
                else {
                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                        Session.logMessageLine(prefix + "\tIndent Left but not Right Side");
                    //a,s,x,z
                    if ( !openMiddle(3, BOXBOTTOM, 4, 7, 1, width()-2) )
                        return  "a";
                    
                }
            } 
            else {
                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                    Session.logMessageLine(prefix + "\tNo Indent Left Side");
                
                if ( indentRightSide(4,7) ) {
                    // c, e
                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                        Session.logMessageLine(prefix + "\tIndent Right Side(4,7)");
                    
                    if ( filledInRow(6) >= width()-1)
                        return  "e";
                    else {
                        if ( filledInRow(BOXBOTTOM) <= 2 ) {
                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                Session.logMessageLine(prefix + "\tFilled in Row(BOXBOTTOM) <=2");
                            
                            return "r";
                        } else if ( width() <= 6 ) {
                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                Session.logMessageLine(prefix + "\tFilled in Row(BOXBOTTOM) > 2");
                            return  "c";
                        }
                    }
                }
                else {
                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                        Session.logMessageLine(prefix + "\tNo Indent Right Side");
                    
                    if ( openMiddle(3, BOXBOTTOM, 4, 7, 1, width()-2) ) {
                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                            Session.logMessageLine(prefix + "\tOpen Middle");
                        
                        return  "o";
                    }
                    else {
                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                            Session.logMessageLine(prefix + "\tNo Open Middle");
                        
                        if ( indentBottom(BOXBOTTOM,1,width()-2) ) {
                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                Session.logMessageLine(prefix + "\tIndent Bottom");
                            
                            return  "n";
                        }
                        else {
                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                Session.logMessageLine(prefix + "\tNo Indent Bottom");
                            
                            if ( indentTop(BOXTOP, 1, width()-2) ) {
                                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                    Session.logMessageLine(prefix + "\tIndent Top");
                                
                                return  "u";
                            }
                        }
                    }
                }
                
            }
        }
        
        return "?";
    }
    
    public  String  processCharBelowBox() {
        String  prefix  =   this.getClass().getSimpleName() + ":processCharBelowBox";
         
        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
            Session.logMessageLine(prefix + "\tChars Below Box");
        
                
        // first first columns only
         if ( (width()==5) || (width()==6) && indentTop(3, 1, width()-2) )  {
            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                Session.logMessageLine(prefix + "\tIndent Top");
             return "y";
         }
         else {
            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                Session.logMessageLine(prefix + "\tNo Indent Top");
             
            if ( filledInRow(11) >= width() - 2 ) {
                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                    Session.logMessageLine(prefix + "\tWidth Row(11) == wide enough for a g");
                
                return  "g";
            }
            else {
                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                    Session.logMessageLine(prefix + "\tWidth Row(11) == not wide enough for a g");
                
                if ( openMiddle(BOXTOP, BOXBOTTOM, 5, 7, 1, width()-2) ) {
                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                        Session.logMessageLine(prefix + "\tOpen Middle");
                    
                    return  "p";
                }
            }            
         }
         
        return "?";
     }
    
    
    public  String  processCharsAboveAndBelowBox() {
        String  prefix  =   this.getClass().getSimpleName() + ":processCharsAboveAndBelowBox";
        
        int     topRow=0;
        if ( filledInRow(0) == 0 )
            topRow = 1;
          
        return  "?";
    }
    
    
    public  String  processCharAboveBox() {
        String  prefix  =   this.getClass().getSimpleName() + ":processCharAboveBox";
        
       
        
        int     topRow=0;
        if ( filledInRow(0) == 0 )
            topRow = 1;
                    
        int     lc      =   width()-1;
        
        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  )  {
            Session.logMessageLine(prefix + "\tChars Above Box");
            
            if ( topRow == 1 )
                Session.logMessageLine(prefix + "\tGhost Row @ 0");
        }
        
        
        if ( (filledInRow(0) == width()) || ( filledInRow(1) == width()) && 
             (indentLeftSide(3,7) || indentRightSide(3,7) ) ) {
            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                Session.logMessageLine(prefix + "\tFilled Row 0 or 1 = width plus indented left or right ");
            // 7, T, 5, F
                    
            if ( lowerLeftBoxEmpty() && lowerRightBoxEmpty() ) {
                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                    Session.logMessageLine(prefix + "\tLower Left and Right boxes empty");
                 
                    // T or 7
                return  "T";
            }
            else {
                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                    Session.logMessageLine(prefix + "\tLower Left or Right box filled");
                
                if ( filledInColumn(0) >= 9 ) {
                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                        Session.logMessageLine(prefix + "\tFull column 0");
                    
                    if ( filledInRow(BOXBOTTOM) >= width()-1) {
                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                            Session.logMessageLine(prefix + "\tFull Bottom Row");
                        
                        return  "E";
                    }
                    else {
                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                            Session.logMessageLine(prefix + "\tNot Full Bottom Row");
                        
                        return  "F";
                    }
                }
                else {
                    if ( filledInRow(BOXBOTTOM) <= 3) {
                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                            Session.logMessageLine(prefix + "\tBottom Row filled <= 3");
                        
                        return  "7";
                    }
                    else {
                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                            Session.logMessageLine(prefix + "\tBottom Row filled >3");
                        
                        return  "5";
                    }
                }
            }
        }
        else {
            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                Session.logMessageLine(prefix + "\tNot filled across top at Row 0 or 1");
            
            if ( indentLeftSide(2,8) ) {
                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                    Session.logMessageLine(prefix + "\tIndented Left Side");
                
                if ( width() == 4) {
                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                        Session.logMessageLine(prefix + "\tWidth == 4 (checking for 1)");
                     
                    if (filledInColumn(lc) >= 9 ) {
                        return "1";
                    }
                }
                else {
                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                        Session.logMessageLine(prefix + "\tWidth != 4");
                    
                    if ( filledInColumn(lc) >= 9) {
                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                            Session.logMessageLine(prefix + "\tFull Last column");
                        
                        if ( filledInColumn(0) <= 7) {
                            return  "d";
                        }
                    }
                    else {
                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                            Session.logMessageLine(prefix + "\tNo Full Last column");
                         
                        if ( (filledInRow(topRow) >= 4) && (filledInRow(4)>=width()-1) ) {
                            
                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                Session.logMessageLine(prefix + "\t5 without a full top row");
                            
                            return "5";
                        }
                        else {
                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                Session.logMessageLine(prefix + "\tNot a 5 without a full top row");
                            
                            if ( (filledInRow(topRow)>0) && (filledInRow(topRow)<=2) &&
                                 (filledInRow(BOXBOTTOM)>0) && (filledInRow(BOXBOTTOM)<=2) ) {
                                    
                                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                    Session.logMessageLine(prefix + "\tNarrow On Top and Bottom");
                                
                                if ( filledInRow(7) >= width()-1 )
                                    return  "4";
                                else {
                                    if ( filledRowCol(BOXBOTTOM,0) && !filledRowCol(BOXBOTTOM,lc) ) {
                                        return  "/";
                                    }
                                }
                            }
                            else {
                                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                    Session.logMessageLine(prefix + "\tNot Narrow On Top and Bottom");
                                
                                boolean threeProng = false;
                                
                                if ( !filledRowCol(4,0) && !filledRowCol(4,1) && !filledRowCol(4,1) &&
                                     !filledRowCol(5,0) && !filledRowCol(5,1) &&  filledRowCol(5,1) &&
                                     !filledRowCol(6,0) && !filledRowCol(6,1) && !filledRowCol(6,1) )
                                    
                                    threeProng = true;
                                
                                
                                if ( threeProng ) {
                                    if ( width() <= 6  ) {
                                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                            Session.logMessageLine(prefix + "\tProng of a 3");
                                    
                                        return "3";
                                    }
                                    else {
                                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                            Session.logMessageLine(prefix + "\tToo Wide for 3");
                                    }
                                }
                                else {
                                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                        Session.logMessageLine(prefix + "\tNo Prong of a 3");
                                     
                                    if ( filledInRow(BOXBOTTOM) == width() ) {
                                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                            Session.logMessageLine(prefix + "\tFilled in Box Bottom == width");
                                        
                                        return  "2";
                                    }
                                    else {
                                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                            Session.logMessageLine(prefix + "\tFilled in Box Bottom != width");
                                        
                                        if ( filledInRow(6) >= width()-1) {
                                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                                Session.logMessageLine(prefix + "\tFilled in Row 6");
                                            
                                            if ( openMiddle(topRow, BOXBOTTOM, 2, 4, 1, width()-2)) {
                                                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                                    Session.logMessageLine(prefix + "\tOpen Middle");
                                                
                                                return  "9";
                                            }
                                            else {
                                                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                                    Session.logMessageLine(prefix + "\tNo Open Middle");
                                            }
                                        }
                                        else {
                                             if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                                Session.logMessageLine(prefix + "\tNot Filled in Row 6");
                                             
                                             if ( indentRightSide(2,8)) {
                                                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                                    Session.logMessageLine(prefix + "\tIndented Right Side");
                                                
                                                if ( indentTop(topRow,1, width()-2) ) {
                                                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                                        Session.logMessageLine(prefix + "\tIndented Top");
                                                    
                                                    return  "V";
                                                }
                                                else {
                                                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                                        Session.logMessageLine(prefix + "\tNo Indented Top");
                                                    
                                                    return  "S";
                                                }
                                             }
                                             else {
                                                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                                    Session.logMessageLine(prefix + "\tNot Indented Right Side");
                                             }
                                        }
                                    }
                                }
                            }
                        }                         
                    }
                }
            }
            else {
                if ( indentRightSide(2,8) ) {
                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                        Session.logMessageLine(prefix + "\tIndented Right Side");
                    
                    if ( indentTop(topRow, 1, width()-2) ) {
                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                            Session.logMessageLine(prefix + "\tIndented Top");
                        
                        // K, H, L
                        switch ( filledInRow(5) ) {
                         
                            case 1:
                            case 2:
                                if ( filledInRow(BOXBOTTOM) == width())
                                    return  "L";
                                break;
                                
                            case 3:
                            case 4:
                                if ( upperRightBoxEmpty() ) {
                                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                        Session.logMessageLine(prefix + "\tUpper Right Empty");
                                     
                                    if ( filledInColumn(lc) >= 5 ) {
                                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                            Session.logMessageLine(prefix + "\tlast Column Filled");
                                        
                                        return  ("h");
                                    } else {
                                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                            Session.logMessageLine(prefix + "\tlast Column Not Filled");
                                        
                                        if ( filledInRow(BOXBOTTOM) >= width()-1) {
                                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                                Session.logMessageLine(prefix + "\tBottom Row Filled");
                                            
                                            return  "b";
                                        }
                                        else {
                                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                                Session.logMessageLine(prefix + "\tBottom Row Not Filled");
                                            
                                            return  "k";
                                        }
                                    }
                                } else if ( filledInRow(5) == 4 ) {
                                    return  "K";
                                }
                                
                            default:
                                if ( filledInRow(5) >= width()-1)
                                    return  "H";
                                
                        }
                    }
                    else {
                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                            Session.logMessageLine(prefix + "\tNot Indented Top");
                        
                        if ( filledInRow(4) >= width()-1) {
                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                Session.logMessageLine(prefix + "\tFilled Row 4");
                            
                            return  "6";
                            
                        }
                        else {
                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                Session.logMessageLine(prefix + "\tNot Filled Row 4");
                            
                            if ( filledInRow(5) >= width()-1) {
                                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                    Session.logMessageLine(prefix + "\tFilled Row 5");
                                 
                                return  "P";
                            }
                            else {
                                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                    Session.logMessageLine(prefix + "\tNot Filled Row 5");
                            }
                        }
                    }
                }
                else {
                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                        Session.logMessageLine(prefix + "\tIndented Neither Left or Right Side");
                    
                    if ( openMiddle(topRow, BOXBOTTOM, 3, 7, 1, width()-2) ) {
                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                            Session.logMessageLine(prefix + "\tOpen Middle");
                        
                        if ( filledRowCol(topRow,0)   &&  filledRowCol(BOXBOTTOM,0) &&
                            !filledRowCol(topRow, lc) && !filledRowCol(BOXBOTTOM, lc) )  {
                            
                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                Session.logMessageLine(prefix + "\tHas Left Corners, Missing Right Corners");
                             
                            return  "D";
                        } else
                            return  "0";
                    }
                    else {
                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                            Session.logMessageLine(prefix + "\tNo Open Middle");
                        
                        if ( (filledInColumn(0) >= 9) && (filledInColumn(lc) >= 9) ) {
                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                Session.logMessageLine(prefix + "\tFirst and Last Columns Filled");
                            // H,N
                            
                            if ( filledInRow(5) >= width()-1)
                                return "H";
                            else
                                return  "N";
                        }
                        else {
                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                Session.logMessageLine(prefix + "\tFirst and Last Columns Not Filled");
                            
                            if ( (width()>=7) && (filledInRow(5) >= width()-1) && 
                                 (filledInRow(BOXBOTTOM) >= width()-1) ) {
                                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                    Session.logMessageLine(prefix + "\tWidth()>=7 and Filled Row (5) >= width-1");
                                
                                return ("B");
                            }
                            else {
                                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                    Session.logMessageLine(prefix + "\tFilled Row (5) != width");
                                
                                if ( indentBottom(BOXBOTTOM, 1, width()-2)) {
                                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                        Session.logMessageLine(prefix + "\tIndented Bottom");
                                    
                                    if ( filledInRow(5) >= width()-1 ) {
                                        return  "R";
                                    }
                                }
                                else {
                                    if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                        Session.logMessageLine(prefix + "\tNo Indented Bottom");
                                    
                                    if ( filledInRow(5) >= 5 ) {
                                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                            Session.logMessageLine(prefix + "\tFilled Row 5 > =5");
                                        
                                        return  "8";
                                    } else {
                                        if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                            Session.logMessageLine(prefix + "\tFill in  Row 5 Shows Nothing");
                                        
                                        if ( width() == 4 ) {
                                            if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                                Session.logMessageLine(prefix + "\tWidth == 4");
                                            
                                            if ( filledRowCol(3,lc) && filledInColumn(lc) <= 4) {
                                                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                                    Session.logMessageLine(prefix + "\tt or f indicated");
                                                if ( !filledRowCol(0,lc) && !filledRowCol(1,lc) )
                                                    return  "t";
                                                else
                                                    return  "f";
                                            }
                                            else {
                                                if ( Session.logLevelSet(Session.logType.TABLE_NAME)  ) 
                                                    Session.logMessageLine(prefix + "\tNeither t nor f indicated");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                }
            }            
        }
        
        return "?";
    }
        
        
    
}
