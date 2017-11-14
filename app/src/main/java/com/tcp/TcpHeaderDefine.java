package com.tcp;

public class TcpHeaderDefine {
    //PC->Phone数据头
    public static final short StartScan = 0x1001;
    public static final short HostStepData = 0x1002;
    public static final short GameOver = 0x1003;
    public static final short SendPreviewCommand = 0x1004;

    //Phone->PC数据头
    public static final short PhoneStepData = 0x2001;
    public static final short PhoneScanState = 0x2002;//0：未识别  1：识别但状态不正确  2：识别且状态正确
    public static final short PhonePreviewData = 0x2003;

    //文件信息 TODO：今后再丰富
    public static final String FileName = "FileName";
    public static final String BlackPlayerName = "BlackPlayerName";
    public static final String WhitePlayerName = "WhitePlayerName";

    //android消息
    public static final int NetConnected = 0;
}