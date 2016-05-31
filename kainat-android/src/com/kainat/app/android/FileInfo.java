package com.kainat.app.android;

public class FileInfo
{
  static final String[] TYPES;
  public long createdTime = 0L;
  public boolean hidden;
  public boolean isDirectory;
  public long lastAccessTime = 0L;
  public long lastModifiedTime = 0L;
  public String path = null;
  public boolean readable;
  public long size = 0L;
  public int subFiles = 0;
  public int subFolders = 0;
  public String typeString = null;
  public boolean writable;

  static
  {
    String[] arrayOfString = new String[7];
    arrayOfString[0] = "COMM";
    arrayOfString[1] = "FILESYSTEM";
    arrayOfString[2] = "NAMED_PIPE";
    arrayOfString[3] = "PRINTER";
    arrayOfString[4] = "SERVER";
    arrayOfString[5] = "SHARE";
    arrayOfString[6] = "WORKGROUP";
    TYPES = arrayOfString;
  }

  public FileInfo(String paramString)
  {
    this.path = paramString;
  }

  public void setType(int paramInt)
  {
    switch (paramInt)
    {
    case 1:
      this.typeString = TYPES[1];
      break;
    case 2:
      this.typeString = TYPES[5];
      break;
    case 4:
      this.typeString = TYPES[6];
      break;
    case 8:
      this.typeString = TYPES[4];
      break;
    case 16:
      this.typeString = TYPES[2];
      break;
    case 32:
      this.typeString = TYPES[3];
      break;
    case 64:
      this.typeString = TYPES[0];
    }
  }
}