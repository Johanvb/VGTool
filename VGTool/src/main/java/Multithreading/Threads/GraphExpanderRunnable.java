package Multithreading.Threads;

import Entities.Jena.Graph.VariantGraph;
import Entities.GraphHandling.GraphExpander;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Johan on 10/05/15.
 */
public class GraphExpanderRunnable implements Runnable {

    VariantGraph graph;
    CountDownLatch latch;


    public GraphExpanderRunnable(VariantGraph graph, CountDownLatch latch) {
        this.graph = graph;
        this.latch = latch;
    }

    public void run() {
        try {

            GraphExpander expander = new GraphExpander();
            expander.expandGraph(graph);

        } finally {
            latch.countDown();
        }
    }
}
