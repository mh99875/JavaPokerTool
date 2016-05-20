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
 */
public class UnableToReadCharException extends Exception {

    private static final long serialVersionUID = 870837527263232809L;

    /**
     * Creates a new instance of <code>UnableToReadChar</code> without detail
     * message.
     */
    public UnableToReadCharException() {
    }

    /**
     * Constructs an instance of <code>UnableToReadChar</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UnableToReadCharException(String msg) {
        super(msg);
    }
}
