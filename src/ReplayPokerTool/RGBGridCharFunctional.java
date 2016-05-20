/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReplayPokerTool;

/**
 *
 * @author marcushaupt
 * @version 1.0
 */
public class RGBGridCharFunctional extends RGBGridCharShield {
    
    RGBGridCharFunctional (int nRows, int nCols, int ntsOff) {
        super(nRows, nCols);
    }
    
    public  RGBGridCharFunctional(RGBGridCharShield map, int sCol, int eCol) {
        super(map, sCol, eCol);
    }
    
    
     
    @Override
    public  String  charFromGrid() 
    throws UnableToReadCharException
    {

        String  prefix  =   this.getClass().getSimpleName() + ":RGBMapCharFunctional:charFromMap";
        
        String ch       =   descendTreeFromRoot();
        
        Session.logMessageLine(prefix + "**********>" + ch + "\n\n");
        
        return ch;
              
    } // end of function
    
}
