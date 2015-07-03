package Entities.Jena;

import Utilities.Constants;
import thewebsemantic.Id;
import thewebsemantic.Namespace;

/**
 * Created by Johan on 21/04/15.
 */

@Namespace(JenaObject.ns)
public abstract class JenaObject {

    public static final String ns = Constants.NS;

    public String id;

    public JenaObject(String id) {
        setId(id);
    }

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void println(String s) {
        System.out.println(s);
    }

}
