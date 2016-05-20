/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

/**
 *
 * @author marcushaupt
 * 
 * A full game session which will continuously read poker games from a poker 
 * table
 * 
 * The Session creates silently unless logType <b>STARTUP</b> is set
 
 Run: called when Thread.start is executed
 
 Creates a new GameManager to read games from the supplied ReplayPokerWindow
 
 Places the GameManager on a separate thread
 
 GameManager has no checked exceptions so if it fails for whatever reason
 the application terminates 
 */


public class SessionFullGame extends Session {
        
    ReplayPokerWindow    pts_;
    CardUtilities        ca_;
    
    public  SessionFullGame(ReplayPokerWindow pts, CardUtilities ca) {
        ca_     =   ca;
        pts_    =   pts;
    }
    
    @Override
    public void run () {
        
        String  prefix          =   "SessionFullGame:run";
                
        Session.setLogLevels(Session.logType.STARTUP); {
            Session.logMessageLine(this.getClass().getSimpleName() + "\tCreated");
            Session.logMessageLine(prefix + "\tStarting Game Manager");
        }
        
        Thread  gmThread        =   new Thread( new GameManager(pts_, ca_)); 
        gmThread.start();
    }
    
}
