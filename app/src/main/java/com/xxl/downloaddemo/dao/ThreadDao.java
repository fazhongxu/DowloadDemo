package com.xxl.downloaddemo.dao;

import com.xxl.downloaddemo.bean.ThreadInfo;

import java.util.List;

/**
 * Created by xxl on 2017/9/13.
 */

public interface ThreadDao {
    /**
     * 插入线程信息
     */
    void insertThreadInfo(ThreadInfo threadInfo);

    /**
     * 删除线程信息
     */
    void deleteThreadInfo(String url);

    /**
     * 更新线程信息
     */
    void updateThreadInfo(int id,String url,int finished);

    /**
     * 查询线程信息
     */
    List<ThreadInfo> selectThreadInfo(String url);

    /**
     * 查询线程信息是否存在
     */
    boolean threadExist(String url);
}
