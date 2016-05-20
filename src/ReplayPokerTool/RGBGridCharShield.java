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
 * 
 */

public class RGBGridCharShield extends RGBGrid {
    
    public  RGBGridCharShield (int nRows, int nCols) {
        super(nRows, nCols);
    }
    
   
    public  RGBGridCharShield(BufferedImage b, int startX, int startY, int nRows, int nCols) {
        super(b, startX, startY, nRows, nCols);    
    }
     
    
    public  RGBGridCharShield(RGBGridCharShield map, int sCol, int eCol) {
        super(map, sCol, eCol);
    }
    
    
    @Override
    public  void    cleanCharsNearShieldLeftEdge() {
        
        // do nothing
        if ( width() > 0 )
            return;
        
        
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
            setDebug(true);
        
        String  prefix  =   this.getClass().getSimpleName() + ":cleanCharsNearShieldLeftEdge";
        int c, r;
        
        // if we start to the right of the shield egde there's nothing to clean
        if ( startCol() > 2 ) 
            return;
        
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  ) {
            Session.logMessageLine(prefix + "\tStart Col: " + startCol());
            
            printGrid();
            
            for (int i=0; i<numCols(); i++)
                Session.logMessageLine(prefix + "\tCol[" + i + "] = " + filledInColumn(i));
        }
        
        if ( width() >= 3 ) {
            
            if ( filledRowCol(0,0) && filledRowCol(0,1) && filledRowCol(0,2) ) {
                
                if ( !filledRowCol(3,0) && !filledRowCol(4,0) && !filledRowCol(5,0) ) {
                    // delete the whole mess up there
                    for (c=0; c<width(); c++) {
                        for (r=0; r<2; r++) {
                            clearGridPoint(r,c);
                        }
                    }
                }
            }
        }
        int deleteLeading   =   0;
        
        for (c=0; c<width(); c++) {
            if ( filledInColumn(c) >= 5 )
                break;
            else if ( filledInColumn(c) == 0 )
                break;
            else
                deleteLeading++;
        }
        
        if ( deleteLeading > 0 ) {
            for (c=0; c<deleteLeading; c++)
                deleteColumn(0);
            
            
            int f=0;

            for (c=0;c<width();c++)
                f   +=  filledInColumn(c);
            if ( f == 0 )
                wipeOut(); // this item no longer exists
        }
        else {
            // is the shield part of the name?
            if ( startCol() == 0 ) {
                int lc  =   width()-1;
                
                if ( (width() > 6) && filledInColumn(lc) <= 8 ) {
                    //delete the first two rows
                    for (c=0; c<4; c++) {
                        clearGridPoint(0,c);
                        clearGridPoint(1,c);
                    }
                }
            }
        }
        
        // see if the top of the shield is part of this char
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
            Session.logMessageLine(prefix + "\tCleaning Top Row");
        
