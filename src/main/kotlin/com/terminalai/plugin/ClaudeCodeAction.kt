package com.terminalai.plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vfs.VirtualFile

class ClaudeCodeAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return

        val projectPath = getProjectPath(virtualFile) ?: return
        TerminalLauncher.launchTerminal(projectPath, "claude", project)
    }

    override fun update(e: AnActionEvent) {
        val virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible = virtualFile != null && virtualFile.isDirectory
    }

    private fun getProjectPath(file: VirtualFile): String? {
        return if (file.isDirectory) file.path else null
    }
}
