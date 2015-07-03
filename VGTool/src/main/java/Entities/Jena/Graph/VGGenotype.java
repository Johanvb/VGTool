package Entities.Jena.Graph;

/**
 * Created by Johan on 02/06/15.
 */
public class VGGenotype {

    String first;

    String second;

    boolean phased;

    public VGGenotype(String first, String second, boolean phased) {
        this.first = first;
        this.second = second;
        this.phased = phased;
    }
}
