package com.nomopix.appmanager

import com.nomopix.appmanager.core.network.IndexParser
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IndexParserTest {

    private lateinit var indexParser: IndexParser

    @Before
    fun setUp() {
        indexParser = IndexParser()
    }

    @Test
    fun testInvalidUrlFallsBackToNonEmptyList() = runTest {
        val repos = indexParser.fetchIndex("https://invalid-url.local/index.txt")
        assertTrue(repos.isNotEmpty())
        assertTrue(repos.all { it.owner.isNotBlank() && it.repo.isNotBlank() })
    }
}
