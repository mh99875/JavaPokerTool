/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

/**
 *
 * @author marcushaupt
 * 
 * in the currently not-coded multi-screen case this would be thrown
 * when there are no more window slots available for the application to use
 * to store another Replay Poker window
 */

public class NoMoreWindowsException extends Exception {

    private static final long serialVersionUID = 19973033263232809L;
    
    /**
     * Creates a new instance of <code>NoFreeScreensException</code> without
     * message.
     */
    public NoMoreWindowsException() {
    }

    /**
     * Constructs an instance of <code>NoFreeScreensException</code> with the
     * specified message.
     *
     * @param msg the detail message.
     */
    public NoMoreWindowsException(String msg) {
        super(msg);
    }
}
