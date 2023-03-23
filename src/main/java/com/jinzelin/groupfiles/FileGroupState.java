package com.jinzelin.groupfiles;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@State(name = "groupFiles", storages = {@Storage(value = "groupFiles.xml")})
public class FileGroupState implements PersistentStateComponent<FileGroupState> {
    private Map<String, String> data = new HashMap<>();

    @Override
    public @Nullable FileGroupState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull FileGroupState state) {
        this.data = state.data;
    }

    public static FileGroupState getInstance(Project project){
        return project.getService(FileGroupState.class);
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
