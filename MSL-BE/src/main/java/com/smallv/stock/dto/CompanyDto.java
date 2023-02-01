package com.smallv.stock.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CompanyDto {

    private String srtnCd;
    private String itmsNm;
    private String lstgStCnt;
    private String mrktCtg;
    private int cmo;
}
