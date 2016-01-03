import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;


public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//List<String> toOutput = new ArrayList<String>();
		//toOutput.add("learn1 learn2 learn1 learn2");
		

		int numPlayers = 2;
		Player[] players = new Player[numPlayers];
		players[0]=new Player("prvi",false,false,true);
		players[1]=new Player("drugi",false,true,false);
		//players[2]=new Player("tretji",true,false,false);
		//players[3]=new Player("cetrti",false,true,false);
		
		
		//if 4 players are playing
		/*
		players[0].setMyTeammate(players[2]);
		players[2].setMyTeammate(players[0]);
		players[1].setMyTeammate(players[3]);
		players[3].setMyTeammate(players[1]);
		
		Player[] team1={players[0],players[2]};
		Player[] team2={players[1],players[3]};
		*/
		
		//players[0].setQmemory((HashMap) deserialize("prvi.ser"));
		players[1].setQmemory((HashMap) deserialize("drugi.ser"));
		
		
		
		int numRounds=10;
		Deck deck = new Deck();
		
		for(int r=0;r<numRounds;r++){
			GameLogic.deal(players,deck);
			
			Player winnerGame=players[0];
			
			while(!deck.isEmpty() || !players[numPlayers-1].noCardsInHands()){
				GameLogic.deal(players,deck);
								
				GameLogic.round(players);
				Player winner = GameLogic.winner(players);

				players=GameLogic.order(players);
				
				if(deck.isEmpty() && players[numPlayers-1].noCardsInHands()){
					winner.setWonLastRound(true);
					
					for(Player p : players){ //check game winner
						if(p.pointsValue() >= winnerGame.pointsValue())
							winnerGame = p;
					}
					winnerGame.winPlus();
					
					players=GameLogic.orderEnd(winnerGame,players);
					
					//System.out.println(winnerGame);
				}
					
				GameLogic.clearTable(winner,players);
			}
			
			deck = new Deck();
			GameLogic.endGame(players);
		}

		

		
		
		for(Player p : players){
			System.out.print(p.name+" :::");
			System.out.println(p.getTotalWins());
		}
		
		
		//if 4 players playing, 2 teams
		/*
		System.out.println("team 1 (prvi,tretji): "+ (team1[0].getTotalWins()+team1[1].getTotalWins()));
		System.out.println("team 2 (drugi,cetrti): "+ (team2[0].getTotalWins()+team2[1].getTotalWins()));
		*/
		

		for(Player p : players){
			
			//to output string to file
			
			PrintWriter out1;
			try {
				out1 = new PrintWriter("mem1_"+p.name+".txt");
				out1.println(p.Qmemory.toString());
				out1.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			
			
			//to serialize memory into file
			
			if(!p.Qmemory.isEmpty()){
				System.out.println("serialize "+p.name+" into file? true/false");
				Scanner scan = new Scanner(System.in);
				if(scan.nextBoolean()==true)
					serialize(p.Qmemory,p.name);
			}
			
		}

		
	}
	
	
	public static void serialize(Object obj,String name){
		try
	      {
	         FileOutputStream fileOut = new FileOutputStream(name+".ser");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(obj);
	         out.close();
	         fileOut.close();
	         System.out.printf("Serialized data is saved in "+name+".ser");
	         System.out.println();
	      }catch(IOException i)
	      {
	          i.printStackTrace();
	      }
	}

	public static Object deserialize(String fileName){
		Object ob;
		try{
			FileInputStream fileIn = new FileInputStream(fileName);
		    ObjectInputStream in = new ObjectInputStream(fileIn);
		    ob = in.readObject();
		    in.close();
		    fileIn.close();
		}catch(IOException i){
		    i.printStackTrace();
		    return null;
		}catch(ClassNotFoundException c){
		    System.out.println("class not found");
		    c.printStackTrace();
		    return null;
	    }
		return ob;
	}

	
}
