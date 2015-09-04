package Main;

import Entities.Pair;
import Entities.Sampling.SampledGraph;
import Multithreading.ThreadHandler;
import IO.VCFToGraphsBuilder;
import IO.StorageInputHandler;
import IO.VCFInputHandler;
import IO.SamplesWriter;
import Utilities.Constants;
import Utilities.EnclosingModel;
import Utilities.GraphPrinter;
import com.hp.hpl.jena.graph.query.SimpleQueryEngine;
import htsjdk.tribble.FeatureReader;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;


import org.apache.commons.cli.*;
//CommandLineParser;
//import org.apache.commons.cli.CommandLine;
//import org.apache.commons.cli.DefaultParser;
//import org.apache.commons.cli.ParseException;
//import org.apache.commons.cli.Option;
//import org.apache.commons.cli.Options;
//import org.apache.commons.cli.OptionBuilder;
//import org.apache.commons.cli.HelpFormatter;

import java.io.IOException;
import java.util.*;

/**
 * Created by Christian on 02/09/15.
 */


public class MainCLI {
    private static boolean graph_loaded = false;
    private static boolean graph_probabilities = false;
    private static boolean samples_created = false;
    private static List<SampledGraph> sampledGraphs = null;
    private static String chromosome = null;

    public static void main(String args[]) throws Exception {
		CommandLineParser parser = new org.apache.commons.cli.PosixParser();

		Options options = new Options();

		options.addOption("h", "help", false, "print this message" );
		options.addOption("v", "version",  false, "print the version information and exit" );
		options.addOption(OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("vcf file with samples to load")
                .withLongOpt("vcf")
                .create("f"));
		options.addOption(OptionBuilder.withArgName("N")
                .hasArg()
                .withDescription("sample <N> individuals")
                .withLongOpt("sample")
                .create("s"));
       options.addOption( OptionBuilder.withArgName( "Graph rdf file" )
                                    .hasArg()
                                    .withDescription("Load graph from graph RDF format")
									.withLongOpt("graph")
                                    .create("g"));
       options.addOption( OptionBuilder.withArgName( "file" )
                                    .hasArg()
                                    .withDescription("Load graph from graph RDF format")
									.withLongOpt("graph")
                                    .create("g"));
       options.addOption( OptionBuilder.withArgName( "file" )
                                    .hasArg()
                                    .withDescription("Write sampled individuals in VCF format to <file>")
									.withLongOpt("output-vcf")
                                    .create("ov"));
       options.addOption( OptionBuilder.withArgName( "file" )
                                      .hasArg()
                                      .withDescription("Write graph in RDF format to <file>")
                                      .withLongOpt("output-rdf")
                                      .create("or"));
       options.addOption(OptionBuilder.withArgName("Integer")
               .hasArg()
               .withDescription("Minimum context size (default=" + Constants.depthThreshold + ")")
               .withLongOpt("context-size")
               .create("cs"));
       options.addOption(OptionBuilder.withArgName("Integer")
               .hasArg()
               .withDescription("Sampling queue size (default=" + Constants.queueSize + ")")
               .withLongOpt("queue-size")
               .create("qs"));

       options.addOption(OptionBuilder.withArgName("Integer")
               .hasArg()
               .withDescription("Probability of selecting using largest available context when sampling (defines genometric sampling distribution) (default=" + Constants.pickHeadProbability + ")")
               .withLongOpt("queue-probability")
               .create("qp"));

       options.addOption(OptionBuilder.withArgName("I")
               .hasArg()
               .withDescription("Consider only chromosome <I>")
               .withLongOpt("chr")
               .create("c"));

       options.addOption(OptionBuilder.withArgName("Integer")
               .hasArg()
               .withDescription("Maximal number of concurrent threads used by program (default is number of available processors=" + Constants.threads + ")")
               .withLongOpt("threads")
               .create("t"));

        options.addOption("p", "probabilities", false, "Add probabilities to graph (implied by -s option)");
        options.addOption("i", "interactive", false, "Run program in interactive mode");

		try {
			// parse the command line arguments
			CommandLine line = parser.parse( options, args );

		    // validate that block-size has been set
			if (line.hasOption("help")) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "java -jar VGTool.jar", options );
			}

