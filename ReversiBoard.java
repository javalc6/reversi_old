/*

class ReversiBoard implements the board of the game Reversi

used by Reversi.java

10-12-2006 version 0.1: initial release
16-12-2017 version 0.2: refactored

*/

import java.util.Random;

public class ReversiBoard implements Strategy {
  TKind[][] board = new TKind[8][8];
  int[] counter = new int[2]; // 0 = black, 1 = white
  boolean PassCounter;

  public ReversiBoard() {
    clear();
  }

  public TKind get(int i, int j) {
    return board[i][j];
  }

  public void set(Move move, TKind player) {
    switch (board[move.i][move.j]) {
      case white:
        counter[1]--;
        break;
      case black:
        counter[0]--;
        break;
    }
    board[move.i][move.j] = player;
    switch (player) {
      case white:
        counter[1]++;
        break;
      case black:
        counter[0]++;
        break;
    }
  }
  public void set(int x, int y, TKind player) {
    switch (board[x][y]) {
      case white:
        counter[1]--;
        break;
      case black:
        counter[0]--;
        break;
    }
    board[x][y] = player;
    switch (player) {
      case white:
        counter[1]++;
        break;
      case black:
        counter[0]++;
        break;
    }
  }

  public int getCounter(TKind player) {
    return counter[player.ordinal() - 1];
  }

  public void clear() {
    for (int i = 0; i < 8; i++) for (int j = 0; j < 8; j++) board[i][j] = TKind.nil;
    board[3][4] = TKind.black;
    board[4][3] = TKind.black;
    board[3][3] = TKind.white;
    board[4][4] = TKind.white;
    counter[0] = 2;
    counter[1] = 2;
    PassCounter = false;
  }

  public void println() {
    System.out.print("[");
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) System.out.print(board[i][j] + ",");
      System.out.println((i == 7 ? "]" : ""));
    }
  }

  public int move(int x, int y, TKind kind) {
    return checkBoard(x, y, kind);
  }

  public boolean gameEnd() {
    return counter[0] + counter[1] == 64;
  }

  private int Check(int x, int y, int incx, int incy, TKind kind, boolean set) {
    TKind opponent;
    if (kind == TKind.black) opponent = TKind.white;
    else opponent = TKind.black;
    int n_inc = 0;
    x += incx;
    y += incy;
    while ((x < 8) && (x >= 0) && (y < 8) && (y >= 0) && (board[x][y] == opponent)) {
      x += incx;
      y += incy;
      n_inc++;
    }
    if ((n_inc != 0) && (x < 8) && (x >= 0) && (y < 8) && (y >= 0) && (board[x][y] == kind)) {
      if (set)
        for (int j = 1; j <= n_inc; j++) {
          x -= incx;
          y -= incy;
          set(x, y, kind);
        }
      return n_inc;
    } else return 0;
  }

  private int checkBoard(int x, int y, TKind kind) {
    // check increasing x
    int j = Check(x, y, 1, 0, kind, true);
    // check decreasing x
    j += Check(x, y, -1, 0, kind, true);
    // check increasing y
    j += Check(x, y, 0, 1, kind, true);
    // check decreasing y
    j += Check(x, y, 0, -1, kind, true);
    // check diagonals
    j += Check(x, y, 1, 1, kind, true);
    j += Check(x, y, -1, 1, kind, true);
    j += Check(x, y, 1, -1, kind, true);
    j += Check(x, y, -1, -1, kind, true);
    if (j != 0) set(x, y, kind);
    return j;
  }

  private boolean isValid(int x, int y, TKind kind) {
    // check increasing x
    if (Check(x, y, 1, 0, kind, false) != 0) return true;
    // check decreasing x
    if (Check(x, y, -1, 0, kind, false) != 0) return true;
    // check increasing y
    if (Check(x, y, 0, 1, kind, false) != 0) return true;
    // check decreasing y
    if (Check(x, y, 0, -1, kind, false) != 0) return true;
    // check diagonals
    if (Check(x, y, 1, 1, kind, false) != 0) return true;
    if (Check(x, y, -1, 1, kind, false) != 0) return true;
    if (Check(x, y, 1, -1, kind, false) != 0) return true;
    return Check(x, y, -1, -1, kind, false) != 0;
  }

  private class resultFindMax {
    int max, nb, nw;
  };

  private resultFindMax findMax(int level, TKind me, TKind opponent, Strategy mStrategy) {
    int min, score, tnb, tnw;
    TKind[][] TempBoard = new TKind[8][8];
    int[] TempCounter = new int[2];
    resultFindMax res = new resultFindMax();
    level--;
    res.nb = counter[0];
    res.nw = counter[1];
    for (int i = 0; i < 8; i++) System.arraycopy(board[i], 0, TempBoard[i], 0, 8);
    System.arraycopy(counter, 0, TempCounter, 0, 2);
    min = 10000; // high value

    for (int i = 0; i < 8; i++)
      for (int j = 0; j < 8; j++)
        if ((board[i][j] == TKind.nil) && (checkBoard(i, j, me) != 0)) {
          if (level != 0) {
            resultFindMax tres = findMax(level, opponent, me, mStrategy);
            tnb = tres.nb;
            tnw = tres.nw;
            score = tres.max;
          } else {
            tnb = counter[0];
            tnw = counter[1];
            score =
                counter[opponent.ordinal() - 1]
                    - counter[me.ordinal() - 1]
                    + mStrategy.strategy(me, opponent, board);
          }
          if (min > score) {
            min = score;
            res.nb = tnb;
            res.nw = tnw;
          }
          for (int k = 0; k < 8; k++) System.arraycopy(TempBoard[k], 0, board[k], 0, 8);
          System.arraycopy(TempCounter, 0, counter, 0, 2);
        }
    res.max = -min;
    return res;
  }

  public boolean findMove(TKind player, int llevel, Move move, Strategy mStrategy) {
    TKind[][] TempBoard = new TKind[8][8];
    int[] TempCounter = new int[2];
    int nb, nw, min, n_min;
    boolean found;
    resultFindMax res = new resultFindMax();
    Random random = new Random();

    if (counter[0] + counter[1] >= 52 + llevel) {
      llevel = counter[0] + counter[1] - 52;
      if (llevel > 5) llevel = 5;
    }

    for (int i = 0; i < 8; i++) System.arraycopy(board[i], 0, TempBoard[i], 0, 8);
    System.arraycopy(counter, 0, TempCounter, 0, 2);
    found = false;
    min = 10000; // high value
    n_min = 1;
    for (int i = 0; i < 8; i++)
      for (int j = 0; j < 8; j++)
        if ((board[i][j] == TKind.nil) && (checkBoard(i, j, player) != 0)) {
          if (player == TKind.black) res = findMax(llevel - 1, TKind.white, player, mStrategy);
          else res = findMax(llevel - 1, TKind.black, player, mStrategy);
          if ((!found) || (min > res.max)) {
            min = res.max;
            nw = res.nw;
            nb = res.nb;
            move.i = i;
            move.j = j;
            found = true;
          } else if (min == res.max) { // RANDOM MOVE GENERATOR
            n_min++;
            if (random.nextInt(n_min) == 0) {
              nw = res.nw;
              nb = res.nb;
              move.i = i;
              move.j = j;
            }
          }
          //             if found
          //             then PreView(nw,nb);
          for (int k = 0; k < 8; k++) System.arraycopy(TempBoard[k], 0, board[k], 0, 8);
          System.arraycopy(TempCounter, 0, counter, 0, 2);
        }
    return found;
  }

  public boolean userCanMove(TKind player) {
    for (int i = 0; i < 8; i++)
      for (int j = 0; j < 8; j++)
        if ((board[i][j] == TKind.nil) && isValid(i, j, player)) return true;
    return false;
  }
