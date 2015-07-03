package Entities.Sampling;

import java.util.*;

/**
 * Created by Johan on 14/05/15.
 */
public class SampleUtilities {


    public static List<SampledIndividual> getSampledIndividuals(int count) {

        List<SampledIndividual> individuals = new ArrayList<SampledIndividual>();

        for (int i = 0; i < count; i++) {
            individuals.add(new SampledIndividual("" + i));
        }

        return individuals;
    }

    public static void printCreatedSamples(SampledGraph sampledGraph) {

        System.out.print("Individuals ");
        int entriesPerline = 0;
        for (SampledIndividual individual : sampledGraph.sampledIndividuals) {
            System.out.print(individual.id + " ");
            entriesPerline++;
            if (entriesPerline % 25 == 0) {
                System.out.println();
            }
        }
        System.out.println();

        ArrayList<Integer> positions = new ArrayList<Integer>();
        positions.addAll(sampledGraph.genotypeIndividualsFirst.keySet());

        Collections.sort(positions);

        for (Integer position : positions) {
            List<String> genotypes = new ArrayList<String>();

            List<String> genotypesFirst = new ArrayList<String>();
            genotypesFirst.addAll(sampledGraph.genotypeIndividualsFirst.get(position).keySet());
            List<String> genotypesSecond = new ArrayList<String>();
            genotypesSecond.addAll(sampledGraph.genotypeIndividualsSecond.get(position).keySet());


            for (String genotype : genotypesFirst) {
                if (!genotypes.contains(genotype)) {
                    genotypes.add(genotype);
                }
            }
            for (String genotype : genotypesSecond) {
                if (!genotypes.contains(genotype)) {
                    genotypes.add(genotype);
                }
            }

            Collections.sort(genotypesFirst);
            Collections.sort(genotypesSecond);
            Collections.sort(genotypes);

            boolean allPositions = false;

            if (genotypes.size() > 1 || allPositions) {
                System.out.print("Position " + position + " ");
                int count = 0;
                for (String genotype : genotypes) {
                    System.out.print(genotype + (count == genotypes.size() - 1 ? "\t" : ","));
                    count++;
                }

                entriesPerline = 0;
                for (SampledIndividual individual : sampledGraph.sampledIndividuals) {

                    int genotypeCount = 0;
                    for (String genotype : genotypes) {
                        if (sampledGraph.genotypeIndividualsFirst.get(position).containsKey(genotype) && sampledGraph.genotypeIndividualsFirst.get(position).get(genotype).keySet().contains(individual)) {

                            String phasingType = sampledGraph.genotypeIndividualsFirst.get(position).get(genotype).get(individual) ? "|" : "/";

                            System.out.print(genotypeCount + phasingType);
                            break;
                        } else {
                            genotypeCount++;
                        }
                    }

                    genotypeCount = 0;
                    for (String genotype : genotypes) {
                        if (sampledGraph.genotypeIndividualsSecond.get(position).containsKey(genotype) && sampledGraph.genotypeIndividualsSecond.get(position).get(genotype).keySet().contains(individual)) {
                            System.out.print(genotypeCount + "\t");
                            break;
                        } else {
                            genotypeCount++;
                        }
                    }

                    entriesPerline++;
                    if (entriesPerline % 25 == 0) {
                        System.out.println();
                    }
                }

                System.out.println();
            }
        }
    }
}
