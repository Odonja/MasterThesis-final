package org.anhu.sccAlgorithms;

import org.anhu.gameStructures.ParityGame;
import org.anhu.gameStructures.dynamicGameStructures.DynamicSCCDecomposition;
import org.anhu.gameStructures.dynamicGameStructures.SCCTreeNodeEdge;
import org.anhu.gameStructures.dynamicGameStructures.VertexEdge;
import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.InnerNode;
import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.LeafNode;
import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.SCCTreeNode;
import org.anhu.gameStructures.dynamicGameStructures.nodeStructures.SplitNode;
import org.anhu.statistics.Statistic;

import java.util.*;

public class DynamicDecomposer {
    public static final int FIRST_MEMBER = 0;

    public DynamicSCCDecomposition decompose(ParityGame game, boolean[] vertexInGame, int nrOfVerticesInGame){
        DynamicSCCDecomposition decomposition = new DynamicSCCDecomposition();
        DynamicTarjanResult tarjanFullGraphDecomposition = dynamicTarjanFullGame(game, vertexInGame, nrOfVerticesInGame);
        List<TemporarySCC> fullGraphDecomposition = tarjanFullGraphDecomposition.result;
        Stack<InnerNodeSCC> nodesToBeMade = new Stack<>();
        for(TemporarySCC scc : fullGraphDecomposition){
            SCCTreeNode tree;
            if(scc.members.size() == 1){
                tree = new LeafNode(scc.members.get(FIRST_MEMBER));

            }else{
//                tree = new InnerNode(new SplitNode(scc.members.get(FIRST_MEMBER)));
//                tree = new InnerNode(getMaxPrioritySplitNode(game, scc.members));
//                tree = new InnerNode(getMaxConnectedSplitNode(game, scc.members));
                tree = new InnerNode(getMaxConnectedMaxPrioSplitNode(game, scc.members));
                nodesToBeMade.push(new InnerNodeSCC(tree, scc, 1));
            }
            decomposition.addNode(tree);
            decomposition.addRootVertices(tree, scc.members);
            for(VertexEdge edge : scc.outgoingEdges){
                decomposition.addEdge(edge);
            }
        }
        if(createTrees(game, decomposition, nodesToBeMade)){
            return decomposition;
        }else{
            Statistic.eventTimedOut(Statistic.Event.DECOMPOSE);
            return null;
        }
    }

    private SCCTreeNode getMaxPrioritySplitNode(ParityGame game, List<Integer> vertices){
        int vertex = vertices.get(0);
        int priority = game.getPriority(vertex);
        for(int v : vertices){
            int p = game.getPriority(v);
            if(p > priority){
                vertex = v;
                priority = p;
            }
        }
        return new SplitNode(vertex);
    }

    private SCCTreeNode getMaxConnectedSplitNode(ParityGame game, List<Integer> vertices){
        int vertex = vertices.get(0);
        int nrOfConnections = game.getIncomingEdges(vertex).size() + game.getOutgoingEdges(vertex).size();
        for(int v : vertices){
            int c = game.getIncomingEdges(v).size() + game.getOutgoingEdges(v).size();
            if(c > nrOfConnections){
                vertex = v;
                nrOfConnections = c;
            }
        }
        return new SplitNode(vertex);
    }

    private SCCTreeNode getMaxConnectedMaxPrioSplitNode(ParityGame game, List<Integer> vertices){
        int vertex = vertices.get(0);
        int nrOfConnections = game.getIncomingEdges(vertex).size() + game.getOutgoingEdges(vertex).size();
        int priority = game.getPriority(vertex);
        for(int v : vertices){
            int c = game.getIncomingEdges(v).size() + game.getOutgoingEdges(v).size();
            if(c > nrOfConnections){
                vertex = v;
                nrOfConnections = c;
            }else if(c == nrOfConnections){
                int p = game.getPriority(v);
                if(p > priority){
                    vertex = v;
                    priority = p;
                }
            }
        }
        return new SplitNode(vertex);
    }

