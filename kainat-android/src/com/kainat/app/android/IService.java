package com.kainat.app.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public abstract interface IService
{
  public abstract boolean copyFile(long paramLong, String paramString1, String paramString2, boolean paramBoolean);

  public abstract boolean createFile(long paramLong, String paramString, boolean paramBoolean1, boolean paramBoolean2);

  public abstract boolean createThumbnail(String paramString, boolean paramBoolean);

  public abstract boolean deleteFile(long paramLong, String paramString, boolean paramBoolean);

  public abstract void destroy();

  public abstract boolean exists(long paramLong, String paramString, boolean paramBoolean);

  public abstract FileInfo getFileInfo(long paramLong, String paramString, boolean paramBoolean);

  public abstract InputStream getFileInputStream(String paramString);

  public abstract OutputStream getFileOutputStream(String paramString);

  public abstract Map<String, Object> getNearbyDevices(long paramLong, boolean paramBoolean);

  public abstract Map<String, Object> getTypedFiles(long paramLong, String paramString1, String paramString2, boolean paramBoolean);

  public abstract void init();

  public abstract Map<String, Object> listFiles(String paramString, boolean paramBoolean)
    throws IOException;

  public abstract void loadApplications(boolean paramBoolean);

  public abstract boolean moveFile(long paramLong, String paramString1, String paramString2, boolean paramBoolean);

  public abstract boolean renameFile(long paramLong, String paramString1, String paramString2, boolean paramBoolean);
}