package sokoban;

import com.excalibur.core.util.json.JsonUtils;
import com.sun.javafx.binding.StringFormatter;
import com.sun.javafx.collections.MapAdapterChange;
import model.SokobanException;
import model.SymbolMap;
import util.MapFileUtil;
import util.SokobanUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WeiWang Zhang
 * @date 2018/4/20.
 * @time 20:47.
 */
public class JsonMapGenerator {

    public static void main(String[] args) {

        int index = 55;
        List<SymbolMap> mapList = MapFileUtil.readMapsFromFile("100Boxes.txt");
//        List<SymbolMap> mapList = MapFileUtil.readMapsFromFile("Puzzle.txt");
        StringBuilder sb = new StringBuilder("");
        List<String> mapJsonList = new ArrayList<>();
        for(SymbolMap map:mapList){
            try {
                int[][] intMap = MapReader.readFromSymbol(map.getContent());
                int[][] stIntMap = new int[14][10];
                SokobanAdapter.fillToLargeMap(intMap,stIntMap);
                String mapJson = SokobanAdapter.turnMapToString(stIntMap);
                String mapKey = SokobanUtil.checkStageForMd5(intMap);
                String sqlInsert = "insert into sokoban_classic_stage set id=%d, stage_name =\'%s\', img_url=\'%s\', stage_json=\'%s\',stage_key=\'%s\', create_time=now(),update_time=now();";
                sqlInsert = String.format(sqlInsert,index,map.getName(), "", mapJson, mapKey);
                sb.append(sqlInsert).append("\n");
                index++;

//                sb.append("'").append(mapJson).append("'").append(",").append("\n");
            } catch (SokobanException e) {
//                System.out.println(String.format("地图 %s 无法匹配大小",map.getName()));
//                e.printStackTrace();
            }
        }
        System.out.println(sb.toString());
    }
}
