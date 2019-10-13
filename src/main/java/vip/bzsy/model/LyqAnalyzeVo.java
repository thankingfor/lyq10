package vip.bzsy.model;

import lombok.Data;

/**
 * @author ：李延富
 * @date ：Created in 2019/10/13 16:19
 * description ：
 */
@Data
public class LyqAnalyzeVo {
    private String group;
    private String max2;
    private String min2;
    private String max3;
    private String min3;
    private String max4;

    public static LyqAnalyzeVo toVo(LyqAnalyze analyze, String group) {
        LyqAnalyzeVo analyzeVo = new LyqAnalyzeVo();
        analyzeVo.setGroup(group);
        analyzeVo.setMax2("("+analyze.getMaxDesc2()+","+analyze.getMaxAsc2()+") = " + analyze.getMaxRet2());
        analyzeVo.setMin2("("+analyze.getMinDesc2()+","+analyze.getMinAsc2()+") = " + analyze.getMinRet2());
        analyzeVo.setMax3("("+analyze.getMaxDesc3()+","+analyze.getMaxAsc3()+") = " + analyze.getMaxRet3());
        analyzeVo.setMin3("("+analyze.getMinDesc3()+","+analyze.getMinAsc3()+") = " + analyze.getMinRet3());
        analyzeVo.setMax4("("+analyze.getDesc4()+","+analyze.getAsc4()+") = " + analyze.getRet4());
        return analyzeVo;
    }
}
