package util;


import com.excalibur.core.util.json.JsonUtils;
import model.SokobanException;
import model.SokobanMap;
import sokoban.SokobanAdapter;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Queue;

public class SokobanUtil {


    /**
     * 地图格子  0-目标点  1-箱子  2-人  3-墙壁 4-是否为死亡点
     * 0 - 空
     * 1 - 目标点
     * 2 - 箱子
     * 3 - 箱子&目标点
     * 4 - 人
     * 5 - 人&目标点
     * 8 - 墙壁
     **/
    public final static int DESTINATION = 0b0001;
    public final static int BOX = 0b0010;
    public final static int PLAYER = 0b0100;
    public final static int BLOCK = 0b1000;

    public final static int DESTINATION_CLEAR = 0b11110;
    public final static int BOX_CLEAR = 0b11101;
    public final static int PLAYER_CLEAR = 0b11011;
    public final static int BLOCK_CLEAR = 0b10111;

    public final static int DEAD_POINT = 0b10000;
    public final static int NORMAL_PART_MASK = 0b1111;

    /**
     * 在单元格上放置玩家
     *
     * @param map
     * @param row
     * @param col
     */
    public static void putPlayer(int[][] map, int row, int col) throws SokobanException {
        if ((map[row][col] & BOX) > 0 || (map[row][col] & BLOCK) > 0) {
            throw new SokobanException(String.format("无法放置玩家 %d行，%d列 已经存在其他物体", row, col));
        }
        map[row][col] = map[row][col] | PLAYER;
    }

    /**
     * 在单元格上放置箱子
     *
     * @param map
     * @param row
     * @param col
     */
    public static void putBox(int[][] map, int row, int col) throws SokobanException {
        if ((map[row][col] & PLAYER) > 0 || (map[row][col] & BLOCK) > 0) {
            throw new SokobanException(String.format("无法放置箱子 %d行，%d列 已经存在其他物体", row, col));
        }
        map[row][col] = map[row][col] | BOX;
    }

    /**
     * 在单元格上放置障碍物
     *
     * @param map
     * @param row
     * @param col
     */
    public static void putBlock(int[][] map, int row, int col) throws SokobanException {
        if ((map[row][col] & PLAYER) > 0 || (map[row][col] & BOX) > 0 || (map[row][col] & DESTINATION) > 0) {
            throw new SokobanException(String.format("无法放置障碍 %d行，%d列 已经存在其他物体", row, col));
        }
        map[row][col] = map[row][col] | BLOCK;
    }

    /**
     * 在单元格上放置障碍物
     *
     * @param map
     * @param row
     * @param col
     */
    public static void putDestination(int[][] map, int row, int col) throws SokobanException {
        if ((map[row][col] & BLOCK) > 0) {
            throw new SokobanException(String.format("无法放置目的地 %d行，%d列 已经存在障碍物", row, col));
        }
        map[row][col] = map[row][col] | DESTINATION;
    }

    public static void putDeadPoint(int[][] map, int row, int col) throws SokobanException {
        map[row][col] = map[row][col] | DEAD_POINT;
    }

    public static void specialPrintMap(int[][] map) {
        System.out.println("--------------*** 关卡图 ***------------------");
        int row = map.length;
        int col = map[0].length;
        System.out.printf("*  ");
        for (int i = 0; i < col; i++) {
            StringBuilder sb = new StringBuilder((i)+"");
            while(sb.length()<3){
                sb.append(" ");
            }
            System.out.printf(sb.toString());
        }
        System.out.printf("\n");
        for (int i = 0; i < row; i++) {
            StringBuilder sb = new StringBuilder((i)+"");
            while(sb.length()<3){
                sb.append(" ");
            }
            System.out.printf(sb.toString());
            for (int j = 0; j < col; j++) {
                String charac;
//                int unit = map[i][j] & NORMAL_PART_MASK;
                int unit = map[i][j];
                switch (unit) {
                    case 0:
                        charac = "   ";
                        break;
                    case 1:
                        charac = "× ";
                        break;
                    case 2:
                        charac = "□ ";
                        break;
                    case 3:
                        charac = "☒ ";
                        break;
                    case 4:
                        charac = "○ ";
                        break;
                    case 5:
                        charac = "ⓧ ";
                        break;
                    case 8:
                        charac = "█ ";
                        break;
                    case 16:
                        charac = "%%  ";
                        break;
                    case 20:
                        charac = "^  ";
                        break;
                    default:
                        charac = "   ";
                        break;
                }
                System.out.printf(charac);
            }
            System.out.printf("\n\r");
        }
    }

