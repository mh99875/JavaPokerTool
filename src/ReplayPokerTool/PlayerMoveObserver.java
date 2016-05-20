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
 */
public class PlayerMoveObserver implements Runnable {
    
    final   GameManager         gm;
    
    final   PlayerNamesReader   pnr;
    
    int                         userSeat;
    
    PlayerMoveObserver(GameManager g, int seat) {
        
        gm          =   g;
        
        userSeat    =   seat;
        
        pnr         =   new PlayerNamesReader(gm.currentGame().table(), true);
    }
    
    
    @Override
    @SuppressWarnings("")
    public void run () {
        String  prefix  =   this.getClass().getSimpleName() + ":run";

        int n=0;
        
        
        while ( true ) {
            try {
                Thread.sleep(500);

                BufferedImage       b   =   CardUtilities.getCurrentBoard();

                // who is in the seat we think should be the userName's?
                String sitting =   pnr.readShieldName(userSeat, b);
                 
                if ( Session.logLevelSet(Session.logType.PLAYER_MOVE)  ) {
                    Session.logMessageLine(prefix + "[" + n + "]\tUser Seat[" + userSeat + "] = " + sitting);
                    Session.logMessageLine(prefix + "[" + n + "]\tUser = " + Session.userName());
                }
                n++;

                if ( (sitting != null) && !Session.userName().equals(sitting) ) {
                    ReplayPokerTable    table   =    gm.currentGame().table();

                    // where did they go?
                    for (int i=0; i<table.numSeats(); i++) {
                        if ( i != userSeat ) {
                            // the once place in Arizona we know he ain't
                            if ( Session.userName().equals(pnr.readShieldName(i, b)) ) {
                                
                                if ( Session.logLevelSet(Session.logType.PLAYER_MOVE)  ) {
                                    Session.logMessageLine(prefix + "\tUser Moved From Seat: " + userSeat);
                                    Session.logMessageLine(prefix + "\tUser Moved To Seat: " + i);
                                }

                                 // Game Manager nneds a signal to refind the clock without
                                 // seeing the loss of clock in the seat it was watching as the sign
                                 // of a player action
                                 //
                                 // Game Manager needs o adjust it's array of who is where so that
                                 // when it sends down a message for a player (used to be for a seat)
                                 // is named the correct player
                                 //
                                 // in other words rotate it's player name array
                                 //
                                 // game uses a HashMap to map the player to the permanently ordered
                                 // array is uses for the entire game
                                 //
                                 userSeat   =   i;
                                 table.setUserSeat(userSeat);
                                 
                                 // tell the game manager to find a new button
                                 gm.seatMoveEventHandler(userSeat);
                                 break;
                            }
                        }
                    }
                }
            } 
            catch ( InterruptedException | CantReadNameException | SeatXYException e ) {
            }
        }
        
    }
    
    
}
