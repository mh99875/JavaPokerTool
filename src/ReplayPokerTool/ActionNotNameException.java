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
public class ActionNotNameException extends CantReadNameException {

        private static final long serialVersionUID = 30894386940998L;

        
    /**
     * Creates a new instance of <code>ActionNotNameException</code> without
     * detail message.
     */
    public ActionNotNameException() {
    }

    /**
     * Constructs an instance of <code>ActionNotNameException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ActionNotNameException(String msg) {
        super(msg);
    }
}
