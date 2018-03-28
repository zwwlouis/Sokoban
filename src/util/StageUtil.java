package util;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

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


    public static int[][] genStage(String content){
        String[] contentArray = content.split("\n");
        int row = contentArray.length;
        int col = contentArray[0].length();
        int[][] stage = new int[row][col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                stage[i][j] = contentArray[i].charAt(j)-'0';
                if(stage[i][j]>8||stage[i][j]<0){
                    throw new ValueException("value error");
                }
            }
        }
        return stage;
    }

    public static void fillReachableWithPlayer(int[][] stage){
        int row = stage.length;
        int col = stage[0].length;
        //找到人所在地块
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {

            }
        }



    }




}
