package com.antiaction.common.templateengine;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;

import org.junit.Assert;

public final class TestUtils {

	protected static ClassLoader clsLdr = TestUtils.class.getClassLoader();

	public static final File getTestResourceFile(String fname) {
		URL url = clsLdr.getResource(fname);
		String path = url.getFile();
		path = path.replaceAll("%5b", "[");
		path = path.replaceAll("%5d", "]");
		File file = new File(path);
		return file;
	}

	public static final void saveBytes(File file, byte[] bytes) throws IOException {
		if ( file.exists() ) {
			if ( !file.delete() ) {
				Assert.fail( "Unable to delete file!" );
			}
		}
		RandomAccessFile raf = new RandomAccessFile( file, "rw" );
		raf.seek( 0L );
		raf.setLength( 0L );
		raf.write( bytes );
		raf.close();
	}

	public static final void deleteFile(File file) throws IOException {
		if ( file.exists() ) {
			if ( !file.delete() ) {
				Assert.fail( "Unable to delete file!" );
			}
		}
	}

}
