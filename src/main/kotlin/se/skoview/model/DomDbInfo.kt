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

package se.skoview.model

import kotlinx.serialization.*
import se.skoview.app.getAsync
import kotlin.collections.List

@Serializable
data class Query(val q: String?)

@Serializable
data class SearchResult(val total_count: Int, val incomplete_results: Boolean)

var DomainArr = arrayOf<ServiceDomain>()
val DomainMap = mutableMapOf<String, ServiceDomain>()

fun load(callback: () -> Unit) {
    //fun load(callback: () -> Unit) {
    println("In DomDb:load()")

    //val url = "http://api.ntjp.se/dominfo/v1/servicedomain.json"
    val url = "http://ind-dtjp-apache-api-vip.ind1.sth.basefarm.net/dominfo/v1/servicedomains.json"
    //val url = "http://localhost:4000/domdb.json"

    getAsync(url) { response ->
        println("Size of response is: ${response.length}")
        val serviceDomains =
            JSON.parse<Array<ServiceDomain>>(response)
        console.log(serviceDomains)
        DomainArr = serviceDomains

        // Populate the domain map
        for (domain in DomainArr) {
            DomainMap[domain.name] = domain
        }

        callback()
    }
}

@Serializable
data class ServiceDomain(
    val name: String,
    val description: String, // = "-",
    val swedishLong: String = "",
    val swedishShort: String = "",
    val owner: String = "",
    val hidden: Boolean,
    val issueTrackerUrl: String = "",
    val sourceCodeUrl: String = "",
    val infoPageUrl: String = "",
    val interactions: Array<Interaction> = arrayOf<Interaction>(),
    val serviceContracts: List<Contract> = listOf<Contract>(),
    val versions: Array<Version> = arrayOf()
)

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
)

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

fun ServiceDomain.getDescription(): String {
    return this.description ?: "---"
}

enum class DomainType {
    NATIONAL,
    APPLICATION_SPECIFIC,
    EXTERNAL
}

fun ServiceDomain.getDomainType(): DomainType? {
    //println("In getDomainType(), domain: ")
    //if (this.interactions == null) {
    requireNotNull (this.interactions) {
        println("ERROR, $name does not contain any interactions!!")
        console.log(this)
        return null
    }

    val namespaceTypePrefix = this.interactions[0].namespace.split(":")[1]
    return when (namespaceTypePrefix) {
        "riv" -> DomainType.NATIONAL
        "riv-application" -> DomainType.APPLICATION_SPECIFIC
        else -> DomainType.EXTERNAL
    }
}

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

fun Version.getDocumentsAndChangeDate(): List<Triple<String, String, String>> {
    val pathToDocs = "$sourceControlPath/$documentsFolder/"
    val respons = mutableListOf<Triple<String, String, String>>()
    for (doc in this.descriptionDocuments) {
        respons.add(Triple(doc.documentType, pathToDocs + doc.fileName, doc.lastChangedDate) as Triple<String, String, String>)
    }
    return respons
}
