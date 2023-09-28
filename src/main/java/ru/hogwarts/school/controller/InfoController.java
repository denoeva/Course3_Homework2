package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.LongStream;
import java.util.stream.Stream;

@RestController
@RequestMapping("/properties")
public class InfoController {
    @Value("${server.port}")
    private int serverPort;

    @GetMapping("/port")
    public int getServerPort() {
        return serverPort;
    }

    @GetMapping("/stream/times")
    public String calculateSum() {
        long timeCurrent = System.currentTimeMillis();
        Integer result = Stream.iterate(1, a -> a + 1)
                .limit(1_000_000)
//                .parallel()
                .reduce(0, Integer::sum);
        long timeConsumed = System.currentTimeMillis() - timeCurrent;
        return "Time consumed = " + timeConsumed + " millis. Result = " + result;
    }
}
