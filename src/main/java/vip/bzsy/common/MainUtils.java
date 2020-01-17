package vip.bzsy.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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
        for (int i = 1; i <= 10; i++) {
            String cellStringValue = CommonUtils.getCellStringValue(row.getCell(i)).trim();
            if (!StringUtils.isEmpty(cellStringValue)) {
                idsList.add(i -1);
                if (ids.equals("")) {
                    ids =  (i - 1) + "";
                } else {
                    ids += "," + (i - 1);
                }
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
    }

    /**
     * 下载方法
     */
    public void downFile() {
        downUtils.down();
    }

}
