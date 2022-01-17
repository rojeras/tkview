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

@file:Suppress("SENSELESS_COMPARISON")

package se.skoview.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import se.skoview.controller.RivManager
import se.skoview.controller.getAsync
import se.skoview.controller.getBaseUrl
import kotlin.collections.List

/**
 * Global array of domains.
 */
val DomainArr = mutableListOf<ServiceDomain>()

/**
 * Global map of domains. Domain name (namespace) is used for key.
 */
val DomainMap = mutableMapOf<String, ServiceDomain>()

/**
 * Read and and parse all domain info from DOMDB. [kotlinx.serialization] is used to parse the JSON response to
 * Kotlin objects. The classes contains init-blocks which store the information in different collections.
 */
fun domdbLoad() {
    // fun load(callback: () -> Unit) {
    println("In DomDb:load()")

    // val url = "${getBaseUrl()}/http://qa.api.ntjp.se/dominfo/v1/servicedomains.json"
    val url = "${getBaseUrl()}/http://api.ntjp.se/dominfo/v1/servicedomains.json"

    // Older version which I try again to get it to create the actual parsed objects
    getAsync(url) { response ->
        println("Size of domdb response is: ${response.length}")
        val json = Json { allowStructuredMapKeys = true }
        // val serviceDomains: List<ServiceDomain> = json.decodeFromString(ListSerializer(ServiceDomain.serializer()), response)

        val domDb: DomDb = json.decodeFromString(DomDb.serializer(), response)

        console.log(domDb)
        console.log(DomainArr)

        RivManager.domdbLoadingComplete()
    }
}

/**
 * Top DOMDB cache respons object. The cache adds a structre with two elements, a list of domains and timestamp when the cache was last updated.
 */
@Serializable
data class DomDb(
    val answer: List<ServiceDomain>,
    val lastChangeTime: String
) {
    init {
        lastUpdateTime = lastChangeTime
    }

    companion object {
        lateinit var lastUpdateTime: String
    }
}

/**
 *
 * Object specification based on output from DOMDB-api. The init block verifies the domain and store them in the global map and array.
 *
 * @property name Service domain name (namespace).
 * @property description Description.
 * @property swedishLong The long Swedish name.
 * @property swedishShort The short Swedish name
 * @property owner Owner of the domain.
 * @property hidden Flag to indicate that information about the domain is not public.
 * @property domainType Type of domains.
 * @property issueTrackerUrl URL to the <i>Issues</i> page in the Bitbucket repo for this domain.
 * @property sourceCodeUrl URL to the source code in bitbucket.
 * @property infoPageUrl URL to the Release Notes for the domain.
 * @property interactions Array the [Interaction]s in this domain.
 * @property serviceContracts List of the [Contract]s in this domain
 * @property versions Array of the [Version]s in this domain.
 * @constructor Create empty Service domain
 */
@Serializable
data class ServiceDomain(
    val name: String,
    val description: String = "",
    val swedishLong: String = "",
    val swedishShort: String = "",
    val owner: String? = null,
    val hidden: Boolean,
    val domainType: DomainType,
    val issueTrackerUrl: String? = null,
    val sourceCodeUrl: String? = null,
    val infoPageUrl: String? = null,
    val interactions: Array<Interaction>? = null, //  = arrayOf<Interaction>(),
    val serviceContracts: List<Contract>? = null, //  = listOf<Contract>(),
    val versions: Array<Version>? = null,
) {
    var domainTypeString: String? = null // Used for filtering in tabulator

    /**
     * The init block verifies and store the accepted domains.
     */
    init {
        if (
            interactions != null &&
            serviceContracts != null &&
            versions != null
        ) {
            DomainMap[this.name] = this
            DomainArr.add(this)
            domainTypeString = domainType.name
        } else {
            if (!this.name.isBlank()) println("${this.name} is incomplete and removed")
        }
    }
}

/**
 * Object specification based on output from DOMDB-api. Domain type is translated from string to type.
 *
 * @property name A string representing the domain type.
 * @property type Codified type.
 * @constructor Create empty Domain type
 */
@Serializable
data class DomainType(
    val name: String
) {
    val type: DomainTypeEnum
        get() = when (name) {
            "Nationell tjänstedomän" -> DomainTypeEnum.NATIONAL
            "Applikationsspecifik tjänstedomän" -> DomainTypeEnum.APPLICATION_SPECIFIC
            "Extern tjänstedomän" -> DomainTypeEnum.EXTERNAL
            else -> DomainTypeEnum.UNKNOWN
        }
}

/**
 * Object specification based on output from DOMDB-api.
 *
 * @property name
 * @property namespace
 * @property rivtaProfile
 * @property major
 * @property minor
 * @property responderContract
 * @property initiatorContract
 * @property interactionDescriptions
 * @constructor Create empty Interaction
 */
@Serializable
data class Interaction(
    var name: String,
    val namespace: String,
    val rivtaProfile: String,
    val major: Int,
    val minor: Int = 0,
    val responderContract: Contract,
    val initiatorContract: Contract? = null,
    val interactionDescriptions: Array<InteractionDescription> = arrayOf<InteractionDescription>()
) {
    // Fix of spelling error in init.
    init {
        name = if (name == "GetLaboratoryOrderOutcomenteraction") "GetLaboratoryOrderOutcomeInteraction"
        else name
    }
}

