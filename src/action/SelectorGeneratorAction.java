package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import entity.Drawable;
import org.apache.http.util.TextUtils;
import utils.DrawableUtil;
import view.SelectorGeneratorDialog;

import java.util.Collection;
import java.util.List;

public class SelectorGeneratorAction extends AnAction {


    private String TAG = getClass().getSimpleName();
    /*
    * 输入，输出路径
    * */
    private String mOutputFilePath;
    private String mInputFilePath;
    /*
    * drawable路径
    * */
    private String mDrawablePath;
    private Project mProject;
    private VirtualFile mVirtualFile;

    @Override
    public void actionPerformed(AnActionEvent e) {


        initMembers(e);
        getInputPath();
        getDrawablePath();
        //如果输入的文件路径为空或者是空字符串的话。中断
        if (mInputFilePath != null&&TextUtils.isEmpty(mInputFilePath)) {
            return;
        }

        List<Drawable> drawables  = DrawableUtil.getDrawableList(mInputFilePath);
        SelectorGeneratorDialog selectorGeneratorDialog = new SelectorGeneratorDialog(drawables,mInputFilePath,
                mOutputFilePath,mDrawablePath);
        selectorGeneratorDialog.showDialog();

    }

    /**
     * @param event AnActionEvent
     *              初始化成员变量
     */
    private void initMembers(AnActionEvent event) {
        mVirtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        mProject = event.getProject();
        if (mVirtualFile.isDirectory()) {
            mOutputFilePath = mVirtualFile.getPath();
        }else
        {
            mOutputFilePath = mVirtualFile.getParent().getPath();
        }



    }

    /**
     * 获得输入路径
     */
    private void getInputPath() {
        mInputFilePath = Messages.showInputDialog(mProject, "", "请输入图片文件夹路径", Messages
                .getInformationIcon());

        if (TextUtils.isEmpty(mInputFilePath)) {
            Messages.showMessageDialog("未输入图片文件夹的路径","Error",null);
        }
    }


    private void getDrawablePath(){
        Collection<VirtualFile> virtualFiles= FilenameIndex.getVirtualFilesByName(mProject,"drawable", GlobalSearchScope.allScope
                (mProject));
        if (virtualFiles.size() <= 0) {
            System.out.println(TAG+"  0");
            return;
        }else{
            for (VirtualFile virtualFile : virtualFiles) {
                System.out.println(virtualFile.getPath());
                if (virtualFile.getPath().contains("main/res/drawable")) {
                    mDrawablePath = virtualFile.getPath();
                }
            }

        }

    }


}
