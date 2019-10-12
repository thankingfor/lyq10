package vip.bzsy.common;

import org.springframework.stereotype.Component;
import vip.bzsy.controller.BTotalController;
import vip.bzsy.model.AppContent;
import vip.bzsy.model.LyqDate;
import vip.bzsy.model.LyqTable;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author ：李延富
 * @date ：Created in 2019/10/12 22:44
 * description ：
 */
@Component
public class MainUtils {

    @Resource
    private AppContent appContent;

    @Resource
    private ReadAndDownUtils downUtils;

    private static Map<Integer, List<LyqTable>> dataMap = BTotalController.dataMap;
    // 第一组的所有数
    private static List<LyqTable> mainList = dataMap.get(0);
    private static Integer totalCount = BTotalController.groupInt;
    // 每一万合成一个String对象
    private static Integer splIndex = 10000;
    private static String separator1 = "-";
    private static String separator2 = ":";
    private static String separator3 = "/";


    public CommonResponse readFile() {
        CommonResponse read = downUtils.read();
        // 反序列化
        String lyqDateListStr = appContent.getLyqDateListStr();
        appContent.setLyqDateList(getDateList(lyqDateListStr));

        Map<Integer, List<LyqTable>> dataMap = appContent.getDataMap();
        String[] descArray = appContent.getDescArray();
        Integer groupNum = 0;
        for (String group: descArray) {
            String[] gs = group.split(separator3);
            for (String g: gs) {
                List<LyqTable> list = getList(g);
                dataMap.put(groupNum ++, list);
            }
        }
        return read;
    }

    /**
     * 下载方法
     */
    public void downFile() {
        // 1. 日期对象
        String dataStr = getDateListStr(appContent.getLyqDateList());
        appContent.setLyqDateListStr(dataStr);
        // 2. 主要数据
        Integer totalNum = totalCount / 10 + 1;
        // 默认一万个数一组
        String[] arr = new String[totalNum];
        arr[0] = getListStr(mainList);
        for (int i = 1; i < totalNum; i++) {
            arr[i] = getArrStr(i * splIndex);
        }
        appContent.setDescArray(arr);
        // 下载
        downUtils.down();
    }

    private String getDateListStr(List<LyqDate> lyqDateList) {
        StringBuffer buffer = new StringBuffer();
        for(LyqDate lyqDate: lyqDateList) {
            buffer.append(lyqDate.getDateNum()).append(separator1)
                    .append(lyqDate.getValue()).append(separator2);
        }
        return buffer.toString();
    }

    private List<LyqDate> getDateList(String dateStr) {
        List<LyqDate> lyqDateList = new LinkedList<>();
        String[] objs = dateStr.split(separator2);
        for (String obj : objs) {
            String[] items = obj.split(separator1);
            LyqDate lyqDate = new LyqDate();
            lyqDate.setDateNum(items[0]);
            lyqDate.setValue(items[1]);
            lyqDateList.add(lyqDate);
        }

        return lyqDateList;
    }

    /**
     * 反序成对象
     * seq lyqGroup lyqKey lyqValue lyqSeq
     */
    public static List<LyqTable> getList(String dataStr) {
        List<LyqTable> tables = new LinkedList<>();
        String[] objs = dataStr.split(separator2);
        for (String obj : objs) {
            String[] items = obj.split(separator1);
            LyqTable lyqTable = new LyqTable();
            lyqTable.setSeq(Integer.valueOf(items[0]));
            lyqTable.setLyqGroup(Integer.valueOf(items[1]));
            lyqTable.setLyqKey(Integer.valueOf(items[2]));
            lyqTable.setLyqValue(Integer.valueOf(items[3]));
            lyqTable.setLyqSeq(Integer.valueOf(items[4]));
            tables.add(lyqTable);
        }

        return tables;
    }

    /**
     * 【1, 10000】
     * 【2, 20000】
     */
    private static String getArrStr(Integer end) {
        StringBuffer buffer = new StringBuffer();
        Integer start = end - splIndex + 1;
        buffer.append(getListStr(dataMap.get(start)));
        for (int i = start + 1; i <= end; i++) {
            buffer.append(separator3).append(getListStr(dataMap.get(i)));
        }
        return buffer.toString();
    }

    private static String getListStr(List<LyqTable> mainList) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(getStr(mainList.get(0)));
        for (int i = 1; i < mainList.size(); i ++) {
            buffer.append(separator2).append(getStr(mainList.get(i)));
        }
        return buffer.toString();
    }

    /**
     * 所有对象按照 - 分隔符进行分割
     * seq lyqGroup lyqKey lyqValue lyqSeq
     */
    private static String getStr(LyqTable table) {
        return new StringBuffer().append(table.getSeq()).append(separator1)
                .append(table.getLyqGroup()).append(separator1)
                .append(table.getLyqKey()).append(separator1)
                .append(table.getLyqValue()).append(separator1)
                .append(table.getLyqSeq()).toString();
    }
}