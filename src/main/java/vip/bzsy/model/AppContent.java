package vip.bzsy.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

/**
 * @author ：李延富
 * @date ：Created in 2019/10/12 22:37
 * description ：
 */
@Data
@Component
public class AppContent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 定义
     */
    /*多少组  300W 多1 默认整万 + 1 个数*/
    public transient Integer groupInt = 1500001;
    /*多少条数据 10*/
    public transient Integer groupRow = 10;
    /*多少组在分一组 21*/
    public transient Integer groupNum = 21;
    /*分析数据为多少一组 v2*/
    public transient Integer anzGroupNum = 19;

    /**
     * 存放上传的数据 10个中的数字
     */
    private String upDateNum;
    private List<Integer> upNums;
    private String upNumsStr;
    private List<Integer> upList10;


    /**
     * 日期对象
     */
    private List<LyqDate> lyqDateList = new ArrayList<>();

    /**
     * 降序的主要数组
     */
    private transient Map<Integer, List<LyqTable>> dataMap = new HashMap<>();
    private String[] DescArray;

    /**
     * 升序的主要数组
     */
    private transient Map<Integer, List<LyqTable>> AscDataMap = new HashMap<>();
    private String[] AscArray;

    /**
     * 分析的主要数据
     */
    public List<LyqAnalyze> anzList = new LinkedList<>();
    public List<LyqAnalyze> maxMinAnalyze = new LinkedList<>();
    public transient LyqAnalyze maxAnalyze = new LyqAnalyze();
    public transient LyqAnalyze minAnalyze = new LyqAnalyze();
    public List<LyqAnalyze> currentAnzMaxTop = new LinkedList<>();
    public List<LyqAnalyze> currentAnzMinTop = new LinkedList<>();

    private transient String dataPath = "D:/lyq10/data3w";
    private transient String dataPathData = "D:/lyq10/data3w/data";

}
