package model;

import util.SokobanUtil;

import java.util.ArrayList;
import java.util.List;

public class SokobanMap implements Cloneable{
    private int[][] map;
    private int[] playerSite;
    private List<int[]> boxSites;
    private int row;
    private int col;
    private static long cloneTime;
    public SokobanMap(){
        boxSites = new ArrayList<>();
    }
    public SokobanMap(int[][] map){
        boxSites = new ArrayList<>();
        setMap(map);
        checkMap(map);
    }

    public int[][] getMap() {
        return map;
    }

    public void setMap(int[][] map) {
        this.map = map;
    }
    public void checkMap(int[][] map){
        this.row = map.length;
        this.col = map[0].length;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if((map[i][j]& SokobanUtil.PLAYER) > 0){
                    playerSite = new int[]{i,j};
                }
                if((map[i][j]& SokobanUtil.BOX) > 0){
                    boxSites.add(new int[]{i,j});
                }
            }
        }
    }

    @Override
    public SokobanMap clone() throws CloneNotSupportedException {
        long start = System.currentTimeMillis();
        SokobanMap sokobanMap = new SokobanMap();
        List<int[]> newBoxSites = new ArrayList<>();
        for (int[] boxSite:this.boxSites) {
            newBoxSites.add(boxSite.clone());
        }
        sokobanMap.setRow(row);
        sokobanMap.setCol(col);
        sokobanMap.setBoxSites(newBoxSites);
        sokobanMap.setPlayerSite(this.playerSite.clone());
        int[][] newMap = new int[row][];
        for (int i = 0; i < row; i++) {
            newMap[i] = this.map[i].clone();
        }
        sokobanMap.setMap(newMap);
        cloneTime += System.currentTimeMillis() - start;
        return sokobanMap;
    }

    public int[] getPlayerSite() {
        return playerSite;
    }

    public void setPlayerSite(int[] playerSite) {
        this.playerSite = playerSite;
    }

    public void setPlayerSite(int row, int col) {
        this.playerSite[0] = row;
        this.playerSite[1] = col;
    }

    public List<int[]> getBoxSites() {
        return boxSites;
    }

    public void setBoxSites(List<int[]> boxSites) {
        this.boxSites = boxSites;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
    public static long getCloneTime(){
        return cloneTime;
    }
}
