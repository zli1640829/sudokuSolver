import java.util.Scanner;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;

public class Main {

    public static class Square {
        int value;
        ArrayList<Integer> domain;
        int row;
        int column;

        public Square(int row, int column, int value) {
            this.row = row;
            this.column = column;
            this.value = value;
            domain = new ArrayList<>();
            if (value == 0) {
                for (int i = 1; i <= 9; i++) {
                    domain.add(i);
                }
            } else { domain.add(value);}
        }
        public Square(Square s) {
            this.row = s.row;
            this.column = s.column;
            this.value = s.value;
            this.domain = new ArrayList<>(s.domain);
        }
    }

    public static class Board {
        ArrayList<Square> undeterminedBox;
        Square[][] board;

        public Board(Square[][] board) {
            this.board = board;
            undeterminedBox = new ArrayList<>();
            updateUndetermined();
        }

        public void updateUndetermined() {
            undeterminedBox.clear();
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (board[i][j].value == 0) {
                        undeterminedBox.add(board[i][j]);
                    }
                }
            }
        }
    }

    public Queue<Square[]> binaryTransfer(Board newBoard) {
        Queue<Square[]> arc = new LinkedList<>();

        // row
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = j + 1; k < 9; k++) {
                    arc.add(new Square[]{ newBoard.board[i][j], newBoard.board[i][k] });
                    arc.add(new Square[]{ newBoard.board[i][k], newBoard.board[i][j] });
                }
            }
        }

        // column
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = j + 1; k < 9; k++) {
                    arc.add(new Square[]{ newBoard.board[j][i], newBoard.board[k][i] });
                    arc.add(new Square[]{ newBoard.board[k][i], newBoard.board[j][i] });
                }
            }
        }

        // box
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                Square[] cells = new Square[9];
                int index = 0;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        cells[index++] = newBoard.board[boxRow * 3 + i][boxCol * 3 + j];
                    }
                }
                for (int i = 0; i < 9; i++) {
                    for (int j = i + 1; j < 9; j++) {
                        arc.add(new Square[]{ cells[i], cells[j] });
                        arc.add(new Square[]{ cells[j], cells[i] });
                    }
                }
            }
        }
        return arc;
    }

    public Queue<Square> getNeighbour(Square[][] board, Square target) {
        Queue<Square> neighbours = new LinkedList<>();
        int row = target.row;
        int column = target.column;
        // row neighbours.
        for (int j = 0; j < 9; j++) {
            if (j != column) {
                neighbours.add(board[row][j]);
            }
        }
        // column neighbours.
        for (int i = 0; i < 9; i++) {
            if (i != row) {
                neighbours.add(board[i][column]);
            }
        }
        // box neighbours.
        int boxRow = (row / 3) * 3;
        int boxColumn = (column / 3) * 3;
        for (int i = boxRow; i < boxRow + 3; i++) {
            for (int j = boxColumn; j < boxColumn + 3; j++) {
                if (i != row || j != column) {
                    neighbours.add(board[i][j]);
                }
            }
        }
        return neighbours;
    }

    // revise the domain of A given B's assignment
    public boolean revise(Square A, Square B) {
        boolean revised = false;
        if (A.value == 0 && B.value != 0) {
            // change the domain if B already has the value
            for (int i = A.domain.size() - 1; i >= 0; i--) {
                if (A.domain.get(i) == B.value) {
                    A.domain.remove(i);
                    revised = true;
                }
            }
        }
        return revised;
    }


    public boolean AC_three(Board newBoard) {
        // first transfer the board into pairs
        Queue<Square[]> queue = binaryTransfer(newBoard);
        while (!queue.isEmpty()) {
            Square[] pair = queue.poll();
            // compare the two, eliminate the given number in the domain of a not given number
            if (revise(pair[0], pair[1])) {
                if (pair[0].domain.isEmpty()) {
                    return false;
                }
                // the neighbours also can change their domain after the change
                Queue<Square> neighbours = getNeighbour(newBoard.board, pair[0]);
                for (Square neighbor : neighbours) {
                    if (neighbor != pair[1] && neighbor.value == 0) {
                        queue.add(new Square[]{ neighbor, pair[0] });
                    }
                }
            }
        }
        newBoard.updateUndetermined();
        return true;
    }


    public Square[][] backTrackSearch(Board newBoard) {
        if (newBoard.undeterminedBox.isEmpty()) return newBoard.board;

        // MRV
        Square target = null;
        int minDomainSize = Integer.MAX_VALUE;
        for (Square s : newBoard.undeterminedBox) {
            if (s.domain.size() < minDomainSize) {
                minDomainSize = s.domain.size();
                target = s;
            }
        }
        ArrayList<Integer> candidates = new ArrayList<>(target.domain);
        for (int candidate : candidates) {
            // create a copy of the board
            Square[][] boardCopy = new Square[9][9];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    boardCopy[i][j] = new Square(newBoard.board[i][j]);
                }
            }
            Board newBoardCopy = new Board(boardCopy);
            // put the info into the new copy
            newBoardCopy.board[target.row][target.column].value = candidate;
            newBoardCopy.board[target.row][target.column].domain.clear();
            newBoardCopy.board[target.row][target.column].domain.add(candidate);

            // do AC-three again
            if (AC_three(newBoardCopy)) {
                Square[][] result = backTrackSearch(newBoardCopy);
                if (result != null) return result;
            }
        }
        return null;
    }


    public static void printBoard(Square[][] board) {
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                System.out.print(board[i][j].value + " ");
            }
            System.out.println();
        }
    }

    public Square[][] assignSingle(Square [][] a ){
        for (int i = 0; i < 9; i++){
            for (int j = 0; j<9; j++){
                if (a[i][j].value == 0){
                    if (a[i][j].domain.size()==1){
                        a[i][j].value = a[i][j].domain.get(0);
                    }
                }
            }
        }
        return a;
    }

    public static void main(String[] args) {
        Main mainInstance = new Main();
        Square[][] box = new Square[9][9];

        Scanner scanner = new Scanner(System.in);

        for (int i = 0; i < 9; i++) {
            String line = scanner.nextLine().trim();
            String[] tokens = line.split("\\s+");
            for (int j = 0; j < 9; j++) {
                int value = Integer.parseInt(tokens[j]);
                box[i][j] = new Square(i, j, value);
            }
        }

        Board result = new Board(box);
        mainInstance.AC_three(result);


        Square[][] solution = mainInstance.backTrackSearch(result);
        if (solution != null){
            solution = mainInstance.assignSingle(solution);
            printBoard(solution);
        } else {System.out.println ("No solution.");}

    }
}
