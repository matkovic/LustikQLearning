import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;


public class Player {
	String name;
	private List<Card> cardsInHand;
	private List<Card> cardsWon;
	private boolean wonLastRound;
	
	private boolean learner;
	private boolean learner2;
	private boolean human;
	
	HashMap Qmemory;
	private List Qkey;
	
	private Card action;
	
	final double alpha = 0.1;
	final double gamma = 0.6;
	final double epsilon = 0.2;
	
	private Player myTeammate;
	
	int totalWins;
	
	public Player(String name, boolean learner,boolean learner2, boolean human){
		this.name=name;
		cardsInHand=new ArrayList<Card>();
		cardsWon = new ArrayList<Card>();
		wonLastRound=false;
	
		this.learner=learner;
		this.learner2=learner2;
		this.human=human;
		
		if(learner)
			Qmemory=new HashMap<List<Card>,HashMap<Card,Double>>();
		else if(learner2)
			Qmemory= new HashMap<List<Collection>,HashMap<Card,Double>>();
		else
			Qmemory = new HashMap();
		
		totalWins=0;
		
		myTeammate=null;
	}
	
	public void setCard(Card card){
		cardsInHand.add(card);
	}
	
	public void setCardsWon(List<Card> win){
		cardsWon.addAll(win);
	}
	
	public void setWonLastRound(boolean won){
		this.wonLastRound=true;
	}
	
	public void setMyTeammate(Player tm){
		this.myTeammate=tm;
	}
	
	public void setQmemory(HashMap hm){
		this.Qmemory=hm;
	}
	
	public Card throwCard(boolean overTake){ /////learn
		if(!human){
			double rand = Math.random();
			Random randomGenerator = new Random();
			Card c = cardsInHand.get(randomGenerator.nextInt(cardsInHand.size()));
			
			if(learner){
				Qkey =  new ArrayList<Card>(GameLogic.onTableCards);
			}
			else if(learner2){
				List<Card> currentState = new ArrayList<Card>(GameLogic.onTableCards);
				List<Card> inHands = new ArrayList<Card>(this.cardsInHand);
				Collections.sort(inHands, new CustomComparator());
				
				Qkey= new ArrayList<Collection>();
				Qkey.add(currentState);
				Qkey.add(inHands);
			}	
			
			if(overTake){
				c=pickCardToTakeOver();
			}
			else{
				if((!learner && !learner2) || rand>(1-epsilon) || !Qmemory.containsKey(Qkey)) //explore
					c = cardsInHand.get(randomGenerator.nextInt(cardsInHand.size()));
				else { 	//exploit
					HashMap<Card,Double> eSet = (HashMap<Card, Double>) Qmemory.get(Qkey);
					double maxQ=maxQ(eSet);
					for(Entry<Card, Double> entry : eSet.entrySet()){
						if(maxQ==entry.getValue() && cardsInHand.contains(entry.getKey())){
							c=entry.getKey();
							break;
						}
					}
				}
			}
			cardsInHand.remove(c);
			
			//
			action = c;
			//
			
			return c;
		}
		else{
			System.out.println(GameLogic.onTableCards);
			System.out.println(this.cardsInHand);
			Scanner scan = new Scanner(System.in);
			int dec=scan.nextInt()-1;
			Card c = cardsInHand.get(dec);
			cardsInHand.remove(dec);
			action=c;
			return c;
		}
	}
	
	public class CustomComparator implements Comparator<Card> {
	    @Override
	    public int compare(Card o1, Card o2) {
	        //return (o1.getValue()+""+o1.getColor()).compareTo((o2.getValue()+""+o2.getColor()));
	    	return (o1.getValue()).compareTo(o2.getValue());
	    }
	}
	
