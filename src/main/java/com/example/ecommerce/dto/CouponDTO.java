package com.example.ecommerce.dto;

import java.time.Instant;

import com.example.ecommerce.entity.CouponType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDTO {
	private Long id;
	private String code;
	private CouponType type; // CART_WISE, PRODUCT_WISE, BXGY
	private String description;
	private Boolean active; 
	private Instant expiresAt;
	private String detailsJson;
	private Instant validFrom;
    private Instant validTill;
	
}