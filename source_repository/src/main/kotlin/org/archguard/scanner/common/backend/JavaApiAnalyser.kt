package org.archguard.scanner.common.backend

import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction
import org.archguard.scanner.common.container.ContainerDemand
import org.archguard.scanner.common.container.ContainerResource
import org.archguard.scanner.common.container.ContainerService

class JavaApiAnalyser {
    var demands: List<ContainerDemand> = listOf()
    var resources: List<ContainerResource> = listOf()

    fun analysisByNode(node: CodeDataStruct, _workspace: String) {
        val routeAnnotation = node.filterAnnotations("RestController", "RequestMapping")
        if (routeAnnotation.isNotEmpty()) {
            var baseUrl = ""
            val mappingAnnotation = node.filterAnnotations("RequestMapping")
            if (mappingAnnotation.isNotEmpty()) {
                val url = mappingAnnotation[0].KeyValues[0].Value
                baseUrl = url.removePrefix("\"").removeSuffix("\"")
            }

            node.Functions.forEach { createResource(it, baseUrl, node) }
        }

        val useRestTemplate = node.Imports.filter { it.Source.endsWith(".RestTemplate") }
        if (useRestTemplate.isNotEmpty()) {
            node.Functions.forEach {
                it.FunctionCalls.forEach { call ->
                    if (call.NodeName == "RestTemplate" && call.FunctionName != "<init>") {
                        var method = ""
                        val functionName = call.FunctionName
                        when {
                            functionName.startsWith("Get") -> {
                                method = "Get"
                            }
                            functionName.startsWith("Post") -> {
                                method = "Post"
                            }
                            functionName.startsWith("Delete") -> {
                                method = "Delete"
                            }
                            functionName.startsWith("Put") -> {
                                method = "Put"
                            }
                        }

                        var url = ""
                        if (call.Parameters.isNotEmpty() && call.Parameters[0].TypeValue.isNotEmpty()) {
                            url = call.Parameters[0].TypeValue.removePrefix("\"").removeSuffix("\"")
                        }

                        demands = demands + ContainerDemand(
                            source_caller = node.NodeName,
                            target_url = url,
                            target_http_method = method
                        )
                    }
                }
            }
        }
    }

    private fun createResource(func: CodeFunction, baseUrl: String, node: CodeDataStruct) {
        var httpMethod = ""
        var route = baseUrl
        for (annotation in func.Annotations) {
            var isHttpAnnotation = true
            when (annotation.Name) {
                "GetMapping" -> httpMethod = "Get"
                "PostMapping" -> httpMethod = "Post"
                "DeleteMapping" -> httpMethod = "Delete"
                "PutMapping" -> httpMethod = "Put"
                else -> isHttpAnnotation = false
            }

            val hasSubUrlMapping = annotation.KeyValues.isNotEmpty()
            if(isHttpAnnotation && httpMethod.isNotEmpty() && hasSubUrlMapping) {
                val subUrl = annotation.KeyValues[0].Value
                val pureUrl = subUrl.removePrefix("\"").removeSuffix("\"")

                if (baseUrl.isNotEmpty()) {
                    route = "$baseUrl$pureUrl"
                } else {
                    route = pureUrl
                }
            }
        }

        if (httpMethod.isNotEmpty()) {
            if (!route.startsWith("/")) {
                route = "/${route}"
            }

            route.replace("//", "/")

            resources = resources + ContainerResource(
                sourceUrl = route,
                sourceHttpMethod = httpMethod,
                packageName = node.Package,
                className = node.NodeName,
                methodName = func.Name
            )
        }
    }

    fun toContainerServices(): Array<ContainerService> {
        var componentCalls: Array<ContainerService> = arrayOf()

        val componentRef = ContainerService(name = "")
        componentRef.resources = this.resources
        componentRef.demands = this.demands

        componentCalls += componentRef
        return componentCalls
    }
}