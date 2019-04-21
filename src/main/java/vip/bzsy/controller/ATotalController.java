package vip.bzsy.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author lyf
 * @create 2019-04-01 15:24
 */
@Slf4j
@Controller
public class ATotalController {

    @RequestMapping(value = {"/", "/index"})
    public String index(Model model) {
        return "index";
    }

}
