/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author marcushaupt
 * 
 * @version     1.0
 * 
 * Session holds the universal logger and the list of players
 * 
 * Logging is via log4j
 * The name of the thread is used to differentiate different sessions
 * multi-tabling is not currently supported
 * 
 * the logType enum contains both general levels and levels relating to unit 
 * tests of features. Each unit test sets the logTypes it's interested in.
 * 
 * All prints to the console or a file are done through static functions in
 * this class.
 * 
 * Exit is only called through this class.
 * 
 * The player list, a struct containing all the players seen so for in this
 * session is kept such that in a multi-tabled environment the app would add
 * users in a thread-safe way (synchronized writes)
 * 
 */



public class Session implements Runnable {
    
    private static          String  userName        =   null;
    
    private static final    Logger  LOG  = LogManager.getRootLogger();
    
    private static  final   SortedMap<String,Player> PLAYERLIST = Collections.synchronizedSortedMap(new TreeMap());
    
    public  static  enum    logType { ERROR, INFO, WARN, ANCHOR, CARDREAD, GAME, POT, STARTUP, 
                                      TABLE_NAME, HAND_NUMBER, TABLE_NUMBER, SHIELD_NUMBER,
                                      SHIELD_CHAR, REPAIR, PLAYER_MOVE, CLOCK, MERGE, SPLIT, NEWGAME,
                                      FINDUSER, CARDRANK, DAEMONREADSHIELD, GAMETRANSITION, INVALIDANCHOR
                                    }
    
    public  static  ArrayList<logType>  messagesToLog   =   new ArrayList<>();
    
    
    public  static void setUserName(String user) {
        userName    =    user;
    }
    
    public  static  String  userName() {
        return  userName;
    }
    
    public  static  final void setLogLevels(logType ... lt) {
        messagesToLog.addAll(Arrays.asList(lt));
    }
    
    public  static  final   void  addLogLevel(logType t) {
        if ( !messagesToLog.contains(t) )
            messagesToLog.add(t);
    }
    
    public  static  final   void  removeLogLevel(logType t) {
        if ( !messagesToLog.contains(t) )
            messagesToLog.remove(t);
    }
    
    public  static  final   boolean logLevelSet(logType lt) {
        
        if ( messagesToLog != null ) {
            return  messagesToLog.contains(lt);
        }
        
        return false;
    }
    
    public  static final Player findOrAddPlayer (String screenName) {
        
        if ( ! PLAYERLIST.containsKey(screenName) )
            PLAYERLIST.put(screenName, new Player(screenName));
        
        return  getPlayer(screenName);
    }
   
    public  static final void    addPlayer(Player p) {
        PLAYERLIST.put(p.getScreenName(), p);
    }
    
    public  static final Player  getPlayer(String screenName) {
        return PLAYERLIST.get(screenName);
    }
    
    public  static  final void logMessageLine(String msg) {
        String name = Thread.currentThread().getName();
        
        System.out.println(msg);
    }
    
    public  static  final void logMessageChar(String msg) {
        System.out.print(msg);
    }
    
    public  static  final   void sendLogMessage(String msg) {
        LOG.info(msg);
    }
    
    public  static  final void exit(int n) {
        System.out.println("Session:exit\tExiting");
        System.exit(n);
    }
    
    @Override
    public void run () {
    
    }
    
}
