package com.datasupply;

import org.junit.Test;

public class DataSupply {

    LocalDateUtil dateUtil = new LocalDateUtil();

    @Test
    public void CuccC3() {
        String beginTime = "2018-09-01";
//        String endTime = "20180923";
        String endTime = "2018-09-23";

        dateUtil.getDayList(beginTime, endTime, "yyyy-MM-dd", "yyyy-MM-dd").stream().forEach(
                time -> {
                    String yesterday = dateUtil.minusDay(time, 1, "yyyy-MM-dd", "yyyyMMdd");
                    String deleteSql = "DELETE FROM dams.USERINFO_C3_LAST_HISTORY_NEW WHERE UPDATETIME = TO_DATE('" + time + "', 'yyyy-mm-dd')";
                    DBUtil.excute(deleteSql);
                    String insetSql = "insert into dams.USERINFO_C3_LAST_HISTORY_NEW select userid, time, status, sourcetype, TO_DATE('" + time + "', 'yyyy-mm-dd') as updatetime " +
                            "from (select userid,time,status,sourcetype, ROW_NUMBER() OVER(PARTITION BY userid ORDER BY time DESC, status) AS ROWNUMBER from " +
                            "(select userid,time,status,sourcetype from dams.USERINFO_C3_TOTAL where updateTIME = to_date('" + yesterday + "', 'yyyy-mm-dd')union all " +
                            "select userid,time,status,sourcetype from dams.USERINFO_C3_LAST_HISTORY_NEW where updateTIME = to_date('" + yesterday + " ', 'yyyy-mm-dd') " +
                            ")) t where t.ROWNUMBER = 1";
                    DBUtil.excute(insetSql);
                }
        );
    }


    @Test
    public void CuccArea() {
        String beginTime = "2020-01-01";
//        String endTime = "2020-03-02";
        String endTime = "2020-03-09";

        dateUtil.getDayList(beginTime, endTime, "yyyy-MM-dd", "yyyy-MM-dd").stream().forEach(
                time -> {
                    String yesterday = dateUtil.minusDay(time, 1, "yyyy-MM-dd", "yyyyMMdd");
                    String deleteSql = "DELETE FROM  damsolap.AREA_USER_LAST_HISTORY_CUCC1 WHERE lasttime = TO_DATE('" + time + "','yyyy-mm-dd')";
                    DBUtil.excute(deleteSql);
                    String insertSql = "insert into damsolap.AREA_USER_LAST_HISTORY_CUCC1 SELECT USERID,AREAID,STBTYPE,LOGININFO, TO_DATE('" + time + "', 'yyyy-mm-dd') FROM " +
                            "(SELECT USERID, AREAID, STBTYPE, LOGININFO, ROW_NUMBER () OVER ( PARTITION BY USERID, AREAID ORDER BY LOGININFO DESC ) AS NUM " +
                            "  FROM (SELECT USERID, AREAID, STBTYPE, LOGININFO FROM( " +
                            "  SELECT USERID, AREAID, STBTYPE, LOGININFO FROM  damsolap.AREA_USER_LAST_HISTORY_CUCC1 where lasttime = TO_DATE('" + yesterday + "', 'yyyy-mm-dd') " +
                            "  UNION ALL " +
                            "  SELECT USERID, AREAID, STBTYPE, LOGININFO FROM damsolap.area_user_total  " +
                            "  WHERE  ptime BETWEEN TO_DATE('" + time + "', 'YYYY-MM-DD') AND TO_DATE('" + time + "', 'YYYY-MM-DD') AND areaid != 'error'" +
                            "))T) A WHERE A.NUM = 1";
                    DBUtil.excute(insertSql);
                }
        );
    }

    @Test
    public void CuccEpg() {
        String beginTime = "20200101";
//        String endTime = "2019-12-17";
        String endTime = "20200313";


        dateUtil.getDayList(beginTime, endTime, "yyyyMMdd", "yyyyMMdd").stream().forEach(
                time -> {
                    String yesterday = dateUtil.minusDay(time, 1, "yyyyMMdd", "yyyyMMdd");
//                    String deleteSql = "DELETE FROM dams.USERINFO_EPG_LAST_HISTORY_NEW WHERE TIME = TO_DATE('" + time + "','yyyymmdd')";
//                    DBUtil.excute(deleteSql);
                    String insertSql = "insert into dams.USERINFO_EPG_LAST_HISTORY_NEW  " +
                            "SELECT TO_DATE('" + time + "', 'yyyymmdd') AS time, userid, MAX(logintime) AS logintime, type, MAX(state) AS state, MAX(version) AS version " +
                            "FROM (" +
                            "  SELECT userid, logintime, type, state, version " +
                            "  FROM dams.USERINFO_EPG t " +
                            "  WHERE logintime >= to_date('" + time + " 00:00:00', 'YYYYMMDD hh24:mi:ss') " +
                            "    AND logintime <= to_date('" + time + " 23:59:59', 'YYYYMMDD hh24:mi:ss') " +
                            "  UNION ALL " +
                            "  SELECT userid, logintime, type, state, version " +
                            "  FROM dams.USERINFO_EPG_LAST_HISTORY_NEW WHERE time = to_date('" + yesterday + "', 'YYYYMMDD') " +
                            ") " +
                            "GROUP BY userid, type";
                    DBUtil.excute(insertSql);
                }
        );
    }


