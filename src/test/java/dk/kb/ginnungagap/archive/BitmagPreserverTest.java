package dk.kb.ginnungagap.archive;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.bitrepository.common.utils.FileUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.config.TestBitmagConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.testutils.TestFileUtils;

public class BitmagPreserverTest extends ExtendedTestCase {

    TestBitmagConfiguration bitmagConf;
    
    File metadataFile;
    File resourceFile;
    
    String collectionId = "Test-collection-id";
    String recordId = "TEST-RECORD-ID";
    
    @BeforeClass
    public void setup() throws Exception {
        TestFileUtils.setup();
        
        File origMetadataFile = new File("src/test/resources/test-mets.xml");
        metadataFile = new File(TestFileUtils.getTempDir(), origMetadataFile.getName());
        FileUtils.copyFile(origMetadataFile, metadataFile);
        
        File origResourceFile = new File("src/test/resources/test-resource.txt");
        resourceFile = new File(TestFileUtils.getTempDir(), origResourceFile.getName());
        FileUtils.copyFile(origResourceFile, resourceFile);
    }
    
    @BeforeMethod
    public void setupMethod() throws Exception {
        bitmagConf = new TestBitmagConfiguration(TestFileUtils.getTempDir(), null, 1, 1000000, TestFileUtils.getTempDir(), "SHA-1");        
    }
    
    @AfterClass
    public void tearDown() throws Exception {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testInitialConditions() throws Exception {
        addDescription("Test the initial conditions of the bitmag preserver.");
        BitmagArchive archive = mock(BitmagArchive.class);
        BitmagPreserver preserver = new BitmagPreserver(archive, bitmagConf);
        assertTrue(preserver.warcPackerForCollection.isEmpty());
        preserver.checkConditions();
    }
    
//    @Test
//    public void testPreservingRecord() throws Exception {
//        addDescription("Test preserving a record, which is too small for automatic upload");
//        BitmagArchive archive = mock(BitmagArchive.class);
//        
//        CumulusRecord record = mock(CumulusRecord.class);
//        when(record.getFieldValue(anyString())).thenReturn(collectionId);
//        when(record.getFile()).thenReturn(resourceFile);
//        when(record.getUUID()).thenReturn(recordId);
//
//        BitmagPreserver preserver = new BitmagPreserver(archive, bitmagConf);
//
//        addStep("Pack the record and its metadata", 
//                "Should be placed in the WARC file, but the warc file should not be uploaded yet (not large enough)");
//        preserver.packRecordAssetFile(record, metadataFile);
//        
//        assertFalse(preserver.warcPackerForCollection.isEmpty());
//        assertTrue(preserver.warcPackerForCollection.containsKey(collectionId));
//
//        File warcFile = preserver.warcPackerForCollection.get(collectionId).getWarcFile();
//        
//        assertTrue(warcFile.exists());
//        assertEquals(bitmagConf.getTempDir().getAbsolutePath(), warcFile.getParentFile().getAbsolutePath());
//        
//        verify(record, times(1)).getFile();
//        verify(record, times(1)).getFieldValue(anyString());
//        verify(record, times(1)).getUUID();
//        verifyNoMoreInteractions(record);
//        
//        verifyZeroInteractions(archive);
//        
//        addStep("Upload all warc files", 
//                "The archive should receive the warc-file.");
//        preserver.uploadAll();
//        verify(archive).uploadFile(eq(warcFile), eq(collectionId));
//        verifyNoMoreInteractions(archive);
//    }
//    
//    @Test
//    public void testPreservingRecordWithUpload() throws Exception {
//        addDescription("Test preserving a record which is large enough for automatic upload.");
//        BitmagArchive archive = mock(BitmagArchive.class);
//        when(archive.uploadFile(any(File.class), anyString())).thenReturn(true);
//        
//        CumulusRecord record = mock(CumulusRecord.class);
//        when(record.getFieldValue(anyString())).thenReturn(collectionId);
//        when(record.getFile()).thenReturn(resourceFile);
//        when(record.getUUID()).thenReturn(recordId);
//        
//        bitmagConf.setWarcFileSizeLimit(40000);
//
//        BitmagPreserver preserver = new BitmagPreserver(archive, bitmagConf);
//
//        addStep("Pack the record and the default metadata", 
//                "Should be placed in the WARC file, but the warc file should not be uploaded yet (not large enough)");
//        preserver.packRecordAssetFile(record, metadataFile);
//        
//        assertFalse(preserver.warcPackerForCollection.isEmpty());
//        assertTrue(preserver.warcPackerForCollection.containsKey(collectionId));
//
//        File warcFile = preserver.warcPackerForCollection.get(collectionId).getWarcFile();
//        
//        assertTrue(warcFile.exists());
//        assertEquals(bitmagConf.getTempDir().getAbsolutePath(), warcFile.getParentFile().getAbsolutePath());
//        
//        verify(record, times(1)).getFile();
//        verify(record, times(1)).getFieldValue(anyString());
//        verify(record, times(1)).getUUID();
//        verifyNoMoreInteractions(record);
//        
//        verifyZeroInteractions(archive);
//
//        addStep("Pack record again, now with larger metadata file.", "Should perform the upload");
//        File newMetadataFile = TestFileUtils.createFileWithContent(createString(bitmagConf.getWarcFileSizeLimit()));
//        preserver.packRecordAssetFile(record, newMetadataFile);
//
//        verify(archive).uploadFile(eq(warcFile), eq(collectionId));
//        verifyNoMoreInteractions(archive);        
//    }
//    
//    @Test
//    public void testPreservingRecordWithUploadFailure() throws Exception {
//        addDescription("Test preserving a record which is large enough for automatic upload.");
//        BitmagArchive archive = mock(BitmagArchive.class);
//        when(archive.uploadFile(any(File.class), anyString())).thenReturn(false);
//        
//        CumulusRecord record = mock(CumulusRecord.class);
//        when(record.getFieldValue(anyString())).thenReturn(collectionId);
//        when(record.getFile()).thenReturn(resourceFile);
//        when(record.getUUID()).thenReturn(recordId);
//        
//        BitmagPreserver preserver = new BitmagPreserver(archive, bitmagConf);
//
//        addStep("Pack the record and the default metadata", 
//                "Should be placed in the WARC file, but the warc file should not be uploaded yet (not large enough)");
//        preserver.packRecordAssetFile(record, metadataFile);
//        File warcFile = preserver.getWarcPacker(collectionId).getWarcFile();
//        preserver.uploadAll();
//        
//        assertTrue(preserver.warcPackerForCollection.isEmpty());
//        assertTrue(warcFile.isFile(), "Should not remove the warc-file when failure.");
//        
//        verify(archive).uploadFile(any(File.class), anyString());
//    }
    
    protected String createString(int minSize) {
        StringBuilder res = new StringBuilder();
        
        while(res.length() < minSize) {
            res.append("abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZæøåÆØÅ");
        }
        
        return res.toString();
    }
}
