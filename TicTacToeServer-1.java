
import java.net.*;
import java.io.*;

public class TicTacToeServer {
public static void main(String[] args) throws Exception {
ServerSocket serverSocket = new ServerSocket(8000);
System.out.println("Server started");


    // Wait for player 1 to connect
    Socket player1 = serverSocket.accept();
    System.out.println("Player 1 connected");

    // Let player 1 know they are X
    PrintWriter out = new PrintWriter(player1.getOutputStream(), true);
    out.println("X");
    

    // Wait for player 2 to connect
    Socket player2 = serverSocket.accept();
    System.out.println("Player 2 connected");

    // Let player 2 know they are O
    out = new PrintWriter(player2.getOutputStream(), true);
    out.println("O");

    

    // Game loop

  String [][] board = new String [3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = "-";
            }
        }
String currentPlayer = "X";
while (true) {
    // Get the input and output streams for the current player
    Socket currentPlayerSocket = currentPlayer.equals("X") ? player1 : player2;
    Socket otherPlayerSocket = currentPlayer.equals("X") ? player2 : player1;
    //out = new PrintWriter(currentPlayerSocket.getOutputStream(), true);
    
    PrintWriter currPlayer=new PrintWriter(currentPlayerSocket.getOutputStream(), true);
    // Let the player know it's their turn
    currPlayer.println("YOUR_MOVE");
   
    PrintWriter otherPlayer = new PrintWriter(otherPlayerSocket.getOutputStream(), true);
    otherPlayer.println("MESSAGE Waiting for opponent's move");
    
    // Get the player's move
    BufferedReader in = new BufferedReader(new InputStreamReader(currentPlayerSocket.getInputStream()));
    String move = in.readLine();
    
    
     
    String[] moveParts = move.split(" ");
    int row = Integer.parseInt(moveParts[0]);
    int col = Integer.parseInt(moveParts[1]);
    if(row>2 || col>2 || board[row][col]!="-")
    {
        currPlayer.println("INVALID_MOVE");
        otherPlayer.println("INVALID_MOVE");
        continue;
    }
    else
    {
        currPlayer.println("SUCCESSFUL_MOVE");
        otherPlayer.println("UPDATING BOARD");
    }
    // Update the board
    board[row][col] = currentPlayer;

    // Print the updated board
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
            currPlayer.print(board[i][j] + " ");
            otherPlayer.print(board[i][j] + " ");
        }
        currPlayer.println();
        otherPlayer.println();
    }

    // Check if the current player has won
    if (hasWon(board, currentPlayer)) {
        currPlayer.println("VICTORY");
        otherPlayer.println("DEFEAT");
        break;
    }

    // Check if there is a tie
    if (isTie(board)) {
        currPlayer.println("TIE");
        otherPlayer.println("TIE");
        break;
    }

    // Switch to the other player
    currentPlayer = currentPlayer.equals("X") ? "O" : "X";
}

}


//Check if player has won the game
public static boolean hasWon(String[][] board, String player) {
    // Check rows
    for (int i = 0; i < 3; i++) {
        if (board[i][0].equals(player) && board[i][1].equals(player) && board[i][2].equals(player)) {
            return true;
        }
    }

    // Check columns
    for (int i = 0; i < 3; i++) {
        if (board[0][i].equals(player) && board[1][i].equals(player) && board[2][i].equals(player)) {
            return true;
        }
    }
// Check diagonals
if (board[0][0].equals(player) && board[1][1].equals(player) && board[2][2].equals(player)) {
    return true;
}
if (board[0][2].equals(player) && board[1][1].equals(player) && board[2][0].equals(player)) {
    return true;
}

// No winner yet
return false;
}
public static boolean isTie(String[][] board) {
    // Check if any positions are still empty
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
            if (board[i][j] == "-") {
                return false;
            } 
        }
    }

    // Check if there is a winner
    return !hasWon(board, "X") && !hasWon(board, "O");
    }
}