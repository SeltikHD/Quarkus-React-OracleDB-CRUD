/**
 * =============================================================================
 * HEXAGONAL ARCHITECTURE - DOMAIN LAYER
 * =============================================================================
 * 
 * This package contains the core business logic of the Autoflex application.
 * 
 * CRITICAL ARCHITECTURAL CONSTRAINTS:
 * 
 * 1. NO FRAMEWORK DEPENDENCIES
 *    Domain objects (Entities, Value Objects, Domain Services) must NOT have
 *    any dependencies on infrastructure frameworks such as:
 *    - JPA annotations (@Entity, @Column, @Id)
 *    - JSON annotations (@JsonProperty, @JsonIgnore)
 *    - CDI annotations (@Inject, @ApplicationScoped)
 *    - Any io.quarkus.* imports
 *    
 * 2. PURE JAVA ONLY
 *    Domain objects should only use:
 *    - Java standard library (java.*, javax.validation for domain validation)
 *    - Domain-specific exceptions and value objects
 *    
 * 3. PORTS (INTERFACES)
 *    The domain defines PORTS (interfaces) that describe what it needs from
 *    the outside world, without knowing how it will be implemented.
 *    
 * 4. IMMUTABILITY
 *    Prefer immutable value objects and entities where possible.
 *    Use records for Value Objects in Java 17+.
 * 
 * PACKAGE STRUCTURE:
 * 
 * com.autoflex.domain
 * ├── model/           → Domain Entities and Value Objects
 * │   ├── product/     → Product aggregate
 * │   └── rawmaterial/ → Raw Material aggregate
 * ├── port/            → Hexagonal Ports (interfaces)
 * │   ├── in/          → Input ports (use cases)
 * │   └── out/         → Output ports (repositories, external services)
 * ├── service/         → Domain Services (pure business logic)
 * └── exception/       → Domain-specific exceptions
 * 
 * =============================================================================
 * WHY HEXAGONAL OVER CLEAN ARCHITECTURE?
 * =============================================================================
 * 
 * While Clean Architecture and Hexagonal Architecture share similar goals,
 * Hexagonal (Ports & Adapters) was chosen for this project because:
 * 
 * 1. CLEARER INFRASTRUCTURE BOUNDARY
 *    The explicit Ports (in/out) and Adapters pattern makes it immediately
 *    clear where the domain ends and infrastructure begins.
 *    
 * 2. TESTABILITY
 *    Input ports can be easily mocked for unit testing domain logic.
 *    Output ports can be stubbed to avoid database dependencies in tests.
 *    
 * 3. FRAMEWORK AGNOSTIC CORE
 *    The production calculation algorithm (core business logic) remains
 *    completely decoupled from Oracle/Quarkus specifics.
 *    
 * 4. SIMPLER MENTAL MODEL
 *    "Inside" (domain) and "Outside" (adapters) is easier to reason about
 *    than multiple circular layers.
 * 
 * =============================================================================
 */
package com.autoflex.domain;
