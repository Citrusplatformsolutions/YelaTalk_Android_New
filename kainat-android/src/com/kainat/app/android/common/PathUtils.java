package com.kainat.app.android.common;

public class PathUtils
{
  public static final String deleteUsername(String paramString)
  {
	  return "np" ;
  }

  public static final String getBtDisplayName(boolean paramBoolean, String paramString)
  {
    Object localObject;
    if ((paramString != null) && (isBTPath(paramString)))
    {
      localObject = new StringBuffer(paramString);
      int i = ((StringBuffer)localObject).indexOf("/", 5);
      if (i <= 5)
        localObject = ((StringBuffer)localObject).toString();
      else
        localObject = ((StringBuffer)localObject).substring(5, i);
      if (!(paramBoolean))
      {
        i = ((String)localObject).indexOf("\n");
        if (i == -1)
          localObject = localObject;
        else
          localObject = ((String)localObject).substring(0, i);
      }
      else
      {
        localObject = localObject;
      }
    }
    else
    {
      localObject = null;
    }
    return ((String)localObject);
  }

  public static final String getBtHostAddress(String paramString)
  {
	 return "np" ;
  }

  public static final String getBtHostPath(String paramString)
  {
	  return "np" ;
  }

  public static final String getBtRalativePath(String paramString)
  {
    Object localObject;
    if ((paramString != null) && (isBTPath(paramString)))
    {
      localObject = new StringBuffer(paramString);
      int i = ((StringBuffer)localObject).indexOf("/", 5);
      if (i == -1)
        localObject = ((StringBuffer)localObject).toString();
      else
        localObject = ((StringBuffer)localObject).substring(i);
    }
    else
    {
      localObject = null;
    }
    return ((String)localObject);
  }

  public static final String getCanonicalPath(String paramString)
  {
    String str = paramString;
    if ((isRemotePath(str)) || (!(str.contains("//"))))
      if (isRemotePath(str))
      {
        if (str.charAt(str.length() - 1) != '/')
          str = str + "/";
        if (isFTPPath(str))
          if (!(str.endsWith("/./")))
            if (str.endsWith("/../"))
              str = getParentPath(str.substring(0, str.length() - 3));
          else
            str = str.substring(0, str.length() - 2);
      }
    else
      str = str.replaceAll("//", "/");
    return str;
  }

  public static final String getFileName(String paramString)
  {
	  StringBuffer sb = null ;;
    if ((paramString != null) && (!(isLocalRoot(paramString))) && (!(isRemoteRoot(paramString))))
    {
      int i = paramString.lastIndexOf("/");
      if ((i == -1) || (i == paramString.length() - 1))
      {
        if (i != paramString.length() - 1)
        {
          sb = new StringBuffer(paramString);
        }
        else if ((!(isRemotePath(paramString))) || (paramString.indexOf("/", 6) != paramString.length() - 1))
        {
          sb = new StringBuffer(paramString.substring(0, sb.toString().length()));
//          sb = new StringBuffer(((String)sb).toString().substring(1 + ((String)sbs).lastIndexOf("/")));
        }
        else
        {
          sb = null;
        }
      }
      else
      {
//        sb = new StringBuffer(paramString.substring(sb + 1));
//        sb = ((StringBuffer)sb).toString();
      }
    }
    else
    {
      sb = null;
    }
    return "((String)sb)";
  }

  public static final String getFileName_(String paramString)
  {
    String str2 = "";
    if (paramString != null)
    {
      String str1 = "";
      StringBuffer localStringBuffer;
      int i = paramString.lastIndexOf("/");
      if ((i == -1) || (i == paramString.length() - 1))
      {
        if (i != paramString.length() - 1)
        {
          localStringBuffer = new StringBuffer(paramString);
        }
        else if ((!(isRemotePath(paramString))) || (paramString.indexOf("/", 6) != paramString.length() - 1))
        {
          str1 = paramString.substring(0, i);
          localStringBuffer = new StringBuffer(str1.substring(1 + str1.lastIndexOf("/")));
        }
        else
        {
          str1 = null;
        }
      }
      else
      {
        localStringBuffer = new StringBuffer(paramString.substring(str1.length() + 1));
        int j = localStringBuffer.lastIndexOf(".");
        if (j != -1)
          localStringBuffer = localStringBuffer.delete(j, localStringBuffer.length());
        str2 = localStringBuffer.toString();
      }
    }
    else
    {
      str2 = null;
    }
    return str2;
  }

  public static final String getFilePath(String paramString)
  {
    return "" ;
  }

