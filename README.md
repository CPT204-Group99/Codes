# CPT204 小组作业源码（Codes）

## 源码布局（`src/` 下按包划分）

| 包 | 说明 |
|----|------|
| `model` | `Location`、`PathResult` |
| `graph` | 图 ADT：`Graph`、`Edge`、`WeightedEdge`、`UnweightedGraph`、`WeightedGraph` |
| `sortingalgorithms` | `SortAlgorithm` 及冒泡、归并、多种快排 |
| `service` | `DataLoader`、`GraphDataLoader`、`SortingService`、`ShortestPathService` |
| `main` | `TaskASorting`、`TaskBShortestPath` 入口 |

## 编译与运行

在项目根目录执行：

```text
javac -encoding UTF-8 -d out -sourcepath src src/main/TaskASorting.java
java -cp out main.TaskASorting "Group Project Datasets"
```

```text
javac -encoding UTF-8 -d out -sourcepath src src/main/TaskBShortestPath.java
java -cp out main.TaskBShortestPath "Group Project Datasets"
```

未传参时默认数据目录为相对路径 `Group Project Datasets`（需在项目根下运行或自行传入绝对路径）。
