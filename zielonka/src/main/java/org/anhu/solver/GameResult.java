package org.anhu.solver;

import org.anhu.commons.ListSetPair;

import java.util.List;

public class GameResult {
    private final ListSetPair<Integer> verticesWonByPlayer0;
    private final ListSetPair<Integer> verticesWonByPlayer1;

    public GameResult(){
        verticesWonByPlayer0 = new ListSetPair<>();
        verticesWonByPlayer1 = new ListSetPair<>();
    }

    public void wonByPlayer0(List<Integer> vertices){
        for(int vertex : vertices){
            verticesWonByPlayer0.addIfNotPresent(vertex);
        }
    }

    public void wonByPlayer1(List<Integer> vertices){
        for(int vertex : vertices){
            verticesWonByPlayer1.addIfNotPresent(vertex);
        }
    }

    public List<Integer> getVerticesWonByPlayer0(){
        return verticesWonByPlayer0.list;
    }

    public List<Integer> getVerticesWonByPlayer1(){
        return verticesWonByPlayer1.list;
    }
}
