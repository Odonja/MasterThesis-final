package org.anhu;

import org.anhu.solver.GameResult;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResultChecker {

    public static void checkAllResultsAreEqual(GameResult solution1, GameResult solution2, GameResult solution3) {
        Set<Integer> solution1_0 = new HashSet<>(solution1.getVerticesWonByPlayer0());
        Set<Integer> solution1_1 = new HashSet<>(solution1.getVerticesWonByPlayer1());

        Set<Integer> solution2_0 = new HashSet<>(solution2.getVerticesWonByPlayer0());
        Set<Integer> solution2_1 = new HashSet<>(solution2.getVerticesWonByPlayer1());

        Set<Integer> solution3_0 = new HashSet<>(solution3.getVerticesWonByPlayer0());
        Set<Integer> solution3_1 = new HashSet<>(solution3.getVerticesWonByPlayer1());

        for(int vertex : solution1.getVerticesWonByPlayer0()){
            assertTrue(solution2_0.contains(vertex));
            assertTrue(solution3_0.contains(vertex));
            assertFalse(solution1_1.contains(vertex));
            assertFalse(solution2_1.contains(vertex));
            assertFalse(solution3_1.contains(vertex));
        }

        for(int vertex : solution1.getVerticesWonByPlayer1()){
            assertTrue(solution2_1.contains(vertex));
            assertTrue(solution3_1.contains(vertex));
            assertFalse(solution1_0.contains(vertex));
            assertFalse(solution2_0.contains(vertex));
            assertFalse(solution3_0.contains(vertex));
        }
    }

    public static void checkAllResultsAreEqual(GameResult solution1, GameResult solution2) {
        Set<Integer> solution1_0 = new HashSet<>(solution1.getVerticesWonByPlayer0());
        Set<Integer> solution1_1 = new HashSet<>(solution1.getVerticesWonByPlayer1());

        Set<Integer> solution2_0 = new HashSet<>(solution2.getVerticesWonByPlayer0());
        Set<Integer> solution2_1 = new HashSet<>(solution2.getVerticesWonByPlayer1());

        for(int vertex : solution1.getVerticesWonByPlayer0()){
            assertTrue(solution2_0.contains(vertex));
            assertFalse(solution1_1.contains(vertex));
            assertFalse(solution2_1.contains(vertex));
        }

        for(int vertex : solution1.getVerticesWonByPlayer1()){
            assertTrue(solution2_1.contains(vertex));
            assertFalse(solution1_0.contains(vertex));
            assertFalse(solution2_0.contains(vertex));
        }
    }



}
