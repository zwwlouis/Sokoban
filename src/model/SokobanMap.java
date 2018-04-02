package model;

import util.SokobanUtil;

import java.util.ArrayList;
import java.util.List;

public class SokobanMap implements Cloneable{
    private int[][] map;
    private int[] playerSite;
    private List<int[]> boxSite;
    public SokobanMap(){

    }
    public SokobanMap(int[][] map){
        boxSite = new ArrayList<>();
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
        int row = map.length;
        int col = map[0].length;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if((map[i][j]& SokobanUtil.PLAYER) > 0){
                    playerSite = new int[]{i,j};
                }
                if((map[i][j]& SokobanUtil.BOX) > 0){
                    boxSite.add(new int[]{i,j});
                }
            }
        }
    }

    @Override
    public SokobanMap clone() throws CloneNotSupportedException {
        SokobanMap sokobanMap = new SokobanMap();
        sokobanMap.setBoxSite(new ArrayList<>(this.boxSite));
        sokobanMap.setPlayerSite(this.playerSite.clone());
        sokobanMap.setMap(this.map.clone());
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

    public List<int[]> getBoxSite() {
        return boxSite;
    }

    public void setBoxSite(List<int[]> boxSite) {
        this.boxSite = boxSite;
    }
}
