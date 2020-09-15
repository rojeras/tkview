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

import pl.treksoft.kvision.core.*
import pl.treksoft.kvision.html.*
import pl.treksoft.kvision.i18n.I18n
import pl.treksoft.kvision.modal.Modal
import pl.treksoft.kvision.panel.SimplePanel
import pl.treksoft.kvision.state.bind
import se.skoview.app.store


object DomainPage : SimplePanel() {

    init {
        div { }.bind(store) { state ->
            println("In DomainPage")

            val selectedDomain = state.selectedDomain

            val domain = DomainMap[selectedDomain]

            if (domain == null) {
                h1 { +"Information om domänen saknas!" }
            } else {
                val domainType = domain.getDomainType()

                println("Display domain: $selectedDomain")
                console.log(domain)

                h1 { +domain.name }
                h2 { +"Beskrivning" }
                //  span { +domainDescription }
                p { +domain.getDescription() }

                h2 { +"Domäntyp" }

                val domainTypeText: String
                val domainTypeAltText: String

                when (domain.getDomainType()) {
                    DomainType.NATIONAL -> {
                        domainTypeText = "Nationell Tjänstedomän"
                        domainTypeAltText =
                            "Tjänstedomänen utvecklas och/eller förvaltas av Inera. Inera A&R ansvarar för kvalité inom teknik, informatik, arkitektur och testsviter/tjänsteproducenter, samt att domänen passar in i och har en tydlig roll i den sammantagna portföljen av nationella tjänstekontrakt. A&Rs kvalitetssäkringsprocess följs under utvecklings- och förvaltningsfasen."
                    }
                    DomainType.EXTERNAL -> {
                        domainTypeText = "Extern tjänstedomän"
                        domainTypeAltText =
                            "Annan part än Inera A&R utvecklar/förvaltar tjänstedomänen. Annan part ansvarar för kvalité inom teknik, informatik och arkitektur. Tjänstekontrakten i tjänstedomänen följer giltig RIV TA-profil (tjänstekontraktens tekniska utformning och paketering) och kan därmed driftsättas och anslutningshanteras i NTJP om ICC.s infrastrukturkrav är uppfyllda. Tjänstekontrakten tillämpas inte inom Ineras uppdrag. Inera A&R ansvarar inte för namnsättning utöver vad som anges nedan, men kan konsulteras om extern part önskar."
                    }
                    DomainType.APPLICATION_SPECIFIC -> {
                        domainTypeText = "Applikationsspecifik tjänstedomän"
                        domainTypeAltText =
                            "Tjänstedomänen definierar ett applikations/lösningsspecifikt API till en applikation/tjänst som ägs av Inera. Applikationsförvaltningen vill återanvända Ineras anslutningsorganisation och infrastruktur (Tjänsteplattformen), men ha kvar full kontroll över alla beslut som rör kontraktsdesign, versioner och releaser. Förvaltningen har ett eget, lokalt ansvar för tjänstekontraktens arkitektur, informatik och teknik inom ramen för det ansvar som projektet/förvaltningen har för applikationen/tjänsten i sig. Tjänstekontrakten får bara användas när den specifika applikationen/tjänsten är en av parterna i informationsutbytet."
                    }
                }

                val modal = Modal(domainTypeText)
                modal.add(span(domainTypeAltText))
                modal.addButton(Button("Stäng").onClick {
                    modal.hide()
                })

                div {
                    p { +domainTypeText }
                    color = Color.hex(0x008583)
                }.onClick {   //.onEvent { mouseover = { modal.show() }
                    modal.show()
                }
            }

            h2 { +"Tjänstekontrakt" }
            if (domain!!.versions == null) h3 { "Ingen information om versioner av tjänstedomäner är tillgänga" }
            else {
                println("Time to show domain versions")
                ul {
                    //for (version in domain.versions.filterNot {  it.name.contains("_") || it.name.contains("RC") || it.name.contains("trunk") }.sortedBy { it.name }.reversed()) {
                    domain.versions
                        .filterNot { it.hidden || it.name.contains("_") || it.name.contains("RC") || it.name.contains("trunk") }
                        .sortedBy { it.name }
                        .reversed()
                        .map { version: Version ->
                            li {
                                +version.name
                                ul {
                                    version.interactionDescriptions.map {
                                        li { +"${it.wsdlContract().first} ${it.wsdlContract().second}.${it.wsdlContract().third}" }
                                    }
                                }
                            }
                        }

                }
            }



            h2 { +"Specifikationer" }
            h2 { +"Länkar" }

        }
    }
}



