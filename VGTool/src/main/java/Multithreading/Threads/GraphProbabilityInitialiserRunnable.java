package Multithreading.Threads;

import Entities.GraphHandling.ProbabilitiesTreesBuilder;
import Entities.GraphHandling.ProbabilityInitializer;
import Entities.Jena.Graph.VGPosition;
import Entities.Jena.Graph.VariantGraph;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Johan on 01/06/15.
 */
public class GraphProbabilityInitialiserRunnable implements Runnable {

    List<VGPosition> positions;
    CountDownLatch latch;

    public GraphProbabilityInitialiserRunnable(List<VGPosition> positions, CountDownLatch latch) {
        this.positions = positions;
        this.latch = latch;
    }


    public void run() {
        try {

//            System.out.println("Init probabilities to graph" );
            //TODO: Maybe move probabilities to another thread.
            ProbabilityInitializer probabilitiesBuilder = new ProbabilityInitializer();
            probabilitiesBuilder.addProbabilitiesToGraph(positions);


        } finally {
            latch.countDown();
        }
    }



}
