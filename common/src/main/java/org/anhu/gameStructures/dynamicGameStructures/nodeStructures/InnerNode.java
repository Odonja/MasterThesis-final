package org.anhu.gameStructures.dynamicGameStructures.nodeStructures;

import org.anhu.commons.ListSetPair;
import org.anhu.gameStructures.dynamicGameStructures.*;

import java.util.*;

public class InnerNode extends BaseNode {

    private final SCCTreeNode splitNode;
    private List<SCCTreeNode> children; // sorted by their split node or vertex
    private List<SCCTreeNodeEdge> edges;

    public InnerNode(SCCTreeNode splitNode) {
        children = new LinkedList<>();

        this.splitNode = splitNode;
        children.add(splitNode);
        splitNode.setParent(this);

        edges = new LinkedList<>();
    }

    @Override
    public DAG getDAG() {
        DAG graph = new DAG(children.size(), edges.size());
        for(SCCTreeNode child : children){
            graph.outgoingEdges.put(child, new ArrayList<>());
            graph.incomingEdges.put(child, new ArrayList<>());
            graph.outgoingEdgesCount.put(child, 0);
            graph.incomingEdgesCount.put(child, 0);
        }

        for(SCCTreeNodeEdge sccEdge : edges){
            graph.outgoingEdges.get(sccEdge.sourceNode).add(sccEdge);
            graph.incomingEdges.get(sccEdge.targetNode).add(sccEdge);

            int oldcount;
            oldcount = graph.outgoingEdgesCount.get(sccEdge.sourceNode);
            graph.outgoingEdgesCount.put(sccEdge.sourceNode, oldcount+1);
            oldcount = graph.incomingEdgesCount.get(sccEdge.targetNode);
            graph.incomingEdgesCount.put(sccEdge.targetNode, oldcount+1);
        }

        graph.outgoingEdges.put(splitNode, new ArrayList<>());
        graph.incomingEdges.put(splitNode, new ArrayList<>());

        graph.outgoingEdgesCount.put(splitNode, 0);
        graph.incomingEdgesCount.put(splitNode, 0);

        return graph;
    }


    @Override
    public boolean isInnerNode() {
        return true;
    }

    @Override
    public void addChild(SCCTreeNode child) {
        children.add(child);
        child.setParent(this);
    }

    @Override
    public void addEdge(SCCTreeNodeEdge edge) {
        edges.add(edge);
    }

    @Override
    public void getAllVertices(List<Integer> vertexList) {
        Stack<SCCTreeNode> toBeProcessed = new Stack<>();
        toBeProcessed.push(this);

        while (!toBeProcessed.isEmpty()){
            SCCTreeNode node = toBeProcessed.pop();
            for(Iterator<SCCTreeNode> i = node.getChildren(); i.hasNext();){
                SCCTreeNode child = i.next();
                if(child.isInnerNode()){
                    toBeProcessed.push(child);
                }else{
                    vertexList.add(child.getVertex());
                }
            }
        }
    }

    @Override
    public void getAllVerticesAndUpdateKnowns(Map<SCCTreeNode, List<Integer>> knownVertexLists, List<Integer> vertexList) {
        Stack<SCCTreeNode> toBeProcessed = new Stack<>();
        toBeProcessed.push(this);

        while (!toBeProcessed.isEmpty()){
            SCCTreeNode node = toBeProcessed.pop();
            for(Iterator<SCCTreeNode> i = node.getChildren(); i.hasNext();){
                SCCTreeNode child = i.next();
                if(child.isInnerNode()){
                    if(knownVertexLists.containsKey(child)){
                        vertexList.addAll(knownVertexLists.remove(child));
                    }else {
                        toBeProcessed.push(child);
                    }
                }else{
                    vertexList.add(child.getVertex());
                }
            }
        }
        knownVertexLists.put(this, vertexList);
    }

