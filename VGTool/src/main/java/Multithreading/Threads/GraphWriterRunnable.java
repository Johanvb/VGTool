package Multithreading.Threads;

import Entities.Jena.Graph.VariantGraph;
import IO.OutputHandler;
import Utilities.Constants;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Johan on 07/05/15.
 */
public class GraphWriterRunnable implements Runnable {

    VariantGraph graph;
    CountDownLatch latch;
    String filename;

    public GraphWriterRunnable(VariantGraph graph, CountDownLatch latch, String filename) {
        this.graph = graph;
        this.latch = latch;
        this.filename = filename;
    }

    public void run() {
        OutputHandler outputHandler = new OutputHandler();

        if(Constants.shouldUseDatabase){
            try {
                outputHandler.writeToDataBase(graph);
            } finally {
                latch.countDown();
            }
        }else{
            try {
                outputHandler.writeToFile(graph, filename);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }
    }
}
