package org.telosys.tools.tests.commons.bundles;

import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.bundles.BundleStatus;
import org.telosys.tools.commons.bundles.BundlesManager;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.cfg.TelosysToolsCfgManager;
import org.telosys.tools.tests.commons.TestsFolders;
import org.telosys.tools.tests.commons.http.HttpTestConfig;

public class BundlesManagerTest extends TestCase {

	TelosysToolsCfg telosysToolsCfg = null ;
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("===== [ SETUP ] =====");
		System.out.println("Loading configuration from folder : " + TestsFolders.getTestsRootFolder() );
		TelosysToolsCfgManager cfgManager = new TelosysToolsCfgManager(TestsFolders.getTestsRootFolder());
		try {
			this.telosysToolsCfg = cfgManager.loadProjectConfig();
		} catch (TelosysToolsException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot load project properties", e);
		}

		
		
		Properties configProperties = telosysToolsCfg.getProperties();
		
//		System.out.println("Forcing http properties from " + TestsFolders.getTestsProxyPropertiesFile() +" ...");
//		PropertiesManager pm = new PropertiesManager(TestsFolders.getTestsProxyPropertiesFile());
//		Properties proxyProperties = pm.load();
//		if ( proxyProperties == null ) {
//			throw new RuntimeException("ERROR : No proxy properties");
//		}
		System.out.println("Forcing http properties ...");
		Properties proxyProperties = HttpTestConfig.getSpecificProxyProperties();
		setProperty(configProperties, proxyProperties, "http.proxyHost");
		setProperty(configProperties, proxyProperties, "http.proxyPort");
		setProperty(configProperties, proxyProperties, "https.proxyHost");
		setProperty(configProperties, proxyProperties, "https.proxyPort");
		
