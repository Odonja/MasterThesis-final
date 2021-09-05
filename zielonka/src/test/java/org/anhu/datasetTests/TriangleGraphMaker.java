package org.anhu.datasetTests;

import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class TriangleGraphMaker {

   // @Test // uncomment to run
    void make() throws IOException {
        final int nrOfSCCs = 300000;
        final int nrOfVerticesInSCC = 3;
        int owner = 0;
        int parity = 0;
        int totalNrOfVertices = nrOfSCCs * nrOfVerticesInSCC;
        StringBuilder sb = new StringBuilder();
        sb.append("parity ").append(totalNrOfVertices-1).append(";\n");
        int vertex;
        for(vertex = 0; vertex < totalNrOfVertices-3; vertex+=nrOfVerticesInSCC){
            makeSCCWith3Vertices(sb, vertex, owner, parity);
            owner = (owner+1)%2;
            parity++;
        }

        sb.append(vertex).append(" ").append(parity).append(" ").append(owner).append(" ");
        sb.append(vertex+1).append(" \"\";\n");

        sb.append(vertex + 1).append(" ").append(parity).append(" ").append(owner).append(" ");
        sb.append(vertex + 2).append(" \"\";\n");

        sb.append(vertex + 2).append(" ").append(parity).append(" ").append(owner).append(" ");
        sb.append(vertex).append(" \"\";\n");


        String outputFile = "directory\\myDatasets\\SCC" + nrOfSCCs + "_size3.gm";
        Writer writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write(sb.toString());
        writer.close();
    }

    private void makeSCCWith3Vertices(StringBuilder sb, int firstVertex, int owner, int parity){
        final int nrOfVerticesInSCC = 3;
        sb.append(firstVertex).append(" ").append(parity).append(" ").append(owner).append(" ");
        sb.append(firstVertex+1).append(",").append(firstVertex+nrOfVerticesInSCC).append(" \"\";\n");

        sb.append(firstVertex + 1).append(" ").append(parity).append(" ").append(owner).append(" ");
        sb.append(firstVertex + 2).append(" \"\";\n");

        sb.append(firstVertex + 2).append(" ").append(parity).append(" ").append(owner).append(" ");
        sb.append(firstVertex).append(" \"\";\n");
    }
}
