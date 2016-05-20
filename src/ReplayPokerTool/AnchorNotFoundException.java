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
public class AnchorNotFoundException extends Exception {

    private static final long serialVersionUID = 878995457527263232L;
    
    /**
     * Creates a new instance of <code>AnchorNotFoundException</code> without
     * message.
     */
    public AnchorNotFoundException() {
    }

    /**
     * Constructs an instance of <code>AnchorNotFoundException</code> with the
     * specified message.
     *
     * @param msg the message.
     */
    public AnchorNotFoundException(String msg) {
        super(msg);
    }
}
