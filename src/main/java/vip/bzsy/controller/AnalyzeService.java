package vip.bzsy.controller;

import org.springframework.stereotype.Service;
import vip.bzsy.model.AppContent;
import vip.bzsy.model.LyqAnalyze;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ：李延富
 * @date ：Created in 2019/10/13 12:36
 * description ：
 */
@Service
public class AnalyzeService {

    @Resource
    private AppContent appContent;

    /**
     * 第一步 把anzList 相等的一对  属于5个数字的增加或者减少
     */
    public void sept1() {
        List<LyqAnalyze> anzList = appContent.getAnzList();
        for (LyqAnalyze analyze: anzList) {
            if (analyze.getMaxAsc2() == analyze.getMaxDesc2() && isInArr(analyze.getMaxAsc2())) {
                analyze.setMaxRet2(analyze.getMaxRet2() + 1);
            } else {
                analyze.setMaxRet2(1);
            }

            if (analyze.getMinAsc2() == analyze.getMinDesc2() && isInArr(analyze.getMinAsc2())) {
                analyze.setMinRet2(analyze.getMinRet2() - 1);
            } else {
                analyze.setMinRet2(-1);
            }

            if (analyze.getMaxAsc3() == analyze.getMaxDesc3() && isInArr(analyze.getMaxAsc3())) {
                analyze.setMaxRet3(analyze.getMaxRet3() + 1);
            } else {
                analyze.setMaxRet3(1);
            }

            if (analyze.getMinAsc3() == analyze.getMinDesc3() && isInArr(analyze.getMinAsc3())) {
                analyze.setMinRet3(analyze.getMinRet3() - 1);
            } else {
                analyze.setMinRet3(-1);
            }

            if (analyze.getDesc4() == analyze.getAsc4() && isInArr(analyze.getDesc4())) {
                analyze.setRet4(analyze.getRet4() + 1);
            } else {
                analyze.setRet4(1);
            }
        }
    }

    private boolean isInArr(Integer num) {
        return appContent.getUpNums().contains(num);
    }

    /**
     * 归零和加一操作
     */
    public void sept2() {

    }

}
