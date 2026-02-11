# Manual Verification Roadmap

> Step-by-step guide to verify all backend features using `curl`.
> Start the application first with `mvn quarkus:dev` (H2 database auto-configured in dev mode).

---

## 1. Create Raw Materials

```bash
# 1a. Create Steel Sheet
curl -s -X POST http://localhost:8080/api/v1/raw-materials \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Steel Sheet",
    "description": "High-grade carbon steel 2mm",
    "code": "RM-STEEL-001",
    "unit": "KILOGRAM",
    "stockQuantity": 500.00,
    "unitCost": 15.50
  }' | jq .

# 1b. Create Rubber Compound
curl -s -X POST http://localhost:8080/api/v1/raw-materials \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Rubber Compound",
    "description": "Industrial grade rubber",
    "code": "RM-RUBBER-001",
    "unit": "KILOGRAM",
    "stockQuantity": 200.00,
    "unitCost": 8.75
  }' | jq .

# 1c. Create Plastic Pellets
curl -s -X POST http://localhost:8080/api/v1/raw-materials \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Plastic Pellets",
    "description": "ABS plastic pellets",
    "code": "RM-PLASTIC-001",
    "unit": "KILOGRAM",
    "stockQuantity": 300.00,
    "unitCost": 5.25
  }' | jq .

# 1d. Create Paint (limited stock)
curl -s -X POST http://localhost:8080/api/v1/raw-materials \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Industrial Paint",
    "description": "Weather-resistant paint",
    "code": "RM-PAINT-001",
    "unit": "LITER",
    "stockQuantity": 50.00,
    "unitCost": 22.00
  }' | jq .
```

**Expected:** Each returns `201 Created` with the raw material data including an auto-generated `id`.

---

## 2. Verify Raw Material Listing

```bash
# List all raw materials
curl -s http://localhost:8080/api/v1/raw-materials | jq .

# Search by name
curl -s "http://localhost:8080/api/v1/raw-materials?search=steel" | jq .

# Get single raw material by ID (replace 1 with actual ID)
curl -s http://localhost:8080/api/v1/raw-materials/1 | jq .
```

---

## 3. Adjust Raw Material Stock

```bash
# Add 100 kg of steel
curl -s -X PATCH http://localhost:8080/api/v1/raw-materials/1/stock \
  -H "Content-Type: application/json" \
  -d '{"quantity": 100.00}' | jq .

# Expected: stockQuantity becomes 600.00
```

---

## 4. Create Products

```bash
# 4a. Premium Widget ($150) — uses Steel + Rubber + Paint
curl -s -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Premium Widget",
    "description": "High-end widget with paint finish",
    "sku": "PROD-PREMIUM-001",
    "unitPrice": 150.00,
    "stockQuantity": 0
  }' | jq .

# 4b. Standard Widget ($80) — uses Steel + Plastic
curl -s -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Standard Widget",
    "description": "Standard plastic widget",
    "sku": "PROD-STD-001",
    "unitPrice": 80.00,
    "stockQuantity": 0
  }' | jq .

# 4c. Budget Widget ($35) — uses Plastic only
curl -s -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Budget Widget",
    "description": "Economy plastic widget",
    "sku": "PROD-BUDGET-001",
    "unitPrice": 35.00,
    "stockQuantity": 0
  }' | jq .
```

**Expected:** Each returns `201 Created`. Note the returned `id` values for use below (assumed: Premium=1, Standard=2, Budget=3).

---

## 5. Add Bill of Materials (BOM) to Products

```bash
# 5a. Premium Widget BOM: 20 kg Steel + 5 kg Rubber + 2 L Paint
curl -s -X POST http://localhost:8080/api/v1/products/1/materials \
  -H "Content-Type: application/json" \
  -d '{"rawMaterialId": 1, "quantityRequired": 20.0}' | jq .

curl -s -X POST http://localhost:8080/api/v1/products/1/materials \
  -H "Content-Type: application/json" \
  -d '{"rawMaterialId": 2, "quantityRequired": 5.0}' | jq .

curl -s -X POST http://localhost:8080/api/v1/products/1/materials \
  -H "Content-Type: application/json" \
  -d '{"rawMaterialId": 4, "quantityRequired": 2.0}' | jq .

# 5b. Standard Widget BOM: 10 kg Steel + 8 kg Plastic
curl -s -X POST http://localhost:8080/api/v1/products/2/materials \
  -H "Content-Type: application/json" \
  -d '{"rawMaterialId": 1, "quantityRequired": 10.0}' | jq .

curl -s -X POST http://localhost:8080/api/v1/products/2/materials \
  -H "Content-Type: application/json" \
  -d '{"rawMaterialId": 3, "quantityRequired": 8.0}' | jq .

# 5c. Budget Widget BOM: 5 kg Plastic
curl -s -X POST http://localhost:8080/api/v1/products/3/materials \
  -H "Content-Type: application/json" \
  -d '{"rawMaterialId": 3, "quantityRequired": 5.0}' | jq .
```

---

## 6. Verify Product BOM

```bash
# Get Premium Widget with its materials
curl -s http://localhost:8080/api/v1/products/1/materials | jq .

# Expected: 3 material items (Steel, Rubber, Paint)
```

---

## 7. Update Material Quantity in BOM

```bash
# Change Premium Widget's steel requirement from 20 to 25 kg
curl -s -X PUT http://localhost:8080/api/v1/products/1/materials/1 \
  -H "Content-Type: application/json" \
  -d '{"quantityRequired": 25.0}' | jq .
```

---

## 8. Calculate Production Plan (Greedy Algorithm)

