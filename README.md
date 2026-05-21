# CPT204 Group Coursework — Java Application

A single Java application for the CPT204 group project: **Task A** ranks candidate locations from three CSV datasets; **Task B** runs shortest-path queries on a weighted road network using endpoints chosen from Task A. The program is organised in layers (`main` → `service` → `graph` / `sortingalgorithms` → `dataloader` → `model`).

---

## Overview

| Task | What the program does |
|------|------------------------|
| **A** | Load `candidates_A/B/C.csv`, sort with seven algorithms, benchmark time and `compareTo` counts, print Top 10 per dataset. |
| **B** | Load `paths.csv` (full graph, ~1000 vertices), run four required shortest-path cases with Dijkstra (array), Bellman–Ford, and Dijkstra (heap). |
| **C** | Layered packages, shared models, and services (see report / UML in `docs/` if present locally). |
| **D** | Team reflection (report; not in code). |

**Entry point:** `main.CourseworkMain` — runs Task A once, then Task B with the same Top-10 map.

**Optional:** `explore` package — BMSSP experiment (Duan et al., arXiv:2504.17033); not required for grading baseline correctness.

---

## Requirements

- **JDK 8 or later** (JDK 11+ recommended). No Maven or Gradle; compile with `javac`.
- **UTF-8** source and console (`javac -encoding UTF-8`).
- Dataset folder **`Group Project Datasets`** at the project root (or pass another path as the first program argument).

---

## Quick start

From the repository root (`Codes/`):

```bash
# Compile (creates ./out)
javac -encoding UTF-8 -d out -sourcepath src src/main/CourseworkMain.java

# Run full pipeline: Task A output + Task B
java -cp out main.CourseworkMain "Group Project Datasets"
```

If you omit the argument, the default directory is `Group Project Datasets` (relative to the current working directory).

**Note:** Task A uses `MEASURED_RUNS = 1000` per algorithm per dataset. A full run can take several minutes (especially bubble sort). For quick local tests, temporarily lower `MEASURED_RUNS` in `TaskASorting.java`.

---

## Data files

Place these under the dataset directory (e.g. `Group Project Datasets/`):

| File | Used by | Format |
|------|---------|--------|
| `candidates_A.csv` | Task A | Header: `location_id,priority_score` — ~1000 rows; mostly near-sorted scores. |
| `candidates_B.csv` | Task A | Same columns; pseudo-random order. |
| `candidates_C.csv` | Task A | Same columns; many tied scores (tie-break by `location_id`). |
| `paths.csv` | Task B | Header: `from_location,to_location,weight` — undirected edges; weights are positive (typically 1–15). |

**Important:** The graph contains **all** locations in `paths.csv`, not only the 30 Top-10 targets. Task A only selects which `location_id` values are used as query endpoints in Task B.

### Sorting rule (`Location.compareTo`)

1. Higher `priority_score` ranks first.  
2. If scores are equal, smaller `location_id` (lexicographic) ranks first.

### Task B query cases (brief)

Endpoints are taken from Top-10 **by rank index** (0 = 1st, 9 = 10th):

| Case | Route |
|------|--------|
| 1 | Dataset A #1 → itself |
| 2 | Dataset A #1 → Dataset A #10 |
| 3 | Dataset A #1 → Dataset B #1, **via** Dataset B #5 |
| 4 | Dataset A #1 → Dataset C #1, **via** Dataset B #5 then Dataset C #5 (fixed order) |

Waypoints are implemented by **splitting** the route into segments; each segment is a single-source shortest path on the full graph.

---

## Console output (Task B)

For each of the three **required** algorithms, the program prints Cases 1–4:

- `=== Dijkstra ===` (array-scan implementation, textbook-style `T` set)  
- `=== Bellman-Ford ===`  
- `=== Dijkstra (binary heap) ===`  

Each case includes: start, destination, optional waypoints, path string, total cost, wall time (ms).

Then:

- `=== explore: BMSSP (DMSY arXiv:2504.17033) ===` — experimental; may fail on some cases and print a **Dijkstra reference** path for comparison.

---

## Source layout (`src/`)

```
src/
├── main/
│   ├── CourseworkMain.java      # Single entry: Task A → Task B
│   ├── TaskASorting.java        # Sorting benchmarks + Top-10 map
│   └── TaskBShortestPath.java   # Graph load + Cases 1–4 + console output
├── model/
│   ├── Location.java            # Candidate record + compareTo
│   └── PathResult.java          # Shortest-path query result
├── dataloader/
│   ├── DataLoader.java          # candidates_*.csv → List<Location>
│   └── GraphDataLoader.java     # paths.csv → WeightedGraph<String>
├── sortingalgorithms/
│   ├── SortAlgorithm.java       # Strategy interface
│   ├── BubbleSort.java
│   ├── QuickSortFirst.java
│   ├── QuickSortLast.java
│   ├── QuickSortMiddle.java
│   ├── QuickSortRandom.java
│   ├── QuickSortMedianOfThree.java
│   └── MergeSort.java
├── service/
│   ├── SortingService.java      # Algorithm list, copyList, isSorted, topN
│   └── ShortestPathService.java # Segmented queries; int + if-else for SSSP choice
├── graph/
│   ├── Graph.java
│   ├── Edge.java / WeightedEdge.java
│   ├── UnweightedGraph.java     # Adjacency list + SearchTree
│   ├── WeightedGraph.java       # Delegates to graph.shortestpath
│   └── shortestpath/
│       ├── ShortestPathComputation.java  # Plain class (parent, cost, T)
│       ├── DijkstraNormal.java
│       ├── DijkstraHeap.java
│       └── BellmanFord.java
└── explore/                     # Optional; not used by CourseworkMain logic
    ├── ExploreMain.java
    ├── DmsyShortestPathService.java
    └── dmsy/                    # BMSSP-related implementation
```

