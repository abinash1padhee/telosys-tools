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
package org.telosys.tools.commons ;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileUtil {
	
    private static final int BUFFER_SIZE = 4*1024 ; // 4 kb   
    
    public static String buildFilePath(String dir, String file) {
    	String s1 = dir ;
    	if ( dir.endsWith("/") || dir.endsWith("\\") )
    	{
    		s1 = dir.substring(0, dir.length() - 1 );
    	}
    	
    	String s2 = file ;
    	if ( file.startsWith("/") || file.startsWith("\\") )
    	{
    		s1 = file.substring(1);
    	}
    	
		return s1 + "/" + s2 ;
	}
    
    //----------------------------------------------------------------------------
    /**
     * Copy a file into another one
     * @param sInputFileName
     * @param sOutputFileName
     * @throws Exception
     */
    public static void copy(String sInputFileName, String sOutputFileName) throws Exception
    {
        //--- Open input file
		FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(sInputFileName);
        } catch (FileNotFoundException ex)
        {
            throw new Exception("copy : cannot open input file.", ex);
        }
        
        //--- Open output file
		FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(sOutputFileName);
        } catch (FileNotFoundException ex)
        {
            throw new Exception("copy : cannot open output file.", ex);
        }
        
        //--- Copy and close
        if ( fis != null && fos != null )
        {
			byte buffer[] = new byte[BUFFER_SIZE];
			int len = 0;
			
			try
            {
                while ((len = fis.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, len);
                }
                fis.close();
                fos.close();
            } catch (IOException ioex)
            {
                throw new Exception("copy : IO error.", ioex);
            }
		}
    }
}
