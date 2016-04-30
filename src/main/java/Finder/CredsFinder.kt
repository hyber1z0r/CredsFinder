package Finder

import org.apache.commons.validator.UrlValidator
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import java.io.PrintWriter

class CredsFinder {
    private val URL = "http://pastebin.com/"
    private val QUERY = "search?q="
    private var driver: WebDriver
    private val validator: UrlValidator

    constructor() {
        System.setProperty("webdriver.chrome.driver", "lib/chromedriver")
        driver = ChromeDriver()
        validator = UrlValidator(arrayOf("http", "https"))
    }

    fun scan(query: String) {
        val allCredentials: MutableList<String> = mutableListOf()
        for (link in findLinks(query)) {
            allCredentials.addAll(extractCreds(link))
        }
        writeToFile(allCredentials)
        driver.quit()
    }

    private fun writeToFile(credentials: List<String>) {
        val writer = PrintWriter("credentials.txt", "UTF-8")
        credentials.forEach { writer.write(it + "\n") }
        writer.close()
    }

    private fun extractCreds(link: String): List<String> {
        // We want to visit the link and extract all lines and return a list of credentials
        driver.get(link)

        val lines = driver.findElement(By.tagName("pre")).text.split("\n")

        return lines.filter { it.contains(Regex("\\w+:\\w+"))}
    }

    // This will find all the links on the first page
    private fun findLinks(query: String): List<String> {
        driver.get(URL + QUERY + query)

        // This is the first page
        val results = driver.findElements(By.className("gsc-result"))

        // This is all the RAW URLs of the first page
        val URLs = results
                .map { it.findElement(By.tagName("a")) }
                .map { it.getAttribute("href") }
                .filter { validator.isValid(it) }
                .map { it.split("/").last() }
                .map { URL + "raw/" + it }

        return URLs
    }
}