package vip.bzsy.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vip.bzsy.model.AppContent;
import vip.bzsy.model.LyqAnalyze;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ：李延富
 * @date ：Created in 2019/10/13 12:36
 * description ：
 */
@Service
@Slf4j
public class AnalyzeService {

    @Resource
    private BTotalController bTotalController;

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
            } else if (analyze.getMaxAsc2() == analyze.getMaxDesc2() && !isInArr(analyze.getMaxAsc2())){
                analyze.setMaxRet2(analyze.getMaxRet2() - 1);
            } else {
                analyze.setMaxRet2(0);
            }

            if (analyze.getMinAsc2() == analyze.getMinDesc2() && isInArr(analyze.getMinAsc2())) {
                analyze.setMinRet2(analyze.getMinRet2() + 1);
            } else if (analyze.getMinAsc2() == analyze.getMinDesc2() && !isInArr(analyze.getMinAsc2())){
                analyze.setMinRet2(analyze.getMinRet2() - 1);
            } else {
                analyze.setMinRet2(0);
            }

            if (analyze.getMaxAsc3() == analyze.getMaxDesc3() && isInArr(analyze.getMaxAsc3())) {
                analyze.setMaxRet3(analyze.getMaxRet3() + 1);
            } else if (analyze.getMaxAsc3() == analyze.getMaxDesc3() && !isInArr(analyze.getMaxAsc3())){
                analyze.setMaxRet3(analyze.getMaxRet3() - 1);
            } else {
                analyze.setMaxRet3(0);
            }

            if (analyze.getMinAsc3() == analyze.getMinDesc3() && isInArr(analyze.getMinAsc3())) {
                analyze.setMinRet3(analyze.getMinRet3() - 1);
            } else if (analyze.getMinAsc3() == analyze.getMinDesc3() && !isInArr(analyze.getMinAsc3())){
                analyze.setMinRet3(analyze.getMinRet3() - 1);
            } else {
                analyze.setMinRet3(0);
            }

            if (analyze.getDesc4() == analyze.getAsc4() && isInArr(analyze.getDesc4())) {
                analyze.setRet4(analyze.getRet4() + 1);
            } else if (analyze.getDesc4() == analyze.getAsc4() && !isInArr(analyze.getDesc4())){
                analyze.setRet4(analyze.getRet4() - 1);
            } else {
                analyze.setRet4(0);
            }
        }
    }

    private boolean isInArr(Integer num) {
        return appContent.getUpNums().contains(num);
    }

    private List<LyqAnalyze> clone(List<LyqAnalyze> list) {
        List<LyqAnalyze> listAnz = new LinkedList<>();
        for (LyqAnalyze analyze: list) {
            listAnz.add(LyqAnalyze.clone(analyze));
        }
        return listAnz;
    }

    /**
     * 最后的分析操作
     */
    public void sept2() {
        List<LyqAnalyze> anzList = appContent.getAnzList();
        List<LyqAnalyze> currentAnzMaxTop = appContent.getCurrentAnzMaxTop();
        List<LyqAnalyze> currentAnzMinTop = appContent.getCurrentAnzMinTop();
        LyqAnalyze maxAnalyze = appContent.getMaxMinAnalyze().get(0);
        LyqAnalyze minAnalyze = appContent.getMaxMinAnalyze().get(1);

        // 获取4个的最大值
        // -------------------分析二 MAX -----------------------
        List<LyqAnalyze> cloneRet1 = clone(anzList);
        List<LyqAnalyze> ret1 = cloneRet1.stream()
                .filter(analyze -> {
                    if (analyze.getMaxDesc2() == analyze.getMaxAsc2()){
                        return true;
                    }
                    return false;
                })
                .sorted((x,y) -> y.getMaxRet2() - x.getMaxRet2())
                .collect(Collectors.toList());
        for (int i = 0; i < 10 && i < ret1.size(); i ++) {
            currentAnzMaxTop.get(i).setMaxDesc2(ret1.get(i).getMaxDesc2());
            currentAnzMaxTop.get(i).setMaxAsc2(ret1.get(i).getMaxAsc2());
            currentAnzMaxTop.get(i).setMaxRet2(ret1.get(i).getMaxRet2());
            if (i == 0) {
                LyqAnalyze currentAnalyze = currentAnzMaxTop.get(0);
                if (maxAnalyze.getMaxRet2() < currentAnalyze.getMaxRet2()) {
                    maxAnalyze.setMaxDesc2(currentAnalyze.getMaxDesc2());
                    maxAnalyze.setMaxAsc2(currentAnalyze.getMaxAsc2());
                    maxAnalyze.setMaxRet2(currentAnalyze.getMaxRet2());
                }
            }
        }
        for (int i = ret1.size() - 1, j = 0; i >= ret1.size() - 10 && i >= 0; i --, j ++) {
            currentAnzMinTop.get(j).setMaxDesc2(ret1.get(i).getMaxDesc2());
            currentAnzMinTop.get(j).setMaxAsc2(ret1.get(i).getMaxAsc2());
            currentAnzMinTop.get(j).setMaxRet2(ret1.get(i).getMaxRet2());
            if (i == ret1.size() - 1) {
                LyqAnalyze currentAnalyze = currentAnzMinTop.get(0);
                if (minAnalyze.getMaxRet2() > currentAnalyze.getMaxRet2()) {
                    minAnalyze.setMaxDesc2(currentAnalyze.getMaxDesc2());
                    minAnalyze.setMaxAsc2(currentAnalyze.getMaxAsc2());
                    minAnalyze.setMaxRet2(currentAnalyze.getMaxRet2());
                }
            }
        }
        log.info("消息分析 分析二 MAX 完毕");
        // -------------------分析二 MIN -----------------------
        List<LyqAnalyze> cloneRet2 = clone(anzList);
        List<LyqAnalyze> ret2 = cloneRet2.stream()
                .filter(analyze -> {
                    if (analyze.getMinDesc2() == analyze.getMinAsc2()){
                        return true;
                    }
                    return false;
                })
                .sorted((x,y) -> y.getMinRet2() - x.getMinRet2())
                .collect(Collectors.toList());
        for (int i = 0; i < 10 && i < ret2.size(); i ++) {
            currentAnzMaxTop.get(i).setMinDesc2(ret2.get(i).getMinDesc2());
            currentAnzMaxTop.get(i).setMinAsc2(ret2.get(i).getMinAsc2());
            currentAnzMaxTop.get(i).setMinRet2(ret2.get(i).getMinRet2());
            if (i == 0) {
                LyqAnalyze currentAnalyze = currentAnzMaxTop.get(0);
                if (maxAnalyze.getMinRet2() < currentAnalyze.getMinRet2()) {
                    maxAnalyze.setMinDesc2(currentAnalyze.getMinDesc2());
                    maxAnalyze.setMinAsc2(currentAnalyze.getMinAsc2());
                    maxAnalyze.setMinRet2(currentAnalyze.getMinRet2());
                }
            }
        }
        for (int i = ret2.size() - 1, j = 0; i >= ret2.size() - 10 && i >= 0; i --, j ++) {
            currentAnzMinTop.get(j).setMinDesc2(ret2.get(i).getMinDesc2());
            currentAnzMinTop.get(j).setMinAsc2(ret2.get(i).getMinAsc2());
            currentAnzMinTop.get(j).setMinRet2(ret2.get(i).getMinRet2());
            if (i == ret2.size() - 1) {
                LyqAnalyze currentAnalyze = currentAnzMinTop.get(0);
                if (minAnalyze.getMinRet2() > currentAnalyze.getMinRet2()) {
                    minAnalyze.setMinDesc2(currentAnalyze.getMinDesc2());
                    minAnalyze.setMinAsc2(currentAnalyze.getMinAsc2());
                    minAnalyze.setMinRet2(currentAnalyze.getMinRet2());
                }
            }
        }
        log.info("消息分析 分析二 MIN 完毕");
        // -------------------分析三 MAX -----------------------
        List<LyqAnalyze> cloneRet3 = clone(anzList);
        List<LyqAnalyze> ret3 = cloneRet3.stream()
                .filter(analyze -> {
                    if (analyze.getMaxDesc3() == analyze.getMaxAsc3()){
                        return true;
                    }
                    return false;
                })
                .sorted((x,y) -> y.getMaxRet3() - x.getMaxRet3())
                .collect(Collectors.toList());
        for (int i = 0; i < 10 && i < ret3.size(); i ++) {
            currentAnzMaxTop.get(i).setMaxDesc3(ret3.get(i).getMaxDesc3());
            currentAnzMaxTop.get(i).setMaxAsc3(ret3.get(i).getMaxAsc3());
            currentAnzMaxTop.get(i).setMaxRet3(ret3.get(i).getMaxRet3());
            if (i == 0) {
                LyqAnalyze currentAnalyze = currentAnzMaxTop.get(0);
                if (maxAnalyze.getMaxRet3() < currentAnalyze.getMaxRet3()) {
                    maxAnalyze.setMaxDesc3(currentAnalyze.getMaxDesc3());
                    maxAnalyze.setMaxAsc3(currentAnalyze.getMaxAsc3());
                    maxAnalyze.setMaxRet3(currentAnalyze.getMaxRet3());
                }
            }
        }
        for (int i = ret3.size() - 1, j = 0; i >= ret3.size() - 10 && i >= 0; i --, j ++) {
            currentAnzMinTop.get(j).setMaxDesc3(ret3.get(i).getMaxDesc3());
            currentAnzMinTop.get(j).setMaxAsc3(ret3.get(i).getMaxAsc3());
            currentAnzMinTop.get(j).setMaxRet3(ret3.get(i).getMaxRet3());
            if (i == ret3.size() - 1) {
                LyqAnalyze currentAnalyze = currentAnzMinTop.get(0);
                if (minAnalyze.getMaxRet3() > currentAnalyze.getMaxRet3()) {
                    minAnalyze.setMaxDesc3(currentAnalyze.getMaxDesc3());
                    minAnalyze.setMaxAsc3(currentAnalyze.getMaxAsc3());
                    minAnalyze.setMaxRet3(currentAnalyze.getMaxRet3());
                }
            }
        }
        log.info("消息分析 分析三 MAX 完毕");
        // -------------------分析三 MIN -----------------------
        List<LyqAnalyze> cloneRet4 = clone(anzList);
        List<LyqAnalyze> ret4 = cloneRet4.stream()
                .filter(analyze -> {
                    if (analyze.getMinDesc3() == analyze.getMinAsc3()){
                        return true;
                    }
                    return false;
                })
                .sorted((x,y) -> y.getMinRet3() - x.getMinRet3())
                .collect(Collectors.toList());

        for (int i = 0; i < 10 && i < ret4.size(); i ++) {
            currentAnzMaxTop.get(i).setMinDesc3(ret4.get(i).getMinDesc3());
            currentAnzMaxTop.get(i).setMinAsc3(ret4.get(i).getMinAsc3());
            currentAnzMaxTop.get(i).setMinRet3(ret4.get(i).getMinRet3());
            if (i == 0) {
                LyqAnalyze currentAnalyze = currentAnzMaxTop.get(0);
                if (maxAnalyze.getMinRet3() < currentAnalyze.getMinRet3()) {
                    maxAnalyze.setMinDesc3(currentAnalyze.getMinDesc3());
                    maxAnalyze.setMinAsc3(currentAnalyze.getMinAsc3());
                    maxAnalyze.setMinRet3(currentAnalyze.getMinRet3());
                }
            }
        }
        for (int i = ret4.size() - 1, j = 0; i >= ret4.size() - 10 && i >= 0; i --, j ++) {
            currentAnzMinTop.get(j).setMinDesc3(ret4.get(i).getMinDesc3());
            currentAnzMinTop.get(j).setMinAsc3(ret4.get(i).getMinAsc3());
            currentAnzMinTop.get(j).setMinRet3(ret4.get(i).getMinRet3());
            if (i == ret4.size() - 1) {
                LyqAnalyze currentAnalyze = currentAnzMinTop.get(0);
                if (minAnalyze.getMinRet3() > currentAnalyze.getMinRet3()) {
                    minAnalyze.setMinDesc3(currentAnalyze.getMinDesc3());
                    minAnalyze.setMinAsc3(currentAnalyze.getMinAsc3());
                    minAnalyze.setMinRet3(currentAnalyze.getMinRet3());
                }
            }
        }
        log.info("消息分析 分析三 MIN 完毕");
        // -------------------分析四 MAX -----------------------
        List<LyqAnalyze> cloneRet5 = clone(anzList);
        List<LyqAnalyze> ret5 = cloneRet5.stream()
                .filter(analyze -> {
                    if (analyze.getDesc4() == analyze.getAsc4()){
                        return true;
                    }
                    return false;
                })
                .sorted((x,y) -> y.getRet4() - x.getRet4())
                .collect(Collectors.toList());

        for (int i = 0; i < 10 && i < ret5.size(); i ++) {
            currentAnzMaxTop.get(i).setDesc4(ret5.get(i).getDesc4());
            currentAnzMaxTop.get(i).setAsc4(ret5.get(i).getAsc4());
            currentAnzMaxTop.get(i).setRet4(ret5.get(i).getRet4());
            if (i == 0) {
                LyqAnalyze currentAnalyze = currentAnzMaxTop.get(0);
                if (maxAnalyze.getRet4() < currentAnalyze.getRet4()) {
                    maxAnalyze.setDesc4(currentAnalyze.getDesc4());
                    maxAnalyze.setAsc4(currentAnalyze.getAsc4());
                    maxAnalyze.setRet4(currentAnalyze.getRet4());
                }
            }
        }
        for (int i = ret5.size() - 1, j = 0; i >= ret5.size() - 10 && i >= 0; i --, j ++) {
            currentAnzMinTop.get(j).setDesc4(ret5.get(i).getDesc4());
            currentAnzMinTop.get(j).setAsc4(ret5.get(i).getAsc4());
            currentAnzMinTop.get(j).setRet4(ret5.get(i).getRet4());
            if (i == ret5.size() - 1) {
                LyqAnalyze currentAnalyze = currentAnzMinTop.get(0);
                if (minAnalyze.getRet4() > currentAnalyze.getRet4()) {
                    minAnalyze.setDesc4(currentAnalyze.getDesc4());
                    minAnalyze.setAsc4(currentAnalyze.getAsc4());
                    minAnalyze.setRet4(currentAnalyze.getRet4());
                }
            }
        }
        log.info("消息分析 分析四 MAX 完毕");
        log.info("消息分析完毕");
    }

}
