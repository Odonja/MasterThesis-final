package org.anhu;

import org.anhu.utils.ResourceReader;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;

import static org.anhu.datasetTests.DatasetChecker.testAllFilesInFolderWithExcel;
import static org.anhu.datasetTests.DatasetChecker.testFile;
import static org.junit.jupiter.api.Assertions.*;

public class Application {

    private final String file = "";//your file url here
    private final String folder = ""; //your folder url here

    @Test
    void runFile(){
        if(file.length() == 0){
            System.out.println("test not executed, empty file");
        }else {
            String resultfile = "output"; // replace this string to get a different output file name
            try {
                testFile(file, resultfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void runAllFilesInFolder() throws IOException {
        if(folder.length() == 0){
            System.out.println("test not executed, empty folder");
        }else {
            testAllFilesInFolderWithExcel(folder);
        }
    }

    // to compare the answer of this program with the answer of pg solver, add the answers of 1 player to the corresponding files
    @Test
    void compareAnswer() throws IOException {
        String answerMasterThesis = ResourceReader.read("application/application_answer_master_thesis.txt");
        String answerPgsolver = ResourceReader.read("application/application_answer_pgsolver.txt");
        String[] masterThesisIndividualVertices = answerMasterThesis.replace("[", "").replace("]", "").split(", ");
        String[] pgsolverIndividualVertices = answerPgsolver.replace("{", "").replace("}", "").replace(" ",  "").split(",");
        int[] masterThesisIndividualVerticesInt = Arrays.stream(masterThesisIndividualVertices).mapToInt(Integer::parseInt).toArray();
        int[] pgsolverIndividualVerticesInt = Arrays.stream(pgsolverIndividualVertices).mapToInt(Integer::parseInt).toArray();
        System.out.println(masterThesisIndividualVerticesInt.length + " " + pgsolverIndividualVerticesInt.length);
        Arrays.sort(masterThesisIndividualVerticesInt);
        Arrays.sort(pgsolverIndividualVerticesInt);
        for(int i = 0; i < masterThesisIndividualVertices.length; i++){
            assertEquals(masterThesisIndividualVerticesInt[i], pgsolverIndividualVerticesInt[i]);
        }
    }

}
