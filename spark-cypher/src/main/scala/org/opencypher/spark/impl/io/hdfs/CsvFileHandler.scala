/*
 * Copyright (c) 2016-2018 "Neo4j Sweden, AB" [https://neo4j.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Attribution Notice under the terms of the Apache License 2.0
 *
 * This work was created by the collective efforts of the openCypher community.
 * Without limiting the terms of Section 6, any Derivative Work that is not
 * approved by the public consensus process of the openCypher Implementers Group
 * should not be described as “Cypher” (and Cypher® is a registered trademark of
 * Neo4j Inc.) or as "openCypher". Extensions by implementers or prototypes or
 * proposals for change that have been documented or implemented should only be
 * described as "implementation extensions to Cypher" or as "proposed changes to
 * Cypher that are not yet approved by the openCypher community".
 */
package org.opencypher.spark.impl.io.hdfs

import java.io._
import java.net.URI
import java.nio.file.{Files, Paths}
import java.util.stream.Collectors

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.opencypher.okapi.impl.exception.{GraphNotFoundException, IllegalArgumentException, InvalidGraphException}
import org.opencypher.spark.impl.io.hdfs.CsvGraphLoader._

trait CsvFileHandler {
  def graphLocation: URI

  def listNodeFiles: Array[URI] = listDataFiles(NODES_DIRECTORY)

  def listRelationshipFiles: Array[URI] = listDataFiles(RELS_DIRECTORY)

  def listDataFiles(directory: String): Array[URI]

  def readSchemaFile(path: URI): String

  def writeSchemaFile(directory: String, filename: String, jsonSchema: String): Unit
}

final class HadoopFileHandler(override val graphLocation: URI, private val hadoopConfig: Configuration)
  extends CsvFileHandler {

  private val fs: FileSystem = FileSystem.get(graphLocation, hadoopConfig)

  override def listDataFiles(directory: String): Array[URI] = {
    val graphDirectory = graphLocation.getPath
    if (!fs.exists(new Path(graphDirectory))) {
      throw GraphNotFoundException(s"CSV graph with name '$graphDirectory'")
    }
    val entitiesDirectory = new Path(graphDirectory, directory)
    if (!fs.exists(entitiesDirectory)) {
      throw InvalidGraphException(s"CSV graph is missing required directory '$directory'")
    }
    fs.listStatus(entitiesDirectory)
      .filter(p => p.getPath.toString.toUpperCase.endsWith(CSV_SUFFIX))
      .map(_.getPath.toUri)
  }

  override def readSchemaFile(path: URI): String = {
    val hdfsPath = new Path(path)
    val schemaPaths = Seq(hdfsPath.suffix(SCHEMA_SUFFIX.toLowerCase), hdfsPath.suffix(SCHEMA_SUFFIX))
    val optSchemaPath = schemaPaths.find(fs.exists)
    val schemaPath = optSchemaPath.getOrElse(throw IllegalArgumentException(s"to find a schema file at $path"))
    val stream = new BufferedReader(new InputStreamReader(fs.open(schemaPath)))

    def readLines = Stream.cons(stream.readLine(), Stream.continually(stream.readLine))

    readLines.takeWhile(_ != null).mkString
  }

  override def writeSchemaFile(directory: String, filename: String, jsonSchema: String): Unit = {
    val hdfsPath = new Path(new Path(graphLocation.getPath, directory), filename)
    val outputStream = fs.create(hdfsPath)
    val bw = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"))
    bw.write(jsonSchema)
    bw.close()
  }
}

final class LocalFileHandler(override val graphLocation: URI) extends CsvFileHandler {

  import scala.collection.JavaConverters._

  override def listDataFiles(directory: String): Array[URI] = {
    val graphDirectory = graphLocation.getPath
    if (Files.notExists(Paths.get(graphDirectory))) {
      throw GraphNotFoundException(s"CSV graph with name '$graphDirectory'")
    }
    val entitiesDirectory = Paths.get(graphDirectory, directory)
    if (Files.notExists(entitiesDirectory)) {
      throw InvalidGraphException(s"CSV graph is missing required directory '$directory'")
    }
    Files
      .list(entitiesDirectory)
      .collect(Collectors.toList())
      .asScala
      .filter(p => p.toString.toUpperCase.endsWith(CSV_SUFFIX))
      .toArray
      .map(_.toUri)
  }

  override def readSchemaFile(csvUri: URI): String = {
    val path = Paths.get(csvUri)

    val schemaPathCandidates = Seq(
      Paths.get(s"$path$SCHEMA_SUFFIX"),
      Paths.get(s"$path${SCHEMA_SUFFIX.toLowerCase}")
    )

    val optSchemaPath = schemaPathCandidates.find(Files.exists(_))
    val schemaPath = optSchemaPath.getOrElse(throw IllegalArgumentException(s"Could not find schema file at $csvUri"))
    new String(Files.readAllBytes(schemaPath))
  }

  override def writeSchemaFile(directory: String, filename: String, jsonSchema: String): Unit = {
    val file = new File(Paths.get(graphLocation.getPath, directory, filename).toString)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(jsonSchema)
    bw.close()
  }
}
