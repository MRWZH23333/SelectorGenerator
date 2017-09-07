package view;

import entity.Drawable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;


/*
*
* 内容区域的控件
*
*
* */

public class DrawableViewBean extends JPanel {

    private JCheckBox mEnableCheckBox = new JCheckBox();
    private JLabel mSrcDrawableNameLabel = new JLabel();
    private JTextField mDstDrawableNameTextField = new JTextField();

    private EnableCheckListener mEnableCheckListener;

    private DstDrawableFocusedListener mDstDrawableFocusedListener;

    private Drawable mDrawable;

    public DrawableViewBean(LayoutManager layout, EmptyBorder emptyBorder, Drawable drawable) {
        super(layout);
        mDrawable = drawable;
        initLayout(layout, emptyBorder);
        initComponent();
        addComponent();
    }


    /**
     * 初始化组件
     */
    private void initComponent() {
        mEnableCheckBox.setHorizontalAlignment(JLabel.CENTER);
        mSrcDrawableNameLabel.setHorizontalAlignment(JLabel.CENTER);
        mDstDrawableNameTextField.setHorizontalAlignment(JLabel.CENTER);

        //设置控件的初始状态
        mEnableCheckBox.setSelected(mDrawable.isSelected());
        mDstDrawableNameTextField.setEnabled(mDrawable.isSelected());
        //设置文字
        mSrcDrawableNameLabel.setText(mDrawable.getDrawableName());
        mDstDrawableNameTextField.setText(mDrawable.getDrawableCurName());

        mEnableCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mEnableCheckListener != null) {
                    mEnableCheckListener.onCheckBoxClick(mEnableCheckBox);
                }
            }
        });

        mDstDrawableNameTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (mDstDrawableFocusedListener != null) {
                    mDstDrawableFocusedListener.onDstDrawableFocusGained(mDstDrawableNameTextField);
                }

            }
            @Override
            public void focusLost(FocusEvent e) {
                if (mDstDrawableFocusedListener != null) {
                    mDstDrawableFocusedListener.onDstDrawableFocusLost(mDstDrawableNameTextField);
                }
            }
        });

    }


    /**
     * 添加组件
     */
    private void addComponent() {
        this.add(mEnableCheckBox);
        this.add(mSrcDrawableNameLabel);
        this.add(mDstDrawableNameTextField);
    }

    /**
     * @param layout 初始化布局
     */
    private void initLayout(LayoutManager layout, EmptyBorder emptyBorder) {
        this.setLayout(layout);
        this.setBorder(emptyBorder);
    }


    /**
     * 外界实现，EnableCheckBox点击时回调
     */
    public interface EnableCheckListener {
        void onCheckBoxClick(JCheckBox checkBox);
    }

    /**
     * 外界实现，JTextField焦点改变时回调
     */
    public interface DstDrawableFocusedListener {
        void onDstDrawableFocusGained(JTextField jTextField);
        void onDstDrawableFocusLost(JTextField jTextField);
    }

    /*
    * 一些set，get方法
    * */
    public EnableCheckListener getEnableCheckListener() {
        return mEnableCheckListener;
    }

    public void setEnableCheckListener(EnableCheckListener enableCheckListener) {
        mEnableCheckListener = enableCheckListener;
    }

    public DstDrawableFocusedListener getDstDrawableFocusedListener() {
        return mDstDrawableFocusedListener;
    }

    public void setDstDrawableFocusedListener(DstDrawableFocusedListener dstDrawableFocusedListener) {
        mDstDrawableFocusedListener = dstDrawableFocusedListener;
    }

}
