package org.anhu.game;

import org.anhu.gameStructures.ParityGame;
import org.anhu.gameStructures.StaticSCCDecomposition;
import org.anhu.reader.InputGameReader;
import org.anhu.sccAlgorithms.TarjansDecomposer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TarjansDecomposerTest {
    @Test
    void TarjansDecomposier_givesDecomposition_2sccsWith1OutgoingEdge(){
        ParityGame game = new ParityGame();
        InputGameReader reader = new InputGameReader();
        try {
            reader.readFileFromUrl(game, "src\\test\\resources\\smallSCCgraph.gm");
            TarjansDecomposer decomposer = new TarjansDecomposer();
            boolean[] gameState = new boolean[]{true, true, true, true, true, true, true, true, true};
            StaticSCCDecomposition sccDecomposition = decomposer.run(game, gameState, game.getNrOfVertices());

            String expected = "[SCC{members=[4, 6, 8, 7, 5, 3, 2, 1], outgoingEdges=[]}, SCC{members=[0], outgoingEdges=[1]}]";
            String actual = sccDecomposition.SCCs.toString();
            System.out.println(sccDecomposition.SCCs);
            assertEquals(expected, actual);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        @Test
        void TarjansDecomposier_givesDecomposition_3sccsWithOutgoingEdges(){
            ParityGame game = new ParityGame();
            InputGameReader reader = new InputGameReader();
            try {
                reader.readFileFromUrl(game, "src\\test\\resources\\smallSCCgraph2.gm");
                TarjansDecomposer decomposer = new TarjansDecomposer();
                boolean[] gameState = new boolean[]{true, true, true, true, true, true, true, true, true};
                StaticSCCDecomposition sccDecomposition = decomposer.run(game, gameState, game.getNrOfVertices());

                String expected = "[SCC{members=[1, 0], outgoingEdges=[]}, SCC{members=[4, 6, 8], outgoingEdges=[1]}, " +
                        "SCC{members=[7, 5, 3, 2], outgoingEdges=[8, 1, 8]}]";
                String actual = sccDecomposition.SCCs.toString();
                System.out.println(sccDecomposition.SCCs);
                assertEquals(expected, actual);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


}