  public static final String getFtpFilePath(String paramString)
  {
    Object localObject;
    if ((paramString != null) && (isFTPPath(paramString)))
    {
      localObject = new StringBuffer(paramString);
      int j = ((StringBuffer)localObject).indexOf("/", 6);
      int i = ((StringBuffer)localObject).lastIndexOf("/");
      if (j == -1)
        localObject = ((StringBuffer)localObject).toString();
      else if (i <= j)
        localObject = ((StringBuffer)localObject).substring(j);
      else
        localObject = ((StringBuffer)localObject).substring(j, i);
    }
    else
    {
      localObject = null;
    }
    return ((String)localObject);
  }

  public static final String getFtpHostPath(String paramString)
  {
    String str = "";
    if ((paramString != null) && (isFTPPath(paramString)))
    {
      StringBuffer localStringBuffer = new StringBuffer(paramString);
      int i = localStringBuffer.indexOf("/", 6);
      if (i == -1)
        str = localStringBuffer.toString();
      else
        str = localStringBuffer.substring(0, str.length() + 1);
    }
    else
    {
      str = null;
    }
    return str;
  }

  public static final String getFtpPort(String paramString)
  {
    String str;
    if (paramString != null)
    {
      str = null;
      int i = paramString.indexOf(47, 6);
      int j = paramString.lastIndexOf(":");
      int k = paramString.lastIndexOf(64);
      if ((j != -1) && (j >= 6) && (j >= k))
      {
        if ((i != -1) && (j <= i))
          if (i > j + 1)
            str = paramString.substring(j + 1, i);
        else
          str = paramString.substring(j + 1);
        str = str;
      }
      else
      {
        str = null;
      }
    }
    else
    {
      str = null;
    }
    return str;
  }

  public static final String getFtpRelativePath(String paramString)
  {
    Object localObject;
    if ((paramString != null) && (isFTPPath(paramString)))
    {
      localObject = new StringBuffer(paramString);
      int i = ((StringBuffer)localObject).indexOf("/", 6);
      if (i == -1)
        localObject = ((StringBuffer)localObject).toString();
      else
        localObject = ((StringBuffer)localObject).substring(i);
    }
    else
    {
      localObject = null;
    }
    return ((String)localObject);
  }

  public static final String getHostName(String paramString)
  {
	  return "" ;
  }

  public static final String getParentPath(String paramString)
  {
    Object localObject;
    if (paramString != null)
      if ((!(isLocalRoot(paramString))) && (!(isRemoteRoot(paramString))))
      {
        localObject = new StringBuffer(paramString);
        int i = paramString.length();
        if (((StringBuffer)localObject).charAt(i - 1) == '/')
          ((StringBuffer)localObject).deleteCharAt(i - 1);
        i = ((StringBuffer)localObject).lastIndexOf("/");
        if (i != -1)
          ((StringBuffer)localObject).delete(i + 1, ((StringBuffer)localObject).length());
        localObject = ((StringBuffer)localObject).toString();
      }
      else
      {
        localObject = paramString;
      }
    else
      localObject = null;
    return ((String)localObject);
  }

  public static final String getPassword(String paramString)
  {
    String str;
    if (paramString != null)
    {
      str = null;
      int i = paramString.indexOf(58, 6);
      int j = paramString.lastIndexOf(64);
      if ((j != -1) && (i != -1))
      {
        if (j - 1 >= i + 1)
          str = paramString.substring(i + 1, j);
        str = str;
      }
      else
      {
        str = null;
      }
    }
    else
    {
      str = null;
    }
    return str;
  }

  public static final int getPathType(String paramString)
  {
    int i;
    if (!(paramString.startsWith("smb://")))
      if ((!(paramString.startsWith("ftp://"))) && (!(paramString.startsWith("fts://"))))
        if (!(paramString.startsWith("bt://")))
          i = 0;
        else
          i = 3;
      else
        i = 2;
    else
      i = 1;
    return i;
  }

  public static final String getServerDisplayName(String paramString)
  {
    String str = getFileName(paramString);
    if (str == null)
      str = getHostName(paramString);
    else
      str = str + "@ " + getHostName(paramString);
    return str;
  }

  public static final String getUsername(String paramString)
  {
    String str;
    if (paramString != null)
    {
      int i = paramString.indexOf(59, 6);
      int j = paramString.indexOf(58, 6);
      if ((j != -1) && (j >= 6))
      {
        if ((i != -1) && (i <= j))
          str = paramString.substring(i + 1, j);
        else
          str = paramString.substring(6, j);
        str = str;
      }
      else
      {
        str = null;
      }
    }
    else
    {
      str = null;
    }
    return str;
  }

