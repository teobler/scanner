package org.archguard.scanner.analyser

import io.mockk.every
import io.mockk.mockk
import org.archguard.scanner.core.sourcecode.SourceCodeContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MyLanguageAnalyserTest {
    private val mockContext: SourceCodeContext = mockk {
        every { path } returns this.javaClass.classLoader.getResource("kotlin").path
    }

    @Test
    fun `should output the code data structs when analysing the code`() {
        val analyser = MyLanguageAnalyser(mockContext)
        val result = analyser.analyse()

        assertThat(result[0].NodeName).isEqualTo("Hello.kt")
    }
}
