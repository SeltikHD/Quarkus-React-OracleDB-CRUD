package com.autoflex.domain.port.in;

import com.autoflex.domain.model.production.ProductionPlan;

/** ProductionUseCase - Input port for production planning operations. */
public interface ProductionUseCase {

  /**
   * Calculates the optimal production plan using the Greedy Algorithm.
   *
   * <p>The algorithm prioritizes products with higher unit prices, calculating the maximum number
   * of units that can be produced based on available raw material stock, then allocating materials
   * before moving to the next product.
   *
   * @return a ProductionPlan with optimal production quantities
   */
  ProductionPlan calculateProductionPlan();
}
