package com.kainat.app.android.common;

public class TypeUtils
{
  public static final int ANDROID_APPLICATION = 65536;
  public static final int IMAGE_BMP = 65554;
  public static final int IMAGE_GIF = 65555;
  public static final int IMAGE_JPEG = 65556;
  public static final int IMAGE_JPG = 65553;
  public static final int IMAGE_PNG = 65552;
  public static final int MEDIA_3GP = 65578;
  public static final int MEDIA_AMR = 65571;
  public static final int MEDIA_ASF = 65575;
  public static final int MEDIA_AVI = 65579;
  public static final int MEDIA_M4A = 65573;
  public static final int MEDIA_MIDI = 65569;
  public static final int MEDIA_MP3 = 65568;
  public static final int MEDIA_MP4 = 65580;
  public static final int MEDIA_MPEG = 65582;
  public static final int MEDIA_MPG = 65581;
  public static final int MEDIA_OGG = 65572;
  public static final int MEDIA_REALMEDIA = 65577;
  public static final int MEDIA_WAV = 65570;
  public static final int MEDIA_WMA = 65574;
  public static final int MEDIA_WMV = 65576;
  public static final int PDF_FILE = 65593;
  public static final int PLAIN_TEXT = 65537;
  public static final int WIN_BAT = 65587;
  public static final int WIN_CHM = 65600;
  public static final int WIN_DLL = 65589;
  public static final int WIN_DOC = 65584;
  public static final int WIN_EXCEL = 65585;
  public static final int WIN_EXE = 65588;
  public static final int WIN_HTML = 65586;
  public static final int WIN_LIB = 65590;
  public static final int WIN_PPT = 65591;
  public static final int XML_FILE = 65594;
  public static final int ZIP_FILE = 65592;

  public static final String getApkSuffix()
  {
    return new String(".apk");
  }

