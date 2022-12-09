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
import com.eudycontreras.othello.utilities.TraversalUtility;

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
	
	static int MAX = 10000;
	static int MIN = -10000;
	int MAX_DEPTH = 5;
	int nodesExamined = 0;
	int depthOfSearch = 0;
	PlayerTurn otherPlayerTurn;
	BoardCellState playerCell;
	BoardCellState otherPlayerCell;

	int[][] cellValues = new int[8][8];

	public ExampleAgentA() {
		this(PlayerTurn.PLAYER_ONE);
		// TODO Auto-generated constructor stub
		otherPlayerTurn = PlayerTurn.PLAYER_TWO;
		otherPlayerCell = BoardCellState.BLACK;
		playerCell = BoardCellState.WHITE;
		initCellValues();
	}
	
	public ExampleAgentA(String name) {
		super(name, PlayerTurn.PLAYER_ONE);
		otherPlayerTurn = PlayerTurn.PLAYER_TWO;
		otherPlayerCell = BoardCellState.BLACK;
		playerCell = BoardCellState.WHITE;
		initCellValues();
	}

	public ExampleAgentA(String name, int depth) {
		super(name, PlayerTurn.PLAYER_ONE);
		otherPlayerTurn = PlayerTurn.PLAYER_TWO;
		otherPlayerCell = BoardCellState.BLACK;
		playerCell = BoardCellState.WHITE;
		this.MAX_DEPTH = depth;
		initCellValues();
	}
	
	public ExampleAgentA(PlayerTurn playerTurn) {
		super(playerTurn);
		if(playerTurn == PlayerTurn.PLAYER_ONE){
			otherPlayerTurn = PlayerTurn.PLAYER_TWO;
			otherPlayerCell = BoardCellState.BLACK;
			playerCell = BoardCellState.WHITE;
		}
		else{
			otherPlayerTurn = PlayerTurn.PLAYER_ONE;
			otherPlayerCell = BoardCellState.WHITE;
			playerCell = BoardCellState.BLACK;
		}
		initCellValues();	
	}

	public ExampleAgentA(PlayerTurn playerTurn, int depth) {
		super(playerTurn);
		if(playerTurn == PlayerTurn.PLAYER_ONE){
			otherPlayerTurn = PlayerTurn.PLAYER_TWO;
			otherPlayerCell = BoardCellState.BLACK;
			playerCell = BoardCellState.WHITE;
		}
		else{
			otherPlayerTurn = PlayerTurn.PLAYER_ONE;
			otherPlayerCell = BoardCellState.WHITE;
			playerCell = BoardCellState.BLACK;
		}
		this.MAX_DEPTH = depth;
		initCellValues();	
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
		List<ObjectiveWrapper> moves = AgentController.getAvailableMoves(gameState, playerTurn);
		MoveWrapper bestMove = new MoveWrapper(moves.get(0));
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
		BoardCellState cellState, otherCellState;
		int cellValue = 0;
		int score = 0;

		if(agentTurn){
			List<ObjectiveWrapper> moves = AgentController.getAvailableMoves(state, otherPlayerTurn);
			mobility = moves.size();
			cellState = playerCell;
			otherCellState = otherPlayerCell;
			score = state.getWhiteCount() - state.getBlackCount();
		}
		else{
			List<ObjectiveWrapper> moves = AgentController.getAvailableMoves(state, playerTurn);
			mobility = moves.size();
			cellState = otherPlayerCell;
			otherCellState = playerCell;
			score = state.getBlackCount() - state.getWhiteCount();
		}

		GameBoardCell[][] cells = state.getCells();
		for(int i = 0; i < cells.length; i++){
			for(int j = 0; j < cells[i].length; j++){
				if(cells[i][j].getCellState() == BoardCellState.EMPTY){
					for(DirectionWrapper neighbour : TraversalUtility.getNeighborCells(cells[i][j], otherCellState)){
						potentialMobility+=1;
						//break;
					}
				}	
			}
		}

		for(int i = 0; i < cells.length; i++){
			for(int j = 0; j < cells[i].length; j++){
				if(cells[i][j].getCellState() == cellState){
					cellValue+=cellValues[i][j];
				}
				else if(cells[i][j].getCellState() == otherCellState){
					cellValue-=cellValues[i][j];
				}	
			}
		}
		
		value = cellValue + mobility + potentialMobility + score;

		return value;
	}

	private int minimaxAlphaBetaPrune(int depth, boolean agentTurn, int alpha, int beta, GameBoardState gameState, MoveWrapper move) {
		if(gameState.isTerminal() || depth == MAX_DEPTH){
			depthOfSearch = depth;
			nodesExamined++;
			return heuristic(gameState, agentTurn);
		}

		if(agentTurn){
			int best = MIN;
			List<ObjectiveWrapper> moves = AgentController.getAvailableMoves(gameState, playerTurn);

			for(int i = 0; i < moves.size(); i++){
				//nodesExamined++;
				GameBoardState newState = AgentController.getNewState(gameState, moves.get(i));
				MoveWrapper newMove = new MoveWrapper(moves.get(i));
				
				int value = minimaxAlphaBetaPrune(depth + 1, false, alpha, beta, newState, newMove);

				best = Math.max(best, value);
            	alpha = Math.max(alpha, best);
				//System.out.println("Agent");
				// Alpha Beta Pruning
				if (beta <= alpha){
					break;
				}
			}
			return best;
		}
		else{
			int best = MAX;
			List<ObjectiveWrapper> moves = AgentController.getAvailableMoves(gameState, otherPlayerTurn);

			for(int i = 0; i < moves.size(); i++){
				//nodesExamined++;
				GameBoardState newState = AgentController.getNewState(gameState, moves.get(i));
				MoveWrapper newMove = new MoveWrapper(moves.get(i));
				
				int value = minimaxAlphaBetaPrune(depth + 1, true, alpha, beta, newState, newMove);

				best = Math.min(best, value);
            	beta = Math.min(beta, best);
				//System.out.println("Not Agent");
				// Alpha Beta Pruning
				if (beta <= alpha){
					break;
				}
			}
			return best;
		}
	}

	private void initCellValues(){
		int[][] values = {  {100, -20, 10, 5, 5, 10, -20, 100},
							{-20, -50, -2, -2, -2, -2, -50, -20},
						  	{10, -2, -1, -1, -1, -1, -2, 10},
						  	{5, -2, -1, -1, -1, -1, -2, 5},
						  	{5, -2, -1, -1, -1, -1, -2, 5},
						  	{10, -2, -1, -1, -1, -1, -2, 10},
						  	{-20, -50, -2, -2, -2, -2, -50, -20},
						  	{100, -20, 10, 5, 5, 10, -20, 100}
						};
		cellValues = values;
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
