package com.github.qingtian1927.w.controller;

import com.github.qingtian1927.w.model.Searchable;
import com.github.qingtian1927.w.model.dto.SearchQuery;
import com.github.qingtian1927.w.service.interfaces.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping(value = {"/search"})
    public String search(@ModelAttribute SearchQuery searchQuery, Model model) {
        String query = searchQuery.getQuery();
        if (query == null || query.isEmpty()) {
            model.addAttribute("error", "Please enter a query");
            return "search";
        }

        List<Searchable> resultList = searchService.search(searchQuery);
        model.addAttribute("results", resultList);

        return "search";
    }
}
