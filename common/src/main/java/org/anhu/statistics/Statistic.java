package org.anhu.statistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;

public class Statistic {


    public final static double nanosecondsInASecond = 1000000000.0;
    public final static long nanosecondsInAminute = (long) (60 * nanosecondsInASecond);
    public final static long nanosecondsInAnhour = (long) (60 * 60 * nanosecondsInASecond);
    public final static StopWatch timer = new StopWatch();

    public static int getNrOfIterationsNeeded(Event event) {
        return eventStat.get(event).getNrOfIterationsNeeded();
    }

    public static void eventTimedOut(Event event) {
        if (!eventStat.containsKey(event)) {
            eventStat.put(event, new EventData());
        }
        eventStat.get(event).timedOut();
    }

    public enum Event {
        TARJAN_T, PARTIALPREP, TARJAN_P, DECOMPOSE, NROFTRIVIALSCCS, NROFNONTRIVIALSCCS, CREATINNERNODE, SIZEOFLONGESTTREE,
        COPYTREEWITHDELETEEDGE, UPDATEDYNAMICDECOMP, UPDATENODE, NROFVERTICES_T, NROFVERTICES_P, RUNZIELONKA_T,
        RUNZIELONKA_P, RUNZIELONKA_D, TIMER
    }


    public static class EventData {
        private final StopWatch stopWatch = new StopWatch();
        private long totalTime = 0;
        private int nrOfTimesCalled = 0;
        private long integerStatistic = 0;
        private boolean timedOut = false;
        private List<Long> times = null;

        public void startTimer() {
            stopWatch.start();
            nrOfTimesCalled++;
        }

        public void stopTimer() {
            stopWatch.stop();
            totalTime += stopWatch.elapsed();
        }

        public long getAverageTime() {
            int nrOfTimes = times.size();
            if (nrOfTimes == 1) {
                return times.get(0);
            }
            times.sort(Comparator.naturalOrder());
            int numbersUsed = nrOfTimes - 2 * (nrOfTimes / 5);
            long accumulatedTime = 0;
            for (int i = 0; i < numbersUsed; i++) {
                accumulatedTime += times.get(i);
            }
            return accumulatedTime / numbersUsed;
        }

        public void resetAndStoreTime() {
            if (times == null) {
                times = new ArrayList<>(10);
            }
            times.add(totalTime);
            totalTime = 0;
            nrOfTimesCalled = 0;
            integerStatistic = 0;
        }

        public long getTotalTime() {
            return totalTime;
        }

        public int getNrOfTimesCalled() {
            return nrOfTimesCalled;
        }

        public void incrementIntegerStatistic() {
            integerStatistic++;
        }

        public void incrementIntegerStatistic(int increment) {
            integerStatistic += increment;
        }

        public void setHighestIntegerStatistic(int value) {
            if (value > integerStatistic) {
                integerStatistic = value;
            }
        }

        public int getIntegerStatistic() {
            return (int) integerStatistic;
        }

        public long getIntegerStatisticlong() {
            return integerStatistic;
        }

        public void timedOut() {
            timedOut = true;
        }

        public boolean didTimeOut() {
            return timedOut;
        }

        @Override
        public String toString() {
            if (nrOfTimesCalled > 0) {
                long averageTime = totalTime / (long) nrOfTimesCalled;
                if (integerStatistic > 0) {
                    return "EventData{" +
                            ", \ntotalTime = " + timeToString(totalTime) +
                            ", \nnrOfTimesCalled = " + nrOfTimesCalled +
                            ", \non average = " + timeToString(averageTime) +
                            ", \nintegerStatistic = " + integerStatistic +
                            '}';
                }
                return "EventData{" +
                        ", \ntotalTime = " + timeToString(totalTime) +
                        ", \nnrOfTimesCalled = " + nrOfTimesCalled +
                        ", \non average = " + timeToString(averageTime) +
                        '}';
            } else {
                return "EventData{" +
                        ", \nintegerStatistic = " + integerStatistic +
                        '}';
            }
        }

        private String timeToString(long totalTime) {
            int hours = (int) (totalTime / nanosecondsInAnhour);
            int minutes = (int) ((totalTime / nanosecondsInAminute) % 60);
            DecimalFormat df = new DecimalFormat("#.####");
            double seconds = ((double) totalTime / nanosecondsInASecond) % 60;
            if (hours > 0) {
                return hours + " Hours " + minutes + " Minutes " + df.format(seconds) + " Seconds";
            } else if (minutes > 0) {
                return minutes + " Minutes " + df.format(seconds) + " Seconds";
            } else {
                return df.format(seconds) + " Seconds";
            }
        }

