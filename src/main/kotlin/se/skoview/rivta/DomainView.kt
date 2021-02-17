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

import pl.treksoft.kvision.core.* // ktlint-disable no-wildcard-imports
import pl.treksoft.kvision.form.select.simpleSelectInput
import pl.treksoft.kvision.html.* // ktlint-disable no-wildcard-imports
import pl.treksoft.kvision.panel.SimplePanel
import pl.treksoft.kvision.panel.hPanel
import pl.treksoft.kvision.panel.simplePanel
import pl.treksoft.kvision.table.* // ktlint-disable no-wildcard-imports
import pl.treksoft.kvision.utils.px
import pl.treksoft.kvision.utils.vw
import se.skoview.app.RivManager
import se.skoview.app.formControlXs
import se.skoview.model.* // ktlint-disable no-wildcard-imports

fun Container.domainView(state: RivState) {

    println("In domainView")

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

            h3 { +"Mera information om denna tjänstedomän" }
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
                if (!selectedDomain.infoPageUrl.isNullOrEmpty()) li {
                    link(
                        "Release notes",
                        selectedDomain.infoPageUrl
                    )
                }
            }
        }

        if (state.selectedDomainVersion == null) {
            val domainVersion = updateDomainVersion(state, selectedDomain)
            if (domainVersion != null) RivManager.selectDomainVersion(domainVersion)
            h3 { +"Inga versioner av denna domän är tillgänglig" }
            return@div
        }

        val selectedDomainVersion = state.selectedDomainVersion
        val noOfVersions = mkFilteredDomainVersionsList(state, selectedDomain).size
        simplePanel {
            marginLeft = 15.px
            marginRight = 15.px
            // background = Background(Color.hex(0xf8ffff))
            border = Border(1.px, BorderStyle.SOLID)
            simplePanel {
                margin = 5.px

                when (noOfVersions) {
                    0 -> h3 { +"Inga versioner av denna domän är tillgänglig" }
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
                        if (selectedDomainVersion.zipUrl.isNotEmpty())
                            li { link("Releasepaket (zip-fil)", selectedDomainVersion.zipUrl) }
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

private class SelectDomainVersion(state: RivState, domain: ServiceDomain?) : SimplePanel() {
    init {
        requireNotNull(domain)

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
