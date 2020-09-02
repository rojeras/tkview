package tabs.rivta

//import common.libs.ItemType
//import kotlinext.js.getOwnPropertyNames
import pl.treksoft.kvision.data.BaseDataComponent
import se.skoview.app.ItemType

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
            for (domain in DomainIndex.sortedBy { it.name }) {
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

