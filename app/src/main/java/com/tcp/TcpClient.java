package com.tcp;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.*;
import java.net.Socket;

public class TcpClient {
    private static TcpClient m_Instance;

    private TcpClient() {
    }

    public static TcpClient getInstance() {
        if (m_Instance == null) {
            m_Instance = new TcpClient();
        }
        return m_Instance;
    }

    private static final String HOST = "192.168.1.111";
    private static final int PORT = 12121;
    private Socket socket = null;

    private DataInputStream dis = null;
    private DataOutputStream dos = null;

    private Handler m_Handler = null;

    public void setHandler(Handler handler) {
        m_Handler = handler;
    }

    /**
     * 同步Start
     */
    public void start() {
        try {
            socket = new Socket(HOST, PORT);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            m_Handler.sendEmptyMessage(TcpHeaderDefine.NetConnected);
        } catch (IOException ex) {
            ex.printStackTrace();
//            ShowDialog("login exception" + ex.getMessage());
        }
        //启动线程，接收服务器发送过来的数据
        new Thread(new DataListener()).start();
    }

    public void Send(byte[] data) {
        try {
            if (dos == null || socket.isClosed())
                return;
            dos.write(data);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class DataListener implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    if (!socket.isClosed()) {
                        if (socket.isConnected()) {
                            if (!socket.isInputShutdown()) {

                                byte[] intBytes = new byte[4];
                                int x = dis.read(intBytes);

                                if (x == -1) {
                                    Log.i("debug", "返回-1");
                                    return;
                                }

                                int command = FormatTransfer.lBytesToInt(intBytes);
                                if (command == TcpHeaderDefine.GameStart) {
                                    dis.read(intBytes);
                                    int len = FormatTransfer.lBytesToInt(intBytes);
                                    byte[] msgBytes = new byte[len];
                                    dis.read(msgBytes);
                                    String msg = new String(msgBytes, "utf-8");
                                    if (m_Handler != null) {
                                        Message message = new Message();
                                        message.what = TcpHeaderDefine.GameStart;
                                        message.obj = msg;
                                        m_Handler.sendMessage(message);
                                    }
                                } else if (command == TcpHeaderDefine.GameOver) {


                                } else if (command == TcpHeaderDefine.Scan) {

                                } else if (command == TcpHeaderDefine.SendPreview) {

                                } else if (command == TcpHeaderDefine.HostStepData) {

                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