/**
 * Version information.
 *
 * @property name Name of version. Often the same as the tag in bitbucket, but not always.
 * @property sourceControlPath URL to source code in Bitbucket.
 * @property documentsFolder URL to document folder in Bitbucket.
 * @property interactionsFolder URL to interactions folder in Bitbucket.
 * @property zipUrl URL to zip package of the domain version.
 * @property hidden Flag to indicate that this version is not public.
 * @property descriptionDocuments List of [DescriptionDocument]s.
 * @property interactionDescriptions Array of [InteractionDescription]s.
 * @property reviews List of [Review]s.
 * @constructor Create empty Version
 */
@Serializable
data class Version(
    val name: String,
    val sourceControlPath: String = "",
    val documentsFolder: String = "",
    val interactionsFolder: String = "",
    var zipUrl: String = "",
    val hidden: Boolean,
    val descriptionDocuments: List<DescriptionDocument> = listOf<DescriptionDocument>(),
    val interactionDescriptions: Array<InteractionDescription> = arrayOf<InteractionDescription>(),
    val reviews: List<Review> = listOf<Review>()
) {
    init {
        // zap to change http to https
        if (zipUrl.startsWith("http://rivta.se"))
            zipUrl = zipUrl.replace("http://rivta.se", "https://rivta.se")
    }
}

/**
 * Object specification based on output from DOMDB-api. All contracts are stored in separate set.
 *
 * @property name Name of service contract (ending with "Responder").
 * @property major Major version.
 * @property minor Minor version.
 * @property namespace Contract name space.
 * @constructor Create empty Contract
 */
@Serializable
data class Contract(
    val name: String,
    val major: Int,
    var minor: Int = 0,
    val namespace: String
) {
    init {
        listBucket.add(this)
    }

    companion object {
        val listBucket: MutableSet<Contract> = mutableSetOf<Contract>()
    }
}

/**
 * Object specification based on output from DOMDB-api
 *
 * @property fileName Name of the document file.
 * @property lastChangedDate Last update time of the file.
 * @property documentType Type of document.
 * @constructor Create empty Description document
 */
@Serializable
data class DescriptionDocument(
    val fileName: String,
    val lastChangedDate: String? = null,
    val documentType: String
) {
    val type: RivDocumentTypeEnum
        get() = when (documentType) {
            "TKB" -> RivDocumentTypeEnum.TKB
            "AB" -> RivDocumentTypeEnum.AB
            "IS" -> RivDocumentTypeEnum.IS
            else -> RivDocumentTypeEnum.UNKNOWN
        }
}

/**
 * Kotlin class representing deserialized JSON data from TAK-api.
 *
 * @property description Contract description.
 * @property lastChangedDate Time stamp.
 * @property folderName Name of interaction folder in Bitbucket.
 * @property wsdlFileName Name of contract WSDL file.
 * @constructor Create empty Interaction description
 */
@Serializable
data class InteractionDescription(
    val description: String = "",
    val lastChangedDate: String? = null,
    val folderName: String = "",
    val wsdlFileName: String
) {
    // Parse the wsdl file name to create contractName, major and minor

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is InteractionDescription) return false
        return description == other.description &&
            // Use string representation of dates to compare, otherwise always unequal
            lastChangedDate.toString() == other.lastChangedDate.toString() &&
            folderName == other.folderName &&
            wsdlFileName == other.wsdlFileName
    }
}

/**
 * Object specification based on output from DOMDB-api
 *
 * @property reviewProtocol Type of review.
 * @property reviewOutcome Result of the review.
 * @property reportUrl URL to the review protocol.
 * @constructor Create empty Review
 */
@Serializable
data class Review(
    val reviewProtocol: ReviewProtocol,
    val reviewOutcome: ReviewOutcome,
    var reportUrl: String = ""
) {
    // zap to change http to https
    init {
        if (reportUrl.startsWith("http://rivta.se"))
            reportUrl = reportUrl.replace("http://rivta.se", "https://rivta.se")
    }
}

/**
 * Object specification based on output from DOMDB-api
 *
 * @property name Reviewer name (example "Arkitektur & Regelverk: Säkerhet").
 * @property code Reviewer code (example "aor-s").
 * @constructor Create empty Review protocol
 */
@Serializable
data class ReviewProtocol(
    val name: String,
    val code: String
)

/**
 * Object specification based on output from DOMDB-api.
 *
 * @property name Review result (example "Delvis Godkänd").
 * @property symbol Review result code (example "(G)").
 * @constructor Create empty Review outcome
 */
@Serializable
data class ReviewOutcome(
    val name: String,
    val symbol: String
)

/**
 * Definition of domain types.
 */
enum class DomainTypeEnum() {
    NATIONAL(),
    APPLICATION_SPECIFIC,
    EXTERNAL,
    UNKNOWN,
}

/**
 * Definition of document types.
 */
enum class RivDocumentTypeEnum {
    TKB,
    AB,
    IS,
    UNKNOWN
}

/**
 * Extension function to extract description.
 *
 * @return Description field of [ServiceDomain]
 */
fun ServiceDomain.getDescription(): String {
    return this.description
}

/**
 * Return the three parts of a TK identifier in a Triple
 *
 * @return Triple(ContractName; String, MajorVersion: Int, MinorVersion: Int)
 *
 */
fun InteractionDescription.wsdlContract(): Triple<String, Int, Int> {
    val aList = wsdlFileName.split("_")
    val nameInteraction = aList[0]
    val name = nameInteraction.removeSuffix("Interaction")
    val version = aList[1]
    val versionList = version.split(".")
    val major = versionList[0]
    val minor = versionList[1]
    return Triple(name, major.toInt(), minor.toInt())
}
