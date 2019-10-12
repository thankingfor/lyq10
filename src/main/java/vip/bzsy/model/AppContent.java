package vip.bzsy.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 日期对象
     */
    private transient List<LyqDate> lyqDateList;
    private String lyqDateListStr;

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

    private transient String dataPath = "D:/lyq10";
    private transient String dataPathData = "D:/lyq10/data";

}
