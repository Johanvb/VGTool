package Entities.Jena.Probabilities;


import Entities.Jena.Graph.VGSequence;
import Entities.Jena.JenaObject;
import thewebsemantic.Namespace;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by Johan on 29/04/15.
 */
@Namespace(JenaObject.ns)
public class VGProbabilityTree extends JenaObject {

    public VGProbabilityNode root;
    int nodeId;

    public VGProbabilityTree(String id) {
        super(id);
        nodeId =0;
    }

    public String getValidNodeId(){
        int returnValue = nodeId;
        nodeId++;
        return getId()+":"+returnValue;
    }

    public VGProbabilityNode getRoot() {
        return root;
    }

    public void setRoot(VGProbabilityNode root) {
        this.root = root;
    }

    public void printProbabilities() {
        root.printChildrenAndProbabilities();
    }


    public void setProbabilityFromChildren() {
        root.setProbabilityFromChildren();

    }

    public boolean hasCalculatedUnphasedRootWithSequences(VGSequence first, VGSequence second) {

        for (VGProbabilityNode child : root.getChildren()) {
            if (child.getGenotype() == null) {
                continue;
            }
            if (!child.getGenotype().phased) {

                if (child.getGenotype().isEqualTo(first, second) || child.getGenotype().isEqualTo(second, first)) {
                    return true;
                }
            }
        }

        return false;
    }

    public Set<VGProbabilityNode> getValidLeafs() {

        Set<VGProbabilityNode> validLeafs = new HashSet<VGProbabilityNode>();

        root.getValidLeafs(validLeafs);

        return validLeafs;

    }

    public VGProbabilityNode getChildAtEndOfPath(List<VGProbabilityNode> vgProbabilityNodes) {

        if(vgProbabilityNodes != null && !vgProbabilityNodes.isEmpty()){
            for(VGProbabilityNode child : root.getChildren()){
                if(child.getGenotype().toString().equals(vgProbabilityNodes.get(0).getGenotype().toString())){
                    return child.getChildAtEndOfPath(vgProbabilityNodes.subList(1, vgProbabilityNodes.size()));
                }
            }
        }

        return null;
    }

    public void getAllPathsForChildren(List<String> paths, List<Float> probabilities, List<Short> individualsCount) {
        root.getAllPathsForChildren("",paths, probabilities,individualsCount);



    }
}
