package vip.bzsy.model;

import lombok.Data;
import lombok.ToString;

/**
 * @author lyf
 * @since 2019-04-01
 */
@Data
@ToString
public class LyqTable {

    private Integer seq;

    private Integer lyqGroup;

    private Integer lyqKey;

    private Integer lyqValue;

    private Integer lyqSeq;

}
