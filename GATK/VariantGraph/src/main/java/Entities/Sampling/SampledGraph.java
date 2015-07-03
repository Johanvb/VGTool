package Entities.Sampling;

import Entities.Jena.Graph.*;

import java.util.*;

/**
 * Created by Johan on 14/05/15.
 */
public class SampledGraph {

    public String name;

    public List<SampledIndividual> sampledIndividuals;

    //Position <Genotype, Individual>
    public Map<Integer, Map<String, Map<SampledIndividual, Boolean>>> genotypeIndividualsFirst;
    public Map<Integer, Map<String, Map<SampledIndividual, Boolean>>> genotypeIndividualsSecond;


    public SampledGraph(VariantGraph graph, List<SampledIndividual> sampledIndividuals) {
        name = graph.getId();
        this.sampledIndividuals = sampledIndividuals;
        genotypeIndividualsFirst = new HashMap<Integer, Map<String, Map<SampledIndividual, Boolean>>>();
        genotypeIndividualsSecond = new HashMap<Integer, Map<String, Map<SampledIndividual, Boolean>>>();
    }


    public void addSampleForKeyAndGenotype(Map<Integer, Map<String, Map<SampledIndividual, Boolean>>> map, int index, String genotype, SampledIndividual individual, boolean positionShouldBePhased) {
        int count = 0;

        String[] strings = genotype.split(".");

        if(strings.length == 0){
            strings = new String[]{genotype};
        }

        for (String s : strings) {
            String alleleSequence = s.replaceAll(",", "");
            Integer positionForGenotype = index + count;
            if (!map.containsKey(positionForGenotype)) {
                addPosition(map, positionForGenotype);
            }
            if (map.get(positionForGenotype).get(alleleSequence) == null) {
                addSequence(map, positionForGenotype, alleleSequence);
            }
            putIndividual(map, positionForGenotype, alleleSequence, individual, positionShouldBePhased);
            count += alleleSequence.length();
        }
    }

    private synchronized void addPosition(Map<Integer, Map<String, Map<SampledIndividual, Boolean>>> map, Integer positionForGenotype){
        if (!map.containsKey(positionForGenotype)) {
            map.put(positionForGenotype, new HashMap<String, Map<SampledIndividual, Boolean>>() );
        }
    }

    private synchronized void addSequence(Map<Integer, Map<String, Map<SampledIndividual, Boolean>>> map, Integer positionForGenotype, String alleleSequence){
        if (map.get(positionForGenotype).get(alleleSequence) == null) {
            map.get(positionForGenotype).put(alleleSequence, new HashMap<SampledIndividual, Boolean>());
        }
    }

    private synchronized void putIndividual(Map<Integer, Map<String, Map<SampledIndividual, Boolean>>> map, Integer positionForGenotype, String alleleSequence, SampledIndividual individual, boolean positionShouldBePhased){
        map.get(positionForGenotype).get(alleleSequence).put(individual, positionShouldBePhased);

    }

}
