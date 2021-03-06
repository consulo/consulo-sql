/*
 * Copyright 2012-2014 Dan Cioca
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dci.intellij.dbn.common.util;

import com.dci.intellij.dbn.vfs.DBVirtualFile;
import com.dci.intellij.dbn.vfs.DatabaseFileSystem;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.io.ReadOnlyAttributeUtil;

import javax.swing.Icon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VirtualFileUtil {

    public static Icon getIcon(VirtualFile virtualFile) {
        if (virtualFile instanceof DBVirtualFile) {
            DBVirtualFile file = (DBVirtualFile) virtualFile;
            return file.getIcon();
        }
        return virtualFile.getFileType().getIcon();
    }

    public static boolean isDatabaseFileSystem(VirtualFile file) {
        return file.getFileSystem() == DatabaseFileSystem.getInstance();
    }

    public static boolean isLocalFileSystem(VirtualFile file) {
        return file.isInLocalFileSystem();
    }

    public static boolean isVirtualFileSystem(VirtualFile file) {
        return !isDatabaseFileSystem(file) && !isLocalFileSystem(file);
    }    

    public static VirtualFile ioFileToVirtualFile(File file) {
        return LocalFileSystem.getInstance().findFileByIoFile(file);
    }

    public static void setReadOnlyAttribute(VirtualFile file, boolean readonly) {
        try {
            ReadOnlyAttributeUtil.setReadOnlyAttribute(file, readonly);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setReadOnlyAttribute(String path, boolean readonly) {
        try {
            ReadOnlyAttributeUtil.setReadOnlyAttribute(path, readonly);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static VirtualFile[] lookupFilesForName(Project project, String name) {
        ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
        VirtualFile[] contentRoots = rootManager.getContentRoots();
        return lookupFilesForName(contentRoots, name);
    }

    public static VirtualFile[] lookupFilesForName(Module module, String name) {
        ProjectRootManager rootManager = ProjectRootManager.getInstance(module.getProject());
        VirtualFile[] contentRoots = rootManager.getContentRoots();
        return lookupFilesForName(contentRoots, name);
    }

    public static VirtualFile[] lookupFilesForName(VirtualFile[] roots, String name) {
        List<VirtualFile> bucket = new ArrayList<VirtualFile>();
        for (VirtualFile root: roots) {
            collectFilesForName(root, name, bucket);
        }
        return bucket.toArray(new VirtualFile[bucket.size()]);
    }

    private static void collectFilesForName(VirtualFile root, String name, List<VirtualFile> bucket) {
        for (VirtualFile virtualFile: root.getChildren()) {
            boolean fileIgnored = FileTypeManager.getInstance().isFileIgnored(virtualFile.getName());
            if (!fileIgnored) {
                if (virtualFile.isDirectory() ) {
                    collectFilesForName(virtualFile, name, bucket);
                } else {
                    if (virtualFile.getName().equalsIgnoreCase(name)) {
                        bucket.add(virtualFile);
                    }
                }
            }
        }
    }
}

