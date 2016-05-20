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
 * Exception thrown if the Poker Table window is some part of the Replay Poker
 * window provided by Replay is off the edge of the screen
 */


public class InvalidwindowPositionException extends Exception {

    private static final long serialVersionUID = 4478995500575272632L;
    
    /**
     * Creates a new instance of <code>InvalidScreenPositionException</code>
     * without detail message.
     */
    public InvalidwindowPositionException() {
    }

    /**
     * Constructs an instance of <code>InvalidScreenPositionException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidwindowPositionException(String msg) {
        super(msg);
    }
}
