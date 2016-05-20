/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

/**
 *
 * Exception thrown when (X,Y) coordinates do not correspond to a seat at a 
 * table {@literal (e.g. negative, zero or off the edge of the display)}
 * 
 * @author marcushaupt
 * @version     1.0
 * 
 * Exception thrown when a given X,Y do not correspond to a seat at a table
 */
public class SeatXYException extends Exception {

    private static final long serialVersionUID = 9844559505757263L;
    
    /**
     * Creates a new instance of <code>SeatXYException</code> without a
     * message.
     */
    public SeatXYException() {
    }

    /**
     * Constructs an instance of <code>SeatXYException</code> with the specified
     * message.
     *
     * @param msg the message.
     */
    public SeatXYException(String msg) {
        super(msg);
    }
}
