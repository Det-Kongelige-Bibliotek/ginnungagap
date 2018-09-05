package dk.kb.ginnungagap.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.ginnungagap.archive.ArchiveWrapper;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusPreservationUtils;
import dk.kb.ginnungagap.cumulus.CumulusWrapper;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.ginnungagap.utils.WarcUtils;

/**
 * Setting the default start path for the view.
 */
@Controller
public class MetadataController {
    /** The log.*/
    protected final Logger log = LoggerFactory.getLogger(MetadataController.class);
    
    /** The configuration.*/
    @Autowired
    protected Configuration conf;
    /** The wrapped cumulus client.*/
    @Autowired
    protected CumulusWrapper cumulusWrapper;
    /** The metadata transformer.*/
    @Autowired
    protected MetadataTransformationHandler metadataTransformer;
    /** The wrapped archive.*/
    @Autowired
    protected ArchiveWrapper archiveWrapper;
    
    
    /**
     * Index controller, for redirecting towards the workflow site.
     * @return The redirect toward the workflow site.
     */
    @RequestMapping("/metadata")
    public String getGinnungagap(Model model) {
        model.addAttribute("catalogs", conf.getCumulusConf().getCatalogs());
        return "metadata";
    }
    
    /**
     * The method for extracting metadata.
     * @param id The ID of the record to extract metadata for.
     * @param idType The type of ID for the Cumulus record, either UUID or Record Name.
     * @param catalog The catalog for the Cumulus record.
     * @param metadataType The type of metadata to extract, either METS or KBIDS.
     * @param source The source for the metadata, either new created from Cumulus, or extracted from thhe archive.
     * @return The response containing the metadata file.
     */
    @RequestMapping("/metadata/extract")
    public ResponseEntity<Resource> extractMetadata(@RequestParam(value="ID",required=true) String id,
            @RequestParam(value="idType", required=false, defaultValue="UUID") String idType,
            @RequestParam(value="catalog", required=true) String catalog,
            @RequestParam(value="metadataType", required=false, defaultValue="METS") String metadataType,
            @RequestParam(value="source", required=false, defaultValue="cumulus") String source) {        
        try {
            String filename = id + ".xml";
            File metadataFile;

            CumulusRecord record = null;
            if(idType.equalsIgnoreCase("uuid")) {
                record = cumulusWrapper.getServer().findCumulusRecord(catalog, id);
            } else {
                record = cumulusWrapper.getServer().findCumulusRecordByName(catalog, id);            
            }
            
            if(source.equalsIgnoreCase("archive")) {
                metadataFile = getArchivedMetadata(filename, metadataType, record);
            } else {
                metadataFile = getCumulusTransformedMetadata(filename, metadataType, record);
            }
            Resource resource = new UrlResource(metadataFile.toURI());
            
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_XML)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.warn("Failed to retrieve metadata", e);
            throw new IllegalStateException("Failed to extract metadata", e);
        }
    }
    
    /**
     * Retrieves the metadata file from the archive. 
     * @param filename The name for the file containing the metadata.
     * @param metadataType The type of metadata, METS or KBIDS.
     * @param record The Cumulus record with information about which WARC file the metadata is in, and the name 
     * of the WARC record containing the type of metadata.
     * @return The extracted metadata file.
     * @throws Exception If it fails to extract the metadata from the archive.
     */
    protected File getArchivedMetadata(String filename, String metadataType, CumulusRecord record) 
            throws Exception {
        String warcId = record.getFieldValue(Constants.FieldNames.METADATA_PACKAGE_ID);
        String collectionId = record.getFieldValue(Constants.FieldNames.COLLECTION_ID);
        File warcFile = archiveWrapper.getFile(warcId, collectionId);
        
        File outputFile = new File(conf.getBitmagConf().getTempDir(), filename);
        String recordId = null;
        if(metadataType.equalsIgnoreCase("KBIDS")) {
            recordId = record.getFieldValue(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY);
        } else {
            if(!metadataType.equalsIgnoreCase("METS")) {
                log.warn("Undecypherable metadata type '" + metadataType + "'. Deliver METS.");
            }
            recordId = record.getFieldValue(Constants.FieldNames.METADATA_GUID);
        }
        
        WarcUtils.extractRecord(warcFile, recordId, outputFile);
        return outputFile;
    }
    
    /**
     * Extract new metadata based on the Cumulus record.
     * @param filename The name of the file to output the metadata.
     * @param metadataType The type of metadata to create.
     * @param record The Cumulus record containing the metadata to extract.
     * @return The file with the metadata.
     * @throws Exception If it fails to extract or transform the metadata.
     */
    protected File getCumulusTransformedMetadata(String filename, String metadataType, CumulusRecord record) 
            throws Exception {
        File metadataFile = new File(conf.getTransformationConf().getMetadataTempDir(), filename);
        if(metadataType.equalsIgnoreCase("KBIDS")) {
            createKbidsMetadata(record, metadataFile);
        } else {
            if(!metadataType.equalsIgnoreCase("METS")) {
                log.warn("Undecypherable metadata type '" + metadataType + "'. Deliver METS.");
            }
            createMetsMetadata(record, metadataFile);
        }
        
        return metadataFile;
    }
    
    /**
     * Create the KBIDS metadata for the Cumulus record.
     * @param record The Cumulus record.
     * @param metadataFile The file where the metadata should be placed.
     * @throws Exception If it fails to create or transform the metadata, or writing it to the output file. 
     */
    protected void createKbidsMetadata(CumulusRecord record, File metadataFile) throws Exception {
        MetadataTransformer transformer = metadataTransformer.getTransformer(
                MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_INTELLECTUEL_ENTITY);

        String ieUUID = record.getFieldValue(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY);
        String metadataUUID = record.getFieldValue(Constants.FieldNames.METADATA_GUID);
        String fileUUID = record.getUUID();
        try (OutputStream os = new FileOutputStream(metadataFile)) {
            File rawMetadataFile = new File(conf.getTransformationConf().getMetadataTempDir(), metadataUUID + ".raw.xml");
            CumulusPreservationUtils.createIErawFile(ieUUID, metadataUUID, fileUUID, rawMetadataFile);
            try (InputStream cumulusIn = new FileInputStream(rawMetadataFile)) {
                transformer.transformXmlMetadata(cumulusIn, os);
            }
            os.flush();
        }
    }
    
    /**
     * Create the METS metadata for the Cumulus record.
     * @param record The Cumulus record.
     * @param metadataFile The file where the metadata should be placed.
     * @throws Exception If it fails to create or transform the metadata, or writing it to the output file. 
     */
    protected void createMetsMetadata(CumulusRecord record, File metadataFile) 
            throws Exception {
        MetadataTransformer transformer = metadataTransformer.getTransformer(
                MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_METS);

        String metadataUUID = record.getFieldValue(Constants.FieldNames.METADATA_GUID);
        try (OutputStream os = new FileOutputStream(metadataFile)) {
            File cumulusMetadataFile = new File(conf.getTransformationConf().getMetadataTempDir(), metadataUUID + ".raw.xml");
            try (OutputStream cumulusOut = new FileOutputStream(cumulusMetadataFile)) {
                record.writeFieldMetadata(cumulusOut);
            }
            try (InputStream cumulusIn = new FileInputStream(cumulusMetadataFile)) {
                transformer.transformXmlMetadata(cumulusIn, os);
            }
            os.flush();
        }
    }
}
