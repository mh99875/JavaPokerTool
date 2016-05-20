/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.awt.Robot;
import java.awt.image.BufferedImage;

/**
 *
 * @author marcushaupt
 */
public class StackSizeReader implements Runnable {
    
    public enum Type { SOH, EOH, CON }
    
    Game               table_;
    final int           MAX_TRIES   =   20;   
    Type                type        =  Type.SOH;
    boolean             debug       =   false;
    
    Game table() {
        return  table_;
    }
    
   
    public  StackSizeReader(Game table, Type t) {
        
        table_              =   table;
        type                =   t;
        
    }
    
    public  int readStacksOnce() {
        
        return 0;
    }
   
    
    @Override
    public void run() {
            
        
    }
    
}
