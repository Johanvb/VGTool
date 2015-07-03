package Entities.Jena.Other;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Johan on 15/05/15.
 */
public class VGIndividualCollection {

    private static boolean compressedIndividuals = false;

    private VGCompressedIndividualCollection compressedValidIndividuals;
    private VGCompressedIndividualCollection compressedInvalidIndividuals;

    private Set<VGIndividual> validIndividuals;
    private Set<VGIndividual> invalidIndividuals;

    public VGIndividualCollection() {
        if (compressedIndividuals) {
            compressedValidIndividuals = new VGCompressedIndividualCollection();
            compressedInvalidIndividuals = new VGCompressedIndividualCollection();
        } else {
            validIndividuals = new HashSet<VGIndividual>();
            invalidIndividuals = new HashSet<VGIndividual>();
        }
    }

    public void addIndividuals(VGIndividualCollection inputCollection) {

        Set<VGIndividual> valid = compressedValidIndividuals.getIndividualSet();
        valid.addAll(inputCollection.compressedValidIndividuals.getIndividualSet());
        compressedValidIndividuals.setIndividuals(valid);

        Set<VGIndividual> invalid = compressedInvalidIndividuals.getIndividualSet();
        invalid.addAll(inputCollection.compressedInvalidIndividuals.getIndividualSet());
        compressedInvalidIndividuals.setIndividuals(invalid);

    }

    public int size() {
        if(compressedIndividuals){
            return compressedValidIndividuals.size() + compressedInvalidIndividuals.size();
        }else{
            return validIndividuals.size() + invalidIndividuals.size();
        }
    }


    public Set<VGIndividual> getIndividuals() {
        Set<VGIndividual> individuals = new HashSet<VGIndividual>();

        if(compressedIndividuals){
            individuals.addAll(compressedValidIndividuals.getIndividualSet());
            individuals.addAll(compressedInvalidIndividuals.getIndividualSet());
        }else{
            individuals.addAll(validIndividuals);
            individuals.addAll(invalidIndividuals);
        }

        return individuals;
    }


    public void addIndividuals(HashSet<VGIndividual> inputIndividuals, boolean valid) {
        if (compressedIndividuals) {
            if (valid) {
                compressedValidIndividuals.addIndividuals(inputIndividuals);
            } else {
                compressedInvalidIndividuals.addIndividuals(inputIndividuals);
            }
        } else {
            if (valid) {
                validIndividuals.addAll(inputIndividuals);
            } else {
                invalidIndividuals.addAll(inputIndividuals);
            }
        }

    }

//Jena methods. Dont save individuals

//    public Set<VGIndividual> compressedValidIndividuals = new HashSet<VGIndividual>();
//    public Set<VGIndividual> compressedInvalidIndividuals = new HashSet<VGIndividual>();
//
//    public void setValidIndividuals(List<String> list) {
//        for (int j = 0; j < list.size(); j += 2) {
//            EnclosingModel.addIndividualsToSet(compressedValidIndividuals, list.get(j), list.get(j + 1));
//        }
//    }
//
//    public void setInvalidIndividuals(List<String> list) {
//        for (int j = 0; j < list.size(); j += 2) {
//            EnclosingModel.addIndividualsToSet(compressedInvalidIndividuals, list.get(j), list.get(j + 1));
//        }
//    }
//
//    public List<String> getValidIndividuals() {
//        return EnclosingModel.getSortedCompressedIndividualsStringsFromVGIndividuals(compressedValidIndividuals);
//    }
//
//    public List<String> getInvalidIndividuals() {
//        return EnclosingModel.getSortedCompressedIndividualsStringsFromVGIndividuals(compressedInvalidIndividuals);
//    }


//    public boolean contains(VGIndividual individual) {
//        return (compressedValidIndividuals.contains(individual) || compressedInvalidIndividuals.contains(individual));
//    }


}