```bash
curl -s -X POST http://localhost:8080/api/v1/production/calculate | jq .
```

**Expected Result (approximate):**
The Greedy Algorithm should prioritize by `unitPrice` descending:

1. **Premium Widget ($150)** — processed first
   - Needs: 25 kg Steel, 5 kg Rubber, 2 L Paint per unit
   - Limiting factor: Steel (600/25 = 24), Rubber (200/5 = 40), Paint (50/2 = 25) → **min = 24 units**
   - Consumes: 600 Steel, 120 Rubber, 48 Paint
   - Remaining: 0 Steel, 80 Rubber, 2 Paint

2. **Standard Widget ($80)** — processed second
   - Needs: 10 kg Steel, 8 kg Plastic per unit
   - Steel remaining: 0 → **0 units** (skipped)

3. **Budget Widget ($35)** — processed third
   - Needs: 5 kg Plastic per unit
   - Plastic remaining: 300/5 = **60 units**
   - Consumes: 300 Plastic

**Expected JSON structure:**

```json
{
  "items": [
    {
      "productName": "Premium Widget",
      "quantity": 24,
      "unitPrice": 150.00,
      "totalValue": 3600.00
    },
    {
      "productName": "Budget Widget",
      "quantity": 60,
      "unitPrice": 35.00,
      "totalValue": 2100.00
    }
  ],
  "totalProductionValue": 5700.00,
  "hasProduction": true,
  "totalUnits": 84
}
```

---

## 9. Error Handling Verification

```bash
# 9a. Duplicate SKU (expect 409)
curl -s -w "\nHTTP Status: %{http_code}\n" -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Duplicate", "sku": "PROD-PREMIUM-001", "unitPrice": 10, "stockQuantity": 0}'

# 9b. Duplicate raw material code (expect 409)
curl -s -w "\nHTTP Status: %{http_code}\n" -X POST http://localhost:8080/api/v1/raw-materials \
  -H "Content-Type: application/json" \
  -d '{"name": "Dup Steel", "code": "RM-STEEL-001", "unit": "KILOGRAM", "stockQuantity": 0, "unitCost": 1}'

# 9c. Product not found (expect 404)
curl -s -w "\nHTTP Status: %{http_code}\n" http://localhost:8080/api/v1/products/9999

# 9d. Raw material not found (expect 404)
curl -s -w "\nHTTP Status: %{http_code}\n" http://localhost:8080/api/v1/raw-materials/9999

# 9e. Invalid input — missing required fields (expect 400)
curl -s -w "\nHTTP Status: %{http_code}\n" -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name": ""}'

# 9f. Add duplicate material to product BOM (expect 400)
curl -s -w "\nHTTP Status: %{http_code}\n" -X POST http://localhost:8080/api/v1/products/1/materials \
  -H "Content-Type: application/json" \
  -d '{"rawMaterialId": 1, "quantityRequired": 10}'

# 9g. Stock below zero (expect 400)
curl -s -w "\nHTTP Status: %{http_code}\n" -X PATCH http://localhost:8080/api/v1/raw-materials/1/stock \
  -H "Content-Type: application/json" \
  -d '{"quantity": -99999}'
```

---

## 10. Remove Material from BOM

```bash
# Remove Paint from Premium Widget
curl -s -X DELETE http://localhost:8080/api/v1/products/1/materials/4 | jq .

# Verify it was removed
curl -s http://localhost:8080/api/v1/products/1/materials | jq .
```

---

## 11. Deactivate and Delete

```bash
# Deactivate Budget Widget — it will be excluded from production calculation
curl -s -X DELETE http://localhost:8080/api/v1/products/3 -w "\nHTTP Status: %{http_code}\n"

# Recalculate production — Budget Widget should not appear
curl -s -X POST http://localhost:8080/api/v1/production/calculate | jq .
```

---

## 12. OpenAPI / Swagger UI

```bash
# Open in browser
open http://localhost:8080/q/swagger-ui

# Or check raw OpenAPI spec
curl -s http://localhost:8080/q/openapi | head -50
```

---

## 13. Health Check

```bash
curl -s http://localhost:8080/health | jq .
```

---

## Summary Checklist

| #   | Feature              | HTTP Method | Endpoint                                 | Status |
| --- | -------------------- | ----------- | ---------------------------------------- | ------ |
| 1   | Create raw material  | POST        | `/api/v1/raw-materials`                  | ☐      |
| 2   | List raw materials   | GET         | `/api/v1/raw-materials`                  | ☐      |
| 3   | Adjust stock         | PATCH       | `/api/v1/raw-materials/{id}/stock`       | ☐      |
| 4   | Create product       | POST        | `/api/v1/products`                       | ☐      |
| 5   | Add material to BOM  | POST        | `/api/v1/products/{id}/materials`        | ☐      |
| 6   | View BOM             | GET         | `/api/v1/products/{id}/materials`        | ☐      |
| 7   | Update BOM quantity  | PUT         | `/api/v1/products/{id}/materials/{rmId}` | ☐      |
| 8   | Calculate production | POST        | `/api/v1/production/calculate`           | ☐      |
| 9   | Remove from BOM      | DELETE      | `/api/v1/products/{id}/materials/{rmId}` | ☐      |
| 10  | Error: duplicate SKU | POST        | `/api/v1/products`                       | ☐      |
| 11  | Error: not found     | GET         | `/api/v1/products/9999`                  | ☐      |
| 12  | Deactivate product   | DELETE      | `/api/v1/products/{id}`                  | ☐      |
| 13  | Health check         | GET         | `/health`                                | ☐      |
