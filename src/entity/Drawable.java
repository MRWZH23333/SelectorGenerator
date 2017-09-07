package entity;

public class Drawable {
    /*原图片名*/
    private String drawableName;
    /*现图片名*/
    private String drawableCurName;
    /*是否被选中*/
    private boolean isSelected;

    public Drawable(String drawableName) {
        this.drawableName = drawableName;
        this.drawableCurName = drawableName;
    }


    public String getDrawableCurName() {
        return drawableCurName;
    }

    public void setDrawableCurName(String drawableCurName) {
        this.drawableCurName = drawableCurName;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public String getDrawableName() {
        return drawableName;
    }

}

