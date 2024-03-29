/**
 * MIT License

 * Copyright (c) 2023 Klee

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.klee.readview.loader

import org.klee.readview.entities.BookData
import org.klee.readview.entities.ChapData
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.regex.Pattern

private const val TAG = "DefaultNativeLoader"
private const val UTF8_BOM_PREFIX = "\uFEFF"       // ZWNBSP字符，UTF-8带BOM格式
/**
 * 一个简单的本地小说加载器
 */
open class DefaultNativeLoader(var file: File? = null) : BookLoader {

    /**
     * TODO: 处理好文件编码问题
     */
    protected open val reader: BufferedReader get() {
        return BufferedReader(
            InputStreamReader(FileInputStream(file))
        )
    }

    val titlePatternList by lazy { ArrayList<Pattern>() }

    /**
     * 判断指定字符串是否为章节标题
     * @param line 需要判断的目标字符串
     */
    protected open fun isTitle(line: String): Boolean {
        if (titlePatternList.size == 0) {
            synchronized(titlePatternList) {
                val titlePattern = Pattern.compile("(^\\s*第)(.{1,7})[章卷](\\s*)(.*)")
                titlePatternList.add(titlePattern)
            }
        }
        titlePatternList.forEach {
            if (it.matcher(line).matches()) {
                return true
            }
        }
        return false
    }

    override fun initToc(): BookData {
        val reader = this.reader
        val bookData = BookData()
        var chapIndex = 1
        val stringBuilder = StringBuilder()
        var chap: ChapData? = null
        var line: String?
        var firstChapInitialized = false
        do {
            line = reader.readLine()
            if (line == null) {
                // 处理剩余内容
                if (firstChapInitialized) {
                    chap?.content = stringBuilder.toString()
                } else if (stringBuilder.isNotEmpty()) {
                    bookData.addChapter(ChapData(chapIndex).apply {
                        content = stringBuilder.toString()
                    })
                }
                stringBuilder.clear()
                break
            }
            // 跳过空白行
            if (line.isBlank())
                continue
            if (line.startsWith(UTF8_BOM_PREFIX)) {
                line = line.substring(1)
            }
            // 开始解析内容
            if (isTitle(line)) {
                // 在第一个标题出现之前，可能会出现部分没有章节标题所属的行，将这些作为一个无标题章节
                if (stringBuilder.isNotEmpty()) {
                    if (!firstChapInitialized) {
                        chap = ChapData(chapIndex).apply {
                            content = stringBuilder.toString()
                            stringBuilder.delete(0, stringBuilder.length)
                        }
                        bookData.addChapter(chap)
                        chapIndex++
                    } else {
                        chap!!.content = stringBuilder.toString()
                        stringBuilder.delete(0, stringBuilder.length)
                    }
                }
                if (!firstChapInitialized) firstChapInitialized = true
                chap = ChapData(chapIndex, line)
                bookData.addChapter(chap)
                chapIndex++
            } else {
                stringBuilder.append(line).append('\n')
            }
        } while (true)
        return bookData
    }

}