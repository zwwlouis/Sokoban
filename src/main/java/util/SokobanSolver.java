package util;

import model.SokobanException;
import model.SokobanMap;

import java.util.*;

public class SokobanSolver {
    //解题过程 int数组一共四位，0\1:箱子位置，2\3：推动方向
    private Stack<int[]> normalSteps;
    //所有有可能的地图状态
    private Map<String, Integer> allPossibleMap;
    /**
     * 先通过简单互斥的方法记录地图状态，快速求得一个可能解，
     * 并把该解的所有中间过程地图状态记录在solvableMap中
     *
     * 第二步通过最优互斥的方式记录地图，但是地图状态限制在第一步求得的solvableMap中
     * 即在第一步求得的次优解中筛选出一个可能的最优解
     */
    private Map<String, Integer> solvableMap;
    private long timeUsed;
    private long maxRecur;
    private long totalRecur;
    private int curRecur;
    private static long maxRecurLimit = 300;
    private static Stack<int[]> bestSteps;
    private static Stack<int[]> bestStepsClone;
    //右，左，下，上四个方向
    public static int[][] directs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

    public static void setMaxRecurLimit(long maxRecurLimit) {
        SokobanSolver.maxRecurLimit = maxRecurLimit;
    }

    public SokobanSolver() {
        normalSteps = new Stack<>();
        bestSteps = new Stack<>();
        bestStepsClone = new Stack<>();
        allPossibleMap = new HashMap<>();
        solvableMap = new HashMap<>();
    }
    /**
     * 尝试给出一个解答（基本不是最优解）
     * @param sokobanMap
     * @return
     * @throws SokobanException
     */
    public boolean NormalSolver(SokobanMap sokobanMap) throws SokobanException {
        boolean result = false;
//        SokobanUtil.specialPrintMap(sokobanMap.getMap());
        long start = System.currentTimeMillis();
        //克隆地图避免在求解过程中遭到修改
        SokobanMap cloneMap = sokobanMap.clone();
        result = toNormalSolution(cloneMap);
        long end = System.currentTimeMillis();
        timeUsed = end - start;
        return result;
    }

    private boolean toNormalSolution(SokobanMap sokobanMap) throws SokobanException {
        totalRecur++;
        if(totalRecur%100000 == 0){
            System.out.println("第"+totalRecur+"次运算  深度 = "+curRecur);
            //            SokobanUtil.specialPrintMap(sokobanMap.getMap());
            //            if(totalRecur > 999999999){
            //                throw new SokobanException("题目过于复杂，超过求解时间");
            //            }
        }
        curRecur++;
        if(curRecur > maxRecurLimit){
            curRecur -= 1;
            return false;
        }
        if(curRecur > maxRecur){
            maxRecur = curRecur;
        }
        //首先对地图做转换，将玩家填满所有可能到达的区域
        Queue<int[]> allPossiblePlayer = SokobanUtil.fillPlayerOnAllReachable(sokobanMap.getMap());
        //判断该地图是否之前出现过
        if (!hasMap(sokobanMap.getMap())) {
            //获取所有箱子的位置
            List<int[]> boxList = sokobanMap.getBoxSites();
            for (int i = 0; i < boxList.size(); i++) {
                int[] boxSite = boxList.get(i);
                for (int j = 0; j < directs.length; j++) {
                    int[] direct = directs[j];
                    //检查该方向上箱子是否可以移动
                    if (SokobanUtil.boxReachable(sokobanMap, boxSite, direct)) {
                        //得到原地图的克隆
                        SokobanMap newMap = sokobanMap.clone();
                        //移动箱子
                        SokobanUtil.pushBoxForOneCell(newMap, i, direct);
                        //将操作压栈
                        normalSteps.push(new int[]{boxSite[0], boxSite[1], direct[0], direct[1]});
                        //判断是否已经完成
                        if (isComplete(newMap)) {
                            curRecur -= 1;
                            return true;
                        }
                        //清空场上多余的玩家
                        SokobanUtil.clearPlayerAtSites(newMap.getMap(), allPossiblePlayer);
                        //递归调用函数
                        boolean result = toNormalSolution(newMap);
                        if (result) {
                            curRecur -= 1;
                            return true;
                        } else {
                            //如果递归无果，则说明此路不通，清除该操作，并继续寻找
                            normalSteps.pop();
                        }
                    }
                }
            }
        }
        curRecur -= 1;
        return false;
    }

