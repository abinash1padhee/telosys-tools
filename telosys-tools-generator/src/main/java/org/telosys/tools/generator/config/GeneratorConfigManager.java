/**
 *  Copyright (C) 2008-2013  Telosys project org. ( http://www.telosys.org/ )
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
package org.telosys.tools.generator.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.generator.GeneratorException;

/**
 * Generator configuration manager 
 * 
 * @author Laurent GUERIN
 *
 */
public class GeneratorConfigManager 
{
	TelosysToolsLogger _logger = null ;
	
	/**
	 * Constructor 
	 * @param logger the logger to use, can be null if not useful
	 */
	public GeneratorConfigManager(TelosysToolsLogger logger) 
	{
		super();
		this._logger = logger;
	}

	private void log(String s)
	{
		if ( _logger != null ) {
			_logger.log(s);
		}
	}
	private void error(String s)
	{
		if ( _logger != null ) {
			_logger.error(s);
		}
	}
	
	/**
	 * Initializes the GeneratorConfiguration using standard properties file
	 * 
	 * @param sProjectLocation the project location to be stored in the configuration and where the properties file is located
	 * @return
	 */
	public IGeneratorConfig initFromDirectory ( String sProjectLocation ) throws GeneratorException
	{
		log ( "initFromDirectory ("+ sProjectLocation + ")" );
		if ( null == sProjectLocation ) throw new GeneratorException("Invalid parameter : Directory is null") ;

		String sFullFileName ;
		if ( sProjectLocation.endsWith("/") || sProjectLocation.endsWith("\\") )
		{
			sFullFileName = sProjectLocation + GeneratorConfig.PROJECT_CONFIG_FILE ;
		}
		else
		{
			sFullFileName = sProjectLocation + "/" + GeneratorConfig.PROJECT_CONFIG_FILE ;
		}
		return init ( sProjectLocation, sFullFileName);
	}
	
	/**
	 * Initializes the GeneratorConfiguration using the given full file name
	 * 
	 * @param sFullFileName the full name of the file
	 * @return
	 */
	public IGeneratorConfig initFromFile ( String sFullFileName ) throws GeneratorException
	{
		log ( "initFromFile (" + sFullFileName + ")" );
		if ( null == sFullFileName ) throw new GeneratorException("Invalid parameter : File name is null") ;

		File file = new File(sFullFileName);
		return init ( file.getParent(), sFullFileName);
	}
	
	/**
	 * Initializes the GeneratorConfiguration using a specific properties file
	 * 
	 * @param sProjectLocation the project location to be stored in the configuration
	 * @param sConfigFileName the full path of the properties file to use 
	 * @return
	 */
	private IGeneratorConfig init ( String sProjectLocation, String sConfigFileName ) throws GeneratorException
	{
		log ( "init ("+ sProjectLocation + "," + sConfigFileName + ")");
		if ( null == sProjectLocation ) throw new GeneratorException("Invalid parameter : Project location is null") ;
		if ( null == sConfigFileName ) throw new GeneratorException("Invalid parameter : Config file is null") ;
		
		Properties prop = loadProperties(sConfigFileName);
		if ( prop != null )
		{
			// Properties loaded
			GeneratorConfig config = new GeneratorConfig( sProjectLocation, prop );
			return config ;
		}
		else
		{
			throw new GeneratorException("Cannot load properties from file '" + sConfigFileName + "'");
		}	
	}
	
	/**
	 * Loads the properties from the given file name 
	 * @param sFileName
	 * @return
	 */
	private Properties loadProperties( String sFileName) 
	{
		return loadProperties( new File(sFileName) );
	}
	
	/**
	 * Loads the properties from the given file 
	 * @param propFile
	 * @return
	 */
	private Properties loadProperties( File propFile ) 
	{
		//--- If the file doesn't exist ... return null (it's not an error)
		if ( propFile.exists() != true ) {
			return null ;
		}

		//--- The file exists => load it !  
		Properties props = new Properties();
		FileInputStream fis = null ;
		try {
			fis = new FileInputStream(propFile);
			props.load(fis);
		} catch (IOException ioe) {
			error("Cannot load properties from file : " + propFile.getAbsolutePath() );
			props = null ;
		}
		finally
		{
			try {
				if ( fis != null )
				{
					fis.close();
				}
			} catch (IOException e) {
				error("Cannot close file : " + propFile.getAbsolutePath() );
			}
		}
		return props;
	}
}
