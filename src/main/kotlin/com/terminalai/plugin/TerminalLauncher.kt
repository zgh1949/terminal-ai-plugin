package com.terminalai.plugin

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import java.io.File

object TerminalLauncher {

    fun launchTerminal(projectPath: String, command: String, project: Project?) {
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                val projectDir = File(projectPath)
                if (!projectDir.exists() || !projectDir.isDirectory) {
                    return@executeOnPooledThread
                }

                val processBuilder = when {
                    SystemInfo.isWindows -> createWindowsCommand(projectDir, command)
                    SystemInfo.isMac -> createMacCommand(projectDir, command)
                    SystemInfo.isLinux -> createLinuxCommand(projectDir, command)
                    else -> throw UnsupportedOperationException("Unsupported OS")
                }

                processBuilder.directory(projectDir)
                processBuilder.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createWindowsCommand(projectDir: File, command: String): ProcessBuilder {
        val cmdCommands = """cd /d "${projectDir.absolutePath}" && echo $command && $command && pause"""
        return ProcessBuilder("cmd.exe", "/c", "start", "cmd.exe", "/k", cmdCommands)
    }

    private fun createMacCommand(projectDir: File, command: String): ProcessBuilder {
        return ProcessBuilder(
            "osascript",
            "-e",
            """tell application "Terminal"
                do script "cd '${projectDir.absolutePath}' && echo '$command' && $command && exec /bin/bash"
                activate
            end tell"""
        )
    }

    private fun createLinuxCommand(projectDir: File, command: String): ProcessBuilder {
        val bashCommand = """cd '${projectDir.absolutePath}' && echo '$command' && $command && exec /bin/bash"""
        return listOf(
            "gnome-terminal" to listOf("--working-directory=${projectDir.absolutePath}", "--", "bash", "-c", bashCommand),
            "konsole" to listOf("--workdir", projectDir.absolutePath, "-e", "bash", "-c", bashCommand),
            "xfce4-terminal" to listOf("--working-directory=${projectDir.absolutePath}", "-e", "bash", "-c", bashCommand),
            "xterm" to listOf("-e", "bash", "-c", bashCommand)
        ).firstOrNull { (terminal, _) ->
            try {
                ProcessBuilder("which", terminal).start().waitFor() == 0
            } catch (e: Exception) {
                false
            }
        }?.let { (_, args) ->
            ProcessBuilder(args)
        } ?: ProcessBuilder("xterm", "-e", "bash", "-c", bashCommand)
    }
}