    private boolean createTrees(ParityGame game, DynamicSCCDecomposition decomposition, Stack<InnerNodeSCC> nodesToBeMade) {
        int checktimerTimer = 500;
        while(!nodesToBeMade.isEmpty()){
            if(checktimerTimer-- == 0){
                if(Statistic.timerExpired()){
                    return false;
                }else{
                    checktimerTimer = 500;
                }
            }
            InnerNodeSCC nodeInProgress = nodesToBeMade.pop();
            Statistic.incrementIntegerStatistic(Statistic.Event.CREATINNERNODE);
            Statistic.setHighestIntegerStatistic(Statistic.Event.SIZEOFLONGESTTREE, nodeInProgress.depth);
            SCCTreeNode innerNodeInProgress = nodeInProgress.node;
            SCCTreeNode splitNodeOfInnerNodeInProgress = innerNodeInProgress.getSplitNode();
            int splitNodeVertex = splitNodeOfInnerNodeInProgress.getVertex();

            List<Integer> sccMembers = nodeInProgress.scc.members;

            boolean[] vertexInGame = new boolean[game.getNrOfVertices()];
            for(int member : sccMembers){
                vertexInGame[member] = true;
            }
            vertexInGame[splitNodeVertex] = false;
            DynamicTarjanResult sccGraphDecomposition = dynamicTarjanPartialGame(game, vertexInGame, sccMembers);

            Map<TemporarySCC, SCCTreeNode> sccToNode = new HashMap<>(2*sccGraphDecomposition.result.size());
            for(TemporarySCC scc : sccGraphDecomposition.result){
                SCCTreeNode child;
                if(scc.members.size() == 1){
                    child = new LeafNode(scc.members.get(FIRST_MEMBER));
                }else{
//                    child = new InnerNode(new SplitNode(scc.members.get(FIRST_MEMBER)));
//                    child = new InnerNode(getMaxPrioritySplitNode(game, scc.members));
//                    child = new InnerNode(getMaxConnectedSplitNode(game, scc.members));
                    child = new InnerNode(getMaxConnectedMaxPrioSplitNode(game, scc.members));
                    nodesToBeMade.push(new InnerNodeSCC(child, scc, nodeInProgress.depth+1));
                }
                nodeInProgress.node.addChild(child);
                sccToNode.put(scc, child);
            }


            // add the edges of the split node
            for(int target : game.getOutgoingEdges(splitNodeVertex)){
                if(vertexInGame[target]){ // edge is in current scc
                    TemporarySCC targetSCC =  sccGraphDecomposition.vertexToSCC.get(target);
                    SCCTreeNode targetNode = sccToNode.get(targetSCC);
                    innerNodeInProgress.addEdge(new SCCTreeNodeEdge(splitNodeOfInnerNodeInProgress, targetNode,
                            splitNodeVertex, target));
                    decomposition.addEdgeStoragePlace(innerNodeInProgress, new VertexEdge(splitNodeVertex, target));
                }
            }

            for(int source : game.getIncomingEdges(splitNodeVertex)){
                if(vertexInGame[source]){ // edge is in current scc
                    TemporarySCC sourceSCC =  sccGraphDecomposition.vertexToSCC.get(source);
                    SCCTreeNode sourceNode = sccToNode.get(sourceSCC);
                    innerNodeInProgress.addEdge(new SCCTreeNodeEdge(sourceNode, splitNodeOfInnerNodeInProgress,
                            source, splitNodeVertex));
                    decomposition.addEdgeStoragePlace(innerNodeInProgress, new VertexEdge(source, splitNodeVertex));
                }
            }

            for(TemporarySCC scc : sccGraphDecomposition.result){
                for(VertexEdge edge : scc.outgoingEdges){
                    TemporarySCC sourceSCC = sccGraphDecomposition.vertexToSCC.get(edge.sourceVertex);
                    TemporarySCC targetSCC =  sccGraphDecomposition.vertexToSCC.get(edge.targetVertex);
                    SCCTreeNode sourceNode = sccToNode.get(sourceSCC);
                    SCCTreeNode targetNode = sccToNode.get(targetSCC);
                    innerNodeInProgress.addEdge(new SCCTreeNodeEdge(sourceNode, targetNode, edge.sourceVertex, edge.targetVertex));
                    decomposition.addEdgeStoragePlace(innerNodeInProgress, new VertexEdge(edge.sourceVertex, edge.targetVertex));
                }
            }

        }
        return true;
    }

