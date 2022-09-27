package com.chen.crossTest.mybatis;

import com.chen.crossTest.pojo.Test;

import java.util.List;

/**
 * 
 * <pre>
 * 盒子清单-Box
 * </pre>
 * <small> 2022-03-27 23:39:03 | cyl</small>
 */
public interface TestMapper {
    //查询所有信息
    public List<Test> queryAll();

    //添加
    public void add(Test test);

    //修改
    public boolean update(Test test);

    //根据id删除信息
    public void deleteById(Long id);

}
