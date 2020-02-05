package vip.bzsy.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author lyf
 * @since 2019-04-01
 */
@Data
@ToString
public class LyqTable implements Serializable {

    private Integer seq;

    private Integer lyqGroup;

    private Integer lyqKey;

    private Integer lyqValue;

    private Integer lyqSeq;

    private String lyq252;

}
