/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;


/**
 *
 * @author marcushaupt
 */
public class Database implements Runnable {
    
    Connection              con             = null;
    Game                   table_;
    
    Card    [] boardCards                   =   new Card[5];

    int []  betsPerStreet                   = new int[4];
 
    static  int openCalls                   =  0;
    
    public  Database() {
        
    }
    
    public  Game   table() {
        return table_;
    }
    
    
    public  Database(Game table, CardUtilities ca) {
        
          
        
        // copy the player actions 
    }

    public  void    displayDataToSave() {
        
        String  prefix  =   "DB:dataToSave";
        
        int i;
        String  fmt;
        int     totalBet    = 0;
        
        /*
        System.out.println(prefix + "\tBBB\t"       +   table().bigBlindBet());
        System.out.println(prefix + "\tSB\t"        +   table().smallBlindSeat());
        System.out.println(prefix + "\tBB\t"        +   table().bigBlindSeat());
        System.out.println(prefix + "\tBT\t"        +   table().buttonSeat());
        System.out.println(prefix + "\tSeated\t"    +   table().numSeated());
        System.out.println(prefix + "\tFinal Pot\t" +   table().lastTablePotRead());
       
         for (int p=0; p<table().numSeats(); p++ ) {
            int scs = table().players_[p].getChipStack();
            
            fmt =   String.format("[%d]%14s SOH:%6d\tEOH: %6d\tTBET:%d", 
                p,
                table().players_[p].getScreenName(),
                (scs >= 0 ? scs : 0), 
                table().players_[p].totalAmountBet());      
            
                System.out.println(fmt);
                
            totalBet    += table().players_[p].totalAmountBet();
         }

        for (i=0; i<5; i++) 
            if ( boardCards[i] !=null ) {
                System.out.println(prefix + "\tBC[" + i + "] = " + 
                        boardCards[i].symbol() );
        }
        
        for (int p=0; p<table().numSeats(); p++ ) {
            if ( table().players_[p].hc0_ != null ) 
                System.out.println(prefix + "\tSEAT[" + p + "] HC0 " + 
                        table().players_[p].hc0_.symbol() );
            
            if ( table().players_[p].hc1_ != null ) 
                System.out.println(prefix + "\tSEAT[" + p + "] HC1 " + 
                        table().players_[p].hc1_.symbol() );
        }
        
        for (i=0; i<5; i++) {
            System.out.println(prefix + "\tStreet[" + i + "] Final " +
                    table().streets_[i].finalPot() + " Players " +
                    table().streets_[i].numPlayers() + " NumBets " +
                    table().streets_[i].numBets() );
        }
        
        System.out.println(prefix + "\tTOTAL PLAYERS BETS: " + totalBet);
        System.out.println(prefix + "\tTABLE FINAL POT   : " + table().lastTablePotRead());
        */
       
    }
    
    public  void    insertCardSymbols() {
        String  prefix  =   "DB:insertCardSymbols";
        String  symbol, rank, suit;
        
        for (int i=1; i<=52; i++) {
            
            if ( i<=13 )
                suit    =   "S";
            else if ( i <= 26 )
                suit    =   "H";
            else if ( i <= 39 )
                suit    =   "D";
            else
                suit    =   "C";
            
            switch ( i % 13 ) {
                case 0: 
                    rank    =   "K";
                    break;
                    
                case 1:
                    rank    =   "A";
                    break;
                    
                case 10:
                    rank    =   "T";
                    break;
                    
                case 11:
                    rank    =   "J";
                    break;
                    
                case 12:
                    rank    =   "Q";
                            break;
                                
                default:
                    rank        =   Integer.toString(i % 13);
                    break;
                    
            } 
            
            symbol  =   rank + suit;
            String  query   = "INSERT INTO CARDSYMBOLS (NUM, SYMBOL, RANK, SUIT) VALUES " + 
                             "(" + i + ", '" + symbol + "'" + ",'" +  rank + "', '" + suit + "')";
            
            System.out.println(query);
            
        }
    }
   
    
    public  boolean    insertAllData() {
        boolean result  =   true;
        
        if ( table().handNumber().length() == 0 )
            result = false;
        else {
        
            System.out.println("DB:insertAllData[" + table().handNumber() + "]");
            
            updatePlayerIDs();

            /*
            if ( insertGameAndPlayers(table().handNumber()) ) {

                //System.out.println("Database:saveCardsOnly InsertHoleCards");
                insertHoleCards(table().handNumber());

                //System.out.println("Database:saveCardsOnly InsertBoardCards");            
                insertBoardCards(table().handNumber());
                
                insertBets(table().handNumber());
            } 
            else 
                result  =   false;
           */
        }
        
        return result;
    }
    
   
    public boolean insertBets(int handNumber) {
        
       
                
        return false;
    }
    
    
    public boolean insertGameAndPlayers(int handNumber) {
        
        String  prefix  =   "Database:insertGameAndPlayers";
        boolean result =    true;
        
        Calendar calendar                   = Calendar.getInstance();
        java.util.Date now                  = calendar.getTime();
        java.sql.Timestamp time             = new java.sql.Timestamp(now.getTime());

        return result;
    }
    
    
    public void insertBoardCards(int handNumber) {
        int i;
        String  query       =   "";
        String  prefix      =   "Database:insertBoardCards";
        
        if ( boardCards[0].found && (boardCards[0].getDeckIndex() >0) ) {
            if ( !boardCards[3].found || (boardCards[3].getDeckIndex() < 0) ) {
                
                // only the flop
                query    =   "INSERT INTO BOARDCARDS (GID, BC0, BC1, BC2)  VALUES(" +  
                        handNumber + ","                    + 
                        boardCards[0].getDeckIndex() + ","     +
                        boardCards[1].getDeckIndex() + ","     +
                        boardCards[2].getDeckIndex() + ")";
                
            } else if ( !boardCards[4].found || (boardCards[4].getDeckIndex() <0)) {
                // flop plus river
                
                query    =   "INSERT INTO BOARDCARDS (GID, BC0, BC1, BC2, BC3)  VALUES(" +  
                        handNumber + ","                    + 
                        boardCards[0].getDeckIndex() + ","     +
                        boardCards[1].getDeckIndex() + ","     +
                        boardCards[2].getDeckIndex() + ","     +
                        boardCards[3].getDeckIndex() + ")";
                
            } else if ( boardCards[4].found || (boardCards[4].getDeckIndex() >= 0)){
                // the river too
                query    =   "INSERT INTO BOARDCARDS (GID, BC0, BC1, BC2, BC3, BC4)  VALUES(" +  
                        handNumber + ","                    + 
                        boardCards[0].getDeckIndex() + ","     +
                        boardCards[1].getDeckIndex() + ","     +
                        boardCards[2].getDeckIndex() + ","     +
                        boardCards[3].getDeckIndex() + ","     + 
                        boardCards[4].getDeckIndex() + ")";
            }        
        } else {
            System.out.println("InsertBoardCards: NO BOARD CARDS FOUND");            
        }

        
        try {
            Statement sta = con.createStatement(); 
            
            if ( query.length() > 0 )
                sta.executeUpdate(query);
             
            sta.close();
        } // end try block
        catch (Exception e) {
            System.err.println(prefix + "\t" +e.getMessage() + " failed.");
        }
        
    }
    
    
    public void insertHoleCards(int handNumber) {
        
        String  prefix  =   "Database:insertHoleCards";
        int i;
        
    } // end function
    
   
    
