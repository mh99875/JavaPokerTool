/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.*;
import java.util.*;
import java.lang.reflect.Field;
import java.awt.image.BufferedImage;



/**
 *
 * @author marcushaupt
 */
public class MouseController {
    
    CardUtilities    ca_;
    
    public CardUtilities ca() {
        return  ca_;
    }
    
    public  MouseController(CardUtilities    ca) {
        ca_ =   ca;
    }
    
    public void moveMouse(int x, int y ) throws Exception {
        Robot   robot   =   new Robot();
        
        robot.mouseMove(x,y);
    }
    
    public  void    leftClickAtPoint(Point p) throws Exception {
        Robot   robot   =   new Robot();
        
        robot.mouseMove(p.x,p.y);
        
        robot.mousePress(InputEvent.BUTTON1_MASK);
        // wait some milliseconds
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
       
    }
    
    // there are either boxes to click for pre-selected actions
    // fold, call, raise to (amt), call any, raise any
    // or buttons to press for actions during the clock-cycle
    // containing three standardized actions
    // Fold, Check, Bet
    // Fold, Call, Raise To (not by)
    // 4 bet sizer buttons
    // a slider bet sizer
   
    
    public  void    writeInTextBox(String text) throws Exception {
        Robot   robot   =   new Robot();

        robot.mouseMove(75,625);        
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(250);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(500);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        
        
        for (int i=0; i<text.length(); i++) {
            Class   c       =   KeyEvent.class;
            Field   f       =   c.getField("VK_" + text.charAt(i));
            int keycode     =   f.getInt(null);
            
            robot.delay(100);
            robot.keyPress(keycode);
            robot.keyRelease(keycode);
        }
    }
    
    
    
    
}

