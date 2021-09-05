package org.anhu.zielonkaSCCdecomposers;

import org.anhu.gameStructures.GameState;
import org.anhu.gameStructures.ParityGame;
import org.anhu.gameStructures.StaticSCCDecomposition;

import java.util.List;

public interface StaticSCCdecomposer {

    StaticSCCDecomposition updateGameStateAndDecompose(ParityGame game, GameState gameState, StaticSCCDecomposition oldSCCDecomposition,
                                                       List<Integer> attractorsetPlayer0,  List<Integer>  attractorsetPlayer1);
    StaticSCCDecomposition decompose(ParityGame game, GameState gameState);
}
