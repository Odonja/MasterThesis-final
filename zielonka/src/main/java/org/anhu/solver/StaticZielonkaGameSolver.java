package org.anhu.solver;

import org.anhu.zielonkaSCCdecomposers.StaticSCCdecomposer;
import org.anhu.gameStructures.GameState;
import org.anhu.gameStructures.SCC;
import org.anhu.gameStructures.ParityGame;
import org.anhu.gameStructures.StaticSCCDecomposition;
import org.anhu.statistics.Statistic;

import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class StaticZielonkaGameSolver extends BasicZielonkaGameSolver
{

    public static final boolean PLAYER0 = false;
    public static final boolean PLAYER1 = true;

    public GameResult solve(ParityGame game, StaticSCCdecomposer decomposer){
        int totalNrOfVertices = game.getNrOfVertices();
        boolean[] fullGame = new boolean[totalNrOfVertices];
        Arrays.fill(fullGame, Boolean.TRUE);
        GameState gameState = new GameState(fullGame, totalNrOfVertices);
        return solve(game, gameState, decomposer);
    }

    private GameResult solve(ParityGame game, GameState gameStateG, StaticSCCdecomposer decomposer){
        GameResult result = new GameResult(); // set of winning vertices for both players
        int totalNrOfVertices = game.getNrOfVertices();

        if(gameStateG.nrOfVerticesInGame != 0){
            StaticSCCDecomposition sccDecomposition = decomposer.decompose(game, gameStateG);

            while(!sccDecomposition.SCCs.isEmpty()){ // then there is a final SCC
                if(Statistic.timerExpired()){
                    return null;
                }
                SCC finalSCC = findFinalSCC(gameStateG.verticesInGame, sccDecomposition.SCCs);
                // set the game state to H
                GameState gameStateH = new GameState(totalNrOfVertices);
                assert finalSCC != null;
                gameStateH.isInGame(finalSCC.members);

                int maxPriority = findMaxPriority(game, finalSCC.members);
                boolean player = maxPriority % 2 == 1; // true for player 1, false for player 0
                boolean opponent = !player;
                List<Integer> maxPriorityVertices = getVerticesWithPriority(game, finalSCC.members, maxPriority); // U
                List<Integer> attractorSetA = findAttractorset(game, gameStateH.verticesInGame, maxPriorityVertices, player);


                // set the game state to H\A
                gameStateH.removeFromGame(attractorSetA);
                GameResult intermediateResult = solve(game, gameStateH, decomposer);
                if(intermediateResult == null){ // timer expired
                    return null;
                }
                List<Integer> resultOpponent = opponent ?
                        intermediateResult.getVerticesWonByPlayer1() : intermediateResult.getVerticesWonByPlayer0();

                // set the game state to H
                gameStateH.isInGame(finalSCC.members);

                List<Integer> attractorSetB = findAttractorset(game, gameStateH.verticesInGame, resultOpponent, opponent);


                if(attractorSetB.size() == resultOpponent.size()){ // if B = W'_alphabar
                    if(player == PLAYER1){ // if player is player 1
                        intermediateResult.wonByPlayer1(attractorSetA); // add A to the vertices won by player 1
                    }else{ // if player is player 0
                        intermediateResult.wonByPlayer0(attractorSetA); // add A to the vertices won by player 0
                    }
                }else{
                    // set the game state to H\B
                    gameStateH.removeFromGame(attractorSetB);

                    intermediateResult =  solve(game, gameStateH, decomposer);
                    if(intermediateResult == null){ // timer expired
                        return null;
                    }
                    if(opponent == PLAYER1){ // if opponent is player 1
                        intermediateResult.wonByPlayer1(attractorSetB); // add B to the vertices won by player 1
                    }else{ // if opponent is player 0
                        intermediateResult.wonByPlayer0(attractorSetB); // add B to the vertices won by player 0
                    }
                }

                List<Integer> attractorsetPlayer0 = findAttractorset(game, gameStateG.verticesInGame, intermediateResult.getVerticesWonByPlayer0(), PLAYER0);
                List<Integer> attractorsetPlayer1 = findAttractorset(game, gameStateG.verticesInGame, intermediateResult.getVerticesWonByPlayer1(), PLAYER1);

                result.wonByPlayer0(attractorsetPlayer0);
                result.wonByPlayer1(attractorsetPlayer1);

                sccDecomposition = decomposer.updateGameStateAndDecompose(game, gameStateG, sccDecomposition, attractorsetPlayer0, attractorsetPlayer1);
            }
        }
        return result;
    }


    private SCC findFinalSCC(boolean[] inGameVertices, List<SCC> sccs){
        for(SCC scc : sccs){
            boolean isFinalSCC = true;
            for (int outgoingEdge : scc.outgoingEdges) {
                if(inGameVertices[outgoingEdge]){
                    isFinalSCC = false;
                    break;
                }
            }
            if(isFinalSCC){
                return scc;
            }
        }
        // should not be reached, there must always be a final scc
        return null;
    }

}
