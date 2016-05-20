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
 * @version     1.0
 * 
 * List of actions performed by player during the course of the game
 * 
 */
public class PlayerActions {
    
    
    private final ArrayList<PlayerAction> actions_    =   new ArrayList<>();
    
    
    public  void addAction(PlayerAction p) {
        actions_.add(p);
    }
    
    
    public  int maxInvestmentForRound(Game.Round round) {
        int max =   0;
        
        if ( !actions_.isEmpty() ) {
          
            for (int i = 0; i < actions_.size(); i++) {
                if ( actions_.get(i).round() == round )  {
                    if ( actions_.get(i).amount() > max )
                        max =  actions_.get(i).amount();
                }
            }
        }
        
        return  max;
    }
    
    
    public  int actionType(Game.ActionType type) {
        int n = 0;
        
        for (int i = 0; i < actions_.size(); i++) {
            if ( actions_.get(i).action() == type )
                n++;
        }
        
        return  n;
    }
    
   
    
    
}
