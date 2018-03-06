
//16-12-2017 version 0.2: refactored

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;

public class GPanel extends JPanel implements MouseListener {
  private static final long serialVersionUID = 1L;
  ReversiBoard board;
  int gameLevel;
  ImageIcon button_black, button_white;
  JLabel score_black, score_white;
  String gameTheme;
  Move hint = null;
  boolean inputEnabled, active;

  public GPanel(
      ReversiBoard board, JLabel score_black, JLabel score_white, String theme, int level) {
    super();
    this.board = board;
    this.score_black = score_black;
    this.score_white = score_white;
    gameLevel = level;
    setTheme(theme);
    addMouseListener(this);
    inputEnabled = true;
    active = true;
  }

  public void setTheme(String gameTheme) {
    hint = null;
    this.gameTheme = gameTheme;
    if (gameTheme.equals("Classic")) {
      button_black = new ImageIcon(Reversi.class.getResource("images/button_black.jpg"));
      button_white = new ImageIcon(Reversi.class.getResource("images/button_white.jpg"));
      setBackground(Color.green);
    } else if (gameTheme.equals("Electric")) {
      button_black = new ImageIcon(Reversi.class.getResource("images/button_blu.jpg"));
      button_white = new ImageIcon(Reversi.class.getResource("images/button_red.jpg"));
      setBackground(Color.white);
    } else {
      gameTheme = "Flat"; // default theme "Flat"
      setBackground(Color.green);
    }
    repaint();
  }

  public void setLevel(int level) {
    if ((level > 1) && (level < 7)) gameLevel = level;
  }

  public void drawPanel(Graphics g) {
    //	    int currentWidth = getWidth();
    //		int currentHeight = getHeight();
    for (int i = 1; i < 8; i++) {
      g.drawLine(i * Reversi.Square_L, 0, i * Reversi.Square_L, Reversi.Height);
    }
    g.drawLine(Reversi.Width, 0, Reversi.Width, Reversi.Height);
    for (int i = 1; i < 8; i++) {
      g.drawLine(0, i * Reversi.Square_L, Reversi.Width, i * Reversi.Square_L);
    }
    g.drawLine(0, Reversi.Height, Reversi.Width, Reversi.Height);
    for (int i = 0; i < 8; i++)
      for (int j = 0; j < 8; j++)
        switch (board.get(i, j)) {
          case white:
            if (gameTheme.equals("Flat")) {
              g.setColor(Color.white);
              g.fillOval(
                  1 + i * Reversi.Square_L,
                  1 + j * Reversi.Square_L,
                  Reversi.Square_L - 1,
                  Reversi.Square_L - 1);
            } else
              g.drawImage(
                  button_white.getImage(),
                  1 + i * Reversi.Square_L,
                  1 + j * Reversi.Square_L,
                  this);
            break;
          case black:
            if (gameTheme.equals("Flat")) {
              g.setColor(Color.black);
              g.fillOval(
                  1 + i * Reversi.Square_L,
                  1 + j * Reversi.Square_L,
                  Reversi.Square_L - 1,
                  Reversi.Square_L - 1);
            } else
              g.drawImage(
                  button_black.getImage(),
                  1 + i * Reversi.Square_L,
                  1 + j * Reversi.Square_L,
                  this);
            break;
        }
    if (hint != null) {
      g.setColor(Color.darkGray);
      g.drawOval(
          hint.i * Reversi.Square_L + 3,
          hint.j * Reversi.Square_L + 3,
          Reversi.Square_L - 6,
          Reversi.Square_L - 6);
    }
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    drawPanel(g);
  }

  public Dimension getPreferredSize() {
    return new Dimension(Reversi.Width, Reversi.Height);
  }

  public void showWinner() {
    inputEnabled = false;
    active = false;
    if (board.counter[0] > board.counter[1])
      JOptionPane.showMessageDialog(this, "You win!", "Reversi", JOptionPane.INFORMATION_MESSAGE);
    else if (board.counter[0] < board.counter[1])
      JOptionPane.showMessageDialog(this, "I win!", "Reversi", JOptionPane.INFORMATION_MESSAGE);
    else JOptionPane.showMessageDialog(this, "Drawn!", "Reversi", JOptionPane.INFORMATION_MESSAGE);
  }

  public void setHint(Move hint) {
    this.hint = hint;
  }

  public void clear() {
    board.clear();
    score_black.setText(Integer.toString(board.getCounter(TKind.black)));
    score_white.setText(Integer.toString(board.getCounter(TKind.white)));
    inputEnabled = true;
    active = true;
  }

  public void computerMove() {
    if (board.gameEnd()) {
      showWinner();
      return;
    }
    Move move = new Move();
    if (board.findMove(TKind.white, gameLevel, move, board)) {
      board.move(move.i, move.j, TKind.white);
      score_black.setText(Integer.toString(board.getCounter(TKind.black)));
      score_white.setText(Integer.toString(board.getCounter(TKind.white)));
      repaint();
      if (board.gameEnd()) showWinner();
      else if (!board.userCanMove(TKind.black)) {
        JOptionPane.showMessageDialog(
            this, "You pass...", "Reversi", JOptionPane.INFORMATION_MESSAGE);
        javax.swing.SwingUtilities.invokeLater(
            new Runnable() {
              public void run() {
                computerMove();
              }
            });
      }
    } else if (board.userCanMove(TKind.black))
      JOptionPane.showMessageDialog(this, "I pass...", "Reversi", JOptionPane.INFORMATION_MESSAGE);
    else showWinner();
  }

  public void mouseClicked(MouseEvent e) {
    // generato quando il mouse viene premuto e subito rilasciato (click)

    if (inputEnabled) {
      hint = null;
      int i = e.getX() / Reversi.Square_L;
      int j = e.getY() / Reversi.Square_L;
      if ((i < 8)
          && (j < 8)
          && (board.get(i, j) == TKind.nil)
          && (board.move(i, j, TKind.black) != 0)) {
        score_black.setText(Integer.toString(board.getCounter(TKind.black)));
        score_white.setText(Integer.toString(board.getCounter(TKind.white)));
        repaint();
        javax.swing.SwingUtilities.invokeLater(
            new Runnable() {
              public void run() {
                Cursor savedCursor = getCursor();
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                computerMove();
                setCursor(savedCursor);
              }
            });
      } else
        JOptionPane.showMessageDialog(this, "Illegal move", "Reversi", JOptionPane.ERROR_MESSAGE);
    }
  }

  public void mouseEntered(MouseEvent e) {
    // generato quando il mouse entra nella finestra
  }

  public void mouseExited(MouseEvent e) {
    // generato quando il mouse esce dalla finestra
  }

  public void mousePressed(MouseEvent e) {
    // generato nell'istante in cui il mouse viene premuto
  }

  public void mouseReleased(MouseEvent e) {
    // generato quando il mouse viene rilasciato, anche a seguito di click
  }
}
