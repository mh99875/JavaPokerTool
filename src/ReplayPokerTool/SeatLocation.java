/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

import java.awt.Point;

/**
 *
 * @author marcushaupt
 * @version     1.0
 * 
 * Based on the seat number and the number of seats at the table determine the
 * neighborhood of where the shield should be and the offsets to bets where we
 * will find the button once the shield location is fixed in ReplayPokerTable
 * 
 * 
 */


public class SeatLocation {
    
    private int  seatNum;
    private int  numSeatsAtTable;
    private int  shieldX;
    private int  shieldY;
    private int  betX;
    private int  betY;
    private int  buttonX;
    private int  buttonY;
    
    
    int shieldX() {
        return  shieldX;
    }
    
    int shieldY() {
        return  shieldY;
    }
    
    int betX() {
        return  betX;
    }
    
    int betY() {
        return  betY;
    }
    
    int buttonX() {
        return  buttonX;
    }
    
    int buttonY() {
        return buttonY;
    }
    
    
    void    moveByOffset(Point offset) {
        
        shieldX     +=  offset.x;        
        shieldY     +=  offset.y;
        
        betX        +=  offset.x;
        betY        +=  offset.y;
        
        buttonX     +=  offset.x;
        buttonY     +=  offset.y;
        
    }
    