/* normal strategy
  public int strategy(TKind me, TKind opponent, TKind[][] board) {
    int tstrat = 0;
    for (int i = 0; i < 8; i++)
      if (board[i][0] == opponent) tstrat++;
      else if (board[i][0] == me) tstrat--;
    for (int i = 0; i < 8; i++)
      if (board[i][7] == opponent) tstrat++;
      else if (board[i][7] == me) tstrat--;
    for (int i = 0; i < 8; i++)
      if (board[0][i] == opponent) tstrat++;
      else if (board[0][i] == me) tstrat--;
    for (int i = 0; i < 8; i++)
      if (board[7][i] == opponent) tstrat++;
      else if (board[7][i] == me) tstrat--;
    return tstrat;
  }
*/
  public int strategy(TKind me, TKind opponent, TKind[][] board) {
	int tstrat = 0;
	for (int i = 0; i < 8; i++)
	  if (board[i][0] == opponent) tstrat++;
	  else if (board[i][0] == me) tstrat--;
	for (int i = 0; i < 8; i++)
	  if (board[i][7] == opponent) tstrat++;
	  else if (board[i][7] == me) tstrat--;
	for (int i = 0; i < 8; i++)
	  if (board[0][i] == opponent) tstrat++;
	  else if (board[0][i] == me) tstrat--;
	for (int i = 0; i < 8; i++)
	  if (board[7][i] == opponent) tstrat++;
	  else if (board[7][i] == me) tstrat--;
	if (board[0][0] == opponent) tstrat += 4;
	else if (board[0][0] == me) tstrat -= 4;
	if (board[0][7] == opponent) tstrat += 4;
	else if (board[0][7] == me) tstrat -= 4;
	if (board[7][0] == opponent) tstrat += 4;
	else if (board[7][0] == me) tstrat -= 4;
	if (board[7][7] == opponent) tstrat += 4;
	else if (board[7][7] == me) tstrat -= 4;
	return tstrat;
  }

}
