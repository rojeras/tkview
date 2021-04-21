/**
 * Copyright (C) 2013-2020 Lars Erik Röjerås
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

import io.kvision.redux.RAction
import se.skoview.app.View

sealed class RivAction : RAction {
    object HomePage : RivAction()
    data class SetView(val view: View) : RivAction()
    data class SelectDomainType(val type: DomainTypeEnum) : RivAction()
    data class SelectAndShowDomain(val domainName: String) : RivAction()
    data class SelectAndShowContract(val contractName: String) : RivAction()
    data class SelectDomainVersion(val domainVersion: Version) : RivAction()
    data class ShowHiddenDomain(val isVisible: Boolean) : RivAction()
    data class ShowHiddenVersion(val isVisible: Boolean) : RivAction()
    data class ShowRcVersion(val isVisible: Boolean) : RivAction()
    data class ShowUnderscoreVersion(val isVisible: Boolean) : RivAction()
    data class ShowTrunkVersion(val isVisible: Boolean) : RivAction()
    data class DomdbLoadingComplete(val isComplete: Boolean) : RivAction()
    data class SetAdminMode(val onOff: String) : RivAction()
    object Refresh : RivAction()
}
