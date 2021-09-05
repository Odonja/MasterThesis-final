package org.anhu.gameStructures.dynamicGameStructures.nodeStructures;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class LeafNode extends BaseNode {

    private final int vertex;

    public LeafNode(int vertex) {
        this.vertex = vertex;
    }

    @Override
    public boolean isLeafNode() {
        return true;
    }

    @Override
    public void getAllVertices(List<Integer> vertexList) {
        vertexList.add(vertex);
    }

    @Override
    public void getAllVerticesAndUpdateKnowns(Map<SCCTreeNode, List<Integer>> knownVertexLists, List<Integer> vertexList) {
        vertexList.add(vertex);
    }

    @Override
    public int getVertex() {
        return vertex;
    }

    @Override
    public boolean allVerticesWillBeRemove(Set<Integer> verticesToBeRemoved) {
        return verticesToBeRemoved.contains(vertex);
    }

    @Override
    public String toString() {
        return "LeafNode{" +
                "isRoot=" + isRoot() +
                ", vertex=" + vertex +
                '}';
    }
}
