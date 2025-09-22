Overview

This project implements a RESTful API to manage and apply different types of discount coupons for an e-commerce platform.
Coupon types supported: Cart-wise, Product-wise, BxGy (Buy X Get Y).
The system is designed to be easily extendable for new coupon types in the future.

- API Endpoints
Endpoint	Method	Description
/coupons	POST	Create a new coupon
/coupons	GET	Retrieve all coupons
/coupons/{id}	GET	Retrieve coupon by ID
/coupons/{id}	PUT	Update coupon by ID
/coupons/{id}	DELETE	Delete coupon by ID
/applicable-coupons	POST	Fetch all applicable coupons for a given cart
/apply-coupon/{id}	POST	Apply a specific coupon to the cart


- Implemented Cases
1. Cart-wise Coupons
Case: 10% off for cart total above 100
Condition: cart.total > 100
Discount: 10% of total cart price
Example Payload:

{
  "type": "cart-wise",
  "detailsJson": "{\"threshold\":100,\"discount\":10}",
  "active": true
}

2. Product-wise Coupons
Case: 20% off on Product 1
Condition: Product 1 must be in cart
Discount: 20% off Product 1
Example Payload:

{
  "type": "product-wise",
  "detailsJson": "{\"productId\":1,\"discount\":20}",
  "active": true
}

3. BxGy Coupons
Case: Buy 2 products from [1,2], get 1 of Product 3 free, repetition limit 2
Condition: Cart must contain at least 2 buy-products and 1 get-product
Example Payload:

{
  "type": "bxgy",
  "detailsJson": "{\"buyProducts\":[{\"productId\":1,\"quantity\":2},{\"productId\":2,\"quantity\":2}],\"getProducts\":[{\"productId\":3,\"quantity\":1}],\"repetitionLimit\":2}",
  "active": true
}

- Unimplemented / Partially Implemented Cases

1. Expiration date handling (expiresAt) is partially implemented, not fully tested
2. Complex BxGy combinations (multiple buy arrays / multiple get arrays)
3. JUnit tests for strategies and service classes
4. Handling overlapping coupon applications (stackable coupons)

- Limitations
1. H2 database resets on server restart → need to recreate coupons each time
2. detailsJson must be provided for each coupon; missing value causes 500 errors
3. Current API throws RuntimeException on missing coupon ID → no proper HTTP 404 mapping
4. Only basic error handling implemented (coupon not found, conditions not met)
5. No caching or optimization for large carts
6. Price rounding not fully handled (using double instead of BigDecimal in DTOs)

- Assumptions
1. Coupons are active by default for testing purposes
2. detailsJson is a valid JSON string matching coupon type
3. Prices in cart are doubles, no currency rounding considered
4. Only one coupon is applied at a time
5. Future coupon types can be added by implementing CouponStrategy interface

Example Cart JSON
{
  "items": [
    {
      "productId": 1,
      "productName": "Shirt",
      "quantity": 3,
      "pricePerUnit": 500.0,
      "price": 1500.0
    },
    {
      "productId": 2,
      "productName": "Shoes",
      "quantity": 2,
      "pricePerUnit": 1200.0,
      "price": 2400.0
    },
    {
      "productId": 3,
      "productName": "Cap",
      "quantity": 1,
      "pricePerUnit": 300.0,
      "price": 300.0
    }
  ]
}

- Example API Flow

1. Create coupon: POST /coupons → returns coupon ID
2. Get applicable coupons: POST /applicable-coupons with cart JSON → returns list of coupons with calculated discount
3. Apply coupon: POST /apply-coupon/{id} with cart JSON → returns updated cart with discounts