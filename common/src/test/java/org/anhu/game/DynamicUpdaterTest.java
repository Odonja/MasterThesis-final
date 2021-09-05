package org.anhu.game;

import org.anhu.commons.ListSetPair;
import org.anhu.gameStructures.ParityGame;
import org.anhu.gameStructures.dynamicGameStructures.*;
import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.InnerNode;
import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.LeafNode;
import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.SCCTreeNode;
import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.SplitNode;
import org.anhu.reader.InputGameReader;
import org.anhu.sccAlgorithms.DynamicDecomposer;
import org.anhu.sccAlgorithms.DynamicUpdater;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicUpdaterTest {

    // graph split -> inner -> leaf -> split
    @Test
    void dynamicUpdater_noUnreachables() {
        DAG graph = new DAG(3, 3);
        SCCTreeNode splitnode = new SplitNode(1);
        SCCTreeNode innerNode = new InnerNode(new SplitNode(3));
        SCCTreeNode leafNode = new LeafNode(2);
        graph.incomingEdges.put(splitnode, new ArrayList<>());
        graph.incomingEdges.put(leafNode, new ArrayList<>());
        graph.incomingEdges.put(innerNode, new ArrayList<>());
        graph.outgoingEdges.put(splitnode, new ArrayList<>());
        graph.outgoingEdges.put(leafNode, new ArrayList<>());
        graph.outgoingEdges.put(innerNode, new ArrayList<>());
        graph.incomingEdgesCount.put(splitnode, 1);
        graph.incomingEdgesCount.put(leafNode, 1);
        graph.incomingEdgesCount.put(innerNode, 1);
        graph.outgoingEdgesCount.put(splitnode, 1);
        graph.outgoingEdgesCount.put(leafNode, 1);
        graph.outgoingEdgesCount.put(innerNode, 1);

        graph.incomingEdges.get(splitnode).add(new SCCTreeNodeEdge(leafNode, splitnode, 2, 1));
        graph.incomingEdges.get(leafNode).add(new SCCTreeNodeEdge(innerNode, leafNode, 3, 2));
        graph.incomingEdges.get(innerNode).add(new SCCTreeNodeEdge(splitnode, innerNode, 1, 3));

        graph.outgoingEdges.get(splitnode).add(new SCCTreeNodeEdge(splitnode, innerNode, 1, 3));
        graph.outgoingEdges.get(leafNode).add(new SCCTreeNodeEdge(leafNode, splitnode, 2, 1));
        graph.outgoingEdges.get(innerNode).add(new SCCTreeNodeEdge(innerNode, leafNode, 3, 2));

        ListSetPair<SCCTreeNode> ss = new ListSetPair<>();
        ss.addIfNotPresent(innerNode);
        Unreachables ui = new DynamicUpdater().findUnreachable(graph, ss);
        assertTrue(ui.unreachableVertices.list.isEmpty());
        assertTrue(ui.incidentEdges.list.isEmpty());

    }

    // graph split -> inner x leaf -> split
    @Test
    void dynamicUpdater_allUnreachables() {
        DAG graph = new DAG(3, 2);
        SCCTreeNode splitnode = new SplitNode(1);
        SCCTreeNode innerNode = new InnerNode(new SplitNode(3));
        SCCTreeNode leafNode = new LeafNode(2);
        graph.incomingEdges.put(splitnode, new ArrayList<>());
        graph.incomingEdges.put(leafNode, new ArrayList<>());
        graph.incomingEdges.put(innerNode, new ArrayList<>());
        graph.outgoingEdges.put(splitnode, new ArrayList<>());
        graph.outgoingEdges.put(leafNode, new ArrayList<>());
        graph.outgoingEdges.put(innerNode, new ArrayList<>());
        graph.incomingEdgesCount.put(splitnode, 1);
        graph.incomingEdgesCount.put(leafNode, 0);
        graph.incomingEdgesCount.put(innerNode, 1);
        graph.outgoingEdgesCount.put(splitnode, 1);
        graph.outgoingEdgesCount.put(leafNode, 1);
        graph.outgoingEdgesCount.put(innerNode, 0);

        graph.incomingEdges.get(splitnode).add(new SCCTreeNodeEdge(leafNode, splitnode, 2, 1));
        graph.incomingEdges.get(innerNode).add(new SCCTreeNodeEdge(splitnode, innerNode, 1, 3));

        graph.outgoingEdges.get(splitnode).add(new SCCTreeNodeEdge(splitnode, innerNode, 1, 3));
        graph.outgoingEdges.get(leafNode).add(new SCCTreeNodeEdge(leafNode, splitnode, 2, 1));

        ListSetPair<SCCTreeNode> ss = new ListSetPair<>();
        ss.addIfNotPresent(innerNode);
        ss.addIfNotPresent(leafNode);
        Unreachables ui = new DynamicUpdater().findUnreachable(graph, ss);
        int expectedNrOfUnreachableVertices = 3;
        int expectedNrOfIncidentEdges = 2;
        assertEquals(expectedNrOfUnreachableVertices, ui.unreachableVertices.list.size());
        assertEquals(expectedNrOfIncidentEdges, ui.incidentEdges.list.size());
    }

    // graph split -> inner ->  split
    // graph split x leaf ->  split
    @Test
    void dynamicUpdater_someUnreachables() {
        DAG graph = new DAG(3, 3);
        SCCTreeNode splitnode = new SplitNode(1);
        SCCTreeNode innerNode = new InnerNode(new SplitNode(3));
        SCCTreeNode leafNode = new LeafNode(2);
        graph.incomingEdges.put(splitnode, new ArrayList<>());
        graph.incomingEdges.put(leafNode, new ArrayList<>());
        graph.incomingEdges.put(innerNode, new ArrayList<>());
        graph.outgoingEdges.put(splitnode, new ArrayList<>());
        graph.outgoingEdges.put(leafNode, new ArrayList<>());
        graph.outgoingEdges.put(innerNode, new ArrayList<>());
        graph.incomingEdgesCount.put(splitnode, 2);
        graph.incomingEdgesCount.put(leafNode, 0);
        graph.incomingEdgesCount.put(innerNode, 1);
        graph.outgoingEdgesCount.put(splitnode, 1);
        graph.outgoingEdgesCount.put(leafNode, 1);
        graph.outgoingEdgesCount.put(innerNode, 1);

        graph.incomingEdges.get(innerNode).add(new SCCTreeNodeEdge(splitnode, innerNode, 1, 3));
        graph.outgoingEdges.get(splitnode).add(new SCCTreeNodeEdge(splitnode, innerNode, 1, 3));

        graph.incomingEdges.get(splitnode).add(new SCCTreeNodeEdge(innerNode, splitnode, 3, 1));
        graph.outgoingEdges.get(innerNode).add(new SCCTreeNodeEdge(innerNode, splitnode, 3, 1));

        graph.incomingEdges.get(splitnode).add(new SCCTreeNodeEdge(leafNode, splitnode, 2, 1));
        graph.outgoingEdges.get(leafNode).add(new SCCTreeNodeEdge(leafNode, splitnode, 2, 1));

        ListSetPair<SCCTreeNode> ss = new ListSetPair<>();
        ss.addIfNotPresent(leafNode);
        ss.addIfNotPresent(splitnode);

        Unreachables ui = new DynamicUpdater().findUnreachable(graph, ss);
        int expectedNrOfUnreachableVertices = 1;
        int expectedNrOfIncidentEdges = 1;
        assertEquals(expectedNrOfUnreachableVertices, ui.unreachableVertices.list.size());
        assertEquals(expectedNrOfIncidentEdges, ui.incidentEdges.list.size());

    }

    @Test
    void dynamicDecomposier_noUnreachables_2sccsWith1OutgoingEdge(){
        ParityGame game = new ParityGame();
        InputGameReader reader = new InputGameReader();
        try {
            reader.readFileFromUrl(game, "src\\test\\resources\\smallSCCgraph.gm");
            boolean[] gameState = new boolean[]{true, true, true, true, true, true, true, true, true};
            DynamicDecomposer decomposer = new DynamicDecomposer();
            DynamicSCCDecomposition decomposition = decomposer.decompose(game, gameState, game.getNrOfVertices());
            System.out.println(decomposition);

            SCCTreeNode innerNodeTree = decomposition.getRootOfVertex(1);
            checkAllReachable(innerNodeTree, new DynamicUpdater());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkAllReachable(SCCTreeNode node, DynamicUpdater updater){
        if(node.isInnerNode()){
            DAG dag  = node.getDAG();
            Unreachables result = updater.findUnreachable(dag, new ListSetPair<>(dag.incomingEdges.keySet()));
            assertTrue(result.unreachableVertices.list.isEmpty());
            assertTrue(result.incidentEdges.list.isEmpty());

            for(SCCTreeNode child : dag.incomingEdges.keySet()){
                checkAllReachable(child, updater);
            }
        }

    }

    @Test
    void dynamicDecomposier_noUnreachables_3sccsWithOutgoingEdges(){
        ParityGame game = new ParityGame();
        InputGameReader reader = new InputGameReader();
        try {
            reader.readFileFromUrl(game, "src\\test\\resources\\smallSCCgraph2.gm");
            boolean[] gameState = new boolean[]{true, true, true, true, true, true, true, true, true};
            DynamicDecomposer decomposer = new DynamicDecomposer();
            DynamicSCCDecomposition decomposition = decomposer.decompose(game, gameState, game.getNrOfVertices());
            System.out.println(decomposition);

            for(int i = 0; i < 9; i++) {
                SCCTreeNode innerNodeTree = decomposition.getRootOfVertex(i);
                checkAllReachable(innerNodeTree, new DynamicUpdater());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void dynamicDecomposier_1unreachable2edges_2sccsWith1OutgoingEdge(){
        ParityGame game = new ParityGame();
        InputGameReader reader = new InputGameReader();
        try {
            reader.readFileFromUrl(game, "src\\test\\resources\\smallSCCgraph.gm");
            boolean[] gameState = new boolean[]{true, true, true, true, true, true, true, true, true};
            DynamicDecomposer decomposer = new DynamicDecomposer();
            DynamicSCCDecomposition decomposition = decomposer.decompose(game, gameState, game.getNrOfVertices());
            System.out.println(decomposition);

            SCCTreeNode innerNodeTree = decomposition.getRootOfVertex(1);
            innerNodeTree.removeEdge(new VertexEdge(4, 1));

            DAG dag  = innerNodeTree.getDAG();
            Unreachables result = new DynamicUpdater().findUnreachable(dag, new ListSetPair<>(dag.incomingEdges.keySet()));
            int expectedNrOfUnreachableVertices = 2;
            int expectedNrOfIncidentEdges = 2;
            assertEquals(expectedNrOfUnreachableVertices, result.unreachableVertices.list.size());
            assertEquals(expectedNrOfIncidentEdges, result.incidentEdges.list.size());
            System.out.println("\n\n unreachable vertices: " + result.unreachableVertices.list);
            System.out.println("\n\n incident edges: " + result.incidentEdges.list);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void dynamicDecomposier_2sccsWith1OutgoingEdge_deleteEdgeBetweenTrees(){
        ParityGame game = new ParityGame();
        InputGameReader reader = new InputGameReader();
        try {
            reader.readFileFromUrl(game, "src\\test\\resources\\smallSCCgraph.gm");
            boolean[] gameState = new boolean[]{true, true, true, true, true, true, true, true, true};
            DynamicDecomposer decomposer = new DynamicDecomposer();
            DynamicSCCDecomposition decomposition = decomposer.decompose(game, gameState, game.getNrOfVertices());
            System.out.println(decomposition);

            VertexEdge edgeBetweenTrees = new VertexEdge(0, 1);
            assertTrue(decomposition.isEdgeBetweenTrees(edgeBetweenTrees));
            assertNull(decomposition.getEdgeStoragePlace(edgeBetweenTrees));
            new DynamicUpdater().deleteEdge(decomposition, edgeBetweenTrees);
            assertFalse(decomposition.isEdgeBetweenTrees(edgeBetweenTrees));
            assertNull(decomposition.getEdgeStoragePlace(edgeBetweenTrees));

            System.out.println(decomposition);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void dynamicDecomposier_2sccsWith1OutgoingEdge_newLeafRoot(){
        ParityGame game = new ParityGame();
        InputGameReader reader = new InputGameReader();
        try {
            reader.readFileFromUrl(game, "src\\test\\resources\\smallSCCgraph.gm");
            boolean[] gameState = new boolean[]{true, true, true, true, true, true, true, true, true};
            DynamicDecomposer decomposer = new DynamicDecomposer();
            DynamicSCCDecomposition decomposition = decomposer.decompose(game, gameState, game.getNrOfVertices());
            System.out.println(decomposition);

            VertexEdge edgeToBeRemoved = new VertexEdge(6, 4);
            assertNotNull(decomposition.getEdgeStoragePlace(edgeToBeRemoved));
            new DynamicUpdater().deleteEdge(decomposition, edgeToBeRemoved);
            assertNull(decomposition.getEdgeStoragePlace(edgeToBeRemoved));
            assertNotEquals(decomposition.getRootOfVertex(6), decomposition.getRootOfVertex(4));

            System.out.println(decomposition);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void dynamicDecomposier_2sccsWith1OutgoingEdge_affectwholetree(){
        ParityGame game = new ParityGame();
        InputGameReader reader = new InputGameReader();
        try {
            reader.readFileFromUrl(game, "src\\test\\resources\\smallSCCgraph.gm");
            boolean[] gameState = new boolean[]{true, true, true, true, true, true, true, true, true};
            DynamicDecomposer decomposer = new DynamicDecomposer();
            DynamicSCCDecomposition decomposition = decomposer.decompose(game, gameState, game.getNrOfVertices());
            System.out.println(decomposition + "\n");

            VertexEdge edgeToBeRemoved = new VertexEdge(2, 3);
            assertNotNull(decomposition.getEdgeStoragePlace(edgeToBeRemoved));
            assertEquals(decomposition.getRootOfVertex(2), decomposition.getRootOfVertex(3));
            new DynamicUpdater().deleteEdge(decomposition, edgeToBeRemoved);
            assertNull(decomposition.getEdgeStoragePlace(edgeToBeRemoved));
            System.out.println(decomposition);

            for(int i = 0 ; i < 3; i++){
                for(int j = i+1 ; j < 9; j++){
                    assertNotEquals(decomposition.getRootOfVertex(i), decomposition.getRootOfVertex(j));
                }
            }

            for(int i : new int[]{4, 6, 8}){
                for(int j : new int[]{3, 5, 7}){
                    assertNotEquals(decomposition.getRootOfVertex(i), decomposition.getRootOfVertex(j));
                }
            }

            for(int i : new int[]{3, 5, 7}){
                for(int j : new int[]{3, 5, 7}){
                    assertEquals(decomposition.getRootOfVertex(i), decomposition.getRootOfVertex(j));
                }
            }

            for(int i : new int[]{3, 5, 7}){
                for(int j : new int[]{3, 5, 7}){
                    assertEquals(decomposition.getRootOfVertex(i), decomposition.getRootOfVertex(j));
                }
            }

            assertTrue(decomposition.isEdgeBetweenTrees(new VertexEdge(0, 1)));
            assertTrue(decomposition.isEdgeBetweenTrees(new VertexEdge(4, 1)));
            assertTrue(decomposition.isEdgeBetweenTrees(new VertexEdge(3, 1)));
            assertTrue(decomposition.isEdgeBetweenTrees(new VertexEdge(5, 2)));
            assertTrue(decomposition.isEdgeBetweenTrees(new VertexEdge(1, 2)));
            assertTrue(decomposition.isEdgeBetweenTrees(new VertexEdge(3, 8)));
            assertTrue(decomposition.isEdgeBetweenTrees(new VertexEdge(7, 8)));

            assertFalse(decomposition.isEdgeBetweenTrees(new VertexEdge(2, 3)));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void dynamicDecomposier_2sccsWith1OutgoingEdge_RemoveAllEdges(){
        ParityGame game = new ParityGame();
        InputGameReader reader = new InputGameReader();
        try {
            reader.readFileFromUrl(game, "src\\test\\resources\\smallSCCgraph.gm");
            boolean[] gameState = new boolean[]{true, true, true, true, true, true, true, true, true};
            DynamicDecomposer decomposer = new DynamicDecomposer();
            DynamicSCCDecomposition decomposition = decomposer.decompose(game, gameState, game.getNrOfVertices());
            System.out.println(decomposition + "\n");

            VertexEdge[] edgesToBeRemoved = new VertexEdge[]{
                    new VertexEdge(0, 1),
                    new VertexEdge(1, 2),
                    new VertexEdge(2, 3),
                    new VertexEdge(3, 1),
                    new VertexEdge(3, 5),
                    new VertexEdge(3, 8),
                    new VertexEdge(4, 1),
                    new VertexEdge(4, 8),
                    new VertexEdge(5, 2),
                    new VertexEdge(5, 7),
                    new VertexEdge(6, 4),
                    new VertexEdge(7, 3),
                    new VertexEdge(7, 8),
                    new VertexEdge(8, 6)
            };

            for(VertexEdge edge : edgesToBeRemoved){
                new DynamicUpdater().deleteEdge(decomposition, edge);

                assertNull(decomposition.getEdgeStoragePlace(edge));
                assertFalse(decomposition.isEdgeBetweenTrees(edge));
            }

            for(int i = 0 ; i < 9; i++){
                assertTrue(decomposition.getRootOfVertex(i).isLeafNode());
                assertEquals(i, decomposition.getRootOfVertex(i).getVertex());
                for(int j = i+1 ; j < 9; j++){
                    assertNotEquals(decomposition.getRootOfVertex(i), decomposition.getRootOfVertex(j));
                }
            }

            System.out.println(decomposition);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void dynamicDecomposier_2sccsWith1OutgoingEdge_OnlySubTree(){
        ParityGame game = new ParityGame();
        InputGameReader reader = new InputGameReader();
        try {
            reader.readFileFromUrl(game, "src\\test\\resources\\smallSCCgraph.gm");
            boolean[] gameState = new boolean[]{true, true, true, true, true, true, true, true, true};
            DynamicDecomposer decomposer = new DynamicDecomposer();
            DynamicSCCDecomposition decomposition = decomposer.decompose(game, gameState, game.getNrOfVertices());
            System.out.println(decomposition + "\n");

            VertexEdge edgeToBeRemoved = new VertexEdge(3, 1);
            assertNotNull(decomposition.getEdgeStoragePlace(edgeToBeRemoved));
            assertEquals(decomposition.getRootOfVertex(3), decomposition.getRootOfVertex(1));
            new DynamicUpdater().deleteEdge(decomposition, edgeToBeRemoved);
            assertNull(decomposition.getEdgeStoragePlace(edgeToBeRemoved));
            assertEquals(decomposition.getRootOfVertex(3), decomposition.getRootOfVertex(1));
            System.out.println(decomposition);

            for(int i = 1 ; i < 9; i++){
                for(int j = i+1 ; j < 9; j++){
                    assertEquals(decomposition.getRootOfVertex(i), decomposition.getRootOfVertex(j));
                }
            }

            for(int j = 1 ; j < 9; j++){
                assertNotEquals(decomposition.getRootOfVertex(0), decomposition.getRootOfVertex(j));
            }

            assertTrue(decomposition.isEdgeBetweenTrees(new VertexEdge(0, 1)));
            assertFalse(decomposition.isEdgeBetweenTrees(new VertexEdge(4, 1)));
            assertFalse(decomposition.isEdgeBetweenTrees(new VertexEdge(3, 1)));
            assertFalse(decomposition.isEdgeBetweenTrees(new VertexEdge(5, 2)));
            assertFalse(decomposition.isEdgeBetweenTrees(new VertexEdge(1, 2)));
            assertFalse(decomposition.isEdgeBetweenTrees(new VertexEdge(3, 8)));
            assertFalse(decomposition.isEdgeBetweenTrees(new VertexEdge(7, 8)));
            assertFalse(decomposition.isEdgeBetweenTrees(new VertexEdge(2, 3)));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
