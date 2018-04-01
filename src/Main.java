import util.StageUtil;

public class Main {
    private static String stage =   "8888888\n" +
                                    "8103018\n" +
                                    "8002008\n" +
                                    "8320238\n" +
                                    "8012108\n" +
                                    "8403008\n" +
                                    "8888888";
    public static void main(String[] args) {
        System.out.println("Hello World!");
        int[][] map = readMapFromString(stage);
        StageUtil.printMap(map);
        StageUtil.putPlayerOnAllReachable(map);
        StageUtil.printMap(map);
        System.out.println();

    }

    public static int[][] readMapFromString(String mapStr){
        String[] rows = mapStr.split("\n");
        int rowNum = rows.length;
        int colNum = rows[0].length();
        int[][] map = new int[rowNum][colNum];
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                map[i][j] = rows[i].charAt(j)-'0';
            }
        }
        return map;
    }




}
