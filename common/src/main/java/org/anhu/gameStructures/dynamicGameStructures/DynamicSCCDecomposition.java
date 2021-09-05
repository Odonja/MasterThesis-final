package org.anhu.gameStructures.dynamicGameStructures;

import org.anhu.gameStructures.ParityGame;
import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.SCCTreeNode;

import java.util.*;

public class DynamicSCCDecomposition {

    private final HashMap<Integer, Set<Integer>> outEdgesBetweenTrees;
    private final Map<Integer, SCCTreeNode> sccTrees;
    private final Map<VertexEdge, SCCTreeNode> edgesStoragePlace;
    private final Map<Integer, SCCTreeNode> vertexToRoot;

    public DynamicSCCDecomposition(){
        outEdgesBetweenTrees = new HashMap<>();

        sccTrees = new HashMap<>();
        edgesStoragePlace = new HashMap<>();
        vertexToRoot = new HashMap<>();
    }

    public void addNode(SCCTreeNode node){
        sccTrees.put(node.getVertex(), node);
    }

    public void replaceInnerNodeTreeByLeafTree(SCCTreeNode innerNode, SCCTreeNode leafNode){
        sccTrees.remove(innerNode.getVertex());
        sccTrees.put(leafNode.getVertex(), leafNode);
    }

    private void removeEdgesBetweenTreesForVertex(ParityGame game, int vertex) {
        outEdgesBetweenTrees.remove(vertex);
        List<Integer> inEdges = game.getIncomingEdges(vertex);
        for(int source : inEdges){
            if(outEdgesBetweenTrees.containsKey(source)) {
                if(outEdgesBetweenTrees.get(source).remove(vertex)) {
                    if (outEdgesBetweenTrees.get(source).isEmpty()) {
                        outEdgesBetweenTrees.remove(source);
                    }
                }
            }
        }


    }

    public void removeTree(ParityGame game, SCCTreeNode node){
        if(!sccTrees.containsKey(node.getVertex())){
            throw new RuntimeException("removing a node that does not exist");
        }
        removeNodeAndVertexToRoot(node);
        removeEdgeStoragePlaceForEdgesInNode(node);
        removeEdgesBetweenTreesOfTree(game, node);
    }

    private void removeEdgesBetweenTreesOfTree(ParityGame game, SCCTreeNode node) {
        if(node.isLeafNode()){
            removeEdgesBetweenTreesForVertex(game, node.getVertex());
            return;
        }
        Stack<SCCTreeNode> work = new Stack<>();
        work.push(node);
        while (!work.isEmpty()){
            SCCTreeNode currentNode = work.pop();
            for (Iterator<SCCTreeNode> iter = currentNode.getChildren(); iter.hasNext(); ) {
                SCCTreeNode child = iter.next();
                if(child.isLeafNode()){
                    removeEdgesBetweenTreesForVertex(game, child.getVertex());
                }else{
                    work.push(child);
                }
            }
        }
    }

    private void removeEdgeStoragePlaceForEdgesInNode(SCCTreeNode node) {
        if(node.isInnerNode()) {
            Stack<SCCTreeNode> stack = new Stack<>();
            stack.push(node);
            while (!stack.isEmpty()){
                SCCTreeNode innerNode = stack.pop();
                for(Iterator<SCCTreeNodeEdge> it = innerNode.getEdges(); it.hasNext(); ){
                    SCCTreeNodeEdge edge = it.next();
                    removeEdgeStoragePlace(new VertexEdge(edge.sourceVertex, edge.targetVertex));
                }
                for(Iterator<SCCTreeNode> it = innerNode.getChildren(); it.hasNext(); ){
                    SCCTreeNode child = it.next();
                    if(child.isInnerNode()){
                        stack.push(child);
                    }
                }
            }
        }
    }

    private void removeNodeAndVertexToRoot(SCCTreeNode node) {
        sccTrees.remove(node.getVertex());
        List <Integer> affectedVertices = new ArrayList<>();
        node.getAllVertices(affectedVertices);
        for(int vertex : affectedVertices){
            vertexToRoot.remove(vertex);
        }
    }


    public void addEdge(VertexEdge edge) {
        if(!outEdgesBetweenTrees.containsKey(edge.sourceVertex)){
            outEdgesBetweenTrees.put(edge.sourceVertex, new HashSet<>());
        }
        outEdgesBetweenTrees.get(edge.sourceVertex).add(edge.targetVertex);
    }

    public void addRootVertices(SCCTreeNode root, List<Integer> vertices){
        for(int vertex: vertices){
            vertexToRoot.put(vertex, root);
        }
    }

    public void addRootVertices(SCCTreeNode root, int vertex){
        vertexToRoot.put(vertex, root);
    }

    public void addEdgeStoragePlace(SCCTreeNode storagePlace, VertexEdge edge){
        edgesStoragePlace.put(edge, storagePlace);
    }

