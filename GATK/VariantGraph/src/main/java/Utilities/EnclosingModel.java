package Utilities;


import Entities.Jena.Graph.VariantGraph;
import Entities.Jena.Other.VGIndividual;

import java.util.*;

/**
 * Created by Johan on 21/04/15.
 */
public class EnclosingModel {

    private static Map<String, VGIndividual> peopleMap = new HashMap<String, VGIndividual>();
    private static List<VGIndividual> sortedIndividuals = new ArrayList<VGIndividual>();
    public static Map<String, List<VariantGraph>> chromosomeGraphs = new HashMap<String, List<VariantGraph>>();

    public static Map<String, VGIndividual> getPeopleMap() {
        return peopleMap;
    }

    public static void addIndividualWithId(VGIndividual individual) {
        peopleMap.put(individual.getId(), individual);
        sortedIndividuals.add(individual);
        Collections.sort(sortedIndividuals);
    }




    public static void putNewGraphForKey(String key, VariantGraph graph) {
        if (!chromosomeGraphs.containsKey(key)) {
            chromosomeGraphs.put(key, new ArrayList<VariantGraph>());
        }
        chromosomeGraphs.get(key).add(graph);
    }

    public static Set<VariantGraph> getGraphsForWrite() {
        Set<VariantGraph> graphSet = new HashSet<VariantGraph>();

        for (String key : EnclosingModel.chromosomeGraphs.keySet()) {
            List<VariantGraph> modelGraphs = EnclosingModel.chromosomeGraphs.get(key);
            for (VariantGraph g : modelGraphs) {
                graphSet.add(g);
            }
        }
        return graphSet;
    }

    public static Set<VariantGraph> getGraphsToShrink() {
        Set<VariantGraph> graphSet = new HashSet<VariantGraph>();

        for (String key : EnclosingModel.chromosomeGraphs.keySet()) {
            List<VariantGraph> modelSet = EnclosingModel.chromosomeGraphs.get(key);
            for (VariantGraph g : modelSet) {
                if (!g.hasBeenShrinked) {
                    graphSet.add(g);
                }
            }
        }

        return graphSet;
    }

    public static Set<List<VariantGraph>> getGraphSetsToMerge() {
        Set<List<VariantGraph>> setsForMerging = new HashSet<List<VariantGraph>>();

        for (String key : EnclosingModel.chromosomeGraphs.keySet()) {
            List<VariantGraph> graphSet = EnclosingModel.chromosomeGraphs.get(key);

            if (graphSet.size() > 1) {
                setsForMerging.add(graphSet);
            }
        }

        return setsForMerging;

    }

    public static Set<VariantGraph> getGraphsForProbabilities() {
        Set<VariantGraph> graphSet = new HashSet<VariantGraph>();

        for (String key : EnclosingModel.chromosomeGraphs.keySet()) {
            List<VariantGraph> modelGraphs = EnclosingModel.chromosomeGraphs.get(key);
            for (VariantGraph g : modelGraphs) {
                graphSet.add(g);
            }
        }
        return graphSet;
    }


    public static List<String> getSortedCompressedIndividualsStringsFromVGIndividuals(Set<VGIndividual> inputIndividuals) {
        List<String> condensedList = new ArrayList<String>();

        if (inputIndividuals.isEmpty()) {
            return condensedList;
        }

        List<VGIndividual> sortedInputIndividualNames = new ArrayList<VGIndividual>(inputIndividuals);
        Collections.sort(sortedInputIndividualNames);

        int i = 1;
        int currentAccumulatedLength = 1;
        String currentName = sortedInputIndividualNames.get(0).getId();
        String currentStartName = currentName;

        for (VGIndividual individual : sortedIndividuals) {
            String name = individual.getId();

            if (i >= sortedInputIndividualNames.size()) {
                condensedList.add(currentStartName);
                condensedList.add("" + currentAccumulatedLength);
                break;
            }

            if (currentName.equals(name)) {
                currentAccumulatedLength++;
                currentName = sortedInputIndividualNames.get(i).getId();
                i++;
            } else if (currentAccumulatedLength > 1) {
                condensedList.add(currentStartName);
                condensedList.add("" + currentAccumulatedLength);

                currentAccumulatedLength = 1;
                currentName = sortedInputIndividualNames.get(i).getId();
                currentStartName = currentName;
                i++;
            }
        }

        return condensedList;
    }

    public static void addIndividualsToSet(Set<VGIndividual> individualSet, String startIndividual, String lengthString) {

        int length = Integer.parseInt(lengthString);
        int startIndexOfIndividual = sortedIndividuals.indexOf(peopleMap.get(startIndividual));
        individualSet.addAll(sortedIndividuals.subList(startIndexOfIndividual, startIndexOfIndividual + length));

    }

    public static VGIndividual getIndividualForId(String id) {
        if (peopleMap.containsKey(id)) {
            return peopleMap.get(id);
        }
        return null;
    }

    public static String getUnusedIndividualName() {

        int nameInt = peopleMap.keySet().size();

        while (peopleMap.containsKey("" + nameInt)) {
            nameInt++;
        }

        return "" + nameInt;
    }
}
