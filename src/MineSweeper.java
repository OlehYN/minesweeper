import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Random;

import javax.swing.*;

public class MineSweeper {

	private final String FILE_NAME = "top.txt";// a name of the file that stores
												// an information about top 10
												// players

	private Timer timer;// counts the amount of elapsed time for the game

	private JFrame frame;// main frame
	private JButton minesQuantity, time;// shows the number of mines left and
										// shows the amount of elapsed time for
										// the game
	private JButton restart;// restarts the game

	private Top top;// stores information about top 10 players

	private Container c;// content pane

	private JButton[] buttons;// shows the field
	private boolean[] field;// contains information about mines position
	private boolean[] checked;// contains information about opened cells

	private MouseClickHandler[] listeners;// buttons listeners

	private boolean newGame;// controls the timer

	private int cellsLeft;// indicates the end of the game

	private final int GRID = 10;// a quantity of cells in the row
	private final int BUTTON_SIZE = 30;// a size of the button
	private final int MINES = 10;// a quantity of mines

	public MineSweeper() {
		frame = new JFrame("Mine Sweeper");
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setSize(330, 430);

		c = frame.getContentPane();
		c.setLayout(new FlowLayout());

		createMenu();

		try {
			top = (Top) SerializationUtil.deserialize(FILE_NAME);
		} catch (Exception e) {
			top = new Top();
		}

		init();

		frame.setVisible(true);
	}

