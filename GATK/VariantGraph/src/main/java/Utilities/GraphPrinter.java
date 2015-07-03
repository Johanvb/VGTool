package Utilities;

import Entities.Jena.Graph.*;
import Entities.Jena.Other.VGIndividual;

import java.util.List;
import java.util.Set;

/**
 * Created by Johan on 22/04/15.
 */
public class GraphPrinter {

    private static VariantGraph variantGraph;

    public static void printAllGraphs() {
        for (String key : EnclosingModel.chromosomeGraphs.keySet()) {
            for (VariantGraph vg : EnclosingModel.chromosomeGraphs.get(key)) {
                printGraph(vg);
            }
        }
    }

    public static void printAllGraphSizes() {

        for (List<VariantGraph> graphs : EnclosingModel.chromosomeGraphs.values()) {

            for (VariantGraph graph : graphs) {
                System.out.println("Graph " + graph.getId() + " " + graph.hashCode() + " size  " + VariantGraphUtilities.totalNodesInGraph(graph));
            }

        }
    }

    public enum PrintType {
        phasingForIndividuals, phasingWithNumbers, sequences, sequencesAndSuccessors
    }

    private static PrintType printType;


    public static void printGraph(VariantGraph vg) {

        variantGraph = vg;

        printType = PrintType.phasingWithNumbers;
        boolean withProbabilities = true;

        switch (printType) {
            case phasingForIndividuals:
                for (VGPosition vgPosition : variantGraph.positions) {
                    Set<VGSequence> sequences = vgPosition.sequencesSet;
                    printPhasingForSequence(sequences);
                }

                break;
            case phasingWithNumbers:
                for (VGPosition vgPosition : variantGraph.positions) {
                    Set<VGSequence> sequences = vgPosition.sequencesSet;
                    printPhasingWithNumbers(vgPosition.getPosition(), sequences);
                }


                break;
            case sequences:
                for (VGPosition vgPosition : variantGraph.positions) {
                    Set<VGSequence> sequences = vgPosition.sequencesSet;
                    printSequences(vgPosition.getPosition(), sequences);
                }

                break;
            case sequencesAndSuccessors:
                for (VGPosition vgPosition : variantGraph.positions) {
                    Set<VGSequence> sequences = vgPosition.sequencesSet;
                    printSequenceAndSuccessors(vgPosition.getPosition(), sequences, withProbabilities);
                }

                break;
        }
    }

    private static void printPhasingForSequence(Set<VGSequence> sequences) {

        boolean didPrintLine = false;
        for (String key : EnclosingModel.getPeopleMap().keySet()) {
            VGIndividual individual = EnclosingModel.getPeopleMap().get(key);
            boolean foundFirst = false;
            boolean shouldPrintLine = false;
            boolean noVariationLines = false;
            String stringToPrint = individual.getId() + ": ";

//            for (VGSequence sequence : sequences) {
//                if (sequence instanceof VGSequenceGap) {
//                    break;
//                }
////                sequences.heteroUnphasedSecond.contains(individual) ||
//                if (sequence.heteroUnphased.contains(individual)) {
//                    shouldPrintLine = true;
//
//                    if (!foundFirst) stringToPrint += sequence.getId() + " [";
//                    stringToPrint += sequence.toString();
//                    if (!foundFirst) {
//                        foundFirst = true;
//                        stringToPrint += " / ";
//                    } else {
//                        stringToPrint += "]";
//                    }
//                }
//                if (sequence.phasedFirst.contains(individual) || sequence.phasedSecond.contains(individual)) {
//                    shouldPrintLine = true;
//
//                    if (!foundFirst) stringToPrint += sequence.getId() + " [";
//                    stringToPrint += sequence.toString();
//                    if (!foundFirst) {
//                        foundFirst = true;
//                        stringToPrint += " | ";
//                    } else {
//                        stringToPrint += "]";
//                    }
//                }
//                if (sequence.homozygousIndividuals.contains(individual)) {
//                    if (noVariationLines)
//                        shouldPrintLine = true;
//                    stringToPrint += sequence.getId() + " *[" + sequence.toString() + " / " + sequence.toString() + "]*";
//                }
//            }
            if (shouldPrintLine && stringToPrint.length() > 0) {
                didPrintLine = true;
                System.out.println(stringToPrint);
            }
        }
        if (didPrintLine)
            System.out.println();

    }

    private static void printSequences(Integer index, Set<VGSequence> sequences) {

        System.out.print(index + " Size:" + sequences.size() + " ");
        for (VGSequence alleleSequence : sequences) {
            System.out.print(alleleSequence.toString() + " ");
        }
        System.out.println();
    }


    private static void printSequenceAndSuccessors(Integer index, Set<VGSequence> sequences, boolean withProbabilities) {


        int i = 0;
        System.out.print(index + " ");
        for (VGSequence alleleSequence : sequences) {
            System.out.print(alleleSequence.sequenceAndSuccessors(withProbabilities));

            i++;
            if (i == sequences.size()) {
                System.out.println("\n");
            }
        }
    }


    private static void printPhasingWithNumbers(Integer index, Set<VGSequence> sequences) {

        System.out.print("Index:" + index + " ");
        for (VGSequence alleleSequence : sequences) {
            System.out.print(alleleSequence + " " + variantGraph.positionsMap.get(index).genoTypeCountForSequence(alleleSequence) + " ");
        }
        System.out.println();

    }

}
