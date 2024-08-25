package com.demo.mysqlinnodb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.demo.mysqlinnodb.service.JdbcService;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PerformanceTestingController {

    private final JdbcService service;

    @GetMapping("/select")
    public ResponseEntity<String> select(@RequestParam(required = false) LocalDate date,
                                         @RequestParam(required = false) LocalDate from,
                                         @RequestParam(required = false) LocalDate to,
                                         @RequestParam(required = false) List<LocalDate> in) {
        return ResponseEntity.ok(service.select(date, from, to, in));
    }

    @GetMapping("/select-equals")
    public ResponseEntity<String> selectEquals() {
        return ResponseEntity.ok(service.randomSelectEquals());
    }

    @GetMapping("/select-from-to")
    public ResponseEntity<String> selectFromTo() {
        return ResponseEntity.ok(service.randomSelectFromTo());
    }

    @GetMapping("/select-in")
    public ResponseEntity<String> selectIn() {
        return ResponseEntity.ok(service.randomSelectIn());
    }

    @GetMapping("/insert")
    public ResponseEntity<String> randomInsert() {
        service.randomInsert();
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/populate")
    public ResponseEntity<String> populateDatabase() {
        service.performBatchInsert();
        return ResponseEntity.ok("OK");
    }
}
