package com.vodCol;

import com.util.DataConvert;
import com.util.ExcelUtil;
import com.util.HadoopFileUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class VodCol2 {

    JdbcTemplate jdbcTemplate;

    JdbcTemplate hive53;

    JdbcTemplate spark53;

    @Before
    public void loadConfig() {
        ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
        jdbcTemplate = context.getBean("jdbcTemplate", JdbcTemplate.class);
        hive53 = context.getBean("hive53", JdbcTemplate.class);
        spark53 = context.getBean("spark53", JdbcTemplate.class);
    }


    @Test
    public void test() throws FileNotFoundException {

        jdbcTemplate.execute("truncate table dams.VOD_COL_TMP");

        final File file = new File("海外大片(20001110000000000000000000000343)20200413152150.xls");
        FileInputStream inputStream = new FileInputStream(file);
        ExcelUtil excelUtil = new ExcelUtil();
        final List<Map<String, Object>> maps = excelUtil.readExcel(inputStream, "yyyyMMdd HH:mm:ss", "栏目内容");
        maps.remove(0);
        String[] keys = new String[]{"顺序号", "内容ID", "内容名称", "内容类型", "套餐名称", "片商", "绑定时间", "导演", "演员", "子集数/总集数", "栏目名称", "栏目ID", "栏目路径"};

        //插入数据库
        final List<Object[]> data = DataConvert.listMapToObjectArray(maps, keys);

        System.out.println(Arrays.toString(data.get(0)));
        jdbcTemplate.batchUpdate("insert into dams.VOD_COL_TMP(NUMERID,CONTENTID,contentname,CONTENTTYPE,SPNAME,CPNAME,BINDTIME,DIRECTOR,ACTOR,ZIJI,COLNAME,COLID,COLPATH)  values(?,?,?,?,?,?,?,?,?,?,?,?,?)", data);


    }

    @Test
    public void test2() throws FileNotFoundException {

        jdbcTemplate.execute("truncate table dams.VOD_COL_TMP");

        final File file = new File("全部电影(20001110000000000000000000000482)20200413152030.xls");
        FileInputStream inputStream = new FileInputStream(file);
        ExcelUtil excelUtil = new ExcelUtil();
        final List<Map<String, Object>> maps = excelUtil.readExcel(inputStream, "yyyyMMdd HH:mm:ss", "系列剧子集");
        maps.remove(0);
        String[] keys = new String[]{"系列剧名","序号","子集ID","子集名称","套餐","片商","绑定时间"};

        //插入数据库
        final List<Object[]> data = DataConvert.listMapToObjectArray(maps, keys);

        System.out.println(Arrays.toString(data.get(0)));
        jdbcTemplate.batchUpdate("insert into dams.VOD_COL_TMP(contentname,NUMERID,ziji,contenttype,SPNAME,CPNAME,BINDTIME)  values(?,?,?,?,?,?,?)", data);


    }


}
