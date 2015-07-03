package Entities.Jena.Graph;

import Entities.Jena.JenaObject;
import thewebsemantic.Namespace;

import java.util.*;

/**
 * Created by Johan on 15/04/15.
 */

@Namespace(JenaObject.ns)
public class VariantGraph extends JenaObject {

    private VGChromosome chromosome;

    public ArrayList<VGPosition> positions;
    public Map<Integer, VGPosition> positionsMap;

    //Indicates if the graph has been shrinked and filled with gaps
    public boolean hasBeenShrinked;
    public boolean hasProbabilities;

    public VariantGraph(String id) {
        super(id);
        setPositions(new ArrayList<VGPosition>());
        hasBeenShrinked = false;
        hasProbabilities = false;
    }


    public VGPosition createPosition(Integer key){
        if (!positionsMap.containsKey(key)) {
            VGPosition newVGPosition = new VGPosition(null);
            newVGPosition.init(this,key);
            positionsMap.put(key, newVGPosition);
            positions.add(newVGPosition);
        }
        return positionsMap.get(key);
    }

    public void addToSequences(Integer key, VGSequence sequence) {

        boolean alreadyContainsSequence = false;

        for (VGSequence alleleSequence : positionsMap.get(key).sequencesSet) {
            if (alleleSequence.toString().equals(sequence.toString())) {
                alreadyContainsSequence = true;
            }
        }

        if (!alreadyContainsSequence) {
            positionsMap.get(key).sequencesSet.add(sequence);
        }
        else {
//            for (VGSequence alreadyStoredSequence : positionsMap.get(key).sequencesSet) {
//
//                if (alreadyStoredSequence.toString().equals(sequence.toString())) {
//
//                    alreadyStoredSequence.unphasedFirst.addIndividuals(sequence.unphasedFirst);
//                    alreadyStoredSequence.unphasedSecond.addIndividuals(sequence.unphasedSecond);
//                    alreadyStoredSequence.phasedFirst.addIndividuals(sequence.phasedFirst);
//                    alreadyStoredSequence.phasedSecond.addIndividuals(sequence.phasedSecond);
//                }
//            }
        }
    }


    public void insertPositionIntoGraph(VGPosition position) {

        for (VGSequence sequence : position.sequencesSet) {
            //Important to give it a new unique id when merging so that we dont have a clash of IDs.
            short identifier = positionsMap.containsKey(position.getPosition()) ? (short) positionsMap.get(position.getPosition()).sequencesSet.size() : 0;
            sequence.changeId(position, identifier);

            addToSequences(position.getPosition(), sequence);
        }


    }

    public void removeProbabilitiesForPosition(VGPosition position) {
        position.resetProbabilityTrees();
    }

    public void sortSequences() {
        Collections.sort(positions);
    }


    //<editor-fold desc=Jena methods>

    public VGChromosome getChromosome() {
        return chromosome;
    }

    public void setChromosome(VGChromosome chromosome) {
        this.chromosome = chromosome;
    }

    public List<VGPosition> getPositions() {
        return positions;
    }

    public void setPositions(List<VGPosition> positions) {
        if (this.positions == null) {
            this.positions = new ArrayList<VGPosition>();
        }
        if (positionsMap == null) {
            positionsMap = new HashMap<Integer, VGPosition>();
        }

        for (VGPosition pos : positions) {
            this.positions.add(pos);
            positionsMap.put(pos.getPosition(), pos);
        }

        sortSequences();
    }

    public boolean isHasProbabilities() {
        return hasProbabilities;
    }

    public void setHasProbabilities(boolean hasProbabilities) {
        this.hasProbabilities = hasProbabilities;
    }

    public boolean isHasBeenShrinked() {
        return hasBeenShrinked;
    }

    public void setHasBeenShrinked(boolean hasBeenShrinked) {
        this.hasBeenShrinked = hasBeenShrinked;
    }


    //</editor-fold>

}
