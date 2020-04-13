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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VodCol {

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

    //点播栏目收视csv导入oracle
    @Test
    public void test() {
        //读取文件
        List<Object[]> data = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("cms_waibu_size1.csv"));
            reader.readLine();
            String line = null;
            int num = 1;
            while ((line = reader.readLine()) != null) {
                String item[] = (line.replace("\"", "") + "," + num).split(",");
                data.add(item);
                num++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        data.remove(0);

        //插入数据库
        jdbcTemplate.batchUpdate("insert into dams.tmp_col(colid,url,num)  values(?,?,?)", data);

    }

    @Test
    public void test2() throws IOException {
        final List<Map<String, Object>> maps = jdbcTemplate.queryForList("select colid,url,num from dams.tmp_col");
        List<String> lines = new ArrayList();
        String[] keys = new String[]{"COLID", "URL", "NUM"};
        maps.stream().forEach(map -> {
            lines.add(HadoopFileUtil.getLine(map, keys));
        });
        HadoopFileUtil.WriteToHDFS("hdfs://10.0.9.53:8020/test/lixin/vod_tmp/vod", lines);
    }

}
