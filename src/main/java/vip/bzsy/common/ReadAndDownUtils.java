package vip.bzsy.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vip.bzsy.model.*;

import javax.annotation.Resource;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static vip.bzsy.controller.BTotalController.dataoutPath;

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

    public CommonResponse down() {
        long time3 = System.currentTimeMillis();
        if (appContent.getDataMap().size() == 0) {
            return CommonResponse.fail("内存不存在！先初始化数据 或者 读取系统文件");
        }
        File file = new File(appContent.getDataPath());
        if (!file.exists()) {
            file.mkdirs();
        }
        //设置请求头
        try (OutputStream outputStream = new FileOutputStream(file)
             ; ObjectOutputStream oos = new ObjectOutputStream(outputStream)) {
            oos.writeObject(appContent);
            log.info("日期对象写入成功！！！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        long time4 = System.currentTimeMillis();
        return CommonResponse.success("一共消耗了" + (time4 - time3) / 1000 + "秒");
    }

    public CommonResponse read() {

        Map<Integer, List<LyqTable>> dataMap = appContent.getDataMap();
        dataMap.clear();

        long time3 = System.currentTimeMillis();
        //如果日期数据存在就加载
        if (new File(appContent.getDataPath()).exists()) {
            try (ObjectInputStream oos = new ObjectInputStream(new FileInputStream(dataoutPath));) {
                appContent = (AppContent) oos.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("日期对象加载成功！！！");
        }
        long time4 = System.currentTimeMillis();
        log.info("一共用时" + (time4 - time3) / 1000 + "秒");
        return CommonResponse.success("一共用时" + (time4 - time3) / 1000 + "秒");
    }
}
