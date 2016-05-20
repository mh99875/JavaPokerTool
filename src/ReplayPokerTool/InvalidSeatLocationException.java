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
 * 
 * Error thrown if the seat is too close to the edge of the display
 */
public class InvalidSeatLocationException extends Exception {

        private static final long serialVersionUID = 89833985229L;

    /**
     * Creates a new instance of <code>InvalidSeatLocationException</code>
     * without detail message.
     */
    public InvalidSeatLocationException() {
    }

    /**
     * Constructs an instance of <code>InvalidSeatLocationException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidSeatLocationException(String msg) {
        super(msg);
    }
}
