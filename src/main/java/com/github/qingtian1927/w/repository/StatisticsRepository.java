package com.github.qingtian1927.w.repository;

import com.github.qingtian1927.w.model.dto.*;
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

    public CommentCount countNewComments() {
        String todaySQL = """
                    SELECT COUNT(id) AS count
                    FROM comments
                    WHERE CAST(created_at AS DATE) = CAST(CURRENT_TIMESTAMP AS DATE)
                """;
        String thisWeekSQL = """
                    SELECT COUNT(id) AS count
                    FROM comments
                    WHERE DATEPART(week, created_at) = DATEPART(week, CURRENT_TIMESTAMP)
                    AND DATEPART(year, created_at) = DATEPART(year, CURRENT_TIMESTAMP)
                """;
        String thisMonthSQL = """
                    SELECT COUNT(id) AS count
                    FROM comments
                    WHERE DATEPART(month, created_at) = DATEPART(month, CURRENT_TIMESTAMP)
                    AND DATEPART(year, created_at) = DATEPART(year, CURRENT_TIMESTAMP)
                """;

        String yesterdaySQL = """
                    SELECT COUNT(id) AS count
                    FROM comments
                    WHERE DATEDIFF(day, created_at, CURRENT_TIMESTAMP) = 1
                """;
        String lastWeekSQL = """
                    SELECT COUNT(id) AS count
                    FROM comments
                    WHERE DATEPART(week, created_at) = DATEPART(week, DATEADD(week, -1, CURRENT_TIMESTAMP))
                    AND DATEPART(year, created_at) = DATEPART(year, DATEADD(week, -1, CURRENT_TIMESTAMP))
                """;

        String lastMonthSQL = """
                    SELECT COUNT(id) AS count
                    FROM comments
                    WHERE DATEPART(month, created_at) = DATEPART(month, DATEADD(month, -1, CURRENT_TIMESTAMP))
                    AND DATEPART(year, created_at) = DATEPART(year, DATEADD(month, -1, CURRENT_TIMESTAMP))
                """;

        Query todayQuery = entityManager.createNativeQuery(todaySQL);
        Query thisWeekQuery = entityManager.createNativeQuery(thisWeekSQL);
        Query thisMonthQuery = entityManager.createNativeQuery(thisMonthSQL);

        Query yesterdayQuery = entityManager.createNativeQuery(yesterdaySQL);
        Query lastWeekQuery = entityManager.createNativeQuery(lastWeekSQL);
        Query lastMonthQuery = entityManager.createNativeQuery(lastMonthSQL);

        return new CommentCount(
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

    public int countBannedUsers() {
        String sql = """
                    SELECT COUNT(id) AS count
                    FROM users
                    WHERE is_not_banned = 'false' AND role = 'USER';
                """;

        Query query = entityManager.createNativeQuery(sql);
        return ((Number) query.getSingleResult()).intValue();
    }

    public List<UserActivityCount> countUserActivities() {
        String sql = """
                    WITH PostCount AS (
                        SELECT
                            CAST(p.created_at AS DATE) AS date,
                            COUNT(user_id) AS count FROM posts p
                        JOIN users u ON p.user_id = u.id
                        WHERE u.role = 'USER'
                        GROUP BY CAST(p.created_at AS DATE)
                    ),
                    CommentCount AS (
                        SELECT
                            CAST(c.created_at AS DATE) AS date,
                            COUNT(user_id) AS count FROM comments c
                        JOIN users u ON c.user_id = u.id
                        WHERE u.role = 'USER'
                        GROUP BY CAST(c.created_at AS DATE)
                    ),
                    LikeCount AS (
                        SELECT
                            CAST(l.created_at AS DATE) AS date,
                            COUNT(user_id) AS count FROM likes l
                            JOIN users u ON l.user_id = u.id
                        WHERE u.role = 'USER'
                        GROUP BY CAST(l.created_at AS DATE)
                    ),
                    FollowCount AS (
                        SELECT
                            CAST(f.created_at AS DATE) AS date,
                            COUNT(follower_id) AS count FROM follows f
                            JOIN users u ON f.follower_id = u.id
                        WHERE u.role = 'USER'
                        GROUP BY CAST(f.created_at AS DATE)
                    )
                    SELECT
                        date, SUM(count) AS count
                    FROM (
                        SELECT date, count FROM PostCount
                        UNION SELECT date, count FROM CommentCount
                        UNION SELECT date, count FROM LikeCount
                        UNION SELECT date, count FROM FollowCount
                    ) AS activity_count
                    GROUP BY date
                    ORDER BY date;
                """;

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        List<UserActivityCount> userActivities = new ArrayList<>();

        for (Object[] result : results) {
            userActivities.add(new UserActivityCount(
                    (Date) result[0],
                    ((Number) result[1]).intValue()
            ));
        }

        return userActivities;
    }
}
