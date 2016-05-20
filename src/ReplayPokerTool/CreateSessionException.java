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
public class CreateSessionException extends Exception {

    private static final long serialVersionUID = 9827263232809L;
    
    /**
     * Creates a new instance of <code>CreateSessionException</code> without
     * message.
     */
    public CreateSessionException() {
    }

    /**
     * Constructs an instance of <code>CreateSessionException</code> with the
     * specified message.
     *
     * @param msg the message.
     */
    public CreateSessionException(String msg) {
        super(msg);
    }
}
