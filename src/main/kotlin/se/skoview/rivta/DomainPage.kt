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
import pl.treksoft.kvision.panel.hPanel
import pl.treksoft.kvision.panel.simplePanel
import pl.treksoft.kvision.state.bind
import pl.treksoft.kvision.table.*
import pl.treksoft.kvision.utils.px
import pl.treksoft.kvision.utils.vw
import se.skoview.app.formControlXs
import se.skoview.app.store
import se.skoview.model.*

object DomainPage : SimplePanel() {

    init {
        width = 100.vw
        background = Background(Color.name(Col.WHITE))
        div { }.bind(store) { state ->
            background = Background(Color.name(Col.LIGHTGRAY))
            overflow = Overflow.INITIAL
            println("In DomainPage")

            // val selectedDomain = state.selectedDomain
            // val domain = DomainMap[selectedDomain]

            val domain = state.selectedDomain
            requireNotNull(domain)
            val selectedDomain = domain.name

            val domainType = domain.getDomainType()

            println("Display domain: $selectedDomain")
            console.log(domain)

            val selectedDomainVersion = state.selectedDomainVersion

            if (selectedDomainVersion == null) {
                h1 {
                    align = Align.CENTER
                    +"${domain.name} - inga versioner är tillgängliga"
                }
                return@bind
            }

            simplePanel {
                marginLeft = 15.px
                h1 {
                    align = Align.CENTER
                    +domain.name
                }
                h3 { +"Beskrivning" }
                //  span { +domainDescription }
                p { +domain.getDescription() }

                h3 { +"Domäntyp" }

                val domainTypeText: String = "${Texts.domainTypeText[domain.getDomainType()]} tjänstedomän"
                val domainTypeAltText: String = "${Texts.domainTypeAltText[domain.getDomainType()]}"

                val modal = Modal(domainTypeText)
                modal.add(span(domainTypeAltText))
                modal.addButton(
                    Button("Stäng").onClick {
                        modal.hide()
                    }
                )

                div {
                    p { +domainTypeText }
                    color = Color.hex(0x008583)
                }.onClick { // .onEvent { mouseover = { modal.show() }
                    modal.show()
                }

                if (!domain.owner.isNullOrEmpty()) {
                    h3 {
                        +"Ägare"
                    }
                    span { +"${domain.owner}" }
                }
                p { +" " }

                h3 { +"Mera information om denna tjänstedomän" }
                ul {
                    if (!domain.issueTrackerUrl.isNullOrEmpty()) li { link("Ärendehantering", domain.issueTrackerUrl) }
                    if (!domain.sourceCodeUrl.isNullOrEmpty()) li { link("Källkod", domain.sourceCodeUrl) }
                    if (!domain.infoPageUrl.isNullOrEmpty()) li { link("Ytterligare information", domain.infoPageUrl) }
                }
            }

            simplePanel {
                background = Background(Color.name(Col.WHITE))
                p { " " }
                hPanel {
                    h2 {
                        +"Välj version:"
                    }
                    add(
                        SelectDomainVersion(state.selectedDomain)
                            .apply {
                                width = 150.px
                                marginLeft = 50.px
                                fontWeight = FontWeight.BOLD
                            }
                    )
                    span {
                        marginLeft = 20.px
                        fontSize = 20.px
                        +" (${mkFilteredDomainVersionsList(state, domain).size})"
                    }
                }

                h3 { +"Tjänstekontrakt" }

                println("Time to show domain versions")

                table(
                    listOf("Namn", "Beskrivning"),
                    setOf(TableType.BORDERED, TableType.SMALL, TableType.STRIPED, TableType.HOVER),
                    // responsiveType = ResponsiveType.RESPONSIVE
                ) {
                    selectedDomainVersion.interactionDescriptions
                        .sortedBy { it.wsdlContract().first }
                        .map {
                            row {
                                cell {
                                    +"${it.wsdlContract().first} ${it.wsdlContract().second}.${it.wsdlContract().third}"
                                }
                                cell {
                                    +it.description
                                }
                            }
                        }
                }

                h3 { +"Specifikationer" }

                if (domain.sourceCodeUrl != null) {

                    println("Will fetch docs: ${selectedDomainVersion.name}")

                    // val docs = selectedDomainVersion.getDocumentsAndChangeDate()

                    val baseUrl = "${
                    domain.sourceCodeUrl.replace(
                        "src",
                        "raw"
                    )
                    }/${selectedDomainVersion.name}/${selectedDomainVersion.documentsFolder}/"

                    println("Documentation url: $baseUrl")
                    val documents: List<DescriptionDocument> =
                        selectedDomainVersion.descriptionDocuments as List<DescriptionDocument>
                    console.log(documents)
                    ul {
                        documents
                            .sortedBy { it.type }
                            .map {
                                li {
                                    val displayName = when (it.type) {
                                        RivDocumentTypeEnum.TKB -> "Tjänstekontraktsbeskrivning"
                                        RivDocumentTypeEnum.AB -> "Arkitekturella beslut"
                                        RivDocumentTypeEnum.IS -> "Informationsspecifikation"
                                        else -> it.fileName
                                    }
                                    link(displayName, "$baseUrl${it.fileName}")
                                }
                            }
                        if (!selectedDomainVersion.zipUrl.isNullOrEmpty())
                            li { link("Releasepaket (zip-fil)", selectedDomainVersion.zipUrl) }
                    }
                }
                h3 { +"Granskningar" }

                if (selectedDomainVersion.reviews.isEmpty()) span { +"Inga granskningar är registrerade för denna version." }
                else {
                    table(
                        listOf("", "Resultat", "Mera information"),
                        setOf(TableType.BORDERED, TableType.SMALL, TableType.STRIPED, TableType.HOVER),
                        // responsiveType = ResponsiveType.RESPONSIVE
                    ) {
                        for (review in selectedDomainVersion.reviews) {
                            console.log(review)
                            row {
                                cell { +review.reviewProtocol.name }
                                cell {
                                    color = when (review.reviewOutcome.name) {
                                        "Godkänd" -> Color.name(Col.GREEN)
                                        "Underkänd" -> Color.name(Col.RED)
                                        "Delvis Godkänd" -> Color.name(Col.BLACK)
                                        else -> Color.name(Col.BLACK)
                                    }
                                    bold { +review.reviewOutcome.name }
                                }
                                cell { link("Ladda ner granskningsprotokoll", review.reportUrl) }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getDefaultDomainVersion(state: RivState, domain: ServiceDomain): Version? {
    // versions will be sorted with higher version numbers first
    val versions = mkFilteredDomainVersionsList(state, domain)

    // Go through list and try to find version which is not RC (contains an "_") nor trunk - return first one
    for (version in versions) {
        if (
            !version.name.contains("trunk") &&
            !version.name.contains("_")
        )
            return version
    }

    // Go through list and try to find version which is not trunk - return first one
    for (version in versions) {
        if (
            !version.name.contains("trunk")
        )
            return version
    }

    // If still no hit just return the first one - which ought to be a sinlge trunk
    if (versions.isEmpty()) return null

    return versions[0]
}

fun mkFilteredDomainVersionsList(state: RivState, domain: ServiceDomain): List<Version> {
    return if (domain.versions == null)
        listOf()
    else
        domain.versions
            .filter { state.showHiddenVersion || !it.hidden }
            .filter { state.showRcVersion || !it.name.contains("RC") }
            .filter { state.showTrunkVersion || !it.name.contains("trunk") }
            .sortedBy { it.name }
            .reversed()
}

private class SelectDomainVersion(domain: ServiceDomain?) : SimplePanel() {
    init {
        requireNotNull(domain)

        val state = store.getState()

        val versions = mkFilteredDomainVersionsList(state, domain)
        val options = versions.map { Pair(it.name, it.name) }

        // if (state.selectedDomainVersion == null) store.dispatch(RivAction.SelectDomainVersion(versions[0]))

        val value =
            if (state.selectedDomainVersion == null) ""
            else state.selectedDomainVersion.name

        simpleSelectInput(
            options = options,
            value = value
        ) {
            background = Background(Color.name(Col.WHITE))
            fontSize = 20.px
            addCssStyle(formControlXs)
        }.onEvent {
            change = {
                val selected: String = self.value ?: ""
                val selectedVersion = versions.filter { it.name == selected }[0]
                store.dispatch(RivAction.SelectDomainVersion(domainVersion = selectedVersion))
            }
        }
    }
}
