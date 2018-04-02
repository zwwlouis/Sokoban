package util;

import model.SokobanException;
import model.SokobanMap;

import java.util.*;

/**
 * @author WeiWang Zhang
 * @date 2018/4/2.
 * @time 19:43.
 */
public class SokobanSolver {
    //解题过程 int数组一共四位，0\1:箱子位置，2\3：推动方向
    private Stack<int[]> solveStep;
    private Map<String,String> allPossibleMap;
    private int row;
    private int col;
    //右，左，下，上四个方向
    public static int[][] directs = {{0,1},{0,-1},{1,0},{-1,0}};
    public SokobanSolver(SokobanMap sokobanMap){
        int[][] map = sokobanMap.getMap();
        this.row = map.length;
        this.col = map[0].length;
        solveStep = new Stack<>();
        allPossibleMap = new HashMap<>();
    }

    public boolean solveMap(SokobanMap sokobanMap) throws SokobanException {
        //首先对地图做转换，将玩家填满所有可能到达的区域
        Queue<int[]> allPossiblePlayer = SokobanUtil.fillPlayerOnAllReachable(sokobanMap.getMap());
        //获取所有箱子的位置
        List<int[]> boxList = sokobanMap.getBoxSite();
        for (int i = 0; i < boxList.size(); i++) {
            int[] boxSite = boxList.get(i);
            for (int j = 0; j < directs.length; j++) {
                int[] direct = directs[j];
                //检查该方向上箱子是否可以移动
                if(SokobanUtil.boxReachable(sokobanMap.getMap(),row,col,boxSite,direct)){
                    //移动箱子并生成新的地图
                    SokobanMap newMap = SokobanUtil.pushBoxToCreateNewMap(sokobanMap,i,direct);
                    //判断该地图是否之前出现过
                    if(!hasMap(newMap.getMap())){
                        //将操作压栈
                        solveStep.push(new int[]{boxSite[0],boxSite[1],direct[0],direct[1]});
                        //判断是否已经完成
                        if(isComplete(newMap)){
                            return true;
                        }
                        //清空场上多余的玩家
                        SokobanUtil.clearAndReputPlayer(newMap.getMap(),allPossiblePlayer);
                        SokobanUtil.printMap(newMap.getMap());
                        //递归调用函数
                        boolean result = solveMap(newMap);
                        if(result){
                            return true;
                        }else{
                            //如果递归无果，则说明此路不通，清除该操作，并继续寻找
                            solveStep.pop();
                        }

                    }else{
                        continue;
                    }
                }
            }
        }
        return false;
    }



    private boolean hasMap(int[][] map){
        String mapStr = SokobanUtil.mapToString(map);
        if(allPossibleMap.containsKey(mapStr)){
            return true;
        }else{
            allPossibleMap.put(mapStr,"");
            return false;
        }
    }

    /**
     * 判断关卡是否已经完成
     * @param sokobanMap
     * @return
     */
    private boolean isComplete(SokobanMap sokobanMap){
        List<int[]> boxList = sokobanMap.getBoxSite();
        int[][] map = sokobanMap.getMap();
        for (int i = 0; i < boxList.size(); i++) {
            int[] boxSite = boxList.get(i);
            //是要有一个箱子不在位置上，就不算完成
            if(!SokobanUtil.hasDestination(map[boxSite[0]][boxSite[1]])){
                return false;
            }
        }
        return true;
    }



}
