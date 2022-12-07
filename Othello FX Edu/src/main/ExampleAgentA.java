package main;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.eudycontreras.othello.capsules.AgentMove;
import com.eudycontreras.othello.capsules.DirectionWrapper;
import com.eudycontreras.othello.capsules.ObjectiveWrapper;
import com.eudycontreras.othello.capsules.MoveWrapper;
import com.eudycontreras.othello.controllers.AgentController;
import com.eudycontreras.othello.controllers.Agent;
import com.eudycontreras.othello.enumerations.BoardCellState;
import com.eudycontreras.othello.enumerations.PlayerTurn;
import com.eudycontreras.othello.models.GameBoardCell;
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
	static int MAX_DEPTH = 3;
	int nodesExamined = 0;
	int depthOfSearch = 0;
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
		nodesExamined = 0;
		depthOfSearch = 0;
		AgentMove move = findMove(gameState);
		System.out.println("Nodes Examined: " + nodesExamined);
		System.out.println("Depth of Search: " + depthOfSearch);
		return move;
	}

	private AgentMove findMove(GameBoardState gameState){
		int bestValue = MIN;
		int alpha = MIN;
		int beta = MAX;
		AgentMove bestMove = null;
		List<ObjectiveWrapper> moves = AgentController.getAvailableMoves(gameState, playerTurn);
		for(int i = 0; i < moves.size(); i++){
			GameBoardState newState = AgentController.getNewState(gameState, moves.get(i));
			MoveWrapper newMove = new MoveWrapper(moves.get(i));
			int value = minimaxAlphaBetaPrune(0, false, alpha, beta, newState, newMove);

			if(bestValue < value){
				bestValue = value;
				bestMove = newMove;
			}
			alpha = Math.max(alpha, bestValue);
			if(beta <= alpha){
				break;
			}
		}
		return bestMove;
	}

	private int heuristic(GameBoardState state, boolean agentTurn){
		int value = 0;
		int potentialMobility = 0;
		int mobility = 0;

		GameBoardCell[][] cells = state.getCells();
		for(int i = 0; i < cells.length; i++){
			for(int j = 0; j < cells[i].length; j++){
				if(cells[i][j].getCellState() == BoardCellState.BLACK)
				for(DirectionWrapper neighbour : cells[i][j].getNeighbors()){
					if(neighbour.getCell().getCellState() == BoardCellState.EMPTY){
						potentialMobility++;
						break;
					}
				}
			}
		}

		if(agentTurn){
			List<ObjectiveWrapper> moves = AgentController.getAvailableMoves(state, playerTurn);
			mobility = moves.size();
		}
		else{
			List<ObjectiveWrapper> moves = AgentController.getAvailableMoves(state, otherPlayerTurn);
			mobility = moves.size();
		}
		
		value = mobility + potentialMobility;

		return value;
	}

	private MoveWrapper minimax(int depth, int nodeIndex, boolean agentTurn, List<ObjectiveWrapper> moves, int alpha, int beta, GameBoardState gameState) {
		if(depth == 2){
			return new MoveWrapper(moves.get(0));
		}

		if(agentTurn){
			int best = MIN;
			MoveWrapper move  = new MoveWrapper(moves.get(0));

			for(int i = 0; i < moves.size(); i++){
				nodesExamined++;
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
				nodesExamined++;
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

	private int minimaxAlphaBetaPrune(int depth, boolean agentTurn, int alpha, int beta, GameBoardState gameState, MoveWrapper move) {
		if(gameState.isTerminal() || depth == MAX_DEPTH){
			depthOfSearch = depth;
			return heuristic(gameState, agentTurn);
		}

		if(agentTurn){
			int best = MIN;
			List<ObjectiveWrapper> moves = AgentController.getAvailableMoves(gameState, playerTurn);

			for(int i = 0; i < moves.size(); i++){
				nodesExamined++;
				GameBoardState newState = AgentController.getNewState(gameState, moves.get(i));
				MoveWrapper newMove = new MoveWrapper(moves.get(i));
				
				int value = minimaxAlphaBetaPrune(depth + 1, false, alpha, beta, newState, newMove);

				best = Math.max(best, value);
            	alpha = Math.max(alpha, best);
				//System.out.println("Agent");
				// Alpha Beta Pruning
				if (beta <= alpha){
					//depthOfSearch = depth;
					break;
				}
			}
			return best;
		}
		else{
			int best = MAX;
			List<ObjectiveWrapper> moves = AgentController.getAvailableMoves(gameState, otherPlayerTurn);

			for(int i = 0; i < moves.size(); i++){
				nodesExamined++;
				GameBoardState newState = AgentController.getNewState(gameState, moves.get(i));
				MoveWrapper newMove = new MoveWrapper(moves.get(i));
				
				int value = minimaxAlphaBetaPrune(depth + 1, true, alpha, beta, newState, newMove);

				best = Math.min(best, value);
            	alpha = Math.min(alpha, best);
				//System.out.println("Not Agent");
				// Alpha Beta Pruning
				if (beta <= alpha){
					//depthOfSearch = depth;
					break;
				}
			}
			return best;
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
