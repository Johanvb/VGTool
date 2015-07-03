package Entities.Jena.Probabilities;

import Entities.Jena.Graph.VGAlleleList;
import Entities.Jena.Graph.VGSequence;
import Entities.Jena.JenaObject;
import Entities.Pair;
import Utilities.GeneticsUtils;
import thewebsemantic.Namespace;

/**
 * Created by Johan on 25/05/15.
 */
@Namespace(JenaObject.ns)
public class VGProbabilityGenotype extends JenaObject {

    public boolean phased;
    public Pair<String, String> sequences;

    public VGProbabilityGenotype(String id) {
        super(id);
        if(id.contains(";")){
            String[] alleles = id.split(";");
            phased = true;
            sequences = new Pair<String, String>(alleles[0], alleles[1]);
        }else if(id.contains(":")){
            String[] alleles = id.split(":");
            phased = true;
            sequences = new Pair<String, String>(alleles[0], alleles[1]);
        }
    }

    @Override
    public String toString() {
        return sequences.getKey()+ (phased ? "|" : "/") + sequences.getValue();
    }

    public String getFirst() {
        return sequences.getKey();
    }

    public String getSecond() {
        return sequences.getValue();
    }

    public void setValues(GeneticsUtils.PhasingType phasingType, VGSequence first, VGSequence second) {
        this.phased = GeneticsUtils.PhasingType.PHASED == phasingType;

        String firstString = "";

        for(VGAlleleList list : first.allelesLists){
            firstString += list.getId()+".";
        }
        firstString = firstString.substring(0, firstString.length()-1);

        String secondString = "";

        for(VGAlleleList list : second.allelesLists){
            secondString += list.getId()+".";
        }
        secondString = secondString.substring(0, secondString.length()-1);

        sequences = new Pair<String, String>(firstString, secondString);
    }

    public boolean isEqualTo(VGSequence first, VGSequence second) {
        String inputString = first.toString() + "/" + second.toString();
        return inputString.equals(toString());
    }
}