    public static void printMap(int[][] map) {
        System.out.println("--------------*** 关卡图 ***------------------");
        System.out.println("1-目标点  2 - 箱子  3-箱子&目标点  4-人  5-人&目标点  8-墙壁");
        int row = map.length;
        int col = map[0].length;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                System.out.printf((map[i][j]&NORMAL_PART_MASK )+ "  ");
            }
            System.out.printf("\n\r");
        }
    }

    /**
     * 根据元素数量基础性验证地图有效性
     *
     * @param map
     * @throws SokobanException
     */
    public static void baseValidateMap(int[][] map) throws SokobanException {
        int row = map.length;
        int col = map[0].length;
        int player = 0, block = 0, destination = 0, box = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                player += (map[i][j] & PLAYER) >> 2;
                block += (map[i][j] & BLOCK) >> 3;
                destination += map[i][j] & DESTINATION;
                box += (map[i][j] & BOX) >> 1;
            }
        }
        if (player != 1) {
            throw new SokobanException("玩家数量不正确");
        }
        if (box != destination) {
            throw new SokobanException(String.format("箱子:%d 目的地:%d 数量不符", box, destination));
        }
    }

    //右，左，下，上四个方向
    public static int[][] directs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

    /**
     * 将玩家可达区域用玩家填满，消除玩家位置带来的地图区别
     *
     * @throws SokobanException
     */
    public static Queue<int[]> fillPlayerOnAllReachable(int[][] map) {
        int row = map.length;
        int col = map[0].length;
        Queue<int[]> playerQueue = new LinkedList<>();
        Queue<int[]> allPossibleQueue = new LinkedList<>();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if ((map[i][j] & PLAYER) > 0) {
                    int[] site = {i, j};
                    playerQueue.offer(site);
                    allPossibleQueue.offer(site);
                }
            }
        }
        while (playerQueue.size() > 0) {
            int[] site = playerQueue.poll();
            for (int i = 0; i < directs.length; i++) {
                if (putPlayWithBoolResult(map, row, col, site[0] + directs[i][0], site[1] + directs[i][1])) {
                    int[] newSite = {site[0] + directs[i][0], site[1] + directs[i][1]};
                    playerQueue.offer(newSite);
                    allPossibleQueue.offer(newSite);
                }
            }
        }
        return allPossibleQueue;
    }

    /**
     * 放置玩家，成功放置时返回true
     *
     * @param map
     * @param mapRow
     * @param mapCol
     * @param row
     * @param col
     * @return
     */
    public static boolean putPlayWithBoolResult(int map[][], int mapRow, int mapCol, int row, int col) {
        if (col < 0 || col >= mapCol || row < 0 || row >= mapRow) {
            return false;
        } else if ((map[row][col] & PLAYER) > 0) {
            return false;
        } else if ((map[row][col] & BOX) > 0) {
            return false;
        } else if ((map[row][col] & BLOCK) > 0) {
            return false;
        }
        map[row][col] = map[row][col] | PLAYER;
        return true;
    }

    /**
     * 将箱子推动一格
     *
     * @param sokobanMap
     * @param boxIndex
     * @param direct
     * @return
     */
    public static void pushBoxForOneCell(SokobanMap sokobanMap, int boxIndex, int[] direct) throws SokobanException {

        int[] boxSite = sokobanMap.getBoxSites().get(boxIndex);
        int[][] map = sokobanMap.getMap();
        //设置玩家位置，为所推箱子位置
        sokobanMap.setPlayerSite(boxSite[0], boxSite[1]);
        //从原箱子位置上删除箱子，增加玩家
        map[boxSite[0]][boxSite[1]] = (map[boxSite[0]][boxSite[1]] & BOX_CLEAR) | PLAYER;
        //修改箱子的位置
        boxSite[0] += direct[0];
        boxSite[1] += direct[1];
        //在新箱子位置上增加箱子
        map[boxSite[0]][boxSite[1]] = map[boxSite[0]][boxSite[1]] | BOX;
    }

    /**
     * 移动玩家到指定位置
     * @param sokobanMap
     * @param site 目标点
     * @param lastDirect
     * @return
     */
    public static boolean movePlayerToCell(SokobanMap sokobanMap, int[] site, int[] lastDirect) throws SokobanException {
        int[][] map = sokobanMap.getMap();
        int row = sokobanMap.getRow();
        int col = sokobanMap.getCol();
        if(site[0] < 0 || site[0] >= row || site[1] < 0 || site[1] >= col){
            throw new SokobanException("移动玩家失败，超出地图范围");
        }
        if(hasBlock(map[site[0]][site[1]])){
            throw new SokobanException(String.format("无法移动玩家 %d行，%d列 存在障碍物", site[0], site[1]));
        }
        //如果新位置上存在箱子，需要推动箱子
        if(hasBox(map[site[0]][site[1]])){
            map[site[0]][site[1]] &= SokobanUtil.BOX_CLEAR;
            int newRow = site[0]+lastDirect[0];
            int newCol = site[1]+lastDirect[1];
            map[newRow][newCol] |= SokobanUtil.BOX;
        }
        //删除原来位置的玩家，如果存在
        int[] playerSite = sokobanMap.getPlayerSite();
        if(playerSite != null) {
            map[playerSite[0]][playerSite[1]] = map[playerSite[0]][playerSite[1]] & SokobanUtil.PLAYER_CLEAR;
        }
        //在新的位置增加玩家，并记录坐标
        map[site[0]][site[1]] |= SokobanUtil.PLAYER;
        sokobanMap.setPlayerSite(site);
        return true;
    }


    /**
     * 清空列表中所有位置上的玩家
     *
     * @param map
     * @param playerSites
     */
    public static void clearPlayerAtSites(int[][] map, Queue<int[]> playerSites) {
        for (int[] site : playerSites) {
            map[site[0]][site[1]] = map[site[0]][site[1]] & PLAYER_CLEAR;
        }
    }

    /**
     *  判断某点箱子是否可达
     * @param sokobanMap
     * @param boxSite
     * @param direct
     * @return
     */
    public static boolean boxReachable(SokobanMap sokobanMap, int[] boxSite, int[] direct) {
        int[][] map = sokobanMap.getMap();
        int mapRow = sokobanMap.getRow();
        int mapCol = sokobanMap.getCol();
        int nextRow = boxSite[0] + direct[0];
        int nextCol = boxSite[1] + direct[1];
        if (nextCol < 0 || nextCol >= mapCol || nextRow < 0 || nextRow >= mapRow) {
            return false;
        } else if ((map[nextRow][nextCol] & BOX) > 0) {
            return false;
        } else if ((map[nextRow][nextCol] & BLOCK) > 0) {
            return false;
        }
        //判断移动反方向是否有玩家
        int[] playerSite = {boxSite[0] - direct[0], boxSite[1] - direct[1]};
        if (playerSite[0] < 0 || playerSite[0] >= mapRow || playerSite[1] < 0 || playerSite[1] >= mapCol) {
            return false;
        } else if (!hasPlayer(map[playerSite[0]][playerSite[1]])) {
            return false;
        }
        //打印地图
//        SokobanUtil.specialPrintMap(sokobanMap.getMap());
        //判断将要移动到的位置是否有死点——会造成箱子卡住
        if(hasDeadPoint(map[nextRow][nextCol])){
            return false;
        }
//        if(isBoxStucked(sokobanMap,new int[]{nextRow,nextCol})){
//            return false;
//        }
        return true;
    }

    /**
     * 检查箱子是否卡住（产生明显的无解型）
     *
     * @param sokobanMap
     * @param boxSite
     * @return
     */
    public static boolean isBoxStucked(SokobanMap sokobanMap, int[] boxSite) {
        int[][] map = sokobanMap.getMap();
        if (hasDestination(map[boxSite[0]][boxSite[1]])) {
            return false;
        }
        int mapRow = sokobanMap.getRow();
        int mapCol = sokobanMap.getCol();
        int[] directDect = new int[4];
        int blockNum = 0;
        for (int i = 0; i < directs.length; i++) {
            int newSiteRow = boxSite[0] + directs[i][0];
            int newSiteCol = boxSite[1] + directs[i][1];
            if (newSiteRow < 0 || newSiteRow >= mapCol || newSiteCol < 0 || newSiteCol >= mapRow) {
                directDect[i] = 1;
                blockNum++;
            } else if(hasBlock(map[newSiteRow][newSiteCol])){
                directDect[i] = 1;
                blockNum++;
            }
        }
        if (blockNum > 2) {
            //如果三个方向都有墙则肯定被卡住
            return true;
        } else if (blockNum == 2 && (directDect[0] * directDect[1]) == 0 && (directDect[2] * directDect[3]) == 0) {
            //如果有墙的两个方向相邻，则被卡住
            return true;
        } else if (blockNum == 1) {
            return false;
        }
        return false;
    }

    /**
     * 检查地图边缘上目标点和箱子数目是否相同，如果不同则将无法走通
     * FIXME 不够严谨，有例外
     * @param sokobanMap
     * @param boxSite
     * @param direct
     * @return
     */
    public static boolean ifBoxAndDesEqualOnEdge(SokobanMap sokobanMap, int[] boxSite, int[] direct) {
        boolean forword = true;
        boolean backword = true;
        int[][] map = sokobanMap.getMap();
        int boxNum = 0, desNum = 0;
        int i = 0;
        boxNum += 1;
        desNum += map[boxSite[0]][boxSite[1]] & DESTINATION;
        while (forword) {
            i++;
            int[] newSite = {boxSite[0] + i * direct[0], boxSite[0] + i * direct[0]};
            if (!isSiteOnMap(sokobanMap, newSite) || hasBlock(map[newSite[0]][newSite[1]])) {
                forword = false;
                continue;
            } else {
                boxNum += (map[newSite[0]][newSite[1]] & BOX) >> 1;
                desNum += map[newSite[0]][newSite[1]] & DESTINATION;
            }
        }
        while (backword) {
            i++;
            int[] newSite = {boxSite[0] - i * direct[0], boxSite[0] - i * direct[0]};
            if (!isSiteOnMap(sokobanMap, newSite) || hasBlock(map[newSite[0]][newSite[1]])) {
                backword = false;
                continue;
            } else {
                boxNum += (map[newSite[0]][newSite[1]] & BOX) >> 1;
                desNum += map[newSite[0]][newSite[1]] & DESTINATION;
            }
        }
        return boxNum == desNum;
    }

    /**
     * 判断目标位置是否在地图上
     *
     * @param sokobanMap
     * @param site
     * @return
     */
    public static boolean isSiteOnMap(SokobanMap sokobanMap, int[] site) {
        if (site[0] < 0 || site[0] >= sokobanMap.getRow() || site[1] < 0 || site[1] >= sokobanMap.getCol()) {
            return false;
        }
        return true;
    }


    public static String mapToString(int[][] map) {
        int row = map.length;
        int col = map[0].length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                sb.append(map[i][j]);
            }
        }
        return sb.toString();
    }
    public static String getDirectCnWord(int[] direct){
        return getDirectCnWord(direct[0],direct[1]);
    }
    public static String getDirectCnWord( int rowIncr,int colIncr){
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

    public static String getDirectEnWord(int[] direct){
        int rowIncr = direct[0];
        int colIncr = direct[1];
        String word = "";
        if (rowIncr * colIncr != 0) {
            word = "";
        } else if (rowIncr == 1) {
            word = "d";
        } else if (rowIncr == -1) {
            word = "u";
        } else if (colIncr == 1) {
            word = "r";
        } else if (colIncr == -1) {
            word = "l";
        }
        return word;
    }


    public static boolean hasPlayer(int element) {
        return (element & PLAYER) > 0;
    }

    public static boolean hasBox(int element) {
        return (element & BOX) > 0;
    }

    public static boolean hasDestination(int element) {
        return (element & DESTINATION) > 0;
    }

    public static boolean hasBlock(int element) {
        return (element & BLOCK) > 0;
    }
    public static boolean hasDeadPoint(int element) {
        return (element & DEAD_POINT) > 0;
    }

    /**
     * 检查推箱子关卡的合法性并生成对应的md5值
     */
    public static String checkStageForMd5( int[][] map) throws SokobanException {
//        SokobanUtil.printMap(map);
        SokobanUtil.fillPlayerOnAllReachable(map);
//        SokobanUtil.printMap(map);
        String key = "";
        try {
            key =  Md5Util.EncoderByMd5(JsonUtils.objectToJson(map));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return key;
    }

}
