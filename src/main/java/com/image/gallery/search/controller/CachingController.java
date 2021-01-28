package com.image.gallery.search.controller;

import com.image.gallery.search.service.impl.CachingImageServiceImpl;
import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CachingController {
    private static final Long DELAY_BETWEEN_CACHING_UPDATE = 300000L;
    private final CachingImageServiceImpl cachingImageService;
    private Timer timer;

    public CachingController(CachingImageServiceImpl cachingImageService) {
        this.cachingImageService = cachingImageService;
    }

    @PostConstruct
    private void startCaching() {
        timer = new Timer();
        timer.schedule(getCachingTimerTask(), 0, DELAY_BETWEEN_CACHING_UPDATE);
    }

    @GetMapping("/set-fixed-delay/{fixedDelay}")
    public ResponseEntity<Object> setFixedDelay(@PathVariable Long fixedDelay) {
        timer.cancel();
        timer.purge();
        timer = new Timer();
        timer.schedule(getCachingTimerTask(), 0, fixedDelay);
        String responseString = "Fixed delay set at " + fixedDelay + " millisecond.";
        return new ResponseEntity<>(responseString, HttpStatus.OK);
    }

    private TimerTask getCachingTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                cachingImageService.updateCache();
            }
        };
    }
}
