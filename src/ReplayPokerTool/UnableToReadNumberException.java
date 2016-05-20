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
public class UnableToReadNumberException extends Exception {

     private static final long serialVersionUID = 19977527263232809L;

    /**
     * Creates a new instance of <code>UnableToReadNumber</code> without detail
     * message.
     */
    public UnableToReadNumberException() {
        
    }

    /**
     * Constructs an instance of <code>UnableToReadNumber</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UnableToReadNumberException(String msg) {
        super(msg);
    }
}
