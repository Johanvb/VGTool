package Multithreading;

import Entities.Jena.Graph.VGPosition;
import Entities.Jena.Graph.VariantGraph;
import Entities.Sampling.SampleUtilities;
import Entities.Sampling.SampledGraph;
import Entities.Sampling.SampledIndividual;
import IO.SamplesWriter;
import Multithreading.Threads.SamplerRunnable;
import Multithreading.Threads.*;
import Utilities.EnclosingModel;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Johan on 07/05/15.
 */
public class ThreadHandler {


    public static void startGraphShrinkerThreadsWithGraphs(Set<VariantGraph> graphSet) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(graphSet.size());

        long t1 = System.currentTimeMillis();


        for (VariantGraph g : graphSet) {
            System.out.println("Shrinking graph " + g.getId());
            GraphShrinkerThread runnable = new GraphShrinkerThread(g, latch);
            Thread thread = new Thread(runnable);
            thread.start();
        }


        latch.await();

        long t2 = System.currentTimeMillis();

        System.out.println("Shrunk in " + (t2 - t1) + " ms.");

//        List<VariantGraph> list = new ArrayList<VariantGraph>();
//        for(VariantGraph g : graphSet){
//            list.add(g);
//        }
//
//        startGraphExpanderThreads(list);
    }

    public static void startGraphWritersThreadWithGraphs(Set<VariantGraph> graphSet, String filename) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(graphSet.size());

        // FIXME: Ensure that graphSet.size == 1 or use alternative scheme for filenames
        for (VariantGraph g : graphSet) {
            GraphWriterRunnable runnable = new GraphWriterRunnable(g, latch, filename);
            Thread thread = new Thread(runnable);
            thread.start();
        }

        latch.await();
    }

    public static void startGraphProbabilityThreadsForGraphs(Set<VariantGraph> graphSet) throws InterruptedException {

        int splitBy = Runtime.getRuntime().availableProcessors();

        System.out.println("Available processors " + splitBy);
        long t1 = System.currentTimeMillis();

        for (VariantGraph g : graphSet) {

            List<List<VGPosition>> choppedPositions = Lists.partition(g.getPositions(), g.getPositions().size() / splitBy);

            CountDownLatch latch = new CountDownLatch(choppedPositions.size());

            for (List<VGPosition> positions : choppedPositions) {

                GraphProbabilityInitialiserRunnable runnable = new GraphProbabilityInitialiserRunnable(positions, latch);
                Thread thread = new Thread(runnable);
                thread.start();

            }

            latch.await();
            latch = new CountDownLatch(choppedPositions.size());


            for (List<VGPosition> positions : choppedPositions) {
                GraphProbabilityTreesRunnable runnable = new GraphProbabilityTreesRunnable(positions, g, latch);
                Thread thread = new Thread(runnable);
                thread.start();
            }
            latch.await();

            g.hasProbabilities = true;
        }

        long t2 = System.currentTimeMillis();

        System.out.println("Probability trees in " + (t2 - t1) + " ms.");
    }


    public static void startGraphMergerThreadsWithGraphs(Set<List<VariantGraph>> setsForMerging) throws InterruptedException {

        //Merge and then re-shrink.
        if (!setsForMerging.isEmpty()) {
            CountDownLatch latch = new CountDownLatch(setsForMerging.size());


            long t1 = System.currentTimeMillis();

            for (List<VariantGraph> graphsForChromosome : setsForMerging) {
                System.out.println("Merging " + graphsForChromosome.size() + " graph for chromosome " + graphsForChromosome.get(0).getId());

                GraphMergerRunnable mergerThread = new GraphMergerRunnable(graphsForChromosome, latch);
                Thread thread = new Thread(mergerThread);
                thread.start();
            }

            latch.await();


            long t2 = System.currentTimeMillis();

            System.out.println("Merged in " + (t2 - t1) + " ms.");

            //Re-shrink graphs
            startGraphShrinkerThreadsWithGraphs(EnclosingModel.getGraphsToShrink());
        }
    }

    public static void startGraphExpanderThreads(List<VariantGraph> graphs) throws InterruptedException {

        long t1 = System.currentTimeMillis();

        CountDownLatch latch = new CountDownLatch(graphs.size());

        for (VariantGraph graph : graphs) {
            GraphExpanderRunnable expanderThread = new GraphExpanderRunnable(graph, latch);
            Thread thread = new Thread(expanderThread);
            thread.start();
        }

        latch.await();


        long t2 = System.currentTimeMillis();

        System.out.println("Expanded in " + (t2 - t1) + " ms.");
    }

    public static SampledGraph startSamplingThreads(VariantGraph graph, int numberOfSamples) throws InterruptedException, IOException {

        List<SampledIndividual> individuals = SampleUtilities.getSampledIndividuals(numberOfSamples);

        SampledGraph sampledGraph = new SampledGraph(graph, individuals);

        Scanner in = new Scanner(System.in);


        long t1 = System.currentTimeMillis();
//        System.out.println("Splut by?");
//        int splitBy = in.nextInt();

        CountDownLatch latch = new CountDownLatch(numberOfSamples);

//        int i = 0;

        for (SampledIndividual individual : individuals) {

            SamplerRunnable samplerRunnable = new SamplerRunnable(graph, sampledGraph, individual, latch);
            Thread thread = new Thread(samplerRunnable);
            thread.start();
//
//            i++;
//            if (i == numberOfSamples / splitBy) {
//                System.out.println("Starting with i " + i);
//                latch = new CountDownLatch(numberOfSamples / splitBy);
//                i = 0;
//            }

        }
        latch.await();


        long t2 = System.currentTimeMillis();

        System.out.println("Time to create samples " + (t2 - t1) + " ms.");

        return sampledGraph;
    }

    public static List<SampledGraph> startSamplingThreadsForAllGraphs(int numberOfSamples) throws InterruptedException, IOException {

        List<SampledGraph> sampledGraphs = new ArrayList<SampledGraph>();

        CountDownLatch latch = new CountDownLatch(numberOfSamples * EnclosingModel.chromosomeGraphs.keySet().size());
        List<SampledIndividual> individuals = SampleUtilities.getSampledIndividuals(numberOfSamples);

        long t1 = System.currentTimeMillis();

        for (String graphKey : EnclosingModel.chromosomeGraphs.keySet()) {

            //We know there is only one graph per chromosome.
            VariantGraph graph = EnclosingModel.chromosomeGraphs.get(graphKey).iterator().next();
            SampledGraph sampledGraph = new SampledGraph(graph, individuals);
            sampledGraphs.add(sampledGraph);

            for (SampledIndividual individual : individuals) {
                SamplerRunnable samplerRunnable = new SamplerRunnable(graph, sampledGraph, individual, latch);
                Thread thread = new Thread(samplerRunnable);
                thread.start();
            }

        }

        latch.await();

        long t2 = System.currentTimeMillis();
        System.out.println("Time to create samples " + (t2 - t1) + " ms.");
        return sampledGraphs;
    }
}
