package Entities.Jena.Graph;

import Entities.Jena.JenaObject;
import thewebsemantic.Namespace;

/**
 * Created by Johan on 22/04/15.
 */
@Namespace(JenaObject.ns)
public class VGSequenceGap extends VGSequence {

    public int length;

    public VGSequenceGap(String id) {
        super(id);
    }

    public void init(VGPosition position, short i){
        super.init(position, i);
    }
//    public VGSequenceGap(String id) {
////        super(id);
//    }

    @Override
    public String toString() {

        return "GAP_SIZE:" + length;

    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

}
