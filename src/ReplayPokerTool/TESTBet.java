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
 */


public class TESTBet {
    
    /**
     *
     * @param args unused
     */
    public static void main(String[] args) {
        
        String  prefix = "ReplayPokerTool:TESTBet";
                        
        try {           
            int     seat    =  2;            
            Session.setLogLevels(Session.logType.GAME, Session.logType.TABLE_NUMBER);
            (new Thread(SessionFactory.getSession(SessionCallFunction.Func.BET, seat)) ).start();
            
        }
        catch ( Exception e ) {
            Session.logMessageLine(prefix + "\tError Creating Sessio or Thread");
            Session.logMessageLine( e.getMessage() );
            Session.logMessageLine("Exiting");
        }
    }
}