        for (c=0; (c<3) && (c<width()); c++) {
        
            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                Session.logMessageLine(prefix + "\tCol[" + c + "] = " + 
                     filledRowCol(0,c) + " "  + filledRowCol(1,c) );

            if ( filledRowCol(0,c) && !filledRowCol(1,c) )
                clearGridPoint(0,c);
        }
                   
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
            printGrid();
    }
    
    
    
    @Override
    public  void    cleanCharsNearShieldRightEdge() {
        
        // do nothing
        if ( width() > 0 )
            return;
        
        
        
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
            setDebug(true);
        
        String  prefix  =   this.getClass().getSimpleName() + ":cleanCharsNearShieldRightEdge";
        int c, r;
        int lc  =   width()-1;
        
        
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  ) {
            Session.logMessageLine(prefix + "\tStart Col: " + startCol() + "\tWidth: " + width());
        
            printGrid();
        }
        
        switch ( width() ) {
            
            case 0:
                return;
                
            case 1:
            case 2:
                if ( filledInRow(2) == 0 ) {
                    wipeOut();
                }
                break;
                
            default:
                // 1. Are we nestled under the shield edge without
                // touching it?
                
                // nothing to do...the shield egde doesn't get in the way
                if ( filledInColumn(width()-1) >= 8 ) {
                    // do we have chars in the upper right corner?
                    if ( filledRowCol(0, width()-1) ) {
                        // we are nestled...no data underneath the shield top edge
                        if ( filledInColumn(width()-2) <= 1) {
                            // delete the three columns at the end
                            deleteColumn(width()-1);
                            deleteColumn(width()-1);
                            deleteColumn(width()-1);
                        }
                    }
                }
        }
        
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
            Session.logMessageLine(prefix + "\tEnd Col = " + (startCol() + width()) );
        
        
        // is the right shield edge part of the char?
        if ( startCol() + width() >= 80 ) {
            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                Session.logMessageLine(prefix + "\tTouching Edge");
            
            int off         = 80 - (  startCol() + width()  ) + 1;
            int deleteCols  = 0;
            
            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                Session.logMessageLine(prefix + "\tOff : " + off);
            
            // do we need the extra columns?
            for (c=width()-off; c < width(); c++) {
                if ( filledInColumn(c) < 4) {
                    deleteCols++;       
                }
                else
                    break;
            }
            
            if ( deleteCols > 0 ) {
                for (c=0; c<deleteCols; c++)
                    deleteColumn( width()-1 );
            }
        }
        
        
        // check and see if the char is a fake uppercase or fake d, etc...
        if ( width() > 4 ) {
            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  ) {
                Session.logMessageLine(prefix + "\tF0 = " + filledRowCol(0, width()-1) );
                Session.logMessageLine(prefix + "\tF1 = " + filledRowCol(1, width()-1) );
            }

            if ( filledRowCol(0, width()-1) || filledRowCol(1, width()-1) ) {
                //Keep for Numbers, Capitals, d,k,
                if ( !filledRowCol(0,0) && !filledRowCol(0,1) ) {
                     // not filled in the front can only be a d or spurious
                     if ( filledInRow(2) < width() ) {
                         // not a d, delete
                         clearRow(0);
                         clearRow(1);
                     }
                }
            }
        }
   
        
        // are the first two rows part of the grid
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
            Session.logMessageLine(prefix + "\tCheck The Top Row");
        
        for (c=0; c<width(); c++) {
                 
            if ( (filledRowCol(0,c) && !filledRowCol(1,c)) || 
                 (filledRowCol(0,c) &&  filledRowCol(1,c) &&  !filledRowCol(2,c)) ){
                
                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                    Session.logMessageLine(prefix + "\tClear Top of Column: " + c);
                
                clearGridPoint(0,c);
                clearGridPoint(1,c);
            }
        }
        
       
                    
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
            printGrid();                
    }
    
    
    
    @Override
    public  String  charFromGrid() 
    throws  UnableToReadCharException
    {
        
        String  prefix  =   this.getClass().getSimpleName() + ":charFromGrid";

        String ch =   descendTreeFromRoot();

        setValue(ch);

        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
            Session.logMessageLine(prefix + "\t********************>" +  value() );

        return ch;
    }
    
    
    // make sure we aren't reading a freak merged char that
    //needs to be split
    String acceptChar(String c )
    throws UnableToReadCharException 
    {
        // is this a real char?
        String  prefix  =  this.getClass().getSimpleName() + ":acceptChar";
        
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
            Session.logMessageLine(prefix + "\tChar: " + c + " Width: " + width() );
        
        if ( c.equals("_"))
            return c;
        
        // it's always an m when chars inside the box are combined
        if ( c.equals("m") ) {
            int bad=0;
            for (int i=0; i<width(); i++) {
                if ( filledInColumn(i) > 8 ) {
                    bad++;
                    break;
                }
                
                if ( filledInColumn(i) < 6 ) {
                    if ( (filledInColumn(i) <1) && (filledInColumn(i) >3) ) {
                        bad++;
                        break;
                                
                    }
                }
            }
            
            if ((filledInRow(0) !=0) || (filledInRow(1) != 0) )
                    bad++;
            
            if ( bad == 0 ) {
            
                if ( (width() == 7 ) || (width()==8) ) {
                    if ((filledInRow(0)==0) && (filledInRow(1)==0) && 
                        (filledInColumn(0) ==8 ) && (filledInColumn(width()-1)==8)  ) {

                        return c;
                    }
                }
                else if ( width() < 6 ) {
                    throw   new UnableToReadCharException(prefix + "\tToo Narrow for m");
                }
                if ( width() > 6 ) {
                    if ( !indentBottom(9,0,4) || !indentBottom(9,5,width()-2)  
                    || (filledInColumn(0) < 8) || ( filledInColumn(width()-1) < 8))  
                         throw   new UnableToReadCharException(prefix + "\tWrong Form for m");
                }
                else  {
                    if ( !filledRowCol(9,1) && !filledRowCol(9, width()-1)
                    || (filledInColumn(0) < 8) || ( filledInColumn(width()-1) < 8))  
                         throw   new UnableToReadCharException(prefix + "\tWrong Form for m");
                }

                return c;
            }
            else {
                throw   new UnableToReadCharException(prefix + "\tWrong Form for m");
            }
        }
        
        
        switch ( width() ) {
            
            case 0:
                throw   new UnableToReadCharException(prefix + "\tWidth 0");
                
            case 1:
                    if ( !c.equals("i") && !c.equals("l") ) {
                        throw   new UnableToReadCharException(prefix + "\tWidth 0");
                    }
                break;
                
            case 2:
            case 3:
               
                if (  !c.equals("i") && !c.equals("l") && !c.equals("f") && !c.equals("t") 
                   && !c.equals("1") && !c.equals("r") && !c.equals("j") && !c.equals("u") 
                   && !c.equals("n")
                        ) {
                    
                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                        Session.logMessageLine(prefix + "\tWrong Width for: " + c);
                                        
                    throw   new UnableToReadCharException(prefix + "\tWrong width for " + c);                    
                }
                
                
                if ( !c.equals("i") && !c.equals("j") && !c.equals("r") && !c.equals("u") && !c.equals("n")) {
                    for (int r=0; r<10; r++) {
                        if ( filledInRow(r) == 0 ) {
                            throw   new UnableToReadCharException(prefix + "\tEmpty Row " + r);
                        }
                    }
                }

            case 4:
            case 5:
            case 6:
                    for (int r=2; r<10; r++) {
                        if ( filledInRow(r) == 0 ) {
                            throw   new UnableToReadCharException(prefix + "\tEmpty Row " + r);
                        }
                    }
                    
                    if ( c.equals("n") ) {
                        if ( (filledInColumn(0) <7) || ( filledInColumn(width()-1) < 7))  {
                            throw   new UnableToReadCharException(prefix);      
                        }
                    }
                break;
                
                
            case 7:
                if ( !c.equals("A") && !c.equals("X") && !c.equals("m") ) {
                    throw   new UnableToReadCharException(prefix + "\tWrong width for " + c);
                }
                break;
                
                
            case 8:
                 if ( c.equals("A") || c.equals("M"))
                     return c;
                
            case 9:
            case 10:
                if ( !c.equalsIgnoreCase("w")  ) {
                    throw   new CharTooWideException(prefix);
                }
                else if ( c.equals("M") ) {
                    if ( (filledInColumn(0) < 9) || ( filledInColumn(width()-1) < 9))  
                        throw   new CharTooWideException(prefix);                    
                }
                break;
                
                
            default:
                throw   new CharTooWideException(prefix + "\t too wide:" + width() );
                   
        }
        
        int n = 0;
        for (int i=0; i<width(); i++)
            n   +=  filledInColumn(i);
            
        if ( n == 0 )
            throw   new UnableToReadCharException("No Data in Char");
        
        return    c;  
        
    }
    

    public String descendTreeFromRoot() 
    throws UnableToReadCharException, CharTooWideException
    {
        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
            setDebug(true);
        
        int eCol    =   width() -1;
        
        String  prefix  =    this.getClass().getSimpleName() + ":descendTreeFromRoot";
        
        if ( width() == 0 )
            return "";
        
        int data=0;
        for (int i=0; i<width(); i++)
            data    +=  filledInColumn(i);
        if ( data == 0 )
            return "";
        
        if ( charsBelow(9) ) {
            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                Session.logMessageLine(prefix + "\tChars Below");
            
            if ( charsAbove(2) ) {
                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                    Session.logMessageLine(prefix + "\tChars Above");
                
                if ( upperRightBoxEmpty() || ((width()==2) || (width()==3)) ) {
                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                        Session.logMessageLine(prefix + "\tUpperRightHand Empty and width: " + width());
                    
                    return acceptChar("j");
                } else {
                    if (  (width() >= 5) && !indentBottom(9) )
                        return acceptChar("Q");
                    else
                        throw   new UnableToReadCharException(prefix);
                }
            } else 
                if ( filledInRow(9) == 0  )
                    return  acceptChar("_");
                else
                if ( indentTopStartingAtRow(2) ) {
                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                        Session.logMessageLine(prefix + "\tIndent Top from Row 2");
                    
                    return acceptChar ("y");
                } else  if ( openMiddle(2, 9, 4, 6) ) { 
                    
                    if ( lowerLeftBelowBoxEmpty() ) {
                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                            Session.logMessageLine(prefix + "\tLower Left Box Below Empty");
                        
                        return acceptChar( "q" );
                    } else 
                        if ( lowerRightBelowBoxEmpty() ) {
                            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                Session.logMessageLine(prefix + "\tLower right Box Below Empty");
                            return acceptChar( "p" );
                        } else
                            return acceptChar( "g" );
                }
        } else {
            if ( allPixelsInsideBox(2, 9) ) {
                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                    Session.logMessageLine(prefix + "\tAll Pixels Inside Box");
                
                if ( width() >= 8 ) {
                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                        Session.logMessageLine(prefix + "\tWidth >= 8");
                    
                    if ( indentTopStartingAtRow(2) ) {
                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                            Session.logMessageLine(prefix + "\tIndent Top Starting from Row 2");
                        return acceptChar("w");
                    } else
                        return acceptChar("m");
                } 
                else 
                    if ( indentTop(2, 1, width()-2)  ) {
                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                            Session.logMessageLine(prefix + "\tIndent Top Starting from Row 2");
                        
                        if ( filledInColumn(0) >= 7 ) {
                            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                Session.logMessageLine(prefix + "\tfilledInColumn 0 >= 7");
       
                          
                            return  acceptChar("u");
                        } else {
                            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                Session.logMessageLine(prefix + "\tLeft and Right Lower Corners Empty");
                            
                            if ( lowerLeftCornerEmpty() && lowerRightCornerEmpty() )
                                return  acceptChar("v");
                            else {
                                if ( filledInRow(5) == width() )
                                    return  acceptChar("e");
                                else
                                    return  acceptChar("x");
                            }
                        }
                    } else  {
                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                            Session.logMessageLine(prefix + "\tNo Indent Top Starting from Row 2");
                        
                        if ( openMiddle(2, 9, 4, 6) ) {
                            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                Session.logMessageLine(prefix + "\tOpen Middle From 4-6");
                            
                            if ( indentRightSide(2,8) ) {
                                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                    Session.logMessageLine(prefix + "\tIndent Right Side");
                                return  acceptChar("c");
                            }
                            else {
                                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                    Session.logMessageLine(prefix + "\tNo Indent Right Side");
                                
                                if ( indentBottom(9, 1, width()-2) ) {
                                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                        Session.logMessageLine(prefix + "\tIndent Bottom");
                                    
                                    return  acceptChar("n");
                                } else {
                                    return acceptChar("o");
                                }
                            }
                        } else {
                            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                Session.logMessageLine(prefix + "\tNo Open Middle From 4-6");
                            
                            if ( indentLeftSide(2,8) ) {
                                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                    Session.logMessageLine(prefix + "\tIndent Left Side");
                                 
                                if ( indentRightSide(2,8) ) {
                                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                        Session.logMessageLine(prefix + "\tIndent Right Side");
                                    // s or Z
                                    if ( (filledInRow(2)==width()) || (filledInRow(3)==width()) ) 
                                        return  acceptChar("z");
                                    else
                                        return  acceptChar("s");
                                } else {
                                    if ( filledRowCol(5, eCol) )
                                        return  acceptChar("a");
                                    else
                                        return  acceptChar("s");
                                }
                            } else {
                                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                    Session.logMessageLine(prefix + "\tNo Indent Left Side");
                                
                                if ( filledInColumn(0) >= 7) {
                                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                        Session.logMessageLine(prefix + "\tFilled In col 0 >= 7");
                                    
                                    if ( indentBottom(9) ) {
                                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                            Session.logMessageLine(prefix + "\tIndent Bottom");
                                        
                                        if ( filledInColumn(width()-1) <= 3)
                                            return  acceptChar("r");
                                        else {
                                            
                                            if ( width() >= 6 ) {
                                                if ( (filledInColumn(0) >= 7) && (filledInColumn(eCol) >= 7) )
                                                    return  acceptChar("m");
                                            } else
                                                return  acceptChar("n");
                                        }
                                    }
                                    else {
                                        // no indent, filled column 0 version of a and s
                                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  ) {
                                            Session.logMessageLine(prefix + "\tNo Indent Bottom");
                                            Session.logMessageLine(prefix + "\tCheck Column 1 row 6");
                                        }
                                        
                                        if ( indentRightSide(2,9) ) {
                                            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                Session.logMessageLine(prefix + "\tIndent Right Side");
                                            
                                            // s or e
                                            if ( filledInRow(5) == width() )
                                                return  acceptChar("e");
                                            else
                                                return  acceptChar("s");
                                            
                                        }
                                        else {
                                            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                Session.logMessageLine(prefix + "\tNo Indent Right Side");
                                            
                                            if ( !filledRowCol(6,0) ) {
                                                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                    Session.logMessageLine(prefix + "\tNot Filled g[6][0]");
                                                
                                                if ( !filledRowCol(5, width()-1))
                                                    return  acceptChar("s");
                                                else
                                                    return  acceptChar("a");
                                            }
                                            else {
                                                if ( !filledRowCol(5,0) ) 
                                                    return acceptChar("a");
                                                else {
                                                    if ( filledInRow(5) == width() )
                                                        return  acceptChar("e");
                                                    else
                                                        throw   new UnableToReadCharException();
                                                }
                                            }
                                        }
                                    }
                                }
                                else {
                                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                        Session.logMessageLine(prefix + "\tFilled In col 0 < 7");
                                    //e, c, s, a
                                    
                                    if ( indentBottom(9) ) {
                                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                            Session.logMessageLine(prefix + "\tIndent Bottom Where None Should Exist");
                                        throw   new UnableToReadCharException();
                                    }
                                                                        
                                    if ( filledInRow(5) == width() )
                                        return  acceptChar("e");
                                    else {
                                        
                                        if ( filledRowCol(2,0) && filledRowCol(2,eCol) &&
                                             filledRowCol(9,0) && filledRowCol(9,eCol) &&
                                            !filledRowCol(2,2) &&
                                            !filledRowCol(9,2) ) {
                                            
                                            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                Session.logMessageLine(prefix + "\tIndications Point to 'x'");
                                            
                                            return  acceptChar("x");
                                        }
                                        
                                        if ( !filledRowCol(6,0) ) {
                                            return  acceptChar("s");
                                        }
                                        else{
                                            if ( indentRightSide(2,9))
                                                return  acceptChar("c");
                                            else
                                                return  acceptChar("a");
                                        }
                                    }
                                }
                            }
                        }
                    }
            } // not all pixels inside box
            else {
                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                    Session.logMessageLine(prefix + "\tPixels Above Box");
                
                if ( width() >=1 && width() <= 2 ) {
                    // Above and width == 1 or width == 2 
                    
                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                        Session.logMessageLine(prefix + "\tPixels Above Box and Narrow: i, l, t, f");
                    
                    if ( (filledInColumn(0) ==9) && filledInRow(1) == 0 ) {
                        return  acceptChar("i");
                    }
                    else {
                        if ( (width()==1   && filledInColumn(0) == 10 ) ||
                              (width()==2) && (filledInColumn(0)>=9) && (filledInColumn(1)>=9) ) 
                            return  acceptChar("l");
                        else {
                            if ( charIsF() )
                                return  acceptChar("f");
                            else {
                                if ( charIsT() )
                                    return  acceptChar("t");
                            }
                        }
                    }
                }
                else if ( upperRightBoxEmpty() ) {
                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                        Session.logMessageLine(prefix + "\tUpper Right Above Box Empty");
                    
                    if ( filledInRow(9) == width() && (filledInColumn(0) >=9) ) {
                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                            Session.logMessageLine(prefix + "\tFilled Row 9 == width() abd filledInColumn(0) >=9");
                        
                        if ( (filledInColumn(eCol) < 4) && (filledInColumn(2) < 4) )
                            return  acceptChar("L");
                        else if ( (filledInColumn(eCol)>=6) && openMiddle(2,9,4,6) )
                            return  acceptChar("b");
                    } else
                        if ( indentBottom(9) )  {
                            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                Session.logMessageLine(prefix + "\tIndent Bottom");
                            
                            if ( upperLefttBoxEmpty() && (filledInRow(7) >=5)  ){
                                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                    Session.logMessageLine(prefix + "\tUpper Left Box Empty");
                                
                                return  acceptChar("A");
                            } else {
                                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                    Session.logMessageLine(prefix + "\tUpper Left Box Filled");
                                // k, h
                                if ( indentTop(2) )
                                    return acceptChar("k");
                                else
                                    return  acceptChar("h");
                            }
                        } else {
                            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                Session.logMessageLine(prefix + "\tNot Indent Bottom");
                            
                            if ( openMiddle(2,9,4,6) )
                                return  acceptChar("b");
                            
                            throw   new UnableToReadCharException(prefix);
                        }
                }
                else {
                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                     Session.logMessageLine(prefix + "\tUpper Right Above Box Filled");
                    
                    if ( openMiddle(0, 9, 3, 5) ) {
                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                            Session.logMessageLine(prefix + "\tOpen Middle Above");
                        
                        if ( indentRightSide(2,8) ) {
                            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                Session.logMessageLine(prefix + "\tIndent Right");
                            
                            if (indentLeftSide(2,8)  ) {
                                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                    Session.logMessageLine(prefix + "\tIndent Left");
                                // 2, 7, 3*
                                
                                if ( indentTop(0) && indentBottom(9) ) {
                                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                        Session.logMessageLine(prefix + "\tIndent top and indent bottom");
                                    if ( indentRightSide(2,9) || indentLeftSide(2,9) )
                                        return  acceptChar("X");
                                }
                                else {
                                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                        Session.logMessageLine(prefix + "\tNot Indent top or indent bottom");
                                    
                                    if ( filledInRow(9) <= 3)
                                        return  acceptChar("7");
                                    else {
                                        if ( (filledInRow(0) == width()) &&
                                             (filledInRow(9) == width()) ) 
                                            return  acceptChar("Z");
                                        else {
                                            if ( filledInRow(9) >= 5)
                                                return  acceptChar("2");
                                            else
                                                return  acceptChar("3");
                                        }
                                    }
                                }
                            }
                            else {
                                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                    Session.logMessageLine(prefix + "\tNo Indent Left");
                                
                                
                                if ( filledInRow(5) >= 4 ) {
                                    if ( (filledInRow(3) >= 4 ) && (filledInRow(4) >= 4 ) )
                                        return  acceptChar("K");
                                    else
                                        return acceptChar("G");
                                } else {
                                    if ( indentRightSide(2,7) ) {
                                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                            Session.logMessageLine(prefix + "\tndent Right");
                                        
                                        return acceptChar("C");
                                    }
                                    else {
                                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                            Session.logMessageLine(prefix + "\tNo Indent Right");
                                        
                                        return acceptChar("D");
                                    }
                                }
                            }
                        } else {
                            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                Session.logMessageLine(prefix + "\tNo indent Right");
                            
                            if ( filledInColumn(0) == 10 )  {
                                return acceptChar("D");
                            } 
                            else {
                                if ( indentLeftSide(2,7) )
                                    return acceptChar("3");
                                else
                                    return acceptChar("O");
                            }
                        }
                    } else {
                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                            Session.logMessageLine(prefix + "\tNo Open Middle Above");
                        
                        
                        if ( width() >= 8 ) 
                            if ( (filledInColumn(0) >= 8) || (filledInColumn(width()-1) >= 9) )
                                return acceptChar("M");
                            else {
                                // do we have the big indents at the top
                                if ( (indentColumnFromRow(1,0) >= 5) || ( indentColumnFromRow(2,0)>=5) )
                                    if ( (indentColumnFromRow(eCol-1,0) >= 5) || ( indentColumnFromRow(eCol-2,0)>=5) )
                                        return acceptChar("W");
                            }
                        else {
                            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                Session.logMessageLine(prefix + "\tWidth < 8");
                            
                            if ( charIsFT1() )  {
                                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                    Session.logMessageLine(prefix + "\tIs a T,F,1");
                                
                                if ( charIs1() ) {
                                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                        Session.logMessageLine(prefix + "\t1");
                                    
                                    return  acceptChar("1");
                                }
                                else { 
                                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                        Session.logMessageLine(prefix + "\tIs a t or f...");
                                    
                                    if ( charIsT() ) {
                                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                            Session.logMessageLine(prefix + "\tIs a t");
                                        
                                        return  acceptChar("t");
                                    } else if ( charIsF() ) {
                                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                            Session.logMessageLine(prefix + "\tIs an f");
                                        
                                        return  acceptChar("f");
                                    }
                                    else
                                        throw   new UnableToReadCharException(prefix + "\tMatches Neither T or F");
                                }
                            } else {
                                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                    Session.logMessageLine(prefix + "\tNot a T or F");
                                
                                if ( indentBottom(9,1, width()-2) )  {
                                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                        Session.logMessageLine(prefix + "\tIndent Bottom");
                                    
                                    if ( upperLefttBoxEmpty() ) {
                                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                            Session.logMessageLine(prefix + "\tUpper Left Box Empty");
                                        
                                        if ( lowerLeftBoxEmpty() )
                                            if ( (filledInColumn(0) <=2) && (filledInColumn(1)<=2) )
                                                return  acceptChar("1");
                                            else
                                                return  acceptChar("4");
                                        else if ( filledInRow(7) >=5 )
                                            return  acceptChar("A");
                                    } else {
                                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                            Session.logMessageLine(prefix + "\tUpper Left Box Filled");
                                        
                                        if (filledInRow(0) >= width()-1 ) {
                                            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                Session.logMessageLine(prefix + "\tFilled in Row(0) >= width()-1");
                                            
                                            if ( (filledInRow(4) >=4) || (filledInRow(5) >=4) ) {
                                                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                    Session.logMessageLine(prefix + "\tFilled in rows 4 or 5 >= 4");
                                                
                                                if ( lowerRightBoxEmpty() ) {
                                                    int n = 0;
                                                    if ( filledRowCol(1,eCol) ) n++;
                                                    if ( filledRowCol(2,eCol) ) n++;
                                                    if ( filledRowCol(3,eCol) ) n++;
                                                    if ( filledRowCol(4,eCol) ) n++;
                                                    
                                                    if ( n >= 3 )
                                                        return acceptChar("P");
                                                    else
                                                        return acceptChar("F");
                                                } else
                                                    return acceptChar("R");
                                            } else {
                                                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                    Session.logMessageLine(prefix + "\tNot Filled in rows 4 or 5 >= 4");
                                                
                                                if ( startsAndEndsLikeT() )
                                                    return  acceptChar("T");
                                                else {
                                                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                        Session.logMessageLine(prefix + "\tDoesn't Start and End Like a T");

                                                    if ( (filledInRow(0) == width()) ||
                                                         (filledInRow(1) == width()) )
                                                        return acceptChar("7");
                                                }
                                            }
                                        } else {
                                            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                Session.logMessageLine(prefix + "\tFilled in Row(0) < width()-1");
                                            
                                            if ( filledInColumn(0) >= 9 ) {
                                                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                    Session.logMessageLine(prefix + "\tFilled in Column(0) >= 9");
                                                
                                                if ( filledInColumn(width()-1) >= 9 ) 
                                                    if ( filledInRow(5) == width() )
                                                        return  acceptChar("H");
                                                    else
                                                        return  acceptChar("N");
                                                else // k, K
                                                    return acceptChar("k");
                                            } else  {// V, X, Y
                                                if ( filledInRow(4) >= width()-1 ) {
                                                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                        Session.logMessageLine(prefix + "\tRogue 6");
                                                    return acceptChar("6");
                                                }
                                                else if ( lowerLeftCornerEmpty() && lowerRightCornerEmpty() ) {
                                                    // V, Y, 6
                                                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                        Session.logMessageLine(prefix + "\tLowerLeft and lowerRight Corners Empty");
                                            
                                                    if ( filledInColumn(0) >= 3 )
                                                        return acceptChar("V");
                                                    else
                                                        return  acceptChar("Y");
                                                } else
                                                    if ( indentRightSide(2,9) || indentLeftSide(2,9) )
                                                        return acceptChar("X");
                                            }
                                        }
                                    }
                                }
                                // no ident bottom 
                                // dilBEGIJSTUZ12345689
                                else {
                                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                        Session.logMessageLine(prefix + "\tNo Indent Bottom");
                                    
                                    if ( indentLeftSide(1,8) ) {
                                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                            Session.logMessageLine(prefix + "\tIndent Left Side");
                                        
                                        // d, J, 4, 9, 3
                                        if ( lowerLeftBoxEmpty() )
                                            return  acceptChar("4");
                                        else {
                                            if ( upperLefttBoxEmpty() ) {
                                                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                    Session.logMessageLine(prefix + "\tUpper Left Box Empty");
                                                
                                                if ( (filledInColumn(0)>=4) || (filledInColumn(1)>=4) )
                                                    return  acceptChar("d");
                                                else
                                                    return acceptChar("J");
                                            }
                                            else { 
                                                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                    Session.logMessageLine(prefix + "\tUpper Left Box Not Empty");
                                                
                                                if ( indentRightSideAtRow(2) ) {
                                                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                        Session.logMessageLine(prefix + "\tindentRightSideAtRow(2)");
                                                
                                                    return  acceptChar("5");
                                                } else {
                                                    if ( indentRightSideAtRow(3) ) {
                                                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                            Session.logMessageLine(prefix + "\tNo indentRightSideAtRow(2)");
                                                        
                                                        if ( filledInRow(4) == width() )
                                                            return  acceptChar("6");
                                                        else
                                                            return  acceptChar("S");
                                                    } else {
                                                        if ( indentLeftSide(2,9) ) {
                                                            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                                Session.logMessageLine(prefix + "\tIndent Left Side");
                                                            
                                                            // 3 or 9
                                                            if ( !filledRowCol(3,0) && !filledRowCol(4,0) &&
                                                                 !filledRowCol(5,0) ) {
                                                                
                                                                return  acceptChar("3");
                                                            }
                                                            else {
                                                                if ( (filledInColumn(eCol) >= 7) && !filledRowCol(3,eCol))
                                                                    return  acceptChar("5");
                                                                else
                                                                    return  acceptChar("9");
                                                            }
                                                        }
                                                        else {
                                                            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                                Session.logMessageLine(prefix + "\tINo ndent Left Side");
                                                            
                                                            return  acceptChar("8");
                                                        }
                                                    }
                                                } // end if indent right side row 2
                                            } // end if upper left box empty
                                        } // end if lower left box empty
                                    } // end if indentLeftSide
                                    else {    
                                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                            Session.logMessageLine(prefix + "\tNo Indent Left Side");
                                         
                                        // make sure we have a deep hole here
                                        if ( indentTop(0, 1, width()-2) && indentTop(2, 1, width()-2) ) 
                                            if ( !filledRowCol(9,0) && !filledRowCol(9, eCol) &&
                                                 !filledRowCol(8,0) && !filledRowCol(8, eCol) )
                                                return  acceptChar("V");
                                            else
                                                return  acceptChar("U");
                                        else {
                                            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                Session.logMessageLine(prefix + "\tNo Indent Top");
                                            
                                            if ( indentLeftSide(1,8) ) {
                                                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                    Session.logMessageLine(prefix + "\tIndent Left Side");
                                               
                                                // S, Z, 1, 2, 3, 5, 9
                                                if ( indentRightSide(0,9) ) {
                                                    // S, Z, 2, 5
                                                    
                                                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                        Session.logMessageLine(prefix + "\tIndent right Side");
                                                }
                                                else {
                                                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                        Session.logMessageLine(prefix + "\tNo Indent right Side");
                                                    
                                                    // 1, 3, 9
                                                    if ( width() <= 3 )
                                                        return  acceptChar("1");
                                                    else
                                                        if ( filledInRow(5) >= 4 )
                                                            return  acceptChar("9");
                                                        else
                                                            return acceptChar("3");
                                                }
                                            }
                                            else {
                                                if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                    Session.logMessageLine(prefix + "\tNo Indent Left Side");
                                                
                                                // ilBEGI68
                                                if ( indentRightSide(2,9) ) {
                                                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                        Session.logMessageLine(prefix + "\tIndent Right Side");
                                                    
                                                    // E,G,6
                                                    if ( (filledInRow(0) == width()) && 
                                                         (filledInRow(9) == width()) ) {
                                                        
                                                        if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                            Session.logMessageLine(prefix + "\tFilled Rows 0 and 9");
                                                        
                                                        return  acceptChar("E");
                                                    } else {
                                                        // G, 6, 8*
                                                        if ( filledInColumn(0) == 10 ) {
                                                            if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                                Session.logMessageLine(prefix + "\tFilled Column 0");
                                                            
                                                            return  acceptChar("G");
                                                        } else {
                                                            if ( indentRightSideAtRow(3) )
                                                                return  acceptChar("6");
                                                            else
                                                                return  acceptChar("8");
                                                        }
                                                    }
                                                }
                                                else if ( !indentRightSide(0,9) ) {
                                                    if ( Session.logLevelSet(Session.logType.SHIELD_CHAR)  )
                                                        Session.logMessageLine(prefix + "\tNo Indent Right Side 0-9");
                                                    
                                                    // i, l, B, 8
                                                    if ( filledInRow(1) == 0 )
                                                        return  acceptChar("i");
                                                    else {
                                                        if ( width() <= 2 )
                                                            return  acceptChar("l");
                                                        else
                                                            // B, 8
                                                            if ( filledInColumn(0) == 10 )
                                                                return  acceptChar("B");
                                                            else
                                                                return  acceptChar ("8");   
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
            }
            
            
            throw   new UnableToReadCharException(prefix);
        }
        
        return "";
    } // end function


    
}
