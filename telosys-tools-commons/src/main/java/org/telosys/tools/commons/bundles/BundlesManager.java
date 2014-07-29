/**
 *  Copyright (C) 2008-2014  Telosys project org. ( http://www.telosys.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.telosys.tools.commons.bundles;

import java.io.File;

import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.ZipUtil;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.github.GitHubClient;


/**
 * Utility class for GitHub
 * 
 * @author L. Guerin
 *
 */
public class BundlesManager {

	private final TelosysToolsCfg cfg ;
	
	//--------------------------------------------------------------------------------------------------
	/**
	 * Constructor 
	 * @param bundlesFolder bundles folder in the file system (full path)
	 */
	public BundlesManager(TelosysToolsCfg cfg) {
		super();
		this.cfg = cfg;
	}

	//--------------------------------------------------------------------------------------------------
	/**
	 * Returns the file system folder's full path for the given bundle name
	 * @param bundleName
	 * @return
	 */
	public String getFileSystemFolder( String bundleName ) {
		//String bundlesFolder = cfg.getTemplatesFolder();
		String bundlesFolder = cfg.getTemplatesFolderAbsolutePath();
		return FileUtil.buildFilePath( bundlesFolder, bundleName);
	}
	
	//--------------------------------------------------------------------------------------------------
	/**
	 * Returns true if the bundle is already installed (if the bundle's folder exists)
	 * @param bundleName
	 * @return
	 */
	public boolean isBundleAlreadyInstalled( String bundleName ) {
		File file = new File(getFileSystemFolder(bundleName)) ;
		if ( file.exists() ) {
			return true ;
		}
		else {
			return false ;
		}
	}
	
	//--------------------------------------------------------------------------------------------------
	/**
	 * Downloads a bundle (GitHub repo) in the downloads folder defined in the current configuration<br>
	 * e.g. downloads a zip file in 'TelosysTools/downloads'  
	 * @param userName the GitHub user name (e.g. "telosys-tools")
	 * @param bundleName the bundle name, in other words the GitHub repository name 
	 * @return
	 */
	public BundleStatus downloadBundle( String userName, String bundleName ) {
		
		return downloadBundle( userName, bundleName, cfg.getDownloadsFolder() ) ;
	}

	public BundleStatus downloadAndInstallBundle( String userName, String bundleName ) {
		
		BundleStatus status1 = downloadBundle( userName, bundleName, cfg.getDownloadsFolder() ) ;
		if ( status1.isDone() && status1.getException() == null ) {
			BundleStatus status2 = installBundle(status1.getZipFile(), bundleName);
			return status2 ;
		}
		else {
			return status1 ;
		}
	}

	//--------------------------------------------------------------------------------------------------
	/**
	 * Download a bundle (GitHub repo) in a specific project folder
	 * @param userName
	 * @param bundleName
	 * @param downloadFolderInProject
	 * @return
	 */
	public BundleStatus downloadBundle( String userName, String bundleName, String downloadFolderInProject )  {
		BundleStatus status = new BundleStatus();
		GitHubClient gitHubClient = new GitHubClient( cfg.getProperties() ) ; 
		String destinationFile = buildDestinationFileName(bundleName, downloadFolderInProject) ;
		status.log("-> Download bundle '" + bundleName + "' ");
		status.log("   in '" + destinationFile + "' ");
		try {
			gitHubClient.downloadRepository(userName, bundleName, destinationFile);
			status.setDone(true);
			status.setMessage("OK, bundle '" + bundleName + "' downloaded.");
			status.setZipFile(destinationFile);
		} catch (Exception e) {
			status.setDone(false);
			status.setMessage("ERROR: cannot download bundle '" + bundleName + "'.");
			status.setException(e);
		}
		return status ;
	}
	
	//--------------------------------------------------------------------------------------------------
	/**
	 * Build the filesystem full path for the given repository name and destination folder
	 * @param repoName GitHub repository name
	 * @param sDownloadFolder
	 * @return
	 */
	private String buildDestinationFileName(String repoName, String sDownloadFolder) {
		// file path in project
		String sFile = repoName + ".zip" ;
		String pathInProject = FileUtil.buildFilePath(sDownloadFolder, sFile);
		// file path in Operating System 
		String fullPath = FileUtil.buildFilePath(cfg.getProjectAbsolutePath(), pathInProject);
		return fullPath;
	}
	
	//--------------------------------------------------------------------------------------------------
	/**
	 * Install (unzip) the given zip file in the bundle's destination folder 
	 * @param zipFileName
	 * @param bundleName
	 * @return
	 */
	public BundleStatus installBundle( String zipFileName, String bundleName ) {
		
		BundleStatus status = new BundleStatus();
		
		if ( isBundleAlreadyInstalled( bundleName ) ) {
			status.setDone(false);
			status.setMessage("Bundle already installed.");
			return status ;
		}
		else {
			String filesystemFolder = getFileSystemFolder(bundleName) ;
			status.log("-> Install '" + zipFileName + "' ");
			status.log("   in '" + filesystemFolder + "' ");
			try {
				ZipUtil.unzip(zipFileName, filesystemFolder, true ) ;
				status.setDone(true);
				status.setMessage("OK, bundle installed.");
			} catch (Exception e) {
				status.setDone(false);
				status.setMessage("ERROR: Cannot unzip "+ zipFileName);
				status.setException(e);
			}
			return status ;
		}
	}
}
