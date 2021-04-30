/**
 * Copyright (C) 2021 Lars Erik Röjerås
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

/**
 * Populate TkView objects from Bitbucket, Meta (replaces DOMDB) and rules.
 */
fun mkTkViewInfo() {
    println("In mkTkViewInfo()")
    for (bbDomain in BbDomain.mapp) {
        // If there is a domain_name.domainmeta.json in the repo - fetch it
        // And replace/add it to the domainmeta array

        val domainMeta = DomainMeta.mapp[bbDomain.key]

        if (domainMeta?.domainVersions == null) {
            println("No visible version for ${bbDomain.value.name}")
            continue
        }

        val domainName = bbDomain.value.name

        val domainType: TkvDomainTypeEnum = when (domainMeta.domainType) {
            "Extern" -> TkvDomainTypeEnum.EXTERNAL
            "Nationell tjänstedomän" -> TkvDomainTypeEnum.NATIONAL
            "Applikationsspecifik tjänstedomän" -> TkvDomainTypeEnum.APPLICATION_SPECIFIC
            else -> TkvDomainTypeEnum.UNKNOWN
        }

        val owner =
            if (!domainMeta.owner.isNullOrBlank())
                StringWithSource(domainMeta.owner, TkvSourceEnum.COMMON_META_DATA)
            else null

        val domainBaseUrl = "https://bitbucket.org/rivta-domains/${domainName.replace(":", ".")}"

        // https://bitbucket.org/rivta-domains/riv.clinicalprocess.activity.actions/issues
        val issuesUrl = if (bbDomain.value.has_issues)
            StringWithSource(
                "$domainBaseUrl/issues",
                TkvSourceEnum.CALCULATED
            )
        else null

        val sourceUrl = StringWithSource("$domainBaseUrl/src/master/", TkvSourceEnum.CALCULATED)

        val releaseNotesUrl =
            if (!domainMeta.releaseNotesUrl.isNullOrEmpty())
                StringWithSource(
                    domainMeta.releaseNotesUrl,
                    TkvSourceEnum.COMMON_META_DATA
                )
            else null

/*
To get the source of a certain tag
1. Fetch the tag: https://api.bitbucket.org/2.0/repositories/rivta-domains/riv.clinicalprocess.logistics.logistics/refs/tags/3.0.7_RC1
2. Read the hash = target.hash
3. Get source: https://api.bitbucket.org/2.0/repositories/rivta-domains/riv.clinicalprocess.logistics.logistics/src/$hash/ -- VERY IMPORTANT that this URL ends with a slash
 */

        TkvDomain(
            name = StringWithSource(domainName, TkvSourceEnum.BITBUCKET),
            swedishShortName = StringWithSource(domainMeta.swedishShort, TkvSourceEnum.COMMON_META_DATA),
            swedishLongName = StringWithSource(domainMeta.swedishLong, TkvSourceEnum.COMMON_META_DATA),
            domainType = domainType,
            description = StringWithSource(domainMeta.description, TkvSourceEnum.COMMON_META_DATA),
            owner = owner,
            issuesUrl = issuesUrl,
            sourceUrl = sourceUrl,
            releaseNotesUrl = releaseNotesUrl,
            bbiUid = bbDomain.value.uuid,
            // todo: Empty hippoUrl should be changed to null
            hippoUrl = StringWithSource(mkHippoDomainUrl(domainName), TkvSourceEnum.TPDB),
            versions = null
        )
    }
    println("All TkvDomains")
    console.log(TkvDomain.mapp)
}

enum class TkvDomainTypeEnum {
    NATIONAL,
    EXTERNAL,
    APPLICATION_SPECIFIC,
    UNKNOWN
}

enum class TkvSourceEnum {
    DOMDB,
    BITBUCKET,
    DOMAIN_META_DATA,
    COMMON_META_DATA,
    TPDB,
    CALCULATED
}

data class StringWithSource(
    val str: String,
    val source: TkvSourceEnum
) {
    override fun toString(): String = "$str [from $source]"
}

data class TkvDomain(
    val name: StringWithSource,
    val swedishShortName: StringWithSource,
    val swedishLongName: StringWithSource,
    val domainType: TkvDomainTypeEnum,
    val description: StringWithSource,
    val owner: StringWithSource?,
    val issuesUrl: StringWithSource?,
    val sourceUrl: StringWithSource,
    val releaseNotesUrl: StringWithSource?,
    val bbiUid: String = "",
    val hippoUrl: StringWithSource?,
    val versions: List<TkvDomainVersion>?
) {
    init {
        mapp[name] = this
    }

    companion object {
        val mapp: MutableMap<StringWithSource, TkvDomain> = mutableMapOf()
    }
}

data class TkvDomainVersion(
    val name: StringWithSource,
    val tkbUrl: StringWithSource,
    val abUrl: StringWithSource,
    val zipUrl: StringWithSource,
    val contracts: List<TkvContract>,
    val reviews: List<TkvReview>,
    val parent: TkvDomain
)

data class TkvContract(
    val name: StringWithSource,
    val domain: TkvDomain,
    val description: StringWithSource,
    val versions: List<TkvContractVersion>,
) {
    init {
        mapp[name] = this
    }

    companion object {
        val mapp: MutableMap<StringWithSource, TkvContract> = mutableMapOf()
    }
}

data class TkvContractVersion(
    val major: Int,
    val minor: Int,
    val parent: TkvContract,
    val hippoUrl: StringWithSource?,
    val partOfDomainVersions: List<TkvDomainVersion>
)

data class TkvReview(
    val area: String,
    val result: String,
    val reportUrl: String
)
