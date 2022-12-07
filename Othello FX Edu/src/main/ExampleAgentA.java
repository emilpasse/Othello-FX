package main;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.eudycontreras.othello.capsules.AgentMove;
import com.eudycontreras.othello.capsules.ObjectiveWrapper;
import com.eudycontreras.othello.capsules.MoveWrapper;
import com.eudycontreras.othello.controllers.AgentController;
import com.eudycontreras.othello.controllers.Agent;
import com.eudycontreras.othello.enumerations.PlayerTurn;
import com.eudycontreras.othello.models.GameBoardState;
import com.eudycontreras.othello.threading.ThreadManager;
import com.eudycontreras.othello.threading.TimeSpan;

/**
 * <H2>Created by</h2> Eudy Contreras
 * <h4> Mozilla Public License 2.0 </h4>
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <a href="https://www.mozilla.org/en-US/MPL/2.0/">visit Mozilla Public Lincense Version 2.0</a>
 * <H2>Class description</H2>
 * 
 * @author Eudy Contreras
 */
public class ExampleAgentA extends Agent{
	
	static int MAX = 1000;
	static int MIN = -1000;
	PlayerTurn otherPlayerTurn;

	public ExampleAgentA() {
		this(PlayerTurn.PLAYER_ONE);
		// TODO Auto-generated constructor stub
	}
	
	public ExampleAgentA(String name) {
		super(name, PlayerTurn.PLAYER_ONE);
		otherPlayerTurn = PlayerTurn.PLAYER_TWO;
	}
	
	private ExampleAgentA(PlayerTurn playerTurn) {
		super(playerTurn);
		if(playerTurn == PlayerTurn.PLAYER_ONE)
			otherPlayerTurn = PlayerTurn.PLAYER_TWO;
		else
			otherPlayerTurn = PlayerTurn.PLAYER_ONE;
	}

	/**
	 * Delete the content of this method and Implement your logic here!
	 */
	@Override
	public AgentMove getMove(GameBoardState gameState) {
		System.out.println("Making move");
		List<ObjectiveWrapper> moves = AgentController.getAvailableMoves(gameState, playerTurn);
		return minimax(0, 0, true, moves, MIN, MAX, gameState);
	}

	private MoveWrapper minimax(int depth, int nodeIndex, boolean agentTurn, List<ObjectiveWrapper> moves, int alpha, int beta, GameBoardState gameState) {
		if(depth == 10){
			return new MoveWrapper(moves.get(0));
		}

		if(agentTurn){
			int best = MIN;
			MoveWrapper move  = new MoveWrapper(moves.get(0));

			for(int i = 0; i < moves.size(); i++){

				GameBoardState newState = AgentController.getNewState(gameState, moves.get(i));
				List<ObjectiveWrapper> newMoves = AgentController.getAvailableMoves(newState, otherPlayerTurn);

				if(newMoves.size() < 1){
					//move = new MoveWrapper(newMoves.get(0));
					break;
				}

				move = minimax(depth + 1, 2 * nodeIndex + i, false, newMoves, alpha, beta, newState);
				best = Math.max(best, move.getMoveReward());
            	alpha = Math.max(alpha, best);
				
				System.out.println(depth + " " + i);

				// Alpha Beta Pruning
				if (beta <= alpha)
					break;
			}
			return move;
		}
		else{
			int best = MAX;
			MoveWrapper move = new MoveWrapper(moves.get(0));

			for (int i = 0; i < moves.size(); i++)
			{
				GameBoardState newState = AgentController.getNewState(gameState, moves.get(i));
				List<ObjectiveWrapper> newMoves = AgentController.getAvailableMoves(newState, playerTurn);

				if(newMoves.size() < 1){
					//move = new MoveWrapper(newMoves.get(0));
					break;
				}

				move = minimax(depth + 1, 2 * nodeIndex + i, true, newMoves, alpha, beta, newState);
				best = Math.min(best, move.getMoveReward());
				beta = Math.min(beta, best);

				System.out.println(depth + " " + i);
				// Alpha Beta Pruning
				if (beta <= alpha)
					break;
			}
			return move;
		}
	}
	
	/**
	 * Default template move which serves as an example of how to implement move
	 * making logic. Note that this method does not use Alpha beta pruning and
	 * the use of this method can disqualify you
	 * 
	 * @param gameState
	 * @return
	 */
	private AgentMove getExampleMove(GameBoardState gameState){
		
		int waitTime = UserSettings.MIN_SEARCH_TIME; // 1.5 seconds
		
		ThreadManager.pause(TimeSpan.millis(waitTime)); // Pauses execution for the wait time to cause delay
		
		return AgentController.getExampleMove(gameState, playerTurn); // returns an example AI move Note: this is not AB Pruning
	}

}
