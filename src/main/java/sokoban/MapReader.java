package sokoban;

import model.SokobanException;
import model.SokobanMap;
import org.apache.commons.lang.StringUtils;
import util.SokobanSolver;
import util.SokobanUtil;

/**
 * @author WeiWang Zhang
 * @date 2018/4/19.
 * @time 10:18.
 */
public class MapReader {
//    public static String testMap = " ####\n" + "##  #####\n" + "#  $  $ #\n" + "# $*..* ##\n" + "#  *$$.  #\n" + "#@ *.*.  #\n" + "####   ###\n" + "   #####";
    public static String fillTheHall =  "  #####\n" +
                                        "  #%%%#\n" +
                                        "  #   #\n" +
                                        "### * ###\n" +
                                        "#% *.* %#\n" +
                                        "#% *.* %#\n" +
                                        "#% *.* %#\n" +
                                        "###$$$###\n" +
                                        "  #%^%#\n" +
                                        "  #####";

    public static String connection =   "   ####\n" +
                                        "####%%##\n" +
                                        "#%  $ %#\n" +
                                        "#% *** #\n" +
                                        "#% . . ##\n" +
                                        "##%* * %#\n" +
                                        " ##*** %#\n" +
                                        "  # $ ###\n" +
                                        "  #%^%#\n" +
                                        "  #####";
    public static String puzzle1 =  " #######\n" +
                                    "##%#. .#\n" +
                                    "#% $ . #\n" +
                                    "#%$#.$.#\n" +
                                    "#% $ .%#\n" +
                                    "## #$%##\n" +
                                    "#%$ %##\n" +
                                    "#% %##\n" +
                                    "#^%##\n" +
                                    "####";
//    public static String testMap ="############\n" + "#..  #     ###\n" + "#..  # $  $  #\n" + "#..  #$####  #\n" + "#..    @ ##  #\n" + "#..  # #  $ ##\n" + "###### ##$ $ #\n" + "  # $  $ $ $ #\n" + "  #    #     #\n" + "  ############";
    /**
     * read from symbol map
     * Wall # → 8
     * Player @ → 4
     * Player on goal square + → 5
     * Box $ → 2
     * Box on goal square * → 3
     * Goal square .→ 1
     * Floor (space)→ 0
     * Dead point % → 16
     * Player on dead point ^ → 20
     * @param mString
     * @return
     */

    public static int[][] readFromSymbol(String mString) throws SokobanException {
        if (StringUtils.isEmpty(mString)) {
            return null;
        }
        String[] mapArray = mString.split("\n");
        int mh = mapArray.length;
        int mw = 0;
        for (int i = 0; i < mh; i++) {
            int width = mapArray[i].length();
            mw = width > mw ? width : mw;
        }
        int[][] map = new int[mh][mw];
        for (int i = 0; i < mh; i++) {
            for (int j = 0; j < mapArray[i].length(); j++) {
                switch (mapArray[i].charAt(j)){
                    case '#':
                        SokobanUtil.putBlock(map,i,j);
                        break;
                    case '@':
                        SokobanUtil.putPlayer(map,i,j);
                        break;
                    case '+':
                        SokobanUtil.putPlayer(map,i,j);
                        SokobanUtil.putDestination(map,i,j);
                        break;
                    case '$':
                        SokobanUtil.putBox(map,i,j);
                        break;
                    case '*':
                        SokobanUtil.putBox(map,i,j);
                        SokobanUtil.putDestination(map,i,j);
                        break;
                    case '.':
                        SokobanUtil.putDestination(map,i,j);
                        break;
                    case '%':
                        SokobanUtil.putDeadPoint(map,i,j);
                        break;
                    case '^':
                        SokobanUtil.putDeadPoint(map,i,j);
                        SokobanUtil.putPlayer(map,i,j);
                        break;
                    case ' ':
                        map[i][j] = 0;
                        break;
                    default:
                        throw new SokobanException("Symbol "+mapArray[i].charAt(j)+" not defined");
                }
            }
        }
        return map;
    }


    public static void main(String[] args) {
        try {
//            int[][] map = readFromSymbol(fillTheHall);
            int[][] map = readFromSymbol(puzzle1);
            SokobanUtil.specialPrintMap(map);
            SokobanMap sokobanMap = new SokobanMap(map);
            SokobanSolver solver = new SokobanSolver();
            boolean result = solver.NormalSolver(sokobanMap);
//            SokobanUtil.specialPrintMap(sokobanMap.getMap());
//            //打印移动方式
//            solver.printBoxRoute(solver.getNormalSteps());
//            //打印移动方式and地图
//            solver.printBoxRouteWithMap(sokobanMap,solver.getNormalSteps());
            //最佳求解
            solver.BestSolver(sokobanMap);
            solver.printBoxRouteWithMap(sokobanMap,solver.getBestStepsClone());
        } catch (SokobanException e) {
            e.printStackTrace();
        }
    }
}
