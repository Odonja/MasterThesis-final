package org.anhu.game;

import org.anhu.gameStructures.ParityGame;
import org.anhu.reader.InputGameReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.*;

/**
 * Unit test for simple App.
 */
public class InputGameReaderTest
{
	@Test
	void InputGameReader_producesAllVertices(){
		ParityGame game = new ParityGame();
		InputGameReader reader = new InputGameReader();
		try {
			reader.readFileFromUrl(game, "src\\test\\resources\\smallSCCgraph.gm");
			for(int i = 0; i < 9; i++){
				assertTrue(game.getVertices().contains(i));
			}
			assertEquals(9, game.getNrOfVertices());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	void InputGameReader_producesAllEdges(){
		ParityGame game = new ParityGame();
		InputGameReader reader = new InputGameReader();
		Map<Integer, int[]>  expectedOutgoingEdges = new HashMap<>();
		expectedOutgoingEdges.put(0, new int[]{1});
		expectedOutgoingEdges.put(1, new int[]{2});
		expectedOutgoingEdges.put(2, new int[]{3});
		expectedOutgoingEdges.put(3, new int[]{1, 5, 8});
		expectedOutgoingEdges.put(4, new int[]{1, 8});
		expectedOutgoingEdges.put(5, new int[]{2, 7});
		expectedOutgoingEdges.put(6, new int[]{4});
		expectedOutgoingEdges.put(7, new int[]{3, 8});
		expectedOutgoingEdges.put(8, new int[]{6});

		Map<Integer, int[]>  expectedIncommingEdges = new HashMap<>();
		expectedIncommingEdges.put(0, new int[]{});
		expectedIncommingEdges.put(1, new int[]{0, 3, 4});
		expectedIncommingEdges.put(2, new int[]{1, 5});
		expectedIncommingEdges.put(3, new int[]{2, 7});
		expectedIncommingEdges.put(4, new int[]{6});
		expectedIncommingEdges.put(5, new int[]{3});
		expectedIncommingEdges.put(6, new int[]{8});
		expectedIncommingEdges.put(7, new int[]{5});
		expectedIncommingEdges.put(8, new int[]{3, 4, 7});

		try {
			reader.readFileFromUrl(game, "src\\test\\resources\\smallSCCgraph.gm");
			for(int i = 0; i < 9; i++){
				List<Integer> outgoingEdges = game.getOutgoingEdges(i);
				List<Integer> incommingEdges = game.getIncomingEdges(i);

				assertEquals(expectedIncommingEdges.get(i).length, incommingEdges.size());
				assertEquals(expectedOutgoingEdges.get(i).length, outgoingEdges.size());

				for(int vertex : expectedIncommingEdges.get(i)){
					assertTrue(incommingEdges.contains(vertex));
				}

				for(int vertex : expectedOutgoingEdges.get(i)){
					assertTrue(outgoingEdges.contains(vertex));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	void InputGameReader_producesCorrectPriorities(){
		ParityGame game = new ParityGame();
		InputGameReader reader = new InputGameReader();
		int[] expectedPriorities = {8, 7, 6, 5, 4, 3, 2, 1, 0};

		try {
			reader.readFileFromUrl(game, "src\\test\\resources\\smallSCCgraph.gm");
			for(int i = 0; i < 9; i++){
				assertEquals(expectedPriorities[i], game.getPriority(i));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	void InputGameReader_producesCorrectowners(){
		ParityGame game = new ParityGame();
		InputGameReader reader = new InputGameReader();
		boolean[] expectedPriorities = {true, true, false, false, true, true, false, false, false};

		try {
			reader.readFileFromUrl(game, "src\\test\\resources\\smallSCCgraph.gm");
			for(int i = 0; i < 9; i++){
				assertEquals(expectedPriorities[i], game.getOwner(i));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
