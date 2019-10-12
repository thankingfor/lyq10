package vip.bzsy.model;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author ：李延富
 * @date ：Created in 2019/10/12 22:37
 * description ：
 */
@Component
@Data
public class AppContent {

    /**
     * 降序的主要数组
     */
    private String[] DescArray;

    /**
     * 升序的主要数组
     */
    private String[] AscArray;
}
