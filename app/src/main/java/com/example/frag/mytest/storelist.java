package com.example.frag.mytest;

import android.os.Environment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by frag on 2015/12/13.
 */
public class storelist {
    private final String mBuffPath;

    /**
     * @param buffPath
     *            存放缓存的路径
     * */
    public storelist(String buffPath) {
        mBuffPath = buffPath;
    }

    /**
     * @param list
     *            向本地写入的缓存数据
     * */
    public synchronized void write(ArrayList<String> list)
    {
        if (list == null ) {
            return;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +"/"+mBuffPath);
            ObjectOutput output = new ObjectOutputStream(fileOutputStream);
            output.writeObject(list);
            output.close();
            fileOutputStream.close();
        }catch(IOException R)
        {
            R.printStackTrace();
        }

    }

    /**
     * 读取缓存数据
     *
     * @return 缓存数据，数据为空时返回长度为0的list
     * */
    public synchronized ArrayList<String> read()
    {
        ArrayList<String> list=new ArrayList<String>();
        try {
            FileInputStream fileInputStream = new FileInputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +"/"+mBuffPath);
            ObjectInput input = new ObjectInputStream(fileInputStream);
            list = (ArrayList<String>) input.readObject();
            fileInputStream.close();
            input.close();
        }catch (IOException E)
        {
            E.printStackTrace();
        }
        catch (ClassNotFoundException E)
        {
            E.printStackTrace();
        }
        return list;
    }
}
