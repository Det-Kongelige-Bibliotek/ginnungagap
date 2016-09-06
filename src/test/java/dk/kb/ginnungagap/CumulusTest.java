package dk.kb.ginnungagap;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.bitrepository.common.utils.FileUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.canto.cumulus.Catalog;
import com.canto.cumulus.Cumulus;
import com.canto.cumulus.Item;
import com.canto.cumulus.ItemCollection;
import com.canto.cumulus.RecordItemCollection;
import com.canto.cumulus.Server.CatalogInfo;

import dk.kb.ginnungagap.config.CumulusConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.cumulus.FieldExtractor;

public class CumulusTest extends ExtendedTestCase {
    
    File outputDir;
    CumulusServer server;
    
    int MAX_NUMBER_OF_RECORDS = 100;
    
    @BeforeClass
    public void setup() {
        Cumulus.CumulusStart();
        outputDir = new File("tempDir");
        if(outputDir.exists()) {
            FileUtils.delete(outputDir);
        }
        outputDir.mkdir();
        
        CumulusConfiguration conf = new CumulusConfiguration(false, "cumulus-core-test-01.kb.dk", "audio-adm", "");
        server = new CumulusServer(conf);
    }
    
    @AfterClass
    public void stop() {
        Cumulus.CumulusStop();
        
//        FileUtils.delete(outputDir);
    }
    
//    @Test
    public void testRandomStuff() throws Exception {
        assertTrue(server.getServer().isAlive());
        
        Collection<String> collectionNames = server.getServer().getCollectionNames();
        System.err.println("Number of collections: " + collectionNames.size());
        for(String name : collectionNames) {
            System.err.println("Collection: " + name);
        }
        System.err.println();
        
        Collection<Integer> catalogIDs = server.getServer().getCatalogIDs(false, false);
        System.err.println("Number of catalogIDs: " + catalogIDs.size());
        for(Integer i : catalogIDs) {
            CatalogInfo ci = server.getServer().getCatalogInfo(i);
            System.err.println("Catalog: #" + i + ", " + ci.getCatalogName()
                    + ", " + ci.getCatalogLocation()
                    + ", " + ci.getDescription()
                    + ", " + ci.getDisplayName()
                    + ", " + (ci.canMigrate() ? "can Migrate" : "cannot Migrate")
                    + ", " + (ci.isDamaged() ? "is damaged" : "is not damages")
                    + ", " + (ci.isPublishedToInternet() ? "is published to internet" : "is not published to internet")
                    + ", " + (ci.isShared() ? "is shared" : "is not shared"));
        }
        
        ItemCollection ic = server.getServer().getAllVocabulariesItemCollection();
        System.err.println("Number of items: " + ic.getItemTotalCount() + ", " + ic.getItemCount());
        System.err.println("Table name: " + ic.getTableName());
        System.err.println("Layout tablename: " + ic.getLayout().getTableName());
    }
    
    @Test
    public void testPrintingToXml() throws Exception {
        String catalogName = "Audio OM";
        int catalogId = server.getServer().findCatalogID(catalogName);
        Catalog catalog = server.getServer().openCatalog(catalogId);

        // Try with resource, to ensure closing it.
        try (RecordItemCollection ric = catalog.newRecordItemCollection(true);) {
            FieldExtractor fe = new FieldExtractor(ric.getLayout());
            Iterator<Item> iri = ric.iterator();
            Item item;
            int i = 0;
            while(iri.hasNext() && (item = iri.next()) != null && i < MAX_NUMBER_OF_RECORDS) {
                i++;
//                System.err.println("Item: " + item.getID() + ", " + item.getItemIdentifier() +
//                        ", " + item.getCatalogID() + ", " + item.getDisplayString());
//                for(Map.Entry<String, String> entry : m.entrySet()) {
//                    System.err.println(entry.getKey() + " -> " + entry.getValue());
//                }
//                System.err.println();
                
                Map<String, String> m = fe.getFieldsAsStrings(item);
                File outputFile = new File(outputDir, "Test-" + item.getID() + "-" + Math.random() + ".xml");
                printMapAsXml(m, new FileOutputStream(outputFile));
                System.err.println("Printed at: " + outputFile.getAbsolutePath());
            }
            
            assertEquals(outputDir.list().length, i);
        }
    }
    
    public void printMapAsXml(Map<String, String> map, OutputStream os) throws IOException {
        os.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes());
        os.write("\n".getBytes());
        os.write("<record>\n".getBytes());
        
        for(Map.Entry<String, String> entry : map.entrySet()) {
            os.write(("  <field name=\"" + entry.getKey() + "\">\n").getBytes());
            os.write(("    <value>" + entry.getValue() + "</value>\n").getBytes());
            os.write(("  </field>\n").getBytes());
        }
        os.write("</record>\n".getBytes());
        
        os.flush();
    }
}
