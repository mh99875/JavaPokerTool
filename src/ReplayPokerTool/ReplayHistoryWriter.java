/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author marcushaupt
 * @version     1.0
 * 
 * Save the hand history to a file with the data appended
 */
public class ReplayHistoryWriter {
    
     
    private     FileOutputStream    fop                 =   null;
    
    private     String              fileName            =   "";
    
    
    public ReplayHistoryWriter(String fName) {
        fileName    =   fName;
    }
    
    
    public  void    close() {
        try {
            if ( fop != null)
            fop.close();
                    
        }
        catch ( IOException e) {
            
        }
    }
        
    
    public  void    write(String msg, boolean debug) {
        String  prefix  =   this.getClass().getSimpleName() + ":write";
        
        try {
            File file       =   new File(fileName);
            fop             =   new FileOutputStream(file, true);
            
            if ( fop != null) {
                
                byte    []      bytes   =   msg.getBytes();
                fop.write(bytes);
                fop.flush();
                fop.close();
                
                if ( debug )
                    Session.logMessageLine(prefix + "\t: " + msg);
            }
            else {
                Session.logMessageLine(prefix + "\tError. fileWriter = null. Exiting");
                Session.exit(0);
            }
        }
        catch ( IOException e) {
            Session.logMessageLine(e.getMessage());
            Session.logMessageLine(prefix + "\tError. Exiting");
            Session.exit(0);
        }
    }
        
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        
        if ( fop != null)
            fop.close();
    };
    
   
    
}
