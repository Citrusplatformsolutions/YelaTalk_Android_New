package com.kainat.app.android.util;

public class ByteArrayOutputStreamUtil extends java.io.ByteArrayOutputStream {

	public ByteArrayOutputStreamUtil() {
	}

	public ByteArrayOutputStreamUtil(int size) {
		super(size);
	}

	public synchronized byte[] toByteArray() {
		return super.buf;
	}
}
