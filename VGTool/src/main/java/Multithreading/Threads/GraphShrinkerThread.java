package Multithreading.Threads;

import Entities.Jena.Graph.VariantGraph;
import Entities.GraphHandling.GraphShrinker;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * Created by Johan on 07/05/15.
 */
public class GraphShrinkerThread implements Runnable {

    private VariantGraph graph;

    private CountDownLatch latch;
    private Semaphore sem;

    public GraphShrinkerThread(VariantGraph graph, CountDownLatch latch, Semaphore sem) {
        this.graph = graph;
        this.latch = latch;
        this.sem = sem;
    }

    public void run() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        GraphShrinker graphHandler = new GraphShrinker(graph);

//        graphHandler.buildGaps();
        graphHandler.collapseSequences();

        graph.hasBeenShrinked = true;

        sem.release();
        latch.countDown();

    }
}
