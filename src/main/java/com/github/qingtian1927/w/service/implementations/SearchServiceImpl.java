package com.github.qingtian1927.w.service.implementations;

import com.github.qingtian1927.w.model.Comment;
import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.Profile;
import com.github.qingtian1927.w.model.Searchable;
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
    public List<Post> searchPosts(String text) {
        return searchRepository.searchPosts(text);
    }

    @Override
    public List<Comment> searchComments(String text) {
        return searchRepository.searchComments(text);
    }

    @Override
    public List<Profile> searchProfiles(String text) {
        return searchRepository.searchProfiles(text);
    }

    @Override
    public List<Searchable> search(String text) {
        return highlightSearch(searchRepository.search(text), text);
    }
}
