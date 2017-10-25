package com.mmall.controller.portal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping(value = "/test/")
public class TestController {

    private Logger logger = LoggerFactory.getLogger(TestController.class);

    @RequestMapping(value="test.do",method = RequestMethod.GET)
    public @ResponseBody String test(String str){
        logger.info("testinfo");
        logger.warn("testinfo");
        logger.error("testinfo");
        return "testValue:"+str;
    }

}
