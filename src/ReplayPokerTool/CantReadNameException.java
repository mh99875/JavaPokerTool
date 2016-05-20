/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

/**
 *
 * @author marcushaupt
 * @version 1.0
 */
public class CantReadNameException extends Exception {

    private static final long serialVersionUID = 9833899505757263L;
    
    /**
     * Creates a new instance of <code>CantReadNameException</code> without
     * message.
     */
    public CantReadNameException() {
    }

    /**
     * Constructs an instance of <code>CantReadNameException</code> with the
     * specified message.
     *
     * @param msg the message.
     */
    public CantReadNameException(String msg) {
        super(msg);
    }
}
