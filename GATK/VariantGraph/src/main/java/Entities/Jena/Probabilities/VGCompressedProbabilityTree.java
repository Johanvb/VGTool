package Entities.Jena.Probabilities;

import Utilities.GeneticsUtils;

import java.util.*;

/**
 * Created by Johan on 05/06/15.
 */
public class VGCompressedProbabilityTree {

    Map<String, CompressedNode> pathProbabilitiesTree;

    private static int compressFactor = 32;
    private String id;

    public VGCompressedProbabilityTree(String id) {
        this.id = id;
        pathProbabilitiesTree = new HashMap<String, CompressedNode>();
    }

    public void putProbabilityForPath(String inputPath, Float prob, Short individuals) {

        String[] firstSplit = inputPath.split("#");

        ArrayList<String> splitStrings = new ArrayList<String>();

        int j = 0;
        String tempString = "";
        for (String splitString : firstSplit) {
            tempString += splitString + ":";
            j++;

            if (j > compressFactor) {
                j = 0;
                splitStrings.add(tempString.substring(0, tempString.length() - 1));
                tempString = "";
            }
        }

        if (j != 0) {
            splitStrings.add(tempString.substring(0, tempString.length() - 1));
        }

        CompressedNode currentNode;

        if (!pathProbabilitiesTree.containsKey(splitStrings.get(0))) {
            pathProbabilitiesTree.put(splitStrings.get(0), new CompressedNode());
        }

        currentNode = pathProbabilitiesTree.get(splitStrings.get(0));


        int i = 0;
        for (String splitString : splitStrings) {
            if (i != 0) {

                if (!currentNode.children.containsKey(GeneticsUtils.getPathFromString(splitString))) {
                    CompressedNode newNode = new CompressedNode();
                    currentNode.children.put(GeneticsUtils.getPathFromString(splitString), newNode);
                }

                currentNode = currentNode.children.get(GeneticsUtils.getPathFromString(splitString));
            }

            i++;

            if (i == splitStrings.size()) {
                currentNode.probability = prob.floatValue();
                currentNode.numberofIndividuals = individuals.shortValue();
            }
        }

        prob = null;
        individuals = null;
        firstSplit = null;
        splitStrings = null;

    }

    private class CompressedNode {

        Map<String, CompressedNode> children;
        float probability;
        short numberofIndividuals;


        public CompressedNode() {
            children = new HashMap<String, CompressedNode>();
        }

        public float getProbability() {
            return probability;
        }

        public void setProbability(float probability) {
            this.probability = probability;
        }

    }

    VGProbabilityTree unCompressedTree;

    public VGProbabilityTree unCompress() {
        unCompressedTree = new VGProbabilityTree(id);

        unCompressedTree.root = new VGProbabilityNode(id+":"+unCompressedTree.getValidNodeId());

        for (String key : pathProbabilitiesTree.keySet()) {
            String[] compressedGenotypes = key.split(":");

            VGProbabilityNode leaf = addNewGenotypesFromNode(unCompressedTree.root, compressedGenotypes);

            CompressedNode child = pathProbabilitiesTree.get(key);

            unCompress(leaf, child);

        }

        unCompressedTree.setProbabilityFromChildren();

        return unCompressedTree;
    }

    private void unCompress(VGProbabilityNode start, CompressedNode compressed) {

        if (compressed.children != null && compressed.children.size() != 0) {
            for (String key : compressed.children.keySet()) {

                String[] compressedGenotypes = key.split(":");


                VGProbabilityNode leaf = addNewGenotypesFromNode(start, compressedGenotypes);
                CompressedNode child = compressed.children.get(key);
                unCompress(leaf, child);

            }
        } else {
            start.probability = compressed.probability;
            start.setNumberOfIndividuals(compressed.numberofIndividuals);
            start.hasSetProbability = true;
        }

    }

    private VGProbabilityNode addNewGenotypesFromNode(VGProbabilityNode root, String[] compressedGenotypes) {

        VGProbabilityGenotype uncompressed = GeneticsUtils.getGenotypeFromString(GeneticsUtils.getGenotypeFromId(compressedGenotypes[0]));
        VGProbabilityNode newNode = root.getOrCreateGenotypeWithValue(uncompressed, unCompressedTree.getValidNodeId());

        root.addChild(newNode);

        if (compressedGenotypes.length == 1) {
            return newNode;
        } else {
            return addNewGenotypesFromNode(newNode, Arrays.copyOfRange(compressedGenotypes, 1, compressedGenotypes.length));
        }

    }
}