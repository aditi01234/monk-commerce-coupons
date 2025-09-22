package com.example.ecommerce.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplyResponse {
	private List<CartItemDiscount> items;
	private double totalPrice;
	private double totalDiscount;
	private double finalPrice;

	// Nested static class
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CartItemDiscount {
		private Long productId;
		private int quantity;
		private double price;
		private double discount;
	}
}
