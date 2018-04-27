package model;

/**
 * @author WeiWang Zhang
 * @date 2018/4/20.
 * @time 20:35.
 */
public class SymbolMap {
    String name;
    /**
     * 以符号表示的地图文字
     * Wall # → 8
     * Player @ → 4
     * Player on goal square + → 5
     * Box $ → 2
     * Box on goal square * → 3
     * Goal square .→ 1
     * Floor (space)→ 0
     */
    String content;
    public SymbolMap() {
    }
    public SymbolMap(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
