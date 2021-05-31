class Solution {
    // 问题1：如何判断能否填满
    public static boolean checkBoard1(int x, int y) {
        int[][] board = new int[x][y];
        int count = 0;
        int i = 0;
        int j = 0;
        while (true) {
            if (board[i][j] == 1) {
                return count == x * y;
            } else {
                board[i][j] = 1;
                i++;
                j++;
                i %= x;
                j %= y;
            }
            count++;
        }
    }

    // 问题2：找规律，什么情况能填满
    public static boolean checkBoard2(int x, int y) {
        if (x == 1 && y == 1) return true;
        while (x != 1 && y != 1) {
            if (y == 0) return false;
            x %= y;
            if (x == 0) return false;
            y %= x;
        }
        return x != 1 || y != 1;
    }

    public static void main(String[] args) {
        // for test
        for (int i = 1; i < 500; i++) {
            for (int j = 1; j < 500; j++) {
                System.out.println("check i=" + i + ", j=" + j);
                if (checkBoard1(i, j) != checkBoard2(i, j)) {
                    System.out.println("Oops! i=" + i + ", j=" + j + ", " + checkBoard1(i, j) + ", " + checkBoard2(i, j));
                    return;
                }
            }
        }
        System.out.println("Pass");
    }
}