            if (line.hasOption("qs")) {
                Constants.queueSize = Integer.parseInt(line.getOptionValue("qs"));
                System.out.println("Queue size set to " + Constants.queueSize);
            }

            if (line.hasOption("qp")) {
                Constants.pickHeadProbability = Float.parseFloat(line.getOptionValue("qp"));
                System.out.println("Queue probability set to " + Constants.pickHeadProbability);
            }

            if (line.hasOption("cs")) {
                Constants.individualsThreshold = Integer.parseInt(line.getOptionValue("cs"));
                System.out.println("Minimum context size set to " + Constants.individualsThreshold);
            }

            if (line.hasOption("t")) {
                Constants.threads = Integer.parseInt(line.getOptionValue("t"));
                System.out.println("Maximum thread utilization is set to " + Constants.threads + " threads");
            }

            if (line.hasOption("c")) {
                chromosome = line.getOptionValue("c");
                System.out.println("Restricting input and analyses to chromosome " + chromosome);
            }

            if (line.hasOption("f")) {
                System.out.println("Input VCF file: " + line.getOptionValue("f"));
                loadVCF(line.getOptionValue("f"));
                mergeGraphs();
                graph_loaded = true;
            }

            if (line.hasOption("g")) {
                System.out.println("Input RDF file: " + line.getOptionValue("g"));
                StorageInputHandler.readGraphFromFile(line.getOptionValue("g"));
                mergeGraphs();
                // TODO: Check if graph has probabiilities
                graph_loaded = true;
            }

            if (options.hasOption("p")) {
                addProbabilities();
            }

            if (line.hasOption("s")) {
                System.out.println("sampling " + line.getOptionValue("s") + " individuals");
                createSamples(Integer.parseInt(line.getOptionValue("s")));
            }

            if (line.hasOption("or")) {
                if (!graph_loaded) {
                    System.err.println("No graph loaded. Nothing to write to RDF file");
                }
                System.out.println("Writing graph to file: " + line.getOptionValue("or"));
                writeGraphs(line.getOptionValue("or"));
            }

            if (line.hasOption("ov")) {
                if (!samples_created) {
                    System.err.println("No samples produced. Nothing to write to VCF file.");
                } else {
                    System.out.println("Writing samples to VCF file " + line.getOptionValue("ov"));
                    writeVCFFile(line.getOptionValue("ov"));
                }
            }


