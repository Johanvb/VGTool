package IO;

import Entities.Pair;
import htsjdk.tribble.AbstractFeatureReader;
import htsjdk.tribble.FeatureReader;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFHeader;

/**
 * Created by Johan on 20/04/15.
 */
public class VCFInputHandler {

    public static Pair<VCFHeader, FeatureReader<VariantContext>> readInputVCF(String fileName) throws Exception {

        final VCFCodec vcfCodec = new VCFCodec();

        /** we don't need some indexed VCFs */
        boolean requireIndex = false;

        FeatureReader<VariantContext> reader = AbstractFeatureReader.getFeatureReader(
                fileName, vcfCodec, requireIndex);

        VCFHeader header = (VCFHeader) reader.getHeader();

        return new Pair<VCFHeader, FeatureReader<VariantContext>>(header, reader);
    }
}
