package lekavar.mazegenerator.data;

import java.util.Random;

/*
 * Author:Eren·Yeager-
 * https://blog.csdn.net/qq_63993414/article/details/122083136
 * */
public class Maze {
    private int width;
    private int height;
    public int[][] map;
    private int r;
    private int c;

    public Maze(int r0, int c0) {
        width = (r0 - 1) / 2;
        height = (c0 - 1) / 2;
        r = 2 * width + 1;
        c = 2 * height + 1;
        map = new int[r][c];
    }

    public int[][] init() {
        for (int i = 0; i < r; i++)
            for (int j = 0; j < c; j++)
                map[i][j] = 0;//
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                map[2 * i + 1][2 * j + 1] = 1;
        Prime();
        map[1][0] = 1;
        map[r - 2][c - 1] = 1;
        return map;
    }

    public void Prime() {
        int[] ok, not;
        int sum = width * height;
        int count = 0;
        ok = new int[sum];
        not = new int[sum];
        int[] offR = {-1, 1, 0, 0};
        int[] offC = {0, 0, 1, -1};

        int[] offS = {-1, 1, width, -width};
        for (int i = 0; i < sum; i++) {
            ok[i] = 0;
            not[i] = 0;
        }
        Random rd = new Random();
        ok[0] = rd.nextInt(sum);
        int pos = ok[0];
        not[pos] = 1;
        while (count < sum) {
            int x = pos % width;
            int y = pos / width;
            int offpos = -1;
            int w = 0;
            while (++w < 5) {
                int point = rd.nextInt(4);
                int repos;
                int move_x, move_y;
                repos = pos + offS[point];
                move_x = x + offR[point];
                move_y = y + offC[point];
                if (move_y >= 0 && move_x >= 0 && move_x < width && move_y < height && repos >= 0 && repos < sum
                        && not[repos] != 1) {
                    not[repos] = 1;
                    ok[++count] = repos;
                    pos = repos;
                    offpos = point;
                    map[2 * x + 1 + offR[point]][2 * y + 1 + offC[point]] = 1;
                    break;
                } else {
                    if (count == sum - 1)
                        return;
                }
            }
            if (offpos < 0) {
                pos = ok[rd.nextInt(count + 1)];
            }
        }
    }

    public void printMaze() {
        System.out.println("========================================");
        String result = "\n";
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                int point = map[i][j];
                result += (point == 0 ? "@" : " ");
            }
            result += '\n';
        }
        System.out.println(result);
        System.out.println("========================================");
    }

    public int[][] rotateMap(int angle) {
        angle = angle % 90 == 0 ? angle : 0;
        int rotateTime = (4 - angle / 90) % 4;
        int[][] result = new int[c][r];
        for (int num = 0; num < rotateTime; num++) {
            for (int i = 0; i < c; i++) {
                for (int j = 0; j < r; j++) {
                    result[r - 1 - j][i] = map[i][j];
                }
            }
        }
        return result;
    }
}
