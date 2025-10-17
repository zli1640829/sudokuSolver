# sudokuSolver

author: Zhiheng Li
contact: zli050829@gmail.com

This Java program is a Sudoku solver built for my university AI course project. It combines ideas from constraint satisfaction problems (CSP) and backtracking search to solve Sudoku puzzles efficiently and logically — very much like how a human would reason through them.

Each cell on the Sudoku grid is modeled as a Square object with a domain of possible values (1–9). The solver first applies the AC-3 (Arc Consistency 3) algorithm to eliminate impossible values by enforcing Sudoku’s row, column, and 3×3 box constraints. This preprocessing step significantly reduces the search space by maintaining domain consistency.

Afterward, the program uses backtracking search guided by the MRV (Minimum Remaining Values) heuristic, choosing the most constrained cell at each step. It recursively explores valid assignments until the entire board satisfies all Sudoku rules.

The program reads a 9×9 Sudoku grid (using 0 for empty cells) from standard input and prints the completed solution.
