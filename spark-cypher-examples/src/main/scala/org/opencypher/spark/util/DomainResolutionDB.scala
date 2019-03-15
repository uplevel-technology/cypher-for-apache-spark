
package org.opencypher.spark.util

import org.opencypher.spark.api.io.sql.SqlDataSourceConfig
import org.opencypher.spark.testing.utils.H2Utils._

import scala.io.Source
import scala.util.Properties

object DomainResolutionDB {

  def init(sqlDataSourceConfig: SqlDataSourceConfig.Jdbc): Unit = {

    withConnection(sqlDataSourceConfig) { connection =>
      connection.execute("DROP SCHEMA IF EXISTS RESOLUTION_DATALAKE")
      connection.execute("CREATE SCHEMA RESOLUTION_DATALAKE")
      connection.setSchema("RESOLUTION_DATALAKE")

      // create the SQL db schema
      connection.execute(readResourceAsString("/uplevel/sql/resolutions_schema.sql"))

      // populate tables with data
      connection.execute(readResourceAsString("/uplevel/sql/resolutions_data.sql"))

    }
  }

  private def readResourceAsString(name: String): String =
    Source.fromFile(getClass.getResource(name).toURI)
      .getLines()
      .filterNot(line => line.startsWith("#") || line.startsWith("CREATE INDEX"))
      .mkString(Properties.lineSeparator)
}
