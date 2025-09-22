package com.example.ecommerce.strategy;

import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.entity.Coupon;

public interface CouponStrategy {
	boolean isApplicable(CartDTO cart, Coupon coupon);

	double calculateDiscount(CartDTO cart, Coupon coupon);
	// apply returns discounted items or summary; to keep simple we return same cart
	// (discount separate)
}
