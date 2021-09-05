package org.anhu.gameStructures.dynamicGameStructures;

import java.util.Objects;

public class VertexEdge {

    public final int sourceVertex;
    public final int targetVertex;

    public VertexEdge(int sourceVertex, int targetVertex){
        this.sourceVertex = sourceVertex;
        this.targetVertex = targetVertex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VertexEdge edge = (VertexEdge) o;
        return sourceVertex == edge.sourceVertex && targetVertex == edge.targetVertex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceVertex, targetVertex);
    }

    @Override
    public String toString() {
        return "VertexEdge(" + sourceVertex +
                ", " + targetVertex +
                ')';
    }
}
