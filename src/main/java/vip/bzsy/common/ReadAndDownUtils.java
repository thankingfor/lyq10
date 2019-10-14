package vip.bzsy.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vip.bzsy.model.*;

import javax.annotation.Resource;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author ：李延富
 * @date ：Created in 2019/10/12 23:47
 * description ：
 */
@Component
@Slf4j
public class ReadAndDownUtils {

    @Resource
    private AppContent appContent;

    public void down() {
        if (appContent.getDataMap().size() == 0) {
            throw new RuntimeException("内存不存在！先初始化数据 或者 读取系统文件");
        }
        File file = new File(appContent.getDataPath());
        if (!file.exists()) {
            file.mkdirs();
        }
        //设置请求头
        try (OutputStream outputStream = new FileOutputStream(new File(appContent.getDataPathData()))
             ; ObjectOutputStream oos = new ObjectOutputStream(outputStream)) {
            oos.writeObject(appContent);
            log.info("日期对象写入成功！！！");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void read() {
        Map<Integer, List<LyqTable>> dataMap = appContent.getDataMap();
        dataMap.clear();
        //如果日期数据存在就加载
        if (new File(appContent.getDataPath()).exists()) {
            try (ObjectInputStream oos = new ObjectInputStream(new FileInputStream(appContent.getDataPathData()))) {
                AppContent appContent2 = (AppContent) oos.readObject();
                copy(appContent2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("日期对象加载成功！！！");
        }
    }

    private void copy(AppContent appContent2) {
        // 10个数字
        appContent.setUpDateNum(appContent2.getUpDateNum());
        appContent.setUpNums(appContent2.getUpNums());
        appContent.setUpNumsStr(appContent2.getUpNumsStr());
        appContent.setUpList10(appContent2.getUpList10());
        // 重要数据
        appContent.setLyqDateList(appContent2.getLyqDateList());
        appContent.setDescArray(appContent2.getDescArray());
        appContent.setAscArray(appContent2.getAscArray());
        // 分析数据
        appContent.setAnzList(appContent2.getAnzList());
        appContent.setMaxMinAnalyze(appContent2.getMaxMinAnalyze());
        appContent.setCurrentAnzMaxTop(appContent2.getCurrentAnzMaxTop());
        appContent.setCurrentAnzMinTop(appContent2.getCurrentAnzMinTop());
    }
}
