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
 * Player Action record stored in Lists by Game
 */


public class PlayerAction {
    
    Game.ActionType     action_;
    int                 amount_;
    int                 seat_;
    Game.Round          round_;
    
    
    public PlayerAction(int seat, Game.ActionType action, int amount, Game.Round round) {
        
        seat_       =   seat;
        action_     =   action;
        amount_     =   amount;
        round_      =   round;          
    }
    
    public  int                 seat()          {   return  seat_;      }
    public  Game.ActionType     action()        {   return  action_;    }
    public  int                 amount()        {   return  amount_;    }
    public  Game.Round          round()         {   return  round_;     }
    
}
