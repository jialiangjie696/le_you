package com.leyou.item.controller;


import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

//    GET /category/of/parent?pid=0


    @Autowired
    private CategoryService categoryService;

    @GetMapping("/of/parent")
//    @CrossOrigin(origins = "http://manage.leyou.com")  //跨域资源共享   声明此方法可以有哪些网站可以进入
    public ResponseEntity<List<CategoryDTO>> findCategoryByParentId(@RequestParam(name = "pid",defaultValue = "0") Long pid){

        List<CategoryDTO>  categoryList = categoryService.findCategoryByparentId(pid);
        return ResponseEntity.ok(categoryList);

    }

}
