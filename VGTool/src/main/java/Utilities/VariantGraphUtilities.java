package Utilities;

import Entities.Jena.Graph.VGAlleleList;
import Entities.Jena.Graph.VGPosition;
import Entities.Jena.Graph.VGSequence;
import Entities.Jena.Graph.VariantGraph;

import java.util.HashMap;

/**
 * Created by Johan on 11/05/15.
 */
public class VariantGraphUtilities {

    public static int totalNodesInGraph(VariantGraph graph) {
        int count = 0;


//        findClashingIds();
//        System.out.println("Total positions for " + getId() + " is " + positions.size());

        for (VGPosition pos : graph.positions) {

//            int temp = 0;

            for (VGSequence seq : pos.sequencesSet) {
                for (VGAlleleList list : seq.allelesLists) {
                    count += list.bases.size();
//                    temp += list.alleles.size();
                }
            }

//            System.out.println("Pos " + pos.getPosition() + " sequences sets total size: " + temp + "   count: " + count);

        }

        return count;
    }


    public static void findClashingIds(VariantGraph graph) {

        HashMap<String, Integer> idMap = new HashMap<String, Integer>();

        String id = graph.getId();
        idMap.put(id, idMap.containsValue(id) ? idMap.get(id) + 1 : 1);

        for (VGPosition position : graph.positions) {

            findClashingId(idMap, position);

        }

        for (String key : idMap.keySet()) {
            if (idMap.get(key) > 1) {
                System.out.println("Clash for " + key + " with appearences: " + idMap.get(key));
            }
        }
    }

    public static void findClashingId(HashMap<String, Integer> idMap, VGPosition position) {

        String id = position.getId();
        idMap.put(id, idMap.containsValue(id) ? idMap.get(id) + 1 : 1);

        for (VGSequence sequence : position.sequencesSet) {
            findClashingId(idMap, sequence);
        }

    }

    public static void findClashingId(HashMap<String, Integer> idMap, VGSequence sequence) {

        String id = sequence.getId();
        idMap.put(id, idMap.containsValue(id) ? idMap.get(id) + 1 : 1);

        for (VGAlleleList list : sequence.allelesLists) {
            findClashingId(idMap, list);
        }
    }

    public static void findClashingId(HashMap<String, Integer> idMap, VGAlleleList list) {
        String id = list.getId();
        idMap.put(id, idMap.containsValue(id) ? idMap.get(id) + 1 : 1);
    }

}
