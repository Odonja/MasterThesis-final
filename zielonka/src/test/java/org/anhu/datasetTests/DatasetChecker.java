package org.anhu.datasetTests;

import org.anhu.gameStructures.SCC;
import org.anhu.gameStructures.StaticSCCDecomposition;
import org.anhu.sccAlgorithms.TarjansDecomposer;
import org.anhu.solver.*;
import org.anhu.zielonkaSCCdecomposers.PartialSCCdecomposer;
import org.anhu.zielonkaSCCdecomposers.StaticSCCdecomposer;
import org.anhu.zielonkaSCCdecomposers.TarjansSCCdecomposer;
import org.anhu.gameStructures.ParityGame;
import org.anhu.reader.InputGameReader;
import org.anhu.statistics.Statistic;
import org.anhu.statistics.StopWatch;
import org.apache.commons.io.comparator.SizeFileComparator;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.anhu.ResultChecker.checkAllResultsAreEqual;
import static org.junit.Assert.assertEquals;

public class DatasetChecker {

    public static void testAllFilesInFolderWithExcel(String directoryPath) throws IOException {
        File directory = new File(directoryPath);
        File[] allFiles = directory.listFiles((dir, name) -> name.endsWith(".gm"));
        assert allFiles != null;
        Arrays.sort(allFiles, SizeFileComparator.SIZE_COMPARATOR);

        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("MM-dd_HH-mm-ss");
        String formattedDate = myDateObj.format(myFormatObj);

        DateTimeFormatter myFormatObj2 = DateTimeFormatter.ofPattern("HH:mm:ss");

        String path = directoryPath + "\\_results\\" + formattedDate + ".txt";
        String pathExcel = directoryPath + "\\_results\\Excel_" + formattedDate + ".txt";
        String pathAnswer = directoryPath + "\\_results\\Answer_" + formattedDate + ".txt";
        String pathError = directoryPath + "\\_results\\Error.txt";
        System.out.println(path);

        for (File file : allFiles) {
            StringBuilder sb = new StringBuilder();
            StringBuilder sbExcel = new StringBuilder();
            StringBuilder sbAnswer = new StringBuilder();
            ParityGame game = new ParityGame();
            InputGameReader reader = new InputGameReader();
            try {
                myDateObj = LocalDateTime.now();
                System.out.println("-----------------------------------------" + file.getName() + " at "
                        + myDateObj.format(myFormatObj2) + "-----------------------------------------");
                sb.append("-----------------------------------------").append(file.getName()).append("-----------------------------------------\n");
                sbAnswer.append("-----------------------------------------").append(file.getName()).append("-----------------------------------------\n");
                reader.readFileFromFile(game, file);
                int nrOfVertices = game.getNrOfVertices();
                sb.append(nrOfVertices).append(" vertices\n");
                System.out.println(nrOfVertices + " vertices");
                sbExcel.append(file.getName().replace(".gm", " ")).append(nrOfVertices);
                findSCCStatistics(game);
                StaticSCCdecomposer decomposerPartial = new PartialSCCdecomposer();
                StaticSCCdecomposer decomposerTarjan = new TarjansSCCdecomposer();

                StopWatch stopWatch = new StopWatch();
                StaticZielonkaGameSolver2 solver = new StaticZielonkaGameSolver2();
                stopWatch.start();
                GameResult solutionTarjan = Statistic.registerExecutionTimeWIthReturnAndTimer(
                        Statistic.Event.RUNZIELONKA_T, () -> solver.solve(game, decomposerTarjan));
                stopWatch.stop();
                if (solutionTarjan == null) {
                    Statistic.eventTimedOut(Statistic.Event.RUNZIELONKA_T);
                    System.out.println("tarjan: timed out");
                    sb.append("tarjan: timed out").append("\n");
                } else {
                    System.out.println("tarjan: " + stopWatch);
                    sb.append("tarjan: ").append(stopWatch).append("\n");
                    assertEquals(nrOfVertices, solutionTarjan.getVerticesWonByPlayer0().size() + solutionTarjan.getVerticesWonByPlayer1().size());
                }

                System.out.print("partial: ");
                stopWatch.start();
                GameResult solutionPartial = Statistic.registerExecutionTimeWIthReturnAndTimer(
                        Statistic.Event.RUNZIELONKA_P, () -> solver.solve(game, decomposerPartial));
                stopWatch.stop();
                if (solutionPartial == null) {
                    Statistic.eventTimedOut(Statistic.Event.RUNZIELONKA_P);
                    System.out.println("partial: timed out");
                    sb.append("partial: timed out").append("\n");
                    sbAnswer.append("partial: timed out").append("\n");
                } else {
                    System.out.println(stopWatch);
                    sb.append("partial: ").append(stopWatch).append("\n");
                    sbAnswer.append("won by player 0:\n");
                    sbAnswer.append(solutionPartial.getVerticesWonByPlayer0()).append("\n");
                    sbAnswer.append("won by player 1:\n");
                    sbAnswer.append(solutionPartial.getVerticesWonByPlayer1()).append("\n");
                    writeToFile(pathAnswer, sbAnswer);
                    assertEquals(nrOfVertices, solutionPartial.getVerticesWonByPlayer0().size() + solutionPartial.getVerticesWonByPlayer1().size());
                }

                DynamicZielonkaGameSolver2 dynamicSolver = new DynamicZielonkaGameSolver2();
                stopWatch.start();
                GameResult solutionDynamic = Statistic.registerExecutionTimeWIthReturnAndTimer(
                        Statistic.Event.RUNZIELONKA_D, () -> dynamicSolver.solve(game));
                stopWatch.stop();
                if (solutionDynamic == null) {
                    Statistic.eventTimedOut(Statistic.Event.RUNZIELONKA_D);
                    System.out.println("dynamic: timed out");
                    sb.append("dynamic: timed out").append("\n");
                } else {
                    System.out.println("dynamic: " + stopWatch);
                    sb.append("dynamic: ").append(stopWatch).append("\n");
                    assertEquals(nrOfVertices, solutionDynamic.getVerticesWonByPlayer0().size() + solutionDynamic.getVerticesWonByPlayer1().size());
                }

                Statistic.writeAllNonVariableEventsExcel(sbExcel);

                Statistic.printAllEvents();
                Statistic.writeAllEvents(sb);
                writeToFile(path, sb);

                if (solutionTarjan != null && solutionPartial != null && solutionDynamic != null) {
                    checkAllResultsAreEqual(solutionTarjan, solutionPartial, solutionDynamic);
                } else if (solutionTarjan != null && solutionPartial != null) {
                    checkAllResultsAreEqual(solutionTarjan, solutionPartial);
                } else if (solutionTarjan != null && solutionDynamic != null) {
                    checkAllResultsAreEqual(solutionTarjan, solutionDynamic);
                } else if (solutionPartial != null && solutionDynamic != null) {
                    checkAllResultsAreEqual(solutionPartial, solutionDynamic);
                }


                for (int i = 1; i < Statistic.getNrOfIterationsNeeded(Statistic.Event.RUNZIELONKA_T); i++) {
                    Statistic.resetAndStoreTime(Statistic.Event.RUNZIELONKA_T);
                    Statistic.resetAndStoreTime(Statistic.Event.TARJAN_T);
                    System.out.println("tarjan " + i + "start");
                    stopWatch.start();
                    Statistic.registerExecutionTime(Statistic.Event.RUNZIELONKA_T, () ->
                            solver.solve(game, decomposerTarjan));
                    stopWatch.stop();
                    System.out.println("tarjan " + i + "done in " + stopWatch);
                    writeToFile(path, new StringBuilder(stopWatch + "\n"));
                }
                Statistic.resetAndStoreTime(Statistic.Event.RUNZIELONKA_T);
                Statistic.resetAndStoreTime(Statistic.Event.TARJAN_T);


                for (int i = 1; i < Statistic.getNrOfIterationsNeeded(Statistic.Event.RUNZIELONKA_P); i++) {
                    Statistic.resetAndStoreTime(Statistic.Event.RUNZIELONKA_P);
                    Statistic.resetAndStoreTime(Statistic.Event.TARJAN_P);
                    Statistic.resetAndStoreTime(Statistic.Event.PARTIALPREP);

                    System.out.println("partial " + i + "start");
                    stopWatch.start();
                    Statistic.registerExecutionTime(Statistic.Event.RUNZIELONKA_P, () ->
                            solver.solve(game, decomposerPartial));
                    stopWatch.stop();
                    System.out.println("partial " + i + "done in " + stopWatch);
                    writeToFile(path, new StringBuilder(stopWatch + "\n"));
                }
                Statistic.resetAndStoreTime(Statistic.Event.RUNZIELONKA_P);
                Statistic.resetAndStoreTime(Statistic.Event.TARJAN_P);
                Statistic.resetAndStoreTime(Statistic.Event.PARTIALPREP);


                for (int i = 1; i < Statistic.getNrOfIterationsNeeded(Statistic.Event.RUNZIELONKA_D); i++) {
                    Statistic.resetAndStoreTime(Statistic.Event.RUNZIELONKA_D);
                    Statistic.resetAndStoreTime(Statistic.Event.DECOMPOSE);
                    Statistic.resetAndStoreTime(Statistic.Event.UPDATEDYNAMICDECOMP);
                    Statistic.resetAndStoreTime(Statistic.Event.COPYTREEWITHDELETEEDGE);

                    System.out.println("dynamic " + i + "start");
                    stopWatch.start();
                    Statistic.registerExecutionTime(Statistic.Event.RUNZIELONKA_D, () -> dynamicSolver.solve(game));
                    stopWatch.stop();
                    System.out.println("dynamic " + i + "done in " + stopWatch);
                    writeToFile(path, new StringBuilder(stopWatch + "\n"));
                }
                Statistic.resetAndStoreTime(Statistic.Event.RUNZIELONKA_D);
                Statistic.resetAndStoreTime(Statistic.Event.DECOMPOSE);
                Statistic.resetAndStoreTime(Statistic.Event.UPDATEDYNAMICDECOMP);
                Statistic.resetAndStoreTime(Statistic.Event.COPYTREEWITHDELETEEDGE);

                Statistic.writeAverageVariableEventsExcel(sbExcel);
                writeToFile(pathExcel, sbExcel);

            } catch (IOException e) {
                System.out.println("io exception");
                writeToFile(pathError, new StringBuilder(file.getName() + "\nio exception\n\n"));
                e.printStackTrace();
            } catch(Exception e){
                sb.append("some exception\n").append(e);
                System.out.println("some exception");
                writeToFile(pathError, new StringBuilder(file.getName() + "\nsome exception\n" + e +"\n\n"));
                writeToFile(path, sb);
                e.printStackTrace();
            }
            Statistic.reset();
        }

    }

