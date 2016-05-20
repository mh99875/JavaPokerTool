/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

/**
 *
 * @author marcushaupt
 * @version     1.0
 * 
 */
public class TESTReplayPokerTool {
    
    public static void main(String[] args) {
        
        String  prefix = "ReplayPokerTool:Main";
                        
        try {           
            Session.setLogLevels(Session.logType.ERROR);
            ( new Thread(SessionFactory.getSession()) ).start();
        }
        catch ( Exception e ) {
            Session.logMessageLine(prefix + "\tError Creating Sessio or Thread");
            Session.logMessageLine( e.getMessage() );
            Session.logMessageLine("Exiting");
        }          
    }
    
}
