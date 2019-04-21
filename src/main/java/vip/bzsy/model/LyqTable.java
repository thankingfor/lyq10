package vip.bzsy.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author lyf
 * @since 2019-04-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ToString
public class LyqTable implements Serializable{

    private static final long serialVersionUID = 1L;

    private transient Integer id;

    private Integer seq;

    private Integer lyqGroup;

    private Integer lyqKey;

    private Integer lyqValue;

    private Integer lyqSeq;

}
