package com.thoughtworks.archguard.scanner.javasource

import chapi.domain.core.*
import infrastructure.SourceBatch
import java.text.SimpleDateFormat
import java.util.*

class ClassRepository(systemId: String) {
    private val batch: SourceBatch = SourceBatch()
    private val systemId: String

    init {
        this.systemId = systemId
    }

    fun saveClassElement(clz: CodeDataStruct) {
        val clzId = saveClass(clz)
        saveClassFields(clzId, clz.Fields, clz.NodeName)
        saveClassMethods(clzId, clz.Functions, clz.NodeName, clz.Package)
    }

    private fun saveClassMethods(clzId: String, functions: Array<CodeFunction>, clzName: String, pkgName: String) {
        for (method in functions) {
            val methodId = doSaveMethod(clzName, method, pkgName)

            method.Annotations.forEach {
                doSaveAnnotation(it, methodId)
            }
        }
    }

    private fun doSaveAnnotation(annotation: CodeAnnotation, methodId: String) {
        val id = generateId()
        val values: MutableMap<String, String> = HashMap()
        values["id"] = id
        values["system_id"] = systemId
        values["targetType"] = "todo"
        values["targetId"] = methodId
        values["name"] = annotation.Name
        batch.add("JAnnotation", values)


        for (keyValue in annotation.KeyValues) {
            doSaveAnnotationValue(id, keyValue)
        }
    }

    private fun doSaveAnnotationValue(annotationId: String, map: AnnotationKeyValue) {
        val id = generateId()
        val values: MutableMap<String, String> = HashMap()
        values["id"] = id
        values["system_id"] = systemId
        values["annotationId"] = annotationId
        values["key"] = map.Key
        values["value"] = map.Value
        batch.add("JAnnotationValue", values)
    }

    private fun doSaveMethod(clzName: String, m: CodeFunction, pkgName: String): String {
        val mId = generateId()
        val time: String = currentTime
        val values: MutableMap<String, String> = HashMap()
        values["id"] = mId
        values["system_id"] = systemId
        values["clzname"] = clzName
        values["name"] = m.Name
        values["returntype"] = m.ReturnType
        values["argumenttypes"] = m.Parameters.map { it.TypeType }.joinToString(",")

        if(m.Modifiers.isNotEmpty()){
            values["access"] = m.Modifiers[0]
        } else {
            values["access"] = "public"
        }

        values["module"] = pkgName
        values["package_name"] = pkgName
        values["class_name"] = clzName
        values["updatedAt"] = time
        values["createdAt"] = time
        values["is_test"] = if (m.isJUnitTest()) "true" else "false"
        batch.add("JMethod", values)
        return mId
    }

    private fun saveClassFields(clzId: String, fields: Array<CodeField>, clzName: String) {
        for (field in fields) {
            val id = generateId()
            val time: String = currentTime
            val values: MutableMap<String, String> = HashMap()

            values["id"] = id
            values["system_id"] = systemId
            values["name"] = field.TypeValue
            values["clzname"] = clzName
            values["type"] = field.TypeType
            values["updatedAt"] = time
            values["createdAt"] = time
            batch.add("JField", values)

            val relation: MutableMap<String, String> = HashMap()
            relation["id"] = generateId()
            relation["system_id"] = systemId
            relation["a"] = clzId
            relation["b"] = id
            batch.add("_ClassFields", relation)
        }
    }

    private fun saveClass(clz: CodeDataStruct): String {
        val time = currentTime
        val clzId = generateId()
        val values: MutableMap<String, String> = HashMap()
        values["id"] = clzId
        values["system_id"] = systemId
        values["name"] = clz.NodeName
        values["is_thirdparty"] = "false"
        values["is_test"] = "false"
        values["updatedAt"] = time
        values["createdAt"] = time
        values["module"] = clz.Package
        values["package_name"] = clz.Package
        values["class_name"] = clz.NodeName
        values["access"] = "todo"
        batch.add("JClass", values)

        return clzId
    }

    companion object {
        fun generateId(): String {
            return UUID.randomUUID().toString()
        }

        val currentTime: String
            get() {
                val dt = Date()
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                return sdf.format(dt)
            }
    }

    fun close() {
        batch.execute()
        batch.close()
    }
}