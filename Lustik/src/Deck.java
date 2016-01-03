import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Deck {
	private List<Card> deck;
	
	//String[] colors = {"karo","srce","kriz","pik"};
	private String[] colors = {"karo","karo","karo","karo"};
	private String[] values = {"7","8","9","10","J","Q","K","A"};
	
	public Deck(){
		deck=new ArrayList<Card>();
		for(int i=0;i<colors.length;i++){
			for(int j=0;j<values.length;j++){
				deck.add(new Card(colors[i],values[j]));
			}
		}
		Collections.shuffle(deck);
	}
	
	public int numCards(){
		return deck.size();
	}
	
	public Card pullCard(){
		Card c = deck.get(0);
		deck.remove(0);
		return c;
	}
	
	public boolean isEmpty(){
		return deck.isEmpty();
	}
	
	public String toString(){
		String str = "";
		for(Card c : deck){
			str=str+c.toString()+"\n";
		}
		return str;
	}
}
