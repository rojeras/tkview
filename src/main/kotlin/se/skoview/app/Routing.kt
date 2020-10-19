package se.skoview.app

import pl.treksoft.navigo.Navigo
import se.skoview.model.DisplayPage
import se.skoview.model.DomainMap
import se.skoview.model.RivAction

enum class View(val url: String) {
    HOME("/"),
    DOMAIN_LIST("/domains"),
    CONTRACT_LIST("/contracts"),
    DOMAIN("domain")
}

fun Navigo.initialize(): Navigo {
    return on(
        View.HOME.url,
        { _ ->
            viewRouter(View.HOME)
        }
    ).on(
        View.DOMAIN_LIST.url,
        { _ ->
            viewRouter(View.DOMAIN_LIST)
        }
    ).on(
        View.CONTRACT_LIST.url,
        { _ ->
            viewRouter(View.CONTRACT_LIST)
        }
    ).on(
        "${View.DOMAIN.url}/:slug",
        { params ->
            viewRouter(View.DOMAIN, stringParameter(params, "slug"))
        }
    )
}

fun stringParameter(params: dynamic, parameterName: String): String {
    return (params[parameterName]).toString()
}

private fun viewRouter(view: View, param: String? = null) {
    println("viewRouter: view = '$view', param='$param'")

    val state = store.getState()

    if (!param.isNullOrEmpty()) {
        store.dispatch(RivAction.SelectDomain(param))
    }

    val showPage: DisplayPage = when (view) {
        View.HOME -> DisplayPage.DOMAIN_LIST
        View.DOMAIN_LIST -> DisplayPage.DOMAIN_LIST
        View.CONTRACT_LIST -> DisplayPage.CONTRACT_LIST
        View.DOMAIN -> DisplayPage.DOMAIN
    }

    store.dispatch(RivAction.SetCurrentPage(showPage))
}
