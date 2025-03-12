package com.github.qingtian1927.w.controller;

import com.github.qingtian1927.w.model.Searchable;
import com.github.qingtian1927.w.service.interfaces.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping(value = {"/search"})
    public String search(@RequestParam("query") String query, Model model) {
        if (query == null || query.isEmpty()) {
            model.addAttribute("error", "Please enter a query");
            return "search";
        }

        System.out.println();
        for (Searchable result : searchService.search(query)) {
            System.out.println(result);
            System.out.println(result.getType());
        }
        System.out.println();

        model.addAttribute("results", searchService.search(query));
        return "search";
    }
}
