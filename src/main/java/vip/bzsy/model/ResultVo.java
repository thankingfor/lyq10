package vip.bzsy.model;

import lombok.Data;

import java.util.List;

/**
 * @author ：李延富
 * @date ：Created in 2020/1/17 20:32
 * description ：
 */
@Data
public class ResultVo {
    private Integer group;
    private Integer key1;
    private Integer value1;
    private Integer key2;
    private Integer value2;

    public static ResultVo toVo(List<LyqTable> listLyq) {
        ResultVo resultVo = new ResultVo();
        resultVo.setGroup(listLyq.get(0).getLyqGroup());
        resultVo.setKey1(listLyq.get(0).getLyqKey());
        resultVo.setValue1(listLyq.get(0).getLyqValue());
        resultVo.setKey2(listLyq.get(1).getLyqKey());
        resultVo.setValue2(listLyq.get(1).getLyqValue());
        return resultVo;
    }

    public static ResultVo toVoASC(List<LyqTable> listLyq) {
        ResultVo resultVo = new ResultVo();
        resultVo.setGroup(listLyq.get(9).getLyqGroup());
        resultVo.setKey1(listLyq.get(9).getLyqKey());
        resultVo.setValue1(listLyq.get(9).getLyqValue());
        resultVo.setKey2(listLyq.get(8).getLyqKey());
        resultVo.setValue2(listLyq.get(8).getLyqValue());
        return resultVo;
    }
}
