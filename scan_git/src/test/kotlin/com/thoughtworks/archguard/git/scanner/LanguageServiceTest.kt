package com.thoughtworks.archguard.git.scanner

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class LanguageServiceTest {

    @Test
    fun should_return_LICENSE_when_is_a_license_file() {
        val lang = LanguageService()
        assertEquals("License", lang.determineLanguage("LICENSE"))
    }

    @Test
    fun should_check_gitignore() {
        val lang = LanguageService()
        assertEquals("gitignore", lang.determineLanguage(".gitignore"))
    }

    @Test
    fun should_return_typescript_define_file() {
        val lang = LanguageService()
        assertEquals("TypeScript Typings", lang.determineLanguage("types.d.ts"))
    }

    @Test
    fun should_test_get_extension_no_extension() {
        val lang = LanguageService()
        assertEquals("YAML", lang.determineLanguage(".travis.yml"))
    }

    @Test
    fun should_return_ext_name() {
        val lang = LanguageService()
        assertEquals("d.ts", lang.getExtension("types.d.ts"))
    }

    @Test
    fun should_return_json_for_file() {
        val lang = LanguageService()
        assertEquals("JSON", lang.determineLanguage("api.json"))
    }

    @Test
    fun should_return_empty_for_not_a_language_file() {
        val lang = LanguageService()
        assertEquals("", lang.determineLanguage("logo.png"))
    }
}