	public boolean willThrowNext(){ /////learn
		if(!human){
			double rand = Math.random();
			
			if((!learner2 && !learner) || rand>(1-epsilon)){
				if(rand>0.5 && (containsValue(GameLogic.onTableCards.get(0).getValue()) || containsValue("7"))) return true;
				return false;
			}
			else{
				if((containsValue(GameLogic.onTableCards.get(0).getValue()) || containsValue("7"))){
					HashMap<Card,Double> eSet = (HashMap<Card, Double>) Qmemory.get(Qkey);
					for(Entry<Card, Double> entry : eSet.entrySet()){
						if(containsValue(entry.getKey().getValue())){
							return true;
						}
					}
					return false;
				}
				else return false;		
			}
		}
		else{
			if((containsValue(GameLogic.onTableCards.get(0).getValue()) || containsValue("7"))){
				System.out.println(GameLogic.onTableCards);
				System.out.println(this.cardsInHand + " ::::will throw next? true/false");
				Scanner scan = new Scanner(System.in);
				boolean dec=scan.nextBoolean();
				return dec;
			}
			else return false;
		}
	}

	public void updateMemory(Player winner){
		if((learner || learner2) && action!=null){
			if(Qmemory.containsKey(Qkey)){
				HashMap <Card,Double> prevActionsValues = (HashMap<Card, Double>) Qmemory.get(Qkey);
				
				if(prevActionsValues.containsKey(action)){
					double oldValue = prevActionsValues.get(action);
					
					double maxQ = maxQ(prevActionsValues);
					double r=-pointsWon(GameLogic.onTableCards);
					if(winner.equals(this) || winner.equals(myTeammate)) r=-r;
					
					double value = (oldValue + alpha * (r+gamma*maxQ - oldValue));
					
					prevActionsValues.put(action, value);
				}
				else{
					prevActionsValues.put(action, 0.0);
				}
				Qmemory.put(Qkey, prevActionsValues);
			}
			else{
				HashMap<Card,Double> act = new HashMap<Card,Double>();
				act.put(action, 0.0);
				Qmemory.put(Qkey, act);
			}
		}
	}

	
	
	private double maxQ(HashMap<Card,Double> actVal){
		Map.Entry<Card, Double> maxEntry = null;

		for (Entry<Card, Double> entry : actVal.entrySet()){
		    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0){
		        maxEntry = entry;
		    }
		}
		return maxEntry.getValue();
	}
	
	
	public boolean containsValue(String value){
		for(Card c : this.cardsInHand){
			if(c.getValue().equals(value)){
				return true;
			}
		}
		return false;
	}
	
	public Card pickCardToTakeOver(){
		Card pick = null;
		double max=Double.NEGATIVE_INFINITY;
		HashMap<Card,Double> eSet = (HashMap<Card, Double>) Qmemory.get(Qkey);
		
		for(Card c : this.cardsInHand){
			if(GameLogic.onTableCards.get(0).getValue().equals(c.getValue()) || c.getValue().equals("7")){ 
				if(eSet==null || !eSet.containsKey(c)){
					pick=c;
					break;
				}
				else{
					if(eSet.get(c)>=max){
						pick=c;
						max=eSet.get(c);
					}
				}
			}
		}	
		return pick;
	}
	
	
	public int pointsValue(){
		int points=0;
		for(Card c : cardsWon){
			if(c.getValue().equals("10") || c.getValue().equals("A")){
				points+=10;
			}
		}
		if(wonLastRound) points+=10;
		
		return points;
	}
	private int pointsWon(List<Card> crds){
		int points=0;
		for(Card c : crds){
			if(c.getValue().equals("10") || c.getValue().equals("A")){
				points+=10;
			}
		}
		return points;
	}
	
	
	public boolean noCardsInHands(){
		return cardsInHand.isEmpty();
	}
	
	public void winPlus(){
		totalWins++;
	}
	public int getTotalWins(){
		return totalWins;
	}
	
	public int getNumCardsInHands(){
		return this.cardsInHand.size();
	}
	public void setCardsWonRemove(){
		this.cardsWon=new ArrayList<Card>();;
	}
	
	public String toString(){
		String str = "player "+this.name+":";
		for(Card c : cardsInHand){
			str+=c.toString()+", ";
		}
		return str;
	}
	
}
