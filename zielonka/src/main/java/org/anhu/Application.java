package org.anhu;

import org.anhu.zielonkaSCCdecomposers.PartialSCCdecomposer;
import org.anhu.zielonkaSCCdecomposers.StaticSCCdecomposer;
import org.anhu.zielonkaSCCdecomposers.TarjansSCCdecomposer;
import org.anhu.gameStructures.ParityGame;
import org.anhu.reader.InputGameReader;
import org.anhu.solver.DynamicZielonkaGameSolver;
import org.anhu.solver.GameResult;
import org.anhu.solver.StaticZielonkaGameSolver;
import org.anhu.statistics.Statistic;
import org.anhu.statistics.StopWatch;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.anhu.statistics.Statistic.*;

public class Application {

    public static void main(String[] args) {
        if(args.length == 0){
            System.out.println("no input file or folder specified");
            return;
        }
        if(Arrays.asList(args).contains("h")){
            System.out.println("commands:");
            System.out.println("a: print the answer of the game");
            System.out.println("i: print all additional information");
            System.out.println("t: print the timing of zielonka's algorithm");
            System.out.println("F: file specified is a folder, runs all .gm files found in the specified folder");
            System.out.println("D: run Zielonka's algorithm with dynamic SCC maintenance");
            System.out.println("P: run Zielonka's algorithm with partial re-decomposition");
            System.out.println("T: run Zielonka's algorithm with Tarjan's algorithm");
            return;
        }

        File[] files = getGameFiles(args);
        if(files == null){
            return;
        }

        for(File file : files){
            LocalDateTime myDateObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("MM-dd_HH-mm-ss");
            System.out.println("-----------------------------------------processing:" + file.getName() + " at "
                    + myDateObj.format(myFormatObj) + "-----------------------------------------");

            ParityGame game = new ParityGame();
            InputGameReader reader = new InputGameReader();
            try {
                reader.readFileFromFile(game, file);
                if(Arrays.asList(args).contains("i")){
                    System.out.println("number of vertices: " + game.getNrOfVertices());
                }

                GameResult gameResult = null;
                if(Arrays.asList(args).contains("T")){
                    gameResult = runWithTarjan(game, args);
                }

                if(Arrays.asList(args).contains("P")){
                    gameResult = runWithPartial(game, args);
                }

                if(Arrays.asList(args).contains("D")){
                    gameResult = runWithDynamic(game, args);
                }
                if(gameResult != null && Arrays.asList(args).contains("a")){
                    System.out.println("\nvertices won by player 0:");
                    System.out.println(gameResult.getVerticesWonByPlayer0());
                    System.out.println("vertices won by player 1:");
                    System.out.println(gameResult.getVerticesWonByPlayer1());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Statistic.reset();
        }
    }

    public static File[] getGameFiles(String[] args){
        if(Arrays.asList(args).contains("F")){
            File directory = new File(args[args.length-1]);
            File[] allFiles = directory.listFiles((dir, name) -> name.endsWith(".gm"));
            if(allFiles == null || allFiles.length == 0){
                System.out.println("no files were found in folder " + args[args.length-1]);
                return null;
            }
            return allFiles;
        }else{
            if(!args[args.length-1].contains(".gm")){
                System.out.println("last argument was not a .gm file");
                return null;
            }
            return new File[]{new File(args[args.length-1])};
        }
    }

    private static GameResult runWithTarjan(ParityGame game, String[] args) {
        System.out.println("\nRunning Zielonka's algorithm with Tarjan....");
        StopWatch stopWatch = new StopWatch();
        StaticZielonkaGameSolver solver = new StaticZielonkaGameSolver();
        StaticSCCdecomposer decomposerTarjan = new TarjansSCCdecomposer();
        stopWatch.start();
        GameResult solutionTarjan = Statistic.registerExecutionTimeWIthReturn(
                Statistic.Event.RUNZIELONKA_T, () -> solver.solve(game, decomposerTarjan));
        stopWatch.stop();
        if(Arrays.asList(args).contains("t")){
            System.out.println("Zielonka's algorithm with Tarjan took: " + stopWatch);
        }
        if(Arrays.asList(args).contains("i")){
            int tarjanCalls = Statistic.getEventData(Statistic.Event.TARJAN_T).getNrOfTimesCalled();
            long tarjanVertices = Statistic.getEventData(Statistic.Event.NROFVERTICES_T).getIntegerStatistic();
            long tarjanTiming = Statistic.getEventData(Statistic.Event.TARJAN_T).getTotalTime();
            System.out.println("number of calls to Tarjan's algorithm: " + tarjanCalls);
            System.out.println("number of vertices decomposed by Tarjan's algorithm: " + tarjanVertices);
            System.out.println("total time spend by Tarjan's algorithm: " + timeToString(tarjanTiming));
        }
        return solutionTarjan;
    }

    private static GameResult runWithPartial(ParityGame game, String[] args) {
        System.out.println("\nRunning Zielonka's algorithm with partial....");
        StopWatch stopWatch = new StopWatch();
        StaticZielonkaGameSolver solver = new StaticZielonkaGameSolver();
        StaticSCCdecomposer decomposerPartial = new PartialSCCdecomposer();
        stopWatch.start();
        GameResult solutionPartial = Statistic.registerExecutionTimeWIthReturn(
                Statistic.Event.RUNZIELONKA_P, () -> solver.solve(game, decomposerPartial));
        stopWatch.stop();
        if(Arrays.asList(args).contains("t")){
            System.out.println("Zielonka's algorithm with partial took: " + stopWatch);
        }
        if(Arrays.asList(args).contains("i")){
            int tarjanCalls = Statistic.getEventData(Statistic.Event.TARJAN_P).getNrOfTimesCalled();
            long tarjanVertices = Statistic.getEventData(Statistic.Event.NROFVERTICES_P).getIntegerStatistic();
            long prepTiming = Statistic.getEventData(Statistic.Event.PARTIALPREP).getTotalTime();
            long tarjanTiming = Statistic.getEventData(Statistic.Event.TARJAN_T).getTotalTime();
            System.out.println("number of calls to Tarjan's algorithm(partial): " + tarjanCalls);
            System.out.println("number of vertices decomposed by Tarjan's algorithm(partial): " + tarjanVertices);
            System.out.println("total time spend preparing for Tarjan's algorithm(partial): " + timeToString(prepTiming));
            System.out.println("total time spend by Tarjan's algorithm(partial): " + timeToString(tarjanTiming));
        }
        return solutionPartial;
    }

    private static GameResult runWithDynamic(ParityGame game, String[] args) {
        System.out.println("\nRunning Zielonka's algorithm with dynamic SCC maintenance....");
        StopWatch stopWatch = new StopWatch();
        DynamicZielonkaGameSolver dynamicSolver = new DynamicZielonkaGameSolver();
        stopWatch.start();
        GameResult solutionDynamic = Statistic.registerExecutionTimeWIthReturnAndTimer(
                Statistic.Event.RUNZIELONKA_D, () -> dynamicSolver.solve(game));
        stopWatch.stop();
        if(Arrays.asList(args).contains("t")){
            System.out.println("Zielonka's algorithm with dynamic SCC maintenance took: " + stopWatch);
        }
        if(Arrays.asList(args).contains("i")){
            long treesize = Statistic.getEventData(Statistic.Event.SIZEOFLONGESTTREE).getIntegerStatistic();
            long nodesUpdated = Statistic.getEventData(Statistic.Event.UPDATENODE).getIntegerStatistic();
            long trivialSCC = Statistic.getEventData(Statistic.Event.NROFTRIVIALSCCS).getIntegerStatistic();
            long nonTrivialSCC = Statistic.getEventData(Statistic.Event.NROFNONTRIVIALSCCS).getIntegerStatistic();
            long buildTimeTree = Statistic.getEventData(Statistic.Event.DECOMPOSE).getTotalTime();
            long updateDecmp = Statistic.getEventData(Statistic.Event.UPDATEDYNAMICDECOMP).getTotalTime() +
                    Statistic.getEventData(Statistic.Event.COPYTREEWITHDELETEEDGE).getTotalTime();

            System.out.println("number of trivial SCCs (leaf node trees): " + trivialSCC);
            System.out.println("number of non trivial SCCs (trees with innernode root): " + nonTrivialSCC);
            System.out.println("Size of the longest tree in the decomposition: " + treesize);
            System.out.println("number of nodes updated: " + nodesUpdated);
            System.out.println("time spend to build the dynamic decomposition: " + timeToString(buildTimeTree));
            System.out.println("time spend to updating the dynamic decomposition: " + timeToString(updateDecmp));
        }
        return solutionDynamic;
    }

    private static String timeToString(long totalTime) {
        int hours = (int) (totalTime / nanosecondsInAnhour);
        int minutes = (int) ((totalTime / nanosecondsInAminute) % 60);
        DecimalFormat df = new DecimalFormat("#.####");
        double seconds = ((double) totalTime / nanosecondsInASecond) % 60;
        if(hours > 0) {
            return hours + " Hours " + minutes + " Minutes " + df.format(seconds) + " Seconds";
        }else if(minutes > 0){
            return minutes + " Minutes " + df.format(seconds) + " Seconds";
        }else{
            return df.format(seconds) + " Seconds";
        }
    }

}
