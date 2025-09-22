package com.example.ecommerce.service;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.example.ecommerce.entity.CouponType;
import com.example.ecommerce.strategy.CouponStrategy;

@Component
public class StrategyFactory {
	private final ApplicationContext ctx;

	public StrategyFactory(ApplicationContext ctx) {
		this.ctx = ctx;
	}

	public CouponStrategy get(CouponType type) {
		return (CouponStrategy) ctx.getBean(type.name());
	}
}
