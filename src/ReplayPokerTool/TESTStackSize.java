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
public class TESTStackSize {
   public static void main(String[] args) {
        
        String  prefix = "ReplayPokerTool:Main";
                        
        try {           
            int     seat    =   4;          
            
            Session.setLogLevels(Session.logType.INFO, Session.logType.ERROR, Session.logType.WARN );
            (new Thread(SessionFactory.getSession(SessionCallFunction.Func.STACK, seat)))
                .start();
            
        }
        catch ( Exception e ) {
            Session.logMessageLine(prefix + "\tError Creating Sessio or Thread");
            Session.logMessageLine( e.getMessage() );
            Session.logMessageLine("Exiting");
        }
              
    }
}
