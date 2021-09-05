package org.anhu.reader;

import org.anhu.gameStructures.ParityGame;

import java.io.*;

public class InputGameReader {

    public static final String regExpReplaceAllExcept = "[^0-9,]+";

    public void readFileFromFile(ParityGame game, File file) throws IOException {
        BufferedReader filereader = new BufferedReader(new FileReader(file)); // open the file
        int highestVertex = Integer.parseInt(filereader.readLine().replaceAll(regExpReplaceAllExcept, ""));
        game.setTotalNrOfVertices(highestVertex+1);
        processVertex(filereader, game);
        filereader.close();
    }

    public void readFileFromUrl(ParityGame game, String fileURL) throws IOException {
        BufferedReader filereader = new BufferedReader(new FileReader(fileURL)); // open the file
        int highestVertex = Integer.parseInt(filereader.readLine().replaceAll(regExpReplaceAllExcept, ""));
        game.setTotalNrOfVertices(highestVertex+1);
        processVertex(filereader, game);
        filereader.close();
    }


    private void processVertex(BufferedReader filereader, ParityGame game) throws IOException {
        int currentIndex = 0;
        String vertexInfoLine = filereader.readLine();
        if(vertexInfoLine.contains("start")){
            vertexInfoLine = filereader.readLine();
        }
        while (vertexInfoLine!= null && !vertexInfoLine.contains("timeout")){
            String[] vertexInformation = vertexInfoLine.split(" ", 4);
            int vertexIndex = Integer.parseInt(vertexInformation[0]);
            if(vertexIndex != currentIndex){
                throw new RuntimeException("missing index " + currentIndex + ", file not suitable");
            }else{
                currentIndex++;
            }
            int vertexParity;
            try {
                vertexParity = Integer.parseInt(vertexInformation[1]);
            } catch (NumberFormatException e) {
                String parityInfo = vertexInformation[1].substring(vertexInformation[1].length()-9);
                vertexParity = Integer.parseInt(parityInfo);
            }
            boolean vertexOwner = Integer.parseInt(vertexInformation[2]) == 1;
            game.addVertex(vertexIndex, vertexParity, vertexOwner);
            String edgeInfo = vertexInformation[3];
            if(edgeInfo.contains("\"")){
                edgeInfo = edgeInfo.substring(0, edgeInfo.indexOf("\"")).replaceAll(" ", "");
            }else{
                edgeInfo = edgeInfo.substring(0, edgeInfo.indexOf(";")).replaceAll(" ", "");
            }
            processVertexEdges(vertexIndex, edgeInfo, game);
            vertexInfoLine = filereader.readLine();
        }
    }


    private void processVertexEdges(int vertex, String edgelist, ParityGame game) {
        String[] outgoingEdges = edgelist.replaceAll(regExpReplaceAllExcept, "").split(",");
        for(String edge:outgoingEdges){
            int edgeDestination = Integer.parseInt(edge);
            if(!(edgeDestination < game.getNrOfVertices())){
                System.out.println("bad edge "+ edgeDestination + " in " + edgelist);
            }
            game.addEdge(vertex, edgeDestination);
        }
    }
}
