package tabs.rivta

//import common.libs.getAsync
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import pl.treksoft.kvision.utils.Object
import se.skoview.app.getAsync
import kotlin.collections.List
import kotlin.js.Date
import pl.treksoft.kvision.utils.obj
import se.skoview.app.ItemType

@Serializable
data class Query(val q: String?)

@Serializable
data class SearchResult(val total_count: Int, val incomplete_results: Boolean)

var DomainIndex = arrayOf<ServiceDomain>()

fun load(callback: () -> Unit) {
    //fun load(callback: () -> Unit) {
    println("In DomDb:load()")

    //val url = "http://api.ntjp.se/dominfo/v1/servicedomain"
    val url = "http://localhost:4000/domdb.json"

    getAsync(url) { response ->
        println("Size of response is: ${response.length}")
        val serviceDomains =
            JSON.parse<Array<ServiceDomain>>(response)
        console.log(serviceDomains)
        DomainIndex = serviceDomains

        callback()
    }
}

@Serializable
data class ServiceDomain(
    val name: String,
    val description: String = "",
    val swedishLong: String = "",
    val swedishShort: String = "",
    val owner: String = "",
    val hidden: Boolean,
    val issueTrackerUrl: String = "",
    val sourceCodeUrl: String = "",
    val infoPageUrl: String = "",
    val interactions: List<Interaction> = listOf<Interaction>(),
    val serviceContracts: List<Contract> = listOf<Contract>(),
    val versions: List<Version> = listOf()
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
    val interactionDescriptions: List<InteractionDescription> = listOf<InteractionDescription>()
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
    val interactionDescriptions: List<InteractionDescription> = listOf<InteractionDescription>(),
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
    val wsdlContract: Triple<String, Int, Int>
        get() {
            val aList = wsdlFileName.split("_")
            val nameInteraction = aList[0]
            val name = nameInteraction.removeSuffix("Interaction")
            val version = aList[1]
            val versionList = version.split(".")
            val major = versionList[0]
            val minor = versionList[1]
            return Triple(name, major.toInt(), minor.toInt())
        }

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