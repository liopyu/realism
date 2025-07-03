package net.liopyu.realism.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskScheduler {
    private static final Queue<Runnable> NEXT_TICK = new ConcurrentLinkedQueue<>();

    public static void scheduleNextTick(Runnable runnable) {
        NEXT_TICK.add(runnable);
    }

    public static void scheduleTick(Runnable logic) {
        TaskScheduler.scheduleNextTick(logic);
    }

    public static void runScheduled() {
        Runnable task;
        while ((task = NEXT_TICK.poll()) != null) {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
