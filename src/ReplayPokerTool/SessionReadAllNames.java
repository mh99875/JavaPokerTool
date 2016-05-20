/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

/**
 *
 * @author marcushaupt
 */
public class SessionReadAllNames extends Session {
    
    ReplayPokerTable    pt_;
    
    SessionReadAllNames(ReplayPokerTable pt) {
        pt_     =   pt;
    }
    
    @Override
    public void run () {
        
        String  prefix  =   "SessionReadAllNames:run";
        
        Game game    = new Game(pt_);
       
        for (int s=0; s<game.numSeats(); s++) {
            if ( game.playerKnown(s) ) {
                String msg = String.format("%s\tSEAT[%d]%15s : %d",
                        prefix, s, game.playerAtSeat(s).getScreenName(),
                    game.getChipStack(s) );
                Session.logMessageLine(msg);
            } // if we have a record of the player
        }
    }
    
}
