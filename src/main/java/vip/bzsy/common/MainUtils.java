package vip.bzsy.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;
import vip.bzsy.model.AppContent;
import vip.bzsy.model.LyqDate;
import vip.bzsy.model.LyqTable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author ：李延富
 * @date ：Created in 2019/10/12 22:44
 * description ：
 */
@Component
@Slf4j
public class MainUtils {

    @Resource
    private AppContent appContent;

    @Resource
    private ReadAndDownUtils downUtils;

    /*降序所有数据*/
    private static Map<Integer, List<LyqTable>> dataMap;
    private static Map<Integer, List<LyqTable>> ascDataMap;
    // 第一组的所有数
    private static Integer totalCount;
    // 每一万合成一个String对象
    private static Integer splIndex = 10000;
    private static String separator1 = "-";
    private static String separator2 = ":";
    private static String separator3 = "/";

    @PostConstruct
    public void init() {
        totalCount = appContent.getGroupInt();
        dataMap = appContent.getDataMap();
        ascDataMap = appContent.getAscDataMap();
    }

    public void resolver1(HSSFWorkbook workbook) {
        // 获取10个数字
        HSSFSheet sheet = workbook.getSheetAt(0);
        HSSFRow row = sheet.getRow(1);
        String ids = "";
        List<Integer> idsList = new ArrayList<>();
        for (int i = 14; i <= 18; i++) {
            String cellStringValue = CommonUtils.getCellStringValue(row.getCell(i)).trim();
            log.info(cellStringValue);
            if (CommonUtils.isNotEmpty(cellStringValue)) {
                Integer num = Integer.valueOf(cellStringValue.substring(0, cellStringValue.length() - 2));
                if (i == 14) {
                    num = num % 2 == 0 ? 0 : 1;
                } else if (i == 15) {
                    num = num % 2 == 0 ? 2 : 3;
                } else if (i == 16) {
                    num = num % 2 == 0 ? 4 : 5;
                } else if (i == 17) {
                    num = num % 2 == 0 ? 6 : 7;
                } else if (i == 18) {
                    num = num % 2 == 0 ? 8 : 9;
                }
                idsList.add(num);
                if (ids == "")
                    ids = num + "";
                else
                    ids = ids + "," + num;
            }
        }
        // 获取期号
        HSSFCell cellDateNum = row.getCell(0);
        cellDateNum.setCellType(HSSFCell.CELL_TYPE_STRING);
        String dateNum = cellDateNum.getStringCellValue();

        appContent.setUpNums(idsList);
        appContent.setUpNumsStr(ids);
        appContent.setUpDateNum(dateNum);
        log.info("上传数据之日期" + dateNum+"----"+ids);
    }

    public void resolver2(HSSFWorkbook workbook) {
        HSSFSheet sheet = workbook.getSheetAt(0);
        // 上传10数字
        List<Integer> listant = new LinkedList<>();
        for (int i = 0; i < appContent.getGroupRow(); i++) {
            HSSFCell cell = sheet.getRow(i + 1).getCell(12);
            String cellStringValue = CommonUtils.getCellStringValue(cell);
            if (CommonUtils.isNotEmpty(cellStringValue)) {
                cellStringValue = cellStringValue.substring(0, cellStringValue.length() - 2);
                listant.add(Integer.valueOf(cellStringValue));
            }
        }
        appContent.setUpList10(listant);
        log.info("解析完毕上传的数据" + listant.toString());
    }

    public void readFile() {
        downUtils.read();
        // 反序列化
        getData(appContent.getDescArray(), dataMap);
        getData(appContent.getAscArray(), ascDataMap);
    }

    private void getData(String[] arrays, Map<Integer, List<LyqTable>> map) {
        Integer groupNum = 0;
        for (String group: arrays) {
            String[] gs = group.split(separator3);
            for (String g: gs) {
                List<LyqTable> list = getList(g);
                map.put(groupNum ++, list);
            }
        }
    }

    /**
     * 下载方法
     */
    public void downFile() {
        // 主要数据
        appContent.setDescArray(mapToStrArr(dataMap));
        appContent.setAscArray(mapToStrArr(ascDataMap));
        // 下载
        downUtils.down();
    }

    private String[] mapToStrArr(Map<Integer, List<LyqTable>> map) {
        Integer totalNum = totalCount / splIndex + 1;
        // 默认一万个数一组
        String[] arr = new String[totalNum];
        arr[0] = getListStr(map.get(0));
        for (int i = 1; i < totalNum; i++) {
            arr[i] = getArrStr(i * splIndex);
        }
        return arr;
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
