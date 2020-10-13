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
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import se.skoview.app.getAsync
import kotlin.collections.List

@Serializable
data class SearchResult(val total_count: Int, val incomplete_results: Boolean)

val DomainArr = mutableListOf<ServiceDomain>()
val DomainMap = mutableMapOf<String, ServiceDomain>()

fun load(callback: () -> Unit) {
    // fun load(callback: () -> Unit) {
    println("In DomDb:load()")

    // val url = "http://api.ntjp.se/dominfo/v1/servicedomain.json"
    // val url = "http://ind-dtjp-apache-api-vip.ind1.sth.basefarm.net/dominfo/v1/servicedomains.json"
    //val url = "http://localhost:4000/domdb-prod-2020-10-12.json"
    val url = "domdb-prod-2020-10-12.json"

    // Older version which I try again to get it to create the actual parsed objects
    getAsync(url) { response ->
        println("Size of response is: ${response.length}")
        val json = Json { allowStructuredMapKeys = true }
        val serviceDomains: List<ServiceDomain> =
            json.decodeFromString(ListSerializer(ServiceDomain.serializer()), response)
        console.log(serviceDomains)

        callback()
    }
}

@Serializable
data class ServiceDomain(
    val name: String,
    val description: String = "",
    val swedishLong: String = "",
    val swedishShort: String = "",
    val owner: String? = null,
    val hidden: Boolean,
    val domainType: DomainType? = null,
    val issueTrackerUrl: String? = null,
    val sourceCodeUrl: String? = null,
    val infoPageUrl: String? = null,
    val interactions: Array<Interaction>? = null, //  = arrayOf<Interaction>(),
    val serviceContracts: List<Contract>? = null, //  = listOf<Contract>(),
    val versions: Array<Version>? = null
) {
    init {
        if (
            interactions != null &&
            serviceContracts != null &&
            versions != null

        ) {
            DomainMap[this.name] = this
            DomainArr.add(this)
        } else println("$name is incomplete and removed")
    }
}

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

@Serializable
data class Interaction(
    val name: String,
    val namespace: String,
    val rivtaProfile: String,
    val major: Int,
    val minor: Int = 0,
    val responderContract: Contract,
    val initiatorContract: Contract? = null,
    val interactionDescriptions: Array<InteractionDescription> = arrayOf<InteractionDescription>()
)

@Serializable
data class Version(
    val name: String,
    val sourceControlPath: String = "",
    val documentsFolder: String = "",
    val interactionsFolder: String = "",
    val zipUrl: String = "",
    val hidden: Boolean,
    val descriptionDocuments: List<DescriptionDocument> = listOf<DescriptionDocument>(),
    val interactionDescriptions: Array<InteractionDescription> = arrayOf<InteractionDescription>(),
    val reviews: List<Review> = listOf<Review>()
)

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

@Serializable
data class DescriptionDocument(
    val fileName: String,
    /*
    @Serializable(with = DateSerializer::class)
    val lastChangedDate: Date? = null,
    */
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

@Serializable
data class InteractionDescription(
    val description: String = "",
    /*
    @Serializable(with = DateSerializer::class)
    val lastChangedDate: Date? = null,
    */
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

@Serializable
data class Review(
    val reviewProtocol: ReviewProtocol,
    val reviewOutcome: ReviewOutcome,
    val reportUrl: String = ""
)

@Serializable
data class ReviewProtocol(
    val name: String,
    val code: String
)

@Serializable
data class ReviewOutcome(
    val name: String,
    val symbol: String
)

/*
@Serializer(forClass = DateSerializer::class)
object DateSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("DateSerializer", StringDescriptor.kind as PrimitiveKind)

    override fun serialize(encoder: Encoder, obj: Date) {
        encoder.encodeString(obj.getDate().toString())
    }

    override fun deserialize(decoder: Decoder): Date {
        return Date(decoder.decodeString())
    }
}
*/

enum class DomainTypeEnum {
    NATIONAL,
    APPLICATION_SPECIFIC,
    EXTERNAL,
    UNKNOWN
}

enum class RivDocumentTypeEnum {
    TKB,
    AB,
    IS,
    UNKNOWN
}

fun ServiceDomain.getDescription(): String {
    return this.description ?: ""
}

fun ServiceDomain.getDomainType(): DomainTypeEnum {
    if (this.domainType != null) return this.domainType.type
    else {
        // Know interactions != null due to filter in ServiceDomain init()
        if (this.interactions!![0] == null) return DomainTypeEnum.UNKNOWN

        val namespaceTypePrefix = this.interactions[0].namespace.split(":")[1]

        return when (namespaceTypePrefix) {
            "riv" -> DomainTypeEnum.NATIONAL
            "riv-application" -> DomainTypeEnum.APPLICATION_SPECIFIC
            else -> DomainTypeEnum.EXTERNAL
        }
    }
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

fun Version.getDocumentsAndChangeDate(): List<DescriptionDocument> {
    return this.descriptionDocuments.toList()
}
