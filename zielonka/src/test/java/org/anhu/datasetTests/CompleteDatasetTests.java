package org.anhu.datasetTests;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import static org.anhu.datasetTests.DatasetChecker.testAllFilesInFolderWithExcel;

public class CompleteDatasetTests {

    public final static String directory = "";

    @Test
    void modelcheckingGames() throws IOException {
        if(directory.length() == 0){
            System.out.println("test not executed, empty directory");
        }else {
            String directoryPath = directory + "datasets\\modelchecking\\games";
            testAllFilesInFolderWithExcel(directoryPath);
        }
    }

    @Test
    void equivalenceHesselinkGames() throws IOException {
        if(directory.length() == 0){
            System.out.println("test not executed, empty directory");
        }else {
            String directoryPath = directory + "datasets\\equivchecking-hesselink\\games";
            testAllFilesInFolderWithExcel(directoryPath);
        }
    }

    @Test
    void mlSolverGames() throws IOException {
        if(directory.length() == 0){
            System.out.println("test not executed, empty directory");
        }else {
            String directoryPath = directory + "datasets\\mlsolver\\games";
            testAllFilesInFolderWithExcel(directoryPath);
        }
    }

    @Test
    void equivalenceGames() throws IOException {
        if(directory.length() == 0){
            System.out.println("test not executed, empty directory");
        }else {
            String directoryPath = directory + "datasets\\equivchecking\\games";
            testAllFilesInFolderWithExcel(directoryPath);
        }
    }

    @Test
    void pgSolverGames() throws IOException {
        if(directory.length() == 0){
            System.out.println("test not executed, empty directory");
        }else {
            String directoryPath = directory + "datasets\\pgsolver\\games";
            testAllFilesInFolderWithExcel(directoryPath);
        }
    }

    @Test
    void triangleGames() throws IOException {
        if(directory.length() == 0){
            System.out.println("test not executed, empty directory");
        }else {
            String directoryPath = directory + "datasets\\triangle";
            testAllFilesInFolderWithExcel(directoryPath);
        }
    }

    @Test
    void randomGames() throws IOException {
        if(directory.length() == 0){
            System.out.println("test not executed, empty directory");
        }else {
            String directoryPath = directory + "datasets\\random";
            testAllFilesInFolderWithExcel(directoryPath);
        }
    }
}
