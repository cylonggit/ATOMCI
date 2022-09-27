package com.chen.crossTest.utils;
import com.chen.crossTest.mybatis.LabLogMapper;
import com.chen.crossTest.pojo.LabLog;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class MybatisUtil {
    public MybatisUtil() throws IOException {
    }

    //mysql
    public static SqlSession sqlSession =null;
    public static LabLogMapper logMapper=null;
    static {
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            sqlSession = sessionFactory.openSession();
            logMapper = sqlSession.getMapper(LabLogMapper.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void insertLog(LabLog log){
        logMapper.addLablog(log);
        sqlSession.commit();  //执行增删改是需手动提交数据
    }


}
