# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**

- *What is the source of truth for each cost type?* Labor may come from an HR/payroll system, inventory from the WMS, transportation from carriers, and overhead from finance. We need clear ownership and reliable ingestion pipelines.
- *What granularity is required?* Costs may need to be split per warehouse, per store, per product, or per shipment leg. The chosen granularity drives the data model.
- *Shared vs. direct costs:* A warehouse serves many stores; we need an allocation key (e.g., order lines, pallets shipped, cubic meters) that reflects real usage.
- *Time dimension:* Costs should be tied to a fiscal period and, when possible, to specific orders so margin analysis is meaningful.
- *Auditability:* Allocations must be traceable and reversible, so finance can reconcile them against the general ledger.

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**

- *Data-driven identification:* Start with a cost dashboard (warehouse cost per order, cost per cubic meter, transportation cost per route). Outliers quickly reveal optimization candidates.
- *Low-risk, high-impact first:* Consolidate slow-moving SKUs, renegotiate carrier contracts, or right-size packaging before large capital projects.
- *Network design:* Evaluate whether a few larger regional warehouses or many small local ones give the best landed cost for the desired service level.
- *Automation:* Replenishment, picking, and packing automation can reduce labor cost but require Capex; model the break-even point.
- *Service-level guardrails:* Every change should be A/B tested or piloted against on-time-in-full (OTIF) and customer satisfaction metrics.

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**

- *Single source of financial truth:* Cost data must map to GL accounts, cost centers, and projects in the ERP to avoid reconciliation gaps.
- *Near-real-time sync:* Event-driven integration (webhooks or CDC) keeps cost accruals fresh without nightly batch delays. The warehouse replacement flow, for example, should publish an event that the cost-control tool consumes.
- *Idempotency and retry:* Financial integrations must handle duplicates and transient failures safely.
- *Reporting and drill-down:* Finance users need both summarized views and the ability to drill down to individual warehouses, stores, or shipments.
- *Audit and controls:* Posting rules, approval workflows, and immutable cost journals help satisfy finance compliance requirements.

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**

- *Historical baselines:* Accurate forecasts need actuals by warehouse, store, and product category. Seasonality, growth, and promotional spikes must be incorporated.
- *Driver-based modeling:* Link costs to operational drivers (order volume, SKU count, average weight, distance to stores) rather than flat percentages.
- *Scenario planning:* Support what-if scenarios (new warehouse opening, store expansion, carrier rate changes) so budgets are stress-tested.
- *Rolling forecasts:* Replace static annual budgets with rolling forecasts that adjust as actuals arrive.
- *Accountability:* Assign budget owners per warehouse/store and track variance with clear explanations.

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**

- *Preserve history, separate entity:* Archiving the old warehouse while creating a new one under the same business-unit code lets the company keep historical costs and capacities intact. The archive record remains the anchor for past reports.
- *Budget continuity:* The replacement budget can be compared directly with the old warehouse's actuals. Without historical data, the new warehouse would have no baseline.
- *Cost transfer:* One-time costs (decommissioning, moving stock, setup) should be captured separately from ongoing operating costs so they do not distort run-rate analysis.
- *Capacity and service review:* Compare throughput, utilization, and cost per unit before and after replacement to validate the business case.
- *Audit trail:* The archive timestamp and reason for replacement support later financial and regulatory audits.

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
