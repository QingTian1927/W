package com.github.qingtian1927.w.repository;

import com.github.qingtian1927.w.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    @Query("SELECT k FROM Keyword k WHERE :content LIKE CONCAT('%', k.word, '%')")
    List<Keyword> findMatchingKeywords(@Param("content") String content);
}