package IO;

import Entities.Jena.Graph.*;
import Entities.Jena.Other.VGIndividual;
import Entities.Pair;
import Utilities.GeneticsUtils;
import Utilities.Constants;
import Utilities.EnclosingModel;
import htsjdk.tribble.FeatureReader;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;

import java.io.IOException;
import java.util.*;

/**
 * Created by Johan on 21/04/15.
 */
public class VCFToGraphsBuilder {

    private static Map<String, VariantGraph> graphsInFile;

    private static Map<String, VGIndividual> individualsMap;

    public static void loadGraphs(Pair<VCFHeader, FeatureReader<VariantContext>> input) throws IOException {

        long start = System.currentTimeMillis();

        individualsMap = new HashMap<String, VGIndividual>();

        graphsInFile = new HashMap<String, VariantGraph>();

        VCFHeader header = input.getKey();
        FeatureReader<VariantContext> reader = input.getValue();

        addIndividualsToModel(header);
        buildVariationRegions(reader.iterator());
        reader.close();

        for (String key : graphsInFile.keySet()) {
            VariantGraph graph = graphsInFile.get(key);
            EnclosingModel.putNewGraphForKey(graph.getId(), graph);
        }

        long end = System.currentTimeMillis();

        individualsMap = null;
        System.out.println("Loaded in " + (end - start) + " ms.");
    }


    private static void addIndividualsToModel(VCFHeader header) {
        for (String gS : header.getGenotypeSamples()) {
            String newIndividualName = EnclosingModel.getUnusedIndividualName();
            VGIndividual individual = new VGIndividual(newIndividualName);
//            if (!EnclosingModel.getPeopleMap().containsKey(individual.getId())) {
            EnclosingModel.addIndividualWithId(individual);
//            }else{
            individualsMap.put(gS, individual);
//            }
        }
    }

    private static void buildVariationRegions(Iterator<VariantContext> it) {

        int count = 0;

        long time1 = System.currentTimeMillis();

        long time2;

        while (it.hasNext()) {
            VariantContext vc = it.next();

            if (count % 10000 == 0) {
                time2 = System.currentTimeMillis();
                System.out.println("Time " + (time2 - time1) + " ms.   " + count);
                time1 = System.currentTimeMillis();
            }
            count++;

            if (!graphsInFile.containsKey(vc.getChr())) {
                VariantGraph graph = new VariantGraph(vc.getChr());
                graph.setChromosome(new VGChromosome(vc.getChr()));
                graphsInFile.put(vc.getChr(), graph);
            }

            Iterable<Genotype> iterable = vc.getGenotypesOrderedByName();

            HashMap<Pair<String, String>, HashSet<VGIndividual>> unphased = new HashMap<Pair<String, String>, HashSet<VGIndividual>>();
            HashMap<Pair<String, String>, HashSet<VGIndividual>> phased = new HashMap<Pair<String, String>, HashSet<VGIndividual>>();
            HashMap<Pair<String, String>, HashSet<VGIndividual>> invalidUnphased = new HashMap<Pair<String, String>, HashSet<VGIndividual>>();
            HashMap<Pair<String, String>, HashSet<VGIndividual>> invalidPhased = new HashMap<Pair<String, String>, HashSet<VGIndividual>>();

            for (Genotype genotype : iterable) {
                String sampleName = individualsMap.get(genotype.getSampleName()).getId();

                if (genotype.hasDP() && genotype.getDP() >= Constants.depthThreshold) {
                    if (genotype.isPhased()) {
                        putIndividualInSetForKey(phased, sampleName, genotype.getAlleles().get(0).getBaseString(), genotype.getAlleles().get(1).getBaseString());
                    } else if (!genotype.getAlleles().get(0).getBaseString().equals(".")) {
                        putIndividualInSetForKey(unphased, sampleName, genotype.getAlleles().get(0).getBaseString(), genotype.getAlleles().get(1).getBaseString());
                    }
                } else {
                    if (genotype.isPhased()) {
                        putIndividualInSetForKey(invalidPhased, sampleName, genotype.getAlleles().get(0).getBaseString(), genotype.getAlleles().get(1).getBaseString());
                    } else if (!genotype.getAlleles().get(0).getBaseString().equals(".")) {
                        putIndividualInSetForKey(invalidUnphased, sampleName, genotype.getAlleles().get(0).getBaseString(), genotype.getAlleles().get(1).getBaseString());
                    }
                }
            }

            VGPosition position = graphsInFile.get(vc.getChr()).createPosition(vc.getStart());

            for (Allele allele : vc.getAlleles()) {
                short i = graphsInFile.get(vc.getChr()).positionsMap.containsKey(vc.getStart()) ?
                        (short) graphsInFile.get(vc.getChr()).positionsMap.get(vc.getStart()).sequencesSet.size() : 0;

                VGSequence sequence = new VGSequence(null);
                sequence.init(position, i);
                addAlleleToSequence(allele, sequence);

                graphsInFile.get(vc.getChr()).addToSequences(vc.getStart(), sequence);
            }

            graphsInFile.get(vc.getChr()).positionsMap.get(vc.getStart()).addIndividualSets(unphased, phased, invalidPhased, invalidUnphased);
        }
    }


    private static void addAlleleToSequence(Allele allele, VGSequence sequence) {

        VGAlleleList alleleList = new VGAlleleList();

        List<VGBaseValue> newBases = new ArrayList<VGBaseValue>();
        for (char baseValue : allele.getBaseString().toCharArray()) {
            VGBaseValue baseValueObject = GeneticsUtils.getBaseValueFromChar(baseValue);
            newBases.add(baseValueObject);
        }

        alleleList.setBases(newBases);
        sequence.allelesLists.add(alleleList);
    }


    private static void putIndividualInSetForKey(HashMap<Pair<String, String>, HashSet<VGIndividual>> map, String sampleName, String first, String second) {
        Pair<String, String> key = new Pair<String, String>(first, second);
        if (!map.containsKey(key)) {
            map.put(key, new HashSet<VGIndividual>());
        }

        map.get(key).add(EnclosingModel.getPeopleMap().get(sampleName));
    }


}