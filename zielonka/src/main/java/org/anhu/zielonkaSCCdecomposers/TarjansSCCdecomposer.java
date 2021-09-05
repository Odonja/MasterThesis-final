package org.anhu.zielonkaSCCdecomposers;

import org.anhu.gameStructures.GameState;
import org.anhu.gameStructures.ParityGame;
import org.anhu.gameStructures.StaticSCCDecomposition;
import org.anhu.sccAlgorithms.TarjansDecomposer;
import org.anhu.statistics.Statistic;

import java.util.List;

public class TarjansSCCdecomposer implements StaticSCCdecomposer {

    private final TarjansDecomposer decomposer;

    public TarjansSCCdecomposer(){
        decomposer = new TarjansDecomposer();
    }

    @Override
    public StaticSCCDecomposition updateGameStateAndDecompose(ParityGame game, GameState gameState,
                                                              StaticSCCDecomposition oldSCCDecomposition,
                                                              List<Integer> attractorsetPlayer0,  List<Integer>  attractorsetPlayer1) {
        // set the game state to G \ W^G_0 U W^G_1
        gameState.removeFromGame(attractorsetPlayer0);
        gameState.removeFromGame(attractorsetPlayer1);
        Statistic.incrementIntegerStatistic(Statistic.Event.NROFVERTICES_T, gameState.nrOfVerticesInGame);
        return Statistic.registerExecutionTimeWIthReturn(Statistic.Event.TARJAN_T,() ->
                decomposer.run(game, gameState.verticesInGame, gameState.nrOfVerticesInGame));
    }

    @Override
    public StaticSCCDecomposition decompose(ParityGame game, GameState gameState) {
        Statistic.incrementIntegerStatistic(Statistic.Event.NROFVERTICES_T, gameState.nrOfVerticesInGame);
        return Statistic.registerExecutionTimeWIthReturn(Statistic.Event.TARJAN_T,() ->
                decomposer.run(game, gameState.verticesInGame, gameState.nrOfVerticesInGame));
    }
}
