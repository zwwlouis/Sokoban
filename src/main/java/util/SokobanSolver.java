package util;

import model.SokobanException;
import model.SokobanMap;

import java.util.*;

public class SokobanSolver {
    //解题过程 int数组一共四位，0\1:箱子位置，2\3：推动方向
    private Stack<int[]> solveSteps;
    private Map<String, String> allPossibleMap;
    private long timeUsed;
    private long maxRecur;
    private long totalRecur;
    private long curRecur;
    private static long maxRecurLimit = 50;
    //右，左，下，上四个方向
    public static int[][] directs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

    public static void setMaxRecurLimit(long maxRecurLimit) {
        SokobanSolver.maxRecurLimit = maxRecurLimit;
    }

    public SokobanSolver() {
        solveSteps = new Stack<>();
        allPossibleMap = new HashMap<>();
    }

    public boolean solveMapWithTimeCount(SokobanMap sokobanMap) throws SokobanException {
        long start = System.currentTimeMillis();
        SokobanUtil.specialPrintMap(sokobanMap.getMap());
        boolean result = solveMap(sokobanMap);
        long end = System.currentTimeMillis();
        timeUsed = end - start;
        return result;
    }

    public boolean solveMap(SokobanMap sokobanMap) throws SokobanException {
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
        //打印地图
//                SokobanUtil.specialPrintMap(sokobanMap.getMap());
        //        SokobanUtil.printMap(sokobanMap.getMap());
        //首先对地图做转换，将玩家填满所有可能到达的区域
        Queue<int[]> allPossiblePlayer = SokobanUtil.fillPlayerOnAllReachable(sokobanMap.getMap());
        //判断该地图是否之前出现过
        if (!hasMap(sokobanMap.getMap())) {
//                    SokobanUtil.specialPrintMap(sokobanMap.getMap());
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

                        //移动箱子并生成新的地图
                        SokobanMap newMap = SokobanUtil.pushBoxToCreateNewMap(sokobanMap, i, direct);

                        //将操作压栈
                        solveSteps.push(new int[]{boxSite[0], boxSite[1], direct[0], direct[1]});
                        //判断是否已经完成
                        if (isComplete(newMap)) {
                            curRecur -= 1;
                            return true;
                        }
                        //清空场上多余的玩家
                        SokobanUtil.clearAndReputPlayer(newMap.getMap(), allPossiblePlayer);
                        //递归调用函数
                        boolean result = solveMap(newMap);
                        if (result) {
                            curRecur -= 1;
                            return true;
                        } else {
                            //如果递归无果，则说明此路不通，清除该操作，并继续寻找
                            solveSteps.pop();
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

    public void printRoute() {
        Object[] stepList = solveSteps.toArray();
        int stepNum = stepList.length;
        for (int i = 0; i < stepNum; i++) {
            int[] step = (int[]) stepList[i];
            System.out.println(String.format("%s行,%s列  向%s移动", step[0]+1, step[1]+1, getDirectWord(step[2], step[3])));
        }
        System.out.println(String.format("总步数 %d步", stepNum));
    }

    public void printRouteMap() {
        Object[] stepList = solveSteps.toArray();
        int stepNum = stepList.length;
        for (int i = 0; i < stepNum; i++) {
            int[] step = (int[]) stepList[i];
            System.out.println(String.format("%s行,%s列  向%s移动", step[0]+1, step[1]+1, getDirectWord(step[2], step[3])));
        }
        System.out.println(String.format("总步数 %d步", stepNum));
    }

    public Object[] getSolveSteps() {
        return solveSteps.toArray();
    }

    private boolean hasMap(int[][] map) {
        String mapStr = SokobanUtil.mapToString(map);
        if (allPossibleMap.containsKey(mapStr)) {
            return true;
        } else {
            allPossibleMap.put(mapStr, "");
            return false;
        }
    }

    private String getDirectWord(int rowIncr, int colIncr) {
        String word = "无";
        if (rowIncr * colIncr != 0) {
            word = "无";
        } else if (rowIncr == 1) {
            word = "下";
        } else if (rowIncr == -1) {
            word = "上";
        } else if (colIncr == 1) {
            word = "右";
        } else if (colIncr == -1) {
            word = "左";
        }
        return word;
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
