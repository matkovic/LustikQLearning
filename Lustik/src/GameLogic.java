import java.util.*;

public class GameLogic {
	static List<Card> onTableCards = new ArrayList<Card>();
	
	public static void deal(Player[] players, Deck deck){
		int deckCards = deck.numCards();
		int playerNumCards = players[0].getNumCardsInHands();
		int toEach = deckCards/players.length;
		
		for(Player p : players){
			for(int i=0;i<Math.min(toEach,4-playerNumCards);i++){
				p.setCard(deck.pullCard());
			}
		}
	}
	
	public static void round (Player[] players){
		int playerToThrow = 0;
		while(true){
			if(playerToThrow!=0 && playerToThrow%players.length==0){
				playerToThrow=0;
				if(!winner(players).equals(players[playerToThrow]) && players[playerToThrow].willThrowNext()){
					onTableCards.add(players[playerToThrow].throwCard(true));
				}else{
					break;
				}
			}
			else{
				onTableCards.add(players[playerToThrow].throwCard(false));
			}
			
			for(Player p : players){
				p.updateMemory(winner(players));
			}
			
			playerToThrow++;
		}
	}
	
	public static Player winner(Player[] players){
		int winner=0;
		for(int i=0;i<onTableCards.size();i++){
			Card ithCard = onTableCards.get(i);
			if(ithCard.getValue().equals(onTableCards.get(0).getValue()) || ithCard.getValue().equals("7")){
				winner=i%players.length;
			}
		}
		return players[winner];
	}
	
	public static void clearTable(Player winner,Player[] players){
		winner.setCardsWon(onTableCards);
		onTableCards = new ArrayList<Card>();
	}
	
	public static void endGame(Player[] players){
		for(Player p : players){
			p.setCardsWonRemove();
			p.setWonLastRound(false);
		}	
	}
	
	public static Player[] order(Player[] players){
		Player winner = winner(players);
		int playerNum=0;
		for(int i=0;i<players.length;i++){
			if(winner.equals(players[i])){
				playerNum=i;
				break;
			}
		}
		Player[] newPlayerOrder = new Player[players.length];
		int cnt=0;
		for(int i=playerNum;i<players.length;i++){
			newPlayerOrder[cnt]=players[i];
			cnt++;
		}
		for(int i=0;i<playerNum;i++){
			newPlayerOrder[cnt]=players[i];
			cnt++;
		}
		return newPlayerOrder;
	}
	
	public static Player[] orderEnd(Player winnerGame, Player[] players){
		int playerNum=0;
		for(int i=0;i<players.length;i++){
			if(winnerGame.equals(players[i])){
				playerNum=i;
				break;
			}
		}
		Player[] newPlayerOrder = new Player[players.length];
		int cnt=0;
		for(int i=playerNum;i<players.length;i++){
			newPlayerOrder[cnt]=players[i];
			cnt++;
		}
		for(int i=0;i<playerNum;i++){
			newPlayerOrder[cnt]=players[i];
			cnt++;
		}
		return newPlayerOrder;
	}	
}
