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
public class TESTPotSize {
    
    public static void main(String[] args) {
        
        String  prefix = "ReplayPokerTool:TESTPotSize";
                        
        try {           
            int     potNo   =   0;     
            Session.setLogLevels(Session.logType.INFO, Session.logType.ERROR, Session.logType.WARN );
            (new Thread(SessionFactory.getSession(SessionCallFunction.Func.POT, potNo)) )
                .start();
        }
        catch ( Exception e ) {
            Session.logMessageLine(prefix + "\tError Creating Sessio or Thread");
            Session.logMessageLine( e.getMessage() );
            Session.logMessageLine("Exiting");
        }
              
    }
    
}
