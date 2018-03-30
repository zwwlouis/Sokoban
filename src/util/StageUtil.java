package util;


import model.SokobanException;

import java.util.Map;
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

    public static void fillReachableWithPlayer(int[][] stage) {
        int row = stage.length;
        int col = stage[0].length;
        //找到人所在地块
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {

            }
        }
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
            throw new SokobanException(String.format("无法放置障碍 %d行，%d列 已经存在其他物体",row,col));
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
            throw new SokobanException(String.format("无法放置目的地 %d行，%d列 已经存在障碍物",row,col));
        }
        map[row][col] = map[row][col]|DESTINATION;
    }

    public static void printMap(int[][] map){
        System.out.println("--------------*** 关卡图 ***------------------");
        int row = map.length;
        int col = map[0].length;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                System.out.printf(map[i][j]+"  ");
            }
            System.out.printf("\n\r");
        }
    }


}
