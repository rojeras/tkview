package se.skoview.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import se.skoview.app.getSync

fun domainMetaLoad() {
    // fun load(callback: () -> Unit) {
    println("In domainMetaLoad()")

    val json = Json { allowStructuredMapKeys = true }

    val urlMetaInfo = "domain.meta.json"
    val response = getSync(urlMetaInfo)
    val domainMeta: DomMetaInfo = json.decodeFromString(DomMetaInfo.serializer(), response)

    println("DomainMeta.mapp:")
    console.log(DomainMeta.mapp)

    // RivManager.domdbLoadingComplete()
}

@Serializable
data class DomMetaInfo(
    val domain_meta: List<DomainMeta>
)

@Serializable
data class DomainMeta(
    val name: String,
    val description: String,
    val swedishLong: String,
    val swedishShort: String,
    val owner: String,
    val domainType: String,
    val domainVersions: List<DomainVersion>
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
    val visible: Boolean,
    val S_review: String,
    val I_review: String,
    val T_review: String
)