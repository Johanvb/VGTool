package Multithreading.Threads;

import Entities.Jena.Graph.VGPosition;
import Entities.Jena.Graph.VariantGraph;
import Entities.Jena.Probabilities.VGProbabilityNode;
import Entities.Jena.Probabilities.VGProbabilityTree;
import Entities.Sampling.SampledGraph;
import Entities.Sampling.SampledIndividual;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Johan on 14/05/15.
 */
public class SamplerRunnable implements Runnable {

    VariantGraph graph;
    SampledGraph sampledGraph;
    SampledIndividual individual;
    CountDownLatch latch;

    ArrayDeque<VGProbabilityTree> treesQueue;

    List<VGProbabilityNode> currentPath;

    public SamplerRunnable(VariantGraph graph, SampledGraph sampledGraph, SampledIndividual individual, CountDownLatch latch) {
        this.graph = graph;
        this.sampledGraph = sampledGraph;
        this.individual = individual;
        this.latch = latch;

        treesQueue = new ArrayDeque<VGProbabilityTree>();

        currentPath = new ArrayList<VGProbabilityNode>();
    }

    public void run() {
        try {
            int index = 0;
            VGPosition currentPosition;
            VGProbabilityNode currentProbabilityNode = null;

            while (index < graph.positions.size()) {

                currentPosition = graph.positions.get(index);

                if (currentProbabilityNode != null) {

                    VGProbabilityNode tempNode = currentProbabilityNode;

                    while (tempNode == null || !choiceIsValid(tempNode, currentPosition)) {
                        if (treesQueue.isEmpty()) {
                            tempNode = null;
                            currentPath = new ArrayList<VGProbabilityNode>();
                            break;
                        }

                        currentPath.remove(0);

                        tempNode = treesQueue.pollFirst().getChildAtEndOfPath(currentPath);
                    }

                    currentProbabilityNode = tempNode;

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
                    treesQueue.addLast(currentPosition.getProbabilityTree());
                }


                currentPath.add(currentProbabilityNode);

                sampledGraph.addSampleForKeyAndGenotype(sampledGraph.genotypeIndividualsFirst, currentPosition.getPosition(),
                        currentProbabilityNode.getGenotype().getFirst(), individual, currentProbabilityNode.getGenotype().phased);

                sampledGraph.addSampleForKeyAndGenotype(sampledGraph.genotypeIndividualsSecond, currentPosition.getPosition(),
                        currentProbabilityNode.getGenotype().getSecond(), individual, currentProbabilityNode.getGenotype().phased);

                index++;
            }

        } finally {
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
