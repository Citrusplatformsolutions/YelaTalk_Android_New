package com.kainat.app.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class FileIOUtility {
    private FileIOUtility() {}
    public static void moveFile(File src, File targetDirectory) throws IOException {
        if (!src.renameTo(new File(targetDirectory, src.getName()))) {
            String str = (new StringBuilder()).append("Failed to move ").append(src).append(" to ").append(targetDirectory).toString();
            throw new IOException(str);
        } else            return;
    }
    public static void copyFile(File src, File dest) throws IOException {
        FileInputStream fileSrc = new FileInputStream(src);
        FileOutputStream fileDest = new FileOutputStream(dest);
        copyInputStream(fileSrc, fileDest);
    }
    public static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte buffer[] = new byte[4096];
        for (int len = in.read(buffer); len >= 0; len = in.read(buffer))
            out.write(buffer, 0, len);
        in.close();
        out.close();
    }
    public static void main(String[] args) throws Exception {
        copyFile(new File("D:\\aa.txt"), new File("D:\\bb.txt"));
        moveFile(new File("D:\\aa.txt"), new File("D:\\test"));
    }
}