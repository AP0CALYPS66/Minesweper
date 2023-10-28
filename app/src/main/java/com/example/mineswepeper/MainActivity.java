package com.example.mineswepeper;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    int curri, currj;
    Button[][] cells;
    int[][] minefield;
    int[][] markfield;
    String[][] lastfield;
    final int WIDTH = 8;
    final int HEIGHT = 8;
    //Stack <int> boreders = new Stack <>;
    final int MINESCONST = 10;
    int cnt;
    int MinesCurrent = 3;
    TextView mines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mines = findViewById(R.id.mines);
        mines.setText("" + MINESCONST + "/" + MinesCurrent);
        generate();
    }

    public void generate() {
        GridLayout layout = findViewById(R.id.Grid);
        layout.removeAllViews();
        layout.setColumnCount(WIDTH);
        cells = new Button[HEIGHT][WIDTH];
        minefield = new int[HEIGHT][WIDTH];
        markfield = new int[HEIGHT][WIDTH];
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                cells[i][j] = (Button) inflater.inflate(R.layout.cells, layout, false);
                cells[i][j].setText("?");
            }
        }
        int defcolor = Color.WHITE;
        final int[] start_mines = new Random().ints(0, HEIGHT * WIDTH - 1).distinct().limit(MINESCONST).toArray();
        for (int x : start_mines) {
            curri = x % WIDTH;
            currj = x / WIDTH;
            minefield[curri][currj] = 1;
        }

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                int finalJ = j;
                int finalI = i;
                cells[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (minefield[finalI][finalJ] == 1) {
                            showMines(minefield, cells);
                            Toast.makeText(getApplicationContext(), "YOU LOSE", Toast.LENGTH_LONG).show();
                        } else {
                            startWave(cells,minefield,markfield,finalI, finalJ);
                            //v.setBackgroundColor(Color.RED);
                        }
                    }
                });
                cells[i][j].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (markfield[finalI][finalJ] == 0) {
                            cells[finalI][finalJ].setText("F");
                            cells[finalI][finalJ].setBackgroundColor(Color.YELLOW);
                            markfield[finalI][finalJ] = 1;
                        } else {
                            //cells[finalI][finalJ] = las;
                            MinesCurrent++;
                            markfield[finalI][finalJ] = 0;
                            cells[finalI][finalJ].setBackgroundColor(defcolor);
                        }
                        mines.setText("" + MINESCONST + "/" + MinesCurrent);
                        if (MinesCurrent == 0) {
                            cnt = 0;
                            for (int i = 0; i < HEIGHT; i++)
                                for (int j = 0; j < WIDTH; j++)
                                    if (markfield[i][j] == 1 && minefield[i][j] == 1) cnt += 1;
                            if (cnt == MINESCONST) Toast.makeText(getApplicationContext(), "WIN!", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
                layout.addView(cells[i][j]);
            }
        }
    }

    public void showMines(int[][] minefield, Button[][] cells) {
        for (int i = 0; i < HEIGHT; i++)
            for (int j = 0; j < WIDTH; j++)
                if (minefield[i][j] == 1) {
                    cells[i][j].setBackgroundColor(Color.RED);
                    cells[i][j].setText("");
                }
    }

    public int checkNei(int[][] minefield, int[][] markfield, int icord, int jcord) {
        int ans = 0;
        for (int i = icord - 1; i <= icord + 1; i++)
            for (int j = jcord - 1; j <= jcord + 1; j++) {
                if (i >= 0 && i < HEIGHT && j >= 0 && j < WIDTH)
                    if (minefield[i][j] == 1) ans++;
            }
        return ans;
    }

    @SuppressLint("SetTextI18n")
    public void startWave(Button[][] cells, int[][] minefield, int[][] markfield, int icord, int jcord) {
        int nei;
        int it,jt;
        int[][] map = new int[HEIGHT][WIDTH];
        Deque<int[]> deque = new ArrayDeque<>();
        int[] cords = new int[2];
        cords[0] = icord;
        cords[1] = jcord;
        deque.add(cords);
        while (!deque.isEmpty()) {
            cords = deque.removeFirst();
            //System.out.println(Arrays.toString(cords));
            map[cords[0]][cords[1]] = 1;
            if (minefield[cords[0]][cords[1]] == 0) {
                nei = checkNei(minefield, markfield, cords[0], cords[1]);
                if (nei == 0) {
                    cells[cords[0]][cords[1]].setText("");
                    it = cords[0];
                    jt = cords[1];
                    for (int i = it - 1; i <= it + 1; i++)
                        for (int j = jt - 1; j <= jt + 1; j++) {
                            if (i >= 0 && i < HEIGHT && j >= 0 && j < WIDTH && map[i][j] == 0) {
                                cords[0] = i;
                                cords[1] = j;
                                map[i][j] = 1;
                                //System.out.println("В условии " + i + " " + j);
                                deque.add(new int[]{i, j});
                            }
                        }
                } else cells[cords[0]][cords[1]].setText(nei + "");
                //System.out.println("Длина дека: " + deque.size());
            }
        }

    }
}