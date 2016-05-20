/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author marcushaupt
 */
public class ExitScreenErrorDialogBox implements ActionListener {
   JFrame   myFrame         =   null;
   
    public static void main(String[] a) {
      (new ExitScreenErrorDialogBox()).showOption("test");
    }
    
   
    public void showOption(String message) {
        String  prefix  =   this.getClass().getSimpleName() + ":showOption";
       
        int messageType = JOptionPane.QUESTION_MESSAGE;
       
        String[] options = {"Exit", "Continue"};
       
        System.out.println(prefix + "\tCall JOptionPane");
       
        JFrame j=new JFrame();
        j.setAlwaysOnTop(true);
        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        j.setVisible(true);
        j.setVisible(false);
       
       int code = JOptionPane.showOptionDialog(j,  message, 
         "Option Dialog Box", 0, messageType, null, options, "Exit");
      
         if ( code == 0 )
            System.exit(0);
                 
   }
   
   
   public void test() {
        myFrame = new JFrame("showOptionDialog()");
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
   }
   
   @Override
   public void actionPerformed(ActionEvent e) {
      int messageType = JOptionPane.QUESTION_MESSAGE;
      
      String[] options = {"Exit", "Continue"};
      
      int code = JOptionPane.showOptionDialog(myFrame, 
         "Unable To Read/Find Poker Table?", 
         "Option Dialog Box", 0, messageType, 
         null, options, "Exit");
      
         if ( code == 0 )
             System.exit(0);
         else
            System.out.println("Answer: " + code);
   }
}
    
