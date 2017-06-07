package dk.kb.ginnungagap.config;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.ginnungagap.utils.FileUtils;
import dk.kb.yggdrasil.exceptions.YggdrasilException;
import dk.kb.yggdrasil.utils.YamlTools;

/**
 * The configurations in YAML format.
 * It must be in the following format:
 * <ul>
 *   <li>ginnungagap:</li>
 *   <ul>
 *     <li>bitrepository:</li>
 *     <ul>
 *       <li>settings_dir: $settings dir path</li>
 *       <li>keyfile: $keyfile path</li>
 *       <li>max_failing_pillars: $max_failing_pillars</li>
 *       <li>warc_size_limit: $warc_size_limit</li>
 *       <li>temp_dir: $temp_dir</li>
 *       <li>algorithm: $algorithm</li>
 *     </ul>
 *     <li>cumulus:</li>
 *     <ul>
 *       <li>server_url: $server url</li>
 *       <li>username: $username</li>
 *       <li>password: $password</li>
 *       <li>catalogs: <br/>- $catalog 1<br/>- $catalog 2<br/>- ...</li>
 *     </ul>
 *     <li>transformation:</li>
 *     <ul>
 *       <li>xsd_dir: $xsd_dir</li>
 *       <li>xslt_dir: $xslt_dir</li>
 *       <li>required_fields_file: $required_fields_file</li>
 *       <li>metadata_temp_dir: $metadata_temp_dir</li>
 *     </ul>
 *     <li>import:</li>
 *     <ul>
 *       <li>temp_dir: $temp_dir</li>
 *       <li>script_file: $script_file</li>
 *       <li>substitute:<br/> -$sub1_name 
 *       <ul>
 *         <li>from: $from1</li>
 *         <li>to: $to1</li>
 *       </ul>
 *       - $sub2_name
 *       <ul>
 *         <li>from: $from2</li>
 *         <li>to: $to2</li>
 *       </ul>
 *       - ...
 *       </li>
 *     </ul>
 *   </ul>
 * </ul>
 * 
 * This contains specific configurations for each part of the workflow.
 * 
 * Bitmag settings and cumulus settings.
 * 
 * The configuration file must be in the YAML format.
 */
@SuppressWarnings("unchecked")
public class Configuration {

    /** Ginnungagap root element.*/
    protected static final String CONF_GINNUNGAGAP = "ginnungagap";
    
    /** The bitrepository node-element.*/
    protected static final String CONF_BITREPOSITORY = "bitrepository";
    /** The bitrepository settings directory leaf-element.*/
    protected static final String CONF_BITREPOSITORY_SETTINGS_DIR = "settings_dir";
    /** The bitrepository key file leaf-element.*/
    protected static final String CONF_BITREPOSITORY_KEYFILE = "keyfile";
    /** The bitrepository max failing pillars leaf-element.*/
    protected static final String CONF_BITREPOSITORY_MAX_FAILING_PILLARS = "max_failing_pillars";
    /** The bitrepository warc size limit leaf-element.*/
    protected static final String CONF_BITREPOSITORY_WARC_SIZE_LIMIT = "warc_size_limit";
    /** The bitrepository temporary directory leaf-element.*/
    protected static final String CONF_BITREPOSITORY_TEMP_DIR = "temp_dir";
    /** The bitrepository algorithm leaf-element.*/
    protected static final String CONF_BITREPOSITORY_ALGORITHM = "algorithm";
    
    /** Cumulus node-element.*/
    protected static final String CONF_CUMULUS = "cumulus";
    /** The cumulus server url leaf-element.*/
    protected static final String CONF_CUMULUS_SERVER = "server_url";
    /** The cumulus server username leaf-element.*/
    protected static final String CONF_CUMULUS_USERNAME = "username";
    /** The cumulus server password leaf-element.*/
    protected static final String CONF_CUMULUS_PASSWORD = "password";
    /** The cumulus catalogs array leaf-element.*/
    protected static final String CONF_CUMULUS_CATALOGS = "catalogs";
    
