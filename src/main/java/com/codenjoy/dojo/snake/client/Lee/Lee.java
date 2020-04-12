package com.codenjoy.dojo.snake.client.Lee;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Lee {
    private static final int OBSTACLES = -9;
    private static final int START = -1;
    private static final int FINISH = -3;
    private final int width;
    private final int height;
    private final int[][] board;
    private final int EMPTY=0;
    List<LPoint> delta = Arrays.asList(LPoint.of(0,1),LPoint.of(1,0),
                                        LPoint.of(-1,0),LPoint.of(0,-1));

    public Lee(int width, int height) {
        this.width = width;
        this.height = height;
        this.board = new int[width][height];
    }
    private void set(LPoint point, int value){
        board[point.x][point.y] = value;
    }
    private int get(LPoint point){
        return board[point.x][point.y];
    }

    private boolean isUnvisited(LPoint p){
        return get(p) == EMPTY;
    }

    private boolean isOnBoard(LPoint p){
        return p.x>=0 && p.x<width && p.y>=0 && p.y<height;
    }

    private Stream<LPoint> neighbours(LPoint p) {
        return delta.stream().map(point -> point.move(p))
                .filter(this::isOnBoard);
    }

    private List<LPoint> neighboursUnvisited(LPoint point){
        return neighbours(point)
                .filter(point1 -> isUnvisited(point1))
                .collect(Collectors.toList());
    }

    private List<LPoint> neighboursByValue(LPoint point, int value){
        return neighbours(point)
                .filter(p -> get(p)==value)
                .collect(Collectors.toList());
    }

    private void initializeBoard(List<LPoint> obstacles){
        IntStream.range(0,height)
                .boxed().flatMap(y->
                IntStream.range(0,width)
                        .mapToObj(x->LPoint.of(x,y)))
                .forEach(point -> set(point,EMPTY));
        obstacles.forEach(point -> set(point,OBSTACLES));
    }

    public Optional<LinkedList<LPoint>> trace(LPoint start, LPoint finish, List<LPoint> obstacles){
        initializeBoard(obstacles);
        set(start,START);
//        set(LPoint.of(2,7),OBSTACLES);
        List<LPoint> curr = new ArrayList<>();
        curr.add(start);
        int[] counter = { 0 };
        boolean found = false;
        while (!(curr.isEmpty() || found)){
            counter[0]++;
            Set<LPoint> next = curr.stream().map(point -> neighboursUnvisited(point))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
            next.forEach(point -> set(point,counter[0]));
            found = next.contains(finish);
            curr.clear();
            curr.addAll(next);
        }
        printMe(Collections.emptyList());

        if (!found) return Optional.empty();
        LinkedList<LPoint> path = new LinkedList<>();
        path.add(finish);
        LPoint curr_p = finish;
        set(start,EMPTY);
        while (counter[0]>1){
            counter[0]--;
            LPoint prev = neighboursByValue(curr_p, counter[0]).get(0);
            path.addFirst(prev);
            curr_p=prev;
        }
        printMe(path);
        return Optional.of(path);

    }

    private void printMe(List<LPoint> path){
        String s = IntStream.range(0, height).mapToObj(y -> IntStream.range(0, width)
                .mapToObj(x -> LPoint.of(x, y))
                .map(point -> formatter(point,path))
                .collect(Collectors.joining())
        ).collect(Collectors.joining("\n"));
        System.out.println(s);
        System.out.println("\n\n");
    }

    private String formatter(LPoint point,List<LPoint> path) {
        int val = get(point);
        if (val==OBSTACLES) return " XX";
        if (path.isEmpty()) return String.format("%3d", val);       // intermediate steps
        if (path.contains(point)) return String.format("%3d", val); // final step
        return " --";
    }
}
