package com.chen.crossTest.mybatis;

import com.chen.crossTest.pojo.LabLog;

import java.util.List;

/**
 * 
 * <pre>
 * 盒子清单-Box
 * </pre>
 * <small> 2022-03-27 23:39:03 | cyl</small>
 */
public interface LabLogMapper {
    //查询所有信息
    public List<LabLog> queryAll();

    //添加
    public void addLablog(LabLog labLog);

    //修改
    public boolean updateLablog(LabLog labLog);

    //根据id删除信息
    public void deleteById(Long id);

}
