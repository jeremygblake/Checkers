import com.sun.tools.javac.comp.Check;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CheckersApp {

	public static char PLAYER1 = 'X';
	public static char PLAYER2 = 'O';
	public static boolean DEBUG = false;
	
	private static Board board;
	private static Player player1;
	private static Player player2;
	private static Player currPlayer;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int p1 = 0;
		int p2 = 0;
		
		for (int i = 1; i < 4 ; i++) {
			for (int j = 1; j < 4; j++) {
				for (int k = 1; k < 4; k++) {
					for (int l = 1; l < 4; l++) {
						for (int m = 1; m < 4; m++) {
							for (int n = 1; n < 4; n++) {
								for (int o = 1; o < 4; o++) {
									board = new Board();
									// Weights: pawns, kings, back row, mid box, mid rows, vulnerable, protected
									double[] player1Weights = {3*i, 3*j, 3*k, 3*l, 3*m, -3*n, 3*o};
									double[] player2Weights = {4, 8, 1, 2, 1.5, -3, 3};
									
									player1 = new Player(true, PLAYER1, player1Weights);
									player2 = new Player(true, PLAYER2, player2Weights);
									currPlayer = player1;
									initBoard(board);
									
						//			board.printBoard();
							
									playGame();
									if (board.getWinner() == player1) {
										p1++;
									}
									else {
										p2++;
									}
									
									board = new Board();
									player1 = new Player(true, PLAYER1, player2Weights);
									player2 = new Player(true, PLAYER2, player1Weights);
									currPlayer = player1;
									initBoard(board);
									
						//			board.printBoard();
							
									playGame();
									if (board.getWinner() == player1) {
										p2++;
									}
									else {
										p1++;
									}
									
									System.out.println("P1: " + p1 + ", P2: " + p2);
								}
							}
						}
					}
				}
			}
		}
		
		


	}
	
	public static void initBoard(Board board) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4; j++) {
				Checker toAdd = new Checker(player1);
				board.addChecker(toAdd, 7-i, 2*j + (i % 2));
				player1.checkers.add(toAdd);
				
				Checker toAdd2 = new Checker(player2);
				board.addChecker(toAdd2, i, 2*j + ((7-i) % 2));
				player2.checkers.add(toAdd2);
			}
		}
	}


	public static void playGame() {

		Scanner scanner = new Scanner(System.in);

		while (!board.isFinished()) {

//			System.out.println("Heuristic Val of Board = " + board.getHeuristicVal());
			if (DEBUG) {
				double heur = currPlayer.calcHeuristic(board);
				System.out.println("Heuristc value: " + heur);
			}

			if (currPlayer == player1) {
				if (!currPlayer.isAI() || DEBUG) {
					System.out.println("Player1's Turn\n");
				}
			} else {
				if (!currPlayer.isAI() || DEBUG) {
					System.out.println("Player2's Turn\n");
				}
			}


			
			String[] infoStringFrom = null;
			if (!currPlayer.isAI()) {
				System.out.println("(r, c) to move from: ");
				String lineFrom = scanner.nextLine();
				infoStringFrom = lineFrom.split("\\s+");
				if (infoStringFrom[0].length() == 2) {
					String str = infoStringFrom[0];
					infoStringFrom = new String[2];
					infoStringFrom[0] = str.substring(0, 1);
					infoStringFrom[1] = str.substring(1);
				}
			}

			if (currPlayer.isAI() || infoStringFrom.length == 2) {
				
				Coordinates coordTo = null;
				Coordinates coordFrom = null;
				
				if (currPlayer.isAI()) {

					miniMax(currPlayer, coordTo, coordFrom, 2);

				}
				else {
					char rowChar = infoStringFrom[0].charAt(0);
					int rowNum = Character.toUpperCase(rowChar) - 'A';
	
					coordFrom = new Coordinates(rowNum, Integer.parseInt(infoStringFrom[1]) - 1); // create coordinate for with the input r,c
	
					int indexOfChecker = 0;
					int i = 0;
					for (Checker checker : currPlayer.checkers) {
						System.out.println(checker.loc.row + " " + checker.loc.col);
						if (checker.loc.row == coordFrom.row && checker.loc.col == coordFrom.col) {
							indexOfChecker = i;
						}
						i += 1;
					}
					System.out.println("Valid moves: ");
					Checker pieceToMove = currPlayer.checkers.get(indexOfChecker);
					board.getValidMoves(pieceToMove);
					System.out.println("Piece at: " + (char) ((char) 'A' + pieceToMove.loc.row) + (pieceToMove.loc.col + 1));
					for (Coordinates move : pieceToMove.possibleMoves) {
						char r = (char) ((char) 'A' + move.row);
						System.out.println("(" + r + ", " + (move.col + 1) + ")");
					}
					
					System.out.println("(r, c) to move to: ");
	
					String lineTo = scanner.nextLine();
					String[] infoStringTo = lineTo.split("\\s+");
					if (infoStringTo[0].length() == 2) {
						String str = infoStringTo[0];
						infoStringTo = new String[2];
						infoStringTo[0] = str.substring(0, 1);
						infoStringTo[1] = str.substring(1);
					}
	
					char rowCharTo = infoStringTo[0].charAt(0);
					int rowNumTo = Character.toUpperCase(rowCharTo) - 'A';
					
	
					coordTo = new Coordinates(rowNumTo, Integer.parseInt(infoStringTo[1])-1);
					
					for (Coordinates move : pieceToMove.possibleMoves) {
						if (move.row == coordTo.row && move.col == coordTo.col) {
							board.moveChecker(coordFrom, coordTo, false);
							board.printBoard();
						}
					}
				}

				currPlayer = currPlayer == player1 ? player2 : player1;
			}
			else{
				System.out.println("Quit");
				return;
			}

		}
		
		Player winner = board.getWinner();
		if (winner.equals(player1)) {
			System.out.println("Player 1 wins");
		}
		else {
			System.out.println("Player 2 wins");
		}
	}


	/**
	 * Look for the best possible value (highest heuristic) for the current player
	 * @param coordTo
	 * @param coordFrom
	 */

	public static void miniMax(Player choosingPlayer, Coordinates coordTo, Coordinates coordFrom, int depth){


		Checker toMove = null;
		double total = -Double.MAX_VALUE;
		Checker[][] ogGrid = board.copyGrid();
		for (Checker pieceToMove : choosingPlayer.checkers) {
			Coordinates loc = pieceToMove.loc;
			if (board.grid[loc.row][loc.col] == null) continue;
			board.getValidMoves(pieceToMove);

			Coordinates coordToSoFar = null;
			Coordinates coordFromSoFar = null;
			Checker[][] oldGrid = board.copyGrid();
			Map<Coordinates, Double> hvals = new HashMap<Coordinates, Double>();
			
			if (DEBUG) {
				System.out.println("\nHeuristic values: ");
				System.out.println("Piece at: " + (char) ((char) 'A' + loc.row) + (loc.col + 1));
			}
			
			for (Coordinates moves : pieceToMove.possibleMoves) {
				Checker[][] copyGrid = board.copyGrid();
				char r = (char) ((char) 'A' + moves.row);
				if (DEBUG) {
					System.out.println("(" + r + ", " + (moves.col + 1) + ")");
					board.printBoard();
				}
				
				board.moveChecker(loc, moves, true);

				double heuristic;
				//if looking further call recursive function
				if (depth > 1){
					if (currPlayer == player1){
						heuristic = maxiMinHVal(player2, board, depth-1);
					} else {
						heuristic = maxiMinHVal(player1, board, depth-1);
					}
				}
				//if at deepest depth then just get current heuristic of board
				else {
					heuristic = currPlayer.calcHeuristic(board);
				}

				hvals.put(moves, heuristic);
				if (DEBUG) {
					System.out.println("(" + r + ", " + (moves.col + 1) + "): " + heuristic);
				}
				
				board.grid = copyGrid;
			}
			board.grid = oldGrid;
			pieceToMove = board.grid[loc.row][loc.col];

			//gets best move for a checker
			double maxVal = -Double.MAX_VALUE;
			for (Coordinates c : hvals.keySet()) {
				if (hvals.get(c) >= maxVal) {
					maxVal = hvals.get(c);
					coordToSoFar = c;
					coordFromSoFar = pieceToMove.loc;
				}
			}

			//Gets best checker
			if (maxVal > total) {
				toMove = pieceToMove;
				total = maxVal;
				coordTo = coordToSoFar;
				coordFrom = coordFromSoFar;
			}
		}
		board.grid = ogGrid;
		toMove = (toMove != null) ? ogGrid[toMove.loc.row][toMove.loc.col] : null;
		if (toMove != null) {
			for (Coordinates move : toMove.possibleMoves) {
				if (move.row == coordTo.row && move.col == coordTo.col) {
					if (DEBUG) {
						System.out.println(coordFrom.row + " " + coordFrom.col);
						System.out.println(move.row + " " + move.col);
					}
					
					board.moveChecker(coordFrom, coordTo, false);
					
					if (DEBUG) {
						board.printBoard();
					}
				}
			}
		}
		else {
			if (currPlayer == player1) {
				board.manuallySetWinner(player2);
			}
			else {
				board.manuallySetWinner(player1);
			}
		}


		
		

	}

	public static double miniMaxHVal(Player choosingPlayer, Board board, int depth){
		Checker toMove = null;
		double total = -Double.MAX_VALUE;
		Checker[][] ogGrid = board.copyGrid();
		for (Checker pieceToMove : choosingPlayer.checkers) {
			Coordinates loc = pieceToMove.loc;
			if (board.grid[loc.row][loc.col] == null) continue;
			board.getValidMoves(pieceToMove);

			Coordinates coordToSoFar = null;
			Coordinates coordFromSoFar = null;
			Checker[][] oldGrid = board.copyGrid();
			Map<Coordinates, Double> hvals = new HashMap<Coordinates, Double>();
			if (DEBUG) {
				System.out.println("\nHeuristic values: ");
				System.out.println("Piece at: " + (char) ((char) 'A' + loc.row) + (loc.col + 1));
			}
			for (Coordinates moves : pieceToMove.possibleMoves) {
				Checker[][] copyGrid = board.copyGrid();
				board.printBoard();
				board.moveChecker(loc, moves, true);
				//double heuristic = choosingPlayer.calcHeuristic(board);

				double heuristic;
				//if looking further call recursive function
				if (depth > 1){
					if (currPlayer == player1){
						heuristic = maxiMinHVal(player2, board, depth-1);
					} else {
						heuristic = maxiMinHVal(player1, board, depth-1);
					}
				}
				//if at deepest depth then just get current heursitic of board
				else {
					heuristic = currPlayer.calcHeuristic(board);
				}


				hvals.put(moves, heuristic);
				char r = (char) ((char) 'A' + moves.row);
				System.out.println("(" + r + ", " + (moves.col + 1) + "): " + heuristic);
				board.grid = copyGrid;
			}
			board.grid = oldGrid;
			pieceToMove = board.grid[loc.row][loc.col];


			double minVal = -Double.MAX_VALUE;
			for (Coordinates c : hvals.keySet()) {
				if (hvals.get(c) >= minVal) {
					minVal = hvals.get(c);
					coordToSoFar = c;
					coordFromSoFar = pieceToMove.loc;
				}
			}


			if (minVal > total) {
				toMove = pieceToMove;
				total = minVal;
				//coordTo = coordToSoFar;
				//coordFrom = coordFromSoFar;
			}
		}

		board.grid = ogGrid;
		toMove = (toMove != null) ? ogGrid[toMove.loc.row][toMove.loc.col] : null;

		return total;
	}




	public static double maxiMinHVal(Player choosingPlayer, Board board, int depth){


		Checker toMove = null;
		double total = +Double.MAX_VALUE;
		Checker[][] ogGrid = board.copyGrid();
		for (Checker pieceToMove : choosingPlayer.checkers) {
			Coordinates loc = pieceToMove.loc;
			if (board.grid[loc.row][loc.col] == null) continue;
			board.getValidMoves(pieceToMove);

			Coordinates coordToSoFar = null;
			Coordinates coordFromSoFar = null;
			Checker[][] oldGrid = board.copyGrid();
			Map<Coordinates, Double> hvals = new HashMap<Coordinates, Double>();
			if (DEBUG) {
				System.out.println("\nHeuristic values: ");
				System.out.println("Piece at: " + (char) ((char) 'A' + loc.row) + (loc.col + 1));
			}
			for (Coordinates moves : pieceToMove.possibleMoves) {
				Checker[][] copyGrid = board.copyGrid();
				if (DEBUG) {
					board.printBoard();
					System.out.println(loc.row + " " + loc.col);
				}
				board.moveChecker(loc, moves, true);
				//double heuristic = choosingPlayer.calcHeuristic(board);

				double heuristic;
				//if looking further call recursive function
				if (depth > 1){
					if (currPlayer == player1){
						heuristic = miniMaxHVal(player2, board, depth-1);
					} else {
						heuristic = miniMaxHVal(player1, board, depth-1);
					}
				}
				//if at deepest depth then just get current heursitic of board
				else {
					heuristic = currPlayer.calcHeuristic(board);
				}


				hvals.put(moves, heuristic);
				char r = (char) ((char) 'A' + moves.row);
				if (DEBUG) {
					System.out.println("(" + r + ", " + (moves.col + 1) + "): " + heuristic);
				}
				board.grid = copyGrid;
			}
			board.grid = oldGrid;
			pieceToMove = board.grid[loc.row][loc.col];


			double minVal = +Double.MAX_VALUE;
			for (Coordinates c : hvals.keySet()) {
				if (hvals.get(c) <= minVal) {
					minVal = hvals.get(c);
					coordToSoFar = c;
					coordFromSoFar = pieceToMove.loc;
				}
			}


			if (minVal < total) {
				toMove = pieceToMove;
				total = minVal;
				//coordTo = coordToSoFar;
				//coordFrom = coordFromSoFar;
			}
		}

		board.grid = ogGrid;
		toMove = (toMove != null) ? ogGrid[toMove.loc.row][toMove.loc.col] : null;

		return total;
	}

}
