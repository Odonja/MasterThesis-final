package org.anhu.solver;

import org.anhu.gameStructures.ParityGame;
import org.anhu.gameStructures.dynamicGameStructures.DynamicSCCDecomposition;
import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.SCCTreeNode;
import org.anhu.statistics.Statistic;
import org.anhu.zielonkaSCCdecomposers.DynamicSCCdecomposer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Hello world!
 *
 */
public class DynamicZielonkaGameSolver2 extends BasicZielonkaGameSolver
{

    public static final boolean PLAYER0 = false;
    public static final boolean PLAYER1 = true;

    public GameResult solve(ParityGame game){
        DynamicSCCdecomposer decomposer = new DynamicSCCdecomposer();
        DynamicSCCDecomposition decomposition = Statistic.registerExecutionTimeWIthReturn(Statistic.Event.DECOMPOSE, () -> decomposer.decompose(game));
        if(decomposition == null){// timer expired
            return null;
        }
        return solve(game, decomposition, decomposer);
    }

    private GameResult solve(ParityGame game, DynamicSCCDecomposition decomposition, DynamicSCCdecomposer decomposer){
        GameResult result = new GameResult(); // set of winning vertices for both players
        if(decomposition.isEmpty()){
            return result;
        }

        while(!decomposition.isEmpty()){ // then there is a final SCC
            List<SCCTreeNode> finalSCCs = decomposition.getAllFinalSCCTrees();
            GameResult iterationResult = new GameResult();
            for(SCCTreeNode finalSCC : finalSCCs){
                if(Statistic.timerExpired()){
                    return null;
                }
                List<Integer> sccMembers = new ArrayList<>();
                finalSCC.getAllVertices(sccMembers);

                boolean[] gameStateH = new boolean[game.getNrOfVertices()];
                for(int vertex:sccMembers){
                    gameStateH[vertex] = true;
                }

                final int maxPriority = findMaxPriority(game, sccMembers);
                final boolean player = maxPriority % 2 == 1; // true for player 1, false for player 0
                final boolean opponent = !player;
                List<Integer> maxPriorityVertices = getVerticesWithPriority(game, sccMembers, maxPriority); // U
                List<Integer> attractorSetA = findAttractorset(game, gameStateH, maxPriorityVertices, player);

                GameResult intermediateResult = new GameResult();
                // if all vertices are in the attractor set then there is no need for recursion
                // since H\A is an empty graph
                if(attractorSetA.size() != sccMembers.size()){
                    DynamicSCCDecomposition sccWithoutAttractorSet =
                            Statistic.registerExecutionTimeWIthReturn(Statistic.Event.COPYTREEWITHDELETEEDGE,
                                    () -> decomposer.getIndependentDecompositionOfSCCWitoutAttractorset(game, finalSCC, attractorSetA));
                    intermediateResult = solve(game, sccWithoutAttractorSet, decomposer);
                    if(intermediateResult == null){ // timer expired
                        return null;
                    }
                }

                List<Integer> resultOpponent = opponent ?
                        intermediateResult.getVerticesWonByPlayer1() :
                        intermediateResult.getVerticesWonByPlayer0();
                List<Integer> attractorSetB = findAttractorset(game, gameStateH, resultOpponent, opponent);

                if(attractorSetB.size() == resultOpponent.size()){ // if B = W'_alphabar
                    if(player == PLAYER1){ // if player is player 1
                        intermediateResult.wonByPlayer1(attractorSetA); // add A to the vertices won by player 1
                    }else{ // if player is player 0
                        intermediateResult.wonByPlayer0(attractorSetA); // add A to the vertices won by player 0
                    }
                }else{
                    intermediateResult = new GameResult();
                    if(attractorSetB.size() != sccMembers.size()) {
                        DynamicSCCDecomposition sccWithoutAttractorSet =
                                Statistic.registerExecutionTimeWIthReturn(Statistic.Event.COPYTREEWITHDELETEEDGE,
                                        () -> decomposer.getIndependentDecompositionOfSCCWitoutAttractorset(game, finalSCC, attractorSetB));
                        intermediateResult = solve(game, sccWithoutAttractorSet, decomposer);
                        if(intermediateResult == null){ // timer expired
                            return null;
                        }
                    }
                    if(opponent == PLAYER1){ // if opponent is player 1
                        intermediateResult.wonByPlayer1(attractorSetB); // add B to the vertices won by player 1
                    }else{ // if opponent is player 0
                        intermediateResult.wonByPlayer0(attractorSetB); // add B to the vertices won by player 0
                    }
                }

                iterationResult.wonByPlayer0(intermediateResult.getVerticesWonByPlayer0());
                iterationResult.wonByPlayer1(intermediateResult.getVerticesWonByPlayer1());

            }
            boolean[] gameStateG = new boolean[game.getNrOfVertices()];
            decomposition.getGameState(gameStateG);

            List<Integer> attractorsetPlayer0 = findAttractorset(game, gameStateG, iterationResult.getVerticesWonByPlayer0(), PLAYER0);
            List<Integer> attractorsetPlayer1 = findAttractorset(game, gameStateG, iterationResult.getVerticesWonByPlayer1(), PLAYER1);
            result.wonByPlayer0(attractorsetPlayer0);
            result.wonByPlayer1(attractorsetPlayer1);

            Set<Integer> verticesToBeRemoved = new HashSet<>(attractorsetPlayer0.size()*2 + attractorsetPlayer1.size()*2);
            verticesToBeRemoved.addAll(attractorsetPlayer0);
            verticesToBeRemoved.addAll(attractorsetPlayer1);

            Statistic.registerExecutionTime(Statistic.Event.UPDATEDYNAMICDECOMP, () -> decomposer.deleteVertices(game, decomposition, verticesToBeRemoved));

        }
        return result;
    }

}
