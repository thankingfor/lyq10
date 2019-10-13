package vip.bzsy.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author ：李延富
 * @date ：Created in 2019/10/13 21:12
 * description ：
 */
@Data
@AllArgsConstructor
public class Analyze4Vo {
    private Integer maxGroup;
    private Integer minGroup;
    private Integer maxKey;
    private Integer maxValue;
    private Integer minKey;
    private Integer minValue;
}
