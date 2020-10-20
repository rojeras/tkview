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

package se.skoview.rivta

val tpdbDomainIdMap: Map<String, Int> = mapOf(
    "crm:carelisting" to 1,
    "insuranceprocess:healthreporting" to 2,
    "clinicalprocess:healthcond:certificate" to 3,
    "itinfra:tp" to 4,
    "itintegration:registry" to 5,
    "itintegration:engagementindex" to 6,
    "clinicalprocess:healthcond:description" to 7,
    "clinicalprocess:logistics:logistics" to 8,
    "clinicalprocess:healthcond:actoutcome" to 9,
    "clinicalprocess:activityprescription:actoutcome" to 10,
    "crm:scheduling" to 11,
    "infrastructure:itintegration:registry" to 12,
    "crm:requeststatus" to 13,
    "Illegal namespace" to 14,
    "clinicalprocess:healthcond:basic" to 15,
    "clinicalprocess:activity:actions" to 16,
    "clinicalprocess:activity:request" to 17,
    "druglogistics:dosedispensing" to 18,
    "se.apotekensservice:or" to 19,
    "se.apotekensservice:axs" to 20,
    "inera:se.apotekensservice:expo" to 21,
    "se.apotekensservice:lf" to 22,
    "se.apotekensservice:pris" to 23,
    "followup:qualityregistry:nkrr" to 24,
    "informatics:terminology" to 25,
    "itintegration:monitoring" to 26,
    "followup:groupoutcomes:qualityreporting" to 27,
    "ehr:patientconsent:accesscontrol" to 28,
    "ehr:accesscontrol" to 29,
    "ehr:patientconsent:administration" to 30,
    "ehr:patientconsent:querying" to 31,
    "ehr:patientrelationship:accesscontrol" to 32,
    "ehr:patientrelationship:administration" to 33,
    "ehr:patientrelationship:querying" to 34,
    "ehr:blocking:querying" to 35,
    "ehr:blocking:accesscontrol" to 26,
    "processdevelopment:infections" to 37,
    "ehr:blocking:synchronization" to 28,
    "supportprocess:serviceprovisioning:healthcareoffering" to 39,
    "population:residentmaster" to 40,
    "infrastructure:directory:authorizationmanagement" to 41,
    "infrastructure:directory:employee" to 42,
    "infrastructure:directory:organization" to 43,
    "ehr:log:store" to 44,
    "ehr:log:querying" to 45,
    "clinicalprocess:activity:order" to 46,
    "infrastructure:eservicesupply:forminteraction" to 47,
    "eservicesupply:eoffering" to 48,
    "infrastructure:supportservices:forminteraction" to 49,
    "infrastructure:eservicesupply:patientportal" to 50,
    "clinicalprocess:healthcond:rheuma" to 51,
    "processmanagement:decisionsupport:insurancemedicinedecisionsupport" to 52,
    "infrastructure:directory:licensetopractice" to 53,
    "ehr:patientsummary:GetEhrExtractResponder" to 54,
    "service:catalogue" to 55,
    "sll:invoicedata" to 56,
    "sll:paymentresponsible" to 57,
    "sll:clinicalprocess:healthcond:actoutcome" to 58,
    "ehr:blocking:administration" to 59,
    "cliniralprocess:logistics:logistics" to 60,
    "inera:se.apotekensservice:axs" to 61,
    "inera:se.apotekensservice:lf" to 62,
    "inera:se.apotekensservice:or" to 63,
    "inera:se.apotekensservice:pris" to 64,
    "masterdata:citizen:citizen" to 65,
    "financial:billing:claim" to 66,
    "sob:apps:resident" to 67,
    "strategicresourcemanagement:persons:person" to 68,
    "lv:reporting:pharmacovigilance" to 69,
    "se.apotekensservice:expo" to 70,
    "informationsecurity:authorization:blocking" to 71,
    "informationsecurity:auditing:log" to 72,
    "informationsecurity:authorization:consent" to 73,
    "infrastructure:directory" to 74,
    "ehr:ehrexchange" to 75,
    "crm:financial:billing:claim" to 76,
    "urn:riv-skl:followup:groupoutcomes:qualityreporting" to 77,
    "clinicalprocess:logistics:cervixscreening" to 78,
    "financial:patientfees:exemption" to 79
)

fun mkHippoUrl(domain: String): String {
    val domainId = tpdbDomainIdMap[domain] ?: 0

    if (domainId == 0) return ""

    return "https://integrationer.tjansteplattform.se/hippo/?filter=d$domainId"
}
