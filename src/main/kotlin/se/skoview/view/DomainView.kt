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

package se.skoview.view

import io.kvision.core.* // ktlint-disable no-wildcard-imports
import io.kvision.form.select.simpleSelectInput
import io.kvision.html.* // ktlint-disable no-wildcard-imports
import io.kvision.panel.SimplePanel
import io.kvision.panel.hPanel
import io.kvision.panel.simplePanel
import io.kvision.table.* // ktlint-disable no-wildcard-imports
import io.kvision.utils.px
import io.kvision.utils.vw
import kotlinx.browser.window
import se.skoview.controller.RivManager
import se.skoview.controller.formControlXs
import se.skoview.model.* // ktlint-disable no-wildcard-imports

/**
 * Domain view. Defines the page which displays a (single) domain.
 *
 * @param state
 */
fun Container.domainView(state: RivState) {

    println("In domainView with domain: ${state.selectedDomainName}")

    div {

        marginLeft = 1.vw
        marginRight = 1.vw
        width = 98.vw
        background = Background(Color.name(Col.WHITE))
        overflow = Overflow.INITIAL

        val selectedDomainName = state.selectedDomainName

        val selectedDomain = DomainMap[selectedDomainName]
        if (selectedDomain == null) return@div

        simplePanel {
            marginLeft = 15.px
            marginRight = 15.px
            h1 {
                align = Align.CENTER
                marginBottom = 40.px
                +"${selectedDomain.swedishShort} - ${selectedDomain.name}"
            }
            h3 { +"Beskrivning" }
            //  span { +domainDescription }
            p { +selectedDomain.getDescription() }

            h3 { +"Domäntyp" }

            div {
                content = "${Texts.domainTypeText[selectedDomain.domainType.type]} tjänstedomän"
                title = Texts.domainTypeAltText[selectedDomain.domainType.type]
            }

            if (!selectedDomain.owner.isNullOrEmpty()) {
                h3 {
                    +"Ägare"
                }
                span { +"${selectedDomain.owner}" }
            }
            p { +" " }
        }

        if (state.selectedDomainVersion == null) {
            val domainVersion = state.defaultDomainVersion(selectedDomain)
            if (domainVersion != null) RivManager.selectDomainVersion(domainVersion)
            h3 { +"Ingen fastställd version av denna domän finns att tillgå" }
            return@div
        }

        val selectedDomainVersion = state.selectedDomainVersion
        val noOfVersions = state.mkFilteredDomainVersionsList(selectedDomain).size
        simplePanel {
            marginLeft = 15.px
            marginRight = 15.px
            border = Border(1.px, BorderStyle.SOLID)
            simplePanel {
                margin = 5.px

                when (noOfVersions) {
                    0 -> h3 { +"Ingen fastställd version av denna domän finns att tillgå" }
                    // 1 -> h2 { +"Version ${mkFilteredDomainVersionsList(state, selectedDomain)[0].name}" }
                    else ->
                        hPanel {
                            h3 {
                                marginTop = 15.px
                                +"Välj domänversion:"
                            }
                            add(
                                SelectDomainVersion(state, selectedDomain)
                                    .apply {
                                        width = 120.px
                                        marginLeft = 50.px
                                        marginTop = 15.px
                                        fontWeight = FontWeight.BOLD
                                    }
                            )
                            div {
                                marginLeft = 20.px
                                marginTop = 20.px
                                +" ($noOfVersions)"
                            }
                        }
                }

                h4 {
                    content = "Tjänstekontrakt"
                    marginTop = 15.px
                }
                marginLeft = 15.px
                marginRight = 15.px
                table(
                    // listOf("Namn", "Version", "Beskrivning", "Senast uppdaterad", "Se anslutningar i hippo"),
                    listOf("Namn", "Version", "Beskrivning", "Se anslutningar i hippo"),
                    setOf(TableType.BORDERED, TableType.SMALL, TableType.STRIPED, TableType.HOVER),
                    // responsiveType = ResponsiveType.RESPONSIVE
                ) {
                    selectedDomainVersion.interactionDescriptions
                        .distinctBy { it.wsdlContract().first + it.wsdlContract().second + it.wsdlContract().third } // Remove duplicates, see Issue #5
                        .sortedBy { it.wsdlContract().first }
                        .map {
                            val name = it.wsdlContract().first
                            val major = it.wsdlContract().second
                            val minor = it.wsdlContract().third
                            row {
                                cell {
                                    +name
                                }
                                cell {
                                    +"$major.$minor"
                                }
                                cell {
                                    +it.description
                                }
                                /*
                                cell {
                                    if (!it.lastChangedDate.isNullOrEmpty())
                                        +it.lastChangedDate
                                }
                                 */
                                cell {
                                    val url = mkHippoContractUrl(name, major)
                                    val linkText =
                                        if (url.isNotBlank()) "<a href=\"$url\" target=\"_blank\"><img alt=\"Utforska i hippo\" src=\"/tkview/tpnet.png\" width=\"20\" height=\"20\"></a>"
                                        else ""
                                    div(
                                        rich = true,
                                        align = Align.CENTER,
                                        content = linkText
                                    ) {
                                        title = "Se anslutningar för detta kontrakt i hippo"
                                    }
                                }
                            }
                        }
                }

                h4 { +"Specifikationer" }

                if (selectedDomain.sourceCodeUrl != null) {

                    val baseUrl = "${
                        selectedDomain.sourceCodeUrl.replace(
                            "src",
                            "raw"
                        )
                    }/${selectedDomainVersion.name}/${selectedDomainVersion.documentsFolder}/"

                    val documents: List<DescriptionDocument> =
                        selectedDomainVersion.descriptionDocuments
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
                                    // +" (${it.lastChangedDate})"
                                }
                            }
                        // if (selectedDomainVersion.zipUrl.isNotEmpty()) li { link("Releasepaket (zip-fil)", selectedDomainVersion.zipUrl) }
                        if (!selectedDomain.infoPageUrl.isNullOrEmpty()) li {
                            link(
                                "Release notes",
                                selectedDomain.infoPageUrl
                            )
                        }
                    }
                }
                h4 { +"Granskningar" }

                if (selectedDomainVersion.reviews.isEmpty()) span { +"Inga granskningar är registrerade för denna version." }
                else {
                    table(
                        listOf("", "Resultat", "Mera information"),
                        setOf(TableType.BORDERED, TableType.SMALL, TableType.STRIPED, TableType.HOVER),
                    ) {
                        for (review in selectedDomainVersion.reviews) {
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
                button("Ladda ner releasepaket (zip-fil) för version ${state.selectedDomainVersion.name}")
                    .onClick {
                        // window.open("${selectedDomainVersion.zipUrl}","_blank","resizable=yes")
                        window.open("${selectedDomainVersion.zipUrl}")
                        println("Button 'Ladda ner' clicked")
                    }.apply {
                        size = ButtonSize.SMALL
                        fontFamily = "Ariel"
                        fontWeight = FontWeight.BOLD
                        addBsBgColor(BsBgColor.PRIMARY)
                        addBsColor(BsColor.WHITE)
                        marginBottom = 5.px
                        disabled = false
                    }
            }
        }

        h3 {
            content = "Mera information om denna tjänstedomän"
            marginTop = 15.px
        }
        ul {
            if (!selectedDomain.issueTrackerUrl.isNullOrEmpty()) li {
                link(
                    "Ärendehantering",
                    selectedDomain.issueTrackerUrl,

                )
            }
            if (!selectedDomain.sourceCodeUrl.isNullOrEmpty()) li {
                link(
                    "Källkod",
                    selectedDomain.sourceCodeUrl
                )
            }
        }
    }
}

/**
 * Select domain version. Panel to select a version of a domain.
 *
 * @constructor
 *
 * @param state - current state
 * @param domain - current domain
 */
private class SelectDomainVersion(state: RivState, domain: ServiceDomain?) : SimplePanel() {
    init {
        requireNotNull(domain)

        val versions = state.mkFilteredDomainVersionsList(domain)
        val options = versions.map { Pair(it.name, it.name) }

        // if (state.selectedDomainVersion == null) store.dispatch(RivAction.SelectDomainVersion(versions[0]))

        val value =
            if (state.selectedDomainVersion == null) ""
            else state.selectedDomainVersion.name

        simpleSelectInput(
            options = options,
            value = value
        ) {
            // background = Background(Color.name(Col.WHITE))
            // fontSize = 20.px
            addCssStyle(formControlXs)
        }.onEvent {
            change = {
                val selected: String = self.value ?: ""
                val selectedVersion = versions.filter { it.name == selected }[0]
                RivManager.selectDomainVersion(domainVersion = selectedVersion)
            }
        }
    }
}
