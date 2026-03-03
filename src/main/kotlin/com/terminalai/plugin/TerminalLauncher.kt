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
        val tempScript = File(projectDir, "temp_launch_terminal.bat")
        tempScript.writeText("""
            @echo off
            cd /d "${projectDir.absolutePath}"
            echo $command
            $command
            pause
        """.trimIndent())
        tempScript.deleteOnExit()

        return ProcessBuilder("cmd.exe", "/c", "start", "cmd.exe", "/k", "\"${tempScript.absolutePath}\"")
    }

    private fun createMacCommand(projectDir: File, command: String): ProcessBuilder {
        val tempScript = File(projectDir, "temp_launch_terminal.sh")
        tempScript.writeText("""
            #!/bin/bash
            cd "${projectDir.absolutePath}"
            echo "$command"
            $command
            exec /bin/bash
        """.trimIndent())
        tempScript.setExecutable(true)
        tempScript.deleteOnExit()

        return ProcessBuilder(
            "osascript",
            "-e",
            """tell application "Terminal"
                do script "cd '${projectDir.absolutePath}' && '${tempScript.absolutePath}'"
                activate
            end tell"""
        )
    }

    private fun createLinuxCommand(projectDir: File, command: String): ProcessBuilder {
        val tempScript = File(projectDir, "temp_launch_terminal.sh")
        tempScript.writeText("""
            #!/bin/bash
            cd "${projectDir.absolutePath}"
            echo "$command"
            $command
            exec /bin/bash
        """.trimIndent())
        tempScript.setExecutable(true)
        tempScript.deleteOnExit()

        return listOf(
            "gnome-terminal" to listOf("--working-directory=${projectDir.absolutePath}", "--", "bash", "-c", "'${tempScript.absolutePath}'"),
            "konsole" to listOf("--workdir", projectDir.absolutePath, "-e", "bash", "-c", "'${tempScript.absolutePath}'"),
            "xfce4-terminal" to listOf("--working-directory=${projectDir.absolutePath}", "-e", "bash", "-c", "'${tempScript.absolutePath}'"),
            "xterm" to listOf("-e", "bash", "-c", "cd '${projectDir.absolutePath}' && '${tempScript.absolutePath}'")
        ).firstOrNull { (terminal, _) ->
            try {
                ProcessBuilder("which", terminal).start().waitFor() == 0
            } catch (e: Exception) {
                false
            }
        }?.let { (_, args) ->
            ProcessBuilder(args)
        } ?: ProcessBuilder("xterm", "-e", "bash", "-c", "cd '${projectDir.absolutePath}' && '${tempScript.absolutePath}'")
    }
}