	/**
	 * Adds a menu to the frame
	 */
	private void createMenu() {
		final JMenuBar menuBar = new JMenuBar();

		// create menus
		JMenu gameMenu = new JMenu("Game");
		JMenu helpMenu = new JMenu("Help");

		gameMenu.setMnemonic(KeyEvent.VK_G);
		helpMenu.setMnemonic(KeyEvent.VK_H);

		// create menu items
		JMenuItem reset = new JMenuItem("Reset");
		reset.setMnemonic(KeyEvent.VK_R);
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getContentPane().removeAll();
				frame.getContentPane().repaint();
				frame.getContentPane().revalidate();

				if (timer != null)
					timer.stop();

				init();
			}
		});

		JMenuItem topTen = new JMenuItem("Top ten");
		topTen.setMnemonic(KeyEvent.VK_T);
		topTen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				Object[] options = { "Ok", "Reset" };

				int d = JOptionPane.showOptionDialog(frame, top.toString(),
						"Top players", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, options, options[1]);

				if (d == 1) {
					top = new Top();
					try {
						SerializationUtil.serialize(top, FILE_NAME);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		JMenuItem exit = new JMenuItem("eXit");
		exit.setMnemonic(KeyEvent.VK_X);
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		JMenuItem help = new JMenuItem("heLp");
		help.setMnemonic(KeyEvent.VK_L);

		JMenuItem about = new JMenuItem("About");
		about.setMnemonic(KeyEvent.VK_A);

		// add menu items to menus
		gameMenu.add(reset);
		gameMenu.add(topTen);
		gameMenu.add(exit);

		helpMenu.add(help);
		helpMenu.add(about);

		help.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane
						.showMessageDialog(
								frame,
								"<html><body><p style='width: 200px;'>Minesweeper is a single-player puzzle video game. The objective of the game is to clear a rectangular board containing hidden \"mines\" without detonating any of them, with help from clues about the number of neighboring mines in each field. The game originates from the 1960s, and has been written for many computing platforms in use today. It has many variations and offshoots.</p></body></html>",
								"Help", JOptionPane.PLAIN_MESSAGE);
			}
		});

		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame,
						"Here will be a very important information", "About",
						JOptionPane.PLAIN_MESSAGE);
			}
		});

		// add menu to menubar
		menuBar.add(gameMenu);
		menuBar.add(helpMenu);

		frame.setLocationRelativeTo(null);
		frame.setJMenuBar(menuBar);

	}

	/**
	 * Prepares the field for the first click
	 */
	private void init() {
		newGame = true;
		checked = new boolean[GRID * GRID];

		GridLayout gl1 = new GridLayout(0, 3);// a layout of the c1
		Container c1 = new Container();// a container of the timer, reset button
										// and minesQuantity button
		c1.setLayout(gl1);
		c1.setPreferredSize(new Dimension(300, 50));

		minesQuantity = new JButton(String.valueOf(MINES));
		minesQuantity.setEnabled(false);

		time = new JButton("0");
		time.setEnabled(false);

		restart = new JButton("Reset");
		restart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.getContentPane().removeAll();
				frame.getContentPane().repaint();
				frame.getContentPane().revalidate();

				if (timer != null)
					timer.stop();

				init();
			}
		});

		c1.add(minesQuantity);
		c1.add(restart);
		c1.add(time);

		GridLayout gl2 = new GridLayout(0, GRID);// a layout of the c2
		Container c2 = new Container();// a container of the field buttons
		c2.setLayout(gl2);

		buttons = new JButton[GRID * GRID];
		listeners = new MouseClickHandler[GRID * GRID];

		for (int i = 0; i < buttons.length; i++) {
			listeners[i] = new MouseClickHandler();
			buttons[i] = new JButton("");
			buttons[i]
					.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
			buttons[i].addMouseListener(listeners[i]);
			buttons[i].setMargin(new Insets(0, 0, 0, 0));
			buttons[i].setName(String.valueOf(i));
			c2.add(buttons[i]);
		}

		c.add(c1);
		c.add(c2);
	}

	public static void main(String[] args) {
		new MineSweeper();
	}

	private class MouseClickHandler extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			JButton temp = (JButton) e.getComponent();// component that was
														// clicked

			if (SwingUtilities.isRightMouseButton(e)
					&& !checked[Integer.valueOf(temp.getName())]) {
				String value = temp.getText();

				switch (value) {
				case "":
					temp.setText("M");
					int q = Integer.valueOf(minesQuantity.getText());
					--q;
					minesQuantity.setText(String.valueOf(q));
					break;
				case "M":
					temp.setText("?");
					int p = Integer.valueOf(minesQuantity.getText());
					++p;
					minesQuantity.setText(String.valueOf(p));
					break;
				default:
					temp.setText("");
					break;
				}
			} else if (SwingUtilities.isLeftMouseButton(e)
					&& !checked[Integer.valueOf(temp.getName())]) {
				int id = Integer.valueOf(temp.getName());

				if (newGame)
					prepareField(Integer.valueOf(id));

				if (!temp.getText().equals("?") && !temp.getText().equals("M")) {

					if (!field[id])
						open(id);
					else
						gameOver();
				}
			}

		}

		/**
		 * Performs the cell-opening operation
		 * 
		 * @param id
		 *            Id of the clicked button
		 */
		private void open(int id) {
			--cellsLeft;

			int count = 0;// indicates the quantity of mines near the given cell

			if (id != 0 && id % GRID != 0 && id - GRID > 0
					&& field[id - GRID - 1])
				++count;

			if (id != 0 && id - GRID >= 0 && field[id - GRID])
				++count;

			if (id != 0 && id % GRID != (GRID - 1) && id - GRID > 0
					&& field[id - GRID + 1])
				++count;

			if (id % GRID != 0 && field[id - 1])
				++count;

			if (id % GRID != (GRID - 1) && field[id + 1])
				++count;

			if ((id / GRID) != (GRID - 1)
					&& ((id / GRID) - ((id + GRID - 1)) / GRID) != 0
					&& field[id + GRID - 1])
				++count;

			if ((id / GRID) != (GRID - 1) && field[id + GRID])
				++count;

			if ((id / GRID) != (GRID - 1)
					&& ((id / GRID) - ((id + GRID + 1) / GRID)) != -2
					&& field[id + GRID + 1])
				++count;

			buttons[id].setText(String.valueOf(count));
			buttons[id].setEnabled(false);

			checked[id] = true;

			if (cellsLeft == 0)
				success();
			else {

				if (count == 0) {
					if (id != 0 && id % GRID != 0 && id - GRID > 0
							&& !checked[id - GRID - 1])
						open(id - GRID - 1);

					if (id != 0 && id - GRID >= 0 && !checked[id - GRID])
						open(id - GRID);

					if (id != 0 && id % GRID != (GRID - 1) && id - GRID > 0
							&& !checked[id - GRID + 1])
						open(id - GRID + 1);

					if (id % GRID != 0 && !checked[id - 1])
						open(id - 1);

					if (id % GRID != (GRID - 1) && !checked[id + 1])
						open(id + 1);

					if (id / GRID != (GRID - 1)
							&& ((id / GRID) - ((id + GRID - 1)) / GRID) != 0
							&& !checked[id + GRID - 1])
						open(id + GRID - 1);

					if (id / GRID != (GRID - 1) && !checked[id + GRID])
						open(id + GRID);

					if (id / GRID != (GRID - 1)
							&& ((id / GRID) - ((id + GRID + 1) / GRID)) != -2
							&& !checked[id + GRID + 1])
						open(id + GRID + 1);
				}
			}

		}

		/**
		 * Signalizes about winning and prepares a field for next game
		 */
		private void success() {
			timer.stop();
			for (int i = 0; i < buttons.length; i++) {
				buttons[i].removeMouseListener(listeners[i]);

				if (field[i] && buttons[i].getText().equals("")) {
					buttons[i].setText("B");
					buttons[i].setForeground(Color.RED);
				}
			}

			String s = JOptionPane.showInputDialog(frame,
					"You win! Enter your name:");

			if (s.length() > 15)
				s = s.substring(0, 15);

			if (s != null && !s.equals("")) {
				top.addPerson(new Person(s, Integer.valueOf(time.getText())));
				try {
					SerializationUtil.serialize(top, FILE_NAME);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * Signalizes about losing and prepares a field for next game
		 */
		private void gameOver() {
			timer.stop();
			for (int i = 0; i < buttons.length; i++) {
				buttons[i].removeMouseListener(listeners[i]);

				if (field[i] && buttons[i].getText().equals("")) {
					buttons[i].setText("B");
					buttons[i].setForeground(Color.RED);
				}
			}

			JOptionPane.showMessageDialog(frame, "Unfortunately, you lose.");

		}

		/**
		 * Starts game, prepares a field
		 * 
		 * @param id
		 *            Id where will be no mine
		 */
		private void prepareField(int id) {

			timer = new Timer(1000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int t = Integer.valueOf(time.getText()) + 1;
					time.setText(String.valueOf(t));

				}
			});

			timer.start();

			cellsLeft = (GRID * GRID) - MINES;
			field = new boolean[GRID * GRID];

			int count = MINES;
			Random r = new Random();

			while (count > 0) {
				int b = r.nextInt(GRID * GRID);

				if (b != id && field[b] != true) {
					field[b] = true;
					--count;

				}
			}

			newGame = false;
		}
	}

}