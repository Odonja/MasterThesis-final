package org.anhu.gameStructures;

import java.util.*;

public class ParityGame {

    private int nrOfVertices;
    private final Map<Integer, Integer> priorities;
    private final Map<Integer, Boolean> owners; // true for player odd/1, false for player even/0
    private final Map<Integer, List<Integer>> outgoingEdges;
    private final Map<Integer, List<Integer>> incomingEdges;

    public ParityGame(){
        priorities = new HashMap<>();
        owners = new HashMap<>();
        outgoingEdges = new HashMap<>();
        incomingEdges = new HashMap<>();
    }

    public void addVertex(int vertex, int priority, boolean owner) {
        priorities.putIfAbsent(vertex, priority);
        owners.putIfAbsent(vertex, owner);
        outgoingEdges.putIfAbsent(vertex, new ArrayList<>());
        incomingEdges.putIfAbsent(vertex, new ArrayList<>());
    }

    public void addEdge(int vertexFrom, int vertexTo) {
        outgoingEdges.get(vertexFrom).add(vertexTo);
        incomingEdges.putIfAbsent(vertexTo, new ArrayList<>());
        incomingEdges.get(vertexTo).add(vertexFrom);
    }

    public List<Integer> getOutgoingEdges(int vertex){
        return outgoingEdges.get(vertex);
    }

    public List<Integer> getIncomingEdges(int vertex) {
        return incomingEdges.get(vertex);
    }

    public boolean getOwner(int vertex) {
        return owners.get(vertex);
    }

    public int getPriority(int vertex) {
        return priorities.get(vertex);
    }

    public Set<Integer> getVertices(){
        return owners.keySet();
    }

    public int getNrOfVertices() {
        return nrOfVertices;
    }

    public void setTotalNrOfVertices(int nrOfVertices) {
        this.nrOfVertices = nrOfVertices;
    }

}