    public DynamicTarjanResult dynamicTarjanFullGame(ParityGame game, boolean[] vertexInGame, int nrOfVerticesInGame){
        TarjanInfo info = new TarjanInfo(game, nrOfVerticesInGame);

        for(int vertex = 0; vertex < game.getNrOfVertices(); vertex++){
            if(vertexInGame[vertex] && !info.low.containsKey(vertex)){
                strongConnect(vertex, game, vertexInGame ,info);
            }
        }
        return new DynamicTarjanResult(info.result, info.vertexToSCC);
    }

    public DynamicTarjanResult dynamicTarjanPartialGame(ParityGame game, boolean[] vertexInGame, List<Integer> verticesInGame){
        int nrOfVerticesInGame = verticesInGame.size();
        TarjanInfo info = new TarjanInfo(game, nrOfVerticesInGame);

        for(int vertex : verticesInGame){
            if(vertexInGame[vertex] && !info.low.containsKey(vertex)){
                strongConnect(vertex, game, vertexInGame ,info);
            }
        }
        return new DynamicTarjanResult(info.result, info.vertexToSCC);
    }

    private void strongConnect(int vertex, ParityGame game, boolean[] vertexInGame, TarjanInfo info) {

        info.verticesProcessedThisRound[vertex] = true;

        Stack<WorkItem> work = new Stack<>();
        work.push(new WorkItem(vertex, 0));

        Map<Integer, List<VertexEdge>> outgoingEdges = new HashMap<>();

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
            for(int i = workItem.nrOfEdgesProcessed; i < outEdges.size(); i++){
                int target = outEdges.get(i);
                if(vertexInGame[target] && !info.low.containsKey(target)){ // a totally new vertex
                    work.push(new WorkItem(workItem.vertex, i+1));
                    work.push(new WorkItem(target, 0));
                    recurse = true;
                    break;

                }else if (info.verticesProcessedThisRound[target]){ // a vertex in this round
                    info.low.put(workItem.vertex, Math.min(info.low.get(workItem.vertex), info.discoveryNumber.get(target)));
                } else if (vertexInGame[target]){ //then there is an outgoing edge from the scc
                    if(!outgoingEdges.containsKey(workItem.vertex)){
                        outgoingEdges.put(workItem.vertex, new ArrayList<>());
                    }
                    outgoingEdges.get(workItem.vertex).add(new VertexEdge(workItem.vertex, target));
                }
            }
            if(!recurse){
                if(info.low.get(workItem.vertex).equals(info.discoveryNumber.get(workItem.vertex))){
                    TemporarySCC scc = new TemporarySCC();
                    while (true){

                        int v = info.processedVertices.pop();
                        info.verticesProcessedThisRound[v] = false;
                        scc.members.add(v);
                        List<VertexEdge> vout = outgoingEdges.get(v);
                        if(vout != null) {
                            scc.outgoingEdges.addAll(vout);
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
                        outgoingEdges.get(work.peek().vertex).add(new VertexEdge(work.peek().vertex, workItem.vertex));

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
        public final List<TemporarySCC> result;
        public final Map<Integer, TemporarySCC> vertexToSCC;

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

    private class TemporarySCC{
        public final List<Integer> members;
        public final List<VertexEdge> outgoingEdges;

        public TemporarySCC(){
            members = new LinkedList<>();
            outgoingEdges = new ArrayList<>();
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

    private class DynamicTarjanResult {

        public final List<TemporarySCC> result;
        public final Map<Integer, TemporarySCC> vertexToSCC;

        public DynamicTarjanResult(List<TemporarySCC> result, Map<Integer, TemporarySCC> vertexToSCC){
            this.result = result;
            this.vertexToSCC = vertexToSCC;
        }

    }

    private class InnerNodeSCC {
        public final SCCTreeNode node;
        public final TemporarySCC scc;
        public final int depth;

        private InnerNodeSCC(SCCTreeNode node, TemporarySCC scc, int depth){
            this.node = node;
            this.scc = scc;
            this.depth = depth;
        }

    }

}
