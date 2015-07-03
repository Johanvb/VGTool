package Entities.Jena.Deprecated;

/**
 * Created by Johan on 05/06/15.
 */
public class VGPositionDeprecated {


//    public void buildGenotypeSets() {
//
//        //Phased
////        for (VGSequence firstSequence : sequencesSet) {
////            for (VGSequence secondSequence : sequencesSet) {
////
////                Pair<VGSequence, VGSequence> genotype = new Pair<VGSequence, VGSequence>(firstSequence, secondSequence);
////
////                Set<VGIndividual> firstIndividuals = firstSequence.getPhasedFirstIndividuals();
////                Set<VGIndividual> secondIndividuals = secondSequence.getPhasedSecondIndividuals();
////
////                Set<VGIndividual> intersection = new HashSet<VGIndividual>(firstIndividuals);
////                intersection.retainAll(secondIndividuals);
////
////                if (intersection.size() == 0){
////                    continue;
////                }
////
////                individualsForGenotypePhased.put(genotype, intersection);
////            }
////        }
////        //Unphased
////        for (VGSequence firstSequence : sequencesSet) {
////            for (VGSequence secondSequence : sequencesSet) {
////
////                Pair<VGSequence, VGSequence> genotype = new Pair<VGSequence, VGSequence>(firstSequence, secondSequence);
////
////                Set<VGIndividual> firstIndividuals = firstSequence.getUnphasedFirstIndividuals();
////                Set<VGIndividual> secondIndividuals = secondSequence.getUnphasedSecondIndividuals();
////
////                Set<VGIndividual> intersection = new HashSet<VGIndividual>(firstIndividuals);
////                intersection.retainAll(secondIndividuals);
////
////                if (intersection.size() == 0){
////                    continue;
////                }
////
////                Pair<VGSequence, VGSequence> reversePair = new Pair<VGSequence, VGSequence>(secondSequence, firstSequence);
////                if (!(individualsForGenotypeUnphased.containsKey(reversePair) || individualsForGenotypeUnphased.containsKey(genotype))) {
////                    individualsForGenotypeUnphased.put(genotype, intersection);
////                }
////            }
////        }
//    }


    //    public boolean shouldBePhased() {
//
//        Set<VGIndividual> phasedIndividuals = new HashSet<VGIndividual>();
//        Set<VGIndividual> unphasedIndividuals = new HashSet<VGIndividual>();
//
//        for (VGSequence sequence : sequencesSet) {
//
//            phasedIndividuals.addAll(sequence.getPhasedFirstIndividuals());
//            phasedIndividuals.addAll(sequence.getPhasedSecondIndividuals());
//
//            unphasedIndividuals.addAll(sequence.getUnphasedFirstIndividuals());
//
//        }
//
//        int phasedIndividualsCount = phasedIndividuals.size();
//
//        if (phasedIndividualsCount < Constants.individualsThreshold) {
//            return false;
//        }
//
//        int unphasedIndividualsCount = unphasedIndividuals.size();
//
//        if (unphasedIndividualsCount < Constants.individualsThreshold) {
//            return true;
//        }
//
//        int total = phasedIndividualsCount + unphasedIndividualsCount;
//
//        Random rand = new Random();
//        int choice = rand.nextInt(total);
//
//        return choice >= unphasedIndividualsCount;
//    }



    /*
    public boolean isValidGenotype(VGSequence first, VGSequence second, boolean positionShouldBePhased) {
        if (first == null)
            return true;

        Pair<VGSequence, VGSequence> pair = new Pair<VGSequence, VGSequence>(first, second);

        if (positionShouldBePhased) {
            return individualsForGenotypePhased.containsKey(pair) && individualsForGenotypePhased.get(pair).size() > Constants.individualsThreshold;
        } else {
            return individualsForGenotypeUnphased.containsKey(pair) && individualsForGenotypeUnphased.get(pair).size() > Constants.individualsThreshold;
        }
    }

    public boolean hasValidGenotypeForChoice(VGSequence first, boolean positionShouldBePhased) {
        for (VGSequence second : sequencesSet) {
            Pair<VGSequence, VGSequence> pair = new Pair<VGSequence, VGSequence>(first, second);

            if (positionShouldBePhased) {
                if (individualsForGenotypePhased.containsKey(pair) && individualsForGenotypePhased.get(pair).size() > Constants.individualsThreshold) {
                    return true;
                }
            } else {
                if (individualsForGenotypeUnphased.containsKey(pair) && individualsForGenotypeUnphased.get(pair).size() > Constants.individualsThreshold) {
                    return true;
                }
            }
        }

        return false;
    }

      public VGSequence getRandomSequence() {

        if (sequencesSet.size() == 1) {
            return sequencesSet.iterator().next();
        }

        int totalSize = 0;

        for (VGSequence sequence : sequencesSet) {
            totalSize += sequence.getTotalGenotypeCount();
        }

        Random rand = new Random();
        int choice = rand.nextInt(totalSize);

        int accumulated = 0;

        for (VGSequence sequence : sequencesSet) {
            accumulated += sequence.getTotalGenotypeCount();

            if (choice <= accumulated) {
                return sequence;
            }
        }

        System.out.println("Shouldnt get here " + choice + " " + accumulated + " size " + sequencesSet.size());
        return null;
    }


    public VGSequence getPlausibleSequenceFromAlreadyMadeChoice(VGSequence firstChoice, boolean positionShouldBePhased) {

        int total = 0;
        Random rand = new Random();

        ArrayList<Pair<VGSequence, VGSequence>> keys = new ArrayList<Pair<VGSequence, VGSequence>>();

        if (positionShouldBePhased) {

            keys.addAll(individualsForGenotypePhased.keySet());

            for (Pair<VGSequence, VGSequence> pair : keys) {
                if (pair.getKey().equals(firstChoice)) {
                    total += individualsForGenotypePhased.get(pair).size();
                }
            }

            int choice = rand.nextInt(total);

            int accumulated = 0;

            for (Pair<VGSequence, VGSequence> pair : keys) {
                if (pair.getKey().equals(firstChoice)) {
                    accumulated += individualsForGenotypePhased.get(pair).size();
                    if (accumulated >= choice) {
                        return pair.getValue();
                    }
                }
            }
        } else {

            keys.addAll(individualsForGenotypeUnphased.keySet());

            for (Pair<VGSequence, VGSequence> pair : keys) {
                if (pair.getKey().equals(firstChoice)) {
                    total += individualsForGenotypeUnphased.get(pair).size();
                }
            }

            int choice = rand.nextInt(total);

            int accumulated = 0;

            for (Pair<VGSequence, VGSequence> pair : keys) {
                if (pair.getKey().equals(firstChoice)) {

                    accumulated += individualsForGenotypeUnphased.get(pair).size();

                    if (accumulated >= choice) {
                        return pair.getValue();
                    }
                }
            }
        }

        return null;
    }


     */

}