    @Override
    public SCCTreeNodeEdge removeEdge(VertexEdge vertexEdge) {
        SCCTreeNodeEdge sccEdge = null;
        for(SCCTreeNodeEdge edge : edges){
            if(edge.sourceVertex == vertexEdge.sourceVertex && edge.targetVertex == vertexEdge.targetVertex){
                sccEdge = edge;
                break;
            }
        }
        if(sccEdge != null) {
            edges.remove(sccEdge);
        }else{
            throw new RuntimeException("wanted to remove an edge that wasnt there");
        }
        return sccEdge;
    }

    @Override
    public void removeEdgesOfVertices(Set<Integer> verticesToBeRemoved, ListSetPair<SCCTreeNode> endpoints) {
        List<SCCTreeNodeEdge> oldListOfEdges = edges;
        edges = new LinkedList<>();
        for(SCCTreeNodeEdge edge : oldListOfEdges){
            if(verticesToBeRemoved.contains(edge.sourceVertex) || verticesToBeRemoved.contains(edge.targetVertex)){
                endpoints.addIfNotPresent(edge.sourceNode);
                endpoints.addIfNotPresent(edge.targetNode);
            }else{
                edges.add(edge);
            }
        }
    }

    @Override
    public SCCTreeNode getSplitNode() {
        return splitNode;
    }

    @Override
    public int getNrOfChildren() {
        return children.size();
    }

    @Override
    public int getVertex() {
        return splitNode.getVertex();
    }

    @Override
    public void removeUnreachables(Unreachables unreachables){
        List<SCCTreeNode> oldListOfChildren = children;
        children = new LinkedList<>();
        for(SCCTreeNode child : oldListOfChildren){
            if(!unreachables.unreachableVertices.contains(child)){
                children.add(child);
            }
        }

        List<SCCTreeNodeEdge> oldListOfEdges = edges;
        edges = new LinkedList<>();
        for(SCCTreeNodeEdge edge : oldListOfEdges){
            if(!unreachables.incidentEdges.contains(edge)){
                edges.add(edge);
            }
        }
    }

    @Override
    public void replaceInnerNodeChildByLeafNode(SCCTreeNode innerNode, SCCTreeNode leafNode){
        children.remove(innerNode);
        children.add(leafNode);
        leafNode.setParent(this);

        for(SCCTreeNodeEdge edge : edges){
            if(edge.sourceVertex == innerNode.getVertex()){
                edge.sourceNode = leafNode;
            }else if(edge.targetVertex == innerNode.getVertex()){
                edge.targetNode = leafNode;
            }
        }
    }

    @Override
    public void correctEdges(Map<Integer, SCCTreeNode> vertexToChild) {
        for(SCCTreeNodeEdge edge :edges ){
            if(vertexToChild.containsKey(edge.sourceVertex)){
                edge.sourceNode = vertexToChild.get(edge.sourceVertex);
            }
            if(vertexToChild.containsKey(edge.targetVertex)){
                edge.targetNode = vertexToChild.get(edge.targetVertex);
            }
        }
    }

    @Override
    public Iterator<SCCTreeNode> getChildren(){
        return children.iterator();
    }

    @Override
    public Iterator<SCCTreeNodeEdge> getEdges(){
        return edges.iterator();
    }

    @Override
    public boolean allVerticesWillBeRemove(Set<Integer> verticesToBeRemoved) {
        Stack<SCCTreeNode> toBeProcessed = new Stack<>();
        toBeProcessed.push(this);

        while (!toBeProcessed.isEmpty()){
            SCCTreeNode node = toBeProcessed.pop();
            for(Iterator<SCCTreeNode> i = node.getChildren(); i.hasNext();){
                SCCTreeNode child = i.next();
                if(child.isInnerNode()){
                    toBeProcessed.push(child);
                }else if(!child.allVerticesWillBeRemove(verticesToBeRemoved)){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "InnerNode{" +
                "isRoot=" + isRoot() +
                ", splitNode=" + splitNode.getVertex() +
                ", children=" + children +
                ", edges=" + edges +
                '}';
    }
}
