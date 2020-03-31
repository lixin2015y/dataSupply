package com.cancelorderpayment;

import com.util.DataConvert;
import com.util.ExcelUtil;
import com.util.HadoopFileUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CancelOrderPayment {

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


    /**
     * 将excel导入oracle
     *
     * @throws FileNotFoundException
     */
    @Test
    public void test1() throws FileNotFoundException {
        //读取文件
        FileInputStream inputStream = new FileInputStream(new File("截至到3月29日天津广电第三方的退费汇总.xlsx"));
        ExcelUtil excelUtil = new ExcelUtil();
        final List<Map<String, Object>> maps = excelUtil.readExcel(inputStream, "yyyyMMdd HH:mm:ss");
        maps.remove(0);
        maps.stream().forEach(map -> map.put("账期", "202003"));
        String[] keys = new String[]{"立单日期", "省", "市", "客户名称", "退费账号", "投诉号码", "广电还是中心", "业务代码", "业务名称", "PRM代码", "业务开始账期", "业务金额", "退费日期", "退费状态", "退费人", "实际到账账号", "类型", "账期"};

        //插入数据库
        final List<Object[]> data = DataConvert.listMapToObjectArray(maps, keys, "退费日期");

        jdbcTemplate.batchUpdate("insert into dams.CANCELORDERPAYMENT_TMP(LIDAN,SHENG,SHI,USERNAME,USERID,COMPLAINID,IPTV,PRODUCTID,PRODUCTNAME,PRM,SERVICEBEGINTIME,PRICE,CANCELTIME,CANCELPATH,CANCELPERSON,ACOUNT,TYPE, MONTH)  values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", data);

    }

    /**
     * 将excel文件导入hdfs
     */
    @Test
    public void test2() throws IOException {
        String[] keys = new String[]{"立单日期", "省", "市", "客户名称", "退费账号", "投诉号码", "广电还是中心", "业务代码", "业务名称", "PRM代码", "业务开始账期", "业务金额", "退费日期", "退费状态", "退费人", "实际到账账号", "类型"};

        HadoopFileUtil.DeleteHDFSFile("hdfs://10.0.9.53:8020/test/lixin/cancelpayment_tmp/202003");
        //读取文件
        FileInputStream inputStream = new FileInputStream(new File("截至到3月29日天津广电第三方的退费汇总.xlsx"));
        ExcelUtil excelUtil = new ExcelUtil();
        final List<Map<String, Object>> maps = excelUtil.readExcel(inputStream, "yyyyMMdd HH:mm:ss");
        List<String> lines = new ArrayList();
        maps.remove(0);
        maps.stream().forEach(map -> {
            lines.add(getLine(map, keys, "202003"));
        });
        HadoopFileUtil.WriteToHDFS("hdfs://10.0.9.53:8020/test/lixin/cancelpayment_tmp/202003", lines);
    }


    /**
     * 查询结果并导入到oracle
     */
    @Test
    public void test3() {
        String sql = "SELECT t11.userid,t11.productid,t11.productname,\n" +
                "CASE WHEN MAX(t22.starttime) IS NULL THEN '否' ELSE '是' END AS rating,202003 AS MONTH,\n" +
                "MAX(t22.starttime) as starttime\n" +
                "FROM (\n" +
                "  SELECT t1.userid,t1.productid,t1.canceltime,t1.productname FROM \n" +
                "  (SELECT userid,productid,canceltime,productname FROM lixin.cancelpayment_tmp WHERE MONTH = 202003) t1 \n" +
                "  LEFT JOIN lixin.channelordername t2 \n" +
                "  ON (t1.productid = t2.productid) WHERE t2.channelid IS NULL\n" +
                ")t11\n" +
                "LEFT JOIN (SELECT userid,sp,seconds,ptime,starttime FROM iptv.voduserparquet WHERE ptime >= 20200301) t22\n" +
                "ON (t11.userid = t22.userid AND t22.sp LIKE concat('%',t11.productid,'%') AND t22.ptime > t11.canceltime)\n" +
                "GROUP BY t11.userid,t11.productid,t11.productname";

        final List<Map<String, Object>> maps = spark53.queryForList(sql);
        System.out.println(maps);
        String[] keys = new String[]{"userid", "productid", "productname", "rating", "month","starttime"};
        final List<Object[]> objects = DataConvert.listMapToObjectArray(maps, keys);
        jdbcTemplate.batchUpdate("insert into dams.cancelorderpayment_result_temp(userid,productid,productname,rating,month,starttime) values (?,?,?,?,?,?)", objects);

    }


    private String getLine(Map map, String[] key, String month) {
        StringBuilder line = new StringBuilder();
        Arrays.stream(key).forEach(v -> {
            line.append(map.containsKey(v) ?
                    (v.equals("退费日期") ? map.get(v).toString().split(" ")[0].replace("-", "") + '|' : map.get(v).toString() + '|')
                    : '|');
        });
        line.append(month);
        return line.toString() + "\n";
    }

    private String getKongLingShu(Map map, String[] key, String month) {
        StringBuilder line = new StringBuilder();
        Arrays.stream(key).forEach(v -> {
            line.append(map.containsKey(v) ?
                    (v.equals("退费日期") ? map.get(v).toString().replace(" ", "").replace(":", "").replace("-", "").split("\\.")[0] + '|' : map.get(v).toString() + '|')
                    : '|');
        });
        line.append(month);
        return line.toString() + "\n";
    }


    @Test
    public void konglingshu() throws IOException {
        String[] keys = new String[]{"立单日期", "省", "市", "客户名称", "退费账号", "投诉号码", "广电还是中心", "业务代码", "业务名称", "PRM代码", "业务开始账期", "业务金额", "退费日期", "退费状态", "退费人", "实际到账账号", "类型"};

        HadoopFileUtil.DeleteHDFSFile("hdfs://10.0.9.53:8020/test/lixin/cancelorderpayment/202003");
        //读取文件
        FileInputStream inputStream = new FileInputStream(new File("截至到3月29日天津广电第三方的退费汇总.xlsx"));
        ExcelUtil excelUtil = new ExcelUtil();
        final List<Map<String, Object>> maps = excelUtil.readExcel(inputStream, "yyyyMMdd HH:mm:ss");
        List<String> lines = new ArrayList();
        maps.remove(0);
        String[] keys2 = new String[]{"退费账号","业务代码","业务金额","退费日期"};
        maps.stream().forEach(map -> {
            lines.add(getKongLingShu(map, keys2, "202003"));
        });
        maps.remove(0);
        System.out.println(lines);
        HadoopFileUtil.WriteToHDFS("hdfs://10.0.9.53:8020/test/lixin/cancelorderpayment/202003", lines);
    }


}
