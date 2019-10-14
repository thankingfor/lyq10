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
public class LyqDate implements Serializable {

    /**
     * 期号
     */
    private String dateNum;

    /**
     * 上传的值
     */
    private String value;

}
