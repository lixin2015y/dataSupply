package com.biaoqingzhuangaoqing;

import com.util.DataConvert;
import com.util.ExcelUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public class BiaoQingToGaoQing {

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

    //标清转高清excel导入oracle
    @Test
    public void test() throws FileNotFoundException {
        //读取文件
        FileInputStream inputStream = new FileInputStream(new File("3月智能机顶盒换机明细.xlsx"));
        ExcelUtil excelUtil = new ExcelUtil();
        final List<Map<String, Object>> maps = excelUtil.readExcel(inputStream, "yyyyMMdd HH:mm:ss");
        maps.remove(0);
        String[] keys = new String[]{"订单号","业务号码","工号","业务受理时间","fee_name","金额","fee_mode(3为延伸服务进话单模式，0为不进话单模式，4为政企延伸服务)"};

        //插入数据库
        final List<Object[]> data = DataConvert.listMapToObjectArray(maps, keys);

        jdbcTemplate.batchUpdate("insert into dams.BIAOQINGTOGAOQING(ORDERNUM,USERID,JOBNUMBER,SERVICETIME,FREENAME,PRICE,FREEMODE)  values(?,?,?,TO_DATE(?,'yyyy-mm-dd hh24:mi:ss'),?,?,?)", data);

    }


}
