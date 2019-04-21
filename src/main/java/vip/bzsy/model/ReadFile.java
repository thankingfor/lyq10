package vip.bzsy.model;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import vip.bzsy.controller.BTotalController;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

/**
 * @author lyf
 * @create 2019-04-19 14:42
 */
@Slf4j
public class ReadFile implements Runnable {

    private Integer start;
    private Integer end;
    private String outPath;

    public ReadFile(Integer start, Integer end,String outPath) {
        this.start = start;
        this.end = end;
        this.outPath = outPath;
    }

    @Override
    public void run() {
        try (ObjectInputStream oos = new ObjectInputStream(new FileInputStream(outPath+"/内存数据"+start+"-"+end+".txt"))) {
            Map<Integer, List<LyqTable>> dataMap = (Map<Integer, List<LyqTable>>) oos.readObject();
            BTotalController.setDataMap(dataMap);
            log.info("内存数据"+start+"-"+end+".txt,"+dataMap.size()+"个内存对象加载成功！！！");

        } catch (Exception e) {
            BTotalController.isTrue = false;
        } finally {
            BTotalController.atomicInteger.incrementAndGet();
        }
    }
}
