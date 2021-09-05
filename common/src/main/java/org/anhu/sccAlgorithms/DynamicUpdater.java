package org.anhu.sccAlgorithms;

import org.anhu.commons.ListSetPair;
import org.anhu.gameStructures.dynamicGameStructures.*;
import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.LeafNode;
import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.SCCTreeNode;

import java.util.*;

public class DynamicUpdater {

    public void deleteEdge(DynamicSCCDecomposition decomposition, VertexEdge edge){
        // if the edge is not part of an SCC tree then no SCC tree will change
        if(decomposition.isEdgeBetweenTrees(edge)){
            decomposition.removeEdgeBetweenTrees(edge);
            return;
        }
        SCCTreeNode node = decomposition.getEdgeStoragePlace(edge);
        if(node == null){
            // probably self loop
            if(edge.sourceVertex == edge.targetVertex){
                return;
            }
            throw new RuntimeException("deleting an edge that does not exist: \n" + edge + "\n" +  decomposition);
        }
        SCCTreeNodeEdge nodeEdge = node.removeEdge(edge);
        decomposition.removeEdgeStoragePlace(edge);

        List<SCCTreeNode> alteredVertices = new ArrayList<>();
        alteredVertices.add(nodeEdge.sourceNode);
        alteredVertices.add(nodeEdge.targetNode);
        updateSCCtree(decomposition, node, alteredVertices);
    }

    private void updateSCCtree(DynamicSCCDecomposition decomposition, SCCTreeNode initialNode, List<SCCTreeNode> initialAlteredVertices) {
        SCCTreeNode node = initialNode;
        ListSetPair<SCCTreeNode> alteredVertices =  new ListSetPair<>(initialAlteredVertices);
        Map<SCCTreeNode, List<Integer>> knownVertexLists = new HashMap<>();

        boolean updatesInTreeAreStillNeeded = true;
        while (updatesInTreeAreStillNeeded) {
            ListSetPair<SCCTreeNode> alteredVerticesForParent = new ListSetPair<>();
            updateNode(decomposition, node, alteredVertices, alteredVerticesForParent, knownVertexLists);
            if(alteredVerticesForParent.list.isEmpty()){
                updatesInTreeAreStillNeeded = false;
            }else{
                node = node.getParent();
                alteredVertices = alteredVerticesForParent;
            }
        }
    }


    public Unreachables findUnreachable(DAG graph, ListSetPair<SCCTreeNode> sourcesAndSinks){
        Unreachables result = new Unreachables(graph.nrOfVertices);
        findUnreachableDown(graph, sourcesAndSinks, result);
        findUnreachableUp(graph, sourcesAndSinks, result);
        return result;
    }

    private void findUnreachableDown(DAG graph, ListSetPair<SCCTreeNode> sources, Unreachables unreachables) {
        Stack<SCCTreeNode> queue = new Stack<>();
        // add all sources that are not the split node
        for(SCCTreeNode node : sources.list){
            if(!node.isSplitNode() && graph.incomingEdgesCount.get(node) == 0){
                queue.push(node);
            }
        }

        while(!queue.isEmpty()){
            SCCTreeNode node = queue.pop();
            unreachables.unreachableVertices.addIfNotPresent(node);
            // no need to check incomming edges as anything in the queue does not have any
            for(SCCTreeNodeEdge outEdge : graph.outgoingEdges.get(node)){
                // add to I
                unreachables.incidentEdges.addIfNotPresent(outEdge);
                // remove the edge by decrementing the count for the target vertex
                int nrOfIncommingEdges = graph.incomingEdgesCount.get(outEdge.targetNode);
                graph.incomingEdgesCount.put(outEdge.targetNode, nrOfIncommingEdges-1);

                if(nrOfIncommingEdges <= 1){ // which means it is 0 now after decrementing
                    // so it has become a sink after edge removal
                    queue.push( outEdge.targetNode);
                }
            }
        }
    }

