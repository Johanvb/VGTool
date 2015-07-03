package IO;

import Entities.Jena.Graph.VariantGraph;
import Entities.Jena.Other.VGIndividual;
import Utilities.EnclosingModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.TDBLoader;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import thewebsemantic.binding.Jenabean;

import java.util.Collection;

/**
 * Created by Johan on 03/05/15.
 */
public class StorageInputHandler {


    public static VariantGraph readGraphFromDatabase() {

        String directory = "VCFDataBase/TestDataBase";
        Dataset dataset = TDBFactory.createDataset(directory);
        dataset.begin(ReadWrite.READ);
        String file = "ost";
        try {
            Model model = dataset.getNamedModel("http://nameFile");
            TDBLoader.loadModel(model, file);
        } finally {
            dataset.end();
        }

        return null;
    }


    public static void readGraphFromFile() {

            System.out.println("Loading");
            long first = System.currentTimeMillis();

            Model m = ModelFactory.createDefaultModel();
            RDFDataMgr.read(m, "VCFDataBase/VCF_Files/chr1.ttl", Lang.TURTLE);

            Jenabean b = Jenabean.instance();
            b.bind(m);

            Collection<VariantGraph> vgs = b.reader().loadDeep(VariantGraph.class);
            for (VariantGraph vg : vgs) {
                EnclosingModel.putNewGraphForKey(vg.getId(), vg);
            }

            long second = System.currentTimeMillis();
            System.out.println("Loaded in " + (second - first) + " ms.");

    }

}
