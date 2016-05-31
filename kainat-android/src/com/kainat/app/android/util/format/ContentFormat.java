package com.kainat.app.android.util.format;

public class ContentFormat {
    public static final byte VOICE_QCP_FIXED_HALF_RATE = 0;
    public static final byte VOICE_QCP_MAX_HALF_RATE = 1;
    public static final byte VOICE_QCP_FIXED_FULL_RATE = 2;
    public static final byte VOICE_QCP_MAX_FULL_RATE = 3;
    public static final byte VOICE_PCM_FORMAT = 4;
    public static final byte VOICE_MULAW_FORMAT = 5;
    public static final byte VOICE_AMR_FORMAT = 6;
    public static final byte VOICE_MS_GSM0610_FORMAT = 7;
    public static final byte VOICE_GSM0610_FORMAT = 8;
    public static final byte VOICE_UNKNOWN_CODEC = VOICE_GSM0610_FORMAT + 1;

    /*
     * Picture format
     */
    public static final byte PICTURE_JPEG = 1;
    public static final byte PICTURE_GIF = 2;
    
    /*
     * Text format
     */
    public static final byte TEXT_TXT = 1;
    public static final byte TEXT_RTF = 2;

    /*
     * Video Format
     */
    public static final byte VIDEO_3GP_FORMAT = 9;
    public static final byte VIDEO_MP4_FORMAT = 10;
}
