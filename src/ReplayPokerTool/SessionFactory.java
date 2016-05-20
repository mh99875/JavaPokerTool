/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

/**
 *
 
 Create a new Session to be run in a thread = the core of the application
 
 Creation is via one of the following static methods
 Returning: Session
 Throwing: CreateSessionException
 
 public  static Session getSession() 
 throws CreateSessionException
 By default a full game session is created
 
 
 getSession(String)
 pass the name of a Session to create
 Currently only "Full" (default) and "NAMES" (read all names at the table)
 are supported.

 Session getSession(String type, int seat)
 Same as above with a seat number. This function is in the interface for 
 future use.
 
  
 getSession(SessionCallFunction.Func func) 
 Funtion.Func an enum in SessionCallFunction
 public  enum    Func    { NAME, STACK, BET, POT, BUTTON, GAMENO, TABLENAME }
 
 
 getSession(SessionCallFunction.Func func, int seat) 
 same as above except with a seat parameter indicating one of the seats at
 the poker table
 
 Session getSession(String type, SessionCallFunction.Func func, int seatOrPot)
 The builder function building the session does the following:
 
 1. Tries to create a ReplayPokerWindow, which finds the table based on
 the Anchor and other characteristics of the user's screen indicating the 
 presence of a Replay Poker Window (see ReplayPokerWindow for details)
 * The screen represents the window in which the Replay Game is played
 
 * 2. Creates a CardUtilities object, functions for working with a BufferedImage
 
 * 3. Create a ReplayPokerTable, represents the table within the 
 * ReplayPokerWindow e.g. seats, green felt and board cards, bets, etc...
 
 * 4. Calls the constructor for the specific type of Session requested, e.g.
 * a full game or a unit test
 
 * If no error is thrown the Session object returned is suitable for running on
 * a thread and no further work is required for the calling function except for
 * selecting a thread and executing Thread.start.
 * 
 * @author Marcus Haupt
 * @version 1.0
 * 
 */
public class SessionFactory {
        
    public  static Session getSession() 
    throws CreateSessionException
    {
        return  getSession("FULL", null, 0);
    }
    
    public  static Session getSession(String type) 
    throws CreateSessionException
    {
        return  getSession(type, null, 0);
    }
    
    public  static Session getSession(SessionCallFunction.Func func) 
    throws CreateSessionException
    {
        return  getSession("FUNCTION", func, 0);
    }
    
    public  static Session getSession(SessionCallFunction.Func func, int seat) 
    throws CreateSessionException
    {
        return  getSession("FUNCTION", func, seat);
    }
    
    public  static Session getSession(String type, int seat)
    throws CreateSessionException
    {
        return  getSession(type, null, seat);
    }
    
    private  static Session getSession(String type, SessionCallFunction.Func func, int seatOrPot)
    throws  CreateSessionException
    {
        
        String  prefix  =   "SessionFactoy:getSession()";
        
        try {
            ReplayPokerWindow    pts     =   ReplayPokerWindow.newWindow();
            
            if ( pts == null ) {
                Session.logMessageLine(prefix + "\tFatal Error. PokerTableScreen Not Created");
                Session.exit(0);
            }
            else if ( pts.getWindowAnchorCopy() == null ) {
                 Session.logMessageLine(prefix + "\tFatal Error. PokerTableScreen.screenAnchor is Null");
                Session.exit(0);
            }
            
            CardUtilities       ca      =   new CardUtilities(pts);
            ReplayPokerTable    pt      =   new ReplayPokerTable(pts);

            if ( type.equalsIgnoreCase("FULL") ) {
                
                return   new SessionFullGame(pts, ca);
                
            } else if ( type.equalsIgnoreCase("NAMES") ) {
                
                return   new SessionReadAllNames(pt);
                
            } else if ( type.equalsIgnoreCase("FUNCTION") ) {
                
                if ( func != null )
                    return new SessionCallFunction(pt, func, seatOrPot);
                
            }
        }
        catch ( AnchorNotFoundException | NoMoreWindowsException | 
                NoSeatsFoundException | InvalidwindowPositionException ae ) {
            
            Session.logMessageLine(ae.getMessage() + " Exiting");
            Session.exit(0);
        }
        
        throw   new CreateSessionException(prefix + "\ttype=" + type + 
                        " seat = " + seatOrPot + " Func = " + (func == null ? "(null)" : func.toString()) );
    }
    
}
