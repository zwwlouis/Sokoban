package model;

/**
 * @author WeiWang Zhang
 * @date 2018/4/20.
 * @time 17:01.
 */
public class MapJsonElement {
    private String type;
    private String state;
    public MapJsonElement() {
    }
    public MapJsonElement(String type, String state) {
        this.type = type;
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
