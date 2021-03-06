package Utilities;



/**
 * Created by Johan on 20/04/15.
 */
public class Constants {

    public static final String SOURCE = "http://vg";
    public static final String NS = SOURCE + "#";

    public static int depthThreshold = 0;
    public static int individualsThreshold = 5;

    //-1 is no maximum depth.
    public static int maxDepthForProbabilityTrees = -1;

    //Size of tree queue for sampling
    public static int treeQueueSize = 20;

    public static boolean compressTrees = false;
    public static boolean compressIndividualSets = false;
    public static boolean shouldUseDatabase = false;

    public static int queueSize = 2;
    public static float pickHeadProbability = 0.9f;

    public static int threads = Runtime.getRuntime().availableProcessors();

}
