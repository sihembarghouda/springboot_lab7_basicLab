
# Spring Cloud Microservices Lab: Eureka & Feign

## Overview
This lab demonstrates a simple microservices architecture using Spring Cloud. It includes:

- **Eureka Server** for service discovery
- **Product Service** to manage product data
- **Order Service** to place orders and fetch product details via Feign

## Prerequisites
- Java 17 or higher
- Maven or Gradle
- IDE (e.g., IntelliJ IDEA, Eclipse, VS Code)
- Basic Spring Boot knowledge

## Microservices Setup

### 1. Eureka Server

**Project Info**:
- Artifact: `eureka-server`
- Dependencies: Eureka Server

**Configuration (`application.properties`)**:
```properties
server.port=8761
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
spring.application.name=eureka-server
```

**Main Class**:
```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

### 2. Product Service

**Project Info**:
- Artifact: `product-service`
- Dependencies: Spring Web, Eureka Discovery Client

**Configuration (`application.properties`)**:
```properties
spring.application.name=product-service
server.port=8081
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

**Product Model**:
```java
public class Product {
    private Long id;
    private String name;
    private String description;
    private double price;
    // Constructor, getters, setters
}
```

**ProductController**:
```java
@RestController
@RequestMapping("/products")
public class ProductController {
    private Map<Long, Product> productMap = new HashMap<>();

    @PostConstruct
    public void setupProducts() {
        productMap.put(1L, new Product(1L, "Laptop", "High performance laptop", 999.99));
        productMap.put(2L, new Product(2L, "Phone", "Smartphone with great camera", 699.99));
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return new ArrayList<>(productMap.values());
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productMap.get(id);
    }
}
```

**Main Class**:
```java
@SpringBootApplication
@EnableEurekaClient
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
```

### 3. Order Service

**Project Info**:
- Artifact: `order-service`
- Dependencies: Spring Web, Eureka Discovery Client, OpenFeign

**Configuration (`application.properties`)**:
```properties
spring.application.name=order-service
server.port=8082
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

**Main Class**:
```java
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

**Product Model** (same as Product Service)

**Order Model**:
```java
public class Order {
    private Long id;
    private Long productId;
    private int quantity;
    private double totalPrice;
    private Product product;
    // Constructor, getters, setters
}
```

**Feign Client**:
```java
@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/products/{id}")
    Product getProduct(@PathVariable("id") Long id);
}
```

**OrderController**:
```java
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final ProductClient productClient;
    private final Map<Long, Order> orderMap = new HashMap<>();
    private Long nextOrderId = 1L;

    public OrderController(ProductClient productClient) {
        this.productClient = productClient;
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        Product product = productClient.getProduct(order.getProductId());
        order.setId(nextOrderId++);
        order.setProduct(product);
        order.setTotalPrice(product.getPrice() * order.getQuantity());
        orderMap.put(order.getId(), order);
        return order;
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderMap.get(id);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return new ArrayList<>(orderMap.values());
    }
}
```

## Running the Application

1. Start **Eureka Server** (`http://localhost:8761`)
2. Start **Product Service** (`http://localhost:8081/products`)
3. Start **Order Service** (`http://localhost:8082/orders`)

## Testing the Services

### Product Service

- `GET http://localhost:8081/products`
- `GET http://localhost:8081/products/1`

### Order Service

- `POST http://localhost:8082/orders`
```json
{ "productId": 1, "quantity": 2 }
```
- `GET http://localhost:8082/orders`
