package Entities.Jena.Other;

import Entities.Jena.JenaObject;
import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.binding.Jenabean;

import java.util.Collection;

/**
 * Created by Johan on 20/04/15.
 */
public class VGIndividual  implements Comparable<VGIndividual> {


    String id;

    public VGIndividual(String id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int compareTo(VGIndividual o) {
        return getId().compareTo(o.getId());
    }
}
