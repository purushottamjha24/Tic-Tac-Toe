
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Client {

	static boolean isFrameClicked = false;
	static String move = "";
	
	static String title = "TicTacToe";

	public static void main(String[] args) throws Exception {

		int dimension = 600;

		JFrame frame = new JFrame();
		frame.setTitle(title);
		frame.setPreferredSize(new Dimension(dimension, dimension));
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setLayout(new GridLayout(3, 3));

		//Initialize markers
		JLabel[] markers = new JLabel[9];
		for (int i = 0; i < 9; i++){
			markers[i] = new JLabel();
			markers[i].setFont(new Font("Arial", Font.BOLD, 85));
		}

		for (JLabel marker : markers) {
			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());  //centers the component
			panel.setSize(dimension / 3, dimension / 3);
			panel.setBorder(BorderFactory.createLineBorder(Color.black));

			panel.add(marker);
			frame.add(panel);
		}

		frame.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Point click = e.getPoint();
				int clickI = click.y / (dimension / 3);
				int clickJ = click.x / (dimension / 3);

				isFrameClicked = true;
				move = clickI + " " + clickJ;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		frame.pack();
		frame.setVisible(true);

		Socket socket = new Socket("localhost", 8000);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

		// Get welcome message

		// Determine player's mark (X or O)
		String mark = in.readLine();
		System.out.println("WELCOME " + mark);
		
		title += " | Player : " + mark;

		// Start game loop
		while (true) {
			String line = in.readLine();
			isFrameClicked = false;
			
			frame.setTitle(title);

			// Check for end of game
			if (line.startsWith("VICTORY")) {
				System.out.println("You win!");
				
				JLabel winLabel = new JLabel("You Win!");
				winLabel.setFont(new Font("Arial", Font.BOLD, 35));
				for (JLabel marker : markers) {
					marker.getParent().setVisible(false);
				}
				frame.setLayout(new GridBagLayout());
				frame.add(winLabel);
				
				break;
			} else if (line.startsWith("DEFEAT")) {
				System.out.println("You lose.");
				
				JLabel loseLabel = new JLabel("You Lose!");
				loseLabel.setFont(new Font("Arial", Font.BOLD, 35));
				for (JLabel marker : markers) {
					marker.getParent().setVisible(false);
				}
				frame.setLayout(new GridBagLayout());
				frame.add(loseLabel);
				
				break;
			} else if (line.startsWith("TIE")) {
				System.out.println("It's a tie.");

				JLabel tieLabel = new JLabel(" T I E! ");
				tieLabel.setFont(new Font("Arial", Font.BOLD, 35));
				for (JLabel marker : markers) {
					marker.getParent().setVisible(false);
				}
				frame.setLayout(new GridBagLayout());
				frame.add(tieLabel);
				break;
			} else if (line.startsWith("MESSAGE")) {
				System.out.println(line.substring(8));
				line = in.readLine();
				if (line.equals("INVALID_MOVE")) {
					System.out.println("Invalid move played by opponent.");
				} else {
					System.out.println("UPDATING BOARD");
					printBoard(in, markers);
				}
			} else if (line.startsWith("YOUR_MOVE")) {

				// Get the player's move

				frame.setTitle(title + " | YOUR TURN");

				//waits till user clicks
				while (!isFrameClicked) {
					Thread.sleep(10);
				}

				out.println(move);
				line = in.readLine();

				if (line.equals("INVALID_MOVE")) {
					System.out.println("Invalid move, try again.");
					continue;
				} else
					System.out.println(line);
				// Print the updated board
				printBoard(in, markers);
			} else if (line.equals("INVALID_MOVE")) {
				System.out.println("Invalid move, try again.");
				continue;
			}
		}

		socket.close();
	}

	// Print the Tic Tac Tie board
	public static void printBoard(BufferedReader in, JLabel[] markers) throws IOException {
		String[][] board = new String[3][3];
		for (int i = 0; i < 3; i++) {
			String line = in.readLine();
			String[] row = line.split(" ");
			for (int j = 0; j < 3; j++) {
				board[i][j] = row[j];
				if(!row[j].equals("-"))
					markers[i * 3 + j].setText(row[j] + "");
			}
		}

		System.out.println("  0 1 2");
		for (int i = 0; i < 3; i++) {
			System.out.print(i + " ");
			for (int j = 0; j < 3; j++) {
				System.out.print(board[i][j] + " ");
			}
			System.out.println();
		}
	}
}
