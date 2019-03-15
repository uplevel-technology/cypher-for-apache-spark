-- noinspection SqlDialectInspectionForFile

SET SCHEMA H2.RESOLUTION_DATALAKE;

CREATE GRAPH RESOLUTIONS {

  -- nodes
  DomainName ( value STRING ),

  IpAddress ( value STRING, version NUMBER )

  InfoSource ( id NUMBER, name STRING )

  Resolution (id NUMBER )

  -- relationships
  RESOLVED_TO(
    timestamp STRING -- actually can be date
  )

  -- node mappings
  (DomainName)
    FROM TABLE_DOMAIN_NAME

  (IpAddress)
    FROM TABLE_IP_ADDRESS

  (InfoSource)
    FROM TABLE_INFO_SOURCE



  -- hyper repr
  -- (Resolution)
   --  FROM TABLE_RESOLUTION



  -- rel mappings
  (DomainName)-[:RESOLVED_TO]->(IpAddress)
    FROM TABLE_RESOLUTIONS resolution
      START NODES (DomainName)
        FROM TABLE_DOMAIN_NAME domainName
          JOIN ON domainName.VALUE = resolution.DOMAINNAME
      END NODES (IPAddress)
        FROM TABLE_IP_ADDRESS ip
          JOIN ON ip.VALUE = resolution.RESOLVESTO

}
