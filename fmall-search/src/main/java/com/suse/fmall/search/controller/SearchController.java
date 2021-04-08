package com.suse.fmall.search.controller;

import com.suse.fmall.search.service.MallSearchService;
import com.suse.fmall.search.vo.SearchParam;
import com.suse.fmall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author LiuJing
 * @Date: 2021/03/29/ 16:48
 * @Description
 */
@Controller
public class SearchController {
    @Autowired
    private MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model, HttpServletRequest request){
        param.set_queryString(request.getQueryString());
        //1.根据传递来的页面的查询数据，去ES中检索商品
        SearchResult result = mallSearchService.search(param);
        model.addAttribute("result",result);
        return "list";
    }
}
