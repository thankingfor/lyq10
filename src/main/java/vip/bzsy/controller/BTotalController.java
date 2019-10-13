package vip.bzsy.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import vip.bzsy.common.CommonResponse;
import vip.bzsy.common.CommonUtils;
import vip.bzsy.common.DataCheckException;
import vip.bzsy.common.MainUtils;
import vip.bzsy.enums.Way;
import vip.bzsy.model.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
        List<LyqTable> list0 = copyStart(anInt);//获取第0组并且按照排序
        List<LyqTable> listMax = new LinkedList<>();//存放最大值
        //开始修改0-3000组的key
        for (int group = 1; group < groupInt; group++) {
            List<LyqTable> tables = copySort(list0, group);//本组的降序的tables
            listMax.add(tables.get(0));//把最大的值存放
            list0 = tables;//把list0改成下一组的方便循环
        }
        long time2 = System.currentTimeMillis();
        print("第一个任务耗时：", time1, time2);
        /**
         * 第二个任务
         * 按照lyq_seq进行排序
         */
        long time3 = System.currentTimeMillis();
        List<Type2Vo> type2VoListType = new LinkedList<>();
        Map<Integer, List<LyqTable>> listMap = new HashMap<>();
        Integer groupType2 = 1;
        for (int group = 1; group < groupInt; group++) {
            List<LyqTable> listgroup = dataMap.get(group);
            listgroup.sort((x, y) -> x.getLyqSeq() - y.getLyqSeq());
            listMap.put(group, listgroup);
            if (group % groupNum == 0) {
                //log.info("正在处理第二个任务，目前第"+groupType2+"组");
                List<Type2Vo> type2VoList = sortType2(listMap, groupType2);
                type2VoListType.addAll(type2VoList);
                listMap.clear();
                groupType2++;
            }
        }
        //type2VoListType最终排序
        //type2VoListType补充key
        type2VoListType = setLyqDates(type2VoListType);
        //进行下载操作 取最大的5000个最大值  取前2000个最大值
        List<LyqTable> listMaxResult = listMax.stream().sorted((x, y) -> y.getLyqValue() - x.getLyqValue()).limit(5000).collect(Collectors.toList());
        List<Type2Vo> type2VoListResult = type2VoListType.stream().sorted((x, y) -> y.getValue() - x.getValue()).limit(2000).collect(Collectors.toList());

        /**
         * 准备下载
         */
        log.info("准备开始下载模板");
        HSSFWorkbook workbook = copyDownMax(listMaxResult, type2VoListResult, dateNum);
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
        /**
         * 结果统计
         */
        long endTime2 = System.currentTimeMillis();
        long time4 = System.currentTimeMillis();
        print("第二个任务耗时：", time3, time4);
        print("一共耗时：", time1, time4);
    }

    /**
     * 把第二个操作补充上key
     * @param type2VoListType
     * @return
     */
    public List<Type2Vo> setLyqDates(List<Type2Vo> type2VoListType){
        List<LyqTable> lyqTables = dataMap.get(0);
        lyqTables.sort((x, y) -> x.getLyqSeq()-y.getLyqSeq());
        List<Type2Vo> collect = type2VoListType.stream().map(x -> {
            Integer key = lyqTables.get(x.getSeq()-1).getLyqKey();
            x.setKey(key);
            return x;
        }).collect(Collectors.toList());
        log.info("补充key完毕");
        return collect;
    }

    /**
     * 9.4下载文档
     *
     * @param response
     * @param listMax
     * @param type2VoListType
     * @param dateNum
     * @throws Exception
     */
    public HSSFWorkbook copyDownMax(List<LyqTable> listMax,
                                    List<Type2Vo> type2VoListType, String dateNum) throws Exception {
        //操作list进行下载  日期号  组  key value
        HSSFWorkbook workbook = new HSSFWorkbook();//1.在内存中操作excel文件
        HSSFSheet sheet = workbook.createSheet();//2.创建工作谱
        HSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("组号");
        row.createCell(1).setCellValue("key");
        row.createCell(2).setCellValue("value");
        row.createCell(4).setCellValue("组号");
        row.createCell(5).setCellValue("序列");
        row.createCell(6).setCellValue("key");
        row.createCell(7).setCellValue("合计");
        row.createCell(10).setCellValue("组号");
        row.createCell(11).setCellValue("key");
        row.createCell(12).setCellValue("value");
        row.createCell(14).setCellValue("组号");
        row.createCell(15).setCellValue("序列");
        row.createCell(16).setCellValue("key");
        row.createCell(17).setCellValue("合计");
        //4.遍历数据,创建数据行
        for (LyqTable table : listMax) {//因为默认是按照最大值排序 所以先写后面的
            int lastRowNum = sheet.getLastRowNum();//获取最后一行的行号
            HSSFRow dataRow = sheet.createRow(lastRowNum + 1);
            dataRow.createCell(10).setCellValue("第" + table.getLyqGroup() + "组");
            dataRow.createCell(11).setCellValue(table.getLyqKey());
            dataRow.createCell(12).setCellValue(table.getLyqValue());
        }
        log.info("处理完第一个组");
        for (int i = 0; i < type2VoListType.size(); i++) {//默认最大值排序
            HSSFRow dataRow = sheet.getRow(i + 1);
            dataRow.createCell(14).setCellValue("第" + type2VoListType.get(i).getGroup() + "组");
            dataRow.createCell(15).setCellValue(type2VoListType.get(i).getSeq());
            dataRow.createCell(16).setCellValue(type2VoListType.get(i).getKey());
            dataRow.createCell(17).setCellValue(type2VoListType.get(i).getValue());
        }
        log.info("处理完第二个组");
        listMax.sort((x, y) -> x.getLyqGroup()-y.getLyqGroup());
        type2VoListType.sort((x, y) -> x.getGroup() - y.getGroup());
        for (int i = 0; i < listMax.size(); i++) {
            HSSFRow dataRow = sheet.getRow(i + 1);
            LyqTable table = listMax.get(i);
            dataRow.createCell(0).setCellValue("第" + table.getLyqGroup() + "组");
            dataRow.createCell(1).setCellValue(table.getLyqKey());
            dataRow.createCell(2).setCellValue(table.getLyqValue());
        }
        log.info("处理完第三个组");
        for (int i = 0; i < type2VoListType.size(); i++) {
            HSSFRow dataRow = sheet.getRow(i + 1);
            dataRow.createCell(4).setCellValue("第" + type2VoListType.get(i).getGroup() + "组");
            dataRow.createCell(5).setCellValue(type2VoListType.get(i).getSeq());
            dataRow.createCell(6).setCellValue(type2VoListType.get(i).getKey());
            dataRow.createCell(7).setCellValue(type2VoListType.get(i).getValue());
        }
        log.info("处理完第四个组");
        return workbook;
    }

    /**
     * 9.3 最大的多条数据
     *
     * @param listMap
     * @param group
     * @return
     */
    public List<Type2Vo> sortType2(Map<Integer, List<LyqTable>> listMap, Integer group) {
        Map<Integer, Integer> map = new TreeMap<>();//默认根据key升序
        /**
         * 1.得到21组数据的和
         */
        for (Integer key : listMap.keySet()) {
            List<LyqTable> list = listMap.get(key);
            for (LyqTable lyqTable : list) {
                if (!map.containsKey(lyqTable.getLyqSeq())) {
                    map.put(lyqTable.getLyqSeq(), lyqTable.getLyqValue());
                } else {
                    Integer value = map.get(lyqTable.getLyqSeq()) + lyqTable.getLyqValue();
                    map.put(lyqTable.getLyqSeq(), value);
                }
            }
        }
        /**
         * 根据value排序
         */
        List<Map.Entry<Integer, Integer>> lists = new ArrayList<>(map.entrySet());
        lists.sort((o1, o2) -> o2.getValue() - o1.getValue());
        /**
         * 获取最大的数，如果重复就都取
         */
        List<Type2Vo> type2VoList = new LinkedList<>();
        //第一个先添加
        Type2Vo max = new Type2Vo();
        max.setGroup(group);
        max.setSeq(lists.get(0).getKey());
        max.setValue(lists.get(0).getValue());
        type2VoList.add(max);
        for (int i = 1; i < groupRow; i++) {
            if (max.getValue()==lists.get(i).getValue()){
                Type2Vo vo = new Type2Vo();
                vo.setGroup(group);
                vo.setSeq(lists.get(i).getKey());
                vo.setValue(lists.get(i).getValue());
                type2VoList.add(vo);
            }else {
                break;
            }
        }
        return type2VoList;
    }

    /**
     * 9.2对每一组的更新
     * 需要知道前一组的情况
     *
     * @param copyList 前一组的list
     * @return 这一组的list
     */
    public List<LyqTable> copySort(List<LyqTable> copyList, Integer gruop) {
        //long time1 = System.currentTimeMillis();
        //1.获取数据并且按照seq排序
        List<LyqTable> listgroupbySeq = dataMap.get(gruop);
        listgroupbySeq.sort((x, y) -> x.getSeq() - y.getSeq());
        //long time2 = System.currentTimeMillis();
        //print("查询一组数据,并排序", time1, time2);
        //2.把key 复给 下一组 带上0组的seq
        for (int i = 0; i < groupRow; i++) {
            if (listgroupbySeq.get(i).getSeq() != i + 1) {
                throw new RuntimeException("顺序不一致");
            }
            listgroupbySeq.get(i).setLyqKey(copyList.get(i).getLyqKey()); //把上一组的key给这一组
            listgroupbySeq.get(i).setLyqSeq(copyList.get(i).getLyqSeq()); //把第零组的序列付给每一组
        }
        //long time3 = System.currentTimeMillis();
        //print("赋值", time2, time3);
        //3.逆向排序
        listgroupbySeq.sort((x, y) -> y.getLyqValue() - x.getLyqValue());
        //long time4 = System.currentTimeMillis();
        //print("再次查询一组用时", time1, time4);
        return listgroupbySeq;
    }

    /**
     * 9.1 copy的初始化操作
     */
    public List<LyqTable> copyStart(List<Integer> anInt) {
        List<LyqTable> listgroupbySeq = dataMap.get(0);
        listgroupbySeq.sort((x, y) -> x.getSeq() - y.getSeq());
        //模拟Excel获取3000个数据 并且赋值
        for (int i = 0; i < groupRow; i++) {
            //上面已经排序过了 不需要判断了
            /*if (listgroupbySeq.get(i).getSeq() != i + 1) {
                throw new RuntimeException("顺序不一致");
            }*/
            listgroupbySeq.get(i).setLyqKey(anInt.get(i)); //把上一组的key给这一组
        }
        //变量排序
        listgroupbySeq.sort((x, y) -> y.getLyqValue() - x.getLyqValue());
        return listgroupbySeq;
    }

    /**
     * 分析
     * 分析2
     * 分析3
     * 分析4
     * @return
     */
    @ResponseBody
    @RequestMapping("/analyze")
    public CommonResponse analyze() {
        analyze2();
        return CommonResponse.success();
    }

    private void analyze2() {
        Integer maxGroup = groupInt / anzGroupNum;
        Integer start = 1;
        Integer groupNum = 1;
        for (; groupNum <= maxGroup; groupNum ++) {
            // 一大组
            List<LyqTable> groupTables = new LinkedList<>();
            for (; start <= groupNum * anzGroupNum; start ++) {
                List<LyqTable> lyqTables = dataMap.get(start);
                // 每一组进行排序
                lyqTables.sort((a, b) -> b.getLyqValue() - a.getLyqValue());
                groupTables.addAll(lyqTables);
            }
            // 排序一大组根据变量
            groupTables.sort((a, b) -> b.getLyqValue() - a.getLyqValue());
        }

    }

    /**
     * 8.上传数据
     */
    @ResponseBody
    @RequestMapping("/upload")
    private CommonResponse getIntByFile(MultipartFile file) throws IOException, DataCheckException {
        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
        mainUtils.resolver1(workbook);
        mainUtils.resolver2(workbook);
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
     * 8.2  第二步单独操作
     *
     * @param file
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/upload2")
    private CommonResponse getIntByFile2(MultipartFile file) throws IOException, DataCheckException {
        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
        HSSFSheet sheet = workbook.getSheetAt(0);
        HSSFRow row = sheet.getRow(1);
        HSSFCell cellDateNum = row.getCell(0);
        cellDateNum.setCellType(HSSFCell.CELL_TYPE_STRING);
        String dateNum = cellDateNum.getStringCellValue();
        //判断日期是否重复
        long count = lyqDateList.stream().filter(x -> x.getDateNum().equals(dateNum)).count();
        if (count > 0) {
            return CommonResponse.fail("期号重复了！！！");
        }
        log.info("获取上传日期" + dateNum);
        LyqDate lyqDate = new LyqDate();
        lyqDate.setDateNum(dateNum);
        lyqDate.setValue("-");
        lyqDateList.add(lyqDate);

        appContent.setUpDateNum(dateNum);
        appContent.setUpNumsStr("-");
        appContent.setUpNums(new ArrayList<>());
        mainUtils.resolver2(workbook);
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
        if (checkMap(dataMap).getCode() == 0) {
            throw new DataCheckException();
        }
        lyqDate.setDateNum(lyqDate.getDateNum().trim());
        String ids = lyqDate.getValue();
        long count = lyqDateList.stream().filter(x -> x.getDateNum().equals(lyqDate.getDateNum())).count();
        if (count > 0) {
            return CommonResponse.fail("期号重复了！！！");
        }
        //把所有的数据改为0 其他的++  然后把日期号存入数据库
        updateToZore(ids.trim());
        lyqDateList.add(lyqDate);
        //lyqDate.insert();
        return CommonResponse.success();
    }

    public void updateToZore(String trim) {
        //变为0 array
        String[] split = trim.split(",");
        Integer[] ids = new Integer[split.length];
        for (int i = 0; i < split.length; i++) {
            ids[i] = Integer.valueOf(split[i].trim());
        }
        long time1 = System.currentTimeMillis();
        Iterator<Integer> iterator = dataMap.keySet().iterator();
        iterator.forEachRemaining(group -> {
            List<LyqTable> lyqTables = dataMap.get(group);
            List<LyqTable> collect = lyqTables.stream()
                    .map(table -> {
                        if (Arrays.asList(ids).contains(table.getLyqKey()))
                            table.setLyqValue(0);
                        else
                            table.setLyqValue(table.getLyqValue() + 1);
                        return table;
                    }).collect(Collectors.toList());
            dataMap.put(group, collect);
        });
        //Integer row1 = lyqTableMapper.updateToZore(ids);
        long time2 = System.currentTimeMillis();
        print("更新操作（归零和加一）用时：", time1, time2);
    }

    /**
     * 3.查询日期list
     */
    @ResponseBody
    @RequestMapping(value = "/replace/list")
    public Map<String, Object> replacelist(Integer page, Integer rows) {
        if (lyqDateList.size() == 0) {
            log.info("内存没有数据，请添加数据...");
            map.clear();
            map.put("rows", null);
            map.put("total", 0);
            return map;
        }
        /**
         * 流的排序分页操作
         */
        List<LyqDate> collect = lyqDateList.stream()
                .sorted((x, y) -> y.getDateNum().compareTo(x.getDateNum()))
                .skip((page - 1) * rows).limit(rows).parallel().collect(Collectors.toList());
        map.clear();
        map.put("rows", collect);
        map.put("total", lyqDateList.size());
        return map;
    }

    /**
     * 查询分析结果
     */
    @ResponseBody
    @RequestMapping(value = "/anz/list")
    public Map<String, Object> anzSearch(Integer page, Integer rows) {
        if (anzList.size() == 0) {
            log.info("内存没有数据，请添加数据...");
            map.clear();
            map.put("rows", null);
            map.put("total", 0);
            return map;
        }
        /**
         * 流的排序分页操作
         */
        List<LyqAnalyzeVo> collect = anzList.stream()
                .sorted((x, y) -> x.getGroup().compareTo(y.getGroup()))
                .map(analyze -> LyqAnalyzeVo.toVo(analyze, "第"+analyze.getGroup()+"组"))
                .skip((page - 1) * rows).limit(rows).parallel().collect(Collectors.toList());
        map.clear();
        map.put("rows", collect);
        map.put("total", anzList.size());
        return map;
    }

    /**
     * 查询分析结果
     */
    @ResponseBody
    @RequestMapping(value = "/top/list")
    public Map<String, Object> topSearch() {
        if (anzList.size() == 0) {
            log.info("内存没有数据，请添加数据...");
            map.clear();
            map.put("rows", null);
            map.put("total", 0);
            return map;
        }
        /**
         * 流的排序分页操作
         */
        List<LyqAnalyzeVo> ret = new LinkedList<>();
        List<LyqAnalyzeVo> maxRetList = currentAnzMaxTop.stream()
                .sorted((x, y) -> x.getGroup().compareTo(y.getGroup()))
                .map(analyze -> LyqAnalyzeVo.toVo(analyze, "当前最大榜-第"+analyze.getGroup()+"名"))
                .collect(Collectors.toList());
        List<LyqAnalyzeVo> minRetList = currentAnzMaxTop.stream()
                .sorted((x, y) -> x.getGroup().compareTo(y.getGroup()))
                .map(analyze -> LyqAnalyzeVo.toVo(analyze, "当前最小榜-第"+analyze.getGroup()+"名"))
                .collect(Collectors.toList());
        ret.add(LyqAnalyzeVo.toVo(maxAnalyze, "最大"));
        ret.add(LyqAnalyzeVo.toVo(minAnalyze, "最小"));
        ret.addAll(maxRetList);
        ret.addAll(minRetList);
        map.clear();
        map.put("rows", ret);
        map.put("total", 22);
        return map;
    }

    /**
     * 4.查询内存中的dataMap
     */
    @ResponseBody
    @RequestMapping(value = "/copy/list")
    public Map<String, Object> copylist(Integer page, Integer rows,
                                        @RequestParam(value = "way", defaultValue = "DESC") Way way) throws DataCheckException {
        Map<Integer, List<LyqTable>> dataMaps = getWay(way);
        if (checkMap(dataMaps).getCode() == 0) {
            log.info("内存没有数据，请添加数据...");
            map.clear();
            map.put("rows", null);
            map.put("total", 0);
            return map;
        }
        List<LyqTable> lyqTables = dataMaps.get(page - 1);
        List<LyqTable> collect = lyqTables.stream()
                .sorted((x, y) -> x.getSeq() - y.getSeq())
                .limit(rows).collect(Collectors.toList());
        map.clear();
        map.put("rows", collect);
        map.put("total", dataMaps.size() * rows);
        return map;
    }

    private Map<Integer, List<LyqTable>> getWay(Way way) {
        if (Way.ASC.equals(way)) {
            return dataMap;
        } else if (Way.DESC.equals(way)) {
            return ascDataMap;
        }
        return null;
    }


    /**
     * 2.初始化内存数据
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/init/data")
    public CommonResponse initInsert() {
        dataMap.clear();
        long time1 = System.currentTimeMillis();
        for (int i = 0; i < groupInt; i++) {
            List<LyqTable> init3000 = getInit3000(i);
            List<LyqTable> init3000Asc = getInit3000(i);
            dataMap.put(i, init3000);
            ascDataMap.put(i, init3000Asc);
        }
        long time2 = System.currentTimeMillis();

        int maxGroupAnz = groupInt / anzGroupNum;
        for (int i = 0; i < maxGroupAnz; i ++) {
            LyqAnalyze analyze = LyqAnalyze.getNewObj(i + 1);
            anzList.add(analyze);
        }

        maxAnalyze = LyqAnalyze.getNewObj(0);
        minAnalyze = LyqAnalyze.getNewObj(0);
        maxMinAnalyze.add(maxAnalyze);
        maxMinAnalyze.add(minAnalyze);
        for (int i = 1; i <= 10; i ++) {
            currentAnzMaxTop.add(LyqAnalyze.getNewObj(i));
            currentAnzMinTop.add(LyqAnalyze.getNewObj(i));
        }
        print(dataMap.size()+"组数据初始化完成，添加到内存总耗时：", time1, time2);
        return CommonResponse.success();
    }


    public List<LyqTable> getInit3000(Integer group) {
        List<LyqTable> list = new LinkedList<>();
        for (int i = 0; i < groupRow; i++) {
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
        if (dataMaps.size() == groupInt) {
            return CommonResponse.success("数据合法");
        }
        log.info(dataMaps.size()+"组检验不足"+groupInt+"组");
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
        sheet.setColumnWidth(14, 1440 * 2);
        sheet.setColumnWidth(15, 1440 * 2);
        sheet.setColumnWidth(16, 1440 * 2);
        sheet.setColumnWidth(17, 1440 * 2);
        sheet.setColumnWidth(18, 1440 * 2);
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
        row.createCell(14).setCellValue("第一个参数");
        row.createCell(15).setCellValue("第二个参数");
        row.createCell(16).setCellValue("第三个参数");
        row.createCell(17).setCellValue("第四个参数");
        row.createCell(18).setCellValue("第五个参数");
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
        row1.createCell(14).setCellValue(0);
        row1.createCell(15).setCellValue(2);
        row1.createCell(16).setCellValue(4);
        row1.createCell(17).setCellValue(6);
        row1.createCell(18).setCellValue(9);
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
        appContent.setLyqDateList(lyqDateList);
        return mainUtils.downFile();
    }

    /**
     * 上传数据
     */
    @ResponseBody
    @RequestMapping(value = "/get/obj")
    public CommonResponse getobj() {
        CommonResponse commonResponse = mainUtils.readFile();
        init();
        return commonResponse;
    }

    /**
     * 随机生成一组数
     * @return
     */
    public List<Integer> get3000Int() {
        Random random = new Random();
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < groupRow; i++) {
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
        log.info("开始打印data数据 " + dataMap.size());
        Iterator<Integer> iterator = dataMap.keySet().iterator();
        iterator.forEachRemaining(key -> log.info(dataMap.get(key).toString()));
    }

    @Resource
    private MainUtils mainUtils;

    @Resource
    private AppContent appContent;

    @PostConstruct
    public void init() {
        groupRow = appContent.getGroupRow();
        groupInt = appContent.getGroupInt();
        groupNum = appContent.getGroupNum();
        anzGroupNum = appContent.getAnzGroupNum();
        dataMap = appContent.getDataMap();
        ascDataMap = appContent.getAscDataMap();
        lyqDateList = appContent.getLyqDateList();
        anzList = appContent.getAnzList();
        maxMinAnalyze = appContent.getMaxMinAnalyze();
        if (maxMinAnalyze != null && maxMinAnalyze.size() == 2) {
            maxAnalyze = maxMinAnalyze.get(0);
            minAnalyze = maxMinAnalyze.get(1);
        }
        currentAnzMaxTop = appContent.getCurrentAnzMaxTop();
        currentAnzMinTop = appContent.getCurrentAnzMinTop();
    }

    /*多少组  300W 多1 默认整万 + 1 个数*/
    public Integer groupInt;
    /*多少条数据 10*/
    public Integer groupRow;
    /*多少组在分一组 21*/
    public Integer groupNum = 21;
    /*分析数据为多少一组 v2*/
    public Integer anzGroupNum = 19;
    // 降序map 和 升序map
    public Map<Integer, List<LyqTable>> dataMap;
    public Map<Integer, List<LyqTable>> ascDataMap;
    // 日期list
    public List<LyqDate> lyqDateList;
    // 分析数据
    public List<LyqAnalyze> maxMinAnalyze;
    public List<LyqAnalyze> anzList;
    public LyqAnalyze maxAnalyze;
    public LyqAnalyze minAnalyze;
    public List<LyqAnalyze> currentAnzMaxTop;
    public List<LyqAnalyze> currentAnzMinTop;

    // 负责返回数据用的
    private Map<String, Object> map = new HashMap<>();

    /**
     * 随机对象
     */
    private Random random = new Random();
}
