package com.kainat.app.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.util.Log;

public class IOUtils {
	private static int CHUNK_SIZE = 8 * 1024;
	private static byte[] BUFFER = new byte[CHUNK_SIZE];

	private int totalChunks;
	private byte[][] chunks;

	private IOUtils() {
	}

	public synchronized static String readnnn(InputStream istream) throws IOException {
		StringBuffer sb = new StringBuffer();
		int len;
		while ((len = istream.read(BUFFER)) != -1) {
//			String str = new String(BUFFER, Constants.ENC);
			String str = new String(BUFFER, 0,len);//Constants.ENC);
			
			sb.append(str);
		}
		Log.i(" READ", sb.toString());
		return sb.toString();
	}

	
	public synchronized static String read(InputStream istream) throws IOException 
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(istream, "UTF-8"));
		String readLine = "" ;
		StringBuffer fullData = new StringBuffer();
		while (( readLine = reader.readLine()) != null){
			fullData.append(readLine);
		}
		return fullData.toString();
	}

	private void createChunks(int totalSize) {
		if (totalSize <= 0) {
			throw new IllegalArgumentException("totalSize is <= 0: " + totalSize);
		} else if (totalSize < CHUNK_SIZE) {
			throw new IllegalArgumentException("totalSize " + totalSize + " is < chunkSize " + CHUNK_SIZE);
		}

		this.totalChunks = (int) Math.ceil((double) totalSize / (double) CHUNK_SIZE);
		this.chunks = new byte[totalChunks][];

		int sizeLeft = totalSize;
		int i = 0;
		while (sizeLeft > 0) {
			int nextChunkSize = Math.min(sizeLeft, CHUNK_SIZE);
			sizeLeft -= nextChunkSize;

			chunks[i] = new byte[nextChunkSize];
			i++;
		}
	}

	public void writeTo(byte[] data, OutputStream stream) throws IOException {
		createChunks(data.length);
		for (byte[] chunk : chunks) {
			stream.write(chunk, 0, chunk.length);
		}
	}
}