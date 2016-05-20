/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

/**
 *
 * @author marcushaupt
 */
public class NoSeatsFoundException extends Exception {

    private static final long serialVersionUID = 22047899505757263L;
    
    /**
     * Creates a new instance of <code>NoSeatsFoundException</code> without
     * detail message.
     */
    public NoSeatsFoundException() {
    }

    /**
     * Constructs an instance of <code>NoSeatsFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSeatsFoundException(String msg) {
        super(msg);
    }
}