### Package roles

| Package | Responsibility |
|---------|----------------|
| `main` | Orchestration and console I/O only. |
| `service` | Task-level rules: sort registration, segmented shortest paths, timing. |
| `model` | Immutable-style data carriers shared by A and B. |
| `dataloader` | CSV parsing isolated from algorithms. |
| `sortingalgorithms` | Concrete sorts; no knowledge of CSV or Task B. |
| `graph` | Graph ADT and SSSP implementations. |
| `explore` | Research extension; failures do not affect the three baselines. |

---

## Design highlights

- **Pipeline:** `CourseworkMain` calls `TaskASorting.runSelection` and passes the returned `Map<String, List<Location>>` directly into `TaskBShortestPath.runTaskB`.
- **Fair sorting comparison:** Each timed run sorts a **fresh copy** of the list; warm-up runs are excluded from reported averages.
- **Graph storage:** Adjacency list `List<List<Edge>>` with integer vertex indices; each undirected CSV row becomes two directed `WeightedEdge` entries.
- **ShortestPathService:** One `buildPathResult` loop; algorithm choice uses `int` constants and `if-else` (no lambdas or functional interfaces required for coursework).
- **Top-10 storage:** After all algorithms finish on a dataset, the last algorithm’s sorted order defines the stored Top-10 (see `SortingService.allAlgorithms()` order — currently ends with merge sort).

---

## Task A — algorithms and measurement

Registered in `SortingService.allAlgorithms()`:

1. Bubble Sort  
2. Quick Sort (first pivot)  
3. Quick Sort (last pivot)  
4. Quick Sort (middle pivot)  
5. Quick Sort (random pivot)  
6. Quick Sort (median-of-three)  
7. Merge Sort  

Per algorithm and per dataset:

- **50** warm-up sorts (not reported)  
- **1000** measured sorts — average wall time (ms) and average `compareTo` count  
- `SortingService.isSorted` check on the last run  

---

## Task B — shortest-path algorithms

| Implementation | Class | Invoked via |
|----------------|-------|-------------|
| Dijkstra (array scan) | `DijkstraNormal` | `WeightedGraph.getShortestPath` |
| Dijkstra (binary heap) | `DijkstraHeap` | `WeightedGraph.dijkstraShortestPathTreeHeap` |
| Bellman–Ford | `BellmanFord` | `WeightedGraph.bellmanFordShortestPathTree` |

`ShortestPathComputation` holds `parent`, `cost`, and `T` after one SSSP run; `WeightedGraph` converts it to `ShortestPathTree` for path and cost queries.

---

## Optional: explore package

Compile and run separately (does not replace `CourseworkMain`):

```bash
javac -encoding UTF-8 -d out -sourcepath src src/explore/ExploreMain.java
java -cp out explore.ExploreMain
```

This code is an **educational port** of BMSSP ideas from Duan et al. (arXiv:2504.17033). It is **not** a full production implementation of the paper’s data structures. When BMSSP fails, `TaskBShortestPath` prints Dijkstra reference cost and path.

---

## Repository notes

| Path | Status |
|------|--------|
| `out/` | Build output (gitignored); create with `javac -d out`. |
| `Graph codes/` | Local textbook-style reference copy (gitignored); **not** compiled by `CourseworkMain`. |
| `docs/` | May contain UML / report notes locally (gitignored in this repo). |
| `W10 Visualizers/`, `src/visualizers/` | Swing demos (gitignored); independent of the coursework entry. |

---

## Documentation and report

For coursework reporting, align console output with:

- **Chapter 1:** sorting results and complexity discussion.  
- **Chapter 2:** Cases 1–4 paths and costs (three baselines should agree).  
- **Chapter 3:** package structure and OOP (class diagrams, sequence diagrams).  
- **Chapter 4:** team process (e.g. Trello, Git, Feishu) and reflection.

Internal technical notes may exist in `docs/CPT204_项目技术文档.md` or `docs/UML_COURSEWORK.md` if kept in your local clone.

---

## Troubleshooting

| Issue | Suggestion |
|-------|------------|
| `FileNotFoundException` for CSV | Run from project root or pass absolute path: `java -cp out main.CourseworkMain "C:\...\Group Project Datasets"` |
| Run takes very long | Reduce `MEASURED_RUNS` in `TaskASorting` for debugging only; restore for final report numbers |
| `IllegalStateException` on sort | Algorithm failed `isSorted` — check `compareTo` and sort implementation |
| `No path found` | Start/end ID not in graph or disconnected segment — verify `location_id` exists in `paths.csv` |
| Garbled console on Windows | Use UTF-8 terminal; compile with `-encoding UTF-8` |

---

## License / academic use

Coursework submission for CPT204. Follow your module’s academic integrity rules for collaboration and AI tool use.
