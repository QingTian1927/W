package com.github.qingtian1927.w.model;

import java.util.Date;

public interface Bookmarkable {
    User getUser();
    String getContent();
    Date getCreatedDate();
}
