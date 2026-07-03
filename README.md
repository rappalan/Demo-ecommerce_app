# E-Commerce REST API (Spring Boot)

A ready-to-run Spring Boot backend for an e-commerce platform — Product catalog, Cart, and Order
management — matching the project described in your interview answers (Java 11, Spring Boot,
MySQL/SQL Server, Docker, deployable to Azure).

## Tech stack

- Java 11
- Spring Boot 2.7 (Web, Data JPA, Validation)
- H2 in-memory database (for instant local testing — zero setup)
- MySQL 8 (for real/Docker deployment)
- Docker + Docker Compose
- Lombok (removes boilerplate getters/setters)

## Project structure

```
ecommerce-app/
├── src/main/java/com/naidu/ecommerce/
│   ├── EcommerceApplication.java     # Main class - starts the app
│   ├── controller/                   # REST endpoints (receives HTTP requests)
│   │   ├── ProductController.java
│   │   ├── CartController.java
│   │   └── OrderController.java
│   ├── service/                      # Business logic layer
│   │   ├── ProductService.java
│   │   ├── CartService.java
│   │   └── OrderService.java
│   ├── repository/                   # Database access (Spring Data JPA)
│   │   ├── ProductRepository.java
│   │   ├── CartItemRepository.java
│   │   └── OrderRepository.java
│   ├── entity/                       # Database tables as Java classes
│   │   ├── Product.java
│   │   ├── CartItem.java
│   │   ├── Order.java
│   │   └── OrderItem.java
│   ├── dto/                          # Request/response objects
│   │   └── AddToCartRequest.java
│   └── exception/                    # Centralized error handling
│       ├── ResourceNotFoundException.java
│       ├── BadRequestException.java
│       └── GlobalExceptionHandler.java
├── src/main/resources/
│   ├── application.properties        # Default config (H2 - instant local run)
│   ├── application-mysql.properties  # MySQL config (for Docker/production)
│   └── data.sql                      # Sample products loaded on startup
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

This mirrors exactly the flow described in the interview answers:
**Client → Controller → Service → Repository → Database.**

## Option 1: Run locally in 30 seconds (no Docker, no MySQL needed)

Requirements: Java 11+ and Maven installed.

```bash
cd ecommerce-app
mvn spring-boot:run
```

The app starts on **http://localhost:8080** using an in-memory H2 database, pre-loaded with 5
sample products (see `data.sql`). You can view the database directly at
`http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:ecommercedb`, username: `sa`, no password).

## Option 2: Run with Docker (MySQL, closer to real deployment)

Requirements: Docker + Docker Compose installed.

```bash
cd ecommerce-app
docker-compose up --build
```

This starts two containers: a MySQL database and the Spring Boot app, wired together
automatically. The app is available at **http://localhost:8080**.

To stop: `docker-compose down` (add `-v` to also delete the MySQL data volume).

## Deploying to Azure

Since the app is already Dockerized, you can push the image to **Azure Container Registry (ACR)**
and run it on **Azure App Service (Web App for Containers)** or **Azure Container Apps**:

```bash
docker build -t ecommerce-app .
docker tag ecommerce-app <your-acr-name>.azurecr.io/ecommerce-app:v1
docker push <your-acr-name>.azurecr.io/ecommerce-app:v1
```

Then point your Azure Web App / Container App to that image, and set the `DB_HOST`, `DB_PORT`,
`DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` environment variables to your Azure Database for MySQL
instance.

## API Endpoints

### Products

| Method | Endpoint             | Description          |
|--------|-----------------------|----------------------|
| GET    | /api/products          | List all products    |
| GET    | /api/products/{id}     | Get one product      |
| POST   | /api/products          | Create a product     |
| PUT    | /api/products/{id}     | Update a product     |
| DELETE | /api/products/{id}     | Delete a product     |

### Cart

| Method | Endpoint                     | Description               |
|--------|-------------------------------|----------------------------|
| POST   | /api/cart/add                 | Add an item to the cart    |
| GET    | /api/cart/{customerId}        | View a customer's cart     |
| DELETE | /api/cart/item/{cartItemId}   | Remove one item from cart  |

### Orders

| Method | Endpoint                            | Description                          |
|--------|---------------------------------------|----------------------------------------|
| POST   | /api/orders/place/{customerId}        | Place an order from the current cart   |
| GET    | /api/orders/{id}                      | Get order details                      |
| GET    | /api/orders/customer/{customerId}     | List a customer's orders               |
| PUT    | /api/orders/{id}/status/{status}      | Update order status (PLACED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED) |

## Try it end-to-end (curl examples)

```bash
# 1. List products (sample data is pre-loaded)
curl http://localhost:8080/api/products

# 2. Add product with id 1 to customer "naidu123"'s cart
curl -X POST http://localhost:8080/api/cart/add \
  -H "Content-Type: application/json" \
  -d '{"customerId":"naidu123","productId":1,"quantity":2}'

# 3. View the cart
curl http://localhost:8080/api/cart/naidu123

# 4. Place the order (converts the cart into an order, reduces stock)
curl -X POST http://localhost:8080/api/orders/place/naidu123

# 5. View the order (use the "id" returned in step 4)
curl http://localhost:8080/api/orders/1

# 6. View all orders for this customer
curl http://localhost:8080/api/orders/customer/naidu123
```

## How this maps to your interview answers

- **"Tell me about your project"** → this is the e-commerce platform: product catalog, cart, and
  order processing, built with Java 11 + Spring Boot.
- **"Explain the end-to-end flow"** → `CartController` → `CartService` → `CartItemRepository` →
  H2/MySQL, exactly as described.
- **Dependency Injection** → every `@Autowired` field in the controllers/services (e.g.
  `OrderController` injecting `OrderService`) is a live example you can point to.
- **@Service** → `ProductService`, `CartService`, `OrderService` all hold business logic, separate
  from the controllers.
- **Exception handling** → `GlobalExceptionHandler` shows checked vs. how you handle runtime
  errors (`ResourceNotFoundException`, `BadRequestException`) with clean JSON responses instead of
  stack traces.

## Notes for a beginner

- **Controller** = the "front door" that receives HTTP requests.
- **Service** = where the actual business rules live (e.g. "don't allow an order if stock is too low").
- **Repository** = talks to the database — you never write raw SQL for basic CRUD, Spring Data JPA
  generates it for you.
- **Entity** = a Java class that represents one database table.
- Lombok's `@Data` annotation automatically generates getters, setters, `toString()`, etc., so you
  don't have to write them by hand.