    /**
     * 尝试给出最优解（不一定最优）
     * 需要继续NormalSolver的结果
     * @return
     */
    public boolean BestSolver(SokobanMap sokobanMap) throws SokobanException {
        if(normalSteps == null || normalSteps.size()<1){
            throw new SokobanException("请先调用normalSolver求得一个解！");
        }
        allPossibleMap = new HashMap<>();
        totalRecur = 0;
        maxRecur = 0;
        //克隆地图避免在记录过程中遭到修改
        SokobanMap cloneMap = sokobanMap.clone();
//        recordSolutionToMap(cloneMap,normalSteps);
        System.out.println("Start BestSolver!!");
        SokobanUtil.specialPrintMap(sokobanMap.getMap());
        //克隆地图避免在求解过程中遭到修改
        cloneMap = sokobanMap.clone();
        Boolean result = toBestSolution(cloneMap);
        return result;
    }

    /**
     * 将解题过程中所有的地图状态记录下来
     * @param sokobanMap
     * @param steps
     */
    private void recordSolutionToMap(SokobanMap sokobanMap, Stack<int[]> steps) throws SokobanException {
        Object[] stepList = steps.toArray();
        int stepNum = stepList.length;
        //先记录地图的初始状态
        SokobanMap cloneMap = sokobanMap.clone();
        SokobanUtil.fillPlayerOnAllReachable(cloneMap.getMap());
        solvableMap.put(SokobanUtil.mapToString(cloneMap.getMap()),0);
        for (int i = 0; i < stepNum; i++) {
            int[] step = (int[]) stepList[i];
            System.out.println(String.format("第%d步  %s行,%s列 向%s移动", i,step[0]+1, step[1]+1, SokobanUtil.getDirectCnWord(step[2], step[3])));
            SokobanUtil.movePlayerToCell(sokobanMap,new int[]{step[0],step[1]},new int[]{step[2],step[3]});
            cloneMap = sokobanMap.clone();
            SokobanUtil.fillPlayerOnAllReachable(cloneMap.getMap());
            solvableMap.put(SokobanUtil.mapToString(cloneMap.getMap()),i+1);
        }
        SokobanUtil.specialPrintMap(sokobanMap.getMap());
        System.out.println(String.format("地图状态记录完毕! 总步数 %d步", stepNum));
    }

    private boolean toBestSolution(SokobanMap sokobanMap) throws SokobanException {
        totalRecur++;
        if(totalRecur%1000000 == 0){
            System.out.println("第"+totalRecur+"次运算  深度 = "+curRecur);
        }
        curRecur++;
        if(curRecur > maxRecurLimit){
            curRecur -= 1;
            return false;
        }
        if(curRecur > maxRecur){
            maxRecur = curRecur;
        }
        //首先对地图做转换，将玩家填满所有可能到达的区域
        Queue<int[]> allPossiblePlayer = SokobanUtil.fillPlayerOnAllReachable(sokobanMap.getMap());
        //判断该地图是否是更优解
        if (isBetterSolution(sokobanMap.getMap(),curRecur-1)) {
            //获取所有箱子的位置
            List<int[]> boxList = sokobanMap.getBoxSites();
            for (int i = 0; i < boxList.size(); i++) {
                int[] boxSite = boxList.get(i);
                for (int j = 0; j < directs.length; j++) {
                    int[] direct = directs[j];
                    //检查该方向上箱子是否可以移动
                    if (SokobanUtil.boxReachable(sokobanMap, boxSite, direct)) {
                        //打印移动方向
                        //                    System.out.println(String.format("目标箱子%d行,%d列;移动方向%d;%d",boxSite[0],boxSite[1],direct[0],direct[1]));
                        //得到原地图的克隆
                        SokobanMap newMap = sokobanMap.clone();
                        //移动箱子
                        SokobanUtil.pushBoxForOneCell(newMap, i, direct);
                        //将操作压栈
                        bestSteps.push(new int[]{boxSite[0], boxSite[1], direct[0], direct[1]});
                        //判断是否已经完成
                        if (isComplete(newMap)) {
                            curRecur -= 1;
                            return true;
                        }
                        //清空场上多余的玩家
                        SokobanUtil.clearPlayerAtSites(newMap.getMap(), allPossiblePlayer);
                        //递归调用函数
                        boolean result = toBestSolution(newMap);
                        if (result) {
                            //得到解答
                            int step = curRecur;
                            System.out.println("best solver 找到一个解 step = "+step);
                            if(step < maxRecurLimit){
                                //记录当前步数和解法，并继续搜寻
                                maxRecurLimit = step;
                                bestStepsClone = (Stack<int[]>) bestSteps.clone();
                                bestSteps.pop();
                            }
                        } else {
                            //如果递归无果，则说明此路不通，清除该操作，并继续寻找
                            bestSteps.pop();
                        }
                    }
                }
            }
        }
        curRecur -= 1;
        return false;
    }

