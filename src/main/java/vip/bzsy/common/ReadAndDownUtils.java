package vip.bzsy.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
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

    private void copy(AppContent appContent2) throws IOException {
        // 10个数字
        appContent.setUpDateNum(appContent2.getUpDateNum());
        appContent.setUpNums(appContent2.getUpNums());
        appContent.setUpNumsStr(appContent2.getUpNumsStr());
        appContent.setUpList10(appContent2.getUpList10());
        // 重要数据
        appContent.setLyqDateList(appContent2.getLyqDateList());
        appContent.setDataMap(appContent2.getDataMap());
        appContent.setAscDataMap(appContent2.getAscDataMap());
        // 252
        read252();
    }

    public void read252(){
        try {
            Map<String, Integer> hashMap = appContent.getMap252();
            ClassPathResource resource = new ClassPathResource("252组.xlsx");
            InputStream inputStream = resource.getInputStream();
            //File file = ResourceUtils.getFile("classpath:252组.xlsx");
            //log.info("252 path = {}", file.getAbsolutePath());
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (int rowNum = 0; rowNum < 300; rowNum ++) {
                XSSFRow row = sheet.getRow(rowNum);
                if (row == null || "".equals(CommonUtils.getCellStringValue(row.getCell(12)).trim())) {
                    continue;
                }
                String key = "";
                Integer value = 0;
                for (int cellNum = 2; cellNum <= 12; cellNum++) {

                    XSSFCell cell = row.getCell(cellNum);
                    String cellStringValue = CommonUtils.getCellStringValue(cell).trim();

                    if (cellNum == 12) {
                        value = Integer.valueOf(cellStringValue);
                    } else {
                        key += cellStringValue;
                    }
                }
                hashMap.put(key, value);
                log.info("252 ::: {}={}", key, value);
            }
        } catch (IOException e) {
            log.error("初始化252出错,{}", e);
        }
    }

}
