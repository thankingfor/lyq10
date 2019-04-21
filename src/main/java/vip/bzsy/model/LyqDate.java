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
public class LyqDate implements  Serializable{

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 期号
     */
    private String dateNum;

    /**
     * 上传的值
     */
    private String value;


}
