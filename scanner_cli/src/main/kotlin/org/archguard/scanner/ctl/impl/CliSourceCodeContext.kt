package org.archguard.scanner.ctl.impl

import org.archguard.scanner.core.client.ArchGuardClient
import org.archguard.scanner.core.sourcecode.SourceCodeContext

// extend SourceCodeContext to accept cil specific parameters(infra related), like memory limit, queue size, etc.
class CliSourceCodeContext(
    val systemId: String,
    override val language: String,
    override val features: List<String>,
    override val client: ArchGuardClient,
    override val path: String,
    override val withoutStorage: Boolean,
) : SourceCodeContext
