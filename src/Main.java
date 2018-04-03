import model.SokobanException;
import model.SokobanMap;
import util.SokobanSolver;
import util.SokobanUtil;

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

        //此处需要保证得到map数组是真实有效的
        int[][] map = readMapFromString(stage);
//        SokobanUtil.specialPrintMap(map);
        SokobanMap sokobanMap = new SokobanMap(map);
        SokobanSolver solver = new SokobanSolver();
        try {
            boolean result = solver.solveMapWithTimeCount(sokobanMap);
            if(result){
                System.out.println("solve complete!");
                System.out.println(solver.getSolveStatus());
                solver.printRoute();
            }
        } catch (SokobanException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
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




}