        public int getNrOfIterationsNeeded() {
            if (timedOut) {
                return 1;
            }
            int minutes = (int) ((totalTime / (60 * nanosecondsInASecond)));
            if (minutes >= 3) {
                return 1;
            } else if (minutes >= 1) {
                return 5;
            }
            return 10;
        }
    }

    public static EventData getEventData(Event event) {
        if (!eventStat.containsKey(event)) {
            return new EventData();
        }
        return eventStat.get(event);
    }

    public static EnumMap<Event, EventData> eventStat = new EnumMap<>(Event.class);

    public static void reset() {
        eventStat = new EnumMap<>(Event.class);
    }

    public static void resetAndStoreTime(Event event) {
        if (eventStat.containsKey(event)) {
            eventStat.get(event).resetAndStoreTime();
        }
    }

    public static void registerExecutionTime(Event event, Runnable r) {
        if (!eventStat.containsKey(event)) {
            eventStat.put(event, new EventData());
        }
        EventData data = eventStat.get(event);
        data.startTimer();
        r.run();
        data.stopTimer();
    }

    public interface Callable<R> {
        R call();
    }

    public static <R> R registerExecutionTimeWIthReturn(Event event, Callable<R> r) {
        if (!eventStat.containsKey(event)) {
            eventStat.put(event, new EventData());
        }
        EventData data = eventStat.get(event);
        data.startTimer();
        R returnValue = r.call();
        data.stopTimer();
        return returnValue;
    }

    public static <R> R registerExecutionTimeWIthReturnAndTimer(Event event, Callable<R> r) {
        if (!eventStat.containsKey(event)) {
            eventStat.put(event, new EventData());
        }
        EventData data = eventStat.get(event);
        timer.startWithTimeout(2 * nanosecondsInAnhour);
        data.startTimer();
        R returnValue = r.call();
        data.stopTimer();
        if (returnValue == null) {
            data.didTimeOut();
        }
        return returnValue;
    }

    public static boolean timerExpired() {
        return timer.timerExpired();
    }

    public static void incrementIntegerStatistic(Event event) {
        if (!eventStat.containsKey(event)) {
            eventStat.put(event, new EventData());
        }
        EventData data = eventStat.get(event);
        data.incrementIntegerStatistic();
    }

    public static void incrementIntegerStatistic(Event event, int increment) {
        if (!eventStat.containsKey(event)) {
            eventStat.put(event, new EventData());
        }
        EventData data = eventStat.get(event);
        data.incrementIntegerStatistic(increment);
    }

    public static void setHighestIntegerStatistic(Event event, int value) {
        if (!eventStat.containsKey(event)) {
            eventStat.put(event, new EventData());
        }
        EventData data = eventStat.get(event);
        data.setHighestIntegerStatistic(value);
    }

    public static void printEvent(Event event) {
        if (eventStat.containsKey(event)) {
            System.out.println(event + ": " + eventStat.get(event));
        } else {
            System.out.println("no statistics for event " + event);
        }
    }

    public static void writeEvent(StringBuilder sb, Event event) {
        if (eventStat.containsKey(event)) {
            sb.append(event).append(": ").append(eventStat.get(event)).append("\n");
        } else {
            sb.append("no statistics for event ").append(event).append("\n");
        }
    }

    public static void printAllEvents() {
        for (Event event : Event.values()) {
            System.out.println();
            printEvent(event);
        }
    }

    public static void writeAllEvents(StringBuilder sb) {
        for (Event event : Event.values()) {
            sb.append("\n");
            //   writer.write();
            writeEvent(sb, event);
        }
        sb.append("\n");
    }

    public static void writeAllNonVariableEventsExcel(StringBuilder sb) {
        if (eventStat.containsKey(Event.NROFTRIVIALSCCS)) {
            sb.append(" ").append(eventStat.get(Event.NROFTRIVIALSCCS).getIntegerStatistic());
        } else {
            sb.append(" 0");
        }

        if (eventStat.containsKey(Event.NROFNONTRIVIALSCCS)) {
            sb.append(" ").append(eventStat.get(Event.NROFNONTRIVIALSCCS).getIntegerStatistic());
        } else {
            sb.append(" 0");
        }

        if (!eventStat.get(Event.DECOMPOSE).didTimeOut()) {
            if (eventStat.containsKey(Event.SIZEOFLONGESTTREE)) {
                sb.append(" ").append(eventStat.get(Event.SIZEOFLONGESTTREE).getIntegerStatistic());
            } else {
                sb.append(" na");
            }
        } else {
            sb.append(" na");
        }

        if (!eventStat.get(Event.RUNZIELONKA_T).didTimeOut()) {
            sb.append(" ").append(eventStat.get(Event.TARJAN_T).getNrOfTimesCalled());
            sb.append(" ").append(eventStat.get(Event.NROFVERTICES_T).getIntegerStatisticlong());
        } else {
            sb.append(" na na");
        }

        if (!eventStat.get(Event.RUNZIELONKA_P).didTimeOut()) {
            sb.append(" ").append(eventStat.get(Event.NROFVERTICES_P).getIntegerStatistic());
        } else {
            sb.append(" na");
        }

        if (!eventStat.get(Event.RUNZIELONKA_D).didTimeOut()) {
            if (eventStat.containsKey(Event.UPDATENODE)) {
                sb.append(" ").append(eventStat.get(Event.UPDATENODE).getNrOfTimesCalled());
            } else {
                sb.append(" 0");
            }
        } else {
            sb.append(" na");
        }

    }

