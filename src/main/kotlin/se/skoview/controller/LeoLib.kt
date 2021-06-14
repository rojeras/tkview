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

package se.skoview.controller

import io.kvision.core.Component
import kotlinx.browser.document
import org.w3c.xhr.XMLHttpRequest

/**
 * Library function to make asynchronous API call.
 *
 * @param url URL of the api call,
 * @param callback Lambda to invoke on the result of the call,
 */
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

/**
 * Library function to make synchronous API call.
 *
 * @param url URL of the api call
 *
 * @return The respons of the call if successful, else null.
 */
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

/**
 * Library function. Calculates height of remaining viewport below the top of a certain component and returns input string for setStyle call.
 *
 * @param topComponent Component to base the calculation from.
 * @param delta A delta value which will be set depending on the height of the component.
 *
 * @return String with CSS setting to be used in call to [setStyle][io.kvision.core.StyledComponent.setStyle].
 */
fun getHeightToRemainingViewPort(
    topComponent: Component,
    delta: Int = 48
): String {
    val occupiedViewPortArea = (topComponent.getElementJQuery()?.height() ?: 152).toInt()
    val heightToRemove = occupiedViewPortArea + delta
    return "calc(100vh - ${heightToRemove}px)"
}

/**
 * Library function.
 *
 * @return URL to api. In our case the caching application.
 */
fun getBaseUrl(): String = "https://rivta.se/tkview/apicache.php"

/**
 * Library function. Obtains and return the version information (set in index.html)
 *
 * @return Current version of application.
 */
fun getVersion(versionName: String = "tkviewVersion"): String {
    val versionElement = document.getElementById(versionName)

    return if (versionElement != null) versionElement.getAttribute("content") ?: "-1.-1.-1"
    else "-2.-2.-2"
}
