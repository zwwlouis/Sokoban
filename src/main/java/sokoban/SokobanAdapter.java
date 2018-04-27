package sokoban;

import com.excalibur.core.util.json.JsonUtils;
import com.sun.javafx.binding.StringFormatter;
import model.MapJsonElement;
import model.SokobanException;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.SokobanUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author WeiWang Zhang
 * @date 2018/4/2.
 * @time 11:02.
 */
public class SokobanAdapter {
    private final static MapJsonElement element_stone = new MapJsonElement("stone", "rect");
    private final static MapJsonElement element_girl = new MapJsonElement("girl", "front1");
    private final static MapJsonElement element_des = new MapJsonElement("des", "normal");
    private final static MapJsonElement element_ferrero = new MapJsonElement("ferrero", "normal");


    public static Object checkSokobanMapJson(String content) throws SokobanException {
        int[][] map = readMap(content);
        SokobanUtil.printMap(map);
        SokobanUtil.baseValidateMap(map);
        return map;
    }

    /**
     * 将小地图 居中填入一张大地图
     *
     * @param small
     * @param large
     */
    public static void fillToLargeMap(int[][] small, int[][] large) throws SokobanException {
        int sh = small.length;
        int sw = small[0].length;
        int lh = large.length;
        int lw = large[0].length;
        if (sh > lh || sw > lw) {
            throw new SokobanException("地图尺寸错误！");
        }
        int hOffset = (lh - sh) / 2;
        int wOffset = (lw - sw) / 2;
        for (int i = 0; i < sh; i++) {
            for (int j = 0; j < sw; j++) {
                large[i + hOffset][j + wOffset] = small[i][j];
            }
        }
    }

    public static int[][] readMap(String content) throws SokobanException {
        int[][] map;
        try {
            JSONArray rows = new JSONArray(content);
            int rowNum = rows.length();
            if (rowNum < 1) {
                throw new SokobanException("地图尺寸错误！");
            }
            int colNum = rows.getJSONArray(0).length();
            if (colNum < 1) {
                throw new SokobanException("地图尺寸错误！");
            }
            map = new int[rowNum][colNum];
            for (int i = 0; i < rowNum; i++) {
                JSONArray row = rows.getJSONArray(i);
                for (int j = 0; j < colNum; j++) {
                    JSONArray unit = row.getJSONArray(j);
                    parseJsonUnitToNum(unit, map, i, j);
                }
            }
            //检查地图的基础合法性
            SokobanUtil.baseValidateMap(map);
        } catch (JSONException exp) {
            throw new SokobanException("地图格式错误！");
        }
        return map;
    }


    /**
     * 将int类型的map转成前端显示的json对象
     *
     * @param map
     * @return
     */
    public static String turnMapToString(int[][] map) {
        int row = map.length;
        int col = map[0].length;
        List<Object> mapObj = new ArrayList<>();
        for (int i = 0; i < row; i++) {
            List<Object> mapRowObj = new ArrayList<>();
            for (int j = 0; j < col; j++) {
                mapRowObj.add(parseNumUnitToMap(map[i][j]));
            }
            mapObj.add(mapRowObj);
        }
        return JsonUtils.objectToJson(mapObj);
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
    public static void parseJsonUnitToNum(JSONArray unit, int[][] map, int row, int col) throws SokobanException {
        for (int i = 0; i < unit.length(); i++) {
            try {
                JSONObject element = (JSONObject) unit.get(i);
                Map<String, Object> elementMap = element.toMap();
                String type = (String) elementMap.get("type");
                if (StringUtils.isEmpty(type)) {
                    return;
                }
                switch (type) {
                    case "stone":
                        SokobanUtil.putBlock(map, row, col);
                        break;
                    case "wood":
                        SokobanUtil.putBlock(map, row, col);
                        break;
                    case "girl":
                        SokobanUtil.putPlayer(map, row, col);
                        break;
                    case "ferrero":
                        SokobanUtil.putBox(map, row, col);
                        break;
                    case "des":
                        SokobanUtil.putDestination(map, row, col);
                        break;
                    default:
                        throw new SokobanException(type + " 标签未定义");
                }
            } catch (ClassCastException exp) {
                throw new SokobanException("地图格式有误！");
            }
        }
    }


    /**
     * 将数字地图块转成json
     *
     * @return
     */
    public static List<MapJsonElement> parseNumUnitToMap(int eleInt) {
        List<MapJsonElement> elements = new ArrayList<>();
        if (SokobanUtil.hasBlock(eleInt)) {
            elements.add(element_stone);
        }
        if (SokobanUtil.hasDestination(eleInt)) {
            elements.add(element_des);
        }
        if (SokobanUtil.hasPlayer(eleInt)) {
            elements.add(element_girl);
        }
        if (SokobanUtil.hasBox(eleInt)) {
            elements.add(element_ferrero);
        }
        return elements;
    }
}
