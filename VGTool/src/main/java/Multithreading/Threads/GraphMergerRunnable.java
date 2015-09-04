package Multithreading.Threads;

import Entities.Jena.Graph.VariantGraph;
import Multithreading.ThreadHandler;
import Entities.GraphHandling.GraphMerger;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * Created by Johan on 08/05/15.
 */
public class GraphMergerRunnable implements Runnable {


    private final List<VariantGraph> graphs;
    private final CountDownLatch latch;
    private final Semaphore sem;

    public GraphMergerRunnable(List<VariantGraph> graphs, CountDownLatch latch, Semaphore sem) {
        this.graphs = graphs;
        this.latch = latch;
        this.sem = sem;

    }

    public void run() {
        try {
            sem.acquire();
            GraphMerger graphMerger = new GraphMerger();
            ThreadHandler.startGraphExpanderThreads(graphs);


            //TODO: Consider doing a pairwise merge here. Only necessary if more than two graphs are put in.
            int i = 0;
            for (Iterator<VariantGraph> iter = graphs.iterator(); iter.hasNext(); ) {
                VariantGraph graphToMerge = iter.next();

                if (i != 0) {
                    graphMerger.mergeGraphs(graphs.get(0), graphToMerge);
                    iter.remove();
                }

                i++;
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            sem.release();
            latch.countDown();
        }
    }

}
