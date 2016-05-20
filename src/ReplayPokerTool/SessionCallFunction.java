/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.awt.image.BufferedImage;
import java.util.Calendar;

/**
 *
 * @author marcushaupt
 * @version     1.0
 * 
 */

public class SessionCallFunction extends Session {
    
    Func                function_;
    int                 seatNumber_;
    ReplayPokerTable    table_;
    
    
    public  enum    Func    { NAME, STACK, BET, POT, BUTTON, GAMENO, TABLENAME }
    
    
    public  SessionCallFunction(ReplayPokerTable t, Func function, int seatNumber) {
        function_   =   function;
        seatNumber_ =   seatNumber;
        table_      =   t;        
    }
    
    @Override
    public void run () {
        
        String  prefix  =   "SessionCallFunction(" + function_.toString() + ")[" + seatNumber_ + "]";
        
        BufferedImage       b       =   CardUtilities.getCurrentBoard();
        
        Session.setLogLevels(Session.logType.INFO, Session.logType.ERROR, Session.logType.WARN );
        
        Session.logMessageLine(prefix + "\tExecuting Function");
        
        switch ( function_ ) {
            
            // Read Screen Name
            case NAME:
                Session.logMessageLine(prefix + "\t" + function_.toString() + "[" + seatNumber_ + "]");
                
                try {
                    PlayerNamesReader   pnr = new PlayerNamesReader(table_, true);
                    
                    long stMS, enMS;
                    
                    stMS = Calendar.getInstance().get(Calendar.MILLISECOND);
                    pnr.readShieldName(seatNumber_, b);
                    enMS = Calendar.getInstance().get(Calendar.MILLISECOND);;
                    
                    Session.logMessageLine(prefix + "\t" + pnr.readShieldName(seatNumber_, b) + "\t" +
                            (enMS-stMS) + " Milliseconds");
                    
                    
                }
                catch ( CantReadNameException | SeatXYException e) {
                    
                    if ( e instanceof SeatXYException ) {
                        Session.logMessageLine(prefix + "\tSeat Location Error. Aborting Session");
                        Session.logMessageLine( e.getMessage() );
                        Session.exit(0);
                    }
                }
                break;
                
                
            case TABLENAME:
                // tablename read automatically as part of game creation
                try {
                    Session.logMessageLine(prefix + "\tTable Name: " + table_.pw().readTableName().windowName() );
                }
                catch ( UnableToReadCharException e) {
                    
                }
                Session.exit(0); 
                break;
                
                
            case GAMENO:
                Session.logMessageLine(prefix + "\t" + function_.toString() );
                try {
                    Session.logMessageLine(prefix + "\tGame Number: " +
                            table_.readHandNumber(b) );
                }
                catch (UnableToReadCharException e) {
                    Session.logMessageLine(prefix + "\tGame Number Not Read");
                }
                catch ( AnchorNotFoundException e ) {
                    
                }
                break;
                
            // Read Chip Stack
            case STACK:
                Session.logMessageLine(prefix + "\t" + function_.toString() + "[" + seatNumber_ + "]");
                try {
                    table_.readShieldChipStack(seatNumber_, b);
                }
                catch ( UnableToReadNumberException e) {
                    Session.logMessageLine(prefix + "\tUnable to Read Chip Stack Seat[" + seatNumber_ + "]");
                }
                break;
                
                
            case BUTTON:
                Session.logMessageLine(prefix + "\t" + table_.hasButton(seatNumber_, b));
                break;
                
            // Read Bet
            case BET:
                try {
                    //table_.readPlayerBet(seatNumber_, b);
                    Session.logMessageLine(prefix + "\tBet: " + table_.readPlayerBet(seatNumber_, b) );
                }
                catch ( UnableToReadNumberException e) {
                    Session.logMessageLine(prefix + "\tError");
                    Session.logMessageLine(e.getMessage());
                }
                break;
                
            // read Pots
            case POT:
                
                try {
                    switch ( seatNumber_ ) {
                        case 0:
                            Session.logMessageLine(prefix + "\t" + function_.toString() + " CALLED");
                            table_.potValue(b, Game.Pot.CALLED);
                        break;
                        
                        case 1:
                            Session.logMessageLine(prefix + "\t" + function_.toString() + " UNCALLED");
                            table_.potValue(b, Game.Pot.UNCALLED);
                            break;
                            
                        case 2:
                            Session.logMessageLine(prefix + "\t" + function_.toString() + " RAKE");
                            table_.potValue(b, Game.Pot.RAKE);
                            break;
                    }
                    
                } catch ( UnableToReadNumberException | AnchorNotFoundException e) {
                    
                }
                break;
        }
        
    } // End Function Run
    
    
}
