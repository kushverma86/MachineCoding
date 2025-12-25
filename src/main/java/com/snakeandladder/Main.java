package com.snakeandladder;

import com.snakeandladder.Entity.Board;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Creating a board of size 100 !!!");
        Board board = new Board(100);

        System.out.println("Enter snakes count : ");
        int snakesCount = Integer.parseInt(br.readLine());
        for (int i=0; i<snakesCount; i++){
            System.out.println("Enter head and tail for snake " + (i+1) + " in format <head> <tail>");
            String[] pos = br.readLine().trim().split(" ");
            board.addSnake(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));
        }

        System.out.println("Enter ladders count : ");
        int laddersCount = Integer.parseInt(br.readLine());
        for (int i=0; i<laddersCount; i++){
            System.out.println("Enter start and end for ladder " + (i+1) + " in format <head> <tail>");
            String[] pos = br.readLine().trim().split(" ");
            board.addLadder(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));
        }

        System.out.println("Board has been set!!!!");
        BoardGameManager boardGameManager = new BoardGameManager(board);

        System.out.println("Enter players count : ");
        int playerssCount = Integer.parseInt(br.readLine());
        for (int i=0; i<playerssCount; i++){
            System.out.println("Enter player" + (i+1) +  " name : ");
            String playerName = br.readLine().trim();
            boardGameManager.addPlayer(playerName);
        }

        boardGameManager.init();

    }
}
