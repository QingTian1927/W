package com.github.qingtian1927.w.service.interfaces;

import com.github.qingtian1927.w.model.Comment;
import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.Profile;
import com.github.qingtian1927.w.model.Searchable;

import java.util.List;

public interface SearchService {
    List<Post> searchPosts(String text);
    List<Comment> searchComments(String text);
    List<Profile> searchProfiles(String text);
    List<Searchable> search(String text);

    static String highlightTerms(String content, String terms) {
        return content.replaceAll(
                terms, "<span class=\"text-darker bg-lightgreen\">" + terms + "</span>"
        );
    }
}
