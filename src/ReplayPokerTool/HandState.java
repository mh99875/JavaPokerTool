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
public class HandState {
    
    Hand        bestMade;
    Hand        bestDraw;
    Hand        bestRunner;
    
    CardList    draws;
    CardList    runners;
    
    
    
    public HandState() {
        bestMade            = null;
        bestDraw            = null;
        bestRunner          = null;
        
        draws               = new CardList();
        runners             = new CardList();
    }
    
}
