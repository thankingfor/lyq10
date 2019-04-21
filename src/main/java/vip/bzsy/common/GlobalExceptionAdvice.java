package vip.bzsy.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Slf4j
//@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = Exception.class)
    public CommonResponse handlerException(HttpServletRequest req,
                                           CommonException ex) {
        CommonResponse response = new CommonResponse(CommonStatus.EXCEPTION);
        log.info(ex.getMessage());
        return response;
    }

    @ExceptionHandler(value = CommonException.class)
    public CommonResponse handlerCommonException(HttpServletRequest req,
                                                 CommonException ex) {
        CommonResponse response = new CommonResponse(CommonStatus.EXCEPTION);
        response.setMsg(ex.getMessage());
        return response;
    }

    @ExceptionHandler(value = DataCheckException.class)
    public CommonResponse handlerDataCheckException(HttpServletRequest req,
                                                 CommonException ex) {
        CommonResponse response = new CommonResponse(CommonStatus.EXCEPTION);
        response.setMsg("请从新加载数据或者初始化数据");
        return response;
    }
}
