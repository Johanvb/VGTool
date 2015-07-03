package Multithreading.Threads;

import Entities.Jena.Graph.VariantGraph;
import Entities.GraphHandling.GraphShrinker;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Johan on 07/05/15.
 */
public class GraphShrinkerThread implements Runnable {

    private VariantGraph graph;

    private CountDownLatch latch;

    public GraphShrinkerThread(VariantGraph graph, CountDownLatch latch) {
        this.graph = graph;
        this.latch = latch;
    }

    public void run() {
        GraphShrinker graphHandler = new GraphShrinker(graph);

//        graphHandler.buildGaps();
        graphHandler.collapseSequences();

        graph.hasBeenShrinked = true;

        latch.countDown();
    }
}
