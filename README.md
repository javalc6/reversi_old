![reversi](images/reversi.jpg)
============
```
Deprecation notice: this repository is deprecated as it uses outdated Java libraries (e.g. JApplet)
```
# Reversi
Reversi (Othello) is a game based on a grid with eight rows and eight columns, played between you and the computer, by adding pieces with two sides: black and white.
At the beginning of the game there are 4 pieces in the grid, the player with the black pieces is the first one to place his piece on the board.
Each player must place a piece in a position that there exists at least one straight (horizontal, vertical, or diagonal) line between the new piece and another piece of the same color, with one or more contiguous opposite pieces between them. 


# Compile
Run following commands to build the application:
```
javac -Xlint *.java
jar cmf build.mf reversi.jar *.java *.class images\*.jpg *.html gpl.txt
```

# Run
Just use the following command to run the application:
```
java Reversi
```

# Screenshot

![Screenshot](https://raw.githubusercontent.com/javalc6/reversi/master/images/screenshot.png)

# References

- [Reversi](https://thesaurus.altervista.org/revers-help)

- [Reversi on Android](https://play.google.com/store/apps/details?id=livio.reversi)
