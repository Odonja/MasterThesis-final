package org.anhu.gameStructures.dynamicGameStructures;

import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.SCCTreeNode;

import java.util.Objects;

public class SCCTreeNodeEdge {

    public SCCTreeNode sourceNode;
    public SCCTreeNode targetNode;
    public final int sourceVertex;
    public final int targetVertex;

    public SCCTreeNodeEdge(SCCTreeNode sourceNode, SCCTreeNode toNode, int sourceVertex, int targetVertex) {
        this.sourceNode = sourceNode;
        this.targetNode = toNode;
        this.sourceVertex = sourceVertex;
        this.targetVertex = targetVertex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SCCTreeNodeEdge edge = (SCCTreeNodeEdge) o;
        return sourceVertex == edge.sourceVertex && targetVertex == edge.targetVertex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceVertex, targetVertex);
    }

    @Override
    public String toString() {
        return "SCCTreeNodeEdge{" +
                "sourceVertex=" + sourceVertex +
                ", targetVertex=" + targetVertex +
                ", sourceNode=" + nodeType(sourceNode) + "(" + sourceNode.getVertex() + ")" +
                ", targetNode=" + nodeType(targetNode) + "(" + targetNode.getVertex() + ")" +
                '}';
    }

    private String nodeType(SCCTreeNode node){
        if(node.isSplitNode()){
            return "splitNode";
        }else if(node.isLeafNode()){
            return "leafNode";
        }else if(node.isInnerNode()){
            return  "innerNode";
        }
        return "blaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaNode";
    }
}