    void    setShieldLocations (int shieldX, int shieldY) {
        
        String  prefix  =   this.getClass().getSimpleName() + ":setShieldLocations[" + seatNum + "]";
                
        this.shieldX    =   shieldX;
        this.shieldY    =   shieldY;     
        
        if ( Session.logLevelSet(Session.logType.INFO)  )
            Session.logMessageLine(prefix + "\tNum Seats: " + numSeatsAtTable );
        
        switch ( numSeatsAtTable ) {
            
            case 4:
                
                switch ( seatNum ) {
                    case 0:
                        buttonX        =   this.shieldX + 45;
                        buttonY        =   this.shieldY + 25;
                        betX           =   this.shieldX + 94;
                        betY           =   this.shieldY + 48;
                        
                        if ( Session.logLevelSet(Session.logType.INFO)  )
                            Session.logMessageLine(prefix + "\tBet (X,Y) = (" + betX 
                                + "," + betY + ")" );
                    break;
                    
                    
                    case 1:
                        buttonX        =   this.shieldX  + 5;
                        buttonY        =   this.shieldY  + 20;
                        betX           =   this.shieldX  - 20;
                        betY           =   this.shieldY  + 34;
                        
                        if ( Session.logLevelSet(Session.logType.INFO)  )
                            Session.logMessageLine(prefix + "\tBet (X,Y) = (" + betX 
                                + "," + betY + ")" );
                        
                    break;
                    
                    
                    case 2:
                        buttonX        =   this.shieldX - 35;
                        buttonY        =   this.shieldY - 25;
                        betX           =   this.shieldX - 5;
                        betY           =   this.shieldY - 23;
                        
                        if ( Session.logLevelSet(Session.logType.INFO)  )
                            Session.logMessageLine(prefix + "\tBet (X,Y) = (" + betX 
                                + "," + betY + ")" );
                    break;
                    
                    
                    case 3:
                        buttonX        =   this.shieldX + 90;
                        buttonY        =   this.shieldY - 25;
                        betX           =   this.shieldX + 103;
                        betY           =   this.shieldY - 23;
                        
                        if ( Session.logLevelSet(Session.logType.INFO)  )
                            Session.logMessageLine(prefix + "\tBet (X,Y) = (" + betX 
                                + "," + betY + ")" );
                    break;
                }
                break;
                
                
                
            case 6:
            
                switch ( seatNum ) {
                    case 0:
                        buttonX        =   this.shieldX;
                        buttonY        =   this.shieldY + 20;
                        betX           =   this.shieldX + 50;
                        betY           =   this.shieldY + 68;
                        
                   break;
                   
                    case 1:
                        buttonX        =   this.shieldX    +   65;
                        buttonY        =   this.shieldY    +   15;
                        betX           =   this.shieldX    +   40;
                        betY           =   this.shieldY    +   68;
                    break;
                    
                    
                    case 2:
                        buttonX        =   this.shieldX    -   12;
                        buttonY        =   this.shieldY    +   15;
                        betX           =   this.shieldX    -   22;
                        betY           =   this.shieldY    -   4;
                        break;

                        
                    case 3:
                        buttonX        =   this.shieldX    - 25;
                        buttonY        =   this.shieldY    - 50;
                        betX           =   this.shieldX    + 45;
                        betY           =   this.shieldY    - 88;
                        break;

                        
                    case 4:
                        buttonX        =   this.shieldX    + 80;
                        buttonY        =   this.shieldY    - 55;
                        betX           =   this.shieldX    + 45;
                        betY           =   this.shieldY    - 87;
                        break;

                        
                    case 5:
                        buttonX        =   this.shieldX    + 65;
                        buttonY        =   this.shieldY    + 15;
                        betX           =   this.shieldX    + 105;
                        betY           =   this.shieldY    + 15;
                        break;
                                
                }
            
            break;
            
            
            case 9:
            
                switch ( seatNum ) {
                
                    case 0:
                        betX               =   this.shieldX    + 55;
                        betY               =   this.shieldY    + 68;
                        buttonX            =   this.shieldX    + 1;
                        buttonY            =   this.shieldY    + 14;
                    break;
                    
                    case 1:
                        betX               =   this.shieldX    + 40;
                        betY               =   this.shieldY    + 65;
                        buttonX            =   this.shieldX    + 60;
                        buttonY            =   this.shieldY    + 15;
                    break;
                
                    
                    case 2:
                        betX               =   this.shieldX    - 15;
                        betY               =   this.shieldY    + 34;
                        buttonX            =   this.shieldX    + 8;
                        buttonY            =   this.shieldY    + 20;
                    break;
                
                
                    case 3:
                        betX               =   this.shieldX    - 17;
                        betY               =   this.shieldY    - 4;
                        buttonX            =   this.shieldX    - 10;
                        buttonY            =   this.shieldY    + 10;
                    break;
                
                
                    case 4:
                        betX               =   this.shieldX    + 37;
                        betY               =   this.shieldY    - 90;
                        buttonX            =   this.shieldX    - 73;
                        buttonY            =   this.shieldY    - 45;
                        break;
                    
                    case 5:
                        betX               =   this.shieldX    + 103;
                        betY               =   this.shieldY    - 80;
                        buttonX            =   this.shieldX    - 30;
                        buttonY            =   this.shieldY    - 65;
                        
                    break;
                    
                
                    case 6:
                        betX               =   this.shieldX    + 75;
                        betY               =   this.shieldY    - 86;
                        buttonX            =   this.shieldX    + 80;
                        buttonY            =   this.shieldY    - 40;
                    break;
                
                    
                    case 7:
                        betX               =   this.shieldX    + 107;
                        betY               =   this.shieldY    + 13;
                        buttonX            =   this.shieldX    + 65;
                        buttonY            =   this.shieldY    + 12;
                    break;
                
                    
                    case 8:
                        betX               =   this.shieldX    + 98;
                        betY               =   this.shieldY    + 48;
                        buttonX            =   this.shieldX    + 45;
                        buttonY            =   this.shieldY    + 20;
                    break;
                } // end switch 9 seat table
        } // end switch num seats at the table
    }
    
    
    
    
    // sets the estimate of where the shield might be 
    // at a position above and to the right of the shield
    public  SeatLocation(int seatNum, int numSeatsTable) 
    throws  InvalidSeatLocationException
    {
        
        this.seatNum            =   seatNum;
        numSeatsAtTable    =   numSeatsTable;
        
        switch ( numSeatsAtTable ) {
            
            // load the offset from the screen anchor
            
            case 4:
                switch ( seatNum ) {
                    case 0:
                        shieldX         = -65;
                        shieldY         =  85;
                    break;
                    
                    case 1:
                        shieldX         = 495;
                        shieldY         = 85;
                    break;
                    
                    case 2:
                        shieldX         = 480;
                        shieldY         = 303;
                    break;
                    
                    case 3:
                        shieldX         = -34;
                        shieldY         = +314;
                    break;
                    
                }
                break;
            
            case 6:
            
                switch ( seatNum ) {
                    case 0:
                        shieldX         = 90;
                        shieldY         = 20;
                   break;

                    case 1:
                        shieldX         = 330;
                        shieldY         = 20;
                    break;

                    case 2:
                        shieldX         = 519;
                        shieldY         = 200;
                        break;

                    case 3:
                        shieldX         = 320;
                        shieldY         = 343;
                        break;

                    case 4:
                        shieldX         = 125;
                        shieldY         = 345;
                        break;

                    case 5:
                        shieldX         = -88;
                        shieldY         = 200;
                        break;
                        
                    default:
                        throw   new InvalidSeatLocationException("Seat[" + seatNum + "] TableSeats = " + numSeatsTable );
                                
                }
            
            break;
            
            
            case 9:
            
                switch ( seatNum ) {
                
                    case 0:
                        shieldX             = 90;
                        shieldY             = 20;
                    break;
                    
                    case 1:
                        shieldX             = 330;
                        shieldY             = 20;
                    break;
                
                    
                    case 2:
                        shieldX             = 490;
                        shieldY             = 80;
                    break;
                
                
                    case 3:
                        shieldX             = 520;
                        shieldY             = 200;
                    break;
                
                
                    case 4:
                        shieldX             = 420;
                        shieldY             = 335;
                    break;
                
                
                    case 5:
                        shieldX             = 220;
                        shieldY             = 350;
                    break;
                    
                
                    case 6:
                        shieldX             = 15;
                        shieldY             = 335;
                    break;
                
                    case 7:
                        shieldX             = -85;
                        shieldY             = 200;
                    break;
                
                    case 8:
                        shieldX             = -65;
                        shieldY             = 80;
                    break;
                
                    default:
                        throw   new InvalidSeatLocationException("Seat[" + seatNum + "] TableSeats = " + numSeatsTable );
                } // end switch 9 seat table
        } // end switch num seats at the table
    } // end SeatLocation CTOR
    
    
} // end Class Definition
                    