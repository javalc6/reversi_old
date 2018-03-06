/*
Reversi

Reversi (Othello) is a game based on a grid with eight rows and eight columns, played between you and the computer, by adding pieces with two sides: black and white.
At the beginning of the game there are 4 pieces in the grid, the player with the black pieces is the first one to place his piece on the board.
Each player must place a piece in a position that there exists at least one straight (horizontal, vertical, or diagonal) line between the new piece and another piece of the same color, with one or more contiguous opposite pieces between them.

Usage:  java Reversi

10-12-2006 version 0.1: initial release
26-12-2006 version 0.15: added support for applet
01-11-2007 version 0.16: minor improvement in level handling
16-12-2017 version 0.2: refactored

Requirement: Java 1.5 or later

future features:
- undo
- save/load board on file, logging of moves
- autoplay
- sound

This software is released under the GNU GENERAL PUBLIC LICENSE, see attached file gpl.txt
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;

public class Reversi extends JFrame implements ActionListener {
  private static final long serialVersionUID = 1L;
  JEditorPane editorPane;

  static final String WindowTitle = "Reversi";
  static final String ABOUTMSG = WindowTitle + "\n\n26-12-2006\njavalc6";

  static GPanel gpanel;
  static JMenuItem hint;
  static boolean helpActive = false;

  static final int Square_L = 33; // length in pixel of a square in the grid
  static final int Width = 8 * Square_L; // Width of the game board
  static final int Height = 8 * Square_L; // Width of the game board

  ReversiBoard board;
  static JLabel score_black, score_white;
  JMenu level, theme;

  public Reversi() {
    super(WindowTitle);

    score_black = new JLabel("2"); // the game start with 2 black pieces
    score_black.setForeground(Color.blue);
    score_black.setFont(new Font("Dialog", Font.BOLD, 16));
    score_white = new JLabel("2"); // the game start with 2 white pieces
    score_white.setForeground(Color.red);
    score_white.setFont(new Font("Dialog", Font.BOLD, 16));
    board = new ReversiBoard();
    gpanel = new GPanel(board, score_black, score_white, "Electric", 3);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setupMenuBar();
    gpanel.setMinimumSize(new Dimension(Reversi.Width, Reversi.Height));

    JPanel status = new JPanel();
    status.setLayout(new BorderLayout());
    status.add(score_black, BorderLayout.WEST);
    status.add(score_white, BorderLayout.EAST);
    //		status.setMinimumSize(new Dimension(100, 30));
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, gpanel, status);
    splitPane.setOneTouchExpandable(false);
    getContentPane().add(splitPane);

    pack();
    setVisible(true);
    setResizable(false);
  }

  // voci del menu di primo livello
  // File Edit Help
  //
  void setupMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(buildGameMenu());
    menuBar.add(buildHelpMenu());
    setJMenuBar(menuBar);
  }

  public void actionPerformed(ActionEvent e) {
    JMenuItem source = (JMenuItem) (e.getSource());
    String action = source.getText();
    if (action.equals("Classic")) gpanel.setTheme(action);
    else if (action.equals("Electric")) gpanel.setTheme(action);
    else if (action.equals("Flat")) gpanel.setTheme(action);
  }

  protected JMenu buildGameMenu() {
    JMenu game = new JMenu("Game");
    JMenuItem newWin = new JMenuItem("New");
    level = new JMenu("Level");
    theme = new JMenu("Theme");
    JMenuItem undo = new JMenuItem("Undo");
    hint = new JMenuItem("Hint");
    undo.setEnabled(false);
    JMenuItem quit = new JMenuItem("Quit");

    // build level sub-menu
    ActionListener newLevel =
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            JMenuItem source = (JMenuItem) (e.getSource());
            gpanel.setLevel(Integer.parseInt(source.getText()));
          }
        };
    ButtonGroup group = new ButtonGroup();
    JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem("2");
    group.add(rbMenuItem);
    level.add(rbMenuItem).addActionListener(newLevel);
    rbMenuItem = new JRadioButtonMenuItem("3", true);
    group.add(rbMenuItem);
    level.add(rbMenuItem).addActionListener(newLevel);
    rbMenuItem = new JRadioButtonMenuItem("4");
    group.add(rbMenuItem);
    level.add(rbMenuItem).addActionListener(newLevel);
    rbMenuItem = new JRadioButtonMenuItem("5");
    group.add(rbMenuItem);
    level.add(rbMenuItem).addActionListener(newLevel);
    rbMenuItem = new JRadioButtonMenuItem("6");
    group.add(rbMenuItem);
    level.add(rbMenuItem).addActionListener(newLevel);

    // build theme sub-menu
    group = new ButtonGroup();
    rbMenuItem = new JRadioButtonMenuItem("Classic");
    group.add(rbMenuItem);
    theme.add(rbMenuItem);
    rbMenuItem.addActionListener(this);
    rbMenuItem = new JRadioButtonMenuItem("Electric", true);
    group.add(rbMenuItem);
    theme.add(rbMenuItem);
    rbMenuItem.addActionListener(this);
    rbMenuItem = new JRadioButtonMenuItem("Flat");
    group.add(rbMenuItem);
    theme.add(rbMenuItem);
    rbMenuItem.addActionListener(this);

    // Begin "New"
    newWin.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            gpanel.clear();
            hint.setEnabled(true);
            repaint();
          }
        });
    // End "New"

    // Begin "Quit"
    quit.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            System.exit(0);
          }
        });
    // End "Quit"

    // Begin "Hint"
    hint.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {

            if (gpanel.active) {
              Move move = new Move();
              if (board.findMove(TKind.black, gpanel.gameLevel, move, board)) gpanel.setHint(move);
              repaint();
              /*					if (board.move(move,TKind.black) != 0) {
              						score_black.setText(Integer.toString(board.getCounter(TKind.black)));
              						score_white.setText(Integer.toString(board.getCounter(TKind.white)));
              						repaint();
              						javax.swing.SwingUtilities.invokeLater(new Runnable() {
              							public void run() {
              								Cursor savedCursor = getCursor();
              								setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
              								gpanel.computerMove();
              								setCursor(savedCursor);
              							}
              						});
              					}
              */
            } else hint.setEnabled(false);
          }
        });
    // End "Hint"

    game.add(newWin);
    game.addSeparator();
    game.add(undo);
    game.add(hint);
    game.addSeparator();
    game.add(level);
    game.add(theme);
    game.addSeparator();
    game.add(quit);
    return game;
  }

  protected JMenu buildHelpMenu() {
    JMenu help = new JMenu("Help");
    JMenuItem about = new JMenuItem("About " + WindowTitle + "...");
    JMenuItem openHelp = new JMenuItem("Help Topics...");

    // Begin "Help"
    openHelp.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            createEditorPane();
          }
        });
    // End "Help"

    // Begin "About"
    about.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            ImageIcon icon = new ImageIcon(Reversi.class.getResource("images/reversi.jpg"));
            JOptionPane.showMessageDialog(
                null, ABOUTMSG, "About " + WindowTitle, JOptionPane.PLAIN_MESSAGE, icon);
          }
        });
    // End "About"

    help.add(openHelp);
    help.add(about);

    return help;
  }

  protected void createEditorPane() {
    if (helpActive) return;
    editorPane = new JEditorPane();
    editorPane.setEditable(false);
    editorPane.addHyperlinkListener(
        new HyperlinkListener() {
          public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
              if (e instanceof HTMLFrameHyperlinkEvent) {
                ((HTMLDocument) editorPane.getDocument())
                    .processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent) e);
              } else {
                try {
                  editorPane.setPage(e.getURL());
                } catch (java.io.IOException ioe) {
                  System.out.println("IOE: " + ioe);
                }
              }
            }
          }
        });
    java.net.URL helpURL = Reversi.class.getResource("HelpFile.html");
    if (helpURL != null) {
      try {
        editorPane.setPage(helpURL);
        new HelpWindow(editorPane);
      } catch (java.io.IOException e) {
        System.err.println("Attempted to read a bad URL: " + helpURL);
      }
    } else {
      System.err.println("Couldn't find file: HelpFile.html");
    }
  }

  public class HelpWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    public HelpWindow(JEditorPane editorPane) {
      super("Help Window");
      addWindowListener(
          new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
              Reversi.helpActive = false;
              setVisible(false);
            }
          });

      JScrollPane editorScrollPane = new JScrollPane(editorPane);
      editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      getContentPane().add(editorScrollPane);
      setSize(600, 400);
      setVisible(true);
      helpActive = true;
    }
  }

  public HyperlinkListener createHyperLinkListener1() {
    return new HyperlinkListener() {
      public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
          if (e instanceof HTMLFrameHyperlinkEvent) {
            ((HTMLDocument) editorPane.getDocument())
                .processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent) e);
          } else {
            try {
              editorPane.setPage(e.getURL());
            } catch (java.io.IOException ioe) {
              System.out.println("IOE: " + ioe);
            }
          }
        }
      }
    };
  }

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } catch (Exception e) {
    }
    Reversi app = new Reversi();
  }
}
