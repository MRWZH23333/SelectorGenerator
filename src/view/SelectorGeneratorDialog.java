package view;

import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBScrollPane;
import entity.Drawable;
import entity.SelectorBean;
import utils.DrawableUtil;
import utils.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SelectorGeneratorDialog extends JFrame implements ActionListener {

    private String TAG = getClass().getSimpleName();
    //输入，输出路径
    private String mInputFilePath;
    private String mOutPutFilePath;
    //DrawablePath
    private String mDrawablePath;

    //Dialog属性
    private GridBagLayout mLayout = new GridBagLayout();
    private GridBagConstraints mConstraints = new GridBagConstraints();


    //标签JPanel
    private JPanel mTitlePanel = new JPanel();
    private JCheckBox mTitleCheckAll = new JCheckBox("全选");
    private JLabel mTitleSrcDrawName = new JLabel("原图片名");
    private JLabel mTitleDstDrawName = new JLabel("现图片名");

    //内容JPanel
    private JPanel mContentPanel = new JPanel();
    private JBScrollPane mJScrollPane = new JBScrollPane(mContentPanel);
    private GridBagLayout mContentLayout = new GridBagLayout();
    private GridBagConstraints mContentConstraints = new GridBagConstraints();

    //正则表达式JPanel
    private JPanel mRegexMenuPanel = new JPanel();
    private GridBagLayout mRegexMenuGridBagLayout = new GridBagLayout();
    private GridBagConstraints mRegexMenConstraints = new GridBagConstraints();
    private JCheckBox mEnableRegexCheckBox = new JCheckBox("查找字符(正则表达式匹配)");
    private JTextField mSrcStringTextField = new JTextField();
    private JLabel mReplaceTipLabel = new JLabel("替换为");
    private JTextField mReplaceStringTextField = new JTextField();
    private JButton mConfirmRegexButton = new JButton("确定");

    //确定JPanel
    private JPanel mConfirmPanel = new JPanel();
    private GridBagLayout mConfirmGridBagLayout = new GridBagLayout();
    private GridBagConstraints mConfirmConstraints = new GridBagConstraints();
    private JLabel mPrefixLabel = new JLabel("前缀");
    private JTextField mPrefixTextField = new JTextField();
    private JButton mAddPrefixButton = new JButton("添加前缀");
    private JButton mGenerateSelector = new JButton("生成Selector");


    /**
     * 存储Drawable的List。
     */
    private List<Drawable> mDrawables;


    public SelectorGeneratorDialog(List<Drawable> drawables, String inputFilePath, String outPutFilePath, String
            drawablePath) {
        mDrawables = drawables;
        mInputFilePath = inputFilePath;
        mOutPutFilePath = outPutFilePath;
        mDrawablePath = drawablePath;
        //初始化标题栏
        initTitle();
        //默认先进行一次正则表达式的替换
        DrawableUtil.updateCurDrawableName("Normal@2x", "_normal", true,mDrawables);
        DrawableUtil.updateCurDrawableName("Prsd@2x", "_pressed", true,mDrawables);
        DrawableUtil.updateCurDrawableName("_press", "_pressed", true,mDrawables);
        //初始化内容栏
        initContent();
        //初始化正则表达式搜索栏
        initRegexMenu();
        //初始化确认栏
        initConfirmMenu();
        initEvent();
        setConstraints();
        setDialog();
    }

    /**
     * 添加标题栏
     */
    private void initTitle() {
        mTitlePanel.setLayout(new GridLayout(1, 3, 10, 10));
        mTitlePanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        mTitleCheckAll.setHorizontalAlignment(JCheckBox.CENTER);
        mTitleSrcDrawName.setHorizontalAlignment(JLabel.CENTER);
        mTitleDstDrawName.setHorizontalAlignment(JLabel.CENTER);
        //添加到JPanel中
        mTitlePanel.add(mTitleCheckAll);
        mTitlePanel.add(mTitleSrcDrawName);
        mTitlePanel.add(mTitleDstDrawName);

        //默认全选
        mTitleCheckAll.setSelected(true);
        for (Drawable drawable : mDrawables) {
            drawable.setSelected(mTitleCheckAll.isSelected());
        }

        //判断TitleCheckAll是否需要勾上

        mTitlePanel.setSize(1080, 30);

        getContentPane().add(mTitlePanel, 0);

    }

    /**
     * 更新Title
     */
    private void updateTitle(){
        for (Drawable drawable : mDrawables) {
            if (!drawable.isSelected()) {
                mTitleCheckAll.setSelected(false);
                break;
            }
            mTitleCheckAll.setSelected(true);
        }
        mTitlePanel.revalidate();
    }


    /**
     * 初始化内容区域
     */
    private void initContent() {
        if (mDrawables == null) {
            throw new IllegalStateException("需要传入一个DrawableList");
        }
        mContentPanel.removeAll();
        for (int i = 0; i < mDrawables.size(); ++i) {
            Drawable drawable = mDrawables.get(i);
            // TODO: 2017/9/5
            DrawableViewBean itemPanel = new DrawableViewBean(new GridLayout(1, 3, 10, 10), new EmptyBorder(5, 10, 5,
                    10), drawable);
            //设置每个Item DrawableViewBean的监听事件
            itemPanel.setEnableCheckListener(new DrawableViewBean.EnableCheckListener() {
                @Override
                public void onCheckBoxClick(JCheckBox checkBox) {
                    drawable.setSelected(checkBox.isSelected());
                    updateTitle();
                    updateContent();
                }
            });
            itemPanel.setDstDrawableFocusedListener(new DrawableViewBean.DstDrawableFocusedListener() {
                @Override
                public void onDstDrawableFocusGained(JTextField jTextField) {
                    drawable.setDrawableCurName(jTextField.getText());
                    System.out.println(drawable.getDrawableCurName());
                }

                @Override
                public void onDstDrawableFocusLost(JTextField jTextField) {
                    drawable.setDrawableCurName(jTextField.getText());
                    updateContent();
                    System.out.println(drawable.getDrawableCurName());

                }
            });
            //添加内容
            mContentPanel.add(itemPanel);
            mContentConstraints.fill = GridBagConstraints.HORIZONTAL;
            mContentConstraints.gridwidth = 0;
            mContentConstraints.weightx = 1;
            mContentConstraints.gridx = 0;
            mContentConstraints.gridy = i;
            mContentLayout.setConstraints(itemPanel, mContentConstraints);
        }
        mContentPanel.setLayout(mContentLayout);
        //用一个JScrollPane包住mContentPanel
        mJScrollPane = new JBScrollPane(mContentPanel);
        mJScrollPane.revalidate();

        getContentPane().add(mJScrollPane, 1);
    }

    /**
     * 初始化正则表达式菜单栏
     * 正则表达式搜索是否启用
     */
    private void initRegexMenu() {
        //添加控件
        mRegexMenuPanel.add(mEnableRegexCheckBox);
        mRegexMenuPanel.add(mSrcStringTextField);
        mRegexMenuPanel.add(mReplaceTipLabel);
        mRegexMenuPanel.add(mReplaceStringTextField);
        mRegexMenuPanel.add(mConfirmRegexButton);

        //设定Label位置
        mReplaceTipLabel.setHorizontalAlignment(JLabel.CENTER);

        mRegexMenConstraints.fill = GridBagConstraints.BOTH;

        mRegexMenConstraints.gridy = 0;
        mRegexMenConstraints.gridx = 0;
        mRegexMenConstraints.weightx = 1;
        mRegexMenConstraints.weighty = 0;
        mRegexMenConstraints.gridwidth = 1;
        mRegexMenuGridBagLayout.setConstraints(mEnableRegexCheckBox, mRegexMenConstraints);


        mRegexMenConstraints.gridy = 0;
        mRegexMenConstraints.gridx = 1;
        mRegexMenConstraints.weightx = 2;
        mRegexMenConstraints.weighty = 0;
        mRegexMenConstraints.gridwidth = 1;
        mRegexMenuGridBagLayout.setConstraints(mSrcStringTextField, mRegexMenConstraints);


        mRegexMenConstraints.gridy = 0;
        mRegexMenConstraints.gridx = 2;
        mRegexMenConstraints.weightx = 1;
        mRegexMenConstraints.weighty = 0;
        mRegexMenConstraints.gridwidth = 1;
        mRegexMenuGridBagLayout.setConstraints(mReplaceTipLabel, mRegexMenConstraints);


        mRegexMenConstraints.gridy = 0;
        mRegexMenConstraints.gridx = 3;
        mRegexMenConstraints.weightx = 2;
        mRegexMenConstraints.weighty = 0;
        mRegexMenConstraints.gridwidth = 1;
        mRegexMenuGridBagLayout.setConstraints(mReplaceStringTextField, mRegexMenConstraints);

        mRegexMenConstraints.gridy = 0;
        mRegexMenConstraints.gridx = 4;
        mRegexMenConstraints.weightx = 1;
        mRegexMenConstraints.weighty = 0;
        mRegexMenConstraints.gridwidth = 1;
        mRegexMenuGridBagLayout.setConstraints(mConfirmRegexButton, mRegexMenConstraints);

        mRegexMenuPanel.setLayout(mRegexMenuGridBagLayout);
        //是否启用正则表达式搜索功能，设置mSrcStringTextField，mReplaceStringTextField,mReplaceStringTextField是否获得焦点
        mSrcStringTextField.setEnabled(mEnableRegexCheckBox.isSelected());
        mReplaceStringTextField.setEnabled(mEnableRegexCheckBox.isSelected());
        mConfirmRegexButton.setEnabled(mEnableRegexCheckBox.isSelected());

        getContentPane().add(mRegexMenuPanel, 2);
    }

    /**
     * 更新RegexMenu
     */
    private void updateRegexMenu(){
        mSrcStringTextField.setEnabled(mEnableRegexCheckBox.isSelected());
        mReplaceStringTextField.setEnabled(mEnableRegexCheckBox.isSelected());
        mConfirmRegexButton.setEnabled(mEnableRegexCheckBox.isSelected());

        mRegexMenuPanel.revalidate();
    }

    /**
     * 初始化确认菜单
     */
    private void initConfirmMenu() {

        mConfirmPanel.add(mPrefixLabel);
        mConfirmPanel.add(mPrefixTextField);
        mConfirmPanel.add(mAddPrefixButton);
        mConfirmPanel.add(mGenerateSelector);

        mConfirmConstraints.fill = GridBagConstraints.BOTH;

        mConfirmConstraints.gridwidth = 1;
        mConfirmConstraints.gridx = 0;
        mConfirmConstraints.gridy = 0;
        mConfirmConstraints.weightx = 1;
        mConfirmGridBagLayout.setConstraints(mPrefixLabel, mConfirmConstraints);

        mConfirmConstraints.gridwidth = 1;
        mConfirmConstraints.gridx = 1;
        mConfirmConstraints.gridy = 0;
        mConfirmConstraints.weightx = 3;
        mConfirmGridBagLayout.setConstraints(mPrefixTextField, mConfirmConstraints);

        mConfirmConstraints.gridwidth = 1;
        mConfirmConstraints.gridx = 2;
        mConfirmConstraints.gridy = 0;
        mConfirmConstraints.weightx = 1;
        mConfirmGridBagLayout.setConstraints(mAddPrefixButton, mConfirmConstraints);


        mConfirmConstraints.gridwidth = 1;
        mConfirmConstraints.gridx = 3;
        mConfirmConstraints.gridy = 0;
        mConfirmConstraints.weightx = 1;
        mConfirmGridBagLayout.setConstraints(mGenerateSelector, mConfirmConstraints);


        mConfirmPanel.setLayout(mConfirmGridBagLayout);
        getContentPane().add(mConfirmPanel, 3);
    }


    /**
     * 设置Dialog的Constraints
     */
    private void setConstraints() {
        mConstraints.fill = GridBagConstraints.BOTH;

        mConstraints.gridwidth = 0;
        mConstraints.gridx = 0;
        mConstraints.gridy = 0;
        mConstraints.weightx = 1;
        mConstraints.weighty = 0;
        mLayout.setConstraints(mTitlePanel, mConstraints);

        mConstraints.gridwidth = 0;
        mConstraints.gridx = 0;
        mConstraints.gridy = 1;
        mConstraints.weightx = 1;
        mConstraints.weighty = 1;
        mLayout.setConstraints(mJScrollPane, mConstraints);

        mConstraints.gridwidth = 0;
        mConstraints.gridx = 0;
        mConstraints.gridy = 2;
        mConstraints.weightx = 1;
        mConstraints.weighty = 0;
        mLayout.setConstraints(mRegexMenuPanel, mConstraints);


        mConstraints.gridwidth = 0;
        mConstraints.gridx = 0;
        mConstraints.gridy = 3;
        mConstraints.weightx = 1;
        mConstraints.weighty = 0;
        mLayout.setConstraints(mConfirmPanel, mConstraints);
    }


    /**
     * 设置Dialog的参数
     */
    private void setDialog() {
        //设置布局
        setLayout(mLayout);
        //设置不可改变大小
        setResizable(false);
        //设置大小
        setSize(1080, 600);
        //设置居中
        setLocationRelativeTo(null);
        //显示最前
        setAlwaysOnTop(true);
    }


    /**
     * 初始化按钮的监听事件
     */
    private void initEvent() {
        mEnableRegexCheckBox.addActionListener(this);
        mTitleCheckAll.addActionListener(this);
        mGenerateSelector.addActionListener(this);
        mConfirmRegexButton.addActionListener(this);
        mAddPrefixButton.addActionListener(this);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "全选":
                for (Drawable drawable : mDrawables) {
                    drawable.setSelected(mTitleCheckAll.isSelected());
                }
                updateContent();
                break;
            case "确定":
                DrawableUtil.updateCurDrawableName(getRegex(), getReplaceString(), false,mDrawables);
                updateContent();
                System.out.println(TAG + "     确定");
                break;
            case "生成Selector":
                //复制图片
                if (copyFile(mInputFilePath, mOutPutFilePath)) {
                    break;
                }
                //生成Selector
                generateSelector();
                dialogDismiss();
                break;
            case "查找字符(正则表达式匹配)":
                updateRegexMenu();
                break;
            case "添加前缀":
                addPrefix();
                updateContent();
            default:
                break;
        }
    }

    /**
     * 添加前缀
     */
    private void addPrefix() {
        String prefix = mPrefixTextField.getText();
        ArrayList<Drawable> drawables = (ArrayList<Drawable>) DrawableUtil.getSelectedDrawables(mDrawables);
        for (Drawable drawable : drawables) {
            String drawableCurName = drawable.getDrawableCurName();
            drawable.setDrawableCurName(prefix + drawableCurName);
        }
    }

    /**
     * @param inputFilePath  输入路径
     * @param outPutFilePath 输出路径
     *                       <p>
     *                       将图片复制到目标路径
     * @return 复制是否被中断
     */
    private boolean copyFile(String inputFilePath, String outPutFilePath) {
        boolean isInterrupted = false;
        for (Drawable drawable : mDrawables) {
            if (!drawable.isSelected()) {
                continue;
            }
            int dialogMessage = Util.copyFile(inputFilePath, outPutFilePath, drawable);
            switch (dialogMessage) {
                case Messages.YES:
                    break;
                case Messages.NO:
                    break;
                case Messages.CANCEL:
                    isInterrupted = true;
                    break;
            }
            if (isInterrupted) {
                break;
            }
        }
        return isInterrupted;
    }


    /**
     * 生成Selector
     */
    private void generateSelector() {
        //生成Selector的文本 只生成选中的部分
        ArrayList<SelectorBean> selectorBeans = (ArrayList<SelectorBean>) DrawableUtil.getSelectorBeans(mDrawables);
        System.out.print(selectorBeans.size());
        for (SelectorBean selectorBean : selectorBeans) {
            //去掉png后缀
            String normalDrawable = Util.replaceString(selectorBean.normalDrawable, "", ".png");
            String pressedDrawable = Util.replaceString(selectorBean.pressedDrawable, "", ".png");
            //生成xml内容
            String xmlContent = DrawableUtil.getSelectorContent(normalDrawable, pressedDrawable);
            //生成文件名，文件路径名
            String fileName = Util.replaceString(selectorBean.normalDrawable, "", "_normal.png") + "_selector.xml";
            String filePath = mDrawablePath + "/" + fileName;

            switch (Util.outputFile(filePath, fileName, xmlContent)) {
                case Messages.YES:
                    break;
                case Messages.NO:
                    break;
                case Messages.CANCEL:
                    //用户点击cancel，中断功能
                    return;
                default:
                    break;

            }

        }

    }


    /**
     * 刷新内容区域
     */
    private void updateContent() {
        remove(mJScrollPane);
        initContent();
        setConstraints();
        revalidate();
    }

    /**
     * 展示Dialog
     */
    public void showDialog() {
        setVisible(true);
    }


    /**
     * 取消展示Dialog
     */
    private void dialogDismiss() {
        setVisible(false);
        dispose();
    }


    /**
     * @return 正则表达式输入框的字符串
     */
    private String getRegex() {
        return mSrcStringTextField.getText();
    }

    /**
     * @return 替换的字符串
     */
    private String getReplaceString() {
        return mReplaceStringTextField.getText();
    }

}
