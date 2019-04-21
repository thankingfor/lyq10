package vip.bzsy.model;

import lombok.extern.slf4j.Slf4j;
import vip.bzsy.controller.BTotalController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author lyf
 * @create 2019-04-19 14:08
 */
@Slf4j
public class DownFile implements Runnable {

    private Integer start;
    private Integer end;
    private String outPath;

    public DownFile(Integer start, Integer end,String outPath) {
        this.start = start;
        this.end = end;
        this.outPath = outPath;
    }

    @Override
    public void run() {
        try (OutputStream outputStream = new FileOutputStream(new File(outPath+"/内存数据"+start+"-"+end+".txt"))
             ; ObjectOutputStream oos = new ObjectOutputStream(outputStream)) {
            Map<Integer, List<LyqTable>> dataMap = new TreeMap<>();
            for (int i = start ; i <= end ;i++){
                dataMap.put(i, BTotalController.dataMap.get(i));
            }
            oos.writeObject(dataMap);
            log.info("内存数据"+start+"-"+end+".txt,"+dataMap.size()+"个数据写入成功！！！");
        } catch (Exception e) {
            BTotalController.isTrue = false;
        } finally {
            BTotalController.atomicInteger.incrementAndGet();
        }
    }
}
