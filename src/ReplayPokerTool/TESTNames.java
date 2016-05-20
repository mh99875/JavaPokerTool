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
public class TESTNames {
    
     public static void main(String[] args) {
        
        String  prefix = "ReplayPokerTool:TESTNames";
                        
        try {           
            Session.setLogLevels(Session.logType.INFO, Session.logType.ERROR, Session.logType.WARN );
            (new Thread(SessionFactory.getSession("NAMES")))
                .start();
            
        }
        catch ( Exception e ) {
            Session.logMessageLine(prefix + "\tError Creating Sessio or Thread");
            Session.logMessageLine( e.getMessage() );
            Session.logMessageLine("Exiting");
        }
              
    }
    
}
