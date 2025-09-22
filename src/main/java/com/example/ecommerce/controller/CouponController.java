package com.example.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.CouponDTO;
import com.example.ecommerce.entity.Coupon;
import com.example.ecommerce.service.CouponService;

@RestController
@RequestMapping("/coupons")
public class CouponController {
	private final CouponService service;

	public CouponController(CouponService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<Coupon> create(@RequestBody CouponDTO dto) {
		return ResponseEntity.ok(service.create(dto));
	}

	@GetMapping
	public ResponseEntity<List<Coupon>> list() {
		return ResponseEntity.ok(service.list());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Coupon> get(@PathVariable Long id) {
		return ResponseEntity.ok(service.get(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Coupon> update(@PathVariable Long id, @RequestBody CouponDTO dto) {
		return ResponseEntity.ok(service.update(id, dto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
