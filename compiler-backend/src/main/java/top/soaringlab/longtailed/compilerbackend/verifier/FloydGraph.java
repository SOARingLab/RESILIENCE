package top.soaringlab.longtailed.compilerbackend.verifier;

import java.util.Arrays;

/**
 * 弗洛伊德算法就是通过中间节点不断的迭代，
 * 来求得各个顶点到各个节点的最短距离
 * 1.首先以中间顶点A，去更新距离表和前驱关系表
 * 2.以此类推，不断的变更中间顶点，去更新距离表和前驱关系表
 * 3.当遍历完成时，便实现最终结果
 *

 */
/* */

// 创建图
public class FloydGraph {
    String[] vertex; // 存放顶点的数组
    private float[][] dis; // 保存从各个顶点到其他顶点的距离，最后的结果，也是保存在该数组
    private float[][] pre; // 保存到达目标顶点的前驱顶点

    /**
     * @param length 大小
     * @param matrix 邻接矩阵
     * @param vertex 顶点数组
     */
    public FloydGraph(int length, float[][] matrix, String[] vertex) {

        
        this.vertex = vertex;
        this.dis = matrix;
        this.pre = new float[length][length];
        //  对pre数组初始化，注意存放的是前驱顶点的下标
        for (int i = 0; i < length; i++) {
            Arrays.fill(pre[i], i);
        }
    }

    // 显示pre数组和dis数组
    public void show() {
       // System.out.println("前驱关系");
//        for (int[] pr : pre) {
//            System.out.println(Arrays.toString(pr));
//        }
        //for (int i = 0; i < vertex.length; i++) {
        //    for (int j = 0; j < vertex.length; j++) {
        //        System.out.print(vertex[pre[i][j]] + " ");
        //    }
       //     System.out.println();
       // }

        System.out.println("距离矩阵");
        for (float[] di : dis) {
            System.out.println(Arrays.toString(di));
        }

    }

    // 弗洛伊德算法
    public void floyd() {
        float len = 0; // 变量保存距离
        // 从中间顶点的遍历， k就是中间顶点的下标 ['A', 'B', 'C', 'D', 'E', 'F', 'G']
        for (int k = 0; k < dis.length; k++) {
            // 从i顶点开始出发 ['A', 'B', 'C', 'D', 'E', 'F', 'G']
            for (int i = 0; i < dis.length; i++) {
                // 从i -> k -> j ['A', 'B', 'C', 'D', 'E', 'F', 'G']
                for (int j = 0; j < dis.length; j++) {
                    len = dis[i][k] + dis[k][j]; // 求出从i顶点出发，经过k中间顶点，到达j顶点的距离
                    if (len < dis[i][j]){ // 如果len小于dis[i][j]
                        dis[i][j] = len; // 更新距离
                        pre[i][j] = pre[k][j]; // 更新前驱顶点
                    }
                }
            }
        }
        //dis为引用数据类型，无需返回
        //return dis;
    }
}
