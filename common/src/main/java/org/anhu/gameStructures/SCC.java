package org.anhu.gameStructures;

import java.util.ArrayList;
import java.util.List;

public class SCC {

    public final List<Integer> members;
    public final List<Integer> outgoingEdges;

    public SCC(){
        members = new ArrayList<>();
        outgoingEdges = new ArrayList<>();
    }

    public void addMember(int member){
        members.add(member);
    }

    public void addOutgoingEdges(List<Integer> outgoingEdges){
        this.outgoingEdges.addAll(outgoingEdges);
    }

    @Override
    public String toString() {
        return "SCC{" +
                "members=" + members +
                ", outgoingEdges=" + outgoingEdges +
                '}';
    }
}
