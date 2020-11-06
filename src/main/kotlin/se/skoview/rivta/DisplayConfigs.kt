/**
 * Copyright (C) 2020 Lars Erik Röjerås
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

package se.skoview.rivta

import pl.treksoft.kvision.core.Color
import pl.treksoft.kvision.core.WhiteSpace
import pl.treksoft.kvision.core.WordBreak
import pl.treksoft.kvision.core.onClick
import pl.treksoft.kvision.html.*
import se.skoview.app.RivManager
import se.skoview.model.DomainTypeEnum

fun getClickableDomainComponent(domainName: String): Div {
    return Div {
    }.apply {
        span {
            wordBreak = WordBreak.BREAKALL
            whiteSpace = WhiteSpace.PREWRAP
            color = Color.hex(0x008583)
            +domainName
        }.onClick {
            println("In onClick in getClickableDomainComponent()")
            println("Will dispatch SelectDomain")
            RivManager.fromAppShowDomainView(domainName)
        }
    }
}

object Texts {
    val domainTypeText = mapOf<DomainTypeEnum, String>(
        DomainTypeEnum.NATIONAL to "Nationell",
        DomainTypeEnum.EXTERNAL to "Extern",
        DomainTypeEnum.APPLICATION_SPECIFIC to "Applikationsspecifik"
    )

    val domainTypeAltText = mapOf(
        DomainTypeEnum.NATIONAL to "Tjänstedomänen utvecklas och/eller förvaltas av Inera. Ineras Arkitektursektion ansvarar för kvalité inom teknik, informatik, arkitektur och testsviter/tjänsteproducenter, samt att domänen passar in i och har en tydlig roll i den sammantagna portföljen av nationella tjänstekontrakt. Arkitektursektionens kvalitetssäkringsprocess följs under utvecklings- och förvaltningsfasen.",
        DomainTypeEnum.EXTERNAL to "Annan part än Inera utvecklar/förvaltar tjänstedomänen. Annan part ansvarar för kvalité inom teknik, informatik och arkitektur. Tjänstekontrakten i tjänstedomänen följer giltig RIV TA-profil (tjänstekontraktens tekniska utformning och paketering) och kan därmed driftsättas och anslutningshanteras i NTJP om Ineras infrastrukturkrav är uppfyllda. Tjänstekontrakten tillämpas inte inom Ineras uppdrag. Ineras Arkitektursektion ansvarar inte för namnsättning utöver vad som anges nedan, men kan konsulteras om extern part önskar.",
        DomainTypeEnum.APPLICATION_SPECIFIC to "Tjänstedomänen definierar ett applikations/lösningsspecifikt API till en applikation/tjänst som ägs av Inera. Applikationsförvaltningen vill återanvända Ineras anslutningsorganisation och infrastruktur (Tjänsteplattformen), men ha kvar full kontroll över alla beslut som rör kontraktsdesign, versioner och releaser. Förvaltningen har ett eget, lokalt ansvar för tjänstekontraktens arkitektur, informatik och teknik inom ramen för det ansvar som projektet/förvaltningen har för applikationen/tjänsten i sig. Tjänstekontrakten får bara användas när den specifika applikationen/tjänsten är en av parterna i informationsutbytet."
    )
}
