package com.example.ecommerce.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.ecommerce.dto.ApplicableCouponDTO;
import com.example.ecommerce.dto.ApplicableResponse;
import com.example.ecommerce.dto.ApplyResponse;
import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.dto.CouponDTO;
import com.example.ecommerce.entity.Coupon;
import com.example.ecommerce.repository.CouponRepository;
import com.example.ecommerce.strategy.CouponStrategy;

@Service
public class CouponService {
	private final CouponRepository repo;
	private final StrategyFactory factory;

	public CouponService(CouponRepository repo, StrategyFactory factory) {
		this.repo = repo;
		this.factory = factory;
	}

	public Coupon create(CouponDTO dto) {
		Coupon c = new Coupon();
		c.setCode(dto.getCode());
		c.setType(dto.getType());
		c.setDetailsJson(dto.getDetailsJson());
		if (dto.getActive() != null)
			c.setActive(dto.getActive());
		c.setExpiresAt(dto.getExpiresAt());
		return repo.save(c);
	}

	public List<Coupon> list() {
		return repo.findAll();
	}

	public Coupon get(Long id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
	}

	public Coupon update(Long id, CouponDTO dto) {
		Coupon c = get(id);
		if (dto.getCode() != null)
			c.setCode(dto.getCode());
		if (dto.getDetailsJson() != null)
			c.setDetailsJson(dto.getDetailsJson());
		if (dto.getType() != null)
			c.setType(dto.getType());
		if (dto.getActive() != null)
			c.setActive(dto.getActive());
		c.setExpiresAt(dto.getExpiresAt());
		return repo.save(c);
	}

	public void delete(Long id) {
		repo.deleteById(id);
	}

	public ApplicableResponse applicable(CartDTO cart) {
		List<Coupon> candidates = repo.findByActiveTrue();
		List<ApplicableCouponDTO> list = candidates.stream().map(c -> {
			CouponStrategy s = factory.get(c.getType());
			double discount = 0.0;
			if (s.isApplicable(cart, c))
				discount = s.calculateDiscount(cart, c);
			return new ApplicableCouponDTO(c.getId(), c.getCode(), c.getType().name(), discount);
		}).filter(a -> a.getDiscount() > 0).collect(Collectors.toList());
		return new ApplicableResponse(cart, list);
	}

	public ApplyResponse apply(Long couponId, CartDTO cart) {
		Coupon c = get(couponId);
		CouponStrategy s = factory.get(c.getType());
		if (!s.isApplicable(cart, c))
			throw new RuntimeException("Coupon not applicable");
		double discount = s.calculateDiscount(cart, c);
		double totalPrice = cart.getItems().stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
		double finalPrice = totalPrice - discount;
		
		List<ApplyResponse.CartItemDiscount> items = cart.getItems().stream()
				.map(i -> new ApplyResponse.CartItemDiscount(i.getProductId(), i.getQuantity(), i.getPrice(), 0.0))
				.collect(Collectors.toList());
		return new ApplyResponse(items, round(totalPrice), round(discount), round(finalPrice));
	}

	private double round(double v) {
		return Math.round(v * 100.0) / 100.0;
	}
}