    public SCCTreeNode getEdgeStoragePlace(VertexEdge edge){
        return edgesStoragePlace.get(edge);
    }

    public void removeEdgeStoragePlace(VertexEdge edge){
        edgesStoragePlace.remove(edge);
    }

    public boolean isEdgeBetweenTrees(VertexEdge edge){
        if(!outEdgesBetweenTrees.containsKey(edge.sourceVertex)){
            return false;
        }
        return outEdgesBetweenTrees.get(edge.sourceVertex).contains(edge.targetVertex);
    }

    public void removeEdgeBetweenTrees(VertexEdge edge){
        outEdgesBetweenTrees.get(edge.sourceVertex).remove(edge.targetVertex);
        if(outEdgesBetweenTrees.get(edge.sourceVertex).isEmpty()){
            outEdgesBetweenTrees.remove(edge.sourceVertex);
        }
    }

    @Override
    public String toString() {
        return "DynamicSCCDecomposition{" +
                "\nedgesBetweenTrees=" + outEdgesBetweenTrees +
                ", \nsccTrees=" + sccTrees +
                ", \nedgesStoragePlace=" + edgesStoragePlaceToString() +
                ", \nvertexToRoot=" + vertexToRootToString() +
                '}';
    }

    public SCCTreeNode getRootOfVertex(int vertex){
        return vertexToRoot.get(vertex);
    }

    private String edgesStoragePlaceToString(){
        StringBuilder result = new StringBuilder("{");
        for(VertexEdge edge : edgesStoragePlace.keySet()){
            SCCTreeNode storagePlace = edgesStoragePlace.get(edge);
            result.append(edge).append("=").append(nodeType(storagePlace)).append("(").append(storagePlace.getVertex()).append("), ");
        }
        result.append("}");
        return result.toString();
    }

    private String vertexToRootToString(){
        StringBuilder result = new StringBuilder("{");
        for(int vertex : vertexToRoot.keySet()){
            SCCTreeNode root = vertexToRoot.get(vertex);
            result.append(vertex).append("=").append(nodeType(root)).append("(").append(root.getVertex()).append("), ");
        }
        result.append("}");
        return result.toString();
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

    public boolean isEmpty() {
        return sccTrees.isEmpty();
    }

    public SCCTreeNode getFinalSCCTree() {
        for(SCCTreeNode tree : sccTrees.values()){
            if(tree.isLeafNode() && !outEdgesBetweenTrees.containsKey(tree.getVertex())){
                return tree;
            }

            if(tree.isInnerNode()){
                boolean isFinalSCCTree = true;
                Stack<SCCTreeNode> stack = new Stack<>();
                stack.push(tree);
                while (isFinalSCCTree && !stack.isEmpty()){
                    SCCTreeNode node = stack.pop();
                    for(Iterator<SCCTreeNode> it = node.getChildren(); it.hasNext(); ){
                        SCCTreeNode child = it.next();
                        if(child.isInnerNode()){
                            stack.push(child);
                        }else if(outEdgesBetweenTrees.containsKey(child.getVertex())){
                            isFinalSCCTree = false;
                            break;
                        }
                    }
                }
                if(isFinalSCCTree){
                    return tree;
                }
            }
        }
        // should not reach here
        throw new RuntimeException("getFinalSCCTree did not find a final SCCTree: \n" + this);
    }

    public void getGameState(boolean[] gameState) {
        List<Integer> allVertices = new ArrayList<>();
        for(int key : sccTrees.keySet()){
            sccTrees.get(key).getAllVertices(allVertices);
        }
        for(int vertex : allVertices){
            gameState[vertex] = true;
        }
    }

    public List<SCCTreeNode> getAllFinalSCCTrees() {
        List<SCCTreeNode> finalSCCTrees = new ArrayList<>();
        for(SCCTreeNode tree : sccTrees.values()){
            if(tree.isLeafNode() && !outEdgesBetweenTrees.containsKey(tree.getVertex())){
                finalSCCTrees.add(tree);
            }

            if(tree.isInnerNode()){
                boolean isFinalSCCTree = true;
                Stack<SCCTreeNode> stack = new Stack<>();
                stack.push(tree);
                while (isFinalSCCTree && !stack.isEmpty()){
                    SCCTreeNode node = stack.pop();
                    for(Iterator<SCCTreeNode> it = node.getChildren(); it.hasNext(); ){
                        SCCTreeNode child = it.next();
                        if(child.isInnerNode()){
                            stack.push(child);
                        }else if(outEdgesBetweenTrees.containsKey(child.getVertex())){
                            isFinalSCCTree = false;
                            break;
                        }
                    }
                }
                if(isFinalSCCTree){
                    finalSCCTrees.add(tree);
                }
            }
        }
        if(finalSCCTrees.isEmpty()) {
            // should not reach here
            throw new RuntimeException("getFinalSCCTree did not find a final SCCTree: \n" + this);
        }
        return finalSCCTrees;
    }
}
