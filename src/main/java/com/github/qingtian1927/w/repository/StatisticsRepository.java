package com.github.qingtian1927.w.repository;

import com.github.qingtian1927.w.model.dto.ActiveUserCount;
import com.github.qingtian1927.w.model.dto.AgeGroupCount;
import com.github.qingtian1927.w.model.dto.PostCount;
import com.github.qingtian1927.w.model.dto.UserGrowthCount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unchecked")
@Repository
public class StatisticsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<AgeGroupCount> getUserAgeGroupCount() {
        String sql = """
                   WITH TotalUsers AS (
                       SELECT COUNT(id) AS total FROM users WHERE role = 'USER'
                   )
                   SELECT
                       age,
                       COUNT(id) AS count,
                       ((COUNT(u.id) * 100) / t.total) AS percentage
                   FROM users u
                   CROSS JOIN TotalUsers t
                   WHERE u.role = 'USER'
                   GROUP BY u.age, t.total
                   ORDER BY count DESC
                """;

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> queryResultList = (List<Object[]>) query.getResultList();
        List<AgeGroupCount> ageGroupCountList = new ArrayList<>();

        for (Object[] result : queryResultList) {
            ageGroupCountList.add(new AgeGroupCount(
                    ((Number) result[0]).intValue(),
                    ((Number) result[1]).intValue(),
                    ((Number) result[2]).intValue()
            ));
        }

