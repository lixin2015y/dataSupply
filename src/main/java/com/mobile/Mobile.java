package com.mobile;

import com.util.ExcelUtil;
import com.util.HadoopFileUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Mobile {

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

    //用户id上传至hive
    @Test
    public void test() throws IOException {
        HadoopFileUtil.uploadFileHdfs("魔百和账号3月.txt", "/test/lixin/userid/userid.txt");
    }

    //查询结果导出excel
    @Test
    public void test2() {
        String sql = "SELECT t1.userid as userid,t2.seconds as channelseconds,t3.seconds as vodseconds,t4.seconds as tvodseconds from lixin.mobile_userid t1 left join lixin.mobile_channel t2 on  t1.userid = t2.userid left join lixin.mobile_vod t3 on  t1.userid = t3.userid left join lixin.mobile_tvod t4 on t1.userid = t4.userid";
        final List<Map<String, Object>> maps = spark53.queryForList(sql);
        final List<String> keys = Arrays.asList("userid", "channelseconds", "vodseconds", "tvodseconds");
        final List<String> headers = Arrays.asList("userid", "直播收视秒", "点播收视秒", "回看收视秒");
        final Workbook workbook = ExcelUtil.exportExcelWithOutStyle(maps, keys, headers);
        try (OutputStream fileOut = new FileOutputStream("2020年3月份魔百盒收视.xlsx")) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
