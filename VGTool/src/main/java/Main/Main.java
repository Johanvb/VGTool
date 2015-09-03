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
    public static void main(String args[]) throws Exception {
        MainCLI.interactive();
    }
}