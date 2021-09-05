package org.anhu.solver;

import org.anhu.commons.ListSetPair;
import org.anhu.gameStructures.ParityGame;

import java.util.ArrayList;
import java.util.List;

public class BasicZielonkaGameSolver {

    List<Integer> findAttractorset(ParityGame game, boolean[] inGameVertices, List<Integer> initialAttractors, boolean player) {
        ListSetPair<Integer> finalattractorSet = new ListSetPair<>();
        ListSetPair<Integer> attractorSet = new ListSetPair<>(initialAttractors);
        ListSetPair<Integer> nextRoundAttractorSet = new ListSetPair<>();

        boolean changed = true;
        while (changed){
            changed = false;
            // check for each vertex in the attractor set
            for(int vertex : attractorSet.list){
                List<Integer> sourcesOfInEdges = game.getIncomingEdges(vertex);
                int nrOfSources = sourcesOfInEdges.size();
                int nrOfSourcesInAttractorSet = 0;
                // check all incomming edges
                for(int source: sourcesOfInEdges){
                    if(!inGameVertices[source]){
                        nrOfSources--;
                    }else if(finalattractorSet.contains(source) || attractorSet.contains(source) || nextRoundAttractorSet.contains(source)){
                        nrOfSourcesInAttractorSet++;
                    }else{
                        // if the incomming edge is in the game and not yet in the attractor set
                        boolean owner = game.getOwner(source);
                        // and the owner is the player, add it to the attractorset
                        if(owner == player){
                            nextRoundAttractorSet.addIfNotPresent(source);
                            nrOfSourcesInAttractorSet++;
                            changed = true;
                        }else{
                            // if the owner is not the player then it should not be able to escape to be added
                            boolean allAttracted = true;
                            // check all its outgoing edges for escape routes
                            for(int target : game.getOutgoingEdges(source)){
                                if(inGameVertices[target] && !finalattractorSet.contains(target) &&
                                        !attractorSet.contains(target) && !nextRoundAttractorSet.contains(source)){
                                    // opponent can escape since it can go somewhere that is not the attractor set
                                    allAttracted = false;
                                    break;
                                }
                            }
                            // if it cannot escape add it to the attractor set
                            if(allAttracted){
                                nextRoundAttractorSet.addIfNotPresent(source);
                                nrOfSourcesInAttractorSet++;
                                changed = true;
                            }
                        }
                    }
                }
                if(nrOfSources == nrOfSourcesInAttractorSet){
                    finalattractorSet.addIfNotPresent(vertex);
                }else{
                    nextRoundAttractorSet.addIfNotPresent(vertex);
                }
            }
            attractorSet = nextRoundAttractorSet;
            nextRoundAttractorSet = new ListSetPair<>();
        }
        finalattractorSet.addAll(attractorSet.list);
        return finalattractorSet.list;
    }

    int findMaxPriority(ParityGame game, List<Integer> sccMembers){
        int max = 0;
        for (int vertex : sccMembers) {
            int priority = game.getPriority(vertex);
            if(priority > max){
                max = priority;
            }
        }
        return max;
    }

    List<Integer> getVerticesWithPriority(ParityGame game, List<Integer> sccMembers, int priority){
        List<Integer> vertices = new ArrayList<>();
        for (int vertex : sccMembers) {
            if(game.getPriority(vertex) == priority){
                vertices.add(vertex);
            }
        }
        return vertices;
    }


}
