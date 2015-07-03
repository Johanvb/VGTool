package Entities.Jena.Graph;

import Entities.Jena.JenaObject;
import Entities.Jena.Other.VGIndividual;
import Entities.Jena.Other.VGIndividualCollection;
import Entities.Jena.Probabilities.VGCompressedProbabilityTree;
import Entities.Jena.Probabilities.VGProbabilityNode;
import Entities.Jena.Probabilities.VGProbabilityTree;
import Entities.Pair;

import Utilities.GeneticsUtils;
import thewebsemantic.Id;
import thewebsemantic.Namespace;

import java.util.*;

/**
 * Created by Johan on 02/05/15.
 */
@Namespace(JenaObject.ns)
public class VGPosition extends JenaObject implements Comparable<VGPosition> {

    private VariantGraph graph;
    private int position;

    public Set<VGSequence> sequencesSet;

    private Map<Pair<VGSequence, VGSequence>, VGIndividualCollection> individualsForGenotypeUnphased;
    private Map<Pair<VGSequence, VGSequence>, VGIndividualCollection> individualsForGenotypePhased;

    private VGProbabilityTree probabilityTree;
    private VGCompressedProbabilityTree compressedTree;

    public VGPosition(String id) {
        super(id);
    }

    public void init(VariantGraph g, int position){
        this.graph = g;
        this.position = position;

        setSequences(new HashSet<VGSequence>());

        individualsForGenotypeUnphased = new HashMap<Pair<VGSequence, VGSequence>, VGIndividualCollection>();
        individualsForGenotypePhased = new HashMap<Pair<VGSequence, VGSequence>, VGIndividualCollection>();
    }

    @Id
    public String getId() {
        return graph.getId() + ":" + position;
    }

    public void setId(String id) {

    }

    public int totalIndividualsAtPosition() {

        int total = 0;

        HashSet<VGIndividual> set = new HashSet<VGIndividual>();

        for (Pair<VGSequence, VGSequence> pair : individualsForGenotypePhased.keySet()) {
            set.addAll(individualsForGenotypePhased.get(pair).getIndividuals());
        }

        for (Pair<VGSequence, VGSequence> pair : individualsForGenotypeUnphased.keySet()) {
            set.addAll(individualsForGenotypeUnphased.get(pair).getIndividuals());
        }

        return set.size();

    }

    public VGCompressedProbabilityTree getCompressedTree() {
        return compressedTree;
    }

    public void setCompressedTree(VGCompressedProbabilityTree compressedTree) {
        this.compressedTree = compressedTree;
    }

    public int compareTo(VGPosition o) {
        return position - o.position;
    }

    public void resetProbabilityTrees() {
        probabilityTree = null;
        compressedTree = null;
    }


    public Collection<VGSequence> getSequences() {
        return sequencesSet;
    }

    public void setSequences(Collection<VGSequence> sequences) {
        if (sequencesSet == null) {
            sequencesSet = new HashSet<VGSequence>();
        }

        for (VGSequence seq : sequences) {
            sequencesSet.add(seq);
        }
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public VGProbabilityTree getProbabilityTree() {
        if(probabilityTree != null){
            return probabilityTree;
        }else if(compressedTree != null){
            return compressedTree.unCompress();
        }

        return null;
    }

    public void setProbabilityTree(VGProbabilityTree probabilityTree) {
        this.probabilityTree = probabilityTree;
    }


    public VGIndividualCollection getPhasedIndividualsForGenotype(VGSequence first, VGSequence second) {
        return individualsForGenotypePhased.get(new Pair<VGSequence, VGSequence>(first, second));

    }

    public VGIndividualCollection getUnphasedIndividualsForGenotype(VGSequence first, VGSequence second) {
        if (individualsForGenotypeUnphased.get(new Pair<VGSequence, VGSequence>(first, second)) != null) {
            return individualsForGenotypeUnphased.get(new Pair<VGSequence, VGSequence>(first, second));
        } else {
            return individualsForGenotypeUnphased.get(new Pair<VGSequence, VGSequence>(second, first));
        }
    }

    public Set<VGIndividual> getIndividualsForGenotype(VGSequence first, VGSequence second, GeneticsUtils.PhasingType type) {
        VGIndividualCollection returnCollectionValue;
        if (GeneticsUtils.PhasingType.PHASED == type) {
            returnCollectionValue = getPhasedIndividualsForGenotype(first, second);
        } else {
            returnCollectionValue = getUnphasedIndividualsForGenotype(first, second);
        }

        return returnCollectionValue == null ? null : returnCollectionValue.getIndividuals();
    }

    public VGProbabilityNode getPlausibleChoice() {
        return getProbabilityTree().root.chooseRandomChild();
    }

    public void addIndividualSets(HashMap<Pair<String, String>, HashSet<VGIndividual>> unphased, HashMap<Pair<String, String>,
            HashSet<VGIndividual>> phased, HashMap<Pair<String, String>, HashSet<VGIndividual>> invalidPhased, HashMap<Pair<String, String>, HashSet<VGIndividual>> invalidUnphased) {

        for (VGSequence first : sequencesSet) {

            for (VGSequence second : sequencesSet) {

                Pair<String, String> pair = new Pair<String, String>(first.toString(), second.toString());
                if (unphased.containsKey(pair)) {
                    addInvidualsFromSet(first, second, individualsForGenotypeUnphased, unphased.get(pair), true, false);
                }
                if (invalidUnphased.containsKey(pair)) {
                    addInvidualsFromSet(first, second, individualsForGenotypeUnphased, invalidUnphased.get(pair), false, false);
                }
                if (phased.containsKey(pair)) {
                    addInvidualsFromSet(first, second, individualsForGenotypePhased, phased.get(pair), true, true);
                }
                if (invalidPhased.containsKey(pair)) {
                    addInvidualsFromSet(first, second, individualsForGenotypePhased, invalidPhased.get(pair), false, true);
                }
            }
        }
    }

    private void addInvidualsFromSet(VGSequence first, VGSequence second, Map<Pair<VGSequence, VGSequence>, VGIndividualCollection> individualsCollection, HashSet<VGIndividual> inputIndividuals, boolean valid, boolean phased) {
        //AlleleUtils.getGenotypePair(first, second, phased);
        Pair<VGSequence, VGSequence> pairKey = new Pair<VGSequence, VGSequence>(first, second);
        if (!individualsCollection.containsKey(pairKey)) {
            individualsCollection.put(pairKey, new VGIndividualCollection());
        }

        individualsCollection.get(pairKey).addIndividuals(inputIndividuals, valid);

    }


    public int genoTypeCountForSequence(VGSequence alleleSequence) {
        int count = 0;

        for (Pair<VGSequence, VGSequence> pair : individualsForGenotypeUnphased.keySet()) {
            if (pair.getKey().equals(alleleSequence) || pair.getValue().equals(alleleSequence)) {
                if (pair.getKey().equals(pair.getValue())) {
                    count += individualsForGenotypeUnphased.get(pair).size();
                }
                count += individualsForGenotypeUnphased.get(pair).size();
            }
        }

        for (Pair<VGSequence, VGSequence> pair : individualsForGenotypePhased.keySet()) {
            if (pair.getKey().equals(alleleSequence) || pair.getValue().equals(alleleSequence)) {
                if (pair.getKey().equals(pair.getValue())) {
                    count += individualsForGenotypePhased.get(pair).size();
                }
                count += individualsForGenotypePhased.get(pair).size();
            }
        }

        return count;
    }


}
