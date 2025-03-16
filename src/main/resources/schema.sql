IF NOT EXISTS (SELECT *
               FROM sys.fulltext_catalogs
               WHERE name = 'PostFTCat')
    BEGIN
        CREATE FULLTEXT CATALOG PostFTCat;
    END

IF NOT EXISTS (SELECT *
               FROM sys.indexes
               WHERE name = 'idx_posts_id'
                 AND object_id = OBJECT_ID('posts'))
    BEGIN
        CREATE UNIQUE INDEX idx_posts_id ON posts (id);
    END

IF NOT EXISTS (SELECT *
               FROM sys.fulltext_indexes
               WHERE OBJECT_NAME(object_id) = 'posts')
    BEGIN
        CREATE FULLTEXT INDEX ON posts (content)
            KEY INDEX idx_posts_id
            ON PostFTCat;
    END

-- # -------------------------------------------------------------------------------------------------------------------

IF NOT EXISTS (SELECT *
               FROM sys.fulltext_catalogs
               WHERE name = 'CommentFTCat')
    BEGIN
        CREATE FULLTEXT CATALOG CommentFTCat;
    END

IF NOT EXISTS (SELECT *
               FROM sys.indexes
               WHERE name = 'idx_comments_id'
                 AND object_id = OBJECT_ID('comments'))
    BEGIN
        CREATE UNIQUE INDEX idx_comments_id ON comments (id);
    END

IF NOT EXISTS (SELECT *
               FROM sys.fulltext_indexes
               WHERE OBJECT_NAME(object_id) = 'comments')
    BEGIN
        CREATE FULLTEXT INDEX ON comments (content)
            KEY INDEX idx_comments_id
            ON CommentFTCat;
    END

-- # -------------------------------------------------------------------------------------------------------------------

IF NOT EXISTS (SELECT *
               FROM sys.fulltext_catalogs
               WHERE name = 'ProfileFTCat')
    BEGIN
        CREATE FULLTEXT CATALOG ProfileFTCat;
    END

IF NOT EXISTS (SELECT *
               FROM sys.indexes
               WHERE name = 'idx_profiles_id'
                 AND object_id = OBJECT_ID('profiles'))
    BEGIN
        CREATE UNIQUE INDEX idx_profiles_id ON profiles (user_id);
    END

IF NOT EXISTS (SELECT *
               FROM sys.fulltext_indexes
               WHERE OBJECT_NAME(object_id) = 'profiles')
    BEGIN
        CREATE FULLTEXT INDEX ON profiles (bio)
            KEY INDEX idx_profiles_id
            ON ProfileFTCat;
    END

-- # -------------------------------------------------------------------------------------------------------------------

IF NOT EXISTS (SELECT *
               FROM sys.indexes
               WHERE name = 'idx_keywords_word'
                 AND object_id = OBJECT_ID('keywords'))
    BEGIN
        CREATE INDEX idx_keywords_word ON keywords (word);
    END
