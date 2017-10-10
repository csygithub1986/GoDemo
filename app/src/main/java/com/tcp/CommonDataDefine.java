package com.tcp;

public class CommonDataDefine {
    //PC->Phone数据头
    public static final int GameStart = 0x1001;
    public static final int GameOver = 0x1002;
    public static final int ServerStepData = 0x1003;
    public static final int Scan = 0x1004;
    public static final int SendPreview = 0x1005;

    //Phone->PC数据头
    public static final int PhoneStepData = 0x2001;
    public static final int PreviewData = 0x2002;

    //文件信息 TODO：今后再丰富
    public static final String FileName = "FileName";
    public static final String BlackPlayerName = "BlackPlayerName";
    public static final String WhitePlayerName = "WhitePlayerName";

    //android消息
    public static final int NetConnected = 0;
}