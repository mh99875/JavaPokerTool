/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

/**
 *
 * @author marcushaupt
 * @version 1.0
 */
public class TESTReadName {
     public static void main(String[] args) {
        
        String  prefix = "ReplayPokerTool:Main";
                        
        try {           
            int     seat    =  8;          
            //Session.setLogLevels(Session.logType.MERGE);
            
            Session.setLogLevels(Session.logType.REPAIR, Session.logType.SHIELD_CHAR);
            
            (new Thread(SessionFactory.getSession(SessionCallFunction.Func.NAME, seat)))
                    .start();
        }
        catch ( Exception e ) {
            Session.logMessageLine(prefix + "\tError Creating Sessio or Thread");
            Session.logMessageLine( e.getMessage() );
            Session.logMessageLine("Exiting");
        }
              
    }
}
