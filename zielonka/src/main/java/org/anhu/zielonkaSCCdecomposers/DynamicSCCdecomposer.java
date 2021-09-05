package org.anhu.zielonkaSCCdecomposers;

import org.anhu.commons.ListSetPair;
import org.anhu.gameStructures.ParityGame;
import org.anhu.gameStructures.dynamicGameStructures.DynamicSCCDecomposition;
import org.anhu.gameStructures.dynamicGameStructures.SCCTreeNodeEdge;
import org.anhu.gameStructures.dynamicGameStructures.VertexEdge;
import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.InnerNode;
import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.LeafNode;
import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.SCCTreeNode;
import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.SplitNode;
import org.anhu.sccAlgorithms.DynamicDecomposer;
import org.anhu.sccAlgorithms.DynamicUpdater;
import org.anhu.statistics.Statistic;

import java.util.*;

public class DynamicSCCdecomposer{

    private final DynamicUpdater updater;

    public DynamicSCCdecomposer(){
        updater = new DynamicUpdater();
    }

    public DynamicSCCDecomposition decompose(ParityGame game) {
        boolean[] fullGame = new boolean[game.getNrOfVertices()];
        Arrays.fill(fullGame, Boolean.TRUE);
        return new DynamicDecomposer().decompose(game, fullGame, game.getNrOfVertices());
    }

    public void deleteVertices(ParityGame game, DynamicSCCDecomposition decomposition, Set<Integer> verticesToBeRemoved){
        List<Integer> verticesToBeRemovedList = new ArrayList<>(verticesToBeRemoved);
        for(int vertex : verticesToBeRemovedList){
            if(verticesToBeRemoved.contains(vertex)){
                SCCTreeNode root =  decomposition.getRootOfVertex(vertex);
                if(root.allVerticesWillBeRemove(verticesToBeRemoved)){
                    List<Integer> rootVertices = new ArrayList<>();
                    root.getAllVertices(rootVertices);
                    decomposition.removeTree(game, root);
                    for (int removedVertex : rootVertices) {
                        verticesToBeRemoved.remove(removedVertex);
                    }
                }else{
                    removeVerticesFromTree(game, decomposition, root, verticesToBeRemoved);
                }
            }
        }
    }

    private void findAndDeleteEdgesOfVerticesRoBeRemoved(Set<Integer> verticesToBeRemoved, NodeToBeProcessed nodeToBeProcessed) {
        SCCTreeNode node = nodeToBeProcessed.node;
        node.removeEdgesOfVertices(verticesToBeRemoved, nodeToBeProcessed.changes);
    }

    private void removeVerticesFromTree(ParityGame game, DynamicSCCDecomposition decomposition, SCCTreeNode tree, Set<Integer> verticesToBeRemoved){
        List<Integer> verticesThatCanBeRemoved = new LinkedList<>();
        Queue<NodeToBeProcessed> toBeProcessed = topologicalSortTree(tree, verticesToBeRemoved, verticesThatCanBeRemoved);
        Map<SCCTreeNode, List<Integer>> knownVertexLists = new HashMap<>();
        while (!toBeProcessed.isEmpty()){
            NodeToBeProcessed nodeToBeProcessed = toBeProcessed.poll();
            SCCTreeNode node = nodeToBeProcessed.node;

            findAndDeleteEdgesOfVerticesRoBeRemoved(verticesToBeRemoved, nodeToBeProcessed);

            if(!nodeToBeProcessed.changes.list.isEmpty()){
                if(node.isRoot()){
                    Statistic.registerExecutionTime(Statistic.Event.UPDATENODE, () ->
                            updater.updateNode(decomposition, node, nodeToBeProcessed.changes, null, knownVertexLists));
                }else{
                    Statistic.registerExecutionTime(Statistic.Event.UPDATENODE, () ->
                            updater.updateNode(decomposition, node, nodeToBeProcessed.changes, nodeToBeProcessed.parent.changes, knownVertexLists));
                }
            }
        }
        for(int vertex : verticesThatCanBeRemoved){
            SCCTreeNode root = decomposition.getRootOfVertex(vertex);
            if(root != null){
                decomposition.removeTree(game, root);
            }
            verticesToBeRemoved.remove(vertex);
        }
    }

