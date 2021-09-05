package org.anhu.gameStructures;

import java.util.List;

public class GameState {
    public final boolean[] verticesInGame;
    public int nrOfVerticesInGame;

    public GameState(int totalNrOfVertices){
        verticesInGame = new boolean[totalNrOfVertices];
        nrOfVerticesInGame = 0;
    }

    public GameState(boolean[] verticesInGame, int nrOfVerticesInGame){
        this.verticesInGame = verticesInGame;
        this.nrOfVerticesInGame = nrOfVerticesInGame;
    }

    public void isInGame(int vertex){
        if(!verticesInGame[vertex]){
            verticesInGame[vertex] = true;
            nrOfVerticesInGame++;
        }
    }

    public void isInGame(List<Integer> vertices){
        for(int vertex : vertices){
            isInGame(vertex);
        }
    }

    public void removeFromGame(int vertex){
        if(verticesInGame[vertex]){
            verticesInGame[vertex] = false;
            nrOfVerticesInGame--;
        }
    }

    public void removeFromGame(List<Integer> vertices){
        for(int vertex : vertices){
            removeFromGame(vertex);
        }
    }

}
