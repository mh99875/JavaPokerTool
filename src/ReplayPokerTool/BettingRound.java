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
public class BettingRound {

    ArrayList<BettingRoundCycle>     cycles           =   new ArrayList<BettingRoundCycle>();
    
    public BettingRound() {
        addCycle();
    }
    
    
    public void addCycle() {
        cycles.add( new BettingRoundCycle());
    }
    
    BettingRoundCycle    currentCycle() {

        return getCycleNumber(numCycles()-1);
    }
    
    
    public int      numCycles() {
        return cycles.size();
    }
    
    public BettingRoundCycle getCycleNumber(int n) {
        if ( (n >= 0) && (n <= cycles.size()-1)) {
            return cycles.get(n);
        } else
            return null;
    }
    
    
}
