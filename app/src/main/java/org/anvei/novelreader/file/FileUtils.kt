package org.anvei.novelreader.file

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object FileUtils {

    fun readStringFromFile(file: File): String {
        val reader = BufferedReader(FileReader(file))
        val stringBuilder = StringBuilder()
        var line: String?
        while (true) {
            line = reader.readLine()
            line ?: break
            stringBuilder.append(line).append("\n")
        }
        reader.close()
        if (stringBuilder.isEmpty())
            return ""
        return stringBuilder.substring(0, stringBuilder.length - 1)     // 去掉最后一个多余的换行符
    }

    fun writeString(file: File, string: String) {
        val writer = BufferedWriter(FileWriter(file))
        writer.write(string)
        writer.close()
    }
}