    public static void writeAverageVariableEventsExcel(StringBuilder sb) {
        if (!eventStat.get(Event.RUNZIELONKA_T).didTimeOut()) {
            long tarjanAverageTime = eventStat.get(Event.TARJAN_T).getAverageTime();
            sb.append(" ").append(smallTimeToString(tarjanAverageTime));
        } else {
            sb.append(" na");
        }

        if (!eventStat.get(Event.RUNZIELONKA_P).didTimeOut()) {
            long partialZielonkaAverageTime = eventStat.get(Event.PARTIALPREP).getAverageTime();
            sb.append(" ").append(smallTimeToString(partialZielonkaAverageTime));
        } else {
            sb.append(" na");
        }

        if (!eventStat.get(Event.RUNZIELONKA_P).didTimeOut()) {
            long partialAverageTime = eventStat.get(Event.TARJAN_P).getAverageTime();
            sb.append(" ").append(smallTimeToString(partialAverageTime));
        } else {
            sb.append(" na");
        }

        if (!eventStat.get(Event.DECOMPOSE).didTimeOut()) {
            long dynamicDecomposeAverageTime = eventStat.get(Event.DECOMPOSE).getAverageTime();
            sb.append(" ").append(smallTimeToString(dynamicDecomposeAverageTime));
        } else {
            sb.append(" 2h+");
        }

        if (!eventStat.get(Event.RUNZIELONKA_D).didTimeOut()) {
            long dynamicUpdateTreeAverageTime = 0;
            if (eventStat.containsKey(Event.UPDATEDYNAMICDECOMP)) {
                dynamicUpdateTreeAverageTime += eventStat.get(Event.UPDATEDYNAMICDECOMP).getAverageTime();
            }
            if (eventStat.containsKey(Event.COPYTREEWITHDELETEEDGE)) {
                dynamicUpdateTreeAverageTime += eventStat.get(Event.COPYTREEWITHDELETEEDGE).getAverageTime();
            }
            sb.append(" ").append(smallTimeToString(dynamicUpdateTreeAverageTime));
        } else {
            sb.append(" na");
        }

        if (!eventStat.get(Event.RUNZIELONKA_T).didTimeOut()) {
            long zielonkaTarjanAverageTime = eventStat.get(Event.RUNZIELONKA_T).getAverageTime();
            sb.append(" ").append(smallTimeToString(zielonkaTarjanAverageTime));
        } else {
            sb.append(" 2h+");
        }

        if (!eventStat.get(Event.RUNZIELONKA_P).didTimeOut()) {
            long zielonkaPartialAverageTime = eventStat.get(Event.RUNZIELONKA_P).getAverageTime();
            sb.append(" ").append(smallTimeToString(zielonkaPartialAverageTime));
        } else {
            sb.append(" 2h+");
        }

        if (!eventStat.get(Event.RUNZIELONKA_D).didTimeOut()) {
            long zielonkaDynamicAverageTime = eventStat.get(Event.RUNZIELONKA_D).getAverageTime();
            sb.append(" ").append(smallTimeToString(zielonkaDynamicAverageTime)).append("\n");
        } else {
            sb.append(" 2h+\n");
        }
    }

    private static String smallTimeToString(final long time) {
        int hours = (int) (time / nanosecondsInAnhour);
        int minutes = (int) ((time / nanosecondsInAminute) % 60);
        double seconds = ((double) time / nanosecondsInASecond) % 60;
//        double seconds = ((double) time / nanosecondsInASecond) ;
        if (hours > 0) {
            return hours + "h" + minutes + "m";
        } else if (minutes > 0) {
            if (minutes >= 10) {
                return minutes + "m";
            } else {
                DecimalFormat df = new DecimalFormat("#");
                return minutes + "m" + df.format(seconds) + "s";
            }
        } else {
            DecimalFormat df = new DecimalFormat("#.###");
            return df.format(seconds) + "s";
        }
    }
}
