/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

/**
 *
 *
 * <p>The default "Main" class for the application</p>
 * 
 * <p>ReplayPokerTool captures data from poker games occurring 
 * in default-sized Replay Poker windows spawned from the browser</p>
 * 
 * <p>Currently there is no support for the Omaha family of games where each player 
 * receives 4 cards</p>
 * 
 * <p>There is a framework for handling multi-tabling but the details
 * have not been fleshed out yet so there is no multi-table support 
 * currently included.</p>
 * 
 * <p>A Session corresponds to a single Replay Poker
 * window, the first one located closest to the upper left hand corner
 * of the screen.</p>
 * 
 * <p>Running the tool with more than one Replay Poker
 * window active will not cause an error as the program will follow
 * only the one closest to the upper left hand corner.</p>
 * 
 * <p>Moving screens or windows on the desktop around in such a way that one that was closest
 * to the upper left hand corner is blocked, switched or obscured may
 * cause errors but the program will try to recover and continue reading
 * game state in any event until a timeout is reached (currently 5 minutes) at
 * which point the progrm exits. See PokerTableScreen and PokerTableScreenObsever
 * for more details.</p>
 * 
 * <p>It is best to have one screen open in the upper left hand corner and
 * leave it untouched for the duration of your game play. Note that the 
 * traditional way to play on-line poker is to have one or more windows set
 * in a specific arrangement on the screen (the layout) e.g. 2, 4, 6, 8 tables
 * tiled. When one game ends, a new one will be added in the spot vacated by the
 * ended one. Although the tool tries to recover from window moves you are not doing it any favors
 * and may lose game state.</p>
 * 
 * <p>Note there is no support for window resizing. Character and icon reading
 * is done through feature recognition and as of yet there was been no systematic
 * analysis of how the features change after a re-size event. Note that a 
 * significant amount of time and effort has been put into making the icons and 
 * characters on the screen difficult to read. For more details on this subject
 * See PlayerNameReader, CardAnalyzer, RGBGridShieldNumber, RGBGridTableNumber, 
 * GRBGridTableName, RGBGridCharShield, RGBGridHandNumber, RGBGridCharAnchor and
 * others which would need to be tested and modified to handle reading data from
 * a table resize event.</p>
 * 
 * <p>This program is designed to be run in a terminal, saving complete game state 
 * without interfering with the user's poker game. As such there are no modal 
 * dialog boxes popping up.</p>
 * 
 * <p>There is, as of this time, no operating-system independent facility to tell if 
 * the user has ended their Replay Poker session, at which time the Replay Poker
 * Tool would also exit. Replay runs in an Amazon AWS window so we cannot see it
 * process table. There is no way for the app to differentiate between the app
 * moving or being obscured (hidden beneath another window) and the app being closed.</p>
 * 
 * <p>All logging (but not Hand History writing) is done through the static methods of the Session class
 * Session, SessionFactory, and the Unit tests SessionFunctionCall,
 * SessionReadAllNames. See Game.saveHistory() for the saved game code</p>
 * 
 * <p>The application requests a Session, which is run on a thread.
 * Each full Session request (e.g. Main) creates</p>
 * <ol>
 * <li>A PokerTableScreen, representing the window containing
 * the poker game</li>
 * <li>A ReplayPokerTable which knows about the actual poker
 * table drawn on the screen.</li>
 * <li>A GameManager which reads the data from the table and stores it in a
 * newly created instance of Game, which saves the HandHistory data
 * GameManager runs in an endless loop</li>
 * </ol>
 * 
 * 
 * 
 * <p>Function: Main</p>
 *No args
 * 
 * @author Marcus Haupt
 * @version 1.0
 * 
 */


public class ReplayPokerTool {

    /**
     * 
     * argument list can can be null or -u(user) username where the app will use the arg
     * as the user name. If not supplied the user name is discovered from the game
     * state in that only the user's cards are exposed while the clock is visible
     * on the table.
     * 
     * @param args 
     * 
     *
     */
    
    public static void main(String[] args) {
        
        String  prefix = "ReplayPokerTool:Main";
        
        String  userName    =   null;
        
         try {   
             
            Session.setLogLevels(Session.logType.GAME);
            
            ( new Thread(SessionFactory.getSession()) ).start();
        }
        catch ( Exception e ) {
            Session.logMessageLine(prefix + "\tError Creating Session or Thread");
            Session.logMessageLine( e.toString() + " Exiting");
            Session.logMessageLine("Exiting");
        }          
    }
}
    
    
    
        

    
