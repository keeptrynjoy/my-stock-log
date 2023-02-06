package com.smallv.stock.controller;

import com.smallv.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/stock")
public class StockController {

    private final StockService stockService;

    @GetMapping(value = {"/update/info/{baseDate}","/update/info/"})
    public ResponseEntity saveStockInfo(@PathVariable(required = false) String baseDate ) {

        stockService.saveStockInfo(Integer.parseInt(Optional.ofNullable(baseDate).orElse("5")));

        return ResponseEntity.ok("업데이트 완료");
    }

    @GetMapping("/update/corp-code")
    public ResponseEntity saveCorpCode(){

        stockService.saveCorpCode();

        return ResponseEntity.ok("업데이트 완료");
    }

    @GetMapping("/update/finance/{stockCode}/{year}/{quarter}")
    public ResponseEntity financeSave(@PathVariable("stockCode") String stockCode,
                                      @PathVariable("year") int year,
                                      @PathVariable("quarter") String quarter){

        stockService.saveFinanceInfo(stockCode, year,quarter);

        return ResponseEntity.ok("업데이트 완료");
    }



}
