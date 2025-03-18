package com.github.qingtian1927.w.service.implementations;

import com.github.qingtian1927.w.model.Bookmark;
import com.github.qingtian1927.w.model.Bookmarkable;
import com.github.qingtian1927.w.model.User;
import com.github.qingtian1927.w.repository.BookmarkRepository;
import com.github.qingtian1927.w.service.interfaces.BookmarkService;
import com.github.qingtian1927.w.service.interfaces.CommentService;
import com.github.qingtian1927.w.service.interfaces.PostService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public BookmarkServiceImpl(BookmarkRepository bookmarkRepository, PostService postService, CommentService commentService) {
        this.bookmarkRepository = bookmarkRepository;
        this.postService = postService;
        this.commentService = commentService;
    }

    @Override
    public Bookmark save(Bookmark bookmark) {
        if (exists(bookmark)) {
            return null;
        }
        return this.bookmarkRepository.save(bookmark);
    }

    @Override
    public Optional<Bookmark> findById(UUID id) {
        return this.bookmarkRepository.findById(id);
    }

    @Override
    public Optional<Bookmark> findByContent(User user, String type, Long id) {
        return this.bookmarkRepository.findByUserAndTypeAndReferenceId(user, type, id);
    }

    @Override
    public List<Bookmark> findByUser(User user) {
        List<Bookmark> bookmarks = this.bookmarkRepository.findByUser(user);

        for (Bookmark bookmark : bookmarks) {
            Bookmarkable referencedObject = null;
            switch (bookmark.getType()) {
                case Bookmark.TYPE_POST ->
                        referencedObject = postService.findById(bookmark.getReferenceId()).orElse(null);
                case Bookmark.TYPE_COMMENT ->
                        referencedObject = commentService.findById(bookmark.getReferenceId()).orElse(null);
            }
            bookmark.setReferenceObject(referencedObject);
        }

        return bookmarks;
    }

    @Override
    public boolean exists(Bookmark bookmark) {
        return exists(bookmark.getUser(), bookmark.getType(), bookmark.getReferenceId());
    }

    @Override
    public boolean exists(User user, String type, Long referenceId) {
        return this.bookmarkRepository.existsByUserAndTypeAndReferenceId(user, type, referenceId);
    }

    @Transactional
    @Override
    public void deleteById(UUID id) {
        this.bookmarkRepository.deleteById(id);
    }
}
