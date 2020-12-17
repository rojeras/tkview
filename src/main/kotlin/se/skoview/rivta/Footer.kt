package se.skoview.rivta

import pl.treksoft.kvision.core.*
import pl.treksoft.kvision.html.*
import pl.treksoft.kvision.utils.perc
import pl.treksoft.kvision.utils.px
import se.skoview.model.RivState

fun Container.footerInfo(state: RivState) {
    div(
        rich = true,
    ) {
        marginTop = 5.px
        width = 100.perc
        align = Align.CENTER
        background = Background(Color.name(Col.LIGHTGRAY))
        if (! state.lastUpdateTime.isNullOrBlank()) {
        +"<b>Senast uppdaterad</b> ${state.lastUpdateTime}"
        // add(ResetCacheButton("Uppdatera nu"))
        }
    }
}