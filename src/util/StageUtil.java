package util;


import model.SokobanException;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class StageUtil {


    /**
     * 地图格子  0-目标点  1-箱子  2-人  3-墙壁
     * 0 - 空
     * 1 - 目标点
     * 2 - 箱子
     * 3 - 箱子&目标点
     * 4 - 人
     * 5 - 人&目标点
     * 8 - 墙壁
     * **/


    public static int[][] genStage(String content) throws SokobanException {
        String[] contentArray = content.split("\n");
        int row = contentArray.length;
        int col = contentArray[0].length();
        int[][] stage = new int[row][col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                stage[i][j] = contentArray[i].charAt(j)-'0';
                if(stage[i][j]>8||stage[i][j]<0){
                    throw new SokobanException("value error");
                }
            }
        }
        return stage;
    }


        /**
         * 石头
         * 三角形石头： {type: 'stone', state: 'tri'}
         * 方形石头： {type: 'stone', state: 'rect'}
         * 树桩：{type: 'wood', state: 'normal'}
         * 人：{type: 'girl', state: 'front1'}
         * 费列罗：{type: 'ferrero', state: 'normal'}
         * 目标地点：{type: 'des', state: 'light'}.
         * *
         * 转换后的格式:
         * 地图格子  0-目标点  1-箱子  2-人  3-墙壁
         * 0 - 空  * 1 - 目标点  * 2 - 箱子  * 3 - 箱子&目标点  * 4 - 人  * 5 - 人&目标点  * 8 - 墙壁
         *
         * @return
         */


    private final static int DESTINATION = 0b0001;
    private final static int BOX = 0b0010;
    private final static int PLAYER = 0b0100;
    private final static int BLOCK = 0b1000;
    private final static int DESTINATION_CLEAR = 0b1110;
    private final static int BOX_CLEAR = 0b1101;
    private final static int PLAYER_CLEAR = 0b1011;
    private final static int BLOCK_CLEAR = 0b0111;

    /**
     * 在单元格上放置玩家
     * @param map
     * @param row
     * @param col
     */
    public static void putPlayer(int[][] map, int row, int col) throws SokobanException {
        if((map[row][col] & BOX) > 0 || (map[row][col] & BLOCK) > 0){
            throw new SokobanException(String.format("无法放置玩家 %d行，%d列 已经存在其他物体",row,col));
        }
        map[row][col] = map[row][col]|PLAYER;
    }

    /**
     * 在单元格上放置箱子
     * @param map
     * @param row
     * @param col
     */
    public static void putBox(int[][] map, int row, int col) throws SokobanException {
        if((map[row][col] & PLAYER) > 0 || (map[row][col] & BLOCK) > 0){
            throw new SokobanException(String.format("无法放置箱子 %d行，%d列 已经存在其他物体",row,col));
        }
        map[row][col] = map[row][col]|BOX;
    }

    /**
     * 在单元格上放置障碍物
     * @param map
     * @param row
     * @param col
     */
    public static void putBlock(int[][] map, int row, int col) throws SokobanException {
        if((map[row][col] & PLAYER) > 0 || (map[row][col] & BOX) > 0 ||(map[row][col] & DESTINATION) > 0){
            throw new SokobanException(String.format("无法放置障碍 %d行，%d列 已经存在其他物体",row+1,col+1));
        }
        map[row][col] = map[row][col]|BLOCK;
    }

    /**
     * 在单元格上放置障碍物
     * @param map
     * @param row
     * @param col
     */
    public static void putDestination(int[][] map, int row, int col) throws SokobanException {
        if((map[row][col] & BLOCK) > 0){
            throw new SokobanException(String.format("无法放置目的地 %d行，%d列 已经存在障碍物",row+1,col+1));
        }
        map[row][col] = map[row][col]|DESTINATION;
    }

    public static void printMap(int[][] map){
        System.out.println("--------------*** 关卡图 ***------------------");
        System.out.println("1-目标点  2 - 箱子  3-箱子&目标点  4-人  5-人&目标点  8-墙壁");
        int row = map.length;
        int col = map[0].length;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                System.out.printf(map[i][j]+"  ");
            }
            System.out.printf("\n\r");
        }
    }

    /**
     * 将玩家可达区域用玩家填满，消除玩家位置带来的地图区别
     *
     * @throws SokobanException
     */
    public static Queue<int[]> putPlayerOnAllReachable(int[][] map){
        int row = map.length;
        int col = map[0].length;
        Queue<int[]> playerQueue = new LinkedList<>();
        Queue<int[]> possiblePlaySites = new LinkedList<>();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if ((map[i][j] & PLAYER) > 0) {
                    int[] site = {i,j};
                    playerQueue.offer(site);
                    possiblePlaySites.offer(site);
                }
            }
        }
        while(playerQueue.size()>0){
            int[] site = playerQueue.poll();
            if(putPlayWithBoolResult(map,row,col,site[0],site[1]-1)){
                int[] newSite = {site[0],site[1]-1};
                playerQueue.offer(newSite);
                possiblePlaySites.offer(newSite);
            }
            if(putPlayWithBoolResult(map,row,col,site[0],site[1]+1)){
                int[] newSite = {site[0],site[1]+1};
                playerQueue.offer(newSite);
                possiblePlaySites.offer(newSite);
            }
            if(putPlayWithBoolResult(map,row,col,site[0]-1,site[1])){
                int[] newSite = {site[0]-1,site[1]};
                playerQueue.offer(newSite);
                possiblePlaySites.offer(newSite);
            }
            if(putPlayWithBoolResult(map,row,col,site[0]+1,site[1])){
                int[] newSite = {site[0]+1,site[1]};
                playerQueue.offer(newSite);
                possiblePlaySites.offer(newSite);
            }
        }
        return possiblePlaySites;
    }

    /**
     * 放置玩家，成功放置时返回true
     * @param map
     * @param mapRow
     * @param mapCol
     * @param row
     * @param col
     * @return
     */
    public static boolean putPlayWithBoolResult(int map[][], int mapRow, int mapCol, int row,int col){
        if(col < 0 || col >= mapCol || row < 0 || row >= mapRow){
            return false;
        }else if((map[row][col] & PLAYER)>0){
            return false;
        }else if((map[row][col] & BOX)>0){
            return false;
        }else if((map[row][col] & BLOCK)>0){
            return false;
        }
        map[row][col] = map[row][col] | PLAYER;
        return true;
    }

    /**
     * 将箱子推动一格并生成一张新的克隆地图
     * @param oriMap
     * @param playerRow
     * @param playerCol
     * @param boxRow
     * @param boxCol
     * @return
     */
    public static int[][] pushBoxToCreateNewMap(int[][] oriMap, int playerRow, int playerCol, int boxRow, int boxCol){
        //得到原地图的克隆
        int[][] cloneMap = oriMap.clone();
        int boxNewRow = boxRow + boxRow - playerRow;
        int boxNewCol = boxCol = boxCol - playerCol;
        oriMap[boxRow][boxCol] = (oriMap[boxRow][boxCol]&BOX_CLEAR)|PLAYER;
        oriMap[boxNewRow][boxNewCol] = oriMap[boxNewRow][boxNewCol]|BOX;
        return cloneMap;
    }

    /**
     * 清空列表中所有位置上的玩家
     * @param map
     * @param playerSites
     */
    public static void clearAndReputPlayer(int[][] map, Queue<int[]> playerSites){
        for (int[] site:playerSites) {
            map[site[0]][site[1]] = map[site[0]][site[1]] & PLAYER_CLEAR;
        }
    }

    /**
     * 判断某点箱子是否可达
     * @param map
     * @param mapRow
     * @param mapCol
     * @param row
     * @param col
     * @return
     */
    public static boolean boxReachable(int map[][], int mapRow, int mapCol, int row,int col){
        if(col < 0 || col >= mapCol || row < 0 || row >= mapRow){
            return false;
        }else if((map[row][col] & BOX)>0){
            return false;
        }else if((map[row][col] & BLOCK)>0){
            return false;
        }
        return true;
    }

}
