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
import pl.treksoft.kvision.form.select.simpleSelectInput
import pl.treksoft.kvision.html.*
import pl.treksoft.kvision.modal.Modal
import pl.treksoft.kvision.panel.SimplePanel
import pl.treksoft.kvision.state.bind
import se.skoview.app.formControlXs
import se.skoview.app.store
import se.skoview.model.*


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

                val domainTypeText: String = "${Texts.domainTypeText[domain.getDomainType()]} tjänstedomän"
                val domainTypeAltText: String = "${Texts.domainTypeAltText[domain.getDomainType()]}"

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


            if (domain!!.versions == null) h3 { "Ingen information om versioner av tjänstedomäner är tillgänga" }
            else {

                span { +"Välj version av domän" }
                val options = domain.versions
                    //.filterNot { it.hidden }
                    //.filterNot { it.name.contains("_") || it.name.contains("RC") }
                    //.filterNot { it.name.contains("trunk") }
                    .sortedBy { it.name }
                    .reversed()
                    .map { Pair(it.name, it.name) }

                if (state.selectedDomainVersion == null) store.dispatch(RivAction.SelectDomainVersion(options[0].first))

                simpleSelectInput(
                    options = options,
                    value = state.selectedDomainVersion,
                ) {
                    fontWeight = FontWeight.BOLD
                    addCssStyle(formControlXs)
                }.onEvent {
                    change = {
                        val selected: String = self.value ?: ""
                        store.dispatch(RivAction.SelectDomainVersion(domainVersion = selected))
                    }
                }

                val domainVersion = domain.versions.filter { it.name == state.selectedDomainVersion }
                h3 { +"Specifikationer" }

                println("Will fetch docs:")
                val docs = domainVersion[0].getDocumentsAndChangeDate()
                console.log(docs)

                ul {
                    li {
                        //domainVersion.
                    }
                }

                h3 { +"Tjänstekontrakt" }
                println("Time to show domain versions")

                ul {
                    domainVersion.map { version: Version ->
                        version.interactionDescriptions.map {
                            li { +"${it.wsdlContract().first} ${it.wsdlContract().second}.${it.wsdlContract().third}" }
                        }
                    }
                }
                h3 { +"Länkar" }
            }
        }
    }
}



