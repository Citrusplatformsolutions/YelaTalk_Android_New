package com.kainat.app.android.common;

import java.io.File;

public class FileUtils
{
  public static boolean copyFile(File paramFile1, File paramFile2)
  {
	  return false;
    /*if ((paramFile1 == null) || (paramFile2 == null))
      i = 0;
    while (true)
    {
      return i;
      if (paramFile1.exists())
        break;
      i = 0;
    }
    int i = 0;
    if (paramFile2.exists())
      paramFile2.delete();
    FileInputStream localFileInputStream = null;
    FileOutputStream localFileOutputStream = null;
    try
    {
      localFileInputStream = new FileInputStream(paramFile1);
    }
    catch (IOException localIOException4)
    {
      try
      {
        localFileOutputStream = new FileOutputStream(paramFile2);
      }
      catch (IOException sd)
      {
        try
        {
          byte[] arrayOfByte;
          int j;
          while (true)
          {
            arrayOfByte = new byte[102400];
            j = localFileInputStream.read(arrayOfByte);
            if (j != -1)
              break;
            localFileInputStream.close();
            localFileOutputStream.close();
            i = 1;
            if ((i == 0) && (paramFile2.exists()))
              label94: paramFile2.delete();
            i = i;
          }
          localFileOutputStream.write(arrayOfByte, 0, j);
        }
        catch (IOException localIOException1)
        {
          localFileOutputStream = localFileOutputStream;
          localFileInputStream = localFileInputStream;
          if (localFileInputStream != null);
          try
          {
            do
              localFileInputStream.close();
            while (localFileOutputStream == null);
            localFileOutputStream.close();
          }
          catch (IOException localIOException2)
          {
            while (true)
            {
              while (true)
              {
                break label94:
                localIOException3;
              }
              localIOException4;
              localFileInputStream = localFileInputStream;
            }
          }
        }
      }
    }*/
  }
}