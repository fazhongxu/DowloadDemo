package com.xxl.downloaddemo.bean;

import java.io.Serializable;

/**
 * Created by xxl on 2017/9/13.
 * 文件信息
 */

public class FileInfo implements Serializable {
    private int id;
    private String url;
    private int size;
    private String name;

    public FileInfo() {

    }

    public FileInfo(int id, String url, int size, String name) {
        this.id = id;
        this.url = url;
        this.size = size;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", size=" + size +
                ", name='" + name + '\'' +
                '}';
    }
}
