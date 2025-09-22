package com.example.ecommerce.strategy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.dto.CartItemDTO;
import com.example.ecommerce.entity.Coupon;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("BXGY")
public class BxGyCouponStrategy implements CouponStrategy {
	private final ObjectMapper mapper = new ObjectMapper();

	public static record Pair(Long productId, int quantity) {
	}

	public static record BxGyDetails(List<Pair> buy, List<Pair> get, int repetitionLimit, boolean applyToCheapestGet) {
		public int totalBuyCount() {
			return buy.stream().mapToInt(Pair::quantity).sum();
		}

		public int totalGetCount() {
			return get.stream().mapToInt(Pair::quantity).sum();
		}

		public Set<Long> getBuyIds() {
			return buy.stream().map(Pair::productId).collect(Collectors.toSet());
		}

		public Set<Long> getGetIds() {
			return get.stream().map(Pair::productId).collect(Collectors.toSet());
		}
	}

	@Override
	public boolean isApplicable(CartDTO cart, Coupon coupon) {
		if (!coupon.isActive())
			return false;
		if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(Instant.now()))
			return false;
		try {
			BxGyDetails d = mapper.readValue(coupon.getDetailsJson(), BxGyDetails.class);
			int matchingBuyUnits = cart.getItems().stream().filter(i -> d.getBuyIds().contains(i.getProductId()))
					.mapToInt(CartItemDTO::getQuantity).sum();
			return matchingBuyUnits >= d.totalBuyCount();
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public double calculateDiscount(CartDTO cart, Coupon coupon) {
		try {
			BxGyDetails d = mapper.readValue(coupon.getDetailsJson(), BxGyDetails.class);
			int matchingBuyUnits = cart.getItems().stream().filter(i -> d.getBuyIds().contains(i.getProductId()))
					.mapToInt(CartItemDTO::getQuantity).sum();

			int times = matchingBuyUnits / d.totalBuyCount();
			times = Math.min(times, d.repetitionLimit());
			if (times == 0)
				return 0.0;

			// find eligible get unit prices (each unit separately) from cart
			List<Double> getUnitPrices = new ArrayList<>();
			for (CartItemDTO item : cart.getItems()) {
				if (d.getGetIds().contains(item.getProductId())) {
					for (int q = 0; q < item.getQuantity(); q++)
						getUnitPrices.add(item.getPrice());
				}
			}
			if (getUnitPrices.isEmpty())
				return 0.0;

			Collections.sort(getUnitPrices);
			int eligibleGetUnits = times * d.totalGetCount();
			double discount = 0.0;
			for (int i = 0; i < Math.min(eligibleGetUnits, getUnitPrices.size()); i++)
				discount += getUnitPrices.get(i);
			return round(discount);
		} catch (Exception e) {
			return 0.0;
		}
	}

	private double round(double v) {
		return Math.round(v * 100.0) / 100.0;
	}
}
