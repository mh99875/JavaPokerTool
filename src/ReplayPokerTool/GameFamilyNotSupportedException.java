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
 * Error Thrown is the table is Omaha Blue
 */
public class GameFamilyNotSupportedException extends Exception {

    private static final long serialVersionUID = 1270837263232809L;
    
    /**
     * Creates a new instance of <code>GameFamilyNotSupportedException</code>
     * without message.
     */
    public GameFamilyNotSupportedException() {
    }

    /**
     * Constructs an instance of <code>GameFamilyNotSupportedException</code>
     * with the specified message.
     *
     * @param msg the detail message.
     */
    public GameFamilyNotSupportedException(String msg) {
        super(msg);
    }
}
