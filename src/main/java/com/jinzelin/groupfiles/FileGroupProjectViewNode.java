package com.jinzelin.groupfiles;

import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FileGroupProjectViewNode extends ProjectViewNode {
    private final String name;
    private final List<AbstractTreeNode<?>> children;

    protected FileGroupProjectViewNode(Project project,
                                 ViewSettings viewSettings,
                                 AbstractTreeNode<?> node,
                                 String name) {
        super(project, node.getValue(), viewSettings);
        this.name = name;
        this.children = new ArrayList<>();
    }

    @Override
    public boolean contains(@NotNull VirtualFile file) {
        for (AbstractTreeNode<?> childNode : this.children) {
            ProjectViewNode<?> treeNode = (ProjectViewNode<?>) childNode;
            if (treeNode.contains(file)) {
                return true;
            }
        }
        return false;
    }

    public void addChild(AbstractTreeNode<?> node) {
        this.children.add(node);
    }

    @NotNull
    @Override
    public List<AbstractTreeNode<?>> getChildren() {
        return this.children;
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText(name);
        presentation.setIcon(AllIcons.Nodes.ModuleGroup);
    }

    @Override
    public String getName() {
        return this.name;
    }
}