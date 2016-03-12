package com.example.frag.mytest;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by frag on 2015/12/2.
 */
public class client implements Runnable {
    private Socket cli;
    private Handler handler;
    public Handler myhandler;
    BufferedReader is;
    OutputStream os;
    public client(Handler lt)
    {
        handler=lt;
    }
    public void run(){
        try
        {

            System.out.println("开始连接");
            cli = new Socket("222.205.16.41",2222);
            System.out.println("连接上了");
            is = new BufferedReader(new InputStreamReader(cli.getInputStream(), "utf-8"));
            os = cli.getOutputStream();
            new Thread() {
                @Override
                public void run() {
                    String content = null;
                    try {
                        while ((content = is.readLine()) != null) {
                            Message msg = new Message();
                            msg.what = 0x123;
                            msg.obj = content;
                            handler.sendMessage(msg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            Looper.prepare();
            myhandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0x345) {
                        try {
                            os.write((msg.obj.toString()+ "\r\n").getBytes("utf-8"));
                            os.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Looper.loop();
        } catch (SocketTimeoutException e1) {
            System.out.println("网络连接超时！！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

