package vip.bzsy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * @author ：李延富
 * @date ：Created in 2019/10/13 14:21
 * description ：
 */
@Data
@AllArgsConstructor
@ToString
public class LyqAnalyze {
    /**
     * 一大组
     */
    private Integer group;

    /**
     * 分析二
     */
    private Integer maxDesc2;
    private Integer maxAsc2;
    private Integer minDesc2;
    private Integer minAsc2;

    /**
     * 分析3
     */
    private Integer maxDesc3;
    private Integer maxAsc3;
    private Integer minDesc3;
    private Integer minAsc3;

    /**
     * 分析4
     */
    private Integer Desc4;
    private Integer Asc4;
}
