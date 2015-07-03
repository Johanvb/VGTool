package Entities.GraphHandling;

import Entities.Jena.Graph.VGSequence;
import Entities.Jena.Graph.VGSequenceGap;
import Entities.Jena.Graph.VGPosition;
import Entities.Jena.Graph.VariantGraph;
import Entities.Pair;
import Utilities.GeneticsUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Johan on 07/05/15.
 */
public class GraphShrinker {

    private VariantGraph graph;

    public GraphShrinker(VariantGraph graph) {
        this.graph = graph;
    }

    public void buildGaps() {

        int i = 0;

        List<Pair<Integer, VGSequenceGap>> gapIndexesToAdd = new ArrayList<Pair<Integer, VGSequenceGap>>();

        for (VGPosition vgPosition : graph.positions) {
            int sequenceIndex = vgPosition.getPosition();

            //Variation at a position is followed by variation in the next position.
            if (graph.positionsMap.containsKey((sequenceIndex + 1))) {

                Set<VGSequence> sequencesBeforeGap = graph.positionsMap.get(sequenceIndex).sequencesSet;
                Set<VGSequence> sequencesAfterGap = graph.positionsMap.get((sequenceIndex + 1)).sequencesSet;

                GeneticsUtils.crossbindSequences(sequencesBeforeGap, sequencesAfterGap);
            }

            //Linear gaps between variation.
            else if (i + 1 < graph.positions.size()) {
                Integer indexOfNextVariations = graph.positions.get(i + 1).getPosition();

                int gapLength = indexOfNextVariations - (sequenceIndex + 1);

                Set<VGSequence> sequencesBeforeGap = graph.positionsMap.get(sequenceIndex).sequencesSet;
                Set<VGSequence> sequencesAfterGap = graph.positionsMap.get(indexOfNextVariations).sequencesSet;

                //TODO! Important
//                VGSequenceGap gapSequence = new VGSequenceGap(graph.getId() + ":" + (sequenceIndex + 1));
//                gapSequence.length = gapLength;
//                Integer keyIndex = (sequenceIndex + 1);
//                gapIndexesToAdd.add(new Pair<Integer, VGSequenceGap>(keyIndex, gapSequence));
//                AlleleUtils.bindSequencesWithGap(sequencesBeforeGap, gapSequence, sequencesAfterGap);

            }
            i++;
        }

        for (Pair<Integer, VGSequenceGap> gapIndex : gapIndexesToAdd) {
            graph.addToSequences(gapIndex.getKey(), gapIndex.getValue());
        }

        graph.sortSequences();
    }

    public void collapseSequences() {

        for (Iterator<VGPosition> iterator = graph.positions.iterator(); iterator.hasNext(); ) {

            VGPosition vgPosition = iterator.next();
            Set<VGSequence> sequencesForPosition = vgPosition.sequencesSet;

            while (iterator.hasNext()) {
                VGPosition succeedingVgPosition = iterator.next();
                Set<VGSequence> succeedingSequences = succeedingVgPosition.sequencesSet;

                if (sequencesForPosition.size() == 1 && succeedingSequences.size() == 1) {

                    //Gaps shouldn't be merged into sequencesSet. Skip to allele after gap.
                    if (succeedingSequences.iterator().next() instanceof VGSequenceGap) {
                        break;
                    }

                    sequencesForPosition.iterator().next().mergeSequenceIntoSelf(succeedingSequences.iterator().next());
                    graph.positionsMap.remove(succeedingVgPosition.getPosition());
                    iterator.remove();
                } else {
                    break;
                }
            }
        }

        graph.sortSequences();
    }
}
