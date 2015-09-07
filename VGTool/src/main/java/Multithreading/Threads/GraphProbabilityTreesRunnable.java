package Multithreading.Threads;

import Entities.Jena.Graph.VGPosition;
import Entities.Jena.Graph.VariantGraph;
import Entities.GraphHandling.ProbabilitiesTreesBuilder;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Johan on 08/05/15.
 */
public class GraphProbabilityTreesRunnable implements Runnable {

    VariantGraph graph;
    List<VGPosition> positions;
    CountDownLatch latch;

    public GraphProbabilityTreesRunnable(List<VGPosition> p, VariantGraph g, CountDownLatch latch) {
        this.positions = p;
        this.graph = g;
        this.latch = latch;
    }

    public void run() {

        try {

            System.out.println("Adding probabilities to graph ");
            //TODO: Maybe move probabilities to another thread.
            ProbabilitiesTreesBuilder probabilitiesBuilder = new ProbabilitiesTreesBuilder();
            probabilitiesBuilder.addProbabilitiesToGraph(positions,graph);


        } finally {
            System.out.println("GraphProbabilityTreesRunnable: Done for thread ");
            latch.countDown();
        }
    }
}
