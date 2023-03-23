package com.jinzelin.groupfiles;

import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.psi.PsiFileSystemItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TreeStructureProvider implements com.intellij.ide.projectView.TreeStructureProvider {
    static final String OtherGroup = "Other";

    @Override
    public @NotNull Collection<AbstractTreeNode<?>> modify(
            @NotNull AbstractTreeNode<?> parent,
            @NotNull Collection<AbstractTreeNode<?>> children,
            ViewSettings viewSettings) {

        if(parent instanceof FileGroupProjectViewNode){
            return children;
        }

        List<AbstractTreeNode<?>> ret = new ArrayList<>();
        Map<String, FileGroupProjectViewNode> map = new HashMap<>();
        Map<String, String> groups = FileGroupState.getInstance(parent.getProject()).getData();

        for (AbstractTreeNode<?> child : children) {
            String filePath = child.getName();
            if(child.getValue() instanceof PsiFileSystemItem){
                filePath = ((PsiFileSystemItem) child.getValue()).getVirtualFile().getCanonicalPath();
            }
            String groupKey;
            if(groups.containsKey(filePath)){
                groupKey = groups.get(filePath);
            } else {
                groupKey = TreeStructureProvider.OtherGroup;
            }
            FileGroupProjectViewNode group = map.get(groupKey);
            if (group == null) {
                group = new FileGroupProjectViewNode(child.getProject(), viewSettings, child, groupKey);
                map.put(groupKey, group);
                ret.add(group);
            }
            child.setParent(group);
            group.addChild(child);
        }

        // Undo grouping if only one group
        if(ret.size() == 1){
            AbstractTreeNode<?> group = ret.get(0);
            if (group instanceof FileGroupProjectViewNode && Objects.equals(group.getName(), TreeStructureProvider.OtherGroup)) {
                FileGroupProjectViewNode g = (FileGroupProjectViewNode) group;
                for(AbstractTreeNode<?> child : g.getChildren()){
                    child.setParent(parent);
                }
                return g.getChildren();
            }
        }
        return ret;
    }

    @Override
    public @Nullable Object getData(@NotNull Collection<AbstractTreeNode<?>> selected, @NotNull String dataId) {
        return com.intellij.ide.projectView.TreeStructureProvider.super.getData(selected, dataId);
    }
}