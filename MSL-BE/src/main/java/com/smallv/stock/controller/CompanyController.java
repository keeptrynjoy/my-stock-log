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

    @GetMapping(value = {"/update/company/{baseDate}","/update/company"})
    public ResponseEntity stockLoad(@PathVariable(required = false) String baseDate ) {

        companyService.stockLoad(Integer.parseInt(Optional.ofNullable(baseDate).orElse("2")));

        return ResponseEntity.ok(baseDate);
    }


}
