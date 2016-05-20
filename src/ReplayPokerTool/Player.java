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
public class Player {
    
    int                         databaseID_         =   0;
    String                      screenName_;
    HandEvaluator               he_;
    ArrayList<Game>             gamesPlayed_        =   new ArrayList<>();
    
    
    public  HandEvaluator   he() {
        return  he_;
    }
    
    
    ArrayList<Game>         gamesPlayed() {
        return      gamesPlayed_;
    }
    
    int                     numGamesPlayed() {
        return  gamesPlayed().size();
    }
    
   
    synchronized void       addGamePlayed(Game g) {
        gamesPlayed_.add(g);
    }
     
    public  void            setScreenName(String sn) {
        screenName_ =   sn;
    }
    
    public  String          getScreenName() {
        return screenName_;
    }
    
    public  void            setDatabaseId(int id) {
        databaseID_ =   id;
    }
    
    public  int             getDatabaseId() {
        return  databaseID_;
    }
    
    
    // make as a defensive copy
    public                  Player(Player p ) {
        databaseID_         =   p.databaseID_;
        screenName_         =   p.screenName_;
        
    }
    
    public                  Player(String  sName ) {
        screenName_ =   sName;
    }
    
}
