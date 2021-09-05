package org.anhu.sccAlgorithms;

import org.anhu.gameStructures.SCC;
import org.anhu.gameStructures.ParityGame;
import org.anhu.gameStructures.StaticSCCDecomposition;

import java.util.*;

public class TarjansDecomposer {

    public StaticSCCDecomposition run(ParityGame game, boolean[] vertexInGame, int nrOfVerticesInGame){
        TarjanInfo info = new TarjanInfo(game, nrOfVerticesInGame);

        for(int vertex = 0; vertex < game.getNrOfVertices(); vertex++){
            if(vertexInGame[vertex] && !info.low.containsKey(vertex)){
                strongConnect(vertex, game, vertexInGame ,info);
            }
        }
        StaticSCCDecomposition decomposition = new StaticSCCDecomposition();
        decomposition.SCCs = info.result;
        decomposition.vertexToSCC = info.vertexToSCC;
        return decomposition;
    }

    public StaticSCCDecomposition run(ParityGame game, boolean[] vertexInGame, List<Integer> verticesInGame){
        int nrOfVerticesInGame = verticesInGame.size();
        TarjanInfo info = new TarjanInfo(game, nrOfVerticesInGame);

        for(int vertex : verticesInGame){
            if(!info.low.containsKey(vertex)) {
                strongConnect(vertex, game, vertexInGame, info);
            }
        }
        StaticSCCDecomposition decomposition = new StaticSCCDecomposition();
        decomposition.SCCs = info.result;
        decomposition.vertexToSCC = info.vertexToSCC;
        return decomposition;
    }

    public StaticSCCDecomposition run(ParityGame game, boolean[] vertexInGame, List<Integer> verticesInGame, Map<Integer, SCC> vertexToSCC){
        int nrOfVerticesInGame = verticesInGame.size();
        TarjanInfo info = new TarjanInfo(game, nrOfVerticesInGame);
        info.vertexToSCC = vertexToSCC;

        for(int vertex : verticesInGame){
            if(!info.low.containsKey(vertex)) {
                strongConnect(vertex, game, vertexInGame, info);
            }
        }
        StaticSCCDecomposition decomposition = new StaticSCCDecomposition();
        decomposition.SCCs = info.result;
        decomposition.vertexToSCC = info.vertexToSCC;
        return decomposition;
    }

    private void strongConnect(int vertex, ParityGame game, boolean[] vertexInGame, TarjanInfo info) {
        info.verticesProcessedThisRound[vertex] = true;
        Stack<WorkItem> work = new Stack<>();
        Map<Integer, List<Integer>> outgoingEdges = new HashMap<>();
        work.push(new WorkItem(vertex, 0));
        while (!work.isEmpty()){
            WorkItem workItem = work.pop();
            if(workItem.nrOfEdgesProcessed == 0){
                int itemDiscoveryNumber = info.nextDiscoveryNumber;
                info.nextDiscoveryNumber++;
                info.discoveryNumber.put(workItem.vertex, itemDiscoveryNumber);
                info.low.put(workItem.vertex, itemDiscoveryNumber);
                info.processedVertices.push(workItem.vertex);
                info.verticesProcessedThisRound[workItem.vertex] = true;
            }
            boolean recurse = false;
            List<Integer> outEdges = game.getOutgoingEdges(workItem.vertex);
            //System.out.println("edges " + edges);
            for(int i = workItem.nrOfEdgesProcessed; i < outEdges.size(); i++){
                int target = outEdges.get(i);
                if(vertexInGame[target] && !info.low.containsKey(target)){ // a totally new vertex
                    work.push(new WorkItem(workItem.vertex, i+1));
                    work.push(new WorkItem(target, 0));
                    recurse = true;
                    break;
                }else if (info.verticesProcessedThisRound[target]){ // a vertex in this round
                    info.low.put(workItem.vertex, Math.min(info.low.get(workItem.vertex), info.discoveryNumber.get(target)));
                } else{ //then there is an outgoing edge from the scc
                    if(!outgoingEdges.containsKey(workItem.vertex)){
                        outgoingEdges.put(workItem.vertex, new ArrayList<>());
                    }
                    outgoingEdges.get(workItem.vertex).add(target);
                }
            }
            if(!recurse){
                if(info.low.get(workItem.vertex).equals(info.discoveryNumber.get(workItem.vertex))){
                    SCC scc = new SCC();
                    while (true){
                        int v = info.processedVertices.pop();
                        info.verticesProcessedThisRound[v] = false;
                        scc.addMember(v);
                        List<Integer> vout = outgoingEdges.get(v);
                        if(vout != null) {
                            scc.addOutgoingEdges(vout);
                        }
                        info.vertexToSCC.put(v, scc);
                        if(v == workItem.vertex){
                            break;
                        }
                    }
                    info.result.add(scc);
                    if(work.size() > 0){
                        // v is not part of the same scc so its an outgoing edge
                        if(!outgoingEdges.containsKey(work.peek().vertex)){
                            outgoingEdges.put(work.peek().vertex, new ArrayList<>());
                        }
                        outgoingEdges.get(work.peek().vertex).add(workItem.vertex);
                    }
                }
                if(work.size() > 0){
                    int v = workItem.vertex;
                    workItem = work.peek();
                    info.low.put(workItem.vertex, Math.min(info.low.get(workItem.vertex), info.low.get(v)));
                }
            }
        }

    }

    private class TarjanInfo{
        public final Stack<Integer> processedVertices;
        final boolean[] verticesProcessedThisRound;
        public final Map<Integer, Integer> low;
        public final Map<Integer, Integer> discoveryNumber;
        public int nextDiscoveryNumber;
        public final List<SCC> result;
        public Map<Integer, SCC> vertexToSCC;

        public TarjanInfo(ParityGame game, int nrOfVerticesInGame){
            processedVertices = new Stack<>();
            low = new HashMap<>(2*nrOfVerticesInGame);
            discoveryNumber = new HashMap<>(2*nrOfVerticesInGame);
            nextDiscoveryNumber = 0;
            result = new ArrayList<>();
            vertexToSCC = new HashMap<>(2*nrOfVerticesInGame);
            verticesProcessedThisRound = new boolean[game.getNrOfVertices()];
        }
    }

    private class WorkItem {
        public final int vertex;
        public final int nrOfEdgesProcessed;

        public WorkItem(int vertex, int nrOfEdgesProcessed){
            this.vertex = vertex;
            this.nrOfEdgesProcessed = nrOfEdgesProcessed;
        }

        @Override
        public String toString() {
            return "WorkItem{" +
                    "vertex=" + vertex +
                    ", nrOfEdgesProcessed=" + nrOfEdgesProcessed +
                    '}';
        }
    }

}
