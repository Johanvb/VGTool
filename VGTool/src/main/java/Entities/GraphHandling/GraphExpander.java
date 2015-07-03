package Entities.GraphHandling;

import Entities.Jena.Graph.*;

import java.util.*;

/**
 * Created by Johan on 10/05/15.
 */
public class GraphExpander {


    public void expandGraph(VariantGraph graph) {

        removeGapsAndSuccessorsAndPredecessors(graph);
        expandCollapsedSequences(graph);

        graph.hasBeenShrinked = false;

    }

    private void removeGapsAndSuccessorsAndPredecessors(VariantGraph graph) {

        for (Iterator<VGPosition> iterator = graph.positions.iterator(); iterator.hasNext(); ) {

            VGPosition pos = iterator.next();

            for (VGSequence sequence : pos.sequencesSet) {
                sequence.predecessors.clear();
                sequence.successors.clear();
            }

            //Linear place in graph
            if (pos.sequencesSet.size() == 1) {
                VGSequence sequence = pos.sequencesSet.iterator().next();
                if (sequence instanceof VGSequenceGap) {
                    graph.positionsMap.remove(pos.getPosition());
                    iterator.remove();
                }
            }
        }
    }

    private void expandCollapsedSequences(VariantGraph graph) {

        Map<Integer, List<VGSequence>> sequencesToAdd = new HashMap<Integer, List<VGSequence>>();

        for (Iterator<VGPosition> iterator = graph.positions.iterator(); iterator.hasNext(); ) {
            VGPosition pos = iterator.next();

            //Linear place in graph
            if (pos.sequencesSet.size() == 1) {
                VGSequence sequence = pos.sequencesSet.iterator().next();

                int i = 0;
                for (Iterator<VGAlleleList> listIterator = sequence.allelesLists.iterator(); listIterator.hasNext(); ) {
                    VGAlleleList list = listIterator.next();

                    if (i != 0) {
                        int key = pos.getPosition() + i;

                        if (!sequencesToAdd.containsKey(key)) {
                            sequencesToAdd.put(key, new ArrayList<VGSequence>());
                        }


//                        System.out.println("i " + i );
                        //TODO: redo this
                        VGPosition newPos = new VGPosition(null);
                        newPos.init(graph, key);
                        VGSequence newSequence = new VGSequence(null);
                        newSequence.init(newPos , (short)sequencesToAdd.get(key).size());
                        newSequence.allelesLists.add(list);
//                        newSequence.addIndividualsFromSequence(sequence);
                        sequencesToAdd.get(key).add(newSequence);
                        listIterator.remove();
                    }
                    i++;
                }
//                System.out.println(" ");
            }
        }

        for (Integer key : sequencesToAdd.keySet()) {
            graph.createPosition(key);
            List<VGSequence> sequences = sequencesToAdd.get(key);


            for (VGSequence sequence : sequences) {
                graph.addToSequences(key, sequence);
            }
        }

        graph.sortSequences();
    }
}
