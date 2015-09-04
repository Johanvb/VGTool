package Multithreading.Threads;

import Entities.Jena.Graph.VGPosition;
import Entities.Jena.Graph.VariantGraph;
import Entities.Jena.Probabilities.VGProbabilityNode;
import Entities.Jena.Probabilities.VGProbabilityTree;
import Entities.Sampling.SampledGraph;
import Entities.Sampling.SampledIndividual;
import Utilities.Constants;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Created by Johan on 14/05/15.
 */
public class SamplerRunnable implements Runnable {

    VariantGraph graph;
    SampledGraph sampledGraph;
    SampledIndividual individual;
    CountDownLatch latch;
    Semaphore sem;

    ArrayDeque<VGProbabilityTree> treesQueue;

    List<VGProbabilityNode> currentPath;

    public SamplerRunnable(VariantGraph graph, SampledGraph sampledGraph, SampledIndividual individual, CountDownLatch latch, Semaphore sem) {
        this.graph = graph;
        this.sampledGraph = sampledGraph;
        this.individual = individual;
        this.latch = latch;
        this.sem = sem;

        treesQueue = new ArrayDeque<VGProbabilityTree>(Constants.queueSize);

        currentPath = new ArrayList<VGProbabilityNode>();
    }

    public void run() {
        try {
            sem.acquire();

            int index = 0;
            VGPosition currentPosition;
            VGProbabilityNode currentProbabilityNode = null;
            Random rand = new Random();
            ArrayList<VGProbabilityNode> selectionPath = null;
            ArrayList<VGProbabilityTree> selectionTreeQueue = null;

            while (index < graph.positions.size()) {

                while (currentPath.size() > Constants.queueSize)
                    currentPath.remove(0);

                // FIXME: one shorter than currentPath (usually)
                while (treesQueue.size() > Constants.queueSize)
                    treesQueue.removeFirst();

                currentPosition = graph.positions.get(index);

                if (currentProbabilityNode != null) {

                    selectionPath = new ArrayList<VGProbabilityNode>(currentPath);
                    selectionTreeQueue = new ArrayList<VGProbabilityTree>(treesQueue);

                    // Make choice: probabilisticly shorten selection path according to geometric probability distribution
                    while(selectionTreeQueue.size() > 1) {
                        // handle if trees in front of queue are dead!! (i.e., if the queue is getting too long)
                        if (selectionTreeQueue.get(0).getChildAtEndOfPath(selectionPath)== null) {
                            selectionPath.remove(0);
                            selectionTreeQueue.remove(0);
                            continue;
                        }
                        if (rand.nextFloat() < Constants.pickHeadProbability)
                            break;
                        else {
                            selectionPath.remove(0);
                            selectionTreeQueue.remove(0);
                        }
                    }
                    if (selectionTreeQueue.size() >= 1)
                        currentProbabilityNode = selectionTreeQueue.remove(0).getChildAtEndOfPath(selectionPath);
                }

                if (currentProbabilityNode == null || !currentProbabilityNode.hasChildren()) {
                    currentProbabilityNode = currentPosition.getPlausibleChoice();
                    if (currentProbabilityNode == null) {
                        //Skip gaps
                        index++;
                        continue;
                    }

                } else {
                    currentProbabilityNode = currentProbabilityNode.getPlausibleChild();
                }
                treesQueue.addLast(currentPosition.getProbabilityTree());

                currentPath.add(currentProbabilityNode);

                sampledGraph.addSampleForKeyAndGenotype(sampledGraph.genotypeIndividualsFirst, currentPosition.getPosition(),
                        currentProbabilityNode.getGenotype().getFirst(), individual, currentProbabilityNode.getGenotype().phased);

                sampledGraph.addSampleForKeyAndGenotype(sampledGraph.genotypeIndividualsSecond, currentPosition.getPosition(),
                        currentProbabilityNode.getGenotype().getSecond(), individual, currentProbabilityNode.getGenotype().phased);

                index++;
            }

        } catch (InterruptedException e) {
            System.out.println(e.toString());
        } finally {
            if (sem != null)
                sem.release();
            latch.countDown();
        }
    }

    private boolean choiceIsValid(VGProbabilityNode currentProbabilityNode, VGPosition currentPosition) {

        for (VGProbabilityNode nodeAtPosition : currentPosition.getProbabilityTree().root.getChildren()) {

            boolean foundIt = false;

            for (VGProbabilityNode node : currentProbabilityNode.getChildren()) {
                if (node.asString().equals(nodeAtPosition.asString())) {
                    foundIt = true;
                }
//                System.out.println("Node " + node.genotype.toString());
            }

            if (!foundIt) {
//                System.out.println("Couldnt find " + nodeAtPosition.genotype.toString());
                return false;
            }
        }
        return true;
    }
}
