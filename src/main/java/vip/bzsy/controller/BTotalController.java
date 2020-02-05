package vip.bzsy.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import vip.bzsy.common.CommonResponse;
import vip.bzsy.common.DataCheckException;
import vip.bzsy.common.MainUtils;
import vip.bzsy.common.ReadAndDownUtils;
import vip.bzsy.enums.Way;
import vip.bzsy.model.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lyf
 * @create 2019-04-03 7:11
 */
@Slf4j
@Controller
@RequestMapping("/bt")
@SuppressWarnings("all")
public class BTotalController {

    /**
     * 按钮二 获取3000个数据
     * 9.获取第0组
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/copy")
    public void copy(HttpServletRequest request, HttpServletResponse response) throws Exception {
        /**
         * 第一项任务
         */
        long time1 = System.currentTimeMillis();
        List<Integer> anInt = appContent.getUpList10();
        String dateNum = appContent.getUpDateNum();
        List<LyqTable> list0Desc = copyStart(anInt, Way.DESC);//获取第0组并且按照排序
        List<LyqTable> list0Asc = copyStart(anInt, Way.ASC);//获取第0组并且按照排序
        //开始修改0-3000组的key
        for (int group = 1; group < appContent.getGroupInt(); group++) {
            List<LyqTable> tables = copySort(list0Desc, group, appContent.getDataMap(), Way.DESC);//本组的降序的tables
            list0Desc = tables;//把list0改成下一组的方便循环
        }
        for (int group = 1; group < appContent.getGroupInt(); group++) {
            List<LyqTable> tables = copySort(list0Asc, group, appContent.getAscDataMap(), Way.ASC);//本组的降序的tables
            list0Asc = tables;//把list0改成下一组的方便循环
        }

        // 第一次任务之前的过滤252过滤
        fiter252();

        //开始分析
        List<LyqTable> listMaxDesc = new LinkedList<>();//存放最大值
        List<ResultVo> listResultVoDesc = new LinkedList<>();
        List<LyqTable> listMaxAsc = new LinkedList<>();//存放最大值
        List<ResultVo> listResultVoAsc = new LinkedList<>();
        for (int group = 1; group < appContent.getGroupInt(); group++) {
            List<LyqTable> tables = clone(appContent.getDataMap().get(group));
            tables.sort((x, y) -> y.getLyqValue() - x.getLyqValue()); // 根于value 降序排列
            listMaxDesc.add(tables.get(0));//把最大的值存放
            listResultVoDesc.add(ResultVo.toVo(tables));
        }
        for (int group = 1; group < appContent.getGroupInt(); group++) {
            List<LyqTable> tables = clone(appContent.getAscDataMap().get(group));
            tables.sort((x, y) -> y.getLyqValue() - x.getLyqValue()); // 根于value 降序排列
            listMaxAsc.add(tables.get(9));//把最大的值存放
            listResultVoAsc.add(ResultVo.toVoASC(tables));
        }

        long time2 = System.currentTimeMillis();
        print("第一个任务耗时：", time1, time2);

