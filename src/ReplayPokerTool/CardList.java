/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package ReplayPokerTool;

import java.util.*;

/**
 *
 * List of Cards used by Game
 * <ul>
 *<li>to hold the board getCardList</li>
 * <li>to hold the hole getCardList for each player</li>
 * </ul>
 * 
 * use function  <b>cardNumber(int i)</b> to get the getCardList from the list
 * 
 * @author Marcus Haupt
 * 
 * @version 1.0
 * 
 */
public class CardList {
    
    
    private final ArrayList<Card> cards_   =   new ArrayList<>();
    
    
    public CardList() {
        
    }
    
    /**
     * 
     * @return {@literal  ArrayList<Card>} defensive copy 
     */
    public  ArrayList<Card> getCardList() {
        return  new ArrayList<>(cards_);
    }
        
    /**
     * 
     * @param cards list of getCardList to add 
     */
    public  void    addCards(Card ... cards) {
        
        cards_.addAll(Arrays.asList(cards));
    }
    
    
    /**
     * 
     * @param c Card
     */
    public void     addCard(Card c) {
        cards_.add(c);
    }
    
    
    /**
     * 
     * @param c Card
     */
    public  void    removeCard(Card c) {
        
        cards_.remove(c);
        
    }
    
    /**
     * 
     * number of getCardList in the list
     * 
     * @return int 
     */
    public  int     size() {
        return  cards_.size();
    }
    
    
    /**
     * 
     * return the card at a given index
     * performs range checking
     * 
     * @param i integer
     * @return 
     */
    
    public  Card    cardNumber(int i) {
        if ( (i < 0) || (i> size()) )
            return null;
        
        return  cards_.get(i);
    }
    
}
