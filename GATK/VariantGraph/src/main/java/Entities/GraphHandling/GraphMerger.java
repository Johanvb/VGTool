package Entities.GraphHandling;

import Entities.Jena.Graph.VGPosition;
import Entities.Jena.Graph.VGSequence;
import Entities.Jena.Graph.VariantGraph;
import Utilities.VariantGraphUtilities;

/**
 * Created by Johan on 08/05/15.
 */
public class GraphMerger {

    public void mergeGraphs(VariantGraph graph1, VariantGraph graph2) {


        System.out.println("Before merge graph size " + VariantGraphUtilities.totalNodesInGraph(graph1));
        //Merging into g1

        int count = 0;
        for (VGPosition position : graph2.positions) {
            if (count % 5000 == 0){
                System.out.println("Merge " + count);
            }

            count++;
            graph1.insertPositionIntoGraph(position);

            graph1.removeProbabilitiesForPosition(position);
        }

        graph1.sortSequences();
        System.out.println("After merge graph size " + VariantGraphUtilities.totalNodesInGraph(graph1));

        graph1.hasProbabilities = false;

    }
}