  public static final int getFileType(String paramString)
  {
    int i;
    if (paramString != null)
    {
      if (paramString.charAt(paramString.length() - 1) == '/')
        paramString = paramString.substring(0, paramString.length() - 1);
      String str = paramString.toLowerCase();
      if (!(str.endsWith(".apk")))
        if (!(str.endsWith(".png")))
          if (!(str.endsWith(".jpg")))
            if (!(str.endsWith(".bmp")))
              if (!(str.endsWith(".gif")))
                if (!(str.endsWith(".jpeg")))
                  if (!(str.endsWith(".mp3")))
                    if (!(str.endsWith(".mp4")))
                      if (!(str.endsWith(".avi")))
                        if ((!(str.endsWith(".mid"))) && (!(str.endsWith(".midi"))))
                          if (!(str.endsWith(".wmv")))
                            if (!(str.endsWith(".wav")))
                              if (!(str.endsWith(".asf")))
                                if (!(str.endsWith(".mpg")))
                                  if (!(str.endsWith(".mpeg")))
                                    if ((!(str.endsWith(".3gp"))) && (!(str.endsWith(".3gpp"))))
                                      if (!(str.endsWith(".amr")))
                                        if ((!(str.endsWith(".rm"))) && (!(str.endsWith(".rmvb"))))
                                          if ((!(str.endsWith(".ogg"))) && (!(str.endsWith(".x-ogg"))))
                                            if ((!(str.endsWith(".m4a"))) && (!(str.endsWith(".aac"))))
                                              if (!(str.endsWith(".wma")))
                                                if ((!(str.endsWith(".doc"))) && (!(str.endsWith(".docx"))))
                                                  if ((!(str.endsWith(".ppt"))) && (!(str.endsWith(".pps"))) && (!(str.endsWith(".ppx"))) && (!(str.endsWith(".pptx"))) && (!(str.endsWith(".odp"))))
                                                    if ((!(str.endsWith(".xls"))) && (!(str.endsWith(".xlsx"))) && (!(str.endsWith(".xla"))) && (!(str.endsWith(".xlc"))) && (!(str.endsWith(".xlm"))) && (!(str.endsWith(".xlt"))) && (!(str.endsWith(".xlsm"))) && (!(str.endsWith(".xlsb"))))
                                                      if (!(str.endsWith(".chm")))
                                                        if ((!(str.endsWith(".html"))) && (!(str.endsWith(".htm"))))
                                                          if (!(str.endsWith(".bat")))
                                                            if (!(str.endsWith(".exe")))
                                                              if (!(str.endsWith(".dll")))
                                                                if (!(str.endsWith(".lib")))
                                                                  if ((!(str.endsWith(".txt"))) && (!(str.endsWith(".text"))) && (!(str.endsWith(".ini"))) && (!(str.endsWith(".properties"))) && (!(str.endsWith(".prop"))) && (!(str.endsWith(".xml"))) && (!(str.endsWith(".conf"))) && (!(str.endsWith(".classpath"))) && (!(str.endsWith(".project"))))
                                                                    if ((!(str.endsWith(".zip"))) && (!(str.endsWith(".tar"))) && (!(str.endsWith(".gz"))) && (!(str.endsWith(".rar"))) && (!(str.endsWith(".cab"))))
                                                                      if (!(str.endsWith(".xml")))
                                                                        if (!(str.endsWith(".pdf")))
                                                                          i = -1;
                                                                        else
                                                                          i = 65593;
                                                                      else
                                                                        i = 65594;
                                                                    else
                                                                      i = 65592;
                                                                  else
                                                                    i = 65537;
                                                                else
                                                                  i = 65590;
                                                              else
                                                                i = 65589;
                                                            else
                                                              i = 65588;
                                                          else
                                                            i = 65587;
                                                        else
                                                          i = 65586;
                                                      else
                                                        i = 65600;
                                                    else
                                                      i = 65585;
                                                  else
                                                    i = 65591;
                                                else
                                                  i = 65584;
                                              else
                                                i = 65574;
                                            else
                                              i = 65573;
                                          else
                                            i = 65572;
                                        else
                                          i = 65577;
                                      else
                                        i = 65571;
                                    else
                                      i = 65578;
                                  else
                                    i = 65582;
                                else
                                  i = 65581;
                              else
                                i = 65575;
                            else
                              i = 65570;
                          else
                            i = 65576;
                        else
                          i = 65569;
                      else
                        i = 65579;
                    else
                      i = 65580;
                  else
                    i = 65568;
                else
                  i = 65556;
              else
                i = 65555;
            else
              i = 65554;
          else
            i = 65553;
        else
          i = 65552;
      else
        i = 65536;
    }
    else
    {
      i = -1;
    }
    return i;
  }

  public static final String getImageFileSuffix()
  {
    return new String(".jpg;.jpeg;.png;.bmp;.gif");
  }

  public static final String getMediaFileSuffix()
  {
    return new String(".mp3;.mp4;.3gp;.3gpp;.amr;.mid;.midi;.wmv;.wma;.ogg;.x-ogg;.wav;.m4a;.aac;.rm;.rmvb;.avi;.mpg;.mpeg;.asf");
  }

  public static final String getZipSuffix()
  {
    return new String(".zip;.rar;.tar;.gzip;.bz2;.gz;.7z;");
  }

  public static final boolean isAndroidApp(String paramString)
  {
    boolean i = false;
    if (getFileType(paramString) != 65536)
      i = false;
    else
      i = true;
    return true;
  }

  public static final boolean isAudioFile(String paramString)
  {
    int i = getFileType(paramString);
    if ((i < 65568) || (i > 65574))
      return false;
    else
    	return true;
  }

  public static final boolean isChmFile(String paramString)
  {
    boolean i = false;
    if (getFileType(paramString) != 65600)
      i = false;
    else
      i = true;
    return i;
  }

