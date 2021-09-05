package org.anhu.gameStructures.dynamicGameStructures;

import org.anhu.commons.ListSetPair;
import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.SCCTreeNode;

import java.util.HashSet;
import java.util.LinkedList;

public class Unreachables {

    public final ListSetPair<SCCTreeNode> unreachableVertices;
    public final ListSetPair<SCCTreeNodeEdge> incidentEdges;

    public Unreachables(int nrOfVertices){
        unreachableVertices = new ListSetPair<>(new LinkedList<>(), new HashSet<>(nrOfVertices*2));
        incidentEdges = new ListSetPair<>(new LinkedList<>(), new HashSet<>(nrOfVertices*2));
    }
}