		System.out.println("HTTP properties : ");
		printProperty(configProperties, "http.proxyHost");
		printProperty(configProperties, "http.proxyPort");
		printProperty(configProperties, "https.proxyHost");
		printProperty(configProperties, "https.proxyPort");
	}
	private void setProperty(Properties configProperties, Properties proxyProperties, String name) {
		System.out.print("Property '"+name+"' ");
		String value = proxyProperties.getProperty(name) ;
		if ( value != null ) {
			configProperties.setProperty(name, value);
			System.out.print(" set to '"+value+"' ");
		}
		else {
			System.out.print(" null (not set)");
		}
		System.out.println("");
	}
	
	private void printProperty(Properties properties, String name) {
		System.out.println(name + " : " + properties.getProperty(name));
	}
	
	private BundlesManager getBundlesManager() {
		BundlesManager bm = new BundlesManager( telosysToolsCfg );
		return bm;
	}
	
	public void testFolder() throws TelosysToolsException {
		System.out.println("========== File system folder  ");

		String bundlesFolderInConfig = TestsFolders.getTestsBundlesFolder();
		System.out.println("Bundles folder in config : '" + bundlesFolderInConfig + "'");
		BundlesManager bm = getBundlesManager();
		
		System.out.println("Getting downloads folder ...");
		String downloadsFolder = bm.getDownloadsFolderFullPath() ;
		System.out.println(" result = '" + downloadsFolder + "'... ");
		assertEquals(telosysToolsCfg.getDownloadsFolderAbsolutePath(), downloadsFolder);

		System.out.println("Getting bundles folder ...");
		String bundlesFolder = bm.getBundlesFolderFullPath() ;
		System.out.println(" result = '" + bundlesFolder + "'... ");
		assertEquals(telosysToolsCfg.getTemplatesFolderAbsolutePath(), bundlesFolder);
		
		
		String bundleName = "foo";
		System.out.println("Getting folder for " + bundleName + "'... ");		
		String folder = bm.getBundleFolderFullPath(bundleName);
		System.out.println(" result = '" + folder + "'... ");
		
		String expected = telosysToolsCfg.getTemplatesFolderAbsolutePath() + "/" + bundleName ;
		assertEquals(expected, folder);
	}

	public void testBundlesList() throws Exception {
		System.out.println("========== List of available bundles  ");

		BundlesManager bm = getBundlesManager();
		List<String> bundles = bm.getBundlesList("telosys-tools") ;
		for ( String s : bundles ) {
			System.out.println(" . " + s );
		}
	}
	
	public void testIsBundleInstalled() throws TelosysToolsException {
		System.out.println("========== isBundleAlreadyInstalled  ");
		BundlesManager bm = getBundlesManager();
		boolean b = bm.isBundleAlreadyInstalled("no-installed");
		assertFalse(b);
	}

	public void testDownloadBundle() throws TelosysToolsException {
		System.out.println("========== Download  ");
		String bundleName = "persistence-jpa-TT210-R2" ;
		BundlesManager bm = getBundlesManager();
		System.out.println("Downloading bundle '" + bundleName + "'...");
		BundleStatus status = bm.downloadBundle("telosys-tools", bundleName);
		System.out.println("Satus message : " + status.getMessage() );
		System.out.println("Satus is done ? : " + status.isDone() );
		if ( status.getException() != null ) {
			System.out.println("Exception : " + status.getException());
		}
		System.out.println("Zip file : " + status.getZipFile());
		
		assertTrue(status.isDone() );
		assertNull(status.getException());
	}

	public void testDownloadBundleInSpecificFolder() throws TelosysToolsException {
		System.out.println("========== Download in specific folder ");
		String bundleName = "basic-templates-TT210" ;
		BundlesManager bm = getBundlesManager();
		System.out.println("Downloading bundle '" + bundleName + "'...");
		BundleStatus status = bm.downloadBundle("telosys-tools", bundleName, "TelosysTools/downloads2");
		System.out.println("Satus message : " + status.getMessage() );
		System.out.println("Satus is done ? : " + status.isDone() );
		if ( status.getException() != null ) {
			System.out.println("Exception : " + status.getException());
		}
		System.out.println("Zip file : " + status.getZipFile());
		
		assertTrue(status.isDone() );
		assertNull(status.getException());
	}

	public void testDownloadThenInstallBundle() throws TelosysToolsException {
		System.out.println("========== Download + Install ");
		String bundleName = "persistence-jpa-TT210-R2" ;
		BundlesManager bm = getBundlesManager();
		System.out.println("Downloading bundle '" + bundleName + "'...");
		
		BundleStatus status = bm.downloadBundle("telosys-tools", bundleName);

		System.out.println("Satus message : " + status.getMessage() );
		System.out.println("Satus is done ? : " + status.isDone() );
		System.out.println("Zip file : " + status.getZipFile());
		if ( status.getException() != null ) {
			System.out.println("Exception : " + status.getException());
		}
		
		assertTrue(status.isDone() );
		assertNull(status.getException());
		String zipFile = status.getZipFile();
		if ( status.isDone() && status.getException() == null ) {
			System.out.println("Installing bundle '" + bundleName + "' from " + zipFile );
			BundleStatus status2 = bm.installBundle(zipFile, bundleName);
			System.out.println("Satus message : " + status2.getMessage() );
			System.out.println("Satus is done ? : " + status2.isDone() );
			System.out.println("Exception : " + status2.getException());
			System.out.println("Satus log : "  );
			System.out.println( status2.getLog() );
		}
	}

	public void testDownloadAndInstallBundle() throws TelosysToolsException {
		System.out.println("========== downloadAndInstallBundle ");
		String bundleName = "persistence-jpa-TT210-R2" ;
		BundlesManager bm = getBundlesManager();

		BundleStatus status = bm.downloadAndInstallBundle("telosys-tools", bundleName);

		System.out.println("Satus message : " + status.getMessage() );
		System.out.println("Satus is done ? : " + status.isDone() );
		System.out.println("Zip file : " + status.getZipFile());
		if ( status.getException() != null ) {
			System.out.println("Exception : " + status.getException());
		}
		
		//assertTrue(status.isDone() ); // Not "done" if already installed
		assertNull(status.getException());
		System.out.println("Satus log : "  );
		System.out.println( status.getLog() );
	}

}
