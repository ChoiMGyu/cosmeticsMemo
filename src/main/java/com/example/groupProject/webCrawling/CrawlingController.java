package com.example.groupProject.webCrawling;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class CrawlingController {

    private static final Logger logger = LoggerFactory.getLogger(CrawlingController.class);

    private final CrawlingService crawlingService;

    @GetMapping("/crawl")
    public String crawl(@RequestParam String url) throws Exception{
        try {
            String query = "p";
            Elements selects = crawlingService.getJsoupElements(null, url, query);

            logger.info("selects.get(0).text() : " + selects.get(0).text());
            logger.info("selects.get(0).html() : " + selects.get(0).html());
            logger.info("selects.get(0).children() : " + selects.get(0).children());
            logger.info("selects.get(0).parent() : " + selects.get(0).parent());
            logger.info("selects.get(0).parent().previousElementSibling() : " + selects.get(0).parent().previousElementSibling());

            return "Success";
        } catch (Selector.SelectorParseException e) {
            return "Error fetching page title: " + e.getMessage();
        }
    }
}
