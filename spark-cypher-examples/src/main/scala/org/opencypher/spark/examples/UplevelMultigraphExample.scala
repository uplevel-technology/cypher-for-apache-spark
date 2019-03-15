package org.opencypher.spark.examples

import org.opencypher.okapi.api.graph.Namespace
import org.opencypher.spark.api.io.sql.SqlDataSourceConfig
import org.opencypher.spark.api.{CAPSSession, GraphSources}
import org.opencypher.spark.util.{App, DomainResolutionDB}
import org.opencypher.spark.api.io.{Node, Relationship, RelationshipType}
import org.opencypher.spark.examples.NorthwindJdbcExample.{resource, session, sqlGraphSource}



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

  DomainResolutionDB.init(sqlSrcConfig)


  session.registerSource(Namespace("sql"), sqlGraphSource)

  session.cypher(
    """
      |FROM GRAPH sql.Resolutions
      |MATCH (n)
      |RETURN n
    """.stripMargin).records.show

  // print the schema of the graph
  println(session.catalog.graph("sql.Resolutions").schema.pretty)



}
// end::full-example[]
