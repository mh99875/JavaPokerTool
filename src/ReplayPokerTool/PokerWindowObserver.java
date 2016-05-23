/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Calendar;


/**
 *
 * @author marcushaupt
 * @version     1.0 
 * 
 * Daemon watching for changes to the WindowAnchor
 * 
 * 1. <b>Run</b>
 * Every 500 milliseconds wake up and see if the Window Anchor still points to a 
 * valid anchor
 * 
 * if a new anchor is found, create a new table and pass it to the GameManager
 * who will use it and in turn pass it to the Game which will continue to read
 * subject to data/state lost as a result of the window move processing
 *  
 * If a new window can't be found the 
 */
public class PokerWindowObserver implements Runnable {
    
    final   GameManager    gm;
    
    long    failureMS   =   0;
    
    PokerWindowObserver (GameManager gm) {
        this.gm            =   gm;
    }
    
    private boolean    reFindAnchor(ReplayPokerWindow rpw) {
        String  prefix  =   this.getClass().getSimpleName() + ":reFindAnchor";
        
        if ( Session.logLevelSet(Session.logType.INVALIDANCHOR)  )
            Session.logMessageLine(prefix + "\tRefinding Window Anchor");
        
        try {
            rpw.findWindowAnchor();
            
            if ( Session.logLevelSet(Session.logType.INVALIDANCHOR)  ) {
                Session.logMessageLine(prefix + "\tFound Window Anchor");
            }
            
            return true;
        }
        catch ( InvalidwindowPositionException | AnchorNotFoundException e ) {
        }
        catch (GameFamilyNotSupportedException e ) {
            Session.logMessageLine(prefix + "\tGame Type Not Supported. Exiting");
            Session.exit(0);
        }
        
        return false;
    }
    
    
    @Override
    @SuppressWarnings("")
    public void run () {
        String  prefix  =   this.getClass().getSimpleName() + ":run";
        
        while ( true ) {
            try {
                Thread.sleep(200);
                               
                ReplayPokerWindow   rpw =   gm.pts();
                
                BufferedImage       b   =   CardUtilities.getCurrentBoard();
                
                if ( Session.logLevelSet(Session.logType.INVALIDANCHOR)  )
                    Session.logMessageLine(prefix + "\tChecking Anchor");
                
                if ( !rpw.candidateForAnchor(b,rpw.windowAnchorX(), rpw.windowAnchorY() ) ) {
                    rpw.setAnchorToInvalid();
                    
                    if ( Session.logLevelSet(Session.logType.INVALIDANCHOR)  )
                        Session.logMessageLine(prefix + "\tWindow Anchor is Invalid");
                                        
                    if ( reFindAnchor(rpw) ) {
                        if ( Session.logLevelSet(Session.logType.INVALIDANCHOR)  )
                            Session.logMessageLine(prefix + "\tTelling Table to Update Anchors");
                        
                        gm.currentGame().table().anchorReset( );
                        
                        failureMS   =   0;
                    }
                    else if ( failureMS == 0 ) {
                        // start timer to refind
                        Calendar now = Calendar.getInstance();
                        failureMS = now.get(Calendar.MILLISECOND);
                    }
                    else {
                        // see if we have surpassed our 5 minute wait time and are possibly
                        // using system resources unceccessarily
                        // we could use a pop-up or just exit. 
                        // for now just exit
                        if (  Calendar.getInstance().get(Calendar.MILLISECOND) > 
                              failureMS + (5 * 60 * 1000 ) ) {
                            if ( Session.logLevelSet(Session.logType.ERROR)  )
                                Session.logMessageLine(prefix + "\tExiting After Surpassing 5 Minutes " +
                                      "Without Seeing the Anchor");
                        
                            Session.exit(0);
                        }
                    }
                }
                else {
                    if ( Session.logLevelSet(Session.logType.INVALIDANCHOR)  )
                        Session.logMessageLine(prefix + "\tAnchor OK");
                }
            }
            catch ( InterruptedException e) {
                Session.logMessageLine(e.getMessage());
                break;
            }
            catch ( NullPointerException | AnchorNotFoundException e) {
                break;
            }
            //catch ( NoSeatsFoundException  e ) {
            //    if ( Session.logLevelSet(Session.logType.ERROR)  )
            //        Session.logMessageLine(prefix + "\tError. Unable to Find Seats.");                
            //}
        }
    }
}
