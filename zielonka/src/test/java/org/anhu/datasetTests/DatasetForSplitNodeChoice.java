package org.anhu.datasetTests;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.anhu.datasetTests.DatasetChecker.testAllFilesInFolderWithExcel;

public class DatasetForSplitNodeChoice {


    public final static String directory = "";

    @Test
    void modelchecking_pgsolver() throws IOException {
        if(directory.length() == 0){
            System.out.println("test not executed, empty directory");
        }else {
            String directoryPath = directory + "datasetsForSplitNodeComparison";
            testAllFilesInFolderWithExcel(directoryPath);
        }
    }
}
