package Entities.GraphHandling;

import Entities.Jena.Graph.VGPosition;
import Entities.Jena.Probabilities.VGProbabilityNode;
import Entities.Jena.Probabilities.VGProbabilityTree;

import java.util.List;

/**
 * Created by Johan on 01/06/15.
 */
public class ProbabilityInitializer {

    int logInterval = 5000;


    public void addProbabilitiesToGraph(List<VGPosition> positions) {
        int i = 0;
//        System.out.println("Building genotype sets.");


        for (VGPosition position : positions) {
            instantiateProbabilityTreeForPosition(position);
//            position.buildGenotypeSets();
            if (i % logInterval == 0) {
//                System.out.println("Building genotype at " + position.getPosition());
            }
            i++;
        }

    }

    private void instantiateProbabilityTreeForPosition(VGPosition position) {
        VGProbabilityTree probabilityTree = new VGProbabilityTree(position.getId());
//        VGProbabilityTree probabilityTree = new VGProbabilityTree(position.getId());
//        probabilityTree.root = new VGProbabilityNode(position.getPosition() + ":Root");
        probabilityTree.root = new VGProbabilityNode(probabilityTree.getValidNodeId());
        position.setProbabilityTree(probabilityTree);
    }


}
