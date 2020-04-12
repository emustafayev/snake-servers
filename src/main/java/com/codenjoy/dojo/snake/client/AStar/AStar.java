package com.codenjoy.dojo.snake.client.AStar;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AStar {
    private static final int EMPTY = 0;
    private static final int FILLED = 1;
    private static final int OBSTACLES = -3;
    private static final int START = -1;

    static class Node{
        public int f=0;
        public int g=0;
        public int h=0;
        public int value;
        public APoint cameFrom;

        public Node(int value) {
            this.value = value;
        }

        public static Node of(int value){
            return new Node(value);
        }
    }

    private final Node[][] board;
    private final int width;
    private final int height;
    private List<APoint> delta = Arrays.asList(APoint.of(0,1), APoint.of(1,0),
            APoint.of(-1,0), APoint.of(0,-1));

    public AStar(int width, int height) {
        this.width = width;
        this.height = height;
        this.board = new Node[width][height];
    }

    private void createNode(APoint p, int value){
        board[p.x][p.y] = Node.of(value);
    }

    private Node getNode(APoint p){
        return board[p.x][p.y];
    }

    private void updateNode(APoint p, int value){
        getNode(p).value=value;
    }

    private boolean isOnBoard(APoint p){
        return p.x>=0 && p.x<width && p.y>=0 && p.y<height;
    }

    private Stream<APoint> neighbours(APoint p){
        return delta.stream()
                .map(point->point.move(p))
                .filter(this::isOnBoard);
    }

    private boolean isUnvisited(APoint point){
        return getNode(point).value == EMPTY;
    }

    private List<APoint> neighboursUnvisited(APoint p){
        return neighbours(p).filter(this::isUnvisited)
                .collect(Collectors.toList());
    }

    private List<APoint> neighboursByValue(APoint point, int value){
        return neighbours(point)
                .filter(p -> getNode(p).value==value)
                .collect(Collectors.toList());
    }

    private void initializeBoard(List<APoint> obstacles){
        IntStream.range(0, height).boxed().flatMap(y->
                IntStream.range(0,width).mapToObj(x->
                        APoint.of(x,y)))
                .forEach(point -> createNode(point,EMPTY));
        obstacles.forEach(point -> updateNode(point,OBSTACLES));
    }

    public Optional<LinkedList<APoint>> trace(APoint start, APoint finish, List<APoint> obstacles){
        initializeBoard(obstacles);
        updateNode(start,START);
        boolean found = false;
        Set<APoint> openSet = new HashSet<>();
        openSet.add(start);
        APoint[] current = new APoint[1];

        while (!(openSet.isEmpty() || found)){
            current[0] = openSet.stream()
                    .min(Comparator.comparingInt(p -> getNode(p).f)).get();
            found=current[0].equals(finish);
            openSet.remove(current[0]);
            updateNode(current[0],FILLED);
            neighboursUnvisited(current[0]).forEach(new Consumer<APoint>() {
                @Override
                public void accept(APoint neighbour) {
                    int tempG = getNode(current[0]).g+1;
                    Node neighbourNode = getNode(neighbour);

                    if (tempG<neighbourNode.g && openSet.contains(neighbour)){
                        /// cameFrom neighbour
                        neighbourNode.g=tempG;
                    }else {
                        neighbourNode.g = tempG;
                        openSet.add(neighbour);
                    }
                    neighbourNode.h = heuristic(neighbour,finish);
                    neighbourNode.f=neighbourNode.g + neighbourNode.h;
                    neighbourNode.cameFrom =current[0];
                }
            });
        }

        if (!found)return Optional.empty();

        LinkedList<APoint> path = new LinkedList<>();
        APoint temp = current[0];
        path.addFirst(temp);
        while (getNode(temp).cameFrom!=null){
            path.addFirst(temp);
            temp=getNode(temp).cameFrom;
        }

        return Optional.of(path);
    }

    private int heuristic(APoint from, APoint to) {
        return Math.abs((from.x-to.x))+Math.abs((from.y-to.y));
    }
}
