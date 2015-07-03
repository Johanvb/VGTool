package Entities.Jena.Graph;

import Entities.Jena.JenaObject;
import Utilities.GeneticsUtils;
import org.apache.commons.lang3.StringUtils;
import thewebsemantic.Id;
import thewebsemantic.Namespace;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johan on 09/05/15.
 */
@Namespace(JenaObject.ns)
public class VGAlleleList extends JenaObject{

    public List<VGBaseValue> bases;

    public VGAlleleList() {
        super("");
    }

    @Id
    public String getId() {
        return StringUtils.join(bases, ",");
    }

    public void setId(String id) {

    }

    public List<VGBaseValue> getBases() {
        return bases;
    }

    public void setBases(List<VGBaseValue> newBases) {
        bases = GeneticsUtils.getBaseList(newBases);
    }

    public void add(VGBaseValue base) {

        List<VGBaseValue> currentBases = getBases() == null ? new ArrayList<VGBaseValue>() : getBases();

        currentBases.add(base);

        setBases(currentBases);
    }

    public String toString() {
        String returnString = "";

        for (VGBaseValue allele : bases) {
            returnString += allele.toString();
        }

        return returnString;
    }
}
