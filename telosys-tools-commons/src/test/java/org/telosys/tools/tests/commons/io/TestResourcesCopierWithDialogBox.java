package org.telosys.tools.tests.commons.io;

import java.io.File;

import org.telosys.tools.commons.io.DialogBoxOverwriteChooser;
import org.telosys.tools.commons.io.OverwriteChooser;
import org.telosys.tools.commons.io.ResourcesCopier;
import org.telosys.tools.tests.commons.TestsFolders;

public class TestResourcesCopierWithDialogBox {

	public static void main(String[] args) {

		testCopyFileToFile1() ;
	}
	
	//----------- File to File ------------------
	public static void testCopyFileToFile1() {
		int n = copy (	getOriginFile("file1.txt"), 
						getDestinationFile("file1-copy.txt"), 
						OverwriteChooser.YES);
		showResult(n);
	}

	public static void testCopyFileToFile2()  {
		int n = copy (	getOriginFile("file1.txt"), 
						getDestinationFile("file1-copy.txt"), 
						OverwriteChooser.NO);
		showResult(n);
	}

	//----------- Folder to Folder ------------------
	public static void testCopyFolderToFolder1() {
		int n = copy (	getOriginFile("foo"), 
						getDestinationFile("foo2"), 
						OverwriteChooser.YES);
		showResult(n);
	}
	
	public static void testCopyFolderToFolder2() {
		int n = copy (	getOriginFile("foo"), 
						getDestinationFile("foo3"), 
						OverwriteChooser.YES);
		showResult(n);
	}

	public static void testCopyFolderToFolder3()  {
		int n = copy (	getOriginFile("foo/bar"), 
						getDestinationFile("foo-bar"), 
						OverwriteChooser.YES);
		showResult(n);
	}
	
	public static void testCopyFolderToFolder4()  {
		int n = copy (	getOriginFile("foo/bar"), 
						getDestinationFile("foo-bar"), 
						OverwriteChooser.NO);
		showResult(n);
	}
	
	//----------- File to Folder ------------------
	public static void testCopyFileToFolder1()  {
		int n = copy (	getOriginFile("foo/fileA.txt"), 
						getDestinationFile("mydir"), 
						OverwriteChooser.YES);
		showResult(n);
	}
	public static void testCopyFileToFolder1bis() {
		int n = copy (	getOriginFile("foo/fileA.txt"), 
						getDestinationFile("mydir"), 
						OverwriteChooser.NO);
		showResult(n);
	}
	
	public static void testCopyFileToFolder2()  {
		int n = copy (	getOriginFile("foo/fileA.txt"), 
						getDestinationFile("mydir/dest-A"), 
						OverwriteChooser.YES);
		showResult(n);
	}
	public static void testCopyFileToFolder2bis() {
		int n = copy (	getOriginFile("foo/fileA.txt"), 
						getDestinationFile("mydir/dest-A"), 
						OverwriteChooser.NO);
		showResult(n);
	}
	
	//----------------------------------------------
	private static File getOriginFile(String fileOrFolderName ) {
		return new File(TestsFolders.getFullFileName("resources-origin/" + fileOrFolderName));
	}
	
	private static File getDestinationFile(String fileOrFolderName ) {
		return new File(TestsFolders.getFullFileName("resources-destination/" + fileOrFolderName));
	}
	
	private static int copy(File source, File destination, int choice ) {
		int n = 0 ;
		System.out.println("===== COPY ");
		System.out.println("  from : " + source  );
		System.out.println("  to   : " + destination );
		ResourcesCopier copier = new ResourcesCopier(new DialogBoxOverwriteChooser(), null);
		try {
			n = copier.copy(source, destination);
			System.out.println(n + " file(s) copied");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return n ;
	}
	
	//----------------------------------------------
	private static void showResult(int n) {
		System.out.println("Result : " + n);
	}
}
