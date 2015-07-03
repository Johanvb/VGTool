package Utilities;

import Entities.Jena.Graph.*;
import Entities.Jena.Probabilities.VGProbabilityGenotype;
import com.hp.hpl.jena.tdb.store.Hash;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by Johan on 22/04/15.
 */
public class GeneticsUtils {

    public enum PhasingType {
        UNPHASED, PHASED
    }

    private static HashMap<String, VGBaseValue> baseValues = new HashMap<String, VGBaseValue>();
    private static HashMap<String, List<VGBaseValue>> alleleListHashMap = new HashMap<String, List<VGBaseValue>>();
    private static HashMap<String, VGProbabilityGenotype> genotypeHashMap = new HashMap<String, VGProbabilityGenotype>();

    private static HashMap<String, String> probabilityPaths = new HashMap<String, String>();

    private static HashMap<String, Short> uniqueGenotypeMap = new HashMap<String, Short>();
    private static HashMap<Short, String> uniqueIdToGenotypeMap = new HashMap<Short, String>();

    private static short uniqueGenotypeCount = 0;

    public static Short uniqueStringForGenotype(VGProbabilityGenotype genotype) {
        if (!uniqueGenotypeMap.containsKey(genotype.toString())) {
            mapGenotype(genotype);
        }

        return uniqueGenotypeMap.get(genotype.toString());
    }

    private static synchronized void mapGenotype(VGProbabilityGenotype genotype) {
        if (!uniqueGenotypeMap.containsKey(genotype.toString())) {
            uniqueGenotypeMap.put(genotype.toString(), uniqueGenotypeCount);
            uniqueIdToGenotypeMap.put(uniqueGenotypeCount, genotype.toString());
            uniqueGenotypeCount++;
        }
    }

    public static VGProbabilityGenotype getGenotypeFromString(String key){
        return genotypeHashMap.get(key);
    }

    public static VGProbabilityGenotype getGenotype(VGProbabilityGenotype genotype) {
        String key = genotype.toString();
        if (!genotypeHashMap.containsKey(key)) {
            addGenotype(key, genotype);
        }

        return genotypeHashMap.get(key);
    }

    private static synchronized void addGenotype(String key, VGProbabilityGenotype genotype) {
        if (!genotypeHashMap.containsKey(key)) {
            genotypeHashMap.put(key, genotype);
        } else {
//            System.out.println("Caught it addGenotype");
        }
    }

    public static String getPathFromString(String inputPath) {
        if (!probabilityPaths.containsKey(inputPath)) {
            addProbabilityPath(inputPath);

        }

        return probabilityPaths.get(inputPath);

    }

    private static synchronized void addProbabilityPath(String inputPath) {
        if (!probabilityPaths.containsKey(inputPath)) {
            probabilityPaths.put(inputPath, inputPath);
//            miss++;
        } else {
//            System.out.println("Caught it addProbabilityPath");
        }
    }

    public static List<VGBaseValue> getBaseList(List<VGBaseValue> list) {

        String key = StringUtils.join(list, ",");

        if (!alleleListHashMap.containsKey(key)) {
            addBaseList(key, list);
        }

        return alleleListHashMap.get(key);
    }

    private static synchronized void addBaseList(String key, List<VGBaseValue> list) {
        if (!alleleListHashMap.containsKey(key)) {
            alleleListHashMap.put(key, list);
        } else {
//            System.out.println("Caught it");
        }
    }

    public static String getGenotypeFromId(String s) {
        return uniqueIdToGenotypeMap.get(Short.parseShort(s));
    }


    public static synchronized VGBaseValue getBaseValueFromChar(char valueChar) {
        String s = "" + valueChar;
        if (!baseValues.containsKey(s)) {
            baseValues.put(s, new VGBaseValue(s));
        }
        return baseValues.get(s);
    }

    public static synchronized VGBaseValue getBaseValueFromBaseValue(VGBaseValue value) {
        String s = "" + value.getId();
        if (!baseValues.containsKey(s)) {
            baseValues.put(s, new VGBaseValue(s));
        }
        return baseValues.get(s);
    }

    public static void bindSequencesWithGap(Set<VGSequence> sequencesBeforeGap, VGSequenceGap gapSequence, Set<VGSequence> sequencesAfterGap) {
        for (VGSequence sequenceBefore : sequencesBeforeGap) {
            sequenceBefore.successors.add(gapSequence);
            gapSequence.predecessors.add(sequenceBefore);
        }

        for (VGSequence sequenceAfter : sequencesAfterGap) {
            sequenceAfter.predecessors.add(gapSequence);
            gapSequence.successors.add(sequenceAfter);
        }
    }

    public static void crossbindSequences(Set<VGSequence> sequencesBeforeGap, Set<VGSequence> sequencesAfterGap) {

        for (VGSequence sequenceBefore : sequencesBeforeGap) {
            for (VGSequence sequenceAfter : sequencesAfterGap) {
                sequenceBefore.successors.add(sequenceAfter);
                sequenceAfter.predecessors.add(sequenceBefore);
            }
        }
    }

//    public static Pair<VGSequence, VGSequence> getGenotypePair(VGSequence first, VGSequence second, boolean phased) {
//        if(!alleleListHashMap.containsKey(first.toString() + (phased ? "|" : "/") + second.toString())){
//            alleleListHashMap.put(first.toString() + (phased ? "|" : "/") + second.toString(), new Pair<VGSequence, VGSequence>(first, second));
//        }
//
//        return alleleListHashMap.get(first.toString() + (phased ? "|" : "/") + second.toString());
//    }


}
