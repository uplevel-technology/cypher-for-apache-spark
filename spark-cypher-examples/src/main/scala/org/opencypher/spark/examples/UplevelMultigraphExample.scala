package org.opencypher.spark.examples

import org.opencypher.okapi.api.graph.Namespace
import org.opencypher.spark.api.io.sql.SqlDataSourceConfig
import org.opencypher.spark.api.{CAPSSession, GraphSources}
import org.opencypher.spark.util.App
import org.opencypher.spark.api.io.{Node, Relationship, RelationshipType}
import org.opencypher.spark.examples.NorthwindJdbcExample.resource


/**
  * Demonstrates multiple graph capabilities by loading a social network from case class objects and a purchase network
  * from CSV data and schema files. The example connects both networks via matching user and customer names. A Cypher
  * query is then used to compute products that friends have bought.
  */
object UplevelMultiGraphExample extends App {
  // 1) Create CAPS session
  implicit val session: CAPSSession = CAPSSession.local()

  val sqlSrcConfig = SqlDataSourceConfig.Jdbc(
    url = "jdbc:h2:mem:DNS.db;DB_CLOSE_DELAY=30;",
    driver = "org.h2.Driver"
  )

  val sqlGraphSource = GraphSources
    .sql(resource("uplevel/ddl/uplevel.ddl").getFile)
    .withSqlDataSourceConfigs("H2" -> sqlSrcConfig)




}
// end::full-example[]
