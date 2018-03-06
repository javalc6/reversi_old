//16-12-2017 version 0.2: refactored

public interface Strategy {
	int strategy(TKind me, TKind opponent, TKind [][] board);
}