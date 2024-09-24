package com.example.groupProject.webCrawling;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class CrawlingService {

    Connection getJSoupConnection(String url) throws Exception {
        return Jsoup.connect(url);
    }

    public Elements getJsoupElements(Connection connection, String url, String query) throws Exception {
        Connection conn = !ObjectUtils.isEmpty(connection) ? connection : getJSoupConnection(url);

        Elements result = null;

        result = conn.get().select(query);

        return result;
    }
}
