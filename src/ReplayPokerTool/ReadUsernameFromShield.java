/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.image.BufferedImage;


/**
 *
 * @author marcushaupt
 */
public class ReadUsernameFromShield implements Runnable {
    
    private final   int             seatIndex;
    private final   GameManager     gm;
    private final   int             numSeats;
    private final   String          handNumber;
    
    
    public  ReadUsernameFromShield(int seat, GameManager gMgr) {
        gm              =   gMgr;
        seatIndex       =   gMgr.gameIndexForSeat(seat);
        numSeats        =   gMgr.currentGame().numSeats();
        handNumber      =   gMgr.currentGame().handNumber();
    }
    
    
    @Override
    public void run() {
        
        String              prefix          =   this.getClass().getSimpleName() + ":run";
        BufferedImage       b               =   CardUtilities.getCurrentBoard();
        ReplayPokerTable    table           =   gm.currentGame().table();
        Boolean             debug           =   Session.logLevelSet(Session.logType.DAEMONREADSHIELD);
        Robot               robot;
        
        try {
            robot       =  new Robot();
        }
        catch (  AWTException e ) {
            if ( debug ) 
                 Session.logMessageLine(prefix + "\tFailed to Create Robot. " + e.getMessage() );
            return;
        }
                
        while ( true ) {
            // the seat number is an index from the button
            
            if ( !handNumber.equals(gm.currentGame().handNumber()))  {
                if ( debug )
                    Session.logMessageLine(prefix + "\tHand " + handNumber + " ended. ");
                return;
            }
            
            for (int s=0; s<numSeats; s++) {
                if ( gm.gameIndexForSeat(s) == seatIndex ) {
                    // match on two seats having the immutable index (distance from the button)
                    // we keep checking every time through because the user
                    // can rotate the table at any time
                    
                    prefix          =   this.getClass().getSimpleName() + ":run seat [" + s +"]";
                    
                    // if the shield has a clock or an action, wait for it to come back
                    if ( table.hasClock(s,b) ) {
                        if (debug  )
                            Session.logMessageLine(prefix + "\tUser Has Clock."); 
                    
                        robot.delay(3500);
                        b           =   CardUtilities.getCurrentBoard();
                        
                        if ( debug  )
                            Session.logMessageLine(prefix + "\tClock Released."); 
                    }
                    else if ( table.readShieldAction(s, b) != Game.ActionType.NOT_AN_ACTION ) {
                        
                        if ( debug  )
                            Session.logMessageLine(prefix + "\tUser Has an Action."); 
                    
                        robot.delay(2000);
                        b           =   CardUtilities.getCurrentBoard();
                        
                        if ( debug  )
                            Session.logMessageLine(prefix + "\tShield Cleared Of Action"); 
                    }
                
                    
                    try {
                        if ( debug )
                            Session.logMessageLine(prefix + "\tAttmpting to read name");
                
                        String userName    =   table.readShieldName(s, b);
                        
                        if ( userName   != null ) {
                            Session.setUserName(userName);
                        
                            if ( debug )
                                Session.logMessageLine(prefix + "\tSet User Name to " + Session.userName());
                        }
                        
                        return;
                    }
                    catch ( ActionNotNameException e) {
                        // give the shield more time to release the action and return to a name
                    
                        robot.delay(1000);
                    }
                    catch ( CantReadNameException | SeatXYException  e ) {
                        if ( debug )
                            Session.logMessageLine(prefix + "\tError Reading Screen Name: " + e.toString()); 
                        // fatal errors when it comes to reading names
                        return;
                    }
                } // if it matches the one we want
            } // for each seat
        } // while true
    }
                
           
    
}
