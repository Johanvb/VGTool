package Entities.Jena.Deprecated;

/**
 * Created by Johan on 05/06/15.
 */
public class VGSequenceDeprecated {


    //    public void setPhasedSecond(VGIndividualCollection phasedSecond) {
//        this.phasedSecond = phasedSecond;
//    }
//
//    public VGIndividualCollection getPhasedFirst() {
//        return phasedFirst;
//    }
//
//    public VGIndividualCollection getPhasedSecond() {
//        return phasedSecond;
//    }
//
//    public void setPhasedFirst(VGIndividualCollection phasedFirst) {
//        this.phasedFirst = phasedFirst;
//    }
//
//    public VGIndividualCollection getUnphasedFirst() {
//        return unphasedFirst;
//    }
//
//    public VGIndividualCollection getUnphasedSecond() {
//        return unphasedSecond;
//    }
//
//    public void setUnphasedFirst(VGIndividualCollection unphasedFirst) {
//        this.unphasedFirst = unphasedFirst;
//    }
//
//    public void setUnphasedSecond(VGIndividualCollection unphasedSecond) {
//        this.unphasedSecond = unphasedSecond;
//    }


//    public Set<VGIndividual> getIndividuals() {
//        Set<VGIndividual> individuals = new HashSet<VGIndividual>();
//
//        individuals.addAll(unphasedSecond.getIndividuals());
//        individuals.addAll(phasedFirst.getIndividuals());
//        individuals.addAll(phasedSecond.getIndividuals());
//        individuals.addAll(unphasedFirst.getIndividuals());
//
//        return individuals;
//    }
//
//    public Set<VGIndividual> getUnphasedFirstIndividuals() {
//        Set<VGIndividual> individuals = new HashSet<VGIndividual>();
//        individuals.addAll(unphasedFirst.getIndividuals());
//        return individuals;
//    }
//
//    public Set<VGIndividual> getUnphasedSecondIndividuals() {
//        Set<VGIndividual> individuals = new HashSet<VGIndividual>();
//        individuals.addAll(unphasedSecond.getIndividuals());
//        return individuals;
//    }
//
//    public Set<VGIndividual> getPhasedFirstIndividuals() {
//        Set<VGIndividual> individuals = new HashSet<VGIndividual>();
//        individuals.addAll(phasedFirst.getIndividuals());
//        return individuals;
//    }
//
//    public Set<VGIndividual> getPhasedSecondIndividuals() {
//        Set<VGIndividual> individuals = new HashSet<VGIndividual>();
//        individuals.addAll(phasedSecond.getIndividuals());
//        return individuals;
//    }


//    public Set<VGIndividual> getIndividualsSharedWithSuccessor(VGSequence successor) {
//        Set<VGIndividual> intersectionWithSuccessor = new HashSet<VGIndividual>();
//        Set<VGIndividual> successorIndividuals = successor.getIndividuals();
//
//        for (VGIndividual individualInSequence : getIndividuals()) {
//            if (successorIndividuals.contains(individualInSequence)) {
//                intersectionWithSuccessor.add(individualInSequence);
//            }
//        }
//        return intersectionWithSuccessor;
//    }

//    public int getNumberOfIndividuals() {
//        return getIndividuals().size();
//    }

//    public int getNumberOfSharedIndividualsWithSuccessor(VGSequence successor) {
//        return getIndividualsSharedWithSuccessor(successor).size();
//    }

//    public int getNumberOfUnphasedSharedIndividualsWithSet(Set<VGIndividual> set) {
//        return getUnphasedIndividualsSharedWithSet(set).size();
//    }
//
//    public int getNumberOfPhasedFirstSharedIndividualsWithSet(Set<VGIndividual> remainingPotentialIndividuals) {
//        return getPhasedFirstIndividualsSharedWithSet(remainingPotentialIndividuals).size();
//    }
//
//    public int getNumberOfPhasedSecondSharedIndividualsWithSet(Set<VGIndividual> remainingPotentialIndividuals) {
//        return getPhasedSecondIndividualsSharedWithSet(remainingPotentialIndividuals).size();
//    }

//    public Set<VGIndividual> getUnphasedIndividualsSharedWithSet(Set<VGIndividual> set) {
//        Set<VGIndividual> individuals = getUnphasedFirstIndividuals();
//        Set<VGIndividual> intersection = new HashSet<VGIndividual>();
//
//        for (VGIndividual individualInSet : set) {
//            if (individuals.contains(individualInSet)) {
//                intersection.add(individualInSet);
//            }
//        }
//        return intersection;
//    }
//
//    public Set<VGIndividual> getPhasedFirstIndividualsSharedWithSet(Set<VGIndividual> set) {
//        Set<VGIndividual> individuals = getPhasedFirst().getIndividuals();
//        Set<VGIndividual> intersection = new HashSet<VGIndividual>();
//
//        for (VGIndividual individualInSet : set) {
//            if (individuals.contains(individualInSet)) {
//                intersection.add(individualInSet);
//            }
//        }
//        return intersection;
//    }
//
//    public Set<VGIndividual> getPhasedSecondIndividualsSharedWithSet(Set<VGIndividual> set) {
//        Set<VGIndividual> individuals = getPhasedSecond().getIndividuals();
//        Set<VGIndividual> intersection = new HashSet<VGIndividual>();
//
//        for (VGIndividual individualInSet : set) {
//            if (individuals.contains(individualInSet)) {
//                intersection.add(individualInSet);
//            }
//        }
//        return intersection;
//    }


//    public boolean hasEnoughIndividuals(boolean phased, boolean isFirst) {
//        if(phased && isFirst){
//            return getPhasedFirstIndividuals().size() > Constants.individualsThreshold;
//        }else if(phased){
//            return getPhasedSecondIndividuals().size() > Constants.individualsThreshold;
//        }else{
//            return getUnphasedFirstIndividuals().size() > Constants.individualsThreshold;
//        }
//    }

//    private int getPhasedIndividualsCount() {
//        Set<VGIndividual> individuals = getPhasedFirst().getIndividuals();
//        individuals.addAll(getPhasedSecond().getIndividuals());
//        return individuals.size();
//    }


//    public int getTotalNumberForGenotypesForIndividuals(Set<VGIndividual> remainingPotentialIndividuals) {
//        int count = 0;
//
//        Set<VGIndividual> individuals = new HashSet<VGIndividual>();
//
//        individuals.addAll(phasedFirst.compressedValidIndividuals.getIndividualSet());
//        individuals.addAll(phasedSecond.compressedValidIndividuals.getIndividualSet());
//        individuals.addAll(unphasedFirst.compressedValidIndividuals.getIndividualSet());
//        individuals.addAll(unphasedSecond.compressedValidIndividuals.getIndividualSet());
//
//        for (VGIndividual individual : remainingPotentialIndividuals) {
//            if (individuals.contains(individual)) {
//                count++;
//            }
//        }
//
//
//        return count;
//    }

//    public void addIndividualsFromSequence(VGSequence sequence) {
//        unphasedSecond.addIndividuals(sequence.unphasedSecond);
//        phasedFirst.addIndividuals(sequence.phasedFirst);
//        phasedSecond.addIndividuals(sequence.phasedSecond);
//        unphasedFirst.addIndividuals(sequence.unphasedFirst);
//    }

}
