package org.anhu.gameStructures.dynamicGameStructures.nodeStructures;

import org.anhu.commons.ListSetPair;
import org.anhu.gameStructures.dynamicGameStructures.DAG;
import org.anhu.gameStructures.dynamicGameStructures.SCCTreeNodeEdge;
import org.anhu.gameStructures.dynamicGameStructures.Unreachables;
import org.anhu.gameStructures.dynamicGameStructures.VertexEdge;

import java.util.*;

abstract class BaseNode implements SCCTreeNode{

    private SCCTreeNode parent = null;

    @Override
    public DAG getDAG() {

        throw new RuntimeException("method should not be called on " + this);
    }

    @Override
    public boolean isRoot() {
        return parent == null;
    }

    @Override
    public boolean isLeafNode() {
        return false;
    }

    @Override
    public boolean isSplitNode() {
        return false;
    }

    @Override
    public boolean isInnerNode() {
        return false;
    }

    @Override
    public void setParent(SCCTreeNode parent) {
        this.parent = parent;
    }

    @Override
    public void addChild(SCCTreeNode child) {

        throw new RuntimeException("method should not be called on " + this);
    }

    @Override
    public void addEdge(SCCTreeNodeEdge edge) {

        throw new RuntimeException("method should not be called on " + this);
    }

    @Override
    public SCCTreeNodeEdge removeEdge(VertexEdge edge) {
        throw new RuntimeException("method should not be called on " + this);
    }

    @Override
    public SCCTreeNode getParent() {
        return parent;
    }

    @Override
    public SCCTreeNode getSplitNode() {
        throw new RuntimeException("method should not be called on " + this);
    }

    @Override
    public int getNrOfChildren() {
        return 0;
    }

    @Override
    public void removeUnreachables(Unreachables unreachables){

        throw new RuntimeException("method should not be called on " + this);
    }

    @Override
    public void replaceInnerNodeChildByLeafNode(SCCTreeNode node, SCCTreeNode replacementNode){
        throw new RuntimeException("method should not be called on " + this);
    }

    @Override
    public void correctEdges(Map<Integer, SCCTreeNode> vertexToChild) {
        throw new RuntimeException("method should not be called on " + this);
    }

    @Override
    public Iterator<SCCTreeNode> getChildren(){
        throw new RuntimeException("method should not be called on " + this);
    }

    @Override
    public Iterator<SCCTreeNodeEdge> getEdges(){
        throw new RuntimeException("method should not be called on " + this);
    }

    @Override
    public void removeEdgesOfVertices(Set<Integer> verticesToBeRemoved, ListSetPair<SCCTreeNode> endpoints) {
        throw new RuntimeException("method should not be called on " + this);
    }
}
