package org.opencypher.spark.examples

import org.opencypher.okapi.api.graph.Namespace
import org.opencypher.spark.api.io.sql.SqlDataSourceConfig
import org.opencypher.spark.api.{CAPSSession, GraphSources}
import org.opencypher.spark.util.{App, DnsHistoryDb}
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

  DnsHistoryDb.init(sqlSrcConfig)


  session.registerSource(Namespace("sql"), sqlGraphSource)

// get an alert about a malware callback to a domain
// assume we're processing it in real time, thus that current
// query a ginormous sql database of historical DNS lookup results, find all other domains that have ever resolved to that IP
//

  session.cypher(
    """
      |FROM GRAPH sql.DnsHistory
      |MATCH resolution = (d:domain)-[:RESOLVED_TO]->(ip) AS resolutions
      | //YAY now do stuff
      |
      |RETURN resolutions
    """.stripMargin).records.show




  // print the schema of the graph
  println(session.catalog.graph("sql.DnsHistory").schema.pretty)



}
// end::full-example[]
