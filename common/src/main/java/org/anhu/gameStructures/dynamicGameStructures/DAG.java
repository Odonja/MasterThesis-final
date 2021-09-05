package org.anhu.gameStructures.dynamicGameStructures;

import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.SCCTreeNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DAG {

    public final int nrOfVertices;
    public final int nrOfEdges;
    public final Map<SCCTreeNode, List<SCCTreeNodeEdge>> outgoingEdges;
    public final Map<SCCTreeNode, List<SCCTreeNodeEdge>> incomingEdges;
    public final Map<SCCTreeNode, Integer> outgoingEdgesCount;
    public final Map<SCCTreeNode, Integer> incomingEdgesCount;

    public DAG(int nrOfVertices, int nrfEdges){
        this.nrOfVertices = nrOfVertices;
        this.nrOfEdges = nrfEdges;
        outgoingEdges = new HashMap<>(2*nrOfVertices);
        incomingEdges = new HashMap<>(2*nrOfVertices);
        outgoingEdgesCount = new HashMap<>(2*nrOfVertices);
        incomingEdgesCount = new HashMap<>(2*nrOfVertices);

    }

    @Override
    public String toString() {
        return "DAG{" +
                "nrOfVertices=" + nrOfVertices +
                ", nrOfEdges=" + nrOfEdges +
                ", outgoingEdges=" + outgoingEdges +
                ", incommingEdges=" + incomingEdges +
                ", outgoingEdgesCount=" + outgoingEdgesCount +
                ", incommingEdgesCount=" + incomingEdgesCount +
                '}';
    }
}
