package com.jstef.youtube_comment_picker.Controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class FailureController implements ErrorController {
    @RequestMapping("/error")
    public String errorOccured(){
        return "errorpage.html";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
