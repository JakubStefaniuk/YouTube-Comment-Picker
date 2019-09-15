package com.jstef.youtube_comment_picker.Controller;

import com.jstef.youtube_comment_picker.Computation.CommentPicker;
import com.jstef.youtube_comment_picker.Exception.InvalidVideoUrl;
import com.jstef.youtube_comment_picker.Resource.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController {
    @Autowired
    private CommentPicker commentPicker;

    @GetMapping("/")
    public String viewHomePage(){
        return "homepage.html";
    }
    @GetMapping("pick_comment")
    public String pickComment(Model model, @RequestParam("url") String url){
        Comment chosen = commentPicker.pickCommentOrThrow(url);
        model.addAttribute("author",chosen.getAuthor());
        model.addAttribute("message",chosen.getMessage());
        return "homepage.html";
    }
}
