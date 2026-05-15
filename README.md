# CPT204 小组作业源码（Codes）

## 源码布局（`src/` 下按包划分）

| 包 | 说明 |
|----|------|
| `model` | `Location`、`PathResult` |
| `graph` | 图 ADT：`Graph`、`Edge`、`WeightedEdge`、`UnweightedGraph`、`WeightedGraph` |
| `sortingalgorithms` | `SortAlgorithm` 及冒泡、归并、多种快排 |
| `io` | `DataLoader`、`GraphDataLoader`（CSV 读入） |
| `service` | `SortingService`；`ShortestPathService`（数组版 Dijkstra、Bellman-Ford、`PriorityQueue` 堆版 Dijkstra） |
| `main` | `CourseworkMain` 为唯一程序入口；`TaskASorting`、`TaskBShortestPath` 为 A/B 逻辑 |
| `explore` | 论文 Duan et al. (arXiv:2504.17033v2) 的 **Algorithm 1–3 + Lemma 3.3 D** 实现，包路径 `explore.dmsy`；`explore.ExploreMain` 为演示入口 |

## 编译与运行

在项目根目录执行（一次运行：完整 Task A 输出 + Task B，且只执行一遍 `runSelection`）：

```text
javac -encoding UTF-8 -d out -sourcepath src src/main/CourseworkMain.java
java -cp out main.CourseworkMain "Group Project Datasets"
```

Task B 会依次输出：数组版 Dijkstra、Bellman-Ford、堆版 Dijkstra，最后为 `explore` 包中的 BMSSP（DMSY）实验段。

独立运行 `explore` 包中的演示（与作业入口无关）：

```text
javac -encoding UTF-8 -d out -sourcepath src src/explore/ExploreMain.java
java -cp out explore.ExploreMain
```

（`explore` 为论文算法实验代码；Lemma 3.3 的 D 为语义等价实现，非论文中的分块+红黑树证明版数据结构。）

未传参时默认数据目录为相对路径 `Group Project Datasets`（需在项目根下运行或自行传入绝对路径）。
