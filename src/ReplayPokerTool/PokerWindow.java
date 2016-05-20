/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.awt.Point;

/**
 *
 * Abstract base class for all Poker Windows
 * 
 * A poker table sits inside of a window
 * The window may contain the table name (window name)
 * 
 * It has a position (x,y) on the screen
 * 
 * @author marcushaupt
 * @version 1.0
 * 
 */

public abstract class PokerWindow {
    
    protected       int                 windowNum;
    
    protected       Point               windowAnchor;
    
    protected       String              windowName;
    
    protected       int                 bigBlindBet                   =   0;
    protected       int                 smallBlindBet                 =   0;
    
    
    
    public  abstract String             windowName();
     
    public  abstract Point              getWindowAnchorCopy() 
    throws AnchorNotFoundException;
    
    public  abstract int                windowAnchorX() 
    throws AnchorNotFoundException;
    
    public  abstract int                windowAnchorY() 
    throws AnchorNotFoundException;
    
    public  abstract ReplayPokerWindow  readTableName() 
    throws UnableToReadCharException;
}