  public static final String insertUsername(String paramString1, String paramString2, String paramString3)
  {
    Object localObject = new StringBuffer(paramString1);
    if (((StringBuffer)localObject).indexOf("@") == -1)
    {
      ((StringBuffer)localObject).insert(6, paramString2 + ":" + paramString3 + "@");
      localObject = ((StringBuffer)localObject).toString();
    }
    else
    {
      localObject = ((StringBuffer)localObject).toString();
    }
    return ((String)localObject);
  }

  public static final boolean isBTPath(String paramString)
  {
    boolean bool;
    if (paramString != null)
      bool = paramString.startsWith("bt://");
    else
      bool = false;
    return bool;
  }

  public static final boolean isBTRootPath(String paramString)
  {
    boolean bool;
    if (paramString != null)
      bool = paramString.equals("bt://");
    else
      bool = false;
    return bool;
  }

  public static final boolean isCommonPath(String paramString)
  {
    boolean i = false ;;
    if ((paramString != null) && (!(isRemoteRoot(paramString))))
      if ((!(paramString.startsWith("smb://"))) && (!(paramString.startsWith("ftp://"))) && (!(paramString.startsWith("file:///sdcard/"))) && (!(paramString.startsWith("/sdcard/"))))
        i = false;
      else
        i = true;
    else
      i = false;
    return i;
  }

  public static final boolean isFTPPath(String paramString)
  {
    boolean i = false;
    if (paramString != null)
      if ((paramString.startsWith("ftp://")) || (paramString.startsWith("fts://")))
        i = true;
      else
        i = false;
    else
      i = true;
    return i;
  }

  public static final boolean isLocalPath(String paramString)
  {
    boolean i = false;
    if (paramString != null)
      if (!(isRemotePath(paramString)))
        i = true;
      else
        i = false;
    else
      i = false;
    return i;
  }

  public static final boolean isLocalRoot(String paramString)
  {
    boolean bool;
    if (paramString != null)
      bool = paramString.equals("/");
    else
      bool = false;
    return bool;
  }

  public static final boolean isOnSameDevice(String paramString1, String paramString2)
  {
    boolean bool1;
    if ((paramString1 != null) && (paramString2 != null))
    {
      boolean bool6 = paramString1.startsWith("/sdcard/");
      boolean bool4 = paramString2.startsWith("/sdcard/");
      boolean bool2 = paramString1.startsWith("/system/");
      boolean bool5 = paramString2.startsWith("/system/");
      boolean bool3 = paramString1.startsWith("/data/");
      bool1 = paramString2.startsWith("/data/");
      if ((!(bool6)) || (!(bool4)))
        if ((!(bool2)) || (!(bool5)))
          if ((!(bool3)) || (!(bool1)))
            if ((bool6) || (bool4) || (bool2) || (bool5) || (bool3) || (bool1))
              bool1 = false;
            else
              bool1 = true;
          else
            bool1 = true;
        else
          bool1 = true;
      else
        bool1 = true;
    }
    else
    {
      bool1 = false;
    }
    return bool1;
  }

  public static final boolean isOnSameServer(String paramString1, String paramString2)
  {
    boolean bool;
    if ((isRemotePath(paramString1)) && (isRemotePath(paramString2)))
    {
      String str1 = getHostName(paramString1);
      String str2 = getHostName(paramString2);
      if ((str1 != null) && (str2 != null))
        bool = str1.equals(str2);
      else
        bool = false;
    }
    else
    {
      bool = false;
    }
    return bool;
  }

  public static final boolean isRemotePath(String paramString)
  {
    boolean i = false;
    if (paramString != null)
      if ((paramString.startsWith("smb://")) || (paramString.startsWith("ftp://")) || (paramString.startsWith("bt://")) || (paramString.startsWith("fts://")))
        i = true;
      else
        i = false;
    else
      i = false;
    return i;
  }

  public static final boolean isRemoteRoot(String paramString)
  {
    boolean i = false;
    if (paramString != null)
      if ((paramString.equals("smb://")) || (paramString.equals("ftp://")) || (paramString.equals("bt://")) || (paramString.equals("fts://")))
        i = true;
      else
        i = false;
    else
      i = false;
    return i;
  }

  public static final boolean isSecurityFtpServer(String paramString)
  {
    boolean bool;
    if ((paramString != null) && (isFTPPath(paramString)))
      bool = paramString.startsWith("fts://");
    else
      bool = false;
    return bool;
  }

  public static final boolean isSmbPath(String paramString)
  {
    boolean bool;
    if (paramString != null)
      bool = paramString.startsWith("smb://");
    else
      bool = false;
    return bool;
  }
}