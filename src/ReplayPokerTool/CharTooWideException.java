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
public class CharTooWideException extends UnableToReadCharException {

    private static final long serialVersionUID = 2708377263232809L;
    
    /**
     * Creates a new instance of <code>ChaeTooWideException</code> without
     * detail message.
     */
    public CharTooWideException() {
    }

    /**
     * Constructs an instance of <code>ChaeTooWideException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CharTooWideException(String msg) {
        super(msg);
    }
}