    /**
     *
     * @param tree the tree from vertices can be removed
     * @param verticesToBeRemoved the vertices we want to remove from tree if they are in there
     * @param verticesThatCanBeRemoved the vertices in verticesToBeRemoved that are present in this tree, will be filled by the function
     * @return a queue containing the ancestors of the leafnodes of vertices that can be removed such that each decendant
     * is removed from the queue before its ancestor and only the ancestors which also have leaf nodes that should not be removed are present
     */
    private Queue<NodeToBeProcessed> topologicalSortTree(SCCTreeNode tree,
                                                         Set<Integer> verticesToBeRemoved,
                                                         List<Integer> verticesThatCanBeRemoved){
        Stack<NodeToBeProcessed> checkRelevance = new Stack<>();
        Queue<NodeToBeProcessed> queue = new LinkedList<>();
        queue.offer(new NodeToBeProcessed(null, tree));
        while(!queue.isEmpty()){
            NodeToBeProcessed nodeToBeProcessed = queue.poll();
            checkRelevance.push(nodeToBeProcessed);
            for(Iterator<SCCTreeNode> it = nodeToBeProcessed.node.getChildren(); it.hasNext();){
                SCCTreeNode child = it.next();
                if(child.isInnerNode()) {
                    queue.offer(new NodeToBeProcessed(nodeToBeProcessed, child));
                }else if(verticesToBeRemoved.contains(child.getVertex())){
                    verticesThatCanBeRemoved.add(child.getVertex());
                    nodeToBeProcessed.relevant = true;
                    nodeToBeProcessed.nrOfRelevantChildren++;
                }
            }
        }
        Queue<NodeToBeProcessed> result = new LinkedList<>();
        while (!checkRelevance.isEmpty()){
            NodeToBeProcessed nodeToBeProcessed = checkRelevance.pop();
            if(nodeToBeProcessed.relevant){
                if(nodeToBeProcessed.nrOfRelevantChildren == nodeToBeProcessed.node.getNrOfChildren()){ // then this whole subtree can be deleted
                    if(nodeToBeProcessed.parent != null) { // if this is not the root then the parent is also relevant
                        nodeToBeProcessed.parent.nrOfRelevantChildren++;
                        nodeToBeProcessed.parent.relevant = true;
                    }
                }else {
                    if (nodeToBeProcessed.parent != null) { // if this is not the root then the parent is also relevant
                        nodeToBeProcessed.parent.relevant = true;
                    }
                    nodeToBeProcessed.changes = new ListSetPair<>();
                    result.offer(nodeToBeProcessed);
                }
            }
        }
        return result;
    }


    private class NodeToBeProcessed{
        public final NodeToBeProcessed parent;
        public final SCCTreeNode node;
        public ListSetPair<SCCTreeNode> changes;
        public boolean relevant;
        public int nrOfRelevantChildren;

        public NodeToBeProcessed(NodeToBeProcessed parent, SCCTreeNode node){
            this.parent = parent;
            this.node = node;
            relevant = false;
            nrOfRelevantChildren = 0;
        }
    }

    public DynamicSCCDecomposition getIndependentDecompositionOfSCCWitoutAttractorset(ParityGame game, SCCTreeNode root, List<Integer> attractorSet) {
        DynamicSCCDecomposition newDecomposition = new DynamicSCCDecomposition();
        fillNewDecompositionWithCopyOfTree(root, newDecomposition);
        Set<Integer> set = new HashSet<>(attractorSet);
        deleteVertices(game, newDecomposition, set);
        return newDecomposition;
    }

    private void fillNewDecompositionWithCopyOfTree(SCCTreeNode root, DynamicSCCDecomposition newDecomposition) {
        if(root.isLeafNode()){
            SCCTreeNode newLeaf = new LeafNode(root.getVertex());
            newDecomposition.addNode(newLeaf);
            newDecomposition.addRootVertices(newLeaf, root.getVertex());
        }else {

            SCCTreeNode newRoot = new InnerNode(new SplitNode(root.getVertex()));

            List<Integer> verticesFoundInSCC = new ArrayList<>();
            root.getAllVertices(verticesFoundInSCC);
            newDecomposition.addNode(newRoot);
            newDecomposition.addRootVertices(newRoot, verticesFoundInSCC);

            Stack<NodeCopy> nodesToBeCopied = new Stack<>();
            nodesToBeCopied.push(new NodeCopy(root, newRoot));

            while (!nodesToBeCopied.isEmpty()) {
                NodeCopy nodeCopy = nodesToBeCopied.pop();
                SCCTreeNode oldNode = nodeCopy.oldNode;
                SCCTreeNode newNode = nodeCopy.newNode;

                HashMap<SCCTreeNode, SCCTreeNode> mapping = new HashMap<>();
                for (Iterator<SCCTreeNode> i = oldNode.getChildren(); i.hasNext(); ) {
                    SCCTreeNode oldChild = i.next();
                    SCCTreeNode newChild = null;
                    if (oldChild.isSplitNode()) {
                        newChild = newNode.getSplitNode();
                    } else if (oldChild.isLeafNode()) {
                        newChild = new LeafNode(oldChild.getVertex());
                    } else if (oldChild.isInnerNode()) {
                        newChild = new InnerNode(new SplitNode(oldChild.getVertex()));
                        nodesToBeCopied.push(new NodeCopy(oldChild, newChild));
                    }
                    mapping.put(oldChild, newChild);
                    if (!oldChild.isSplitNode()) {
                        newNode.addChild(newChild);
                    }
                }

                for (Iterator<SCCTreeNodeEdge> i = oldNode.getEdges(); i.hasNext(); ) {
                    SCCTreeNodeEdge oldEdge = i.next();
                    int sourceVertex = oldEdge.sourceVertex;
                    int targetVertex = oldEdge.targetVertex;
                    SCCTreeNode sourceNode = mapping.get(oldEdge.sourceNode);
                    SCCTreeNode targetNode = mapping.get(oldEdge.targetNode);
                    newNode.addEdge(new SCCTreeNodeEdge(sourceNode, targetNode, sourceVertex, targetVertex));
                    newDecomposition.addEdgeStoragePlace(newNode, new VertexEdge(sourceVertex, targetVertex));
                }

            }
        }
    }

    private class NodeCopy{
        final SCCTreeNode oldNode;
        final SCCTreeNode newNode;

        public NodeCopy(SCCTreeNode oldNode, SCCTreeNode newNode){
            this.oldNode = oldNode;
            this.newNode = newNode;
        }
    }
}