        return ageGroupCountList;
    }

    public List<UserGrowthCount> countNewUsers() {
        String sql = """
                SELECT TOP (7)
                    CAST(created_at AS DATE) AS date,
                    COUNT(id) AS count
                FROM users
                GROUP BY CAST(created_at AS DATE)
                ORDER BY date;
                """;

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> queryResultList = (List<Object[]>) query.getResultList();
        List<UserGrowthCount> userGrowthCountList = new ArrayList<>();

        for (Object[] result : queryResultList) {
            userGrowthCountList.add(new UserGrowthCount(
                    ((Date) result[0]),
                    ((Number) result[1]).intValue()
            ));
        }

        return userGrowthCountList;
    }

    public int countPostToday() {
        String sql = """
                    SELECT COUNT(id) AS count
                    FROM posts
                    WHERE DATEDIFF(day, created_at, CURRENT_TIMESTAMP) < 1
                """;

        Query query = entityManager.createNativeQuery(sql);
        Object result = (Object) query.getSingleResult();

        return ((Number) result).intValue();
    }

    public int countPostThisWeek() {
        String sql = """
                    SELECT COUNT(id) AS count
                    FROM posts
                    WHERE DATEDIFF(week, created_at, CURRENT_TIMESTAMP) < 1
                """;

        Query query = entityManager.createNativeQuery(sql);
        Object result = (Object) query.getSingleResult();

        return ((Number) result).intValue();
    }

    public int countPostThisMonth() {
        String sql = """
                    SELECT COUNT(id) AS count
                    FROM posts
                    WHERE DATEDIFF(month, created_at, CURRENT_TIMESTAMP) < 1
                """;

        Query query = entityManager.createNativeQuery(sql);
        Object result = (Object) query.getSingleResult();

        return ((Number) result).intValue();
    }

    public PostCount countNewPosts() {
        String todaySQL = """
                    SELECT COUNT(id) AS count
                    FROM posts
                    WHERE CAST(created_at AS DATE) = CAST(CURRENT_TIMESTAMP AS DATE)
                """;
        String thisWeekSQL = """
                    SELECT COUNT(id) AS count
                    FROM posts
                    WHERE DATEPART(week, created_at) = DATEPART(week, CURRENT_TIMESTAMP)
                    AND DATEPART(year, created_at) = DATEPART(year, CURRENT_TIMESTAMP)
                """;
        String thisMonthSQL = """
                    SELECT COUNT(id) AS count
                    FROM posts
                    WHERE DATEPART(month, created_at) = DATEPART(month, CURRENT_TIMESTAMP)
                    AND DATEPART(year, created_at) = DATEPART(year, CURRENT_TIMESTAMP)
                """;

        String yesterdaySQL = """
                    SELECT COUNT(id) AS count
                    FROM posts
                    WHERE DATEDIFF(day, created_at, CURRENT_TIMESTAMP) = 1
                """;
        String lastWeekSQL = """
                    SELECT COUNT(id) AS count
                    FROM posts
                    WHERE DATEPART(week, created_at) = DATEPART(week, DATEADD(week, -1, CURRENT_TIMESTAMP))
                    AND DATEPART(year, created_at) = DATEPART(year, DATEADD(week, -1, CURRENT_TIMESTAMP))
                """;

        String lastMonthSQL = """
                    SELECT COUNT(id) AS count
                    FROM posts
                    WHERE DATEPART(month, created_at) = DATEPART(month, DATEADD(month, -1, CURRENT_TIMESTAMP))
                    AND DATEPART(year, created_at) = DATEPART(year, DATEADD(month, -1, CURRENT_TIMESTAMP))
                """;

        Query todayQuery = entityManager.createNativeQuery(todaySQL);
        Query thisWeekQuery = entityManager.createNativeQuery(thisWeekSQL);
        Query thisMonthQuery = entityManager.createNativeQuery(thisMonthSQL);

        Query yesterdayQuery = entityManager.createNativeQuery(yesterdaySQL);
        Query lastWeekQuery = entityManager.createNativeQuery(lastWeekSQL);
        Query lastMonthQuery = entityManager.createNativeQuery(lastMonthSQL);

        return new PostCount(
                ((Number) todayQuery.getSingleResult()).intValue(),
                ((Number) thisWeekQuery.getSingleResult()).intValue(),
                ((Number) thisMonthQuery.getSingleResult()).intValue(),
                ((Number) yesterdayQuery.getSingleResult()).intValue(),
                ((Number) lastWeekQuery.getSingleResult()).intValue(),
                ((Number) lastMonthQuery.getSingleResult()).intValue()
        );
    }

    public ActiveUserCount countActiveUsers() {
        String currentWeekSQL = """
                    SELECT COUNT(DISTINCT user_id) AS count
                    FROM (
                        SELECT user_id FROM posts
                        WHERE DATEPART(week, created_at) = DATEPART(week, CURRENT_TIMESTAMP)
                        AND DATEPART(year, created_at) = DATEPART(year, CURRENT_TIMESTAMP)
                        UNION
                        SELECT user_id FROM comments
                        WHERE DATEPART(week, created_at) = DATEPART(week, CURRENT_TIMESTAMP)
                        AND DATEPART(year, created_at) = DATEPART(year, CURRENT_TIMESTAMP)
                        UNION
                        SELECT user_id FROM likes
                        WHERE DATEPART(week, created_at) = DATEPART(week, CURRENT_TIMESTAMP)
                        AND DATEPART(year, created_at) = DATEPART(year, CURRENT_TIMESTAMP)
                        UNION
                        SELECT follower_id AS user_id FROM follows
                        WHERE DATEPART(week, created_at) = DATEPART(week, CURRENT_TIMESTAMP)
                        AND DATEPART(year, created_at) = DATEPART(year, CURRENT_TIMESTAMP)
                    ) AS activity;
                """;

        String pastWeekSQL = """
                    SELECT COUNT(DISTINCT user_id) AS count
                    FROM (
                        SELECT user_id FROM posts
                        WHERE DATEPART(week, created_at) = DATEPART(week, DATEADD(week, -1, CURRENT_TIMESTAMP))
                        AND DATEPART(year, created_at) = DATEPART(year, DATEADD(week, -1, CURRENT_TIMESTAMP))
                        UNION
                        SELECT user_id FROM comments
                        WHERE DATEPART(week, created_at) = DATEPART(week, DATEADD(week, -1, CURRENT_TIMESTAMP))
                        AND DATEPART(year, created_at) = DATEPART(year, DATEADD(week, -1, CURRENT_TIMESTAMP))
                        UNION
                        SELECT user_id FROM likes
                        WHERE DATEPART(week, created_at) = DATEPART(week, DATEADD(week, -1, CURRENT_TIMESTAMP))
                        AND DATEPART(year, created_at) = DATEPART(year, DATEADD(week, -1, CURRENT_TIMESTAMP))
                        UNION
                        SELECT follower_id AS user_id FROM follows
                        WHERE DATEPART(week, created_at) = DATEPART(week, DATEADD(week, -1, CURRENT_TIMESTAMP))
                        AND DATEPART(year, created_at) = DATEPART(year, DATEADD(week, -1, CURRENT_TIMESTAMP))
                    ) AS activity;
                """;

        Query currentWeekQuery = entityManager.createNativeQuery(currentWeekSQL);
        Query pastWeekQuery = entityManager.createNativeQuery(pastWeekSQL);

        return new ActiveUserCount(
                ((Number) currentWeekQuery.getSingleResult()).intValue(),
                ((Number) pastWeekQuery.getSingleResult()).intValue()
        );
    }
}
