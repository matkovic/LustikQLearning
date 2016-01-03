
public class Card implements java.io.Serializable {
	private String color;
	private String value;
	
	public Card(String color,String value){
		this.color=color;
		this.value=value;
	}
	
	public String getValue(){
		return value;
	}
	public String getColor(){
		return color;
	}
	
	@Override
	public int hashCode(){
		return value.hashCode();
	}
	
	public boolean equals(Object obj) {
	    if(obj instanceof Card){
	    	Card c = (Card) obj;
	    	if(c.getColor().equals(this.color) && c.getValue().equals(this.value))
	    		return true;
	    }
	    return false;
	}
	
	public String toString(){
		return this.color+" "+this.value;
	}
}