    /** Transformation node-element.*/
    protected static final String CONF_TRANSFORMATION = "transformation";
    /** Transformation XSD directory leaf-element.*/
    protected static final String CONF_TRANSFORMATION_XSD_DIR = "xsd_dir";
    /** Transformation XSLT directory leaf-element.*/
    protected static final String CONF_TRANSFORMATION_XSLT_DIR = "xslt_dir";
    /** Transformation required fields file leaf-element.*/
    protected static final String CONF_TRANSFORMATION_REQUIRED_FIELDS_FILE = "required_fields_file";
    /** Transformation metadata temp file leaf-element.*/
    protected static final String CONF_TRANSFORMATION_METADATA_TEMP_FILE= "metadata_temp_dir";

    /** Importation node-element.*/
    protected static final String CONF_IMPORT = "import";
    /** Conversion temp-dir leaf-element.*/
    protected static final String CONF_IMPORT_TEMP_DIR = "temp_dir";
    /** Conversion script file leaf-element.*/
    protected static final String CONF_IMPORT_SCRIPT_FILE = "script_file";
    /** Conversion script file leaf-element.*/
    protected static final String CONF_IMPORT_SUBSTITUTE = "substitute";
    
    /** Whether Cumulus should have write access. */
    protected static final boolean CUMULUS_WRITE_ACCESS = true;
    
    /** The configuration for the bitrepository.*/
    protected final BitmagConfiguration bitmagConf;
    /** The configruation for accessing Cumulus.*/
    protected final CumulusConfiguration cumulusConf;
    /** The configuration for the transformation.*/
    protected final TransformationConfiguration transformationConf;
    
    /** The configuration for the conversion. May be null, if no conversion is needed.*/
    protected ImportationConfiguration importConfiguration = null;
    
