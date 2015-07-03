package Entities.Jena.Deprecated;

import Entities.Jena.Graph.VGAlleleList;
import Entities.Jena.Graph.VGBaseValue;
import Entities.Jena.JenaObject;
import Utilities.GeneticsUtils;
import thewebsemantic.Id;
import thewebsemantic.Namespace;

/**
 * Created by Johan on 20/04/15.
 */
@Namespace(JenaObject.ns)
public class VGAllele{

    private VGAlleleList parentList;
    private short j;
    private VGBaseValue baseValue;

    public VGAllele(VGAlleleList alleleList, short j) {
        this.parentList = alleleList;
        this.j = j;

    }

    @Id
    public String getId() {
        return parentList.getId()+":"+j;
    }

    public void setId(VGAlleleList list, short j) {
        this.parentList = list;
        this.j = j;
    }

    @Override
    public String toString() {
        return baseValue.toString();
    }

    public void setBaseValue(VGBaseValue baseValue) {
        baseValue = GeneticsUtils.getBaseValueFromBaseValue(baseValue);
    }

    public VGBaseValue getBaseValue() {
        return baseValue;
    }
}
