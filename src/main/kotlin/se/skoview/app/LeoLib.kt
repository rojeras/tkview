/**
 * Copyright (C) 2013-2020 Lars Erik Röjerås
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package se.skoview.app

import org.w3c.xhr.XMLHttpRequest
import io.kvision.core.Component
import kotlin.browser.document

fun getAsync(url: String, callback: (String) -> Unit) {
    console.log("getAsync(): URL: $url")
    val xmlHttp = XMLHttpRequest()
    xmlHttp.open("GET", url)
    xmlHttp.onload = {
        if (xmlHttp.readyState == 4.toShort() && xmlHttp.status == 200.toShort()) {
            callback.invoke(xmlHttp.responseText)
        }
    }
    xmlHttp.send()
}

fun getSync(url: String): String? {
    val xmlHttp = XMLHttpRequest()
    xmlHttp.open("GET", url, false)
    xmlHttp.send(null)

    return if (xmlHttp.status == 200.toShort()) {
        xmlHttp.responseText
    } else {
        null
    }
}

fun getHeightToRemainingViewPort(
    topComponent: Component,
    delta: Int = 48
): String {
    val occupiedViewPortArea = (topComponent.getElementJQuery()?.height() ?: 152).toInt()
    val heightToRemove = occupiedViewPortArea + delta
    return "calc(100vh - ${heightToRemove}px)"
}

fun getBaseUrl(): String {
    val url = "https://rivta.se/tkview/apicache.php"
    return url
}

fun getVersion(versionName: String = "tkviewVersion"): String {
    val versionElement = document.getElementById(versionName)

    return if (versionElement != null) versionElement.getAttribute("content") ?: "-1.-1.-1"
    else "-2.-2.-2"
}