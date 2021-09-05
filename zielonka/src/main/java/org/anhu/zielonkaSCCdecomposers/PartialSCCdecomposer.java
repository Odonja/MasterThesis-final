package org.anhu.zielonkaSCCdecomposers;

import org.anhu.gameStructures.GameState;
import org.anhu.gameStructures.SCC;
import org.anhu.gameStructures.ParityGame;
import org.anhu.gameStructures.StaticSCCDecomposition;
import org.anhu.sccAlgorithms.TarjansDecomposer;
import org.anhu.statistics.Statistic;

import java.util.*;

public class PartialSCCdecomposer implements StaticSCCdecomposer {


    private final TarjansDecomposer decomposer;

    public PartialSCCdecomposer(){
        decomposer = new TarjansDecomposer();
    }

    @Override
    public StaticSCCDecomposition updateGameStateAndDecompose(ParityGame game, GameState gameState,
                                                              StaticSCCDecomposition oldSCCDecomposition,
                                                              List<Integer> attractorsetPlayer0,  List<Integer>  attractorsetPlayer1) {
        boolean[] partialGameState = new boolean[game.getNrOfVertices()];
        List<SCC> oldSCCs = oldSCCDecomposition.SCCs;
        List<Integer> partialGameStateVertices = new LinkedList<>();
        Set<SCC> oldSCCsSet = new HashSet<>(oldSCCs);

        Statistic.registerExecutionTime(Statistic.Event.PARTIALPREP, () -> {
            List<Integer> deletedVertices = new ArrayList<>(attractorsetPlayer0.size() + attractorsetPlayer1.size());
            Set<Integer> deletedVerticesSet = new HashSet<>();
            // set the game state to G \ W^G_0 U W^G_1
            for(int vertex : attractorsetPlayer0){
                deletedVertices.add(vertex);
                deletedVerticesSet.add(vertex);
                gameState.removeFromGame(vertex);
            }
            for(int vertex : attractorsetPlayer1){
                deletedVertices.add(vertex);
                deletedVerticesSet.add(vertex);
                gameState.removeFromGame(vertex);
            }



            for(int vertex : deletedVertices){
                SCC scc = oldSCCDecomposition.vertexToSCC.get(vertex);

                // this SCC has to be recalculated, remove it if it exists
                if(oldSCCsSet.remove(scc)){ // returns true if the object exists

                    // add all members of the scc to the partial game that will be recalculated
                    for(int sccMember : scc.members){
                        if(!deletedVerticesSet.contains(sccMember)) {
                            partialGameState[sccMember] = true;
                            partialGameStateVertices.add(sccMember);
                        }
                        oldSCCDecomposition.vertexToSCC.remove(sccMember); // remove its old SCC link since a new one will be calculated
                    }
                }
            }
        });

        Statistic.incrementIntegerStatistic(Statistic.Event.NROFVERTICES_P, partialGameStateVertices.size());

        StaticSCCDecomposition newDecomposition = Statistic.registerExecutionTimeWIthReturn(Statistic.Event.TARJAN_P,() ->
                decomposer.run(game, partialGameState, partialGameStateVertices, oldSCCDecomposition.vertexToSCC));
        Statistic.registerExecutionTime(Statistic.Event.PARTIALPREP, () -> {
            for (SCC scc : oldSCCs) {
                if (oldSCCsSet.contains(scc)) {
                        newDecomposition.SCCs.add(scc);
                }
            }
        });

        return newDecomposition;
    }

    @Override
    public StaticSCCDecomposition decompose(ParityGame game, GameState gameState) {
        Statistic.incrementIntegerStatistic(Statistic.Event.NROFVERTICES_P, gameState.nrOfVerticesInGame);
        return Statistic.registerExecutionTimeWIthReturn(Statistic.Event.TARJAN_P,() ->
                decomposer.run(game, gameState.verticesInGame, gameState.nrOfVerticesInGame));
    }
}