  public static final boolean isExcelFile(String paramString)
  {
    int i;
    if (getFileType(paramString) != 65585)
      return false;
    else
    	return true;
  }

  public static final boolean isGzFile(String paramString)
  {
    int i;
    if (paramString != null)
    {
      if (paramString.charAt(paramString.length() - 1) == '/')
        paramString = paramString.substring(0, paramString.length() - 1);
      String str = paramString.toLowerCase();
      if ((!(str.endsWith(".gzip"))) && (!(str.endsWith(".gz"))))
    	  return false;
      else
    	  return true;
    }
    else
    {
    	return false;
    }
    
  }

  public static final boolean isHtmlFile(String paramString)
  {
    int i;
    if (getFileType(paramString) != 65586)
    	return false;
    else
    	return true;
  }

  public static final boolean isImageFile(String paramString)
  {
    int i = getFileType(paramString);
    if ((i < 65552) || (i > 65556))
    	return false;
    else
    	return true;
  }

  public static final boolean isJpegFile(String paramString)
  {
    int i;
    if ((getFileType(paramString) == 65556) || (getFileType(paramString) == 65553))
    	return true;
    else
    	return false;
  }

  public static final boolean isMediaFile(String paramString)
  {
    int i = getFileType(paramString);
    if ((i < 65568) || (i > 65582))
    	return false;
    else
    	return true;
  }

  public static final boolean isOggMediaFile(String paramString)
  {
    int i;
    if (getFileType(paramString) != 65572)
    	return false;
    else
    	return true;
  }

  public static final boolean isPdfFile(String paramString)
  {
    int i;
    if (getFileType(paramString) != 65593)
    	return false;
    else
    	return true;
  }

  public static final boolean isPngFile(String paramString)
  {
    int i;
    if (getFileType(paramString) != 65552)
    	return false;
    else
    	return true;
  }

  public static final boolean isPptFile(String paramString)
  {
    int i;
    if (getFileType(paramString) != 65591)
    	return false;
    else
    	return true;
  }

  public static boolean isTarFile(String paramString)
  {
    boolean bool;
    if (paramString != null)
      bool = paramString.endsWith(".tar");
    else
      bool = false;
    return bool;
  }

  public static final boolean isTextFile(String paramString)
  {
    int i;
    if (getFileType(paramString) != 65537)
    	return false;
    else
    	return true;
  }

  public static final boolean isUnsupportedMediaTypes(String paramString)
  {
    int i;
    if ((!(paramString.endsWith("rm"))) && (!(paramString.endsWith("rmvb"))) && (!(paramString.endsWith("avi"))) && (!(paramString.endsWith(".mpg"))) && (!(paramString.endsWith(".mpeg"))) && (!(paramString.endsWith(".asf"))) && (!(paramString.endsWith(".flv"))))
    	return false;
    else
    	return true;
  }

  public static final boolean isVideoFile(String paramString)
  {
    int i = getFileType(paramString);
    if ((i < 65575) || (i > 65582))
    	return false;
    else
    	return false;
  }

  public static final boolean isWordFile(String paramString)
  {
    int i;
    if (getFileType(paramString) != 65584)
    	return false;
    else
    	return true;
  }

  public static final boolean isXmlFile(String paramString)
  {
    int i;
    if (getFileType(paramString) != 65594)
    	return false;
    else
    	return true;
  }

  public static final boolean isZipFile(String paramString)
  {
    int i;
    if (getFileType(paramString) != 65592)
    	return false;
    else
    	return true;
  }

  public static final boolean isZipFile_Current(String paramString)
  {
    int i;
    if (paramString != null)
    {
      if (paramString.charAt(paramString.length() - 1) == '/')
        paramString = paramString.substring(0, paramString.length() - 1);
      if (!(paramString.toLowerCase().endsWith(".zip")))
    	  return false;
      else
    	  return true;
    }
    else
    {
    	return false;
    }
    //return i;
  }
}