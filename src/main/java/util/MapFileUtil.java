package util;

import model.SymbolMap;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author WeiWang Zhang
 * @date 2018/4/20.
 * @time 20:01.
 */
public class MapFileUtil {
    private static String BASE_DIR = "mapfile";

    public static List<SymbolMap> readMapsFromFile(String fileName) {
        URL url = MapFileUtil.class.getClassLoader().getResource(BASE_DIR + "/" + fileName);
        File file = new File(url.getFile());
        List<SymbolMap> stageList = new ArrayList<>();
        try {
            if (!file.exists() || file.isDirectory()) {
                throw new FileNotFoundException();
            }
            FileInputStream fis = new FileInputStream(file);
            //构造一个BufferedReader类来读取文件
            BufferedReader br = new BufferedReader(new FileReader(file));
            String sLine = null;
            //使用readLine方法，一次读一行
            StringBuilder sb = new StringBuilder();
            while ((sLine = br.readLine()) != null) {
                if (!sLine.trim().startsWith(";")) {
                    if (sLine.trim() != "") {
                        sb.append(sLine).append("\n");
                    }
                } else {
                    String name = sLine.substring(sLine.indexOf(";") + 1).trim();
                    stageList.add(new SymbolMap(name, sb.toString()));
                    sb = new StringBuilder();
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stageList;
    }


    public static void main(String[] args) {
        List<SymbolMap> stageList = readMapsFromFile("100Boxes.txt");
        for (SymbolMap symbolMap : stageList) {
            System.out.println("name: " + symbolMap.getName());
            System.out.println(symbolMap.getContent());
            System.out.println("----------------------------");
        }
    }

}
