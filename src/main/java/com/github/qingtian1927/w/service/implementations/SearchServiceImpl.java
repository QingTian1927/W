package com.github.qingtian1927.w.service.implementations;

import com.github.qingtian1927.w.model.Comment;
import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.Profile;
import com.github.qingtian1927.w.model.Searchable;
import com.github.qingtian1927.w.model.dto.SearchQuery;
import com.github.qingtian1927.w.repository.SearchRepository;
import com.github.qingtian1927.w.service.interfaces.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final SearchRepository searchRepository;

    @Autowired
    public SearchServiceImpl(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    public static <T extends Searchable> List<T> highlightSearch(List<T> searchResults, String terms) {
        for (T result : searchResults) {
            result.setContent(SearchService.highlightTerms(result.getContent(), terms));
        }
        return searchResults;
    }

    @Override
    public List<Post> searchPosts(SearchQuery query) {
        return highlightSearch(searchRepository.searchPosts(query), query.getQuery());
    }

    @Override
    public List<Comment> searchComments(SearchQuery query) {
        return highlightSearch(searchRepository.searchComments(query), query.getQuery());
    }

    @Override
    public List<Profile> searchProfiles(SearchQuery query) {
        return highlightSearch(searchRepository.searchProfiles(query), query.getQuery());
    }

    @Override
    public List<Searchable> search(SearchQuery query) {
        return highlightSearch(searchRepository.search(query), query.getQuery());
    }
}