    public int  getPlayerNum(String playerName) {
        int  num    =   -1;

        if ( playerName.length() > 0 ) {
            String query = "SELECT ID FROM PLAYERS WHERE SNAME = '" +
                    playerName + "'";
            try {
                Statement sta = con.createStatement(); 

                ResultSet res = sta.executeQuery(query);

                if (res.next()) {
                    num = res.getInt("id");
                } 
                
                sta.close();

            }
            catch (Exception e) {
                System.err.println("Database:getPlayerNum\tException: "+e.getMessage() + " failed.");
            }
        }
        
        return num;
    }
    
    public  void updatePlayerIDs () {
        
        String  name;
        
    }
    
    public int addPlayer(String playerName ) {
        
        int playerNum   =   getPlayerNum(playerName);
        
        
        if ( (playerNum < 0) && (playerName.length() > 0) ) { 
        
            String query = "INSERT INTO PLAYERS (SNAME) VALUES(" +
                            "'" + playerName + "')";
            boolean err =   executeQueryString(query);
            
            
            if ( !err )
                return getPlayerNum(playerName);
        }
        
        return playerNum;
    }
    
    
    public  boolean    openConnection() {
        boolean success =   true;
        String  prefix  =   "DB:openConnection";
        
        openCalls++;
        
        if ( openCalls >= 2) {
            int [] i    =   new int[0];
            i[999]  =    100;
        }
        
        try {
            //"jdbc:derby://localhost:1527/replay;user=marcus;password=modska");
            con = DriverManager.getConnection(
                    "jdbc:derby://localhost:1527/ReplayHistory;user=replay;password=marcus");
            
            System.out.println(prefix + "\tConnection OK");
             
            insertAllData();
            
        } 
        catch (Exception e) {
            System.err.println("Database:run\tException: " + e.getMessage() + " failed.");
            success =   false;
        }    
        
        if ( !success )
            System.out.println(prefix + "\tConnection Failed");
                    
        return success;
    }
    
    
    
    
    public void     closeConnection() {
        
        try {
            if ( con != null )
                con.close();
        } 
        catch (Exception e) {
            System.err.println("Database\tException: "+e.getMessage() + " failed.");
        }
       
    }
    
    
    
    
    public boolean executeQueryString(String query) {
        
        boolean success =   true;
        
        System.out.println("Database:executeQueryString\t" + query);
        
        try ( Statement sta = con.createStatement() ) { 

            sta.executeUpdate(query);

            sta.close();
        }
        catch (Exception e) {         
            System.err.println("Database:executeQueryString\t" + query + " failed: " + e.getMessage() );
            success =   false;
        }
        
        return success;
    }
    
    
    
    @Override
    public void run () {
       
        boolean onlyDisp    =   true;
        
        if ( onlyDisp ) {
            System.out.println("Database:run. Displaying Data Only");
            displayDataToSave();
        }
        else {
            if ( openConnection() )
                insertAllData();
            else {
                System.out.println("Database:run\tConnection Failed to Open.");
            }
        }
        
    }
    
    
}