    private void findUnreachableUp(DAG graph, ListSetPair<SCCTreeNode> sinks, Unreachables unreachables) {
        Stack<SCCTreeNode> queue = new Stack<>();

        // add all sinks that are not the split node
        for(SCCTreeNode node : sinks.list){
            if(!node.isSplitNode() && graph.outgoingEdgesCount.get(node) == 0){
                queue.push(node);

            }
        }

        while(!queue.isEmpty()){
            SCCTreeNode node = queue.pop();
            unreachables.unreachableVertices.addIfNotPresent(node);

            // no need to check outgoing edges as anything in the queue does not have any
            for(SCCTreeNodeEdge inEdge : graph.incomingEdges.get(node)){
                // add to I
                unreachables.incidentEdges.addIfNotPresent(inEdge);
                // remove the edge by decrementing the count for the target vertex
                int nrOfOutgoingEdges = graph.outgoingEdgesCount.get(inEdge.sourceNode);
                graph.outgoingEdgesCount.put(inEdge.sourceNode, nrOfOutgoingEdges-1);

                if(nrOfOutgoingEdges <= 1){ // which means it is 0 now after decrementing
                    // so it has become a sink after edge removal
                    queue.push( inEdge.sourceNode);
                }
            }
        }
    }

    public void updateNode(DynamicSCCDecomposition decomposition, SCCTreeNode node, ListSetPair<SCCTreeNode> alteredVertices,
                           ListSetPair<SCCTreeNode> changesMadeToParent, Map<SCCTreeNode, List<Integer>> knownVertexLists) {
        DAG dag = node.getDAG();
        Unreachables unreachables = findUnreachable(dag, alteredVertices);
        if (unreachables.unreachableVertices.list.isEmpty()) {
            return;
        }
        SCCTreeNode splitNode = node.getSplitNode();

        unreachables.unreachableVertices.removeIfPresent(splitNode);
        node.removeUnreachables(unreachables);


        Map<Integer, SCCTreeNode> vertexToChild = new HashMap<>();
        SCCTreeNode updatedNode;
        if (node.getNrOfChildren() == 1) { // if the splitnode is the only child left
            SCCTreeNode replacementNode = new LeafNode(node.getVertex());
            if (node.isRoot()) {
                decomposition.replaceInnerNodeTreeByLeafTree(node, replacementNode);
                decomposition.addRootVertices(replacementNode, replacementNode.getVertex());
            } else {
                node.getParent().replaceInnerNodeChildByLeafNode(node, replacementNode);
                vertexToChild.put(replacementNode.getVertex(), replacementNode);
            }
            updatedNode = replacementNode;
        } else {
            updatedNode = node;
        }

        if (node.isRoot()) {
            for (SCCTreeNode independentTree : unreachables.unreachableVertices.list) {
                independentTree.setParent(null);
                decomposition.addNode(independentTree);
                List<Integer> vertices = knownVertexLists.get(independentTree);
                if(vertices == null){
                    vertices = new ArrayList<>();
                    independentTree.getAllVerticesAndUpdateKnowns(knownVertexLists, vertices);
                }


                decomposition.addRootVertices(independentTree, vertices);
                for (SCCTreeNodeEdge edgesBetweenIndependentTrees : unreachables.incidentEdges.list) {
                    decomposition.removeEdgeStoragePlace(new VertexEdge(edgesBetweenIndependentTrees.sourceVertex,
                            edgesBetweenIndependentTrees.targetVertex)); // it is no longer stored in a node
                    decomposition.addEdge(new VertexEdge(edgesBetweenIndependentTrees.sourceVertex,
                            edgesBetweenIndependentTrees.targetVertex)); // it is now an edge between trees
                }
            }
        } else {
            SCCTreeNode parent = node.getParent();
            for (SCCTreeNode child : unreachables.unreachableVertices.list) {
                parent.addChild(child); // it also sets the parent of the child
                List<Integer> storedVertices = knownVertexLists.get(child);
                if(storedVertices == null){
                    storedVertices = new ArrayList<>();
                    child.getAllVerticesAndUpdateKnowns(knownVertexLists, storedVertices);
                }

                for (int vertex : storedVertices) {
                    vertexToChild.put(vertex, child);
                }
            }
            for (SCCTreeNodeEdge edge : unreachables.incidentEdges.list) {
                if(!vertexToChild.containsKey(edge.sourceVertex)){
                    edge.sourceNode = updatedNode;
                }
                if(!vertexToChild.containsKey(edge.targetVertex)){
                    edge.targetNode = updatedNode;
                }
                parent.addEdge(edge);
                VertexEdge vertexEdge = new VertexEdge(edge.sourceVertex, edge.targetVertex);
                decomposition.removeEdgeStoragePlace(vertexEdge);
                decomposition.addEdgeStoragePlace(parent, vertexEdge);
            }
            parent.correctEdges(vertexToChild);

            changesMadeToParent.addAll(unreachables.unreachableVertices.list);
            changesMadeToParent.addIfNotPresent(updatedNode);
        }
    }
}