    private static void findSCCStatistics(ParityGame game) {
        final TarjansDecomposer decomposer = new TarjansDecomposer();
        boolean[] gamestate = new boolean[game.getNrOfVertices()];
        Arrays.fill(gamestate, true);
        StaticSCCDecomposition sccs = decomposer.run(game, gamestate, game.getNrOfVertices());
        for(SCC scc : sccs.SCCs){
            if(scc.members.size() == 1){
                Statistic.incrementIntegerStatistic(Statistic.Event.NROFTRIVIALSCCS);
            }else{
                Statistic.incrementIntegerStatistic(Statistic.Event.NROFNONTRIVIALSCCS);
            }
        }
    }

    private static void writeToFile(String filepath, StringBuilder sb) throws IOException {
        FileOutputStream fos = new FileOutputStream(filepath, true);
        fos.write(sb.toString().getBytes());
        fos.close();
    }

    public static void testFile(String fileUrl, String outputFileUrl) throws IOException {
        StringBuilder sb = new StringBuilder();

        ParityGame game = new ParityGame();
        InputGameReader reader = new InputGameReader();
        try {
            reader.readFileFromUrl(game, fileUrl);
            sb.append("-----------------------------------------").append(fileUrl).append("-----------------------------------------\n");
            sb.append(game.getNrOfVertices()).append(" vertices\n");
            System.out.println("-----------------------------------------" + fileUrl + "-----------------------------------------");
            System.out.println(game.getNrOfVertices() + " vertices");

            StaticSCCdecomposer decomposerPartial = new PartialSCCdecomposer();
            StaticSCCdecomposer decomposerTarjan = new TarjansSCCdecomposer();

            StopWatch stopWatch = new StopWatch();
            StaticZielonkaGameSolver solver = new StaticZielonkaGameSolver();
            stopWatch.start();
            GameResult solutionTarjan = solver.solve(game, decomposerTarjan);
            stopWatch.stop();
            System.out.println("tarjan: " + stopWatch);
            sb.append("tarjan: ").append(stopWatch).append("\n");

            stopWatch.start();
            GameResult solutionPartial = solver.solve(game, decomposerPartial);
            stopWatch.stop();
            System.out.println("partial: " + stopWatch);
            sb.append("partial: ").append(stopWatch).append("\n");

            DynamicZielonkaGameSolver dynamicSolver = new DynamicZielonkaGameSolver();
            stopWatch.start();
            GameResult solutionDynamic = dynamicSolver.solve(game);
            stopWatch.stop();
            System.out.println("dynamic: " + stopWatch);
            sb.append("dynamic: ").append(stopWatch).append("\n");

            Statistic.printAllEvents();
            Statistic.writeAllEvents(sb);
            Statistic.reset();

            writeToFile(outputFileUrl, sb);
            if (solutionTarjan != null && solutionPartial != null && solutionDynamic != null) {
                checkAllResultsAreEqual(solutionTarjan, solutionPartial, solutionDynamic);
            } else if (solutionTarjan != null && solutionPartial != null) {
                checkAllResultsAreEqual(solutionTarjan, solutionPartial);
            } else if (solutionTarjan != null && solutionDynamic != null) {
                checkAllResultsAreEqual(solutionTarjan, solutionDynamic);
            } else if (solutionPartial != null && solutionDynamic != null) {
                checkAllResultsAreEqual(solutionPartial, solutionDynamic);
            }
        } catch (Exception e) {
            writeToFile(outputFileUrl, sb);
            e.printStackTrace();
        }
    }
}
