package IO;

import Entities.Jena.Graph.VariantGraph;
import Entities.Jena.Other.VGIndividual;

import Utilities.Constants;
import Utilities.EnclosingModel;
import Utilities.MyBean2RDF;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import thewebsemantic.Bean2RDF;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Johan on 21/04/15.
 */
public class OutputHandler {

    public void writeToFile(VariantGraph vg, String filename) throws IOException {

        long first = System.currentTimeMillis();
        System.out.println("Saving " + vg.getId());

        Model m = ModelFactory.createDefaultModel();
        m.setNsPrefix("vg", Constants.NS);

        MyBean2RDF writer = new MyBean2RDF(m);

        if (filename == null)
            filename = "VCFDataBase/VCF_Files/" + vg.getId() + ".ttl";

        File file = new File(filename);

        file.getParentFile().mkdirs();
        if (!file.exists()) {
            file.createNewFile();
        }

        writer.saveDeep(vg);


        long second = System.currentTimeMillis();
        System.out.println("Done saving " + vg.getId() + " in " + (second - first) + " ms.");


        FileOutputStream fop = new FileOutputStream(file);
        RDFDataMgr.write(fop, m, RDFFormat.TURTLE);


    }


    public void writeToDataBase(VariantGraph vg) {
        long first = System.currentTimeMillis();
        System.out.println("Saving " + vg.getId());
        String directory = "VCFDataBase/TestDataBase";
//        Dataset dataset = TDBFactory.createDataset(directory);
//        dataset.begin(ReadWrite.WRITE);

        OntModel m = ModelFactory.createOntologyModel();

        m.setNsPrefix("vg", Constants.NS);
        Bean2RDF writer = new Bean2RDF(m);

        writer.saveDeep(vg);


        try {
//            dataset.commit();
        } finally {
//            dataset.end();
            long second = System.currentTimeMillis();
            System.out.println("Done saving " + vg.getId() + " in " + (second - first) + " ms.");

        }

    }


}