    public String getSolveStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("总计用时：%d ms;\r\n", timeUsed));
        sb.append(String.format("克隆用时：%d ms;\r\n", SokobanMap.getCloneTime()));
        sb.append(String.format("总调用次数：%d;\r\n", totalRecur));
        sb.append(String.format("最大递归深度：%d;\r\n", maxRecur));
        sb.append(String.format("探索地图数量：%d;\r\n", allPossibleMap.size()));
        return sb.toString();
    }

    /**
     * 仅打印箱子的移动轨迹
     */
    public void printBoxRoute(Stack<int[]> steps) {
        Object[] stepList = steps.toArray();
        int stepNum = stepList.length;
        for (int i = 0; i < stepNum; i++) {
            int[] step = (int[]) stepList[i];
            System.out.println(String.format("第%d步  %s行,%s列  向%s移动",i,step[0]+1, step[1]+1, SokobanUtil.getDirectCnWord(step[2], step[3])));
        }
        System.out.println(String.format("总步数 %d步", stepNum));
    }

    /**
     * 仅打印箱子的移动轨迹,同时打印地图
     */
    public void printBoxRouteWithMap(SokobanMap sokobanMap,Stack<int[]> steps) throws SokobanException {
        Object[] stepList = steps.toArray();
        int stepNum = stepList.length;
        System.out.println(String.format("总步数 %d步", stepNum));
        SokobanUtil.specialPrintMap(sokobanMap.getMap());
        for (int i = 0; i < stepNum; i++) {
            int[] step = (int[]) stepList[i];
            System.out.println(String.format("第%d步  %s行,%s列 向%s移动", i,step[0]+1, step[1]+1, SokobanUtil.getDirectCnWord(step[2], step[3])));
            SokobanUtil.movePlayerToCell(sokobanMap,new int[]{step[0],step[1]},new int[]{step[2],step[3]});
            SokobanUtil.specialPrintMap(sokobanMap.getMap());
        }
        System.out.println(String.format("总步数 %d步", stepNum));
    }

    public void printRouteMap(Stack<int[]> steps) {
        Object[] stepList = steps.toArray();
        int stepNum = stepList.length;
        for (int i = 0; i < stepNum; i++) {
            int[] step = (int[]) stepList[i];
            System.out.println(String.format("%s行,%s列  向%s移动", step[0]+1, step[1]+1, SokobanUtil.getDirectCnWord(step[2], step[3])));
        }
        System.out.println(String.format("总步数 %d步", stepNum));
    }

    public Stack<int[]> getNormalSteps() {
        return normalSteps;
    }

    public Stack<int[]> getBestSteps() { return bestSteps;  }

    public Stack<int[]> getBestStepsClone() {
        return bestStepsClone;
    }


    /**
     * 检查当前状态是否已经达到过
     * @param map
     * @return
     */
    private boolean hasMap(int[][] map) {
        String mapStr = SokobanUtil.mapToString(map);
        return hasMap(mapStr);
    }

    private boolean hasMap(String mapStr) {
        if (allPossibleMap.containsKey(mapStr)) {
            return true;
        } else {
            allPossibleMap.put(mapStr, 0);
            return false;
        }
    }


    /**
     * 检查当前状态是否是更优的解
     * @param map
     * @param step 达到当前状态所用步数
     * @return
     */
    private boolean isBetterSolution(int[][] map,int step) {
        String mapStr = SokobanUtil.mapToString(map);
        if (solvableMap.containsKey(mapStr)) {
            int formerStep = solvableMap.get(mapStr);
            if(step < formerStep){
                solvableMap.put(mapStr, step);
                return true;
            }else{
                return false;
            }
        } else {
//            //如果不在解空间内，则使用普通去重
//            return !hasMap(mapStr);
//            return false;
            solvableMap.put(mapStr, step);
            return true;
        }
    }

    /**
     * 判断关卡是否已经完成
     *
     * @param sokobanMap
     * @return
     */
    private boolean isComplete(SokobanMap sokobanMap) {
        List<int[]> boxList = sokobanMap.getBoxSites();
        int[][] map = sokobanMap.getMap();
        for (int i = 0; i < boxList.size(); i++) {
            int[] boxSite = boxList.get(i);
            //是要有一个箱子不在位置上，就不算完成
            if (!SokobanUtil.hasDestination(map[boxSite[0]][boxSite[1]])) {
                return false;
            }
        }
        return true;
    }
}
