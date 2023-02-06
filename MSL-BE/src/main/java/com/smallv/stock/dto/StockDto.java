package com.smallv.stock.dto;

import lombok.*;
import org.apache.ibatis.type.Alias;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockDto {

    private String stockCode;
    private String stockNm;
    private String osCnt;
    private String mrktDiv;
    private String corpCode;
}
