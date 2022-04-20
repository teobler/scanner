package org.archguard.scanner.core.client

import chapi.domain.core.CodeDataStruct
import org.archguard.scanner.core.client.dto.ContainerService

// client of the archguard, scanner can communicate to server via this client with limited functions
interface ArchGuardClient {
    fun saveDataStructure(dataStructure: List<CodeDataStruct>, systemId: String, language: String)
    fun saveApi(api: List<ContainerService>, systemId: String, language: String)
}