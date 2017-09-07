package utils;

import entity.Drawable;
import entity.SelectorBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
*
* Drawable处理的工具类
* */
public class DrawableUtil  {

    /**
     * 修改原有的图片名字,传入正则表达式，相应的替换字符串
     *
     * @param regex         正则表达式 搜索相应的字符串
     * @param replaceString 相应的替换字符串
     * @param updateAll     是否修改全部，true 表示不考虑drawable选中与否
     */
    public static void updateCurDrawableName(String regex, String replaceString, boolean updateAll, List<Drawable>
            mDrawables) {
        for (Drawable drawable : mDrawables) {
            /*
            * 1.updateAll为true 不考虑drawable选中情况，更新所有可以匹配到的字符串
            * 2.updateAll为false 只有当drawable被选中的情况下才更新
            * */
            if (!drawable.isSelected() && !updateAll) {
                continue;
            }
            String drawableName = drawable.getDrawableName();
            //如果图片名字中包含 正则表达式匹配的字符则替换字符
            if (Util.isStringContains(drawableName, regex)) {
                String drawableCurName = Util.replaceString(drawableName, replaceString, regex);
                drawable.setDrawableCurName(drawableCurName);
            }
        }
    }




    /**
     * @param normalDrawable  正常状态的图片
     * @param pressedDrawable 按下状态的图片
     * @return XML的字符串内容
     *
     * 生成Selector文件的内容
     */
    public static String getSelectorContent(String normalDrawable, String pressedDrawable) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        stringBuilder.append("<selector xmlns:android=\"http://schemas.android.com/apk/res/android\">\n");
        stringBuilder.append("<item android:state_pressed=\"true\" android:drawable=\"@drawable/");
        stringBuilder.append(pressedDrawable + "\"/>\n");
        stringBuilder.append("<item android:state_pressed=\"false\" android:drawable=\"@drawable/");
        stringBuilder.append(normalDrawable + "\"/>\n");
        stringBuilder.append("</selector>");
        return stringBuilder.toString();
    }




    /**
     * @param inputFilePath 文件路径名
     * @return 存储Drawable的List
     *
     * 获得所有以png结尾的所有图片名，存储在List<Drawable>中
     */
    public static List<Drawable> getDrawableList(String inputFilePath) {
        ArrayList<Drawable> drawables = new ArrayList<>();
        File inputFile = new File(inputFilePath);
        File[] childFiles = inputFile.listFiles();
        for (File file : childFiles) {
            //只接受png形式的图片
            if (file.getName().endsWith(".png")) {
                Drawable drawable = new Drawable(file.getName());
                drawables.add(drawable);
            }
        }
        return drawables;
    }



    /**
     * @param drawables 存储全部的drawabls
     * @return 返回选中的drawabls
     *
     */
    public static List<Drawable> getSelectedDrawables(List<Drawable> drawables) {
        ArrayList<Drawable> selectedDrawables = new ArrayList<>();
        for (Drawable drawable : drawables) {
            if (drawable.isSelected()) {
                selectedDrawables.add(drawable);
            }
        }
        return selectedDrawables;

    }


    /**
     * @param drawables 图片信息的List
     * @return 配对的SelectorBeans
     */
    public static List<SelectorBean> getSelectorBeans(List<Drawable> drawables) {
        drawables = getSelectedDrawables(drawables);
        ArrayList<SelectorBean> selectorBeans = new ArrayList<>();
        for (int i = 0;i<drawables.size();++i) {
            //获得第一个Drawable
            Drawable drawable = drawables.get(i);
            String drawableCurName = drawable.getDrawableCurName();
            //获得配对的drawable对应的名字
            String normalString = null;
            String pressedString = null;
            boolean isNormal;

            if (Util.isStringContains(drawableCurName, "_normal")) {
                normalString = drawableCurName;
                pressedString = Util.replaceString(drawableCurName, "_pressed", "_normal");

                isNormal = true;
            } else if (Util.isStringContains(drawableCurName, "_pressed")) {
                normalString = Util.replaceString(drawableCurName, "_normal", "_pressed");
                pressedString = drawableCurName;

                isNormal = false;
            } else {
                continue;
            }
            //遍历判断是否有匹配的字符,若有则添加到List<SelectorBean>中
            for (int j = i+1; j< drawables.size();++j) {
                String matchDrawableString = drawables.get(j).getDrawableCurName();
                if (isNormal) {//当前是"_normal"的字符串
                    if (pressedString.equals(matchDrawableString)) {
                        SelectorBean selectorBean = new SelectorBean();
                        selectorBean.normalDrawable = normalString;
                        selectorBean.pressedDrawable = matchDrawableString;
                        selectorBeans.add(selectorBean);
                        //存在配对的drawable,跳出循环 删除当前已配对的Drawable
                        break;
                    }
                } else { //当前是"_pressed"的字符串
                    if (pressedString.equals(matchDrawableString)) {
                        SelectorBean selectorBean = new SelectorBean();
                        selectorBean.normalDrawable = matchDrawableString;
                        selectorBean.pressedDrawable = pressedString;
                        selectorBeans.add(selectorBean);
                        //存在配对的drawable,跳出循环 删除当前已配对的Drawable
                        break;
                    }
                }
            }

        }
        return selectorBeans;
    }


}