        /**
         * 准备下载
         */
        log.info("准备开始下载模板");
        listMaxDesc = listMaxDesc.stream()
                .filter(x -> x.getLyqValue() >=5)
                .sorted((x, y) -> y.getLyqValue() - x.getLyqValue())
                .limit(3000)
                .collect(Collectors.toList());
        listMaxAsc = listMaxAsc.stream()
                .filter(x -> x.getLyqValue() >=5)
                .sorted((x, y) -> y.getLyqValue() - x.getLyqValue())
                .limit(3000)
                .collect(Collectors.toList());
        listResultVoDesc = listResultVoDesc.stream()
                .filter(x -> x.getValue2() >=5)
                .sorted((x, y) -> y.getValue2() - x.getValue2())
                .limit(3000)
                .collect(Collectors.toList());
        listResultVoAsc = listResultVoAsc.stream()
                .filter(x -> x.getValue2() >=5)
                .sorted((x, y) -> y.getValue2() - x.getValue2())
                .limit(3000)
                .collect(Collectors.toList());
        HSSFWorkbook workbook = new HSSFWorkbook();//1.在内存中操作excel文件
        workbook = copyDownMax(workbook , listMaxDesc, listResultVoDesc, dateNum, "降序");
        workbook = copyDownMax(workbook , listMaxAsc, listResultVoAsc, dateNum, "升序");
        //5.创建文件名
        String fileName = dateNum + ".xls";
        log.info(fileName);
        //6.获取输出流对象
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        response.setContentType("multipart/form-data");
        log.info("设置请求头");
        //设置请求头
        ServletOutputStream outputStream = response.getOutputStream();
        log.info("获取输出流");
        workbook.write(outputStream);
        log.info("写入输出流");
    }

    /**
     * 过滤252
     * 根据252的文件，找到当前组对应的值
     * 根据key找到value （key_value）
     * 把max_key和key_value交换位置
     */
    private void fiter252() {
        filter252Map(appContent.getDataMap());
        filter252Map(appContent.getAscDataMap());
    }

    private void filter252Map(Map<Integer, List<LyqTable>> dataMap) {
        Map<String, Integer> map252 = appContent.getMap252();
        dataMap.forEach((key, laqtables) -> {
            List<Integer> key252 = new LinkedList<>();
            Integer maxKey = -1;
            Integer maxValue = -1;
            for (int i = 0; i < laqtables.size(); i ++) {
                LyqTable lyqTable = laqtables.get(i);
                lyqTable.setLyq252("");
                if (maxValue < lyqTable.getLyqValue()) {
                    maxKey = lyqTable.getLyqKey();
                    maxValue = lyqTable.getLyqValue();
                }
                if (lyqTable.getLyqValue() > 0) {
                    key252.add(lyqTable.getLyqKey());
                }
            }

            String key252Str = "";

            key252.sort(Integer::compareTo);
            for (Integer key252key: key252) {
                if (key252key != null) {
                    key252Str += key252key;
                }
            }
            Integer value252 = map252.get(key252Str);
            // 把最大的key和当前目标key进行交换
            if (value252 != null && value252 != -1) {
                for (LyqTable lyqTable : laqtables) {
                    if (lyqTable.getLyqKey() == value252) {
                        lyqTable.setLyqKey(maxKey);
                        lyqTable.setLyq252(key252Str + "=" + value252);
                        continue;
                    }
                    if (lyqTable.getLyqKey() == maxKey) {
                        lyqTable.setLyq252("来自" + maxKey);
                        lyqTable.setLyqKey(value252);
                        continue;
                    }
                }
            }
        });
    }

    /**
     * 9.4下载文档
     *
     * @param response
     * @param listMax
     * @param type2VoListType
     * @param listResultVoAsc
     * @param dateNum
     * @throws Exception
     */
    public HSSFWorkbook copyDownMax(HSSFWorkbook workbook, List<LyqTable> lyqTableList,
                                    List<ResultVo> listResultVo, String dateNum, String name) throws Exception {
        //操作list进行下载  日期号  组  key value
        HSSFSheet sheet = workbook.createSheet(name);//2.创建工作谱
        HSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("组号");
        row.createCell(1).setCellValue("key");
        row.createCell(2).setCellValue("value");

        row.createCell(4).setCellValue("组号");
        row.createCell(5).setCellValue("第一大key");
        row.createCell(6).setCellValue("第一大value");
        row.createCell(7).setCellValue("第二大key");
        row.createCell(8).setCellValue("第二大value");

        //4.遍历数据,创建数据行
        for (LyqTable table : lyqTableList) {//因为默认是按照最大值排序 所以先写后面的
            int lastRowNum = sheet.getLastRowNum();//获取最后一行的行号
            HSSFRow dataRow = sheet.createRow(lastRowNum + 1);
            dataRow.createCell(0).setCellValue("第" + table.getLyqGroup() + "组");
            dataRow.createCell(1).setCellValue(table.getLyqKey());
            dataRow.createCell(2).setCellValue(table.getLyqValue());
        }
        int rowNum = 0;
        for (ResultVo resultVo : listResultVo) {//因为默认是按照最大值排序 所以先写后面的
            HSSFRow dataRow = sheet.getRow(rowNum + 1);
            dataRow.createCell(4).setCellValue("第" + resultVo.getGroup() + "组");
            dataRow.createCell(5).setCellValue(resultVo.getKey1());
            dataRow.createCell(6).setCellValue(resultVo.getValue1());
            dataRow.createCell(7).setCellValue(resultVo.getKey2());
            dataRow.createCell(8).setCellValue(resultVo.getValue2());
            rowNum ++;
        }
        return workbook;
    }

    /**
     * 9.2对每一组的更新
     * 需要知道前一组的情况
     *
     * @param copyList 前一组的list
     * @param desc
     * @return 这一组的list
     */
    public List<LyqTable> copySort(List<LyqTable> cloneList, Integer gruop,
                                   Map<Integer, List<LyqTable>> map, Way way) {
        //1.获取数据并且按照seq排序
        List<LyqTable> listgroupbySeq = map.get(gruop);
        //2.把key 复给 下一组 带上0组的seq
        for (int i = 0; i < appContent.getGroupRow(); i++) {
            listgroupbySeq.get(i).setLyqKey(cloneList.get(i).getLyqKey()); //把上一组的key给这一组
            listgroupbySeq.get(i).setLyqSeq(cloneList.get(i).getLyqSeq()); //把第零组的序列付给每一组
        }
        cloneList = clone(listgroupbySeq);
        //3.逆向排序
        if (Way.DESC.equals(way)) {
            cloneList.sort((x, y) -> y.getLyqValue() - x.getLyqValue());
        } else {
            cloneList.sort((x, y) -> x.getLyqValue() - y.getLyqValue());
        }
        return cloneList;
    }

    public List<LyqTable> clone(List<LyqTable> lyqTables) {
        List<LyqTable> lyqTables2 = new ArrayList<>();
        for (LyqTable lyqTable: lyqTables) {
            LyqTable table = new LyqTable();
            table.setSeq(lyqTable.getSeq());
            table.setLyqSeq(lyqTable.getLyqSeq());
            table.setLyqKey(lyqTable.getLyqKey());
            table.setLyqValue(lyqTable.getLyqValue());
            table.setLyqGroup(lyqTable.getLyqGroup());
            lyqTables2.add(table);
        }
        return lyqTables2;
    }

    /**
     * 9.1 copy的初始化操作
     */
    public List<LyqTable> copyStart(List<Integer> anInt, Way way) {
        if (Way.DESC.equals(way)) {
            List<LyqTable> listgroupbySeq = appContent.getDataMap().get(0);
            List<LyqTable> cloneList = clone(listgroupbySeq);
            //模拟Excel获取appContent.getGroupRow()个数据 并且赋值
            for (int i = 0; i < appContent.getGroupRow(); i++) {
                cloneList.get(i).setLyqKey(anInt.get(i)); //把上一组的key给这一组
            }
            //变量排序
            cloneList.sort((x, y) -> y.getLyqValue() - x.getLyqValue());
            return cloneList;
        } else {
            List<LyqTable> listgroupbySeq = appContent.getAscDataMap().get(0);
            List<LyqTable> cloneList = clone(listgroupbySeq);
            //模拟Excel获取3000个数据 并且赋值
            for (int i = 0; i < appContent.getGroupRow(); i++) {
                cloneList.get(i).setLyqKey(anInt.get(i)); //把上一组的key给这一组
            }
            //变量排序
            cloneList.sort((x, y) -> x.getLyqValue() - y.getLyqValue());
            return cloneList;
        }
    }

    /**
     * 8.上传数据
     */
    @ResponseBody
    @RequestMapping("/upload")
    private CommonResponse getIntByFile(MultipartFile file) throws IOException, DataCheckException {
        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
        /*第一步 解析上传的文件*/
        mainUtils.resolver1(workbook);
        mainUtils.resolver2(workbook);
        /*第二步 升序的和降序的进行加一和归零的操作*/
        // ----> 上传数据
        LyqDate lyqDate = new LyqDate();
        lyqDate.setDateNum(appContent.getUpDateNum());
        lyqDate.setValue(appContent.getUpNumsStr());
        CommonResponse replace = replace(lyqDate);
        if (replace.getCode() == 0)
            return CommonResponse.fail("日期号码重复了");
        // ----> 上传数据
        return CommonResponse.success();
    }

    /**
     * 7.按钮一 通过 几位数来进行操作
     * key相同的情况下 value为0
     * 其他的value+1
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/replace")
    public CommonResponse replace(LyqDate lyqDate) throws DataCheckException {
        if (checkMap(appContent.getDataMap()).getCode() == 0) {
            throw new DataCheckException();
        }
        lyqDate.setDateNum(lyqDate.getDateNum().trim());
        String ids = lyqDate.getValue();
        long count = appContent.getLyqDateList().stream().filter(x -> x.getDateNum().equals(lyqDate.getDateNum())).count();
        if (count > 0) {
            return CommonResponse.fail("期号重复了！！！");
        }
        //把所有的数据改为0 其他的++  然后把日期号存入数据库
        long time1 = System.currentTimeMillis();
        updateToZore(ids.trim(), appContent.getDataMap());
        updateToZore(ids.trim(), appContent.getAscDataMap());
        appContent.getLyqDateList().add(lyqDate);
        long time2 = System.currentTimeMillis();
        print("更新操作（归零和加一）用时：", time1, time2);
        //lyqDate.insert();
        return CommonResponse.success();
    }

    public void updateToZore(String trim, Map<Integer, List<LyqTable>> data) {
        //变为0 array
        String[] split = trim.split(",");
        Integer[] ids = new Integer[split.length];
        for (int i = 0; i < split.length; i++) {
            ids[i] = Integer.valueOf(split[i].trim());
        }

        Iterator<Integer> iterator = data.keySet().iterator();
        iterator.forEachRemaining(group -> {
            List<LyqTable> lyqTables = data.get(group);
            List<LyqTable> collect = lyqTables.stream()
                    .map(table -> {
                        if (Arrays.asList(ids).contains(table.getLyqKey()))
                            table.setLyqValue(0);
                        else
                            table.setLyqValue(table.getLyqValue() + 1);
                        return table;
                    }).collect(Collectors.toList());
            data.put(group, collect);
        });
    }

    /**
     * 3.查询日期list
     */
    @ResponseBody
    @RequestMapping(value = "/replace/list")
    public Map<String, Object> replacelist(Integer page, Integer rows) {
        if (appContent.getLyqDateList().size() == 0) {
            log.info("内存没有数据，请添加数据...");
            map.clear();
            map.put("rows", null);
            map.put("total", 0);
            return map;
        }
        /**
         * 流的排序分页操作
         */
        List<LyqDate> collect = appContent.getLyqDateList().stream()
                .sorted((x, y) -> y.getDateNum().compareTo(x.getDateNum()))
                .skip((page - 1) * rows).limit(rows).parallel().collect(Collectors.toList());
        map.clear();
        map.put("rows", collect);
        map.put("total", appContent.getLyqDateList().size());
        return map;
    }

    /**
     * 4.查询内存中的dataMap
     */
    @ResponseBody
    @RequestMapping(value = "/copy/list")
    public Map<String, Object> copylist(Integer page, Integer rows,
                                        @RequestParam(value = "way", defaultValue = "DESC") Way way) throws DataCheckException {
        if (checkMap(appContent.getDataMap()).getCode() == 0) {
            log.info("内存没有数据，请添加数据...");
            map.clear();
            map.put("rows", null);
            map.put("total", 0);
            return map;
        }
        List<LyqTable> lyqTables = null;
        if (Way.ASC.equals(way)) {
            lyqTables = appContent.getAscDataMap().get(page - 1);
        } else if (Way.DESC.equals(way)) {
            lyqTables = appContent.getDataMap().get(page - 1);
        }

        map.clear();
        map.put("rows", lyqTables);
        map.put("total", appContent.getDataMap().size() * rows);
        return map;
    }

    /**
     * 2.初始化内存数据
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/init/data")
    public CommonResponse initInsert() {
        appContent.getDataMap().clear();
        long time1 = System.currentTimeMillis();
        for (int i = 0; i < appContent.getGroupInt(); i++) {
            List<LyqTable> init3000 = getInit3000(i);
            List<LyqTable> init3000Asc = getInit3000(i);
            appContent.getDataMap().put(i, init3000);
            appContent.getAscDataMap().put(i, init3000Asc);
        }
        long time2 = System.currentTimeMillis();

        readAndDownUtils.read252();

        print(appContent.getDataMap().size()+"组数据初始化完成，添加到内存总耗时：", time1, time2);
        return CommonResponse.success();
    }


    public List<LyqTable> getInit3000(Integer group) {
        List<LyqTable> list = new LinkedList<>();
        for (int i = 0; i < appContent.getGroupRow(); i++) {
            LyqTable lyqTable = new LyqTable();
            lyqTable.setSeq(i + 1);
            lyqTable.setLyqKey(i);
            lyqTable.setLyqSeq(i + 1);
            lyqTable.setLyqGroup(group);
            lyqTable.setLyqValue(0);
            list.add(lyqTable);
        }
        return list;
    }

    /**
     * 5.检查dataMap数据是否合法
     */
    @ResponseBody
    @RequestMapping(value = "/check")
    private CommonResponse checkMap(Map<Integer, List<LyqTable>> dataMaps) {
        if (dataMaps.size() == appContent.getGroupInt()) {
            return CommonResponse.success("数据合法");
        }
        log.info(dataMaps.size()+"组检验不足"+appContent.getGroupInt()+"组");
        return CommonResponse.fail("数据不合法");
    }

    /**
     * 6.加载模板
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/donwloadTem")
    public void donwloadTem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //操作list进行下载  日期号  组  key value
        HSSFWorkbook workbook = new HSSFWorkbook();//1.在内存中操作excel文件
        HSSFSheet sheet = workbook.createSheet();//2.创建工作谱
        sheet.setColumnWidth(0, 1440 * 3);
        sheet.setColumnWidth(1, 720);
        sheet.setColumnWidth(2, 720);
        sheet.setColumnWidth(3, 720);
        sheet.setColumnWidth(4, 720);
        sheet.setColumnWidth(5, 720);
        sheet.setColumnWidth(6, 720);
        sheet.setColumnWidth(7, 720);
        sheet.setColumnWidth(8, 720);
        sheet.setColumnWidth(9, 720);
        sheet.setColumnWidth(10, 720);
        sheet.setColumnWidth(11, 1440 * 3);
        sheet.setColumnWidth(12, 1440 * 3);

        HSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("期号");
        row.createCell(1).setCellValue("0");
        row.createCell(2).setCellValue("1");
        row.createCell(3).setCellValue("2");
        row.createCell(4).setCellValue("3");
        row.createCell(5).setCellValue("4");
        row.createCell(6).setCellValue("5");
        row.createCell(7).setCellValue("6");
        row.createCell(8).setCellValue("7");
        row.createCell(9).setCellValue("8");
        row.createCell(10).setCellValue("9");
        row.createCell(11).setCellValue("3000数之样式");
        row.createCell(12).setCellValue("需要上传的3000数");

        row.createCell(14).setCellValue("第一位数");
        row.createCell(15).setCellValue("第二位数");
        row.createCell(16).setCellValue("第三位数");
        row.createCell(17).setCellValue("第四位数");
        row.createCell(18).setCellValue("第五位数");

        //赋值3000模板书数
        Integer[] anInt = new Integer[]{0,1,2,3,4,5,6,7,8,9};
        for (Integer num : anInt) {
            int lastRowNum = sheet.getLastRowNum();//获取最后一行的行号
            HSSFRow dataRow = sheet.createRow(lastRowNum + 1);
            dataRow.createCell(11).setCellValue(num);
            dataRow.createCell(12).setCellValue(num);
        }
        //模板日期
        HSSFRow row1 = sheet.getRow(1);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        row1.createCell(0).setCellValue(format.format(new Date()) + "01");
        //5.创建文件名
        String fileName = "上传模板.xls";
        //6.获取输出流对象
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        response.setContentType("multipart/form-data");
        //设置请求头
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
    }


    /**
     * 下载数据
     */
    @ResponseBody
    @RequestMapping(value = "/down/obj")
    public CommonResponse dataMapdonwloadobj() {
        long time1 = System.currentTimeMillis();
        mainUtils.downFile();
        long time2 = System.currentTimeMillis();
        return CommonResponse.success("一共消耗了" + (time2 - time1) / 1000 + "秒");
    }

    /**
     * 上传数据
     */
    @ResponseBody
    @RequestMapping(value = "/get/obj")
    public CommonResponse getobj() {
        long time1 = System.currentTimeMillis();
        mainUtils.readFile();

        long time2 = System.currentTimeMillis();
        log.info("一共用时" + (time2 - time1) / 1000 + "秒");
        return CommonResponse.success("一共用时" + (time2 - time1) / 1000 + "秒");
    }

    /**
     * 随机生成一组数
     * @return
     */
    public List<Integer> get3000Int() {
        Random random = new Random();
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < appContent.getGroupRow(); i++) {
            list.add(random.nextInt(10));
        }
        return list;
    }

    /**
     * 打印工具
     */
    public static long print(String value, Long start, Long end) {
        log.info(value + (end - start));
        return end - start;
    }

    /**
     * 输出数据
     */
    @ResponseBody
    @RequestMapping("/printData")
    public void printData() {
        log.info("开始打印data数据 " + appContent.getDataMap().size());
        Iterator<Integer> iterator = appContent.getDataMap().keySet().iterator();
        iterator.forEachRemaining(key -> log.info(appContent.getDataMap().get(key).toString()));
    }

    @Resource
    private MainUtils mainUtils;

    @Resource
    private AppContent appContent;

    // 负责返回数据用的
    private Map<String, Object> map = new HashMap<>();

    /**
     * 随机对象
     */
    private Random random = new Random();

    @Resource private ReadAndDownUtils readAndDownUtils;
}
