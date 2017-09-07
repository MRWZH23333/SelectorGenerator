package utils;

import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import entity.Drawable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class Util {

    /**
     * @param drawableFile 图片文件夹
     *                     <p>
     *                     读取文件夹中的图片名字，返回存储Drawable的List。
     */
    public static List<Drawable> getDrawableList(VirtualFile drawableFile) {
        ArrayList<Drawable> drawables = new ArrayList<>();
        VirtualFile[] childFile = drawableFile.getChildren();
        if (childFile.length <= 0) {
            Messages.showMessageDialog("请选择图片文件夹，或当前文件夹为空", "Error", null);
        }
        for (VirtualFile virtualFile : drawableFile.getChildren()) {
            virtualFile.getName();
            String drawableName = virtualFile.getName();
            Drawable drawable = new Drawable(drawableName);
            drawable.setDrawableCurName(drawableName);
            drawables.add(drawable);
        }
        return drawables;
    }




    /**
     * @param inputString   输入的字符串
     * @param replaceString 代替的字符串
     * @param regex         正则表达式
     * @return 代替的字符串 替代掉正则表达式代表的部分之后的字符串
     */
    public static String replaceString(String inputString, String replaceString, String regex) {
        return inputString.replaceAll(regex, replaceString);
    }

    public static boolean isStringContains(String inputString, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputString);
        return matcher.find();
    }


    /**
     * @param inputFilePath  输入的文件路径
     * @param outputFilePath 输出的文件路径
     *
     * @param drawable       图片信息
     * @return  MessageDialog 返回信息
     */
    public static int copyFile(String inputFilePath, String outputFilePath, Drawable drawable) {
        File inputFile = new File(inputFilePath + "\\" + drawable.getDrawableName());
        File outPutFile = new File(outputFilePath + "/" + drawable.getDrawableCurName());
        if (outPutFile.exists()) {
            // TODO: 2017/9/7
            int response = Messages.showYesNoCancelDialog("本地存在同名文件\""+drawable.getDrawableCurName()+"\"是否覆盖",
                    "Error", "Yes", "No",
                    "Cancel",null);
            if (response == Messages.YES) {
                outPutFile.delete();

            } else if (response == Messages.NO) {
                //表示不复制,跳到下一个
                return Messages.NO;
            }else
            {
                //表示不复制，取消所有复制
                return Messages.CANCEL;
            }
        }
        try {
            outPutFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        System.out.println();
        try {
            fileInputStream = new FileInputStream(inputFile);
            fileOutputStream = new FileOutputStream(outPutFile);
            byte[] buffer = new byte[2048];
            int curLength = 0;
            while ((curLength = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, curLength);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //表示成功复制
        return Messages.YES;
    }


    /**
     *将String的内容输出到文件中
     *
     *
     * @param outputPath     输出的路径(包含文件名)
     * @param outputFileName 输出的文件名
     * @param content 文件内容
     * @return Message.Dialog提示信息
     * Yes 表示用户点击Yes，并成功复制当前文件。或 成功复制当前文件
     * NO 表示用户点击NO，当前文件不复制，
     * Cancel 表示用户点击Cancel，当前文件不复制。
     */
    public static  int  outputFile(String outputPath,String outputFileName,String content) {

        byte[] contentBytes = content.getBytes();
        File outputFile = new File(outputPath);
        if (outputFile.exists()) {
            int response = Messages.showYesNoCancelDialog("本地存在同名文件\""+outputFileName+"\"是否覆盖", "Error", "Yes", "No",
                    "Cancel",null);
            if (response == Messages.YES) {
                //确定覆盖,删除原文件
                outputFile.delete();
            } else if (response == Messages.NO) {
                //跳到下一个
                return Messages.NO;
            }else
            {
                return Messages.CANCEL;
            }
            outputFile.delete();
        }
        try {
            outputFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            fileOutputStream.write(contentBytes);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Messages.YES;
    }

}
