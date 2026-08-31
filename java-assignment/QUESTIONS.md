# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
I would unify the persistence approach around repository interfaces (ports) and use-case classes (domain services), following hexagonal/DDD patterns.

- The current mix of direct PanacheEntity static methods (Store), PanacheRepository (ProductRepository, WarehouseRepository), and domain use cases makes the code inconsistent.
- Refactoring would make business rules explicit, testable in isolation with mocks, and decoupled from the framework. It also makes swapping the database layer easier.
- For simple CRUD with no business rules, a PanacheRepository directly used by a resource is fine. As soon as there are validation rules, a use-case layer is worthwhile, as shown for warehouses.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
OpenAPI generation is great when the API is a published contract, consumed by multiple clients, or reviewed by stakeholders. It guarantees the spec and implementation stay in sync and gives consumers a machine-readable contract.

Hand-written resources are faster to bootstrap and more flexible for small, internal endpoints. They avoid generated-code noise and build-tool complexity.

My preference is to maintain an OpenAPI spec for all public/team-facing APIs and generate the server interfaces from it. For the assignment I followed the existing convention: generate Warehouse from the spec and keep Product/Store as hand-written resources. In a real project I would generate all three from a single API spec to keep the surface consistent.
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
I would prioritize as follows:

1. Unit tests for use cases and validation logic - they are cheap, fast, and capture the most important business rules.
2. API/integration tests for each resource - verify endpoints, status codes, serialization, and persistence against a real (test) database.
3. Edge-case/error tests - invalid input, not-found, duplicate keys, and constraint violations.
4. Infrastructure tests only where custom (e.g., the event listener for legacy sync).

To keep coverage effective over time, I would:
- Add a coverage gate in CI (JaCoCo) so regressions fail the build.
- Treat tests as production code: keep them readable, delete obsolete tests, and avoid testing only getters/setters.
- Use mutation testing occasionally to verify that assertions actually catch bugs, not just exercise code.
```