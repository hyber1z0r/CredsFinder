package App

import Finder.CredsFinder

fun main(args: Array<String>) {
    val finder = CredsFinder()
    finder.scan(args[0])
}