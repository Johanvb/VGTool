package Multithreading.Threads;

import Entities.Jena.Graph.VariantGraph;
import Entities.GraphHandling.GraphExpander;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * Created by Johan on 10/05/15.
 */
public class GraphExpanderRunnable implements Runnable {

    VariantGraph graph;
    CountDownLatch latch;
    Semaphore sem;


    public GraphExpanderRunnable(VariantGraph graph, CountDownLatch latch, Semaphore sem) {
        this.graph = graph;
        this.latch = latch;
        this.sem = sem;
    }

    public void run() {
        try {
            sem.acquire();
            GraphExpander expander = new GraphExpander();
            expander.expandGraph(graph);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            sem.release();
            latch.countDown();
        }
    }
}