            // Finally
            if (line.hasOption("i")) {
                interactive();
            }
		}
		catch( ParseException exp ) {
	    	System.out.println( "Unexpected exception:" + exp.getMessage() );
		}
    }


    public static void interactive() throws Exception {
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("Choose option:");
            System.out.println("(LF) Load VCF file from path");
            System.out.println("(R) Load RDF file from path");
            System.out.println("(W) Write graphs to files.");
            System.out.println("(P) Add probabilities to graphs.");
            System.out.println("(S #) Create # samples.");
            System.out.println("(PGS) Print graph sizes.");
            System.out.println("(PG) Print graphs.");

           System.out.println("(Q) Quit");

           String input = in.nextLine();

           if (input.equals("LF")) {
                System.out.println("\nEnter filepath for file.");
                loadVCF(in.nextLine());
           }  else if (input.equals("R")) {
                System.out.println("\nReading from RDF.");
                StorageInputHandler.readGraphFromFile(in.nextLine());
                mergeGraphs();
           }  else if (input.equals("W")) {
                System.out.println("\nSaving graphs to RDF.");
                System.out.println("Enter name for new file.");
                String fileName = in.next();
                writeGraphs(fileName);
           } else if (input.equals("P")) {
                System.out.println("\nAdding probabilities to graphs");
                addProbabilities();
           } else if (input.equals("PGS")) {
                System.out.println("\nSizes:");

                GraphPrinter.printAllGraphSizes();
           } else if (input.contains("S")) {
               String[] strings = input.split(" ");
               try {
                   if (strings.length == 3) {
                       chromosome = strings[1];
                       createSamples(Integer.parseInt(strings[2]));
                   } else if (strings.length == 2) {
                       createSamples(Integer.parseInt(strings[1]));
                   }
               } catch (NumberFormatException e) {
                   System.out.println("Bad number input");
               }
               System.out.println("\nSave samples to file? (Y/N)");

               if (in.next().equals("Y")) {
                   System.out.println("Enter name for new file.");
                   String fileName = in.next();
                   writeVCFFile(fileName);
               }
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

    private static void loadVCF(String fileToLoad) throws IOException, InterruptedException {
        Pair<VCFHeader, FeatureReader<VariantContext>> vcfInput;
        try {
            vcfInput = VCFInputHandler.readInputVCF(fileToLoad);
        } catch (Exception e) {
            System.out.println("Couldn't load file");
            return;
        }
        VCFToGraphsBuilder.loadGraphs(vcfInput);

        shrinkGraphs();
    }

    private static void createSamples(int numberOfSamples) throws InterruptedException {
        if (!graph_loaded) {
            System.err.println("Cannot sample individuals from graph when no graph is loaded..");
            return;
        }
        if (!graph_probabilities) {
                addProbabilities();
        }

        try {
            if (chromosome != null) {
                if (EnclosingModel.chromosomeGraphs.containsKey(chromosome) && EnclosingModel.chromosomeGraphs.get(chromosome).size() == 1) {
                    if (EnclosingModel.chromosomeGraphs.get(chromosome).iterator().next().hasProbabilities) {
                        System.out.println("Creating " + numberOfSamples + " samples for " + chromosome);
                        sampledGraphs = new LinkedList<SampledGraph>();
                        sampledGraphs.add(ThreadHandler.startSamplingThreads(EnclosingModel.chromosomeGraphs.get(chromosome).iterator().next(), numberOfSamples));
                    } else {
                        System.out.println("Can't create samples. No probabilities in graph.");
                    }
                    samples_created = true;
                } else {
                    System.err.println("Cannot create samples for chromosome " + chromosome);
                }
            } else {
                sampledGraphs =  ThreadHandler.startSamplingThreadsForAllGraphs(numberOfSamples);
                samples_created = true;
            }
        } catch (IOException e) {
           System.out.println("IO Exception");
        }
    }

    private static void addProbabilities() throws InterruptedException {
        System.out.println("Estimating graph probabilities");
        ThreadHandler.startGraphProbabilityThreadsForGraphs(EnclosingModel.getGraphsForProbabilities());
        graph_probabilities = true;
    }

    private static void writeGraphs(String outputFile) throws InterruptedException {
        ThreadHandler.startGraphWritersThreadWithGraphs(EnclosingModel.getGraphsForWrite(), outputFile);
    }

    private static void shrinkGraphs() throws InterruptedException {
        //TODO: Rethink when to shrink. Because merging re-expands graphs
        ThreadHandler.startGraphShrinkerThreadsWithGraphs(EnclosingModel.getGraphsToShrink());
    }

    private static void mergeGraphs() throws InterruptedException {
        ThreadHandler.startGraphMergerThreadsWithGraphs(EnclosingModel.getGraphSetsToMerge());
    }

    private static void writeVCFFile(String fileName) throws InterruptedException {
        try {
            SamplesWriter.saveSamplesForAllChromosomes(sampledGraphs, fileName);
        } catch (IOException e) {
            System.err.println("Could not save VCF file: " + fileName);
            e.printStackTrace();
        }
    }
}
