package com.snakeandladder;

import com.snakeandladder.Entity.*;
import com.snakeandladder.exception.PlayerNotFoundException;
import com.snakeandladder.exception.PlayerNotInGameException;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Getter
public class BoardGameManager {

    private Board board;
    private Map<String, Player> playerList;
    @Setter
    private boolean gameFinished;
    private Map<String, Player> playersInGame;

    private Queue<Player> playerTurnQueue;

    private Dice dice;

    BoardGameManager(Board board){
        this.board = board;
        this.gameFinished = false;
        playerList = new HashMap<String, Player>();
        playersInGame = new HashMap<String, Player>();
        playerTurnQueue = new LinkedList<>();
        dice = new Dice(1);  //configurable
    }

    void addPlayer(String name){
        Player player  = new Player(name);
        playerList.put(name, player);
        playersInGame.put(name, player);
    }

    private int movePlayer(String name, int n){
        if (!playerList.containsKey(name)){
            throw new PlayerNotFoundException("Player with " + name + " not found");
        }

        if(!playersInGame.containsKey(name)){
            throw new PlayerNotInGameException("Player with " + name + " have finished game already !!!");
        }
        Player player = playersInGame.get(name);

        if (player.getCurrentPos() + n > board.getTotalCells()){
            System.out.println("Player " + name + " can't move beyond board, Player currPos = " + player.getCurrentPos());
            return player.getCurrentPos();
        }

        //<player_name> rolled a <dice_value> and moved from <initial_position> to <final_position>

        // check for snakes
        if (board.getSnakes().containsKey(player.getCurrentPos() + n)){
            Snake snake = board.getSnakes().get(player.getCurrentPos() + n);
            //<player_name> rolled a <dice_value> and moved from <initial_position> to <final_position>
            player.setCurrentPos(snake.getTail());
            return player.getCurrentPos();
        }

        if (board.getLadders().containsKey(player.getCurrentPos() + n)){
            Ladder ladder = board.getLadders().get(player.getCurrentPos() + n);
//            System.out.println("Player " + name + " rolled a " + n + "and moved from " + player.getCurrentPos() + " to " + ladder.getEnd());
            player.setCurrentPos(ladder.getEnd());
            player.getCurrentPos();
        }


        player.setCurrentPos(player.getCurrentPos() + n);
//        System.out.println("Player " + name + " rolled a " + n + "and moved from " + (player.getCurrentPos() - n)  + " to " + player.getCurrentPos());


        if (player.getCurrentPos() == board.getTotalCells()){
            System.out.println("Player " + player.getName() + " wins the game");
            player.setWon(true);
            playersInGame.remove(player.getName());
        }

        return player.getCurrentPos();
    }


    void init(){
        if(playerList.size() < 2){
            throw new IllegalStateException("Atleast 2 or more players should be added");
        }

        System.out.println("Game started!!!!");

        for (Map.Entry<String, Player> player : playerList.entrySet()){
            playerTurnQueue.add(player.getValue());
        }

        while(playersInGame.size()>1){
//            takeTurn();
            // should take turn round robbin ways
            Player player = playerTurnQueue.remove();
            int roll = dice.roll();
            int currPos = player.getCurrentPos();
            int newPos = movePlayer(player.getName(), roll);
            System.out.println("Player " + player.getName() + " rolled a " + roll + " and moved from " + currPos + " to " + player.getCurrentPos());
            playerTurnQueue.add(player);
        }

        this.setGameFinished(true);

        System.out.println("Game has finished !!!!");
    }



}
