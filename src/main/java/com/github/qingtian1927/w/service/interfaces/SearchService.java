package com.github.qingtian1927.w.service.interfaces;

import com.github.qingtian1927.w.model.Comment;
import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.Profile;
import com.github.qingtian1927.w.model.Searchable;
import com.github.qingtian1927.w.model.dto.SearchQuery;

import java.util.List;

public interface SearchService {
    List<Post> searchPosts(SearchQuery query);
    List<Comment> searchComments(SearchQuery query);
    List<Profile> searchProfiles(SearchQuery query);
    List<Searchable> search(SearchQuery query);

    static String highlightTerms(String content, String terms) {
        return content.replaceAll(
                terms, "<span class=\"text-darker bg-lightgreen\">" + terms + "</span>"
        );
    }
}
