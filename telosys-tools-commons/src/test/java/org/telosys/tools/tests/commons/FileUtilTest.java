package org.telosys.tools.tests.commons;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.telosys.tools.commons.FileUtil;

public class FileUtilTest extends TestCase {

	public void testCopyWithFileName() throws IOException, Exception {
		
		String fileName = "/testfilecopy/origin/file1.txt" ;
		System.out.println("Searching file '" + fileName + "' by classpath..." );
		File file = FileUtil.getFileByClassPath(fileName);
		if ( file.exists() ) {
			System.out.println("File found : " + file);
			System.out.println(" . getAbsolutePath()  : " + file.getAbsolutePath() );
			System.out.println(" . getCanonicalPath() : " + file.getCanonicalPath() );
			System.out.println(" . getName()          : " + file.getName() );
			System.out.println(" . getPath()          : " + file.getPath() );
			System.out.println(" . getParent()        : " + file.getParent() );
		}
		else {
			System.out.println("File not found " );
		}
		assertTrue ( file.exists()) ;
		
		// Original file
		String originalFullFileName = file.getAbsolutePath();
		System.out.println("Original file    : " + originalFullFileName );

		// Destination file in inexistent folder 
		String destFullFileName = FileUtil.buildFilePath(file.getParentFile().getParent()+"/newfolder", "newfile1.txt");
		System.out.println("Destination file : " + destFullFileName );
		
		FileUtil.copy(originalFullFileName, destFullFileName, true);
	}
	
	public void testCopyWithFileObject() throws IOException, Exception {
		
		String sourceFileName = TestsFolders.getFullFileName("file1.txt");
		String destinationFileName = TestsFolders.getFullFileName("file1-bis.txt");
		System.out.println("Copy from file '" + sourceFileName + "' to '" + destinationFileName + "'" );

		File sourceFile = new File(sourceFileName);
		File destinationFile = new File(destinationFileName);
		assertTrue ( sourceFile.exists()) ;
		
		FileUtil.copy(sourceFile, destinationFile, true);
	}
	
	public void testCopyWithFileObject2() throws IOException, Exception {
		
		String sourceFileName = TestsFolders.getFullFileName("file1.txt");
		String destinationFileName = TestsFolders.getFullFileName("foo/bar/file1-bis.txt");
		System.out.println("Copy from file '" + sourceFileName + "' to '" + destinationFileName + "'" );

		File sourceFile = new File(sourceFileName);
		File destinationFile = new File(destinationFileName);
		assertTrue ( sourceFile.exists()) ;
		
		FileUtil.copy(sourceFile, destinationFile, false);
	}

	public void testCopyFileToDirectory() throws IOException, Exception {
		
		String sourceFileName = TestsFolders.getFullFileName("file1.txt");
		String destinationDirectoryName = TestsFolders.getFullFileName("mydir/dest");
		System.out.println("Copy from file '" + sourceFileName + "' to '" + destinationDirectoryName + "'" );

		File sourceFile = new File(sourceFileName);
		File destinationDirectory = new File(destinationDirectoryName);
		assertTrue ( sourceFile.exists()) ;
		
		FileUtil.copyToDirectory(sourceFile, destinationDirectory, true);
	}
	
	public void testFolderCopy() throws IOException, Exception {
		
		String folderName = "/testfilecopy" ;
		System.out.println("Searching folder '" + folderName + "' by classpath..." );
		File folder = FileUtil.getFileByClassPath(folderName);
		if ( folder.exists() ) {
			System.out.println("Folder found : " + folder);
			System.out.println(" . getAbsolutePath()  : " + folder.getAbsolutePath() );
			System.out.println(" . getCanonicalPath() : " + folder.getCanonicalPath() );
			System.out.println(" . getName()          : " + folder.getName() );
			System.out.println(" . getPath()          : " + folder.getPath() );
			System.out.println(" . getParent()        : " + folder.getParent() );
		}
		else {
			System.out.println("Folder not found " );
		}
		assertTrue ( folder.exists()) ;
		
		for ( String fileName : folder.list() ) {
			System.out.println(" . " + fileName );
		}
	
		for ( File file : folder.listFiles() ) {
			System.out.println(" . " + file );
			if ( "origin".equals( file.getName() ) ) {
				System.out.println("'origin' folder found.");
				File originFolder = file ;
				
				File destinationFolder = new File(folder.getAbsolutePath(), "dest");
				FileUtil.copyFolder(originFolder, destinationFolder, false) ;
			}
		}
	}
	

	public void testBuildFilePath1()  {
		String dir = "D:\\workspaces\\runtime-EclipseApplication\\myapp/TelosysTools/templates/front-springmvc-TT210-R2/resources" ;
		String file = "/src/main/webapp" ;
		String s = FileUtil.buildFilePath(dir, file);
		System.out.println("s = " + s );
		assertEquals(dir+file, s);
	}
	
	public void testBuildFilePath2a()  {
		String dir = "D:\\aaa\\bbb/ccc/ddd/" ;
		String file = "/xxx/yyy/zzz.txt" ;
		String s = FileUtil.buildFilePath(dir, file);
		System.out.println("s = " + s );
		assertEquals(dir+"xxx/yyy/zzz.txt", s);
	}
	public void testBuildFilePath2b()  {
		String dir = "D:\\aaa\\bbb/ccc" ;
		String file = "/xxx/yyy/zzz.txt" ;
		String s = FileUtil.buildFilePath(dir, file);
		System.out.println("s = " + s );
		assertEquals("D:\\aaa\\bbb/ccc/xxx/yyy/zzz.txt", s);
	}

	public void testBuildFilePath3()  {
		String dir = "D:\\aaa\\bbb/ccc/ddd" ;
		String file = "/xxx/yyy/zzz.txt" ;
		String s = FileUtil.buildFilePath(dir, file);
		System.out.println("s = " + s );
		assertEquals(dir+file, s);
	}
	public void testBuildFilePath4()  {
		String dir = "D:\\aaa\\bbb/ccc/ddd" ;
		String file = "\\xxx/yyy/zzz.txt" ;
		String s = FileUtil.buildFilePath(dir, file);
		System.out.println("s = " + s );
		assertEquals(dir+"/xxx/yyy/zzz.txt", s);
	}
}
