package com.example.ecommerce.strategy;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.entity.Coupon;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("CART_WISE")
public class CartCouponStrategy implements CouponStrategy {
	private final ObjectMapper mapper = new ObjectMapper();

	public static record CartDetails(double threshold, double discountPercentage, Double maxDiscountAmount) {
	}

	@Override
	public boolean isApplicable(CartDTO cart, Coupon coupon) {
		if (!coupon.isActive())
			return false;
		if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(Instant.now()))
			return false;
		try {
			CartDetails d = mapper.readValue(coupon.getDetailsJson(), CartDetails.class);
			double total = cart.getItems().stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
			return total >= d.threshold();
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public double calculateDiscount(CartDTO cart, Coupon coupon) {
		try {
			CartDetails d = mapper.readValue(coupon.getDetailsJson(), CartDetails.class);
			double total = cart.getItems().stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
			double discount = total * d.discountPercentage() / 100.0;
			if (d.maxDiscountAmount() != null)
				discount = Math.min(discount, d.maxDiscountAmount());
			return round(discount);
		} catch (Exception e) {
			return 0.0;
		}
	}

	private double round(double v) {
		return Math.round(v * 100.0) / 100.0;
	}
}