    @Test
    public void CuccActive() {
        String beginTime = "20200101";
        String endTime = "20200314";
        dateUtil.getDayList(beginTime, endTime, "yyyyMMdd", "yyyyMMdd").stream().forEach(
                time -> {
                    String yesterday = dateUtil.minusDay(time, 1, "yyyyMMdd", "yyyyMMdd");
//                    String deleteSql = "DELETE FROM dams.USERINFO_ACTIVE_LAST_HISTORY_NEW1 WHERE TIME = TO_DATE('" + time + "','yyyymmdd')";
//                    DBUtil.excute(deleteSql);
                    String insertSql = "INSERT INTO dams.userinfo_active_last_history1 " +
                            "SELECT TO_DATE('" + time + "','yyyymmdd') AS time, userid, MAX(logintime) AS logintime, type,  MAX(state) AS state, MAX(version) AS version " +
                            "FROM ( " +
                            "  SELECT userid, logintime, type, state, version " +
                            "  FROM dams.USERINFO_ACTIVE t " +
                            "  WHERE logintime >= to_date('" + time + " 00:00:00', 'YYYYMMDD hh24:mi:ss') " +
                            "  AND logintime <= to_date('" + time + " 23:59:59', 'YYYYMMDD hh24:mi:ss') " +
                            "  UNION ALL " +
                            "  SELECT userid, logintime, type, state, VERSION " +
                            "  FROM dams.userinfo_active_last_history1 WHERE TIME = TO_DATE('" + yesterday + "','yyyymmdd') " +
                            ") " +
                            "GROUP BY userid, TYPE";
                    DBUtil.excute(insertSql);
                }
        );

    }

    @Test
    public void CuccStb() {

        String beginTime = "20180901";
//        String endTime = "20191231";
        String endTime = "20181231";
        dateUtil.getDayList(beginTime, endTime, "yyyyMMdd", "yyyyMMdd").stream().forEach(
                time -> {
//                    String deleteSql = "DELETE FROM dams.USERINFO_ACTIVE_LAST_HISTORY_NEW1 WHERE TIME = TO_DATE('" + time + "','yyyymmdd')";
//                    DBUtil.excute(deleteSql);
                    String insertSql = "INSERT INTO DAMS.USERINFO_STB_LAST_HISTORY " +
                            "SELECT T1.USERID,CASE WHEN T2.TYPE IS NULL THEN T1.TYPE ELSE T2.TYPE END AS TYPE,to_date('" + time + "','yyyymmdd') as time " +
                            "FROM(  " +
                            "(SELECT USERID,TYPE FROM (SELECT USERID,TYPE, ROW_NUMBER() OVER(PARTITION BY USERID ORDER BY LOGINTIME DESC) AS RN FROM DAMS.userinfo_active_last_history where time = TO_date('" + time + "','yyyymmdd') " +
                            ") WHERE RN = 1 ) T1 " +
                            "LEFT JOIN " +
                            "(SELECT USERID,TYPE FROM (SELECT USERID,TYPE, ROW_NUMBER() OVER(PARTITION BY USERID ORDER BY LOGINTIME DESC) AS RN FROM DAMS.userinfo_epg_last_history where time = TO_date('" + time + "','yyyymmdd') " +
                            ") WHERE RN = 1 ) T2 " +
                            "ON (T1.USERID = T2.USERID) " +
                            ")UNION ALL " +
                            "SELECT USERID,TYPE,to_date('" + time + "','yyyymmdd') as time FROM DAMS.userinfo_epg_last_history where time = TO_date('" + time + "','yyyymmdd') and USERID IN  " +
                            "(SELECT USERID FROM DAMS.userinfo_epg_last_history where time = TO_date('" + time + "','yyyymmdd') MINUS SELECT USERID FROM DAMS.userinfo_active_last_history where time = TO_date('" + time + "','yyyymmdd')) ";
                    DBUtil.excute(insertSql);
                }
        );
    }

    @Test
    public void CmccCtccStb() {
        String beginTime = "20190302";
        String endTime = "20191231";
//        String endTime = "20190301";
        dateUtil.getDayList(beginTime, endTime, "yyyyMMdd", "yyyyMMdd").stream().forEach(
                time -> {
                    String insertSql = "INSERT INTO IPTV.USERINFO_STB_LAST_HISTORY " +
                            "SELECT T1.USERID, CASE WHEN T2.TYPE IS NULL THEN T1.TYPE ELSE T2.TYPE END AS TYPE, to_date('"+time+"','yyyymmdd') as time " +
                            "FROM( (SELECT USERID,TYPE FROM (SELECT USERID,TYPE, ROW_NUMBER() OVER(PARTITION BY USERID ORDER BY LOGINTIME DESC) AS RN FROM iptv.userinfo_active_last_history where time = TO_date('"+time+"','yyyymmdd') " +
                            ") WHERE RN = 1 ) T1 " +
                            "LEFT JOIN  (SELECT USERID,TYPE FROM (SELECT USERID,TYPE, ROW_NUMBER() OVER(PARTITION BY USERID ORDER BY LOGINTIME DESC) AS RN FROM iptv.userinfo_epg_last_history where time = TO_date('"+time+"','yyyymmdd') " +
                            ") WHERE RN = 1 ) T2 ON (T1.USERID = T2.USERID) ) " +
                            "UNION ALL " +
                            "SELECT USERID,TYPE,to_date('"+time+"','yyyymmdd') as time FROM iptv.userinfo_epg_last_history where time = TO_date('"+time+"','yyyymmdd') and USERID IN (SELECT USERID FROM iptv.userinfo_epg_last_history where time = TO_date('"+time+"','yyyymmdd') MINUS SELECT USERID FROM iptv.userinfo_active_last_history where time = TO_date('"+time+"','yyyymmdd'))";

                    DBUtil.excute(insertSql);
                });

    }
}
