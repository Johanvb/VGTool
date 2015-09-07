package Entities.GraphHandling;

import Entities.Jena.Probabilities.VGCompressedProbabilityTree;
import Entities.Jena.Probabilities.VGProbabilityNode;
import Entities.Jena.Graph.VGSequence;
import Entities.Jena.Graph.VGPosition;
import Entities.Jena.Graph.VariantGraph;
import Entities.Jena.Other.VGIndividual;
import Entities.Jena.Probabilities.VGProbabilityTree;
import Utilities.GeneticsUtils;
import Utilities.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Johan on 27/04/15.
 */
public class ProbabilitiesTreesBuilder {

    VariantGraph variantGraph;
    List<VGPosition> positions;

    int logInterval;
    int nodeId;
    VGProbabilityTree tree;

    public void addProbabilitiesToGraph(List<VGPosition> positions, VariantGraph g) {

        this.positions = positions;
        variantGraph = g;

        int i = 0;
        long timeOne = System.currentTimeMillis();
        long timeTwo;

        logInterval = 1000;

        System.out.println("Number of positions " + positions.size());
        for (VGPosition position : positions) {

            i++;

            if (i % logInterval == 0) {
                timeTwo = System.currentTimeMillis();
                System.out.println(i + " Building probability tree at " + position.getPosition() + ". " + (timeTwo - timeOne) + " ms. Thead id: " + Thread.currentThread().getId());
                timeOne = timeTwo;
            }

            Set<VGSequence> sequences = position.sequencesSet;

            tree= position.getProbabilityTree();
            VGProbabilityNode root = tree.root;
            nodeId=0;

            for (VGSequence first : sequences) {
                for (VGSequence second : sequences) {
                    for (GeneticsUtils.PhasingType type : GeneticsUtils.PhasingType.values()) {


                        Set<VGIndividual> individuals = position.getIndividualsForGenotype(first, second, type);

                        if (individuals == null || individuals.size() <= Constants.individualsThreshold)
                            continue;

                        if (GeneticsUtils.PhasingType.UNPHASED == type && tree.hasCalculatedUnphasedRootWithSequences(first, second)) {
                            continue;
                        }

                        int index = positions.indexOf(position) + 1;

                        VGProbabilityNode child = new VGProbabilityNode(tree.getId()+""+nodeId);
                        nodeId++;
                        child.setGenotype(type, first, second);
                        root.addChild(child);

                        if (index < positions.size()) {
                            VGPosition successorPos = positions.get(index);
                            buildProbabilities(child, individuals, successorPos, 0);
                        }
                    }
                } }

            int totalIndividualsForProbability = tree.root.getNumberOfIndividuals();

            Set<VGProbabilityNode> validLeafs = tree.getValidLeafs();

            for (VGProbabilityNode leaf : validLeafs) {
                leaf.setProbability((1.0f * leaf.numberOfIndividuals) / totalIndividualsForProbability);
            }

            if (Constants.compressTrees) {
                position.setCompressedTree(new VGCompressedProbabilityTree(tree.getId()));
                VGCompressedProbabilityTree compressedTree = position.getCompressedTree();

                List<String> paths = new ArrayList<String>();
                List<Float> probabilities = new ArrayList<Float>();
                List<Short> individualsCount = new ArrayList<Short>();

                tree.getAllPathsForChildren(paths, probabilities, individualsCount);

                int index = 0;
                for (String path : paths) {
                    compressedTree.putProbabilityForPath(path, probabilities.get(index), individualsCount.get(0));
                    index++;
                }

                paths = null;
                probabilities = null;
                individualsCount = null;

                position.setProbabilityTree(null);  // force it to use compressed tree

            } else {
                tree.root.setProbabilityFromChildren();
            }
        }
    }


    private void buildProbabilities(VGProbabilityNode parent, Set<VGIndividual> remainingPotentialIndividuals, VGPosition position, int depth) {
        parent.setNumberOfIndividuals((short) remainingPotentialIndividuals.size());

        if (Constants.maxDepthForProbabilityTrees != -1 && depth > Constants.maxDepthForProbabilityTrees) {
            return;
        }

        List<Object[]> validProceedGenotypes = new ArrayList<Object[]>();

        for (VGSequence first : position.sequencesSet) {
            for (VGSequence second : position.sequencesSet) {
                for (GeneticsUtils.PhasingType type : GeneticsUtils.PhasingType.values()) {

                    Set<VGIndividual> individualsForGenotypeAtPos = position.getIndividualsForGenotype(first, second, type);


                    if (individualsForGenotypeAtPos != null && !individualsForGenotypeAtPos.isEmpty()) {

                        Set<VGIndividual> intersection = new HashSet<VGIndividual>(remainingPotentialIndividuals);

                        intersection.retainAll(individualsForGenotypeAtPos);

                        if (intersection.size() > Constants.individualsThreshold) {
                            validProceedGenotypes.add(new Object[]{first, second, type, intersection});
                        }
                    }
                }
            }
        }

        for (Object[] objects : validProceedGenotypes) {
            VGSequence first = (VGSequence) objects[0];
            VGSequence second = (VGSequence) objects[1];
            GeneticsUtils.PhasingType type = (GeneticsUtils.PhasingType) objects[2];
            Set<VGIndividual> intersection = (Set<VGIndividual>) objects[3];

            int successorIndex = variantGraph.getPositions().indexOf(position) + 1;

            if (successorIndex < variantGraph.getPositions().size()) {

                if (!(GeneticsUtils.PhasingType.UNPHASED == type && parent.hasCalculatedProbabilitiesForUnphasedChildren(first, second, type))) {
                    VGPosition successorPos = variantGraph.getPositions().get(successorIndex);
                    VGProbabilityNode child = new VGProbabilityNode(tree.getId()+""+nodeId);
                    nodeId++;

                    child.setGenotype(type, first, second);
                    parent.addChild(child);

                    buildProbabilities(child, intersection, successorPos, depth + 1);
                }
            }
        }
    }
}