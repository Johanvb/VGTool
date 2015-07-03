package Entities.Jena.Graph;

import Entities.Jena.JenaObject;
import com.hp.hpl.jena.Jena;
import thewebsemantic.Id;
import thewebsemantic.Namespace;

import java.util.*;

/**
 * Created by Johan on 20/04/15.
 */
@Namespace(JenaObject.ns)
public class VGSequence extends JenaObject{

    public List<VGAlleleList> allelesLists;

    public Set<VGSequence> predecessors;
    public Set<VGSequence> successors;

    VGPosition position;
    private short i;

    public VGSequence(String id) {
        super(id);

    }
    public void init(VGPosition position, short i){
        this.position = position;
        this.i = i;
        setId("");
        setAllelesLists(new ArrayList<VGAlleleList>());
        setPredecessors(new HashSet<VGSequence>());
        setSuccessors(new HashSet<VGSequence>());
    }

    @Id
    public String getId() {
        return position.getId() + ":" + i;
    }

    public void setId(String id) {

    }

    public void mergeSequenceIntoSelf(VGSequence successorSequence) {
        //Before merge there should ALWAYS only be one sequences in the successor.
        allelesLists.add(successorSequence.allelesLists.get(0));

        successors = successorSequence.successors;

        successorSequence.predecessors = null;
    }


    @Deprecated
    public int getTotalGenotypeCount() {

        int count = 0;
//        unphasedFirst.size();
//        count += phasedFirst.size();
//        count += phasedSecond.size();
//        count += unphasedSecond.size();

        return count;

    }

    public void changeId(VGPosition pos, short i) {
        this.position = pos;
        this.i = i;
    }

    //<editor-fold desc=JenaBean getter/setter>


    public void setAllelesLists(List<VGAlleleList> a) {

        if (allelesLists == null) {
            allelesLists = new ArrayList<VGAlleleList>();
        }

        for (VGAlleleList list : a) {
            allelesLists.add(list);
        }
    }


    public void setPredecessors(Collection<VGSequence> i) {
        if (predecessors == null) {
            predecessors = new HashSet<VGSequence>();
        }

        for (VGSequence allele : i) {
            predecessors.add(allele);
        }
    }

    public void setSuccessors(Collection<VGSequence> i) {
        if (successors == null) {
            successors = new HashSet<VGSequence>();
        }

        for (VGSequence allele : i) {
            successors.add(allele);
        }
    }


    public List<VGAlleleList> getAllelesLists() {
        return allelesLists;
    }

    public Collection<VGSequence> getSuccessors() {
        return successors;
    }

    public Collection<VGSequence> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(Set<VGSequence> predecessors) {
        this.predecessors = predecessors;
    }

    public void setSuccessors(Set<VGSequence> successors) {
        this.successors = successors;
    }


    //</editor-fold>


    //<editor-fold desc=Strings>

    @Deprecated
    public String sequenceAndSuccessors(boolean withProbabilities) {

        String returnString = toString() + " [ ";

        for (VGSequence alleleSequence : successors) {
            returnString += alleleSequence.toString() + " " + alleleSequence.getTotalGenotypeCount() + " ";
        }

        return returnString + "]";
    }


    //Should be based on allele basevalues only.
    @Override
    public String toString() {
        String returnString = "";

        for (VGAlleleList list : allelesLists) {
            for (VGBaseValue base : list.bases) {
                returnString += base.toString();
            }
        }

        return returnString;
    }


    //</editor-fold>


}