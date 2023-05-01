package com.wang.gmall.product.web;

import com.wang.gmall.product.entity.CategoryEntity;
import com.wang.gmall.product.service.impl.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    CategoryServiceImpl categoryService;

    @GetMapping({"/","/index.html"})
    public String getIndex(Model model){
        List<CategoryEntity> categories = categoryService.getLevel1Cate();
        model.addAttribute("categories",categories);
        return "index";
    }
}
