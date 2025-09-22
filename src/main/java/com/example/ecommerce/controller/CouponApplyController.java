package com.example.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.ApplicableResponse;
import com.example.ecommerce.dto.ApplyResponse;
import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.service.CouponService;

@RestController
public class CouponApplyController {
	private final CouponService service;

	public CouponApplyController(CouponService service) {
		this.service = service;
	}

	@PostMapping("/applicable-coupons")
	public ResponseEntity<ApplicableResponse> applicable(@RequestBody CartDTO cart) {
		return ResponseEntity.ok(service.applicable(cart));
	}

	@PostMapping("/apply-coupon/{id}")
	public ResponseEntity<ApplyResponse> apply(@PathVariable Long id, @RequestBody CartDTO cart) {
		return ResponseEntity.ok(service.apply(id, cart));
	}
}
