package com.juulis.lanterngame;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            Game game = new Game();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}