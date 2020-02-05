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
    public transient Integer groupInt = 300001;
    /*多少条数据 10*/
    public transient Integer groupRow = 10;
    /*版本*/
    public transient String version = "v3";

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
    private Map<Integer, List<LyqTable>> dataMap = new HashMap<>();

    /**
     * 升序的主要数组
     */
    private Map<Integer, List<LyqTable>> AscDataMap = new HashMap<>();

    private transient String dataPath = "D:/lyq10/"+version+"/"+groupInt;
    private transient String dataPathData = "D:/lyq10/"+version+"/"+groupInt+"/data";

    private Map<String, Integer> map252 = new HashMap<>();
}
