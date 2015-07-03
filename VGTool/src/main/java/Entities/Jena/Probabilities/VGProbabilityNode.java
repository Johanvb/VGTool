package Entities.Jena.Probabilities;

import Entities.Jena.Graph.VGSequence;
import Entities.Jena.JenaObject;
import Utilities.GeneticsUtils;
import Utilities.Constants;
import thewebsemantic.Namespace;

import java.util.*;

/**
 * Created by Johan on 29/04/15.
 */
@Namespace(JenaObject.ns)
public class VGProbabilityNode extends JenaObject{

    private Collection<VGProbabilityNode> children;

    public float probability;
    public short numberOfIndividuals;

    boolean hasSetProbability;

    private VGProbabilityGenotype genotype;

    public VGProbabilityNode(String id) {
        super(id);
        hasSetProbability = false;
        children = new HashSet<VGProbabilityNode>();
    }

    public VGProbabilityGenotype getGenotype() {
        return genotype;
    }

    public void setGenotype(VGProbabilityGenotype genotype) {

        this.genotype = GeneticsUtils.getGenotype(genotype);
//        System.out.println("Genotype " + genotype.toString());

    }


    public void setGenotype(GeneticsUtils.PhasingType type, VGSequence first, VGSequence second) {
        VGProbabilityGenotype g = new VGProbabilityGenotype(first.toString() + (type == GeneticsUtils.PhasingType.PHASED ? ";" : ":" ) + second.toString());

        setGenotype(g);
    }

    public void addChild(VGProbabilityNode vgProbabilityNode) {
        if (children == null) {
            children = new HashSet<VGProbabilityNode>();
        }
        children.add(vgProbabilityNode);
    }

    public boolean isHomozygote() {
        return genotype.getFirst().equals(genotype.getSecond());
    }


    public Collection<VGProbabilityNode> getChildren() {
        return children;
    }

    public void setChildren(Collection<VGProbabilityNode> children) {
        this.children = children;
    }


    public void printChildrenAndProbabilities() {
//        System.out.println();
//        System.out.print(genotype.toString() + " | ");
//
//        for (VGProbabilityNode childEntity : children) {
//            childEntity.printChildrenAndProbabilities();
//        }
    }


    public double getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
        hasSetProbability = true;
    }


    public void setNumberOfIndividuals(short numberOfIndividuals) {
        this.numberOfIndividuals = numberOfIndividuals;
    }

    public void setProbabilityFromChildren() {

        if (children != null && !children.isEmpty() && !children.iterator().next().hasSetProbability) {
            for (VGProbabilityNode child : children) {
                child.setProbabilityFromChildren();
            }
        }

        float probabilityAmongChildren = 0.0f;

        for (VGProbabilityNode child : children) {
            probabilityAmongChildren += child.probability;
        }

        setProbability(probabilityAmongChildren);
    }

    public int getNumberOfIndividuals() {

        if (children == null || children.isEmpty()) {
            return numberOfIndividuals;
        } else {

            int amongChildren = 0;

            for (VGProbabilityNode child : children) {
                amongChildren += child.getNumberOfIndividuals();
            }

            return amongChildren;
        }
    }


    public VGProbabilityNode chooseRandomChild() {
        if (children.size() == 1) {
            return children.iterator().next();
        }

        Random rand = new Random();

        float totalProbabilityInSubTree = 0.0f;

        for (VGProbabilityNode child : children) {
            totalProbabilityInSubTree += child.probability;
        }

        float choice = rand.nextFloat() * totalProbabilityInSubTree;

        float accumulatedProbability = 0.0f;


        for (VGProbabilityNode child : children) {

            accumulatedProbability += child.probability;

            if (choice <= accumulatedProbability) {
                return child;
            }

        }

//        System.out.println("Returning null " + choice + " accu " + accumulatedProbability + " Total " + totalProbabilityInSubTree + " Chilrden: " + children.size());
        return null;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public String asString() {
        return genotype.toString();
    }

    public VGProbabilityNode getPlausibleChild() {

        //TODO change this
        if (children.size() == 1) {
            return children.iterator().next();
        }

        Random rand = new Random();

        float totalProbabilityInSubTree = 0.0f;

        for (VGProbabilityNode child : children) {
            totalProbabilityInSubTree += child.probability;
        }

        float choice = rand.nextFloat() * totalProbabilityInSubTree;

        float accumulatedProbability = 0.0f;

        for (VGProbabilityNode child : children) {
            accumulatedProbability += child.probability;

            if (choice <= accumulatedProbability) {
                return child;
            }
        }

        System.out.println("Returning null " + choice + " accu " + accumulatedProbability + " Total " + totalProbabilityInSubTree + " Chilrden: " + children.size());
        return null;

    }

    public boolean hasCalculatedProbabilitiesForUnphasedChildren(VGSequence first, VGSequence second, GeneticsUtils.PhasingType type) {
        if (type == GeneticsUtils.PhasingType.PHASED)
            return false;

        for (VGProbabilityNode child : getChildren()) {
            if (child.genotype == null) {
                continue;
            }
            if (!child.genotype.phased) {
                if (child.genotype.isEqualTo(first, second) || child.genotype.isEqualTo(second, first)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void getValidLeafs(Set<VGProbabilityNode> validLeafs) {

        if ((children == null || children.isEmpty()) && numberOfIndividuals > Constants.individualsThreshold) {
            validLeafs.add(this);
        } else {
            if(children != null){
                for (VGProbabilityNode child : children) {
                    child.getValidLeafs(validLeafs);
                }
            }
        }
    }

    public VGProbabilityNode getChildAtEndOfPath(List<VGProbabilityNode> vgProbabilityNodes) {
        if (vgProbabilityNodes != null && !vgProbabilityNodes.isEmpty()) {
            for (VGProbabilityNode child : children) {
                if (child.genotype.toString().equals(vgProbabilityNodes.get(0).genotype.toString())) {
                    return child.getChildAtEndOfPath(vgProbabilityNodes.subList(1, vgProbabilityNodes.size()));
                }
            }
        } else {
//            System.out.println("Returning " + this.genotype.toString());
            return this;
        }

        return null;
    }

    public void getAllPathsForChildren(String pathSoFar, List<String> paths, List<Float> probabilities, List<Short> individualsCount) {

        if(genotype == null){
            if(children != null){
                for(VGProbabilityNode child : children){
                    child.getAllPathsForChildren(pathSoFar, paths, probabilities, individualsCount);
                }
            }
        }else{
            String newPathSoFar = pathSoFar + GeneticsUtils.uniqueStringForGenotype(genotype) + "#";

            if ((children == null || children.isEmpty()) && numberOfIndividuals > Constants.individualsThreshold) {
                paths.add(newPathSoFar);
                probabilities.add(probability);
                individualsCount.add(numberOfIndividuals);
                return;
            }
            else{
                if(children != null){
                    for(VGProbabilityNode child : children){
                        child.getAllPathsForChildren(newPathSoFar, paths, probabilities, individualsCount);
                    }
                }

            }
        }
    }

    public VGProbabilityNode getOrCreateGenotypeWithValue(VGProbabilityGenotype uncompressed, String id) {
        if(children == null){
            children = new HashSet<VGProbabilityNode>();
        }

        for(VGProbabilityNode child : children){
            if (child.toString().equals(uncompressed.toString())){
                return child;
            }
        }

        VGProbabilityNode returnValue = new VGProbabilityNode(id);
        returnValue.setGenotype(uncompressed);

        return returnValue;
    }

}
