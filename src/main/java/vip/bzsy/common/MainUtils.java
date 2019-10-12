package vip.bzsy.common;

import org.springframework.stereotype.Component;
import vip.bzsy.controller.BTotalController;
import vip.bzsy.model.AppContent;
import vip.bzsy.model.LyqTable;

import javax.annotation.Resource;
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

    private static Map<Integer, List<LyqTable>> dataMap = BTotalController.dataMap;
    // 第一组的所有数
    private static List<LyqTable> mainList = dataMap.get(0);
    private static Integer totalCount = BTotalController.groupInt;
    // 每一万合成一个String对象
    private static Integer splIndex = 10000;
    private static String separator1 = "-";
    private static String separator2 = "+";
    private static String separator3 = "/";

    /**
     * 下载方法
     */
    public void downFile() {
        Integer totalNum = totalCount / 10 + 1;
        // 默认一万个数一组
        String[] arr = new String[totalNum];
        arr[0] = getListStr(mainList);
        for (int i = 1; i < totalNum; i++) {
            arr[i] = getArrStr(i * splIndex);
        }
        appContent.setDescArray(arr);
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
