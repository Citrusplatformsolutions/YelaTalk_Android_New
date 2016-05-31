package com.kainat.app.android.common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FtpInputStream extends BufferedInputStream
{
  public FtpInputStream(InputStream paramInputStream)
  {
    super(paramInputStream, 8192);
  }

  public int available()
    throws IOException
  {
    int i;
    if ((this.buf != null) && (this.in != null))
      i = this.buf.length - this.pos + this.in.available();
    else
      i = 0;
    return i;
  }

  public int read()
    throws IOException
  {
    return super.read();
  }

  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int j = 0;
    if (paramInt2 >= 1)
    {
      int i1;
      int i = available();
      if (paramInt2 <= i)
        i1 = paramInt2;
      else
        i1 = i;
      if (i1 < 1)
        i1 = 1;
      int k = read();
      if (k != -1)
      {
        int l = paramInt1;
        while (true)
        {
          i = paramInt1 + 1;
          paramArrayOfByte[paramInt1] = (byte)k;
          if (--i1 <= 0)
            break;
          k = read();
          if (k == -1)
            break;
          paramInt1 = i;
        }
        i -= l;
      }
      else
      {
        j = -1;
      }
    }
    else
    {
      j = 0;
    }
    return j;
  }

  public long skip(long paramLong)
    throws IOException
  {
    long l;
    if (paramLong <= 0L)
    {
      l = 0L;
    }
    else
    {
      this.pos = (int)(paramLong + this.pos);
      l = paramLong;
    }
    return l;
  }
}