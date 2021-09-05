package org.anhu.gameStructures.dynamicGameStructures.nodeStructures;

import org.anhu.commons.ListSetPair;
import org.anhu.gameStructures.dynamicGameStructures.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SCCTreeNode {

    /*
    returns DAG in case of an internal node, null otherwise
     */
    DAG getDAG();
    boolean isLeafNode();
    boolean isRoot();
    boolean isSplitNode();
    boolean isInnerNode();
    void setParent(SCCTreeNode parent);
    void addChild(SCCTreeNode child);
    void addEdge(SCCTreeNodeEdge edge);
    void getAllVertices(List<Integer> vertexList);
    void getAllVerticesAndUpdateKnowns(Map<SCCTreeNode, List<Integer>> knownVertexLists, List<Integer> vertexList);
    SCCTreeNodeEdge removeEdge(VertexEdge edge);

    SCCTreeNode getParent();
    SCCTreeNode getSplitNode();
    int getNrOfChildren();

    /*
    in case of a leaf or split node returns the vertex it represents, in case of an internal node it returns the value of its split node
     */
    int getVertex();

    void removeUnreachables(Unreachables unreachables);

    void replaceInnerNodeChildByLeafNode(SCCTreeNode node, SCCTreeNode replacementNode);

    void correctEdges(Map<Integer, SCCTreeNode> vertexToChild);

    Iterator<SCCTreeNode> getChildren();
    Iterator<SCCTreeNodeEdge> getEdges();

    boolean allVerticesWillBeRemove(Set<Integer> verticesToBeRemoved);

    void removeEdgesOfVertices(Set<Integer> verticesToBeRemoved, ListSetPair<SCCTreeNode> endpoints);

}
