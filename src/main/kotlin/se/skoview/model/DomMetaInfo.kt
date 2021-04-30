package se.skoview.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import se.skoview.app.getSync

fun domainMetaLoad() {
    // fun load(callback: () -> Unit) {
    println("In domainMetaLoad()")

    val json = Json { allowStructuredMapKeys = true }

    val urlMetaInfo = "all.domainmeta.json"
    val response = getSync(urlMetaInfo)
    // json.decodeFromString(DomMetaInfo.serializer(), response)
    json.decodeFromString(ListSerializer(DomainMeta.serializer()), response)

    println("DomainMeta.mapp:")
    console.log(DomainMeta.mapp)

    // RivManager.domdbLoadingComplete()
}

@Serializable
data class DomainMeta(
    val name: String,
    val description: String,
    val swedishLong: String,
    val swedishShort: String,
    val owner: String? = null,
    val domainType: String,
    val releaseNotesUrl: String?,
    val domainVersions: List<DomainVersion>?
) {
    init {
        mapp[name] = this
    }
    companion object {
        val mapp: MutableMap<String, DomainMeta> = mutableMapOf()
    }
}

@Serializable
data class DomainVersion(
    val tag: String,
    val S_review: String?,
    val I_review: String?,
    val T_review: String?
)