package Main;

import Entities.Pair;
import Multithreading.ThreadHandler;
import IO.VCFToGraphsBuilder;
import IO.StorageInputHandler;
import IO.VCFInputHandler;
import Utilities.EnclosingModel;
import Utilities.GraphPrinter;
import htsjdk.tribble.FeatureReader;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;

import java.io.IOException;
import java.util.*;

/**
 * Created by Johan on 20/04/15.
 */


public class Main {


    private static String fileToLoad;

    public static void main(String args[]) throws Exception {

        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("Choose option:");
            System.out.println("(L) Load original file.");
            System.out.println("(LF) Load file from path.");
            System.out.println("(C) Load different chromosomes file.");
            System.out.println("(LP) Load phased file.");
            System.out.println("(SV) Load structural variants file.");
            System.out.println("(G) Load different genotypes file.");
            System.out.println("(R) Load from rdf.");
            System.out.println("(W) Write graphs to files.");
            System.out.println("(P) Add probabilities to graphs.");
            System.out.println("(S #) Create # samples.");
            System.out.println("(PGS) Print graph sizes.");
            System.out.println("(PG) Print graphs.");

            System.out.println("(Q) Quit");

            String input = in.nextLine();

            if (input.equals("L")) {
                System.out.println("\nLoading original file.");

                String fileNumber = in.nextLine();

                int number = Integer.parseInt(fileNumber);

                switch (number){
                    case 1:
                        fileToLoad = "VCF/5000_testdata.vcf";
                        break;
                    case 2:
                        fileToLoad = "VCF/10000_testdata.vcf";
                        break;
                    case 3:
                        fileToLoad = "VCF/100000_testdata.vcf";
                        break;
                    case 4:
                        fileToLoad = "VCF/250000_testdata.vcf";
                        break;
                    case 5:
                        fileToLoad = "VCF/1000000_testdata.vcf";
                        break;
                    case 6:
                        fileToLoad = "VCF/new_5000_testdata.vcf";
                        break;
                    case 7:
                        fileToLoad = "VCF/new_10000_testdata.vcf";
                        break;
                    case 8:
                        fileToLoad = "VCF/new_100000_testdata.vcf";
                        break;
                    case 9:
                        fileToLoad = "VCF/new_250000_testdata.vcf";
                        break;
                    case 10:
                        fileToLoad = "VCF/new_1000000_testdata.vcf";
                        break;
                    case 11:
                        fileToLoad = "VCF/new_all_var500_testdata.vcf";
                        break;
                    case 12:
                        fileToLoad = "VCF/new_all_var1000_testdata.vcf";
                        break;
                    case 13:
                        fileToLoad = "VCF/new_all_var2000_testdata.vcf";
                        break;
                    case 14:
                        fileToLoad = "VCF/new_all_var5000_testdata.vcf";
                        break;
                    case 15:
                        fileToLoad = "VCF/new_all_var10000_testdata.vcf";
                        break;
                }

//                fileToLoad = "VCF/50trios.vcf";
                // fileToLoad = "VCF/original_trimmed_mody.vcf";
//                fileToLoad = "VCF/extracted_50trios_chr21.vcf";

//                fileToLoad = "VCF/50trios.chr21.unphased.vcf";
//                fileToLoad = "VCF/10k_trimmed_mody_research_samples_3000.vcf";

                load();
            } else if (input.equals("LF")) {
                System.out.println("\nEnter filepath for file.");

                fileToLoad = in.nextLine();
                load();
            } else if (input.equals("LP")) {
                System.out.println("\nLoading phased file.");

                fileToLoad = "VCF/modified_phasing.vcf";
                load();
            } else if (input.equals("R")) {
                System.out.println("\nReading from RDF.");

                StorageInputHandler.readGraphFromFile();
                mergeGraphs();
            } else if (input.equals("C")) {
                System.out.println("\nLoading modified chromosomes file.");

                fileToLoad = "VCF/modified_chromosome.vcf";
                load();
            } else if (input.equals("G")) {
                System.out.println("\nLoading modified genotypes file.");

                fileToLoad = "VCF/modified_genotypes.vcf";
                load();
            } else if (input.equals("W")) {
                System.out.println("\nSaving graphs to RDF.");

                writeGraphs();
            } else if (input.equals("P")) {
                System.out.println("\nAdding probabilities to graphs");

                addProbabilities();
            } else if (input.equals("PGS")) {
                System.out.println("\nSizes:");

                GraphPrinter.printAllGraphSizes();
            } else if (input.equals("SV")) {
                System.out.println("\nLoading structural variants file.");

                fileToLoad = "VCF/structural_variants.vcf";
                load();
            } else if (input.contains("S")) {

                createSamples(input);

            } else if (input.contains("PG")) {

                GraphPrinter.printAllGraphs();

            } else if (input.equals("Q")) {

                System.out.println("\nQuitting.");

                return;
            } else {
                System.out.println("Bad input.");
            }
            System.out.println();

        }

    }

    private static void createSamples(String input) throws InterruptedException {

        try {

            String[] strings = input.split(" ");
            if (strings.length == 3) {
                if (strings[0].equals("S")) {

                    int numberOfSamples = Integer.parseInt(strings[2]);

                    String chr = strings[1];

                    if (EnclosingModel.chromosomeGraphs.containsKey(chr) && EnclosingModel.chromosomeGraphs.get(chr).size() == 1) {

                        if (EnclosingModel.chromosomeGraphs.get(chr).iterator().next().hasProbabilities) {
                            System.out.println("Creating " + numberOfSamples + " samples for " + chr);

                            ThreadHandler.startSamplingThreads(EnclosingModel.chromosomeGraphs.get(chr).iterator().next(), numberOfSamples);
                        } else {
                            System.out.println("Can't create samples. No probabilities in graph.");
                        }
                    }
                }
            } else if (strings.length == 2) {

                if (strings[0].equals("S")) {

                    int numberOfSamples = Integer.parseInt(strings[1]);

                    ThreadHandler.startSamplingThreadsForAllGraphs(numberOfSamples);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Bad number input");
        } catch (IOException e) {
            System.out.println("IO Exception");
        }

    }

    private static void addProbabilities() throws InterruptedException {

        ThreadHandler.startGraphProbabilityThreadsForGraphs(EnclosingModel.getGraphsForProbabilities());

    }

    private static void writeGraphs() throws InterruptedException {
        ThreadHandler.startGraphWritersThreadWithGraphs(EnclosingModel.getGraphsForWrite());
    }


    private static void load() throws IOException, InterruptedException {
        Pair<VCFHeader, FeatureReader<VariantContext>> vcfInput = null;
        try {
            vcfInput = VCFInputHandler.readInputVCF(fileToLoad);
        } catch (Exception e) {
            System.out.println("Couldn't load file");
            return;
        }
        VCFToGraphsBuilder.loadGraphs(vcfInput);

        shrinkGraphs();
        mergeGraphs();
    }

    private static void shrinkGraphs() throws InterruptedException {
        //TODO: Rethink when to shrink. Because merging re-expands graphs
        ThreadHandler.startGraphShrinkerThreadsWithGraphs(EnclosingModel.getGraphsToShrink());
    }

    private static void mergeGraphs() throws InterruptedException {
        ThreadHandler.startGraphMergerThreadsWithGraphs(EnclosingModel.getGraphSetsToMerge());
    }

}