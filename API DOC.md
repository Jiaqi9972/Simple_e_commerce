# Simple_e_commerce API DOC

## User Service `/api/v1/user`

### all need auth

1. POST `/register`
   
   receive data from cognito and synchronize the db with user group

2. GET `/user-info`
   
   get personal info

3. PATCH `/set-role`
   
   set role (MERCHANT or CUSTOMER)

4. PATCH `/update-user-info`
   
   update personal info

5. POST `/address/add`
   
   add address

6. GET `/address`
   
   get address

7. PATCH `/address/{addressId}`
   
   update address

8. DELETE `/address/{addressId}`
   
   delete address

## Shop Service `/api/v1/shop`

### 1-8 need auth

### 9-10 are public

1. POST `/register`
   
   register a shop ( only MERCHANT )

2. PATCH `/update/{shopId}`
   
   update shop info

3. PATCH `/verify/{shopId}`
   
   verify a shop (only ADMIN)

4. PATCH `/mark-as-deleted/{shopId}`
   
   soft delete a shop

5. PATCH `/activate/{shopId}`
   
   activate a shop

6. PATCH `/deactivate/{shopId}`
   
   deactivate a shop

7. GET `/owner/shops`
   
   return shops by owner 

8. GET `/owner/{shopId}`
   
   return shop details for owner

9. GET `/{shopId}`
   
   **public**  return shop details 

10. GET `/search`
    
    **public**  search shop

## Inventory Service `/api/v1/inventory`

### 1-10 need auth

### 11-12 are public

1. POST `/create`
   
   create inventory record 

2. GET  `/get-record/{recordId}`
   
   get record by id

3. GET `/search-record`
   
   search record with keyword

4. POST `/product/create`
   
   create a new product

5. PATCH `/product/update/{productId}`
   
   update product

6. DELETE `/product/delete/{productId}`
   
   delete product

7. PATCH  `/product/deactivate/{productId}`
   
   deactivate product

8. PATCH  `/product/activate/{productId}`
   
   activate product

9. GET `/product/owner/search`
   
   search product 

10. GET `/product/owner/details/{productId}`
    
    get product details

11. GET `/product/{productId}`
    
    **public** get product details

12. GET `/product/search`
    
    **public** search product
