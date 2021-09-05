package org.anhu.game;

import org.anhu.gameStructures.ParityGame;
import org.anhu.gameStructures.dynamicGameStructures.DAG;
import org.anhu.gameStructures.dynamicGameStructures.DynamicSCCDecomposition;
import org.anhu.gameStructures.dynamicGameStructures.VertexEdge;
import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.SCCTreeNode;
import org.anhu.reader.InputGameReader;
import org.anhu.sccAlgorithms.DynamicDecomposer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicDecomposerTest {

    @Test
    void dynamicDecomposier_givesDecomposition_2sccsWith1OutgoingEdge(){
        ParityGame game = new ParityGame();
        InputGameReader reader = new InputGameReader();
        try {
            reader.readFileFromUrl(game, "src\\test\\resources\\smallSCCgraph.gm");
            boolean[] gameState = new boolean[]{true, true, true, true, true, true, true, true, true};
            DynamicDecomposer decomposer = new DynamicDecomposer();
            DynamicSCCDecomposition decomposition = decomposer.decompose(game, gameState, game.getNrOfVertices());
            System.out.println(decomposition);

            assertTrue(decomposition.isEdgeBetweenTrees(new VertexEdge(0, 1)));
            for(int i = 1; i < 9; i++){
                assertNotEquals(decomposition.getRootOfVertex(0), decomposition.getRootOfVertex(i));
            }

            for(int i = 1; i < 9; i++){
                for(int j = i+1; j < 9; j++){
                    assertEquals(decomposition.getRootOfVertex(i), decomposition.getRootOfVertex(j));
                }
            }
            assertNotNull(decomposition.getEdgeStoragePlace(new VertexEdge(1, 2)));
            assertNotNull(decomposition.getEdgeStoragePlace(new VertexEdge(2, 3)));
            assertNotNull(decomposition.getEdgeStoragePlace(new VertexEdge(3, 1)));
            assertNotNull(decomposition.getEdgeStoragePlace(new VertexEdge(3, 5)));
            assertNotNull(decomposition.getEdgeStoragePlace(new VertexEdge(3, 8)));
            assertNotNull(decomposition.getEdgeStoragePlace(new VertexEdge(4, 1)));
            assertNotNull(decomposition.getEdgeStoragePlace(new VertexEdge(4, 8)));
            assertNotNull(decomposition.getEdgeStoragePlace(new VertexEdge(5, 2)));
            assertNotNull(decomposition.getEdgeStoragePlace(new VertexEdge(5, 7)));
            assertNotNull(decomposition.getEdgeStoragePlace(new VertexEdge(6, 4)));
            assertNotNull(decomposition.getEdgeStoragePlace(new VertexEdge(7, 3)));
            assertNotNull(decomposition.getEdgeStoragePlace(new VertexEdge(7, 8)));
            assertNotNull(decomposition.getEdgeStoragePlace(new VertexEdge(8, 6)));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void dynamicDecomposier_givesDecomposition_3sccsWithOutgoingEdges(){
        ParityGame game = new ParityGame();
        InputGameReader reader = new InputGameReader();
        try {
            reader.readFileFromUrl(game, "src\\test\\resources\\smallSCCgraph2.gm");
            boolean[] gameState = new boolean[]{true, true, true, true, true, true, true, true, true};
            DynamicDecomposer decomposer = new DynamicDecomposer();
            DynamicSCCDecomposition decomposition = decomposer.decompose(game, gameState, game.getNrOfVertices());
            System.out.println(decomposition);
            assertTrue(decomposition.isEdgeBetweenTrees(new VertexEdge(3, 1)));
            assertTrue(decomposition.isEdgeBetweenTrees(new VertexEdge(4, 1)));
            assertTrue(decomposition.isEdgeBetweenTrees(new VertexEdge(3, 8)));
            assertTrue(decomposition.isEdgeBetweenTrees(new VertexEdge(7, 8)));

            for(int i = 0; i < 2; i++){
                for(int j = 2; j < 9; j++){
                    assertNotEquals(decomposition.getRootOfVertex(i), decomposition.getRootOfVertex(j));
                }
            }

            for(int i : new int[]{2, 3, 5, 7}){
                for(int j : new int[]{4, 6, 8}){
                    assertNotEquals(decomposition.getRootOfVertex(i), decomposition.getRootOfVertex(j));
                }
            }

            for(int i : new int[]{0, 1}){
                for(int j : new int[]{0, 1}){
                    assertEquals(decomposition.getRootOfVertex(i), decomposition.getRootOfVertex(j));
                }
            }

            for(int i : new int[]{2, 3, 5, 7}){
                for(int j : new int[]{2, 3, 5, 7}){
                    assertEquals(decomposition.getRootOfVertex(i), decomposition.getRootOfVertex(j));
                }
            }

            for(int i : new int[]{4, 6, 8}){
                for(int j : new int[]{4, 6, 8}){
                    assertEquals(decomposition.getRootOfVertex(i), decomposition.getRootOfVertex(j));
                }
            }

            VertexEdge[] sccedges = new VertexEdge[]{
                new VertexEdge(0, 1),
                new VertexEdge(1, 0),
                new VertexEdge(2, 3),
                new VertexEdge(3, 5),
                new VertexEdge(4, 8),
                new VertexEdge(5, 2),
                new VertexEdge(5, 7),
                new VertexEdge(6, 4),
                new VertexEdge(7, 3),
                new VertexEdge(8, 6),
            };

            for(VertexEdge edge: sccedges){
                assertNotNull(decomposition.getEdgeStoragePlace(edge));
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void dynamicDecomposier_givesCorrectDAG_2sccsWith1OutgoingEdge(){
        ParityGame game = new ParityGame();
        InputGameReader reader = new InputGameReader();
        try {
            reader.readFileFromUrl(game, "src\\test\\resources\\smallSCCgraph.gm");
            boolean[] gameState = new boolean[]{true, true, true, true, true, true, true, true, true};
            DynamicDecomposer decomposer = new DynamicDecomposer();
            DynamicSCCDecomposition decomposition = decomposer.decompose(game, gameState, game.getNrOfVertices());
            System.out.println(decomposition);

            assertThrows(RuntimeException.class, () -> decomposition.getRootOfVertex(0).getDAG());

            DAG dag1 = decomposition.getRootOfVertex(1).getDAG();

            int expectedNrOfVerticesDAG1 = 6;
            assertEquals(expectedNrOfVerticesDAG1, dag1.nrOfVertices);

            int expectedNrOfEdgesDAG1 = 10;
            assertEquals(expectedNrOfEdgesDAG1, dag1.nrOfEdges);

            Set<SCCTreeNode> children = dag1.incomingEdges.keySet();
            boolean containsLeafNode1 = false;
            boolean containsLeafNode2 = false;
            boolean containsLeafNode5 = false;
            boolean containsLeafNode7 = false;
            boolean containsSplitNode3 = false;
            boolean containsInnerNode = false;
            SCCTreeNode innerNode = null;

            for(SCCTreeNode child: children){
                if(child.isLeafNode()){
                    if(child.getVertex() == 1){
                        containsLeafNode1 = true;
                    }else if(child.getVertex() == 2){
                        containsLeafNode2 = true;
                    }else if(child.getVertex() == 5){
                        containsLeafNode5 = true;
                    }else if(child.getVertex() == 7){
                        containsLeafNode7 = true;
                    }
                }
                if(child.isSplitNode() && child.getVertex() == 3){
                    containsSplitNode3 = true;
                }
                if(child.isInnerNode()){
                    containsInnerNode = true;
                    innerNode = child;
                }
            }
            assertTrue(containsLeafNode1);
            assertTrue(containsLeafNode2);
            assertTrue(containsSplitNode3);
            assertTrue(containsLeafNode5);
            assertTrue(containsLeafNode7);
            assertTrue(containsInnerNode);



            assertNotNull(innerNode);
            DAG dag2 = innerNode.getDAG();

            System.out.println("nr of children " + innerNode.getNrOfChildren());
            int expectedNrOfVerticesDAG2 = 3;
            assertEquals(expectedNrOfVerticesDAG2, dag2.nrOfVertices);

            int expectedNrOfEdgesDAG2 = 3;
            assertEquals(expectedNrOfEdgesDAG2, dag2.nrOfEdges);

            int nrOfLeafNodes = 0;
            int nrOfSplitNodes = 0;
            for(SCCTreeNode child : dag2.incomingEdges.keySet()){
                if(child.isLeafNode()){
                    nrOfLeafNodes++;
                }
                if(child.isSplitNode()){
                    nrOfSplitNodes++;
                }
            }

            int expectedNrOfLeafNodes = 3; //assuming split=leaf
            int expectedNrOfSplitNodes = 1;
            assertEquals(expectedNrOfLeafNodes, nrOfLeafNodes);
            assertEquals(expectedNrOfSplitNodes, nrOfSplitNodes);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
