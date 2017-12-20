package com.juulis.lanterngame;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Game {
    boolean game = true;
    Terminal t;
    Screen screen = null;
    Obj player;
    List<Obj> monster = new ArrayList();
    List<Obj> food = new ArrayList();
    int points = 0;

    Game() throws IOException {
        createTerminal();

        while (game) {
            KeyStroke keyStroke = screen.pollInput();
            if (keyStroke != null && (keyStroke.getKeyType() == KeyType.ArrowUp)) {
                movePlayer(0, -1);
            }
            if (keyStroke != null && (keyStroke.getKeyType() == KeyType.ArrowDown)) {
                movePlayer(0, 1);
            }
            if (keyStroke != null && (keyStroke.getKeyType() == KeyType.ArrowLeft)) {
                movePlayer(-1, 0);
            }
            if (keyStroke != null && (keyStroke.getKeyType() == KeyType.ArrowRight)) {
                movePlayer(1, 0);
            }
            if (keyStroke != null && (keyStroke.getKeyType() == KeyType.Escape || keyStroke.getKeyType() == KeyType.EOF)) {
                break;
            }
        }
        System.out.println("Game Over");
        t.setCursorPosition(t.getTerminalSize().getColumns()/3, t.getTerminalSize().getRows()/2);
        t.putCharacter('G');
        t.putCharacter('A');
        t.putCharacter('M');
        t.putCharacter('E');
        t.putCharacter(' ');
        t.putCharacter('O');
        t.putCharacter('V');
        t.putCharacter('E');
        t.putCharacter('R');
        t.putCharacter('!');
        t.putCharacter('!');
        t.flush();

    }

    void movePlayer(int x, int y) throws IOException {
        checkCollision();
        if (!checkCollision()) {
            clearPos(player.x, player.y);
            player.x += x;
            player.y += y;
        }
        placeObject(player);
        t.flush();
        moveMonster();
    }

    void clearPos(int x, int y) throws IOException {
        t.setCursorPosition(x, y);
        t.putCharacter(' ');
    }

    void createFood() throws IOException {
        int[] pos = randomPos();
        Obj f = new Obj(pos[0], pos[1], 'F');
        food.add(f);
        placeObject(f);
    }


    void moveMonster() throws IOException {

        for (int i = 0; i < monster.size(); i++) {
            if (Math.random() > 0.7) {
                int[] oldPos = {monster.get(i).x, monster.get(i).y};

                if (player.x < monster.get(i).x) {
                    monster.get(i).x -= 1;
                }
                if (player.y < monster.get(i).y) {
                    monster.get(i).y -= 1;
                }
                if (player.x > monster.get(i).x) {
                    monster.get(i).x += 1;
                }
                if (player.y > monster.get(i).y) {
                    monster.get(i).y += 1;
                }
                clearPos(oldPos[0], oldPos[1]);
                placeObject(monster.get(i));
            }
        }
    }

    boolean checkCollision() throws IOException {
        for (int i = 0; i < monster.size(); i++) {
            if (player.x == monster.get(i).x && player.y == monster.get(i).y) {
                game = false;
                return true;
            }
        }

        for (int i = 0; i < food.size(); i++) {
            if (player.x == food.get(i).x && player.y == food.get(i).y) {
                food.remove(i);
                points++;
                showPoints();
                createFood();

                if (Math.random() < 0.3) {
                    createMonster();
                    createFood();
                }

            }
        }

        if (player.x < 0) {
            player.x = 0;
            return true;
        } else if (player.y < 0) {
            player.y = 0;
            return true;
        } else if (player.x == t.getTerminalSize().getColumns()) {
            player.x = t.getTerminalSize().getColumns() - 1;
            return true;
        } else if (player.y == t.getTerminalSize().getRows()) {
            player.y = t.getTerminalSize().getRows() - 1;
            return true;
        }

        return false;


    }

    void createMonster() throws IOException {
        int[] pos = randomPos();
        Obj m = new Obj(pos[0], pos[1], 'M');
        monster.add(m);
        placeObject(monster.get(monster.size() - 1));
    }

    int[] randomPos() throws IOException {
        int x = ThreadLocalRandom.current().nextInt(0, t.getTerminalSize().getColumns());
        int y = ThreadLocalRandom.current().nextInt(0, t.getTerminalSize().getRows());
        int[] pos = {x, y};
        return pos;
    }

    void placeObject(Obj obj) throws IOException {
        t.setCursorPosition(obj.x, obj.y);
        t.putCharacter(obj.symbol);
        t.flush();
    }

    void createTerminal() throws IOException {
        t = new DefaultTerminalFactory().createTerminal();
        screen = new TerminalScreen(t);
        screen.startScreen();
        int[] size = {t.getTerminalSize().getColumns(), t.getTerminalSize().getRows()};
        t.setCursorVisible(false);
        TextColor color = new TextColor.RGB(255, 204, 204);
        t.setBackgroundColor(color);
        color = new TextColor.RGB(0, 0, 0);
        t.setForegroundColor(color);

        coloringScreen(size);

        showPoints();
        player = new Obj(t.getTerminalSize().getColumns() / 2, t.getTerminalSize().getRows() / 2, 'X');
        placeObject(player);
        createFood();
        createMonster();

        t.flush();
    }

    void showPoints() throws IOException {
        t.setCursorPosition(2, 1);
        t.putCharacter('P');
        t.putCharacter('O');
        t.putCharacter('I');
        t.putCharacter('N');
        t.putCharacter('T');
        t.putCharacter('S');
        t.putCharacter(':');
        char[] chars = ("" + points).toCharArray();
        for (char e : chars) {
            t.putCharacter(e);
        }
        t.flush();
    }

    void coloringScreen(int[] size) throws IOException {
        TextColor color = new TextColor.RGB(0, 255, 255);
        for (int i = 0; i < size[0]; i++) {
            for (int col = 0; col < size[1]; col++) {
                t.setCursorPosition(i, col);
                t.putCharacter(' ');
                t.flush();
            }
        }
    }
}