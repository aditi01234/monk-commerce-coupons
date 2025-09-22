package com.example.ecommerce.strategy;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.dto.CartItemDTO;
import com.example.ecommerce.entity.Coupon;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("PRODUCT_WISE")
public class ProductCouponStrategy implements CouponStrategy {
	private final ObjectMapper mapper = new ObjectMapper();

	public static record ProductDetails(Long productId, double discountPercentage, Integer maxUsesPerOrder) {
	}

	@Override
	public boolean isApplicable(CartDTO cart, Coupon coupon) {
		if (!coupon.isActive())
			return false;
		if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(Instant.now()))
			return false;
		
		try {
			ProductDetails d = mapper.readValue(coupon.getDetailsJson(), ProductDetails.class);
			return cart.getItems().stream().anyMatch(i -> i.getProductId().equals(d.productId()));
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public double calculateDiscount(CartDTO cart, Coupon coupon) {
		try {
			ProductDetails d = mapper.readValue(coupon.getDetailsJson(), ProductDetails.class);
			for (CartItemDTO item : cart.getItems()) {
				if (item.getProductId().equals(d.productId())) {
					int qty = item.getQuantity();
					int uses = d.maxUsesPerOrder() == null ? qty : Math.min(qty, d.maxUsesPerOrder());
					double disc = uses * item.getPrice() * d.discountPercentage() / 100.0;
					return round(disc);
				}
			}
			return 0.0;
		} catch (Exception e) {
			return 0.0;
		}
	}

	private double round(double v) {
		return Math.round(v * 100.0) / 100.0;
	}
}
