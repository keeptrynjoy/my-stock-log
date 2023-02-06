package com.smallv.stock.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FinanceDto {
    private String stockCode;
    private String accountNm;
    private String bsnsYear;
    private String qrDiv;
    private String currentAmt;
    private String currentAddAmt;
    private String currencyDiv;
    private String fsDiv;
}
