package Entities.Jena.Graph;

import Entities.Jena.JenaObject;
import thewebsemantic.Namespace;

/**
 * Created by Johan on 20/04/15.
 */
@Namespace(JenaObject.ns)
public class VGBaseValue extends JenaObject {


    public VGBaseValue(String id) {
        super(id);
    }

    @Override
    public String toString() {
        return getId();
    }


}
