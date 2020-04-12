package com.codenjoy.dojo.snake.client.AStar;

public class APoint {
    public final int x;
    public final int y;

    public APoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static APoint of(int x, int y){
        return new APoint(x,y);
    }

    public APoint move(APoint delta){
        return APoint.of(x+delta.x,
                         y+delta.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        APoint lPoint = (APoint) o;

        if (x != lPoint.x) return false;
        return y == lPoint.y;
    }

    @Override
    public int hashCode() {
        return x << 16+y;
    }

    @Override
    public String toString() {
        return String.format("[%2d:%2d]",x,y);
    }
}