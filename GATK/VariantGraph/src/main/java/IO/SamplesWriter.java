package IO;

import Entities.Sampling.SampledGraph;
import Entities.Sampling.SampledIndividual;
import Utilities.Constants;
import Utilities.MyBean2RDF;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Johan on 18/05/15.
 */
public class SamplesWriter {


    public static void saveSamplesForChromosome(SampledGraph sampledGraph, String fileName) throws IOException {
        System.out.println("Saving " + fileName);

        long t1 = System.currentTimeMillis();

        File file = new File("VCFDataBase/generated_samples/" + fileName);
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fop = new FileOutputStream(file);

        String header = "##fileformat=VCFv4.1\n";
        fop.write(header.getBytes());
        String firstColumns = "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\t";
        fop.write(firstColumns.getBytes());

        for (SampledIndividual individual : sampledGraph.sampledIndividuals) {
            fop.write(individual.id.getBytes());
            String tab = "\t";
            fop.write(tab.getBytes());
        }

        fop.write("\n".getBytes());

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

            fop.write((sampledGraph.name + "\t" + position + "\t.\t").getBytes());

            int count = 0;
            for (String genotype : genotypes) {
                if (count == 0) {
                    fop.write((genotype + "\t").getBytes());
                } else {
                    fop.write((genotype + (count == genotypes.size() - 1 ? "\t" : ",")).getBytes());
                }
                count++;
            }
            if (count == 1) {
                fop.write(".\t".getBytes());
            }

            fop.write(("100.0\tPASS\tMQ=60;MQ0=0;DP=8876259;AN=6330\tGT\t").getBytes());

            for (SampledIndividual individual : sampledGraph.sampledIndividuals) {

                int genotypeCountFirst = 0;

                String phasingType = "/";
                for (String genotype : genotypes) {
                    if (sampledGraph.genotypeIndividualsFirst.get(position).containsKey(genotype) && sampledGraph.genotypeIndividualsFirst.get(position).get(genotype).keySet().contains(individual)) {

                        phasingType = sampledGraph.genotypeIndividualsFirst.get(position).get(genotype).get(individual) ? "|" : "/";
                        break;
                    } else {
                        genotypeCountFirst++;
                    }
                }

                int genotypeCountSecond = 0;
                for (String genotype : genotypes) {
                    if (sampledGraph.genotypeIndividualsSecond.get(position).containsKey(genotype) && sampledGraph.genotypeIndividualsSecond.get(position).get(genotype).keySet().contains(individual)) {
                        break;
                    } else {
                        genotypeCountSecond++;
                    }
                }

                fop.write((genotypeCountFirst + phasingType + genotypeCountSecond + "\t").getBytes());

            }

            fop.write("\n".getBytes());
        }


        long t2 = System.currentTimeMillis();
        System.out.println("Time to save samples " + (t2 - t1) + " ms.");
    }

    public static void saveSamplesForAllChromosomes(List<SampledGraph> sampledGraphs, String fileName) throws IOException {


        System.out.println("Saving " + fileName);

        File file = new File("VCFDataBase/generated_samples/" + fileName);
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fop = new FileOutputStream(file);

        String header = "##fileformat=VCFv4.1\n";
        fop.write(header.getBytes());
        String firstColumns = "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\t";
        fop.write(firstColumns.getBytes());

        for (SampledIndividual individual : sampledGraphs.get(0).sampledIndividuals) {
            fop.write(individual.id.getBytes());
            String tab = "\t";
            fop.write(tab.getBytes());
        }

        fop.write("\n".getBytes());

        for (SampledGraph sampledGraph : sampledGraphs) {

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

                fop.write((sampledGraph.name + "\t" + position + "\t.\t").getBytes());

                int count = 0;
                for (String genotype : genotypes) {
                    if (count == 0) {
                        fop.write((genotype + "\t").getBytes());
                    } else {
                        fop.write((genotype + (count == genotypes.size() - 1 ? "\t" : ",")).getBytes());
                    }
                    count++;
                }
                if (count == 1) {
                    fop.write(".\t".getBytes());
                }

                fop.write(("100.0\tPASS\tMQ=60;MQ0=0;DP=8876259;AN=6330\tGT\t").getBytes());

                for (SampledIndividual individual : sampledGraph.sampledIndividuals) {

                    int genotypeCountFirst = 0;

                    String phasingType = "/";
                    for (String genotype : genotypes) {
                        if (sampledGraph.genotypeIndividualsFirst.get(position).containsKey(genotype) && sampledGraph.genotypeIndividualsFirst.get(position).get(genotype).keySet().contains(individual)) {

                            phasingType = sampledGraph.genotypeIndividualsFirst.get(position).get(genotype).get(individual) ? "|" : "/";
                            break;
                        } else {
                            genotypeCountFirst++;
                        }
                    }

                    int genotypeCountSecond = 0;
                    for (String genotype : genotypes) {
                        if (sampledGraph.genotypeIndividualsSecond.get(position).containsKey(genotype) && sampledGraph.genotypeIndividualsSecond.get(position).get(genotype).keySet().contains(individual)) {
                            break;
                        } else {
                            genotypeCountSecond++;
                        }
                    }

                    fop.write((genotypeCountFirst + phasingType + genotypeCountSecond + "\t").getBytes());

                }

                fop.write("\n".getBytes());
            }
        }
    }
}