    /**
     * Constructor.
     * @param confFile The file with the Ginnungagap configuration, in the described format.
     */
    @SuppressWarnings("rawtypes")
    public Configuration(File confFile) {
        ArgumentCheck.checkExistsNormalFile(confFile, "File confFile");
        try {
            LinkedHashMap<String, LinkedHashMap> map = YamlTools.loadYamlSettings(confFile);
            
            ArgumentCheck.checkTrue(map.containsKey(CONF_GINNUNGAGAP), 
                    "Configuration must contain the '" + CONF_GINNUNGAGAP + "' root element.");
            Map<String, Object> confMap = (Map<String, Object>) map.get(CONF_GINNUNGAGAP);

            ArgumentCheck.checkTrue(confMap.containsKey(CONF_CUMULUS), 
                    "Configuration must contain the '" + CONF_CUMULUS + "' element.");
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_BITREPOSITORY), 
                    "Configuration must contain the '" + CONF_BITREPOSITORY+ "' element.");
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_TRANSFORMATION), 
                    "Configuration must contain the '" + CONF_TRANSFORMATION + "' element.");
            
            this.bitmagConf = loadBitmagConf((Map<String, Object>) confMap.get(CONF_BITREPOSITORY));
            this.cumulusConf = loadCumulusConfiguration((Map<String, Object>) confMap.get(CONF_CUMULUS));
            this.transformationConf = loadTransformationConfiguration(
                    (Map<String, Object>) confMap.get(CONF_TRANSFORMATION));
            
            if(confMap.containsKey(CONF_IMPORT)) {
                this.importConfiguration = loadConversionConfiguration(
                        (Map<String, Object>) confMap.get(CONF_IMPORT));
            }
        } catch (Exception e) {
            throw new ArgumentCheck("Issue loading the configurations from file '" + confFile.getAbsolutePath() + "'",
                    e);
            
        }
    }
    
    /**
     * Loads the Bitmag configuration for the bitrepository.
     * The elements are taken from the 'bitrepository' element in the configuration.
     * @param map The map of the Bitrepository configuration.
     * @return The configuration for the bitrepository.
     */
    protected BitmagConfiguration loadBitmagConf(Map<String, Object> map) {
        ArgumentCheck.checkTrue(map.containsKey(CONF_BITREPOSITORY_SETTINGS_DIR), 
                "Missing Bitrepository element '" + CONF_BITREPOSITORY_SETTINGS_DIR + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_BITREPOSITORY_KEYFILE), 
                "Missing Bitrepository element '" + CONF_BITREPOSITORY_KEYFILE + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_BITREPOSITORY_MAX_FAILING_PILLARS), 
                "Missing Bitrepository element '" + CONF_BITREPOSITORY_MAX_FAILING_PILLARS + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_BITREPOSITORY_WARC_SIZE_LIMIT), 
                "Missing Bitrepository element '" + CONF_BITREPOSITORY_WARC_SIZE_LIMIT + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_BITREPOSITORY_TEMP_DIR), 
                "Missing Bitrepository element '" + CONF_BITREPOSITORY_TEMP_DIR + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_BITREPOSITORY_ALGORITHM), 
                "Missing Bitrepository element '" + CONF_BITREPOSITORY_ALGORITHM + "'");
        
        File settingsDir = new File((String) map.get(CONF_BITREPOSITORY_SETTINGS_DIR));
        ArgumentCheck.checkExistsDirectory(settingsDir, "Directory " + settingsDir.getAbsolutePath());
        
        File keyFile = null;
        String keyfilePath = (String) map.get(CONF_BITREPOSITORY_KEYFILE);
        if(!keyfilePath.isEmpty()) {
            keyFile = new File(keyfilePath);
        }
        
        int maxFailingPillars = (int) map.get(CONF_BITREPOSITORY_MAX_FAILING_PILLARS);
        int warcSizeLimit = (int) map.get(CONF_BITREPOSITORY_WARC_SIZE_LIMIT);
        
        String tempDirPath = (String) map.get(CONF_BITREPOSITORY_TEMP_DIR);
        File tempDir = FileUtils.getDirectory(tempDirPath);
        String algorithm = (String) map.get(CONF_BITREPOSITORY_ALGORITHM);
        try {
            MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new ArgumentCheck("The algorithm '" + algorithm + "' is not supported.", e);
        }
        
        return new BitmagConfiguration(settingsDir, keyFile, maxFailingPillars, warcSizeLimit, tempDir, algorithm);
    }
    
    /**
     * Loads the Cumulus configuration from the 'cumulus' element in the configuration.
     * @param map The map with the Cumulus configuration.
     * @return The configuration for the Cumulus server.
     */
    protected CumulusConfiguration loadCumulusConfiguration(Map<String, Object> map) {
        ArgumentCheck.checkTrue(map.containsKey(CONF_CUMULUS_SERVER), 
                "Missing Cumulus element '" + CONF_CUMULUS_SERVER + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_CUMULUS_USERNAME), 
                "Missing Cumulus element '" + CONF_CUMULUS_USERNAME + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_CUMULUS_PASSWORD), 
                "Missing Cumulus element '" + CONF_CUMULUS_PASSWORD + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_CUMULUS_CATALOGS), 
                "Missing Cumulus element '" + CONF_CUMULUS_CATALOGS + "'");
        
        List<String> catalogs = (List<String>) map.get(CONF_CUMULUS_CATALOGS);
        
        return new CumulusConfiguration(CUMULUS_WRITE_ACCESS, (String) map.get(CONF_CUMULUS_SERVER), 
                (String) map.get(CONF_CUMULUS_USERNAME), (String) map.get(CONF_CUMULUS_PASSWORD), catalogs);
    }
    
    /**
     * Loads the Transformation configuration from the 'transformation' element in the configuration.
     * @param map The map with the transformation configuration.
     * @return The configuration for performing the transformation.
     * @throws YggdrasilException If loading the required fields file fails.
     */
    protected TransformationConfiguration loadTransformationConfiguration(Map<String, Object> map) 
            throws YggdrasilException {
        ArgumentCheck.checkTrue(map.containsKey(CONF_TRANSFORMATION_XSD_DIR), 
                "Missing Transformation element '" + CONF_TRANSFORMATION_XSD_DIR + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_TRANSFORMATION_XSLT_DIR), 
                "Missing Transformation element '" + CONF_TRANSFORMATION_XSLT_DIR + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_TRANSFORMATION_METADATA_TEMP_FILE), 
                "Missing Transformation element '" + CONF_TRANSFORMATION_METADATA_TEMP_FILE + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_TRANSFORMATION_REQUIRED_FIELDS_FILE), 
                "Missing Transformation element '" + CONF_TRANSFORMATION_REQUIRED_FIELDS_FILE + "'");
        
        File xsdDir = new File((String) map.get(CONF_TRANSFORMATION_XSD_DIR));
        File xsltDir = new File((String) map.get(CONF_TRANSFORMATION_XSLT_DIR));
        File metadataTempDir = FileUtils.getDirectory((String) map.get(CONF_TRANSFORMATION_METADATA_TEMP_FILE));
        File requiredFieldsFile = new File((String) map.get(CONF_TRANSFORMATION_REQUIRED_FIELDS_FILE));
        
        ArgumentCheck.checkExistsDirectory(xsdDir, "XSD dir");
        ArgumentCheck.checkExistsDirectory(xsltDir, "XSLT dir");
        ArgumentCheck.checkExistsDirectory(metadataTempDir, "Metadata temporary dir");
        ArgumentCheck.checkExistsNormalFile(requiredFieldsFile, "RequireFieldsFile");
        
        RequiredFields requiredFields = RequiredFields.loadRequiredFieldsFile(requiredFieldsFile);
        
        return new TransformationConfiguration(xsltDir, xsdDir, metadataTempDir, requiredFields);
    }
    
    /**
     * Loads the Conversion configuration from the 'conversion' element in the configuration.
     * @param map The map with the Conversion configuration.
     * @return The configuration for the conversion.
     */
    protected ImportationConfiguration loadConversionConfiguration(Map<String, Object> map) {
        ArgumentCheck.checkTrue(map.containsKey(CONF_IMPORT_TEMP_DIR), 
                "Missing Conversion element '" + CONF_IMPORT_TEMP_DIR + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_IMPORT_SCRIPT_FILE), 
                "Missing Conversion element '" + CONF_IMPORT_SCRIPT_FILE + "'");

        File tempDir = FileUtils.getDirectory((String) map.get(CONF_IMPORT_TEMP_DIR));
        File scriptFile = new File((String) map.get(CONF_IMPORT_SCRIPT_FILE));
        List<Map<String, String>> subs = (List<Map<String, String>>) map.get(CONF_IMPORT_SUBSTITUTE);
        
        ArgumentCheck.checkExistsDirectory(tempDir, "temp dir");
        ArgumentCheck.checkExistsNormalFile(scriptFile, "script");

        return new ImportationConfiguration(tempDir, scriptFile, subs);
    }
    
    /** @return The configuration for the bitrepository.*/
    public BitmagConfiguration getBitmagConf() {
        return bitmagConf;
    }
    
    /** @return The configruation for accessing Cumulus.*/
    public CumulusConfiguration getCumulusConf() {
        return cumulusConf;
    }
    
    /** @return The configuration for the transformation.*/
    public TransformationConfiguration getTransformationConf() {
        return transformationConf;
    }
    
    /** @return The configuration for the importation. May be null, if no importation is needed.*/
    public ImportationConfiguration getImportationConfiguration() {
        return importConfiguration;
    }
}
