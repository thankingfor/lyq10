package vip.bzsy.common;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@ToString
public class CommonResponse implements Serializable {

    private Integer code;//成功为1

    private String msg;

    private Object data;

    public CommonResponse(Integer code){
        this.code = code;
    }

    public CommonResponse(CommonStatus status){
        this.code = status.getStatus();
        this.msg = status.getText();
    }

    public CommonResponse(Integer code,String msg){
        this.code = code;
    }

    public CommonResponse(Integer code,String msg,Object data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static CommonResponse success(Object obj, String msg){
        CommonResponse commonResponse = new CommonResponse(1,msg,obj);
        return commonResponse;
    }

    public static CommonResponse success(Object obj, CommonStatus status){
        CommonResponse commonResponse = new CommonResponse(1,status.getText(),obj);
        return commonResponse;
    }

    public static CommonResponse statusObj(Object obj, CommonStatus status){
        CommonResponse commonResponse = new CommonResponse(status.getStatus(),status.getText(),obj);
        return commonResponse;
    }

    public static CommonResponse success(Object obj){
        CommonResponse commonResponse = new CommonResponse(1);
        commonResponse.data = obj;
        return commonResponse;
    }

    public static CommonResponse success(){
        return new CommonResponse(1);
    }

    public static CommonResponse fail(String msg){
        CommonResponse commonResponse = new CommonResponse(0);
        commonResponse.msg = msg;
        return commonResponse;
    }

    public static CommonResponse fail(CommonStatus status) {
        CommonResponse commonResponse = new CommonResponse(status);
        return commonResponse;
    }

    public Map<String , Object> toMap(){
        HashMap<String , Object> result = new HashMap<String , Object>();
        if (code != null){
            result.put("code",code);
        }
        if (msg != null){
            result.put("msg",msg);
        }
        if (data != null){
            result.put("data",data);
        }
        return  result;
    }

}
