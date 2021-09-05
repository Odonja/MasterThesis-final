package org.anhu.solver;

import org.anhu.zielonkaSCCdecomposers.StaticSCCdecomposer;
import org.anhu.zielonkaSCCdecomposers.TarjansSCCdecomposer;
import org.anhu.gameStructures.ParityGame;
import org.anhu.reader.InputGameReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Unit test for simple App.
 */
public class StaticZielonkaGameSolverTest
{


	@Test
	void tarjanZielonka_zielonkaExample(){
		ParityGame game = new ParityGame();
		InputGameReader reader = new InputGameReader();
		try {
			reader.readFileFromUrl(game, "src\\test\\resources\\zielonkaExample.gm");
			StaticSCCdecomposer decomposer = new TarjansSCCdecomposer();
			StaticZielonkaGameSolver solver = new StaticZielonkaGameSolver();
			GameResult solution = solver.solve(game, decomposer);

			System.out.println("vertices won by player 0: " + solution.getVerticesWonByPlayer0());
			System.out.println("vertices won by player 1: " + solution.getVerticesWonByPlayer1());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
