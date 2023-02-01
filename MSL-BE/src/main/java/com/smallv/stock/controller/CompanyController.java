package com.smallv.stock.controller;

import com.smallv.stock.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/update/company/")
    public ResponseEntity stockLoad(@RequestParam(defaultValue = "1") String baseDate ) {

        companyService.stockLoad(Integer.parseInt(String.valueOf(baseDate)));

        return ResponseEntity.ok("");
    }


}
