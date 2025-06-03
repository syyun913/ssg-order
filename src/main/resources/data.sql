DELETE FROM PRODUCT;
ALTER TABLE PRODUCT ALTER COLUMN id RESTART WITH 1000000001;

INSERT INTO PRODUCT (PRODUCT_NAME, SELLING_PRICE, DISCOUNT_AMOUNT, STOCK, CREATED_AT)
VALUES ('이마트 생수',    800,   100, 1000, now()),
       ('신라면 멀티팩',  4200,  500,  500, now()),
       ('바나나 한 송이',  3500,  300,  200, now()),
       ('삼겹살 500g',   12000, 2000, 100, now()),
       ('오리온 초코파이', 3000,  400,  300, now());