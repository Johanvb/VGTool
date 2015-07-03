package Entities.Jena.Other;

import Utilities.EnclosingModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Johan on 01/06/15.
 */
public class VGCompressedIndividualCollection {

    List<String> condensedList;

    public VGCompressedIndividualCollection() {
        condensedList = new ArrayList<String>();
    }

    public void setIndividuals(Set<VGIndividual> individualSet){
        condensedList = EnclosingModel.getSortedCompressedIndividualsStringsFromVGIndividuals(individualSet);

//        System.out.print("Condensed:");
//        for(String s : condensedList){
//            System.out.print(" " + s);
//        }
//        System.out.println();

    }

    public void addIndividuals(Set<VGIndividual> individualSet){

        Set<VGIndividual> newSet = getIndividualSet();
        newSet.addAll(individualSet);

        setIndividuals(newSet);

    }

    public Set<VGIndividual> getIndividualSet(){

        Set<VGIndividual> returnIndividuals = new HashSet<VGIndividual>();
        for (int j = 0; j < condensedList.size(); j += 2) {
            EnclosingModel.addIndividualsToSet(returnIndividuals, condensedList.get(j), condensedList.get(j + 1));
        }

        return returnIndividuals;
    }

    public int size(){
        return getIndividualSet().size();
    }

}
