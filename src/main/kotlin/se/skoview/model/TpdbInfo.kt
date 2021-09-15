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

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import se.skoview.controller.RivManager
import se.skoview.controller.getAsync
import se.skoview.controller.getBaseUrl

/**
 * Tpdb service contract dto. A wrapper for the answer from the server cache.
 *
 * @property answer A wrapper list of all [TpdbServiceContract]s the TPDB-api returns.
 * @property lastChangeTime Last time the cache was updated.
 * @constructor Create empty Tpdb service contract dto
 */
@Serializable
data class TpdbServiceContractDto(
    val answer: List<TpdbServiceContract>,
    val lastChangeTime: String
) {
    init {
        lastUpdateTime = lastChangeTime
    }

    /**
     * Companion. Singleton string with timestamp information.
     *
     * @constructor Create empty Companion
     */
    companion object {
        lateinit var lastUpdateTime: String
    }
}

/**
 * Tpdb service contract
 *
 * @property id API id.
 * @property serviceDomainId The id of the service domain this contract belong.
 * @property name
 * @property namespace
 * @property major
 * @property synonym A synonym which optionally can be used in the Statistics application
 * @constructor Create empty Tpdb service contract
 */
@Serializable
data class TpdbServiceContract(
    val id: Int,
    val serviceDomainId: Int,
    val name: String,
    val namespace: String,
    val major: Int,
    val synonym: String? = null
) {
    init {
        if (id != 400) { // Remove erronous urn:riv:crm:financial:billing:claim:ProcessClaimSpecificationResponder:1. Does not exist anymore. Fix issue #15.
            tpdbContractMap[Pair(name, major)] = this
        }
    }
}

/**
 * Tpdb service domain dto. Wrapper object.
 *
 * @property answer List of domains
 * @property lastChangeTime Last time the cache was updated.
 * @constructor Create empty Tpdb service domain dto
 */
@Serializable
data class TpdbServiceDomainDto(
    val answer: List<TpdbServiceDomain>,
    val lastChangeTime: String
) {
    init {
        lastUpdateTime = lastChangeTime
    }
    /**
     * Companion. Singleton string with timestamp information.
     *
     * @constructor Create empty Companion
     */
    companion object {
        lateinit var lastUpdateTime: String
    }
}

/**
 * Tpdb service domain
 *
 * @property id API id
 * @property domainName
 * @property synonym
 * @constructor Create empty Tpdb service domain
 */
@Serializable
data class TpdbServiceDomain(
    val id: Int,
    val domainName: String,
    val synonym: String? = null
) {
    init {
        tpdbDomainMap[domainName] = this
    }
}

/**
 * Tpdb domain map. Global map with domains. Key is domain name.
 */
val tpdbDomainMap by lazy { mutableMapOf<String, TpdbServiceDomain>() }

/**
 * Tpdb contract map. Global map with contracts. Key is [Pair] of contract name and major version.
 */
val tpdbContractMap: MutableMap<Pair<String, Int>, TpdbServiceContract> = mutableMapOf()

/**
 * Tpdb load. Load of domain- and contract information. It is stored in the global maps [tpdbDomainMap] and [tpdbContractMap]. When the asynchronous loading is complete the state is refreshed to force a GUI update.
 *
 */
fun tpdbLoad() {
    // fun load(callback: () -> Unit) {
    println("In tpdbload()")

    val url = "${getBaseUrl()}/https://integrationer.tjansteplattform.se/tpdb/tpdbapi.php/api/v1/domains"

    // Older version which I try again to get it to create the actual parsed objects
    getAsync(url) { response ->
        println("Size of domains are: ${response.length}")
        val json = Json { allowStructuredMapKeys = true }
        // val serviceDomains: List<TpdbServiceDomain> = json.decodeFromString(ListSerializer(TpdbServiceDomain.serializer()), response)
        val tpdbServiceDomainDto: TpdbServiceDomainDto =
            json.decodeFromString(TpdbServiceDomainDto.serializer(), response)
        console.log(tpdbServiceDomainDto)

        RivManager.refresh()
    }

    val contractsUrl = "${getBaseUrl()}/https://integrationer.tjansteplattform.se/tpdb/tpdbapi.php/api/v1/contracts"

    getAsync(contractsUrl) { response ->
        println("Size of contracts are: ${response.length}")
        val json = Json { allowStructuredMapKeys = true }
        // val serviceContracts: List<TpdbServiceContract> = json.decodeFromString(ListSerializer(TpdbServiceContract.serializer()), response)
        val tpdbServiceContractsDto: TpdbServiceContractDto =
            json.decodeFromString(TpdbServiceContractDto.serializer(), response)
        console.log(tpdbServiceContractsDto)

        RivManager.refresh()
    }
}

/**
 * Make hippo domain url. The URL can be used by the user to enter hippo with the domain selected.
 *
 * @param domainName Name of URL
 * @return URL to hippo where the domain is selected.
 */
fun mkHippoDomainUrl(domainName: String): String {
    val domain: TpdbServiceDomain? = tpdbDomainMap[domainName]
    if (domain == null) return ""

    if (!takInstalledDomain(domainName)) return ""

    return "https://integrationer.tjansteplattform.se/hippo/#/hippo/filter=d${domain.id}"
}

/**
 * Make hippo contract url. The URL can be used by the user to enter hippo with the contract selected.
 *
 * @param contractName Name of contract to select in hippo.
 * @param major Major version of contract to select in hippo.
 * @return URL to hippo where the contract is selected.
 */
fun mkHippoContractUrl(contractName: String, major: Int): String {
    val contract: TpdbServiceContract? = tpdbContractMap[Pair(contractName, major)]
    if (contract == null) {
        println("Contract $contractName v$major is not used according to TPDB")
        return ""
    }

    /**
     * Zap to handle namespace error in TPDB.
      */
    val checkedNamespace =
        if (contract.namespace.equals("urn:riv:cliniralprocess:logistics:logistics:GetCareContactsResponder:3")) "urn:riv:clinicalprocess:logistics:logistics:GetCareContactsResponder:3"
        else contract.namespace

    // A contract may have been installed earlier, but removed today. Check in TakApi if currently installed.
    if (!takInstalledContractNamespace.contains(checkedNamespace)) {
        println("Contract $contractName v$major is not installed today according to TakApi. Namespace=${contract.namespace}")
        return ""
    }

    return "https://integrationer.tjansteplattform.se/hippo/#/hippo/filter=C${contract.id}"
}
