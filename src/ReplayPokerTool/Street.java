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

public class Street {
    
    int num_                =   -1;
    int initPot_            =   0;
    int finalPot_           =   0;
    int numPlayers_         =   0;
    int numBets_            =   0;
    
    
    public  Street(int n) {
        num_    =   n;
    }
    
    
    public  Street(Street s) {
        num_            =   s.num_;
        initPot_        =   s.initPot_;
        finalPot_       =   s.finalPot_;
        numPlayers_     =   s.numPlayers_;
        numBets_        =   s.numBets_;
    }
    
    
    public  int potDelta () {
        if ( (initPot_ > 0) && (finalPot_>0) )
            return finalPot_ - initPot_;
        else
            return finalPot_ > 0 ? finalPot_ : initPot_;
            
    }
    
    public  int numBets() {
        return  numBets_;
    }
    
    
    public  int finalPot() {
            return finalPot_;
    }
    
    public  int avgBet() {
        if ( (potDelta() > 0 ) && (numBets_ >0) )
            return potDelta() / numBets_;
        else
            return 0;
    }
    
    
    public  int numPlayers() {
        return  numPlayers_;
    }
}
