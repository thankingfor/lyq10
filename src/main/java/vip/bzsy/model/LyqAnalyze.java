package vip.bzsy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author ：李延富
 * @date ：Created in 2019/10/13 14:21
 * description ：
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LyqAnalyze implements Serializable {
    /**
     * 一大组
     */
    private Integer group;

    /**
     * 分析二
     */
    private Integer maxDesc2;
    private Integer maxAsc2;
    private Integer maxRet2;
    private Integer minDesc2;
    private Integer minAsc2;
    private Integer minRet2;

    /**
     * 分析3
     */
    private Integer maxDesc3;
    private Integer maxAsc3;
    private Integer maxRet3;
    private Integer minDesc3;
    private Integer minAsc3;
    private Integer minRet3;

    /**
     * 分析4
     */
    private Integer desc4;
    private Integer asc4;
    private Integer ret4;

    public static LyqAnalyze getNewObj(Integer group) {
        return new LyqAnalyze(group,
                0, 0,0,0, 0, 0,
                0, 0,0,0, 0, 0,
                0, 0,0);
    }

    public static LyqAnalyze clone(LyqAnalyze lyqAnalyze) {
        LyqAnalyze analyze = new LyqAnalyze();
        analyze.setGroup(lyqAnalyze.getGroup());

        analyze.setMaxDesc2(lyqAnalyze.getMaxDesc2());
        analyze.setMaxAsc2(lyqAnalyze.getMaxAsc2());
        analyze.setMinDesc2(lyqAnalyze.getMinDesc2());
        analyze.setMinAsc2(lyqAnalyze.getMinAsc2());
        analyze.setMaxRet2(lyqAnalyze.getMaxRet2());
        analyze.setMinRet2(lyqAnalyze.getMinRet2());

        analyze.setMaxDesc3(lyqAnalyze.getMaxDesc3());
        analyze.setMaxAsc3(lyqAnalyze.getMaxAsc3());
        analyze.setMinDesc3(lyqAnalyze.getMinDesc3());
        analyze.setMinAsc3(lyqAnalyze.getMinAsc3());
        analyze.setMaxRet3(lyqAnalyze.getMaxRet3());
        analyze.setMinRet3(lyqAnalyze.getMinRet3());

        analyze.setDesc4(lyqAnalyze.getDesc4());
        analyze.setAsc4(lyqAnalyze.getAsc4());
        analyze.setRet4(lyqAnalyze.getRet4());
        return analyze;
    }
}
