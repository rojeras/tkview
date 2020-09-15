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

package se.skoview.bucket

import pl.treksoft.kvision.data.BaseDataComponent
import se.skoview.app.ItemType
import se.skoview.rivta.DomainArr
import se.skoview.rivta.Version

object DInfo : BaseDataComponent() {
    class ShowDomDb(
        val type: ItemType,
        val domainAndContract: String,
        val tooltip: String,
        val iReview: String = "",
        val sReview: String = "",
        val tReview: String = "",
        val hippoUrl: String = "",
        val sourceCodeUrl: String = ""
    )

    val showDomDbs: MutableList<ShowDomDb> = mutableListOf<ShowDomDb>()

    fun view(callback: () -> Unit) {
        //for (domain in ServiceDomain.serviceDomains.sortedBy { it.name }) {
        DomainArr.sortedBy { it.name }.forEach { domain ->
            println(domain.name)

            // Find the highest version which is not an RC
            val filteredVersion =
                domain.versions
                    .sortedByDescending { it.name }
                    .filterNot { it.name.contains("RC") }
                    .filterNot { it.name.contains("_") }
                    .filterNot { it.name.contains("trunk") }

            /*
                for (version in domain.versions
                    .sortedByDescending { it.name }
                    .filterNot { it.name.contains("RC") }
                    .filterNot { it.name.contains("_") }
                    .filterNot { it.name.contains("trunk") }
                )

                 */

            if (filteredVersion.size > 0) {
                val currentVersion: Version = filteredVersion[0]
                var iReviewResult = ""
                var sReviewResult = ""
                var tReviewResult = ""
                for (review in currentVersion.reviews) {
                    when (review.reviewProtocol.code) {
                        "aor-i" -> iReviewResult = review.reviewOutcome.name
                        "aor-s" -> sReviewResult = review.reviewOutcome.name
                        "aor-t" -> tReviewResult = review.reviewOutcome.name
                    }
                }

                showDomDbs.add(
                    ShowDomDb(
                        ItemType.DOMAIN,
                        "${domain.swedishShort}<br>${domain.name} ${currentVersion.name}",
                        domain.description,
                        iReviewResult,
                        sReviewResult,
                        tReviewResult,
                        "https://integrationer.tjansteplattform.se/?filter=C40F5L5",
                        domain.sourceCodeUrl
                    )
                )

                println(currentVersion.name)
                for (interactionDescription in currentVersion.interactionDescriptions.sortedBy { it.wsdlFileName }) {
                    //println(interactionDescription.wsdlFileName)
                    // todo: This should change to not use name of WSDL file, but find an (all?) interactions with
                    // this interactionDescription (refer to the DB schema)
                    for (interaction in domain.interactions) {
                        for (description in interaction.interactionDescriptions) {
                            println("in loop")
                            if (description == interactionDescription) {
                                // Found
                                println("Found description: ${interaction.namespace}")
                                val name = interaction.name
                                val major = interaction.major
                                val minor = interaction.minor
                                showDomDbs.add(
                                    ShowDomDb(
                                        ItemType.CONTRACT,
                                        //"${contract.name} v${contract.major}.${contract.minor}",
                                        "$name v$major.$minor",
                                        interactionDescription.description,
                                        ""
                                    )
                                )

                            }
                        }
                    }

                    //val (name, major, minor) = interactionDescription.wsdlContract
                    //println("$name v$major.$minor")

                }
            }
        }

        println("showDomDbs:")
        console.log(showDomDbs)

        callback()
    }
}

