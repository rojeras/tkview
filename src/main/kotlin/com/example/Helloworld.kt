package com.example

import pl.treksoft.kvision.Application
import pl.treksoft.kvision.core.FlexDirection
import pl.treksoft.kvision.core.JustifyContent
import pl.treksoft.kvision.html.div
import pl.treksoft.kvision.i18n.DefaultI18nManager
import pl.treksoft.kvision.i18n.I18n
import pl.treksoft.kvision.i18n.I18n.tr
import pl.treksoft.kvision.panel.flexPanel
import pl.treksoft.kvision.panel.root
import pl.treksoft.kvision.require
import pl.treksoft.kvision.startApplication
import pl.treksoft.kvision.utils.px

class Helloworld : Application() {
    init {
        require("css/helloworld.css")
    }

    override fun start() {

        root("helloworld") {
            flexPanel(FlexDirection.ROW, justify = JustifyContent.CENTER) {
                div("Hello again world!", classes = setOf("helloworld")) {
                    marginTop = 50.px
                    fontSize = 50.px
                }
            }
        }
    }
}

fun main() {
    startApplication(::Helloworld)
}
