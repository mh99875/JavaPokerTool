/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.util.ArrayList;

/**
 *
 * @author marcushaupt
 */
public class BettingRoundCycle {
    
    ArrayList<PlayerAction>     actions                  =   new ArrayList<PlayerAction>();

    
    public void addAction(PlayerAction pa) {    
        actions.add(pa);
    }    
    
    public int numActions() {
        return actions.size();
    }
    
    PlayerAction getActionNumber(int n) {
         if ( (n >= 0) && (n <= actions.size()-1)) {
            return actions.get(n);
        } else
            return null;
    }
    
}
