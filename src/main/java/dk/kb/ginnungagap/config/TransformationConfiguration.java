package dk.kb.ginnungagap.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Configuration for the transformation and of the required fields.
 */
public class TransformationConfiguration {
    /** The directory with the XSLT files.*/
    protected final File xsltDir;
    /** The directory with XSD files.*/
    protected final File xsdDir;
    /** The temporary directory, where the metadata files are stored.*/
    protected final File metadataTempDir;
    /** The catalogs to go through.*/
    protected final List<String> catalogs;
    /** The required fields from Cumulus for making the transformation.*/
    protected final RequiredFields requiredFields;
    
    /**
     * Constructor.
     * @param xsltDir The directory with XSLT files.
     * @param xsdDir The directory with XSD files.
     * @param catalogs The Cumulus catalogs to transform. TODO: is this a good location for this setting?
     * @param metadataTempDir The temporary directory, where the metadata files are stored.
     * @param requiredFields The required fields.
     */
    public TransformationConfiguration(File xsltDir, File xsdDir, File metadataTempDir, Collection<String> catalogs, 
            RequiredFields requiredFields) {
        this.xsdDir = xsdDir;
        this.xsltDir = xsltDir;
        this.catalogs = new ArrayList<String>(catalogs);
        this.metadataTempDir = metadataTempDir;
        this.requiredFields = requiredFields;
    }
    
    /** @return The required fields. */
    public RequiredFields getRequiredFields() {
        return requiredFields;
    }
    /** @return The catalogs. */
    public List<String> getCatalogs() {
        return catalogs;
    }
    /** @return The directory with the XSLT files.*/
    public File getXsltDir() {
        return xsltDir;
    }
    /** @return The directory with XSD files.*/
    public File getXsdDir() {
        return xsdDir;
    }
    /** @return The temporary directory, where the metadata files are stored.*/
    public File getMetadataTempDir() {
        return metadataTempDir;
    }

}
