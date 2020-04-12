package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.client.AStar.APoint;
import com.codenjoy.dojo.snake.client.AStar.AStar;
import com.codenjoy.dojo.snake.client.Lee.LPoint;
import com.codenjoy.dojo.snake.client.Lee.Lee;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MyAlgorithm {
    private boolean findNewPath = true;
    private Optional<LinkedList<LPoint>> optionalPath;


    public Direction solve(Board board, Dice dice){
        ///SET UP
        Lee l = new Lee(board.size(),board.size());
        Point head = board.getHead();
        Point apple = board.getApples().get(0);
        Point stone = board.getStones().get(0);
        List<LPoint> obstacles = new ArrayList<>();
        List<LPoint> snake = board.getSnake().stream()
                .map(point -> LPoint.of(point.getX(), point.getY()))
                .collect(Collectors.toList());
        obstacles.add(LPoint.of(stone.getX(),stone.getY()));
        obstacles.addAll(snake);
        LPoint nextCoordinates;
        LinkedList<LPoint> path;


        if (findNewPath) optionalPath = l.trace(LPoint.of(head.getX(), head.getY()), LPoint.of(apple.getX(), apple.getY()), obstacles);
        if (optionalPath.isPresent()){
            path = optionalPath.get();

            System.out.println(path);

            nextCoordinates = path.poll();
            findNewPath = path.size() == 0;


            if (head.getY()>nextCoordinates.y && head.getX()==nextCoordinates.x) return Direction.DOWN;
            else if (head.getX()<nextCoordinates.x && head.getY()==nextCoordinates.y) return Direction.RIGHT;
            else if (head.getY()<nextCoordinates.y && head.getX()==nextCoordinates.x) return Direction.UP;
            else if (head.getX()>nextCoordinates.x && head.getY()==nextCoordinates.y) return Direction.LEFT;
        }
        return Direction.random(dice);
        }

    public Direction solve02(Board board, Dice dice){
        ///SET UP
        AStar aStar = new AStar(board.size(),board.size());
        Point head = board.getHead();
        Point apple = board.getApples().get(0);
        Point stone = board.getStones().get(0);
        List<APoint> obstacles = new ArrayList<>();
        List<APoint> snake = board.getSnake().stream()
                .map(point -> APoint.of(point.getX(), point.getY()))
                .collect(Collectors.toList());
        obstacles.add(APoint.of(stone.getX(),stone.getY()));
        obstacles.addAll(snake);
        APoint nextCoordinates;


        Optional<LinkedList<APoint>> optionalPath = aStar.trace(APoint.of(head.getX(), head.getY()), APoint.of(apple.getX(), apple.getY()), obstacles);

        if (optionalPath.isPresent()){
            LinkedList<APoint> path = optionalPath.get();
            System.out.println("\n"+path+"\n");
            nextCoordinates = path.get(0);

            if (head.getY()>nextCoordinates.y && head.getX()==nextCoordinates.x) return Direction.DOWN;
            else if (head.getX()<nextCoordinates.x && head.getY()==nextCoordinates.y) return Direction.RIGHT;
            else if (head.getY()<nextCoordinates.y && head.getX()==nextCoordinates.x) return Direction.UP;
            else if (head.getX()>nextCoordinates.x && head.getY()==nextCoordinates.y) return Direction.LEFT;
        }
        return Direction.random(dice);
    }

}
